package com.application.ui.profile;

import android.os.Bundle;
import com.application.common.Image;
import com.application.connection.response.UserInfoResponse;
import com.application.constant.Constants;
import com.application.entity.BuzzListItem;
import java.util.ArrayList;

public class ProfilePictureData {

  // Data sending flag
  public static final String USER_ID = "user_id";
  public static final String AVATA = "avata";
  public static final String START_LOCATION = "start_location";
  public static final String NUMBER_OF_IMAGE = "number_of_image";
  public static final String GENDER_PROFILE = "gender_profile";
  public static final String BUZZ_ID = "buzz_id";
  public static final String CURENT_IMG_ID = "current_img_id";
  public static final String IMAGE_CACHE_DIR = "thumbs";
  public static final int REQUEST_CODE_GET_IMAGE = 0;
  public static final int LOADER_UPDATE_AVATAR = 1;
  public static final String DATA_TYPE = "data_type";
  public static final int TYPE_UNKNOW = 0;
  public static final int TYPE_BUZZ = 1;
  public static final int TYPE_PROFILE = 2;
  public static final int TYPE_PROFILE_AVATA = 3;
  public static final int TYPE_GALLERY = 4;
  public static final int TYPE_SAVE_CHAT = 5;
  public static final int TYPE_BACKSTAGE = 6;
  public static final int TYPE_BACKSTAGE_APPROVE = 7;
  public static final int TYPE_PREVIOUS_PHOTO = 8;
  // Data of class
  private int mDataType = TYPE_UNKNOW;
  private String mUserId = "";
  private String mAvata = "";
  private String mImgId = "";
  private int mStartLocation = 0;
  private int mNumberOfImage = 0;
  private ArrayList<Image> mListImage = new ArrayList<Image>();
  private int mGender = Constants.GENDER_TYPE_MAN;

  public static Bundle parseDataToBundle(BuzzListItem buzzListItem) {
    Bundle bundle = new Bundle();
    bundle.putInt(DATA_TYPE, TYPE_BUZZ);
    bundle.putString(USER_ID, buzzListItem.getUserId());
    String avata = buzzListItem.getAvatarId();
    if (avata == null) {
      avata = "";
    }
    bundle.putString(AVATA, avata);
    bundle.putInt(GENDER_PROFILE, buzzListItem.getGender());
    bundle.putString(BUZZ_ID, buzzListItem.getBuzzId());
    bundle.putString(CURENT_IMG_ID, buzzListItem.getBuzzValue());
    return bundle;
  }

  public static Bundle parseDataToBundle(int startImgLocation,
      UserInfoResponse user, String imgId) {
    Bundle bundle = new Bundle();
    bundle.putInt(DATA_TYPE, TYPE_PROFILE);
    bundle.putString(AVATA, user.getAvataId());
    bundle.putInt(START_LOCATION, startImgLocation);
    bundle.putInt(GENDER_PROFILE, user.getGender());
    bundle.putInt(NUMBER_OF_IMAGE, user.getPublicImageNumber());
    bundle.putString(USER_ID, user.getUserId());
    bundle.putString(CURENT_IMG_ID, imgId);
    return bundle;
  }

  public static Bundle parseDataToBundle(UserInfoResponse user, String imgId) {
    Bundle bundle = new Bundle();
    bundle.putInt(DATA_TYPE, TYPE_PROFILE_AVATA);
    bundle.putString(AVATA, user.getAvataId());
    bundle.putInt(START_LOCATION, user.getPublicImageNumber());
    bundle.putInt(GENDER_PROFILE, user.getGender());
    bundle.putInt(NUMBER_OF_IMAGE, user.getPublicImageNumber());
    bundle.putString(USER_ID, user.getUserId());
    bundle.putString(CURENT_IMG_ID, imgId);
    return bundle;
  }

  public static Bundle parseDataToBundle(String imgId) {
    Bundle bundle = new Bundle();
    bundle.putInt(DATA_TYPE, TYPE_BACKSTAGE_APPROVE);
    bundle.putString(CURENT_IMG_ID, imgId);
    return bundle;
  }

  public static Bundle parseDataToBundle(String imgId, String userId) {
    Bundle bundle = new Bundle();
    bundle.putInt(DATA_TYPE, TYPE_SAVE_CHAT);
    bundle.putString(CURENT_IMG_ID, imgId);
    bundle.putString(USER_ID, userId);
    return bundle;
  }

  public static Bundle parseDataToBundle(int startImgLocation,
      int numberImage, String userId, String imgId) {
    Bundle bundle = new Bundle();
    bundle.putInt(DATA_TYPE, TYPE_BACKSTAGE);
    bundle.putString(CURENT_IMG_ID, imgId);
    bundle.putInt(START_LOCATION, startImgLocation);
    bundle.putInt(NUMBER_OF_IMAGE, numberImage);
    bundle.putString(USER_ID, userId);
    return bundle;
  }

  public int getNumberOfImage() {
    return mNumberOfImage;
  }

  public void setNumberOfImage(int numberOfImage) {
    this.mNumberOfImage = numberOfImage;
  }

  public int getDataType() {
    return mDataType;
  }

  public void setDataType(int dataType) {
    this.mDataType = dataType;
  }

