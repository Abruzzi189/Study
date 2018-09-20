package com.application.ui.backstage;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import com.application.connection.request.ImageRequest;
import com.application.constant.ConstantsImage;
import com.application.util.preferece.UserPreferences;
import com.squareup.picasso.Picasso;
import glas.bbsystem.R;
import java.util.ArrayList;
import java.util.List;


public class BackstageAdapter extends BaseAdapter {

  public static final int ITEM_TYPE_ADD_VIEW = 0;
  public static final int ITEM_TYPE_IMAGE_VIEW = 1;
  public static final int ITEM_TYPE_DUMMY_VIEW = 2;
  public static final String ADD_BACKSTAGE_IMAGE = "add_backstage_image";
  public static final String DUMMY_BACKSTAGE_IMAGE = "dummy_backstage_image";
  private static final int NUMBER_OF_TYPE = 3;
  private List<String> mlistImg;
  private List<Boolean> mlistRotatable;
  private Context mContext;
  private int mAvatarSize;

  public BackstageAdapter(Context context, ArrayList<String> listImg) {
    this.mContext = context;
    this.mlistImg = listImg;
    this.mlistRotatable = new ArrayList<Boolean>();
    int numberImg = this.mlistImg.size();
    for (int i = 0; i < numberImg; i++) {
      this.mlistRotatable.add(false);
    }
    this.mAvatarSize = mContext.getResources().getDimensionPixelSize(
        R.dimen.activity_setupprofile_img_avatar_width);
  }

  public void clearAllData() {
    if (mlistImg.size() == 0) {
      return;
    }
    List<String> listImg = mlistImg.subList(0, 1);
    if (listImg.get(0).equals(ADD_BACKSTAGE_IMAGE)) {
      mlistImg = listImg;
    } else {
      mlistImg.clear();
    }
    this.notifyDataSetChanged();
  }

  public void remove(int location) {
    this.mlistImg.remove(location);
  }

  public void setAvatarSize(int height) {
    if (height == mAvatarSize) {
      return;
    }
    mAvatarSize = height;
    notifyDataSetChanged();
  }

  public void addMoreImage(String imgId) {
    this.mlistImg.add(imgId);
    this.mlistRotatable.add(false);
  }

  public void addNewImage(String imgId) {
    if (this.mlistImg == null || this.mlistImg.size() < 1
        || !ADD_BACKSTAGE_IMAGE.equals(mlistImg.get(0))) {
      this.mlistImg.add(0, imgId);
      this.mlistRotatable.add(0, false);
    } else {
      this.mlistImg.add(1, imgId);
      this.mlistRotatable.add(1, false);
    }
  }

  public void setImg(int index, String imgId) {
    this.mlistImg.set(index, imgId);
  }

  public boolean isContain(String imgId) {
    return mlistImg.contains(imgId);
  }

  @Override
  public int getViewTypeCount() {
    return NUMBER_OF_TYPE;
  }

  @Override
  public int getCount() {
    return mlistImg.size();
  }

  @Override
  public int getItemViewType(int position) {
    if (ADD_BACKSTAGE_IMAGE.equals(mlistImg.get(position))) {
      return ITEM_TYPE_ADD_VIEW;
    } else if (DUMMY_BACKSTAGE_IMAGE.equals(mlistImg.get(position))) {
      return ITEM_TYPE_DUMMY_VIEW;
    } else {
      return ITEM_TYPE_IMAGE_VIEW;
    }
  }

