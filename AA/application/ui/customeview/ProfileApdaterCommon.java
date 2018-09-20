package com.application.ui.customeview;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.application.common.Image;
import com.application.connection.ResponseData;
import com.application.connection.request.ImageRequest;
import com.application.connection.response.UserInfoResponse;
import com.application.constant.Constants;
import com.application.constant.UserSetting;
import com.application.entity.BuzzListCommentItem;
import com.application.entity.BuzzListItem;
import com.application.entity.CallUserInfo;
import com.application.entity.UserInfo;
import com.application.ui.backstage.ManageBackstageActivity;
import com.application.ui.buzz.BuzzItemListView;
import com.application.ui.buzz.BuzzItemListView.OnActionBuzzListener;
import com.application.ui.buzz.CommentItemBuzz;
import com.application.ui.buzz.SubCommentItemBuzz;
import com.application.ui.notification.ManageOnlineAlertFragment;
import com.application.ui.profile.DetailPictureProfileActivity;
import com.application.ui.profile.MyProfileFragment;
import com.application.ui.profile.ProfilePictureData;
import com.application.util.DesignUtils;
import com.application.util.ImageUtil;
import com.application.util.LogUtils;
import com.application.util.RegionUtils;
import com.application.util.Utility;
import com.application.util.preferece.GoogleReviewPreference;
import com.application.util.preferece.UserPreferences;
import com.example.tux.mylab.gallery.Gallery;
import glas.bbsystem.R;
import java.text.ParseException;
import java.util.ArrayList;

public class ProfileApdaterCommon extends BaseAdapter {

  public final static int PROFILE_TAB = 0;
  public final static int BUZZ_TAB = 1;
  private final int TYPE_AVATAR = 0;
  private final int TYPE_BUZZ_STATUS = 1;
  private final int TYPE_BUZZ_GIFT = 2;
  private final int TYPE_BUZZ_IMAGE = 3;
  private ArrayList<Object> objects;
  private Activity activity;
  private MyProfileFragment mFragment;
  private UserInfoHolderView holderView;
  private String mUserId = "";
  private int mGender;
  private String mAvataId;
  private boolean isMe = false;
  private OnActionBuzzListener actionBuzzListener;
  private CommentItemBuzz.OnActionCommentListener deleteCommentListener;
  private SubCommentItemBuzz.OnDeleteSubCommentListener deleteSubCommentListener;
  private OnPanelClickListener onPanelClickListener;
  private int mThumbSize;
  private int margin = 0;
  private int paddingBuzzView;
  private boolean isButtonEnable = true;
  private boolean isFavorite;
  private int mTabSelected;
  private int mNumberOfImage;
  private RegionUtils regionUtils;
  private String[] jobMale;
  private String[] jobFemale;
  private String[] cubTypes;
  private String[] cupSizes;
  private String[] joinHours;
  private ArrayList<Image> listPublicImage;

  @SuppressWarnings("deprecation")
  @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
  public ProfileApdaterCommon(Activity activity, MyProfileFragment fragment,
      ArrayList<Object> objects, boolean itisme) {
    isButtonEnable = true;
    this.activity = activity;
    this.objects = objects;
    this.isMe = itisme;
    mFragment = fragment;
    this.mAvataId = "";
    Display display = activity.getWindowManager().getDefaultDisplay();
    Point size = new Point();
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
      display.getSize(size);
    } else {
      size.x = display.getWidth();
    }
    margin = (int) activity.getResources().getDimension(
        R.dimen.sd_hori_space_small);
    paddingBuzzView = (int) activity.getResources().getDimension(
        R.dimen.sd_hori_space_medium);
    mThumbSize = (size.x - (int) margin * 6) / 5;
    jobMale = activity.getResources().getStringArray(R.array.job_male);
    jobFemale = activity.getResources().getStringArray(R.array.job_female);
    cubTypes = activity.getResources().getStringArray(R.array.cub_type);
    cupSizes = activity.getResources().getStringArray(R.array.cub_size);

