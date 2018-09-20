package com.application.ui.customeview;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.application.connection.request.ImageRequest;
import com.application.entity.GiftRecieve;
import com.application.imageloader.ImageFetcher;
import com.application.util.preferece.UserPreferences;
import glas.bbsystem.R;
import java.util.ArrayList;


public class GiftAdapter extends ArrayAdapter<GiftRecieve> {

  public static final int MAX_SHOW = 8;
  private ImageFetcher mImageFetcher;
  private ArrayList<GiftRecieve> list;
  private int columnSize = 0;
  private int mMarginLeft;
  private int mGiftSize;

  @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
  @SuppressWarnings("deprecation")
  public GiftAdapter(Context context, int textViewResourceId,
      ArrayList<GiftRecieve> objects, ImageFetcher mImageFetcher) {
    super(context, textViewResourceId, objects);
    mGiftSize = context.getResources().getDimensionPixelSize(
        R.dimen.gift_size);
    this.mImageFetcher = mImageFetcher;
    this.list = objects;
    Point size = new Point();
    WindowManager windowManager = (WindowManager) context
        .getSystemService(Context.WINDOW_SERVICE);
    Display display = windowManager.getDefaultDisplay();
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
      display.getSize(size);
    } else {
      size.x = display.getWidth();
      size.y = display.getHeight();
    }
    mMarginLeft = (int) context.getResources().getDimension(
        R.dimen.conversation_avatar_margin);
    columnSize = (size.x - mMarginLeft * 7) / 4 - 20;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    Holder holder = null;
    GiftRecieve item = getItem(position);
    if (convertView == null) {
      holder = new Holder();
      LayoutInflater inflater = (LayoutInflater) getContext()
          .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      convertView = inflater.inflate(R.layout.item_profile_gift_grid,
          null);
      holder.imgDisplay = (ImageView) convertView
          .findViewById(R.id.item_profile_gift_grid_img_display);
      holder.txtNumber = (TextView) convertView
          .findViewById(R.id.item_profile_gift_grid_txt_number);
      RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
          columnSize, columnSize);
      params.addRule(RelativeLayout.CENTER_HORIZONTAL);
      holder.imgDisplay.setLayoutParams(params);
      convertView.setTag(holder);
    } else {
      holder = (Holder) convertView.getTag();
    }

    holder.txtNumber.setText(item.getNumber() + "");
    String token = UserPreferences.getInstance().getToken();
    ImageRequest giftRequest = new ImageRequest(token, item.getGiftId(),
        ImageRequest.GIFT);
    mImageFetcher.loadImageWithoutPlaceHolder(giftRequest,
        holder.imgDisplay, mGiftSize);
    if (position > MAX_SHOW) {
      convertView.setVisibility(View.GONE);
    } else {
      convertView.setVisibility(View.VISIBLE);
    }
    return convertView;
  }

  public void updateList(ArrayList<GiftRecieve> list) {
    if (this.list.size() > 0) {
      this.list.clear();
      for (GiftRecieve item : list) {
        this.list.add(item);
      }
    } else {
      this.list = list;
    }
    this.notifyDataSetChanged();
  }

  private class Holder {

    private ImageView imgDisplay;
    private TextView txtNumber;
  }
}