  @Override
  public String getItem(int position) {
    return mlistImg.get(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  private View getAddViewType(View v) {
    AddPicViewHolder holder = null;
    if (v == null) {
      holder = new AddPicViewHolder();
      v = View.inflate(mContext,
          R.layout.item_gallery_grid_image_add_pic, null);
      holder.imgDisplay = (ImageView) v
          .findViewById(R.id.item_image_gallery);
      holder.frameLayout = (FrameLayout) v
          .findViewById(R.id.item_grid_image_gallery_layout_frm);
      holder.txtAddPic = (TextView) v
          .findViewById(R.id.item_image_gallery_text);
      v.setTag(holder);
    } else {
      holder = (AddPicViewHolder) v.getTag();
    }
    GridView.LayoutParams layoutParam = new GridView.LayoutParams(
        LayoutParams.MATCH_PARENT, mAvatarSize);
    holder.frameLayout.setLayoutParams(layoutParam);
    holder.imgDisplay.setScaleType(ScaleType.CENTER_CROP);
    holder.imgDisplay.setImageResource(ConstantsImage.ADD_BACKSTAGE_IMAGE);
    holder.txtAddPic.setVisibility(View.VISIBLE);
    return v;
  }

  private View getImageViewType(View v, int position) {
    ImageViewHolder holder = null;
    boolean isRotatable = this.mlistRotatable.get(position);
    if (v == null || isRotatable) {
      holder = new ImageViewHolder();
      v = View.inflate(mContext, R.layout.item_gallery_grid_image, null);
      holder.imgDisplay = (ImageView) v
          .findViewById(R.id.item_image_gallery);
      holder.frameLayout = (FrameLayout) v
          .findViewById(R.id.item_grid_image_gallery_layout_frm);
      v.setTag(holder);
    } else {
      holder = (ImageViewHolder) v.getTag();
    }
    GridView.LayoutParams layoutParam = new GridView.LayoutParams(
        LayoutParams.MATCH_PARENT, mAvatarSize);
    holder.frameLayout.setLayoutParams(layoutParam);
    String token = UserPreferences.getInstance().getToken();
    String imgId = mlistImg.get(position);
    if (isRotatable
        && (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1)) {
      // TamTD: Temporary hiding rotate image code
//			rotateImg(holder.imgDisplay, imgId, token);
      if (imgId != null && imgId.length() > 0) {
        ImageRequest imageRequest = new ImageRequest(token, imgId,
            ImageRequest.THUMBNAIL);
        Picasso.with(mContext).load(imageRequest.toURL())
            .placeholder(R.drawable.dummy_avatar).noFade()
            .into(holder.imgDisplay);
      }
    } else {
      if (imgId != null && imgId.length() > 0) {
        ImageRequest imageRequest = new ImageRequest(token, imgId,
            ImageRequest.THUMBNAIL);
        Picasso.with(mContext).load(imageRequest.toURL())
            .placeholder(R.drawable.dummy_avatar).noFade()
            .into(holder.imgDisplay);
      }
    }

    return v;
  }

  private View getDummyViewType(View v) {
    DummyImageViewHolder holder = null;
    if (v == null) {
      holder = new DummyImageViewHolder();
      v = View.inflate(mContext, R.layout.item_gallery_grid_image, null);
      holder.imgDisplay = (ImageView) v
          .findViewById(R.id.item_image_gallery);
      holder.frameLayout = (FrameLayout) v
          .findViewById(R.id.item_grid_image_gallery_layout_frm);
      v.setTag(holder);
    } else {
      holder = (DummyImageViewHolder) v.getTag();
    }
    GridView.LayoutParams layoutParam = new GridView.LayoutParams(
        LayoutParams.MATCH_PARENT, mAvatarSize);
    holder.frameLayout.setLayoutParams(layoutParam);
    holder.imgDisplay.setScaleType(ScaleType.CENTER_CROP);
    holder.imgDisplay
        .setImageResource(ConstantsImage.DUMMY_BACKSTAGE_IMAGE);
    return v;
  }

  @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
  private void rotateImg(final ImageView v, final String imgId,
      final String token) {
    v.animate().rotationY(90).setDuration(5000)
        .setListener(new AnimatorListener() {
          @Override
          public void onAnimationStart(Animator animation) {
          }

          @Override
          public void onAnimationRepeat(Animator animation) {
          }

          @Override
          public void onAnimationEnd(Animator animation) {
            v.animate().setListener(this);
            v.animate().rotationY(0).setDuration(5000);
            if (imgId != null && imgId.length() > 0) {
              ImageRequest imageRequest = new ImageRequest(token,
                  imgId, ImageRequest.THUMBNAIL);
              // mImageFetcher.loadImage(imageRequest, v,
              // mAvatarSize);
              Picasso.with(mContext).load(imageRequest.toURL())
                  .placeholder(ConstantsImage.DUMMY_IMAGE)
                  .noFade().into(v);
            }
          }

          @Override
          public void onAnimationCancel(Animator animation) {
          }
        });
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    int itemType = getItemViewType(position);
    switch (itemType) {
      case ITEM_TYPE_ADD_VIEW:
        convertView = getAddViewType(convertView);
        this.mlistRotatable.set(position, false);
        break;
      case ITEM_TYPE_IMAGE_VIEW:
        convertView = getImageViewType(convertView, position);
        this.mlistRotatable.set(position, false);
        break;
      case ITEM_TYPE_DUMMY_VIEW:
        convertView = getDummyViewType(convertView);
        this.mlistRotatable.set(position, true);
        break;
    }
    return convertView;
  }

  private class AddPicViewHolder {

    public ImageView imgDisplay;
    public FrameLayout frameLayout;
    public TextView txtAddPic;
  }

  private class ImageViewHolder {

    public ImageView imgDisplay;
    public FrameLayout frameLayout;
  }

  private class DummyImageViewHolder {

    public ImageView imgDisplay;
    public FrameLayout frameLayout;
  }
}