    regionUtils = new RegionUtils(activity);
    joinHours = activity.getResources().getStringArray(R.array.join_hours);
  }

  public void setOnActionBuzzListener(OnActionBuzzListener listener) {
    this.actionBuzzListener = listener;
  }

  public void setOnDeleteBuzzCommentListener(CommentItemBuzz.OnActionCommentListener listener) {
    this.deleteCommentListener = listener;
  }

  public void setOnDeleteSubCommentListener(
      SubCommentItemBuzz.OnDeleteSubCommentListener listener) {
    this.deleteSubCommentListener = listener;
  }

  public void setOnPanelClickListener(OnPanelClickListener listener) {
    this.onPanelClickListener = listener;
  }

  public void setFavorite(boolean isFavorite) {
    this.isFavorite = isFavorite;
    if (holderView != null) {
      updateFavourite(isFavorite, holderView);
    }
  }

  public void setButtonEnable(boolean isButtonEnable) {
    this.isButtonEnable = isButtonEnable;
  }

  @Override
  public int getCount() {
    if (objects == null || objects.size() == 0) {
      return 0;
    }
    return objects.size();
  }

  @Override
  public Object getItem(int position) {
    return position;
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public int getViewTypeCount() {
    return 4;
  }

  @Override
  public int getItemViewType(int position) {
    Object object = objects.get(position);
    if (object instanceof UserInfo) {
      return TYPE_AVATAR;
    } else if (object instanceof BuzzListItem) {
      switch (((BuzzListItem) object).getBuzzType()) {
        case Constants.BUZZ_TYPE_STATUS:
          return TYPE_BUZZ_STATUS;
        case Constants.BUZZ_TYPE_GIFT:
          return TYPE_BUZZ_GIFT;
        case Constants.BUZZ_TYPE_IMAGE:
          return TYPE_BUZZ_IMAGE;
        default:
          break;
      }
    }
    return -1;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    UserInfoHolderView userInfoHolderView;
    Object object = objects.get(position);
    int type = getItemViewType(position);
    LayoutInflater inflater = (LayoutInflater) activity
        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//		LayoutInflater inflater = activity.
    if (type == TYPE_AVATAR) {
      // profile
      if (convertView == null) {
        userInfoHolderView = new UserInfoHolderView();
        convertView = inflater.inflate(R.layout.item_profile_info,
            parent, false);
        RelativeLayout subProfileInfoLayout = (RelativeLayout) convertView
            .findViewById(R.id.profile_info_layout);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.MATCH_PARENT);
        View childView;
        if (((UserInfo) object).getUser().getGender() == Constants.GENDER_TYPE_MAN) {
          childView = inflater.inflate(
              R.layout.item_the_basic_profile_info_male,
              subProfileInfoLayout, false);
        } else {
          childView = inflater.inflate(
              R.layout.item_the_basic_profile_info_female,
              subProfileInfoLayout, false);
        }

        subProfileInfoLayout.addView(childView, params);
        initialUserInfoView(userInfoHolderView, convertView);
        convertView.setTag(userInfoHolderView);
      } else {
        userInfoHolderView = (UserInfoHolderView) convertView.getTag();
      }

      // Fixed active tabs
      userInfoHolderView.userInfo.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          notifyTabChanged(PROFILE_TAB);
        }
      });

      userInfoHolderView.userBuzz.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View arg0) {
          notifyTabChanged(BUZZ_TAB);
        }
      });
      // End Fixed active tabs

      setUserInfoValue(userInfoHolderView, (UserInfo) object);
      if (mTabSelected != PROFILE_TAB) {
        userInfoHolderView.mRootLayout.setVisibility(View.GONE);
        // Fixed active tabs
        userInfoHolderView.userBuzz.setSelected(true);
        userInfoHolderView.userInfo.setSelected(false);
        userInfoHolderView.userBuzz.setTextColor(Color.parseColor("#FFFFFF"));
        userInfoHolderView.userBuzz.setBackgroundColor(Color.parseColor("#fa522a"));
        userInfoHolderView.userInfo.setTextColor(Color.parseColor("#000000"));
        userInfoHolderView.userInfo.setBackgroundColor(Color.parseColor("#FFFFFF"));
        // End Fixed active tabs
      } else {
        userInfoHolderView.mRootLayout.setVisibility(View.VISIBLE);
        // Fixed active tabs
        userInfoHolderView.userBuzz.setSelected(false);
        userInfoHolderView.userInfo.setSelected(true);
        userInfoHolderView.userInfo.setTextColor(Color.parseColor("#FFFFFF"));
        userInfoHolderView.userInfo.setBackgroundColor(Color.parseColor("#fa522a"));
        userInfoHolderView.userBuzz.setTextColor(Color.parseColor("#000000"));
        userInfoHolderView.userBuzz.setBackgroundColor(Color.parseColor("#FFFFFF"));
        // End Fixed active tabs
      }
      setThumbImage(userInfoHolderView);
    } else if (type == TYPE_BUZZ_STATUS || type == TYPE_BUZZ_GIFT
        || type == TYPE_BUZZ_IMAGE) {
      // buzz
      BuzzItemListView holder = null;
      if (mTabSelected != BUZZ_TAB) {
        convertView = inflater.inflate(R.layout.empty_view, parent,
            false);
        convertView.setTag(null);
        return convertView;
      }
      BuzzListItem buzz = (BuzzListItem) objects.get(position);
      if (convertView == null || convertView.getTag() == null) {
        convertView = new LinearLayout(activity);
        convertView.setPadding(paddingBuzzView, paddingBuzzView,
            paddingBuzzView, 0);
        holder = new BuzzItemListView(activity, buzz.getBuzzType(),
            true);
        ((LinearLayout) convertView).addView(holder);
        convertView.setTag(holder);
      } else {
        holder = (BuzzItemListView) convertView.getTag();
      }
      holder.updateView(buzz, true, objects.indexOf(object), deleteCommentListener,
          deleteSubCommentListener, actionBuzzListener);

      holder.setMyProfileFragment(mFragment);
    }
    return convertView;
  }

  private void initialUserInfoView(
      final UserInfoHolderView userInfoHolderView, View convertView) {
    userInfoHolderView.mRootLayout = convertView
        .findViewById(R.id.root_layout);
    userInfoHolderView.ivProfile = (ImageView) convertView
        .findViewById(R.id.ivProfile);
    userInfoHolderView.avaThumb[0] = (ImageView) convertView
        .findViewById(R.id.profile_ava_thumb0);
    userInfoHolderView.avaThumb[1] = (ImageView) convertView
        .findViewById(R.id.profile_ava_thumb1);
    userInfoHolderView.avaThumb[2] = (ImageView) convertView
        .findViewById(R.id.profile_ava_thumb2);
    userInfoHolderView.avaThumb[3] = (ImageView) convertView
        .findViewById(R.id.profile_ava_thumb3);
    userInfoHolderView.mViewTabBuzzSelected = convertView
        .findViewById(R.id.view_select_buzz);
    userInfoHolderView.mViewTabProfileSelected = convertView
        .findViewById(R.id.view_select_user);
    userInfoHolderView.tvTimeLogin = (TextView) convertView
        .findViewById(R.id.time_online_txt);
    FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
        activity.getResources().getDisplayMetrics().widthPixels,
        activity.getResources().getDisplayMetrics().widthPixels);
    LinearLayout.LayoutParams paramsThumb = new LinearLayout.LayoutParams(
        mThumbSize, mThumbSize);
    paramsThumb.leftMargin = margin;

    userInfoHolderView.avaThumb[0].setLayoutParams(paramsThumb);
    userInfoHolderView.avaThumb[1].setLayoutParams(paramsThumb);
    userInfoHolderView.avaThumb[2].setLayoutParams(paramsThumb);
    userInfoHolderView.avaThumb[3].setLayoutParams(paramsThumb);
    userInfoHolderView.ivProfile.setLayoutParams(layoutParams);
    userInfoHolderView.tvStatus = (TextView) convertView
        .findViewById(R.id.profile_txt_Status);
    userInfoHolderView.tvName = (TextView) convertView
        .findViewById(R.id.profile_tv_name);
    userInfoHolderView.tvAge = (TextView) convertView
        .findViewById(R.id.profile_txt_age);
    userInfoHolderView.tvRegion = (TextView) convertView
        .findViewById(R.id.profile_txt_region);
    userInfoHolderView.mVoiceCallLayout = convertView
        .findViewById(R.id.voiceCall_layout);
    userInfoHolderView.mPhoneText = (TextView) convertView
        .findViewById(R.id.phone_txt);
    userInfoHolderView.mVideoCallLayout = convertView
        .findViewById(R.id.videoCall_layout);
    userInfoHolderView.mVideoText = (TextView) convertView
        .findViewById(R.id.video_txt);
    userInfoHolderView.tvThreeSizes = (TextView) convertView
        .findViewById(R.id.profile_txt_three_sizes);
    userInfoHolderView.lThreeSizes = convertView
        .findViewById(R.id.profile_tbr_three_sizes);

    userInfoHolderView.tvCupSize = (TextView) convertView
        .findViewById(R.id.profile_txt_cup_size);
    userInfoHolderView.lCupSize = convertView
        .findViewById(R.id.profile_tbr_cup_size);

    userInfoHolderView.tvCupType = (TextView) convertView
        .findViewById(R.id.profile_txt_cup_type);
    userInfoHolderView.lCupType = convertView
        .findViewById(R.id.profile_cup_type);

    userInfoHolderView.tvTypeOfMan = (TextView) convertView
        .findViewById(R.id.profile_txt_type_of_man);
    userInfoHolderView.lTypeOfMan = convertView
        .findViewById(R.id.profile_type_of_man);

    userInfoHolderView.tvFetish = (TextView) convertView
        .findViewById(R.id.profile_txt_fetish);
    userInfoHolderView.lFetish = convertView
        .findViewById(R.id.profile_fetish);

    userInfoHolderView.tvJoinHours = (TextView) convertView
        .findViewById(R.id.profile_txt_join_hours);
    userInfoHolderView.lJoinHours = convertView
        .findViewById(R.id.profile_join_hours);

    userInfoHolderView.tvHobby = (TextView) convertView
        .findViewById(R.id.profile_txt_hobby);
    userInfoHolderView.lHobby = convertView
        .findViewById(R.id.profile_tbr_hobby);

    userInfoHolderView.tvAbout = (TextView) convertView
        .findViewById(R.id.about_me);
    userInfoHolderView.lAbout = convertView
        .findViewById(R.id.profile_tbr_about_me);

    userInfoHolderView.userInfo = (TextView) convertView
        .findViewById(R.id.profile_info_btn_friend_number);

