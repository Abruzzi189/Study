package com.application.common;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView.ScaleType;
import com.application.common.ProfilePictureTheaterBase.OnProfilePictureClickListener;
import com.application.connection.request.ImageRequest;
import com.application.imageloader.ImageFetcher;
import com.application.imageloader.ImageWorker.ImageListener;
import com.application.ui.profile.ProfilePictureData;
import com.application.util.preferece.UserPreferences;
import java.io.File;
import java.util.ArrayList;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;
import uk.co.senab.photoview.PhotoViewAttacher.OnViewTapListener;

//TODO: Reactor it...
public class DetailPictureProfileAdapter extends PagerAdapter {

  private Context mContext;
  // Fields to control
  private OnProfilePictureClickListener mOnClickListener;
  private ImageFetcher mImageFetcher;
  private ArrayList<String> mListImageId;
  private ArrayList<Boolean> mListIsSavable;
  private int mCurrentImgIndex;
  private int mAvataImgIndex;

  // Fields to view
  // private DetailPictureProfilePager mDetailPictureProfilePager;

  public DetailPictureProfileAdapter(Context context,
      ProfilePictureData profilePictureData, ImageFetcher imageFetcher) {
    this.mContext = context;
    this.mImageFetcher = imageFetcher;
    this.mListImageId = new ArrayList<String>();
    this.mListIsSavable = new ArrayList<Boolean>();
    int size = profilePictureData.getListImg().size();
    for (int i = 0; i < size; i++) {
      mListIsSavable.add(false);
    }
    mCurrentImgIndex = 0;
    mAvataImgIndex = 0;
  }

  public void setListImageId(ArrayList<String> listImageId) {
    this.mListImageId = listImageId;
    int size = listImageId.size();
    for (int i = 0; i < size; i++) {
      mListIsSavable.add(false);
    }
  }

  public int getmAvataImgIndex() {
    return mAvataImgIndex;
  }

  public void setmAvataImgIndex(int mAvataImgIndex) {
    this.mAvataImgIndex = mAvataImgIndex;
  }

  public int getCurrentImgIndex() {
    return mCurrentImgIndex;
  }

  public void setCurrentImgIndex(int index) {
    this.mCurrentImgIndex = index;
  }

  public boolean isSavable() {
    int index = getCurrentImgIndex();
    int size = this.mListIsSavable.size();
    if (index < 0 || index >= size) {
      return false;
    } else {
      return this.mListIsSavable.get(index);
    }
  }

  public File getCurrentFile() {
    String imgId = getCurrentImg();
    String token = UserPreferences.getInstance().getToken();
    ImageRequest imageRequest = new ImageRequest(token, imgId,
        ImageRequest.ORIGINAL);
    return mImageFetcher.getOriginalImage(imageRequest);
  }

  public String getCurrentImg() {
    String result = null;
    if (mCurrentImgIndex < mListImageId.size()) {
      result = mListImageId.get(mCurrentImgIndex);
    }
    return result;
  }

  @Override
  public int getCount() {
    return mListImageId.size();
  }

  @Override
  public boolean isViewFromObject(View view, Object object) {
    return view == object;
  }

  @Override
  public View instantiateItem(ViewGroup container, final int position) {
    // tungdx modified: to zoom in/out
    final PhotoView photoView = new PhotoView(container.getContext());
    String imgId = mListImageId.get(position);
    Resources resources = mContext.getResources();
    int imgSize = resources.getDisplayMetrics().widthPixels;
    String token = UserPreferences.getInstance().getToken();
    if (imgId != null && imgId.length() > 0) {
      // show progressBar
      ProfilePictureTheaterBase.showLoading();

      ImageRequest imageRequest = new ImageRequest(token, imgId,
          ImageRequest.ORIGINAL);
      mImageFetcher.loadImage(imageRequest, photoView, imgSize, imgSize,
          new ImageListener() {

            @Override
            public void onGetImageSuccess(Bitmap bitmap) {
              // hide progressBar
              ProfilePictureTheaterBase.hideLoading();

              createPhotoAttacher(photoView);
              mListIsSavable.set(position, true);
            }

            @Override
            public void onGetImageFailure() {
            }
          });
    }
    createPhotoAttacher(photoView);
    container.addView(photoView, 0);
    return photoView;
  }

  private void createPhotoAttacher(PhotoView photoView) {
    PhotoViewAttacher viewAttacher = new PhotoViewAttacher(photoView);
    viewAttacher.setScaleType(ScaleType.FIT_CENTER);
    viewAttacher.setOnViewTapListener(new OnViewTapListener() {
      @Override
      public void onViewTap(View view, float x, float y) {
        mOnClickListener.onViewPagerClick(view);
      }
    });
  }

  @Override
  public void destroyItem(ViewGroup container, int position, Object object) {
    container.removeView((View) object);
  }

  public void setOnClickListener(OnProfilePictureClickListener onClickListener) {
    this.mOnClickListener = onClickListener;
  }
}