  public String getUserId() {
    return mUserId;
  }

  public void setUserId(String userId) {
    this.mUserId = userId;
  }

  public String getBuzzId(int position) {
    return mListImage.get(position).getBuzz_id();
  }

  public String getImageId(int position) {
    return mListImage.get(position).getImg_id();
  }

  public String getImageId() {
    return mImgId;
  }

  /**
   * @author tungdx
   */
  public void setImageId(String imageId) {
    mImgId = imageId;
  }

  public String getAvata() {
    return mAvata;
  }

  public void setAvata(String avata) {
    this.mAvata = avata;
  }

  public int getStartLocation() {
    return mStartLocation;
  }

  public void setStartLocation(int startLocation) {
    this.mStartLocation = startLocation;
  }

  public ArrayList<String> getListImg() {
    ArrayList<String> result = new ArrayList<String>();
    for (int i = 0; i < mListImage.size(); i++) {
      result.add(mListImage.get(i).getImg_id());
    }
    return result;
  }

  public void setListImg(ArrayList<Image> listImage) {
    this.mListImage = listImage;
  }

  public boolean isOwn(int position) {
    int size = mListImage.size();
    if (size > position) {
      return mListImage.get(position).isOwn();
    } else {
      return false;
    }
  }

  public ArrayList<Image> getListImage() {
    return mListImage;
  }

  public void addListImg(ArrayList<Image> listImage) {
    this.mListImage.addAll(listImage);
  }

  public int getGender() {
    return mGender;
  }

  public void setGender(int gender) {
    this.mGender = gender;
  }

  public Bundle getBundleFromData() {
    Bundle bundle = new Bundle();
    bundle.putInt(DATA_TYPE, TYPE_GALLERY);
    bundle.putString(AVATA, this.mAvata);
    bundle.putInt(START_LOCATION, this.mStartLocation);
    bundle.putInt(GENDER_PROFILE, this.mGender);
    bundle.putInt(NUMBER_OF_IMAGE, this.mNumberOfImage);
    bundle.putString(USER_ID, this.mUserId);
    return bundle;
  }

  public Bundle getBundleFromDataForPreviousImage() {
    Bundle bundle = new Bundle();
    bundle.putInt(DATA_TYPE, TYPE_PREVIOUS_PHOTO);
    bundle.putString(AVATA, this.mAvata);
    bundle.putInt(GENDER_PROFILE, this.mGender);
    bundle.putString(USER_ID, this.mUserId);
    return bundle;
  }

  public void setDataFromBundle(Bundle bundle) {
    if (bundle != null) {
      this.mDataType = bundle.getInt(DATA_TYPE);
      switch (mDataType) {
        case TYPE_UNKNOW:
          break;
        case TYPE_BUZZ:
          setDataTypeBuzz(bundle);
          setDataType(mDataType);
          break;
        case TYPE_PROFILE:
        case TYPE_PROFILE_AVATA:
        case TYPE_GALLERY:
        case TYPE_PREVIOUS_PHOTO:
          setDataTypeProfile(bundle);
          break;
        case TYPE_SAVE_CHAT:
          setDataTypeSaveChat(bundle);
          break;
        case TYPE_BACKSTAGE:
          setDataTypeBackstage(bundle);
          break;
        case TYPE_BACKSTAGE_APPROVE:
          setDataTypeBackstageApprove(bundle);
          break;
        default:
          break;
      }
    }
  }

  private void setDataTypeBuzz(Bundle bundle) {
    this.mUserId = bundle.getString(USER_ID);
    this.mAvata = bundle.getString(AVATA);
    this.mGender = bundle.getInt(GENDER_PROFILE);
    Image image = new Image();
    image.setBuzz_id(bundle.getString(BUZZ_ID));
    image.setImg_id(bundle.getString(CURENT_IMG_ID));
    this.mImgId = image.getImg_id();
    this.mListImage.add(image);
    this.mStartLocation = 0;
  }

  private void setDataTypeProfile(Bundle bundle) {
    this.mUserId = bundle.getString(USER_ID);
    this.mAvata = bundle.getString(AVATA);
    this.mGender = bundle.getInt(GENDER_PROFILE);
    this.mStartLocation = bundle.getInt(START_LOCATION);
    this.mImgId = bundle.getString(CURENT_IMG_ID);
    this.mNumberOfImage = bundle.getInt(NUMBER_OF_IMAGE);
  }

  private void setDataTypeSaveChat(Bundle bundle) {
    Image image = new Image();
    image.setBuzz_id("");
    image.setImg_id(bundle.getString(CURENT_IMG_ID));
    this.mListImage.add(image);
    this.mStartLocation = 0;
    this.mUserId = bundle.getString(USER_ID);
  }

  private void setDataTypeBackstage(Bundle bundle) {
    this.mUserId = bundle.getString(USER_ID);
    this.mImgId = bundle.getString(CURENT_IMG_ID);
    this.mStartLocation = bundle.getInt(START_LOCATION);
    this.mNumberOfImage = bundle.getInt(NUMBER_OF_IMAGE);
  }

  private void setDataTypeBackstageApprove(Bundle bundle) {
    this.mImgId = bundle.getString(CURENT_IMG_ID);
  }
}