//HIEPUH
    userInfoHolderView.userInfo.setTextColor(Color.parseColor("#FFFFFF"));
    userInfoHolderView.userInfo.setBackgroundColor(Color.parseColor("#fa522a"));

//		userInfoHolderView.mViewTabProfileSelected
//				.setVisibility(mTabSelected == PROFILE_TAB ? View.VISIBLE
//						: View.GONE);
//HIEPUH
    userInfoHolderView.userBuzz = (TextView) convertView
        .findViewById(R.id.profile_info_btn_backstage_number);
    userInfoHolderView.userBuzz.setTextColor(Color.parseColor("#fa522a"));
    userInfoHolderView.userBuzz.setBackgroundColor(Color.parseColor("#FFFFFF"));
    userInfoHolderView.userInfo
        .setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            //HIEPUH
            notifyTabChanged(PROFILE_TAB);
            userInfoHolderView.userBuzz.setSelected(false);
            userInfoHolderView.userInfo.setSelected(true);
//						userInfoHolderView.mViewTabBuzzSelected
//								.setVisibility(View.GONE);
//						userInfoHolderView.mViewTabProfileSelected
//								.setVisibility(View.VISIBLE);

          }
        });

    userInfoHolderView.userBuzz = (TextView) convertView
        .findViewById(R.id.profile_info_btn_backstage_number);
