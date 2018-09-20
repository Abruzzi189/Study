package com.application.ui.chat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.application.connection.request.CircleImageRequest;
import com.application.entity.CallUserInfo;
import com.application.imageloader.ImageFetcher;
import com.application.util.Utility;
import com.application.util.preferece.UserPreferences;
import glas.bbsystem.R;
import java.util.ArrayList;
import java.util.List;


public class CallLogListAdapter extends BaseAdapter {

  private Context mContext;
  private ImageFetcher mImageFetcher;
  private List<CallLog> mListCallLog;
  private ICheckCall checkCallListener;

  public CallLogListAdapter(Context context, ImageFetcher imageFetcher) {
    this.mContext = context;
    this.mImageFetcher = imageFetcher;
    mListCallLog = new ArrayList<CallLog>();
  }

  public void setCheckCall(ICheckCall checkCall) {
    this.checkCallListener = checkCall;
  }

  @Override
  public int getCount() {
    return mListCallLog.size();
  }

  @Override
  public CallLog getItem(int position) {
    return mListCallLog.get(position);
  }

  @Override
  public long getItemId(int position) {
    return 0;
  }

  public void addAll(List<CallLog> listCallLog) {
    mListCallLog.addAll(listCallLog);
    notifyDataSetChanged();
  }

  public void clear() {
    mListCallLog.clear();
  }

  @SuppressLint("SimpleDateFormat")
  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    ViewHolder holder = null;
    if (convertView == null) {
      holder = new ViewHolder();
      convertView = View.inflate(mContext, R.layout.item_list_call_log,
          null);
      holder.imgAvatar = (ImageView) convertView
          .findViewById(R.id.avatar);
      // holder.imgStatus = (ImageView) convertView
      // .findViewById(R.id.status);
      holder.txtName = (TextView) convertView
          .findViewById(R.id.user_name);
      holder.txtDuration = (TextView) convertView
          .findViewById(R.id.duration);
      holder.txtTime = (TextView) convertView.findViewById(R.id.time);
      holder.imgVoice = (ImageView) convertView
          .findViewById(R.id.voice_call);
      holder.imgVideo = (ImageView) convertView
          .findViewById(R.id.video_call);
      convertView.setTag(holder);
    } else {
      holder = (ViewHolder) convertView.getTag();
    }
    final CallLog log = getItem(position);

    // Setup avatar
    String avatarId = log.getAvatarId();
    String token = UserPreferences.getInstance().getToken();
    CircleImageRequest imageRequest = new CircleImageRequest(token,
        avatarId);
    mImageFetcher.loadImage(imageRequest, holder.imgAvatar,
        holder.imgAvatar.getWidth(), holder.imgAvatar.getHeight());
    // Setup online status
    // boolean isOnline = log.isOnline();
    // if (isOnline) {
    // holder.imgStatus.setImageResource(R.drawable.bg_status_online);
    // } else {
    // holder.imgStatus.setImageResource(R.drawable.bg_status_offline);
    // }
    // Setup name
    String userName = log.getUserName();
    holder.txtName.setText(userName);
    // Set image call, setup duration
    int callType = log.getCallType();
    if (callType == CallLog.CALL_TYPE_VOICE) {
      holder.txtDuration.setCompoundDrawablesWithIntrinsicBounds(
          R.drawable.ic_call_type_voice, 0, 0, 0);
    } else if (callType == CallLog.CALL_TYPE_VIDEO) {
      holder.txtDuration.setCompoundDrawablesWithIntrinsicBounds(
          R.drawable.ic_call_type_video, 0, 0, 0);
    }
    int responseStatus = log.getResponse();
    if (responseStatus == CallLog.STATUS_ANSWER) {

    } else if (responseStatus == CallLog.STATUS_BUSY) {
      holder.txtDuration.setCompoundDrawablesWithIntrinsicBounds(
          R.drawable.ic_call_type_miss1, 0, 0, 0);
    } else if (responseStatus == CallLog.STATUS_NO_ANSWER) {
      holder.txtDuration.setCompoundDrawablesWithIntrinsicBounds(
          R.drawable.ic_call_type_miss1, 0, 0, 0);
    }
    int duration = log.getDuration();
    Resources resources = mContext.getResources();
    if (duration > 0) {
      holder.txtDuration.setText(resources
          .getString(R.string.call_log_conversation_time)
          + " "
          + Utility.parse(duration));
    } else {
      holder.txtDuration.setText(R.string.call_log_conversation_end);
    }

    // Setup last online
    String startTime = log.getStartTime();
    String timeLocale = Utility.convertGMTtoLocale(startTime,
        "yyyyMMddHHmmss", "yyyy/MM/dd HH:mm:ss");
    holder.txtTime.setText(timeLocale);

    // Set voice
    boolean isVoiceWaiting = log.isVoiceWaiiting();
    if (isVoiceWaiting) {
      holder.imgVoice.setImageResource(R.drawable.ic_action_communication_call_active);
      holder.imgVoice.setBackgroundResource(R.drawable.bg_circle_active);
      holder.imgVoice.setColorFilter(ContextCompat.getColor(mContext, R.color.primary));
    } else {
      holder.imgVoice.setImageResource(R.drawable.ic_action_communication_call);
      holder.imgVoice.setBackgroundResource(R.drawable.bg_circle_deactive);
      holder.imgVoice.clearColorFilter();
    }
    holder.imgVoice.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
//				if (log.isVoiceWaiiting()) {
        CallUserInfo userInfo = new CallUserInfo(log.getUserName(),
            log.getUserId(), log.getAvatarId(), log.getGender());
        if (checkCallListener != null) {
          checkCallListener.setUserInfo(userInfo);
        }
        Utility.showDialogAskingVoiceCall(mContext, userInfo,
            new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog,
                  int which) {
                dialog.dismiss();
                if (checkCallListener != null) {
                  checkCallListener.checkCall(false);
                }
              }
            });
//				}
      }
    });
    // Set video
    boolean isVideoWaiting = log.isVideoWaiting();
    if (isVideoWaiting) {
      holder.imgVideo.setImageResource(R.drawable.ic_action_av_videocam_active);
      holder.imgVideo.setBackgroundResource(R.drawable.bg_circle_active);
      holder.imgVideo.setColorFilter(ContextCompat.getColor(mContext, R.color.primary));
    } else {
      holder.imgVideo.setImageResource(R.drawable.ic_action_av_videocam);
      holder.imgVideo.setBackgroundResource(R.drawable.bg_circle_deactive);
      holder.imgVideo.clearColorFilter();
    }
    holder.imgVideo.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
//				if (log.isVideoWaiting()) {
        CallUserInfo userInfo = new CallUserInfo(log.getUserName(),
            log.getUserId(), log.getAvatarId(), log.getGender());
        if (checkCallListener != null) {
          checkCallListener.setUserInfo(userInfo);
        }
        Utility.showDialogAskingVideoCall(mContext, userInfo,
            new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog,
                  int which) {
                dialog.dismiss();
                if (checkCallListener != null) {
                  checkCallListener.checkCall(true);
                }
              }
            });
//				}
      }
    });

    return convertView;
  }

  public interface ICheckCall {

    public void checkCall(boolean isVideo);

    public void setUserInfo(CallUserInfo callUserInfo);
  }

  public class ViewHolder {

    public ImageView imgAvatar;
    // public ImageView imgStatus;
    public TextView txtName;
    public TextView txtDuration;
    public TextView txtTime;
    public ImageView imgVoice;
    public ImageView imgVideo;
  }
}