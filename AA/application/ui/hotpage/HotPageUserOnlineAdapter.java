package com.application.ui.hotpage;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.application.connection.request.CircleImageRequest;
import com.application.entity.MeetPeople;
import com.application.util.RegionUtils;
import com.application.util.Utility;
import com.application.util.preferece.UserPreferences;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import glas.bbsystem.R;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by ThoNh on 12/7/2017.
 */

public class HotPageUserOnlineAdapter extends BaseAdapter {

  private Context mContext;
  private RegionUtils mRegionUtils;
  private List<MeetPeople> mListPeople;
  private IUserOnlineEventListener mListener;
  public HotPageUserOnlineAdapter(Context context, List<MeetPeople> listPeople) {
    this.mContext = context;
    this.mListPeople = listPeople;
    mRegionUtils = new RegionUtils(context);
  }

  public void setListener(IUserOnlineEventListener listener) {
    this.mListener = listener;
  }

  @Override
  public int getCount() {
    return mListPeople.size();
  }

  @Override
  public Object getItem(int position) {
    return mListPeople.get(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(final int position, View convertView, ViewGroup parent) {

    ViewHolder viewHolder = null;

    if (convertView == null || !(convertView.getTag() instanceof ViewHolder)) {
      convertView = LayoutInflater.from(mContext).inflate(R.layout.item_vertical_hot_page, null);

      viewHolder = new ViewHolder();
      viewHolder.mImageAvatar = (ImageView) convertView
          .findViewById(R.id.item_list_hot_page_avatar);
      viewHolder.mImageFavorite = (ImageView) convertView
          .findViewById(R.id.item_list_hot_page_favorite);
      viewHolder.mImageChat = (ImageView) convertView.findViewById(R.id.item_list_hot_page_chat);

      viewHolder.mTextName = (TextView) convertView.findViewById(R.id.item_list_hot_page_name);
      viewHolder.mTextStatus = (TextView) convertView.findViewById(R.id.item_list_hot_page_status);
      viewHolder.mTextTime = (TextView) convertView.findViewById(R.id.item_list_hot_page_time);
      viewHolder.mTextLocation = (TextView) convertView
          .findViewById(R.id.item_list_hot_page_location);

      convertView.setTag(convertView);

    } else {
      viewHolder = (ViewHolder) convertView.getTag();
    }

    final MeetPeople meetPeople = mListPeople.get(position);
    try {
      Calendar calendarNow = Calendar.getInstance();
      Utility.YYYYMMDDHHMMSS.setTimeZone(TimeZone.getTimeZone("GMT"));
      Date dateSend = Utility.YYYYMMDDHHMMSS.parse(meetPeople
          .getLastLogin());
      Calendar calendarSend = Calendar.getInstance(TimeZone
          .getDefault());
      calendarSend.setTime(dateSend);

      viewHolder.mTextTime.setText(Utility.getDifference(mContext, calendarSend, calendarNow));
    } catch (ParseException e) {
      e.printStackTrace();
      viewHolder.mTextTime.setText(R.string.common_now);
    }

    // load avatar that has cover by gender
    String token = UserPreferences.getInstance().getToken();
    CircleImageRequest imageRequest = new CircleImageRequest(token, meetPeople.getAva_id());
    Glide.with(mContext)
        .load(imageRequest.toURL())
        .apply(RequestOptions.placeholderOf(R.drawable.dummy_circle_avatar))
        .into(viewHolder.mImageAvatar);

    // set status
    String about = meetPeople.getAbout();
    if (!TextUtils.isEmpty(about)) {
      viewHolder.mTextStatus.setText(about);
      viewHolder.mTextStatus.setVisibility(View.VISIBLE);
    } else {
      viewHolder.mTextStatus.setVisibility(View.GONE);
    }

    // set location
    viewHolder.mTextLocation.setText(mRegionUtils.getRegionName(meetPeople.getRegion()));

    // set name and age
    String name = meetPeople.getUser_name();
    viewHolder.mTextName.setText(name);

    //todo favorite

    viewHolder.mImageFavorite.setImageResource(
        meetPeople.isFav == 0 ? R.drawable.ic_action_favorite : R.drawable.ic_action_favorited);

    viewHolder.mImageFavorite.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (mListener != null) {
          mListener.onFavorite(meetPeople, position);
        }
      }
    });

    viewHolder.mImageChat.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (mListener != null) {
          mListener.onChat(meetPeople, position);
        }
      }
    });

    convertView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (mListener != null) {
          mListener.onItemClick(meetPeople, position);
        }
      }
    });

    return convertView;
  }

  public void updateFavorite(MeetPeople meetPeople) {
    int index = mListPeople.indexOf(meetPeople);
    if (index != -1) {
      mListPeople.set(index, meetPeople);
      notifyDataSetChanged();
    }
  }

  public void clearData() {
    if (mListPeople != null) {
      mListPeople.clear();
      notifyDataSetChanged();
    }
  }

  public void appendData(MeetPeople meetPeople) {
    mListPeople.add(meetPeople);
    notifyDataSetChanged();
  }

  public void appendData(List<MeetPeople> meetPeopleList) {
    mListPeople = meetPeopleList;
    notifyDataSetChanged();
  }

  public List<MeetPeople> getData() {
    return mListPeople;
  }

  public interface IUserOnlineEventListener {

    void onChat(MeetPeople meetPeople, int position);

    void onFavorite(MeetPeople meetPeople, int position);

    void onItemClick(MeetPeople meetPeople, int position);
  }

  private static class ViewHolder {

    public ImageView mImageAvatar;
    public ImageView mImageFavorite;
    public ImageView mImageChat;
    public TextView mTextName;
    public TextView mTextStatus;
    public TextView mTextTime;
    public TextView mTextLocation;
  }
}