//		userInfoHolderView.mViewTabBuzzSelected
//				.setVisibility(mTabSelected == BUZZ_TAB ? View.VISIBLE
//						: View.GONE);
    userInfoHolderView.userBuzz.setSelected(mTabSelected == BUZZ_TAB);
    userInfoHolderView.userInfo.setSelected(mTabSelected == PROFILE_TAB);
    userInfoHolderView.userBuzz
        .setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View arg0) {
            notifyTabChanged(BUZZ_TAB);
            userInfoHolderView.userBuzz.setSelected(true);
            userInfoHolderView.userInfo.setSelected(false);
//						userInfoHolderView.mViewTabBuzzSelected
//								.setVisibility(View.VISIBLE);
//						userInfoHolderView.mViewTabProfileSelected
//								.setVisibility(View.GONE);

          }
        });
    userInfoHolderView.mFavouriteTxt = (TextView) convertView
        .findViewById(R.id.favourite_txt);
    updateFavourite(isFavorite, userInfoHolderView);
    userInfoHolderView.mFavouriteTxt
        .setOnClickListener(new OnClickListener() {

          @Override
          public void onClick(View v) {
            if (onPanelClickListener != null) {
              onPanelClickListener.onFavourite();
            }
          }
        });
    userInfoHolderView.mGiveGifTxt = (TextView) convertView
        .findViewById(R.id.give_gift_txt);
    userInfoHolderView.mGiveGifTxt
        .setOnClickListener(new OnClickListener() {

          @Override
          public void onClick(View v) {
            if (onPanelClickListener != null) {
              onPanelClickListener.onGiveGif();
            }
          }
        });
    userInfoHolderView.mOnlineAlertTxt = (TextView) convertView
        .findViewById(R.id.online_alert_txt);
    userInfoHolderView.mOnlineAlertTxt
        .setOnClickListener(new OnClickListener() {

          @Override
          public void onClick(View v) {
            if (onPanelClickListener != null) {
              onPanelClickListener.onSetOnlineAlert();
            }
          }
        });

    userInfoHolderView.mBackStageLayout = convertView
        .findViewById(R.id.backstage_layout);
    userInfoHolderView.mBackStageLayout.setLayoutParams(paramsThumb);
    userInfoHolderView.mChatTxt = (TextView) convertView
        .findViewById(R.id.message_txt);

    DesignUtils.setTextViewDrawableColor(userInfoHolderView.mChatTxt, Color.parseColor("#fa522a"));

    userInfoHolderView.mChatTxt
        .setOnClickListener(new View.OnClickListener() {

          @Override
          public void onClick(View arg0) {
            if (onPanelClickListener != null) {
              onPanelClickListener.chat();
            }
          }
        });

    userInfoHolderView.tvJob = (TextView) convertView
        .findViewById(R.id.profile_txt_job);

    userInfoHolderView.mReportTxt = convertView
        .findViewById(R.id.report_txt);
    userInfoHolderView.mReportTxt
        .setOnClickListener(new View.OnClickListener() {

          @Override
          public void onClick(View v) {
            if (onPanelClickListener != null) {
              onPanelClickListener.onReport();
            }
          }
        });

    userInfoHolderView.mBlockTxt = convertView.findViewById(R.id.block_txt);
    userInfoHolderView.mBlockTxt
        .setOnClickListener(new View.OnClickListener() {

          @Override
          public void onClick(View v) {
            if (onPanelClickListener != null) {
              onPanelClickListener.onBlock();
            }
          }
        });

    userInfoHolderView.mBackstageNumber = (BadgeTextView) convertView
        .findViewById(R.id.backstage_badge);

    // Fixed active tabs
    userInfoHolderView.userBuzz.setSelected(true);
    userInfoHolderView.userInfo.setSelected(false);
    userInfoHolderView.userInfo.setTextColor(Color.parseColor("#FFFFFF"));
    userInfoHolderView.userInfo.setBackgroundColor(Color.parseColor("#fa522a"));
    userInfoHolderView.userBuzz.setTextColor(Color.parseColor("#fa522a"));
    userInfoHolderView.userBuzz.setBackgroundColor(Color.parseColor("#FFFFFF"));
    // End Fixed active tabs
  }

  private void updateFavourite(boolean isFavorite,
      UserInfoHolderView userInfoHolderView) {
    if (isFavorite) {
      userInfoHolderView.mFavouriteTxt.setText(activity.getResources()
          .getString(R.string.profile_favorited));
      userInfoHolderView.mFavouriteTxt
          .setCompoundDrawablesWithIntrinsicBounds(
              null,
              activity.getResources().getDrawable(
                  R.drawable.ic_profile_favorited), null,
              null);
    } else {
      userInfoHolderView.mFavouriteTxt.setText(activity.getResources()
          .getString(R.string.profile_favorite));
      userInfoHolderView.mFavouriteTxt
          .setCompoundDrawablesWithIntrinsicBounds(
              null,
              activity.getResources().getDrawable(
                  R.drawable.ic_profile_favorite), null, null);
    }
  }

  private void setUserInfoValue(final UserInfoHolderView userInfoHolderView,
      final UserInfo userInfo) {
    String token = UserPreferences.getInstance().getToken();
    String imag_id = userInfo.getUser().getAvataId();
    mUserId = userInfo.getUser().getUserId();
    mGender = userInfo.getUser().getGender();
    mNumberOfImage = userInfo.getUser().getPublicImageNumber();
    if (userInfo.getUser().getGender() == UserSetting.GENDER_MALE) {
      userInfoHolderView.ivProfile
          .setImageResource(R.drawable.dummy_avatar_male);
    } else {
      userInfoHolderView.ivProfile
          .setImageResource(R.drawable.dummy_avatar_female);
    }
    // load avatar that has cover by gender
    int avatarSize = (int) (activity.getResources().getDisplayMetrics().widthPixels);
    mAvataId = imag_id;
    ImageRequest imageRequest = new ImageRequest(token, imag_id,
        ImageRequest.ORIGINAL);
    ImageUtil.loadAvataImage(activity, imageRequest.toURL(),
        userInfoHolderView.ivProfile, avatarSize);
    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
        avatarSize, avatarSize);
    userInfoHolderView.ivProfile.setLayoutParams(params);
//		userInfoHolderView.ivProfile.setTag(userInfo);
    userInfoHolderView.ivProfile.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View view) {
        String avata = userInfo.getUser().getAvataId();
        if (avata == null || "".equals(avata)) {
          if (isMe) {
            if (isButtonEnable) {
              isButtonEnable = false;
              mFragment.setNeedRefreshData(false);
//							MediaOptions.Builder builder = new MediaOptions.Builder();
//							MediaOptions options = builder.setIsCropped(true).setFixAspectRatio(true).selectPhoto().build();
//							MediaPickerActivity.open(activity, MyProfileFragment.REQUEST_CODE_GET_AVATAR, options);

              new Gallery.Builder()
                  .cropOutput(true)
                  .fixAspectRatio(true)
                  .multiChoice(false)
                  .viewType(Gallery.VIEW_TYPE_PHOTOS_ONLY)
                  .build()
                  .start(activity);
            }
          }
        } else {
          if (userInfo.getUser().getPublicImageNumber() < 1) {
            return;
          }
          if (isButtonEnable) {
            isButtonEnable = false;
            Intent intent = new Intent(activity,
                DetailPictureProfileActivity.class);
            UserInfoResponse user = userInfo.getUser();
            Bundle userData = ProfilePictureData.parseDataToBundle(
                user, avata);
            intent.putExtras(userData);
            mFragment.setNeedRefreshData(true);
            mFragment.startActivity(intent);
          }
        }
      }

    });

    // set region
    int regionCode = userInfo.getUser().getRegion();
    userInfoHolderView.tvRegion.setText(regionUtils
        .getRegionName(regionCode));

    // set backstage
    userInfoHolderView.mBackstageNumber.setMinNumberToVisible(0);
    userInfoHolderView.mBackstageNumber.setTextNumber(userInfo.getUser()
        .getBackstageNumber());
    userInfoHolderView.mBackStageLayout
        .setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View arg0) {
            UserInfoResponse user = userInfo.getUser();
            String myUserId = UserPreferences.getInstance()
                .getUserId();
            if (user.getBackstageNumber() > 0
                || user.getUserId().equals(myUserId)) {
              if (isButtonEnable) {
                isButtonEnable = false;
                Intent intent = new Intent(activity,
                    ManageBackstageActivity.class);
                intent.putExtra(
                    ManageBackstageActivity.KEY_USER_ID,
                    user.getUserId());
                intent.putExtra(
                    ManageBackstageActivity.KEY_USER_NAME,
                    user.getUserName());
                intent.putExtra(
                    ManageBackstageActivity.KEY_AVATA,
                    mAvataId);
                intent.putExtra(
                    ManageBackstageActivity.KEY_NUMBER_IMG,
                    user.getBackstageNumber());
                mFragment.setNeedRefreshData(true);
                mFragment
                    .startActivityForResult(
                        intent,
                        MyProfileFragment.REQUEST_CODE_BACKSTAGE);
              }
            }
          }
        });

    if (userInfo.getUser().getStatus() != null) {
      userInfoHolderView.tvStatus.setText(userInfo.getUser().getStatus());
    } else {
      userInfoHolderView.tvStatus.setText("");
    }
    userInfoHolderView.tvName.setText(userInfo.getUser().getUserName());
    Log.e("hiepuh", "" + userInfo.getUser().getUserName());
    userInfoHolderView.tvAge.setText(String.valueOf(userInfo.getUser()
        .getAge()) + "歳");

    if (userInfo.getUser().getAbout() != null
        && userInfo.getUser().getAbout().length() > 0) {
      userInfoHolderView.tvAbout.setText(getDefaultText(userInfo
          .getUser().getAbout()));
    } else {
      userInfoHolderView.tvAbout.setText(R.string.profile_ask_me);
    }

    // VOICE call button
    if (userInfo.getUser().isVoiceCallWaiting()) {
      // enable
      userInfoHolderView.mVoiceCallLayout.setClickable(true);
      userInfoHolderView.mPhoneText.setText(R.string.voice_call);
      userInfoHolderView.mPhoneText
          .setTextColor(activity.getResources().getColor(R.color.text_color_selector));
      userInfoHolderView.mPhoneText.setCompoundDrawablesWithIntrinsicBounds(null,
          activity.getResources().getDrawable(R.drawable.ic_communication_call_active), null, null);
      DesignUtils.setTextViewDrawableColor(userInfoHolderView.mPhoneText,
          activity.getResources().getColor(R.color.primary));
    } else {
      // disable
      userInfoHolderView.mPhoneText.setText(R.string.request_voice_call);
      userInfoHolderView.mPhoneText
          .setTextColor(activity.getResources().getColor(R.color.divider_dark));
      userInfoHolderView.mPhoneText.setCompoundDrawablesWithIntrinsicBounds(null,
          activity.getResources().getDrawable(R.drawable.ic_communication_call_inactive), null,
          null);
    }
    userInfoHolderView.mVoiceCallLayout
        .setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View arg0) {
            if (onPanelClickListener != null) {
              CallUserInfo callUserInfo = new CallUserInfo(
                  userInfo.getUser().getUserName(),
                  userInfo.getUser().getUserId(),
                  userInfo.getUser().getAvataId(),
                  userInfo.getUser().getGender());
              onPanelClickListener.phone(callUserInfo);
            }
          }
        });

    // VIDEO call button
    if (userInfo.getUser().isVideoCallWaiting()) {
      // enable
      userInfoHolderView.mVideoCallLayout.setClickable(true);
      userInfoHolderView.mVideoText.setText(R.string.video_call);
      userInfoHolderView.mVideoText
          .setTextColor(activity.getResources().getColor(R.color.text_color_selector));
      userInfoHolderView.mVideoText.setCompoundDrawablesWithIntrinsicBounds(null,
          activity.getResources().getDrawable(R.drawable.ic_av_videocam_active), null, null);
      DesignUtils.setTextViewDrawableColor(userInfoHolderView.mVideoText,
          activity.getResources().getColor(R.color.primary));
    } else {
      // disable
      userInfoHolderView.mVideoText
          .setTextColor(activity.getResources().getColor(R.color.divider_dark));
      userInfoHolderView.mVideoText.setCompoundDrawablesWithIntrinsicBounds(null,
          activity.getResources().getDrawable(R.drawable.ic_av_videocam_inactive), null, null);
      userInfoHolderView.mVideoText.setText(R.string.request_video_call);
    }
    userInfoHolderView.mVideoCallLayout
        .setOnClickListener(new View.OnClickListener() {

          @Override
          public void onClick(View arg0) {
            if (onPanelClickListener != null) {
              CallUserInfo callUserInfo = new CallUserInfo(
                  userInfo.getUser().getUserName(),
                  userInfo.getUser().getUserId(),
                  userInfo.getUser().getAvataId(),
                  userInfo.getUser().getGender());
              onPanelClickListener.video(callUserInfo);
            }
          }
        });

    int jobInd = userInfo.getUser().getJob();
    boolean isMale = userInfo.getUser().getGender() == Constants.GENDER_TYPE_MAN;

    GoogleReviewPreference googleReviewPreference = new GoogleReviewPreference();
    if (googleReviewPreference.isTurnOffUserInfo()) {
      if (isMale) {
        userInfoHolderView.tvHobby.setVisibility(View.VISIBLE);
        userInfoHolderView.lHobby.setVisibility(View.VISIBLE);
        userInfoHolderView.tvAbout.setVisibility(View.VISIBLE);
        userInfoHolderView.lAbout.setVisibility(View.VISIBLE);
      } else {
        userInfoHolderView.tvThreeSizes.setVisibility(View.VISIBLE);
        userInfoHolderView.lThreeSizes.setVisibility(View.VISIBLE);
        userInfoHolderView.tvCupType.setVisibility(View.VISIBLE);
        userInfoHolderView.lCupType.setVisibility(View.VISIBLE);
        userInfoHolderView.tvCupSize.setVisibility(View.VISIBLE);
        userInfoHolderView.lCupSize.setVisibility(View.VISIBLE);
        userInfoHolderView.tvTypeOfMan.setVisibility(View.VISIBLE);
        userInfoHolderView.lTypeOfMan.setVisibility(View.VISIBLE);
        userInfoHolderView.tvFetish.setVisibility(View.VISIBLE);
        userInfoHolderView.lFetish.setVisibility(View.VISIBLE);
        userInfoHolderView.tvJoinHours.setVisibility(View.VISIBLE);
        userInfoHolderView.lJoinHours.setVisibility(View.VISIBLE);
        userInfoHolderView.tvAbout.setVisibility(View.VISIBLE);
        userInfoHolderView.lAbout.setVisibility(View.VISIBLE);
      }
    } else {
      if (isMale) {
        userInfoHolderView.tvHobby.setVisibility(View.GONE);
        userInfoHolderView.lHobby.setVisibility(View.GONE);
        userInfoHolderView.tvAbout.setVisibility(View.GONE);
        userInfoHolderView.lAbout.setVisibility(View.GONE);
      } else {
        userInfoHolderView.tvThreeSizes.setVisibility(View.GONE);
        userInfoHolderView.lThreeSizes.setVisibility(View.GONE);
        userInfoHolderView.tvCupType.setVisibility(View.GONE);
        userInfoHolderView.lCupType.setVisibility(View.GONE);
        userInfoHolderView.tvCupSize.setVisibility(View.GONE);
        userInfoHolderView.lCupSize.setVisibility(View.GONE);
        userInfoHolderView.tvTypeOfMan.setVisibility(View.GONE);
        userInfoHolderView.lTypeOfMan.setVisibility(View.GONE);
        userInfoHolderView.tvFetish.setVisibility(View.GONE);
        userInfoHolderView.lFetish.setVisibility(View.GONE);
        userInfoHolderView.tvJoinHours.setVisibility(View.GONE);
        userInfoHolderView.lJoinHours.setVisibility(View.GONE);
        userInfoHolderView.tvAbout.setVisibility(View.GONE);
        userInfoHolderView.lAbout.setVisibility(View.GONE);
      }
    }

    if (isMale) {
      if (jobInd >= 0 && jobInd < jobMale.length) {
        userInfoHolderView.tvJob
            .setText(getDefaultText(jobMale[jobInd]));
      }
    } else {
      if (jobInd >= 0 && jobInd < jobFemale.length) {
        userInfoHolderView.tvJob
            .setText(getDefaultText(jobFemale[jobInd]));
      }
    }

    if (!isMale) {
      int[] threeSizes = userInfo.getUser().getThreeSizes();
      userInfoHolderView.tvThreeSizes
          .setText(getDefaultText(getTextForThreeSizes(threeSizes)));

      int cupType = userInfo.getUser().getCuteType();
      if (cupType >= 0 && cupType < cubTypes.length) {
        userInfoHolderView.tvCupType
            .setText(getDefaultText(cubTypes[cupType]));
      }

    }

    // check time or timeFormat has case throw nullPointException
    try {
      String timeLogin = Utility.getDistanceTime(activity, userInfo
          .getUser().getLastLoginTime(), Utility.YYYYMMDDHHMMSS);
      userInfoHolderView.tvTimeLogin.setText(timeLogin);
    } catch (ParseException e) {
      userInfoHolderView.tvTimeLogin.setVisibility(View.INVISIBLE);
    }

    // set hobby
    if (isMale) {
      userInfoHolderView.tvHobby.setText(getDefaultText(userInfo
          .getUser().getHobby()));
    }

    // set cup size
    if (!isMale) {
      int cupSize = userInfo.getUser().getCupSize();
      if (cupSize >= 0 && cupSize < cupSizes.length) {
        userInfoHolderView.tvCupSize
            .setText(getDefaultText(cupSizes[cupSize]));
      }
    }

    // set type of man
    if (!isMale) {
      String typeOfMan = userInfo.getUser().getTypeMan();
      userInfoHolderView.tvTypeOfMan.setText(getDefaultText(typeOfMan));
    }

    // set fetish
    if (!isMale) {
      String fetish = userInfo.getUser().getFetish();
      userInfoHolderView.tvFetish.setText(getDefaultText(fetish));
    }

    // set join hour
    if (!isMale) {
      int joinHour = userInfo.getUser().getJoinHours();
      if (joinHour >= 0 && joinHour < joinHours.length) {
        userInfoHolderView.tvJoinHours
            .setText(getDefaultText(joinHours[joinHour]));
      }
    }

    // disable button when view my profile
    String myUserId = UserPreferences.getInstance().getUserId();
    boolean isEnable = !userInfo.getUser().getUserId().equals(myUserId);
    userInfoHolderView.mFavouriteTxt.setEnabled(isEnable);
    userInfoHolderView.mGiveGifTxt.setEnabled(isEnable);
    userInfoHolderView.mOnlineAlertTxt.setEnabled(isEnable);
    userInfoHolderView.mChatTxt.setEnabled(isEnable);
    userInfoHolderView.mVoiceCallLayout.setEnabled(isEnable);
    userInfoHolderView.mVideoCallLayout.setEnabled(isEnable);
    userInfoHolderView.mReportTxt.setEnabled(isEnable);
    userInfoHolderView.mBlockTxt.setEnabled(isEnable);
    userInfoHolderView.mBackStageLayout.setEnabled(isEnable);

    // Set a different text for online alert button when user is set online
    // alert
    if (userInfo.getUser() != null) {
      if (userInfo.getUser().getIsAlt() == ManageOnlineAlertFragment.ALERT_YES) {

        userInfoHolderView.mOnlineAlertTxt
            .setText(R.string.profile_online_alerted);
      } else {
        userInfoHolderView.mOnlineAlertTxt
            .setText(R.string.profile_online_alert);
      }
    }
  }

  private String getTextForThreeSizes(int[] threeSizes) {
    if (threeSizes != null && threeSizes.length == 3) {
      return activity.getResources().getString(
          R.string.profile_reg_three_sizes_b)
          + threeSizes[0]
          + " / "
          + activity.getResources().getString(
          R.string.profile_reg_three_sizes_w)
          + threeSizes[1]
          + " / "
          + activity.getResources().getString(
          R.string.profile_reg_three_sizes_h) + threeSizes[2];
    }
    return "";
  }

  public void updateUser(UserInfo user) {
    objects.clear();
    objects.add(user);
    this.notifyDataSetChanged();
  }

  public void updateBuzz(ArrayList<BuzzListItem> list) {
    for (BuzzListItem itemBuzz : list) {
      int ind = getIndexOfBuzz(itemBuzz.getBuzzId());
      if (ind != -1) {
        objects.set(ind, itemBuzz);
      } else {
        objects.add(itemBuzz);
      }
    }
    notifyNumberBuzzChanged();
    this.notifyDataSetChanged();
  }

  public void updateLikeBuzz(BuzzListItem itemUpdate) {
    BuzzListItem item;
    for (Object object : this.objects) {
      if (object instanceof BuzzListItem) {
        item = (BuzzListItem) object;
        if (item.getBuzzId().equals(itemUpdate.getBuzzId())) {
          int currentLikeType = item.getIsLike();
          int currentLikeNumber = item.getLikeNumber();
          int newLikeType = Constants.BUZZ_LIKE_TYPE_LIKE;
          int newLikeNumber = currentLikeNumber;
          if (currentLikeType == Constants.BUZZ_LIKE_TYPE_LIKE) {
            newLikeType = Constants.BUZZ_LIKE_TYPE_UNLIKE;
            newLikeNumber--;
          } else {
            newLikeNumber++;
          }
          if (newLikeNumber < 0) {
            newLikeNumber = 0;
          }
          item.setIsLike(newLikeType);
          item.setLikeNumber(newLikeNumber);
          break;
        }
      }
    }
    notifyDataSetChanged();
  }

  public void deleteBuzz(BuzzListItem itemUpdate) {
    int buzzIndex = getIndexOfBuzz(itemUpdate.getBuzzId());

    Object object = objects.get(0);
    UserInfoResponse mResponse = ((UserInfo) object).getUser();

    if (buzzIndex != -1) {
      objects.remove(buzzIndex);
    }

    if (itemUpdate.getBuzzType() == Constants.BUZZ_TYPE_IMAGE) {
      if (itemUpdate.getAvatarId() != null) {
        LogUtils.e("ToanTK Item", itemUpdate.getBuzzValue());
        // Remove avatar if Avatar concide itemUpdate.Image
        if (itemUpdate.getAvatarId().equals(itemUpdate.getBuzzValue())) {
          mResponse.setAvataId(null);
          ((UserInfo) object).setUser(mResponse);
          objects.set(0, object);
        }
      }
      mFragment.loadListThumbImage();
    }

    notifyNumberBuzzChanged();
    notifyDataSetChanged();
  }

  public void deleteBuzz(String buzzId) {
    BuzzListItem itemUpdate = getBuzz(buzzId);
    int buzzIndex = getIndexOfBuzz(buzzId);
    // Remove buzz from adapter list
    if (buzzIndex != -1) {
      objects.remove(buzzIndex);
    }

    // Validate data
    if (itemUpdate == null) {
      return;
    }

    // Check for remove image on image list
    if (itemUpdate.getBuzzType() == Constants.BUZZ_TYPE_IMAGE) {
      if (itemUpdate.getAvatarId() != null) {
        // Remove avatar if Avatar concide itemUpdate.Image
        if (itemUpdate.getAvatarId().equals(itemUpdate.getBuzzValue())) {
          Object object = objects.get(0);
          UserInfoResponse mResponse = ((UserInfo) object).getUser();
          mResponse.setAvataId(null);
          ((UserInfo) object).setUser(mResponse);
          objects.set(0, object);
        }
      }
      mFragment.loadListThumbImage();
    }

    notifyNumberBuzzChanged();
    notifyDataSetChanged();
  }

  public void deleteComment(BuzzListItem itemUpdate,
      BuzzListCommentItem itemComent) {
    BuzzListItem item = getBuzz(itemUpdate.getBuzzId());
    item.getCommentList().remove(itemComent);
    int commentNumber = item.getCommentNumber();
    commentNumber--;
    if (commentNumber < 0) {
      commentNumber = 0;
    }
    item.setCommentNumber(commentNumber);
    notifyDataSetChanged();
  }

  public void setThumbImage(ProfilePictureData profilePictureData) {
    if (profilePictureData == null) {
      return;
    }
    listPublicImage = profilePictureData.getListImage();
    notifyDataSetChanged();
  }

  public void setThumbImage(UserInfoHolderView holderView) {
    if (holderView == null || listPublicImage == null) {
      return;
    }

    int numOfImgThumb = MyProfileFragment.NUMBER_OF_IMG_THUMB;
    for (int i = 0; i < numOfImgThumb; i++) {
      holderView.avaThumb[i].setVisibility(View.INVISIBLE);
    }

    String token = UserPreferences.getInstance().getToken();
    int listImgSize = listPublicImage.size();
    if (listImgSize > numOfImgThumb) {
      int numOfHideImg = listImgSize - numOfImgThumb;
      String numImgMore = Integer.toString(numOfHideImg);
      if (numOfHideImg > 20) {
        numImgMore = "20+";
      } else {
        numImgMore = "+" + numImgMore;
      }
      listImgSize = numOfImgThumb;
    }
    for (int i = 0; i < listImgSize; i++) {
      String imgId = listPublicImage.get(i).getImg_id();
      if (!TextUtils.isEmpty(imgId)) {
        final int index = i;
        ImageRequest imageRequestThumb = new ImageRequest(token, imgId,
            ImageRequest.THUMBNAIL);
        ImageUtil.loadAvataImage(activity, imageRequestThumb.toURL(),
            holderView.avaThumb[i], mThumbSize);
        holderView.avaThumb[i].setVisibility(View.VISIBLE);
        holderView.avaThumb[i]
            .setOnClickListener(new OnClickListener() {
              @Override
              public void onClick(View v) {
                if (isButtonEnable) {
                  isButtonEnable = false;
                  onPublicPictureClicked(index);
                }
              }
            });
      }
    }
  }

  private void onPublicPictureClicked(int index) {
    Intent intent = new Intent(activity, DetailPictureProfileActivity.class);
    UserInfoResponse user = new UserInfoResponse(new ResponseData());
    user.setAvataId(mAvataId);
    user.setGender(mGender);
    user.setUserId(mUserId);
    user.setPublicImageNumber(mNumberOfImage);
    String imgId = listPublicImage.get(index).getImg_id();
    Bundle data = ProfilePictureData.parseDataToBundle(index, user, imgId);
    intent.putExtras(data);
    mFragment.startActivity(intent);
  }

  private BuzzListItem getBuzz(String buzzId) {
    BuzzListItem result = null;
    for (Object object : objects) {
      if (object instanceof BuzzListItem) {
        result = (BuzzListItem) object;
        if (result.getBuzzId().equals(buzzId)) {
          return result;
        }
      }
    }
    return null;
  }

  public int getIndexOfBuzz(String buzzId) {
    int ind = 0;
    for (Object object : objects) {
      if (object instanceof BuzzListItem) {
        if (((BuzzListItem) object).getBuzzId().equals(buzzId)) {
          return ind;
        }
      }
      ind++;
    }
    return -1;
  }

  public BuzzListItem getBuzzListItem(int position) {
    Object item = objects.get(position);
    if (item != null) {
      if (item instanceof BuzzListItem) {
        return (BuzzListItem) item;
      }
    }
    return null;
  }

  public int getNumberBuzzList() {
    int count = 0;
    for (Object item : objects) {
      if (item instanceof BuzzListItem) {
        count++;
      }
    }
    return count;
  }

  public int getTabSelected() {
    return mTabSelected;
  }

  public void setTabSelected(int tabSelected) {
    this.mTabSelected = tabSelected;
  }

  private void notifyTabChanged(int tabSelected) {
    mTabSelected = tabSelected;
    if (tabSelected != BUZZ_TAB) {
      mFragment.disablePullUpToRefresh();
      mFragment.removeBuzzEmpty();
    } else {
      mFragment.enablePullUpToRefresh();
      notifyNumberBuzzChanged();
    }
    notifyDataSetChanged();
  }

  private void notifyNumberBuzzChanged() {
    if (mTabSelected != BUZZ_TAB) {
      return;
    }
    if (objects.size() >= 2) {
      mFragment.removeBuzzEmpty();
    } else {
      mFragment.addBuzzEmpty();
    }
  }

  private String getDefaultText(String text) {
    if (TextUtils.isEmpty(text)) {
      return activity.getString(R.string.profile_ask_me);
    }
    return text;
  }

  public void replaceBuzzItem(String replaceBuzzID, BuzzListItem newItem) {
    for (int i = 0; i < objects.size(); i++) {
      Object object = objects.get(i);
      if (object instanceof BuzzListItem) {
        if (((BuzzListItem) object).getBuzzId().equals(replaceBuzzID)) {
          objects.set(i, newItem);
          this.notifyDataSetChanged();
          return;
        }
      }
    }
  }

  public interface OnButtonFriendClickListener {

    public void OnButtonFriendClick();

    public void OnButtonUserFriendsClick(String userId, String userName);

  }

  public interface OnViewAllBuzzClickListener {

    public void OnViewAllBuzz();
  }

  public interface OnSlideProfileClickListener {

    public void OnNextProfileClick();

    public void OnBackProfileClick();
  }

  public interface OnPanelClickListener {

    public void onFavourite();

    public void chat();

    public void phone(CallUserInfo userInfo);

    public void video(CallUserInfo userInfo);

    public void onGiveGif();

    public void onSetOnlineAlert();

    public void onReport();

    public void onBlock();

  }

  private class UserInfoHolderView {

    public ImageView ivProfile;
    public ImageView[] avaThumb = new ImageView[4];
    public TextView tvStatus, tvName, tvAge, tvRegion, tvAbout, tvJob,
        tvThreeSizes, tvCupType, tvCupSize, tvTypeOfMan, tvFetish,
        tvJoinHours, tvHobby;
    public View lAbout, lThreeSizes, lCupType, lCupSize, lTypeOfMan,
        lFetish, lJoinHours, lHobby;
    public TextView userInfo, userBuzz;
    public TextView mFavouriteTxt;
    public TextView mGiveGifTxt;
    public TextView mOnlineAlertTxt;
    public View mBackStageLayout;
    public TextView mChatTxt;
    public View mRootLayout;
    public View mViewTabProfileSelected;
    public View mViewTabBuzzSelected;
    public View mVoiceCallLayout;
    public TextView mPhoneText;
    public View mVideoCallLayout;
    public TextView mVideoText;
    public TextView tvTimeLogin;
    public View mReportTxt;
    public View mBlockTxt;
    public BadgeTextView mBackstageNumber;
  }

}
