package com.application.ui.account;

import static com.application.navigationmanager.NavigationManager.getRootParentFragment;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.application.connection.ResponseReceiver;
import com.application.connection.request.CircleImageRequest;
import com.application.connection.request.UploadImageRequest;
import com.application.connection.request.UserInfoRequest;
import com.application.connection.request.UserInfoUpdateRequest;
import com.application.connection.response.UploadImageResponse;
import com.application.connection.response.UserInfoResponse;
import com.application.connection.response.UserInfoUpdateRespone;
import com.application.constant.Constants;
import com.application.constant.UserSetting;
import com.application.entity.User;
import com.application.entity.UserProfileInfo;
import com.application.imageloader.ImageFetcher;
import com.application.imageloader.ImageUploader;
import com.application.imageloader.ImageUploader.UploadImageProgress;
import com.application.imageloader.ImageWorker.ImageListener;
import com.application.service.DataFetcherService;
import com.application.ui.BaseFragment;
import com.application.ui.CenterButtonDialogBuilder;
import com.application.ui.MainActivity;
import com.application.ui.customeview.AlertDialog;
import com.application.ui.customeview.CircleImageView;
import com.application.ui.customeview.CustomConfirmDialog;
import com.application.ui.customeview.CustomDatePickerDialog;
import com.application.ui.customeview.ErrorApiDialog;
import com.application.ui.profile.MyProfileFragment;
import com.application.ui.region.RegionSettingFragment;
import com.application.util.ImageUtil;
import com.application.util.LocationUtils.Region;
import com.application.util.LogUtils;
import com.application.util.ProfileUtil;
import com.application.util.RegionUtils;
import com.application.util.RoundedAvatarDrawable;
import com.application.util.Utility;
import com.application.util.preferece.GoogleReviewPreference;
import com.application.util.preferece.NewsPreference;
import com.application.util.preferece.UserPreferences;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.tux.mylab.MediaPickerBaseActivity;
import com.example.tux.mylab.camera.Camera;
import com.example.tux.mylab.gallery.Gallery;
import com.example.tux.mylab.gallery.data.MediaFile;
import com.michaelnovakjr.numberpicker.NumberPickerDialog;
import com.michaelnovakjr.numberpicker.SHOWMODE;
import glas.bbsystem.R;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import org.linphone.LinphoneService;

public class EditProfileFragment extends BaseFragment implements OnClickListener, ResponseReceiver {

  public static final String TAG = "EditProfileFragment";
  public static final int LOADER_ID_UPDATE_INFO = 1;
  public static final int LOADER_ID_GET_USER_INFO = 2;
  public static final int FIELD_FETISH = 100;
  public static final int FIELD_HOBBY = 101;
  public static final int FIELD_MESSAGE = 102;
  public static final int FIELD_TYPE_MAN = 103;
  protected static final String USER_INFO_RESPONSE = "user_info_response";
  protected static final String USER_INFO_ME = "user_info_me";
  protected static final String USER_PROFILE_INFO = "user_profile_info";
  protected static final String USER_PROFILE_IMAGE = "user_profile_image";
  protected static final String USER_PROFILE_COLORS = "user_profile_fields_color";
  protected static final String FROM_MY_PAGE_FRAGMENT = "from_my_page_fragment";
  protected static final String MEASUREMENT = "measurement";
  protected static final String KEY_DATA = "key_data";
  protected static final String KEY_FIELD = "key_field";
  private static final int REQUEST_REGION = 0;
  private static final int REQUEST_THREE_SIZES = 1;
  private static final int REQUEST_TEXT = 2;
  private static final int REQUEST_IMAGE = 202;
  private ProgressDialog mDialog;
  private ImageView mImgAvatar;
  private CircleImageView mImgCircleAvatar;
  private TableRow mTbrName;
  private TableRow mTbrAge;
  private TableRow mTbrJob;
  private TableRow mTbrRegion;
  private TableRow mTbrThreeSizes;
  private TableRow mTbrCupSize;
  private TableRow mTbrCuteType;
  private LinearLayout mTbrTypeMan;
  private TableRow mTbrFetish;
  private LinearLayout mTbrHobby;
  private TableRow mTbrJoinHours;
  private LinearLayout mTbrMessage;
  private TextView mTxtMessageTitle;
  private EditText mEdtName;
  private TextView mTxtTypeMan;
  private TextView mTxtFetish;
  private TextView mTxtMessage;
  private TextView mTxtHobby;
  private TextView mTxtAge;
  private TextView mTxtJob;
  private TextView mTxtRegion;
  private TextView mTxtThreeSize;
  private TextView mTxtCupSize;
  private TextView mTxtCuteType;
  private TextView mTxtJoinHours;
  private TextView mTxtPleazFillRequired;
  private TextView mTxtIndicatorName;
  private TextView mTxtIndicatorRegion;
  private TextView mTxtChooseImage;
  private int finishRegisterFlag = Constants.FINISH_REGISTER_NO;
  private UserProfileInfo profileInfo;
  private String userName = "";
  private RegionUtils mRegionUtils;
  private String mImagePath;
  private int colorRegion = R.color.color_hint_bold;
  private int secondaryTextColor = R.color.color_hint_bold;
  private boolean dialogPleazeFillProfileShowed = false;
  private boolean isFromMyPageFragment = false;
  private UploadImageProgress uploadImageProgress = new UploadImageProgress() {
    @Override
    public void uploadImageSuccess(UploadImageResponse response) {
      LogUtils.d(TAG, "uploadImageSuccess: " + response.getCode());
      UserPreferences preferences = UserPreferences.getInstance();
      if (response.getIsApproved() == Constants.IS_APPROVED) {
        String avataId = response.getImgId();
        preferences.saveAvaId(avataId);

        if (getActivity() instanceof MainActivity) {
          showDialogAskGotoProfileWhenEditSuccessfully();
        } else {
          goToMainActivity();
        }
      } else {
        Resources resources = getResources();
        String title = resources
            .getString(R.string.unapproved_image_dialog_title);
        String message = resources
            .getString(R.string.unapproved_image_dialog_content);

        // Create and show dialog
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View customTitle = inflater.inflate(R.layout.dialog_customize, null);

        Builder builder = new CenterButtonDialogBuilder(getActivity(), false);
        ((TextView) customTitle.findViewById(R.id.tv_title_dialog_customize)).setText(title);
        builder.setCustomTitle(customTitle);

        //builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.ok,
            new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog,
                  int which) {
                if (getActivity() instanceof MainActivity) {
                  // mNavigationManager.goBack();
                  showDialogAskGotoProfileWhenEditSuccessfully();
                } else {
                  goToMainActivity();
                }
              }
            });
        android.app.AlertDialog element = builder.show();

        int dividerId = element.getContext().getResources()
            .getIdentifier("android:id/titleDivider", null, null);
        View divider = element.findViewById(dividerId);
        if (divider != null) {
          divider.setBackgroundColor(getResources().getColor(R.color.transparent));
        }

        String pendingAvata = response.getImgId();
        preferences.savePendingAva(pendingAvata);
      }

      if (mDialog != null && mDialog.isShowing()) {
        mDialog.dismiss();
      }

      if (mActionBar != null) {
        mActionBar.enableEditRightButton();
      }
    }

    @Override
    public void uploadImageStart() {

    }

    @Override
    public void uploadImageFail(int code) {
      LogUtils.d(TAG, "uploadImageFail --- " + code);

      if (mDialog != null && mDialog.isShowing()) {
        mDialog.dismiss();
      }

      if (code == Response.SERVER_UPLOAD_IMAGE_ERROR) {
        if (getActivity() == null) {
          return;
        }
        AlertDialog.showUploadImageErrorAlert(getActivity());
      } else if (getActivity() instanceof MainActivity) {
        showDialogAskGotoProfileWhenEditSuccessfully();
      } else {
        goToMainActivity();
      }

      if (mActionBar != null) {
        mActionBar.enableEditRightButton();
      }
    }
  };
  private android.app.AlertDialog mDialogNameTooLong;
  private TextWatcher nameWatcher = new TextWatcher() {
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
      if (profileInfo != null) {
        try {
          if (s.toString().getBytes("Shift_JIS").length > Constants.MAX_LENGTH_NAME_IN_HALF_SIZE) {
            mEdtName.setText(s.subSequence(0, s.length() - 1));
            showDialogNameTooLong();
          } else {
            profileInfo.setName(s.toString());
          }
        } catch (UnsupportedEncodingException e) {
          profileInfo.setName(s.toString());
          e.printStackTrace();
        }
      }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
        int after) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }
  };

  public static EditProfileFragment newInstance() {
    EditProfileFragment fragment = new EditProfileFragment();
    return fragment;
  }

  public static EditProfileFragment newInstance(User me) {
    EditProfileFragment fragment = new EditProfileFragment();
    Bundle bundle = new Bundle();
    bundle.putSerializable(USER_INFO_ME, me);
    fragment.setArguments(bundle);
    return fragment;
  }

  public static EditProfileFragment newInstance(UserInfoResponse userInfo) {
    EditProfileFragment fragment = new EditProfileFragment();
    Bundle bundle = new Bundle();
    bundle.putSerializable(USER_INFO_RESPONSE, userInfo);
    fragment.setArguments(bundle);
    return fragment;
  }

  public static EditProfileFragment newInstance(UserInfoResponse userInfo,
      boolean fromMyPageFragment) {
    EditProfileFragment fragment = new EditProfileFragment();
    Bundle bundle = new Bundle();
    bundle.putSerializable(USER_INFO_RESPONSE, userInfo);
    bundle.putBoolean(FROM_MY_PAGE_FRAGMENT, fromMyPageFragment);
    fragment.setArguments(bundle);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    requestDirtyWord();
    secondaryTextColor = getResources().getColor(R.color.color_hint_bold);
    colorRegion = secondaryTextColor;
    getActivity().getWindow().setSoftInputMode(
        WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_edit_profile, container,
        false);
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    initViews(view);
    Utility.hideKeyboard(getActivity(), view);
    if (savedInstanceState != null) {
      profileInfo = savedInstanceState.getParcelable(USER_PROFILE_INFO);
      mImagePath = savedInstanceState.getString(USER_PROFILE_IMAGE);
      int colors[] = savedInstanceState.getIntArray(USER_PROFILE_COLORS);
      if (colors != null && colors.length >= 1) {
        colorRegion = colors[0];
      }
      isFromMyPageFragment = savedInstanceState.getBoolean(FROM_MY_PAGE_FRAGMENT);
    } else {
      if (profileInfo == null) {
        profileInfo = new UserProfileInfo();
        Bundle bundle = getArguments();

        if (bundle != null) {
          isFromMyPageFragment = bundle.getBoolean(FROM_MY_PAGE_FRAGMENT);
          Object object = bundle.getSerializable(USER_INFO_ME);
          if (object instanceof User) {
            User user = (User) object;
            if (user != null) {
              profileInfo.setName(user.getName());
              profileInfo.setGender(user.getGender());

              SimpleDateFormat format = new SimpleDateFormat(
                  Constants.DATE_FORMAT_SEND_TO_SERVER,
                  Locale.getDefault());
              Date date = user.getBirthday();
              if (date == null) {
                date = new Date();
              }
              String birthday = format.format(date);
              profileInfo.setBirthday(birthday);
            }
          }

          object = (UserInfoResponse) bundle
              .getSerializable(USER_INFO_RESPONSE);
          if (object instanceof UserInfoResponse) {
            UserInfoResponse userInfoResponse = (UserInfoResponse) object;
            if (userInfoResponse != null) {
              profileInfo.updateUserInfo(userInfoResponse);
            }
          }
        } else {
          UserPreferences preferences = UserPreferences.getInstance();
          // Token
          String token = preferences.getToken();
          if (token != null) {
            UserInfoRequest request = new UserInfoRequest(token);
            requestServer(LOADER_ID_GET_USER_INFO, request);
          }

          // Gender
          profileInfo.setGender(preferences.getGender());
        }
      }
    }
    filterFieldsByGender();
    setTextIndicatorRequiredFields();
  }

  /**
   * Notify data service to load list dirty word
   */
  private void requestDirtyWord() {
    Activity activity = getActivity();
    if (activity != null) {
      String token = UserPreferences.getInstance().getToken();
      DataFetcherService.startCheckSticker(activity, token);
    }
  }

  @Override
  public void onStop() {
    super.onStop();
    if (mDialog != null && mDialog.isShowing()) {
      mDialog.dismiss();
    }
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putParcelable(USER_PROFILE_INFO, profileInfo);
    outState.putString(USER_PROFILE_IMAGE, mImagePath);
    outState.putIntArray(USER_PROFILE_COLORS, new int[]{colorRegion});
    LogUtils.i(TAG, "onSaveInstanceState --- put profileInfo to bundle");
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    if (!dialogPleazeFillProfileShowed) {
      showDialogPleaseFillProfile();
    }
    filterFieldsByFinishRegister();
    fillDataForUI();
  }

  private void initViews(View view) {
    // Table row
    mTbrName = (TableRow) view.findViewById(R.id.tbr_name);
    mTbrAge = (TableRow) view.findViewById(R.id.tbr_age);
    mTbrJob = (TableRow) view.findViewById(R.id.tbr_job);
    mTbrHobby = (LinearLayout) view.findViewById(R.id.tbr_hobby);
    mTbrRegion = (TableRow) view.findViewById(R.id.tbr_region);

    mTbrThreeSizes = (TableRow) view.findViewById(R.id.tbr_three_sizes);
    mTbrCupSize = (TableRow) view.findViewById(R.id.tbr_cup_size);
    mTbrCuteType = (TableRow) view.findViewById(R.id.tbr_cute_type);
    mTbrTypeMan = (LinearLayout) view.findViewById(R.id.tbr_type_man);
    mTbrFetish = (TableRow) view.findViewById(R.id.tbr_fetish);

    mTbrJoinHours = (TableRow) view.findViewById(R.id.tbr_join_hours);
    mTbrMessage = (LinearLayout) view.findViewById(R.id.tbr_message);

    // Text view and edit text
    mEdtName = (EditText) view.findViewById(R.id.edt_name);
    mTxtChooseImage = (TextView) view.findViewById(R.id.txt_choose_image);
    mTxtFetish = (TextView) view.findViewById(R.id.txt_fetish);
    mTxtTypeMan = (TextView) view.findViewById(R.id.txt_type_man);
    mTxtHobby = (TextView) view.findViewById(R.id.txt_hobby);

    mTxtMessage = (TextView) view.findViewById(R.id.txt_message);
    mTxtAge = (TextView) view.findViewById(R.id.txt_age);
    mTxtCupSize = (TextView) view.findViewById(R.id.txt_cup_size);
    mTxtCuteType = (TextView) view.findViewById(R.id.txt_cute_type);
    mTxtJob = (TextView) view.findViewById(R.id.txt_job);

    mTxtJoinHours = (TextView) view.findViewById(R.id.txt_join_hours);
    mTxtMessageTitle = (TextView) view.findViewById(R.id.txt_message_title);
    mTxtRegion = (TextView) view.findViewById(R.id.txt_region);
    mTxtThreeSize = (TextView) view.findViewById(R.id.txt_three_sizes);
    mImgAvatar = (ImageView) view.findViewById(R.id.dummy_avata);

    mTxtPleazFillRequired = (TextView) view
        .findViewById(R.id.txt_fill_required_fields);
    mTxtIndicatorName = (TextView) view
        .findViewById(R.id.txt_indicator_name);
    mTxtIndicatorRegion = (TextView) view
        .findViewById(R.id.txt_indicator_region);

    // Avatar image
    mImgCircleAvatar = (CircleImageView) view
        .findViewById(R.id.image_circle_avatar);

    // Initial listener
    mTbrAge.setOnClickListener(this);
    mTbrHobby.setOnClickListener(this);
    mTbrCupSize.setOnClickListener(this);
    mTbrCuteType.setOnClickListener(this);
    mTbrFetish.setOnClickListener(this);

    mTbrJob.setOnClickListener(this);
    mTbrJoinHours.setOnClickListener(this);
    mTbrMessage.setOnClickListener(this);
    mTbrName.setOnClickListener(this);
    mTbrRegion.setOnClickListener(this);

    mTbrThreeSizes.setOnClickListener(this);
    mTbrTypeMan.setOnClickListener(this);
    mImgAvatar.setOnClickListener(this);
    mImgCircleAvatar.setOnClickListener(this);
    mEdtName.addTextChangedListener(nameWatcher);

    mDialog = new ProgressDialog(getActivity());
    mDialog.setMessage(getString(R.string.waiting));
    mDialog.setCancelable(false);
    mDialog.setCanceledOnTouchOutside(false);
  }

  private void filterFieldsByGender() {
    int gender = profileInfo.getGender();
    GoogleReviewPreference googleReviewPreference = new GoogleReviewPreference();
    if (gender == UserSetting.GENDER_MALE) {
      mTbrThreeSizes.setVisibility(View.GONE);
      mTbrCupSize.setVisibility(View.GONE);
      mTbrCuteType.setVisibility(View.GONE);
      mTbrTypeMan.setVisibility(View.GONE);
      mTbrFetish.setVisibility(View.GONE);
      mTbrJoinHours.setVisibility(View.GONE);

      if (googleReviewPreference.isTurnOffUserInfo()) {
        mTbrHobby.setVisibility(View.VISIBLE);
        mTxtMessage.setVisibility(View.VISIBLE);
        mTxtMessageTitle.setVisibility(View.VISIBLE);
        mTxtMessageTitle.setText(R.string.profile_reg_message_male);
      } else {
        mTbrHobby.setVisibility(View.GONE);
        mTxtMessage.setVisibility(View.GONE);
        mTxtMessageTitle.setVisibility(View.GONE);
      }
    } else if (gender == UserSetting.GENDER_FEMALE) {
      mTbrHobby.setVisibility(View.GONE);

      if (googleReviewPreference.isTurnOffUserInfo()) {
        mTbrThreeSizes.setVisibility(View.VISIBLE);
        mTbrCupSize.setVisibility(View.VISIBLE);
        mTbrCuteType.setVisibility(View.VISIBLE);
        mTbrTypeMan.setVisibility(View.VISIBLE);
        mTbrFetish.setVisibility(View.VISIBLE);
        mTbrJoinHours.setVisibility(View.VISIBLE);

        mTxtMessage.setVisibility(View.VISIBLE);
        mTxtMessageTitle.setVisibility(View.VISIBLE);
        mTxtMessageTitle.setText(R.string.profile_reg_message_female);
      } else {
        mTbrThreeSizes.setVisibility(View.GONE);
        mTbrCupSize.setVisibility(View.GONE);
        mTbrCuteType.setVisibility(View.GONE);
        mTbrTypeMan.setVisibility(View.GONE);
        mTbrFetish.setVisibility(View.GONE);
        mTbrJoinHours.setVisibility(View.GONE);
        mTxtMessage.setVisibility(View.GONE);
        mTxtMessageTitle.setVisibility(View.GONE);
      }
    }
  }

  private void filterFieldsByFinishRegister() {
    UserPreferences preferences = UserPreferences.getInstance();
    if (preferences.getFinishRegister() == Constants.FINISH_REGISTER_NO) {
      mTbrAge.setVisibility(View.GONE);
    }
  }

  private int getGender() {
    return profileInfo.getGender();
  }

  private void fillDataForUI() {
    UserPreferences preferences = UserPreferences.getInstance();
    String avaId = preferences.getAvaId();
    String fbAvatar = preferences.getFacebookAvatar();

    if (preferences.getFinishRegister() == Constants.FINISH_REGISTER_YES) {
      mTxtChooseImage.setVisibility(View.GONE);
    }

    if (TextUtils.isEmpty(mImagePath)) {
      if (!TextUtils.isEmpty(avaId)) {
        mImgAvatar.setVisibility(View.GONE);
        mImgCircleAvatar.setVisibility(View.VISIBLE);

        UserPreferences userPreferences = UserPreferences.getInstance();
        CircleImageRequest imageRequest = new CircleImageRequest(
            userPreferences.getToken(), avaId);
        int gender = preferences.getGender();
        ImageFetcher imageFetcher = getImageFetcher();
        imageFetcher.loadImageByGender(imageRequest, mImgCircleAvatar,
            mImgCircleAvatar.getWidth(), gender,
            new ImageListener() {
              @Override
              public void onGetImageSuccess(Bitmap bitmap) {
                mImgAvatar.setVisibility(View.GONE);
                mImgCircleAvatar.setVisibility(View.VISIBLE);
                mImgCircleAvatar.setImageBitmap(bitmap);
              }

              @Override
              public void onGetImageFailure() {
              }
            });

      } else if (!TextUtils.isEmpty(fbAvatar)) {
        if (!(getActivity() instanceof MainActivity)) {
          mImgCircleAvatar.setVisibility(View.GONE);
          Glide.with(this)
              .load(fbAvatar)
              .apply(RequestOptions.circleCropTransform())
              .into(mImgAvatar);
        }
      }

    } else {
      Bitmap bitmap = BitmapFactory.decodeFile(mImagePath);
      // the resulting decoded bitmap, or null if it could not be decoded.  
      if(bitmap !=null) {
        RoundedAvatarDrawable drawable = new RoundedAvatarDrawable(bitmap);
        mImgAvatar.setImageDrawable(drawable);
      }
      mImgCircleAvatar.setVisibility(View.GONE);
    }

    if (profileInfo != null) {
      // Set name
      mEdtName.setText(profileInfo.getName());

      // Set age. Note: field age only appears in the second and later
      // time edit profile.
      if (getActivity() instanceof MainActivity) {
        String birtString = profileInfo.getBirthday();
        SimpleDateFormat format = new SimpleDateFormat(
            Constants.DATE_FORMAT_SEND_TO_SERVER,
            Locale.getDefault());
        Date date = null;
        try {
          date = format.parse(birtString);
        } catch (ParseException e) {
          e.printStackTrace();
        }
        setTextForAge(date);
      }

      // Set type man
      if (!TextUtils.isEmpty(profileInfo.getTypeMan())) {
        mTxtTypeMan.setText(profileInfo.getTypeMan());
      } else {
        mTxtTypeMan.setText(R.string.profile_reg_fill_free);
      }

      // Set fetish
      if (!TextUtils.isEmpty(profileInfo.getFetish())) {
        mTxtFetish.setText(profileInfo.getFetish());
      } else {
        mTxtFetish.setText(R.string.profile_reg_fill_free);
      }

      // Set about(message with male, self-intro with female)
      if (!TextUtils.isEmpty(profileInfo.getMessage())) {
        mTxtMessage.setText(profileInfo.getMessage());
      } else {
        mTxtMessage.setText(R.string.profile_reg_fill_free);
      }

      // Set hobby (only male)
      if (!TextUtils.isEmpty(profileInfo.getHobby())) {
        mTxtHobby.setText(profileInfo.getHobby());
      } else {
        mTxtHobby.setText(R.string.profile_reg_fill_free);
      }
      mTxtHobby.setTextColor(secondaryTextColor);

      // Set job
      int job = profileInfo.getJob();
      int gender = getGender();

      if (gender == UserSetting.GENDER_MALE) {
        String[] maleJob = getResources().getStringArray(
            R.array.job_male);
        if (0 <= job && job < maleJob.length) {
          mTxtJob.setText(maleJob[job]);
        } else {
          mTxtJob.setText(R.string.profile_title_ask_me);
        }

      } else if (gender == UserSetting.GENDER_FEMALE) {
        String[] femaleJob = getResources().getStringArray(
            R.array.job_female);
        if (0 <= job && job < femaleJob.length) {
          mTxtJob.setText(femaleJob[job]);
        } else {
          mTxtJob.setText(R.string.profile_title_ask_me);
        }
      }

      // Set cup size
      int cupSize = profileInfo.getCupSize();
      int[] listCupsSize = ProfileUtil.getCupSize();
      if (cupSize > -1 && cupSize < listCupsSize.length) {
        mTxtCupSize.setText(listCupsSize[cupSize]);
      } else {
        mTxtCupSize.setText(R.string.profile_title_ask_me);
      }

      // Set cute type
      int cuteType = profileInfo.getCuteType();
      int[] listCupType = ProfileUtil.getCuteType();
      if (cuteType > -1 && cuteType < listCupType.length) {
        mTxtCuteType.setText(listCupType[cuteType]);
      } else {
        mTxtCuteType.setText(R.string.profile_title_ask_me);
      }

      // Set join hours.
      int joinHours = profileInfo.getJoinHours();
      int[] listJoinHour = ProfileUtil.getJoinHour();
      if (joinHours > -1 && joinHours < listJoinHour.length) {
        mTxtJoinHours.setText(listJoinHour[joinHours]);
      } else {
        mTxtJoinHours.setText(R.string.profile_title_ask_me);
      }

      // Set three sizes
      int[] threeSizes = profileInfo.getThreeSizes();
      setTextForThreeSizes(threeSizes);

      // Set region
      setTextForRegion(profileInfo.getRegion());
    }
  }

  private void setTextForThreeSizes(int[] threeSizes) {
    if (threeSizes != null && threeSizes.length == 3) {
      mTxtThreeSize.setText(getString(R.string.profile_reg_three_sizes_b)
          + threeSizes[0] + "/"
          + getString(R.string.profile_reg_three_sizes_w)
          + threeSizes[1] + "/"
          + getString(R.string.profile_reg_three_sizes_h)
          + threeSizes[2]);
    } else {
      mTxtThreeSize.setText(R.string.profile_title_ask_me);
    }
  }

  private void setTextForAge(Date date) {
    try {
      SimpleDateFormat dateFormat = new SimpleDateFormat(
          Constants.DATE_FORMAT_DISPLAY, Locale.getDefault());
      String d = dateFormat.format(date);
      mTxtAge.setText(d + " (" + Utility.getAge(date) + " "
          + getString(R.string.profile_reg_years_old) + ")");
      mTxtAge.setTextColor(secondaryTextColor);

    } catch (NullPointerException npe) {
    } catch (IllegalArgumentException iae) {
    }
  }

  private void setTextForRegion(int regionCode) {
    if (mRegionUtils == null) {
      mRegionUtils = new RegionUtils(mAppContext);
    }

    String regionName = mRegionUtils.getRegionName(regionCode);
    if (TextUtils.isEmpty(regionName)) {
      mTxtRegion.setText(R.string.profile_title_ask_me);
    } else {
      mTxtRegion.setText(regionName);
    }

    if (colorRegion == Color.RED) {
      mTxtRegion.setText(R.string.profile_reg_requied);
    }
    mTxtRegion.setTextColor(colorRegion);
  }

  private boolean isNameValid() {
    if (TextUtils.isEmpty(mEdtName.getText().toString()
        .replace("\u3000", " ").trim())) {
      ErrorApiDialog.showAlert(getActivity(),
          getString(R.string.edit_my_profile_title),
          getString(R.string.name_is_empty));

      mEdtName.setText("");
      return false;
    }

    if (Utility.isContainDirtyWord(getActivity(), mEdtName)) {
      return false;
    }
    return true;
  }

  private boolean isAboutValid() {
    String msg = profileInfo.getMessage();
    if (msg == null) {
      return true;
    }
    if (Utility.isContainDirtyWord(getActivity(), msg)) {
      return false;
    }
    return true;
  }

  private boolean isFetishValid() {
    String msg = profileInfo.getFetish();
    if (msg == null) {
      return true;
    }
    if (Utility.isContainDirtyWord(getActivity(), msg)) {
      return false;
    }
    return true;
  }

  private boolean isTypeOfManValid() {
    String msg = profileInfo.getTypeMan();
    if (msg == null) {
      return true;
    }
    if (Utility.isContainDirtyWord(getActivity(), msg)) {
      return false;
    }
    return true;
  }

  private boolean isRegionValid() {
    if (profileInfo == null || profileInfo.getRegion() < 0) {
      colorRegion = Color.RED;
      mTxtRegion.setText(R.string.profile_reg_requied);
      mTxtRegion.setTextColor(colorRegion);
      return false;
    }
    return true;
  }

  //TODO Doi text messsage cua dialog thong bao review thong tin nguoi dung
  private void showDialogReviewProfileInfo(final UserInfoUpdateRespone response) {
    android.app.AlertDialog mDialog = new CustomConfirmDialog(getActivity(), null,
        getString(R.string.profile_update_dialog_review), false)
        .setPositiveButton(0, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            validateDataUserInfoUpdate(response);
          }
        })
        .create();
    mDialog.setCanceledOnTouchOutside(false);
    mDialog.setCancelable(false);
    mDialog.show();

    int dividerId = mDialog.getContext().getResources()
        .getIdentifier("android:id/titleDivider", null, null);
    View divider = mDialog.findViewById(dividerId);
    if (divider != null) {
      divider.setBackgroundColor(mDialog.getContext().getResources().getColor(R.color.transparent));
    }
  }

  private void validateDataUserInfoUpdate(UserInfoUpdateRespone response) {
    // Validate data response
    UserPreferences userPreferences = UserPreferences.getInstance();
    userPreferences
        .saveFinishRegister(response.getFinishRegisterFlag());
    userPreferences.saveAgeVerification(response.getVerificationFlag());
    FragmentActivity activity = getActivity();
    // If that is main activity, it not register process
    if (activity instanceof MainActivity) {
      userPreferences.saveUserName(userName);
    } else {
      finishRegisterFlag = response.getFinishRegisterFlag();
      userPreferences.saveUserName(userName);

      LinphoneService.startLogin(mAppContext);
    }
    uploadAvatarToServer();
  }

  //TODO Khi Cap nhat profile thi show dialog thong bao review thong tin cua user
  public void editProfile() {
    // Hide soft keyboard
    Utility.hideSoftKeyboard(getActivity());

    // Show profile information
    if (profileInfo != null) {
      // Validate data
      if (isNameValid() && isRegionValid() && isAboutValid()
          && isFetishValid() && isTypeOfManValid()) {
        // Disable edit button for abandon duplicate click
        if (mActionBar != null) {
          mActionBar.disableEditRightButton();
        }

        userName = profileInfo.getName();
        callAPIUpdateUserInfo();
      }
    }
  }

  private void callAPIUpdateUserInfo() {
    // Create a update user information request builder
    UserInfoUpdateRequest.Builder builder = new UserInfoUpdateRequest.Builder();

    // Get token
    String token = UserPreferences.getInstance().getToken();
    builder.setToken(token);

    // Get gender
    int gender = getGender();
    if (gender == UserSetting.GENDER_MALE) {
      int job = profileInfo.getJob();
      if (job > -1) {
        job += UserSetting.NUMBER_JOBS_FEMALE;
      }
      builder.setJob(job);
    } else {
      builder.setJob(profileInfo.getJob());
    }

    // Only appears birthday on Main activity
    if (getActivity() instanceof MainActivity) {
      builder.setBirthday(profileInfo.getBirthday());
    }

    builder.setUserName(profileInfo.getName().replace("\u3000", " ").trim());
    builder.setRegion(profileInfo.getRegion());
    builder.setThreeSizes(profileInfo.getThreeSizes());
    builder.setCupSize(profileInfo.getCupSize());
    builder.setCuteType(profileInfo.getCuteType());
    builder.setTypeMan(profileInfo.getTypeMan());
    builder.setFetish(profileInfo.getFetish());
    builder.setJoinHours(profileInfo.getJoinHours());

    builder.setHobby(profileInfo.getHobby());
    builder.setAbout(profileInfo.getMessage());

    UserInfoUpdateRequest request = new UserInfoUpdateRequest(builder);
    restartRequestServer(LOADER_ID_UPDATE_INFO, request);
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.tbr_job:
        chooseJob();
        break;

      case R.id.tbr_cup_size:
        chooseCupSize();
        break;

      case R.id.tbr_cute_type:
        chooseCuteType();
        break;

      case R.id.tbr_join_hours:
        chooseJoinHours();
        break;

      case R.id.tbr_age:
        chooseBirthday();
        break;

      case R.id.tbr_three_sizes:
        showThreeSizesPiker();
        break;

      case R.id.tbr_region:
        int region = Region.REGION_NOT_SET;
        if (profileInfo != null) {
          region = profileInfo.getRegion();
        }
        RegionSettingFragment regionSettingFragment = RegionSettingFragment
            .newInstance(region, UserPreferences.getInstance()
                .isAutoDetectRegion());
        regionSettingFragment.setTargetFragment(getRootParentFragment(this), REQUEST_REGION);
        mNavigationManager.addPage(regionSettingFragment);
        break;

      case R.id.dummy_avata:
      case R.id.image_circle_avatar:
        pickImageCapture();
        break;

      case R.id.tbr_fetish:
        addProfileTextFragment(FIELD_FETISH, getGender());
        break;

      case R.id.tbr_hobby:
        addProfileTextFragment(FIELD_HOBBY, getGender());
        break;

      case R.id.tbr_message:
        addProfileTextFragment(FIELD_MESSAGE, getGender());
        break;

      case R.id.tbr_type_man:
        addProfileTextFragment(FIELD_TYPE_MAN, getGender());
        break;

      default:
        break;
    }
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode == Activity.RESULT_OK) {
      if (requestCode == REQUEST_REGION) {

        if (data != null) {
          int regionSelected = data.getIntExtra(
              RegionSettingFragment.EXTRA_REGION_SELECTED,
              Region.REGION_NOT_SET);
          if (profileInfo != null
              && regionSelected != Region.REGION_NOT_SET) {
            profileInfo.setRegion(regionSelected);

            colorRegion = secondaryTextColor;
            setTextForRegion(profileInfo.getRegion());
          }
        }
      }
      if (requestCode == REQUEST_THREE_SIZES) {

        if (data != null) {
          int[] threeSizes = data.getIntArrayExtra(MEASUREMENT);

          if (profileInfo != null) {
            profileInfo.setThreeSizes(threeSizes);
          }
        }
      }
//            if (requestCode == REQUEST_IMAGE) {
//                ArrayList<MediaItem> mMediaSelectedList = MediaPickerActivity.getMediaItemSelected(data);
//                if (mMediaSelectedList != null) {
//                    for (final MediaItem mediaItem : mMediaSelectedList) {
//                        mImagePath = mediaItem.getCroppedPath();
//                        if (TextUtils.isEmpty(mImagePath)) {
//                            mImagePath = mediaItem.getOriginPath();
//                        }
//
//                        Bitmap bitmap = BitmapFactory.decodeFile(mImagePath);
//                        if (bitmap != null) {
//                            RoundedAvatarDrawable drawable = new RoundedAvatarDrawable(bitmap);
//                            mImgAvatar.setImageDrawable(drawable);
//                            mImgAvatar.setVisibility(View.VISIBLE);
//                            mImgCircleAvatar.setVisibility(View.GONE);
//                        }
//                    }
//                } else {
//                    LogUtils.e(TAG, "Error to get media, NULL");
//                }
//            }
      if (requestCode == Camera.REQUEST_CODE_CAMERA
          || requestCode == Gallery.REQUEST_CODE_GALLERY) {
        Parcelable[] files = data.getParcelableArrayExtra(MediaPickerBaseActivity.RESULT_KEY);
        for (Parcelable parcelable : files) {
          MediaFile file = (MediaFile) parcelable;
          mImagePath = file.getPath();

          Bitmap bitmap = BitmapFactory.decodeFile(mImagePath);
          if (bitmap != null) {
            RoundedAvatarDrawable drawable = new RoundedAvatarDrawable(bitmap);
            mImgAvatar.setImageDrawable(drawable);
            mImgAvatar.setVisibility(View.VISIBLE);
            mImgCircleAvatar.setVisibility(View.GONE);
          }
        }
      }

      if (requestCode == REQUEST_TEXT) {
        if (data != null) {
          String text = data.getStringExtra(KEY_DATA);
          int field = data.getIntExtra(KEY_FIELD, 0);

          switch (field) {
            case FIELD_FETISH:
              if (profileInfo != null) {
                profileInfo.setFetish(text);
              }
              break;

            case FIELD_HOBBY:
              if (profileInfo != null) {
                profileInfo.setHobby(text);
              }
              break;

            case FIELD_MESSAGE:
              if (profileInfo != null) {
                profileInfo.setMessage(text);
              }
              break;

            case FIELD_TYPE_MAN:
              if (profileInfo != null) {
                profileInfo.setTypeMan(text);
              }
              break;

            default:
              break;
          }
        }
      }
    }
  }

  private void addProfileTextFragment(int field, int gender) {
    String data = "";
    if (profileInfo != null) {
      switch (field) {
        case FIELD_FETISH:
          data = profileInfo.getFetish();
          break;
        case FIELD_HOBBY:
          data = profileInfo.getHobby();
          break;
        case FIELD_MESSAGE:
          data = profileInfo.getMessage();
          break;
        case FIELD_TYPE_MAN:
          data = profileInfo.getTypeMan();
          break;
        default:
          break;
      }
    }

    ProfileTextFragment fragment = ProfileTextFragment.newInstance(data,
        field, gender);
    fragment.setTargetFragment(getRootParentFragment(this), REQUEST_TEXT);
    mNavigationManager.addPage(fragment);
  }

  private void chooseJob() {
    int gender = getGender();

    if (gender == UserSetting.GENDER_MALE) {
      final CharSequence[] items = getResources().getStringArray(
          R.array.job_male);
      LayoutInflater inflater = LayoutInflater.from(getActivity());
      View customTitle = inflater.inflate(R.layout.dialog_customize, null);

      android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(
          getActivity());
      ((TextView) customTitle.findViewById(R.id.tv_title_dialog_customize))
          .setText(R.string.profile_reg_job);
      builder.setCustomTitle(customTitle);
      //builder.setTitle(R.string.profile_reg_job);
      int itemChecked = profileInfo != null ? profileInfo.getJob() : -1;

      builder.setSingleChoiceItems(items, itemChecked,
          new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              mTxtJob.setText(items[which]);

              if (profileInfo != null) {
                profileInfo.setJob(which);
              }

              dialog.dismiss();
            }
          });

      android.app.AlertDialog alertDialog = builder.create();
      alertDialog.show();
      int dividerId = alertDialog.getContext().getResources()
          .getIdentifier("android:id/titleDivider", null, null);
      View divider = alertDialog.findViewById(dividerId);
      if (divider != null) {
        divider.setBackgroundColor(getResources().getColor(R.color.transparent));
      }

    } else if (gender == UserSetting.GENDER_FEMALE) {
      final CharSequence[] items = getResources().getStringArray(
          R.array.job_female);
      LayoutInflater inflater = LayoutInflater.from(getActivity());
      View customTitle = inflater.inflate(R.layout.dialog_customize, null);

      android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(
          getActivity());
      ((TextView) customTitle.findViewById(R.id.tv_title_dialog_customize))
          .setText(R.string.profile_reg_job);
      builder.setCustomTitle(customTitle);

      //builder.setTitle(R.string.profile_reg_job);
      int itemChecked = profileInfo != null ? profileInfo.getJob() : -1;
      builder.setSingleChoiceItems(items, itemChecked,
          new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              mTxtJob.setText(items[which]);

              if (profileInfo != null) {
                profileInfo.setJob(which);
              }

              dialog.dismiss();
            }
          });

      android.app.AlertDialog alertDialog = builder.create();
      alertDialog.show();

      int dividerId = alertDialog.getContext().getResources()
          .getIdentifier("android:id/titleDivider", null, null);
      View divider = alertDialog.findViewById(dividerId);
      if (divider != null) {
        divider.setBackgroundColor(getResources().getColor(R.color.transparent));
      }
    }
  }

  private void chooseCupSize() {
    final CharSequence[] items = {
        getString(R.string.profile_reg_cup_size_a),
        getString(R.string.profile_reg_cup_size_b),
        getString(R.string.profile_reg_cup_size_c),
        getString(R.string.profile_reg_cup_size_d),
        getString(R.string.profile_reg_cup_size_e),
        getString(R.string.profile_reg_cup_size_f),
        getString(R.string.profile_reg_cup_size_g),
        getString(R.string.profile_reg_cup_size_h),
        getString(R.string.profile_reg_cup_size_i)};
    LayoutInflater inflater = LayoutInflater.from(getActivity());
    View customTitle = inflater.inflate(R.layout.dialog_customize, null);

    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(
        getActivity());

    ((TextView) customTitle.findViewById(R.id.tv_title_dialog_customize))
        .setText(R.string.profile_reg_cup_size);
    builder.setCustomTitle(customTitle);

    //builder.setTitle(R.string.profile_reg_cup_size);
    int checkedItem = profileInfo != null ? profileInfo.getCupSize() : -1;
    builder.setSingleChoiceItems(items, checkedItem,
        new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            mTxtCupSize.setText(items[which]);

            if (profileInfo != null) {
              profileInfo.setCupSize(which);
            }

            dialog.dismiss();
          }
        });
    android.app.AlertDialog alertDialog = builder.create();
    alertDialog.show();
    int dividerId = alertDialog.getContext().getResources()
        .getIdentifier("android:id/titleDivider", null, null);
    View divider = alertDialog.findViewById(dividerId);
    if (divider != null) {
      divider.setBackgroundColor(getResources().getColor(R.color.transparent));
    }
  }

  private void chooseCuteType() {
    final CharSequence[] items = {
        getString(R.string.profile_reg_cute_type_neat),
        getString(R.string.profile_reg_cute_type_unpussy),
        getString(R.string.profile_reg_cute_type_lolita),
        getString(R.string.profile_reg_cute_type_healing),
        getString(R.string.profile_reg_cute_type_beautiful),
        getString(R.string.profile_reg_cute_type_cute),
        getString(R.string.profile_reg_cute_type_gal),
        getString(R.string.profile_reg_cute_type_comedy),
        getString(R.string.profile_reg_cute_type_natural),
        getString(R.string.profile_reg_cute_type_sister_1),
        getString(R.string.profile_reg_cute_type_sister_2),
        getString(R.string.profile_reg_cute_type_queen),
        getString(R.string.profile_reg_cute_type_secret)};

    LayoutInflater inflater = LayoutInflater.from(getActivity());
    View customTitle = inflater.inflate(R.layout.dialog_customize, null);

    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(
        getActivity());

    ((TextView) customTitle.findViewById(R.id.tv_title_dialog_customize))
        .setText(R.string.profile_reg_cute_type);
    builder.setCustomTitle(customTitle);

    //builder.setTitle(R.string.profile_reg_cute_type);
    int checkedItem = profileInfo != null ? profileInfo.getCuteType() : -1;
    builder.setSingleChoiceItems(items, checkedItem,
        new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            mTxtCuteType.setText(items[which]);

            if (profileInfo != null) {
              profileInfo.setCuteType(which);
            }

            dialog.dismiss();
          }
        });
    android.app.AlertDialog alertDialog = builder.create();
    alertDialog.show();

    int dividerId = alertDialog.getContext().getResources()
        .getIdentifier("android:id/titleDivider", null, null);
    View divider = alertDialog.findViewById(dividerId);
    if (divider != null) {
      divider.setBackgroundColor(getResources().getColor(R.color.transparent));
    }
  }

  private void chooseJoinHours() {
    final CharSequence[] items = {
        getString(R.string.profile_reg_join_hours_morning),
        getString(R.string.profile_reg_join_hours_morning_daytime),
        getString(R.string.profile_reg_join_hours_evening),
        getString(R.string.profile_reg_join_hours_night_midnight),
        getString(R.string.profile_reg_join_hours_late_night),
        getString(R.string.profile_reg_join_hours_irrehular),
        getString(R.string.profile_reg_join_hours_email),
        getString(R.string.profile_reg_join_hours_secret)};

    LayoutInflater inflater = LayoutInflater.from(getActivity());
    View customTitle = inflater.inflate(R.layout.dialog_customize, null);

    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(
        getActivity());
    ((TextView) customTitle.findViewById(R.id.tv_title_dialog_customize))
        .setText(R.string.profile_reg_join_hours);
    builder.setCustomTitle(customTitle);

    //builder.setTitle(R.string.profile_reg_join_hours);
    int checkedItem = profileInfo != null ? profileInfo.getJoinHours() : -1;
    builder.setSingleChoiceItems(items, checkedItem,
        new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            mTxtJoinHours.setText(items[which]);

            if (profileInfo != null) {
              profileInfo.setJoinHours(which);
            }

            dialog.dismiss();
          }
        });
    android.app.AlertDialog alertDialog = builder.create();
    alertDialog.show();

    int dividerId = alertDialog.getContext().getResources()
        .getIdentifier("android:id/titleDivider", null, null);
    View divider = alertDialog.findViewById(dividerId);
    if (divider != null) {
      divider.setBackgroundColor(getResources().getColor(R.color.transparent));
    }
  }

  private void chooseBirthday() {
    int year;
    int dayOfMonth;
    int monthOfYear;

    if (profileInfo != null) {
      SimpleDateFormat format = new SimpleDateFormat(
          Constants.DATE_FORMAT_SEND_TO_SERVER, Locale.getDefault());
      Date date = null;
      try {
        date = format.parse(profileInfo.getBirthday());
      } catch (ParseException e) {
        e.printStackTrace();
      }

      Calendar calendar = Calendar.getInstance();
      if (date != null) {
        calendar.setTime(date);
      }
      year = calendar.get(Calendar.YEAR);
      dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
      monthOfYear = calendar.get(Calendar.MONTH);
    } else {
      Calendar calendar = Calendar.getInstance();
      year = calendar.get(Calendar.YEAR);
      dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
      monthOfYear = calendar.get(Calendar.MONTH);
    }

    DatePickerDialog.OnDateSetListener onDateSetListener = new OnDateSetListener() {
      @Override
      public void onDateSet(DatePicker view, int year, int monthOfYear,
          int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, monthOfYear);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        Date date = calendar.getTime();
        setTextForAge(date);

        if (profileInfo != null) {
          SimpleDateFormat format = new SimpleDateFormat(
              Constants.DATE_FORMAT_SEND_TO_SERVER,
              Locale.getDefault());
          String birthdate = format.format(calendar.getTime());
          profileInfo.setBirthday(birthdate);
        }
      }
    };

    CustomDatePickerDialog datePickerDialog = new CustomDatePickerDialog(getActivity(),
        onDateSetListener, year, monthOfYear, dayOfMonth);
    datePickerDialog.show();
  }

  private void pickImageCapture() {
//        MediaOptions.Builder builder = new MediaOptions.Builder();
//        MediaOptions options = builder.setIsCropped(true).setFixAspectRatio(true).selectPhoto().build();
//        MediaPickerActivity.open(EditProfileFragment.this, REQUEST_IMAGE, options);

    new Gallery.Builder()
        .cropOutput(true)
        .fixAspectRatio(true)
        .viewType(Gallery.VIEW_TYPE_PHOTOS_ONLY)
        .multiChoice(false)
        .build()
        .start(this);
  }

  @Override
  public void startRequest(int loaderId) {
    if (mDialog != null && !mDialog.isShowing()) {
      mDialog.show();
    }
  }

  private void responseUpdateInfo(UserInfoUpdateRespone response) {
    if (response.getCode() == Response.SERVER_SUCCESS) {
      // show news on if backend setting = true
      NewsPreference.setShowNews(getContext(), NewsPreference.KEY_SHOW_NEWS_POPUP_HOT_PAGE, true);
      NewsPreference
          .setShowNews(getContext(), NewsPreference.KEY_SHOW_NEWS_POPUP_MEET_PEOPLE, true);

      if (response.isReview()) {
        showDialogReviewProfileInfo(response);
      } else {
        validateDataUserInfoUpdate(response);
      }
    } else {
      if (mActionBar != null) {
        mActionBar.enableEditRightButton();
      }
      ErrorApiDialog.showAlert(getActivity(), R.string.common_error,
          response.getCode());
    }
  }

  private void responseGetUserInfo(UserInfoResponse response) {
    if (response.getCode() == Response.SERVER_SUCCESS) {
      profileInfo.updateUserInfo(response);
      fillDataForUI();
    } else {
      if (mActionBar != null) {
        mActionBar.enableEditRightButton();
      }
      ErrorApiDialog.showAlert(getActivity(), R.string.common_error,
          response.getCode());
    }
  }

  @Override
  public void receiveResponse(Loader<Response> loader, Response response) {
    if (getActivity() == null) {
      return;
    }
    int loaderID = loader.getId();
    getLoaderManager().destroyLoader(loaderID);
    switch (loaderID) {
      case LOADER_ID_UPDATE_INFO:
        responseUpdateInfo((UserInfoUpdateRespone) response);
        break;
      case LOADER_ID_GET_USER_INFO:
        responseGetUserInfo((UserInfoResponse) response);
        if (mDialog.isShowing()) {
          mDialog.dismiss();
        }
        break;

    }


  }

  @Override
  public Response parseResponse(int loaderID, ResponseData data,
      int requestType) {
    Response response = null;
    switch (loaderID) {
      case LOADER_ID_UPDATE_INFO:
        response = new UserInfoUpdateRespone(data);
        break;
      case LOADER_ID_GET_USER_INFO:
        response = new UserInfoResponse(data);
        break;
    }
    return response;
  }

  @Override
  public void onBaseLoaderReset(Loader<Response> loader) {
  }

  private void uploadAvatarToServer() {
    ImageUploader imageUploader = new ImageUploader(uploadImageProgress);
    String token = UserPreferences.getInstance().getToken();

    UploadImageRequest imageRequest = null;

    if (!TextUtils.isEmpty(mImagePath)) {
      File file = new File(mImagePath);
      String md5Encrypted = ImageUtil.getMD5EncryptedString(file);
      imageRequest = new UploadImageRequest(token, UploadImageRequest.AVATAR, file, md5Encrypted);
      LogUtils.d("Upload", "Upload image: " + imageRequest.toURL());
      imageUploader.execute(imageRequest);
    } else {
      if (mActionBar != null) {
        mActionBar.enableEditRightButton();
      }
      if (getActivity() instanceof MainActivity) {
        if (mDialog != null && mDialog.isShowing()) {
          mDialog.dismiss();
        }
        showDialogAskGotoProfileWhenEditSuccessfully();

      } else {
        if (mDialog != null && mDialog.isShowing()) {
          mDialog.dismiss();
        }
        goToMainActivity();

      }
    }
  }

  private void goToMainActivity() {
    ProfileRegisterActivity activity = (ProfileRegisterActivity) getActivity();
    Intent intent = new Intent(activity, MainActivity.class);
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
    intent.putExtra(Constants.INTENT_FINISH_REGISTER_FLAG,
        finishRegisterFlag);
    activity.startActivity(intent);
    activity.finish();
  }

  @SuppressWarnings("deprecation")
  private void showThreeSizesPiker() {
    final NumberPickerDialog picker = new NumberPickerDialog(getActivity(),
        R.string.profile_reg_three_sizes, SHOWMODE.THREE,
        getString(R.string.profile_reg_three_sizes_b),
        getString(R.string.profile_reg_three_sizes_w),
        getString(R.string.profile_reg_three_sizes_h));

    picker.setRangeOne(UserSetting.MIN_THREE_SIZES,
        UserSetting.MAX_THREE_SIZES);
    picker.setRangeTwo(UserSetting.MIN_THREE_SIZES,
        UserSetting.MAX_THREE_SIZES);
    picker.setRangeThree(UserSetting.MIN_THREE_SIZES,
        UserSetting.MAX_THREE_SIZES);

    if (profileInfo != null && profileInfo.getThreeSizes() != null
        && profileInfo.getThreeSizes().length > 2) {

      picker.setInitialBoldValue(profileInfo.getThreeSizes()[0],
          profileInfo.getThreeSizes()[1],
          profileInfo.getThreeSizes()[2]);

    } else {
      picker.setInitialBoldValue(UserSetting.MIN_THREE_SIZES,
          UserSetting.MIN_THREE_SIZES, UserSetting.MIN_THREE_SIZES);
    }

    picker.setInverseBackgroundForced(true);
    picker.setButton(DialogInterface.BUTTON_NEGATIVE,
        getString(R.string.common_cancel),
        new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
          }
        });
    picker.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.common_save),
        new DialogInterface.OnClickListener() {

          @Override
          public void onClick(DialogInterface dialog, int which) {
            int[] threeSizes = new int[3];

            threeSizes[0] = picker.getNumberPickerOne()
                .getCurrent();
            threeSizes[1] = picker.getNumberPickerTwo()
                .getCurrent();
            threeSizes[2] = picker.getNumberPickerThree()
                .getCurrent();

            if (profileInfo != null) {
              profileInfo.setThreeSizes(threeSizes);
              setTextForThreeSizes(threeSizes);
            }

          }
        });

    picker.show();
  }

  private void showDialogNameTooLong() {
    if (mDialogNameTooLong != null && mDialogNameTooLong.isShowing()) {
      return;
    }

    String title = getString(R.string.edit_my_profile_title);
    String msg = getString(R.string.profile_name_too_long);

    mDialogNameTooLong = new CustomConfirmDialog(getActivity(), title, msg, false)
        .setPositiveButton(0, null)
        .create();

    mDialogNameTooLong.setCancelable(false);
    mDialogNameTooLong.show();

    int dividerId = mDialogNameTooLong.getContext().getResources()
        .getIdentifier("android:id/titleDivider", null, null);
    View divider = mDialogNameTooLong.findViewById(dividerId);
    if (divider != null) {
      divider.setBackgroundColor(
          mDialogNameTooLong.getContext().getResources().getColor(R.color.transparent));
    }
  }

  private void showDialogAskGotoProfileWhenEditSuccessfully() {
    LayoutInflater inflater = LayoutInflater.from(getActivity());
    View customTitle = inflater.inflate(R.layout.dialog_customize, null);
    android.app.AlertDialog.Builder builder = new CenterButtonDialogBuilder(getActivity(), true);
    ((TextView) customTitle.findViewById(R.id.tv_title_dialog_customize))
        .setText(R.string.edit_my_profile_title);
    builder.setCustomTitle(customTitle);

    //builder.setTitle(R.string.edit_my_profile_title);
    builder.setMessage(R.string.profile_reg_ask_jumpto_profile);
    builder.setCancelable(false);

    builder.setNegativeButton(R.string.profile_reg_ask_jumpto_profile_no,
        new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();

            Runnable runnable = new Runnable() {
              @Override
              public void run() {
                mNavigationManager.goBack();
              }
            };
            Handler handler = new Handler();
            handler.post(runnable);
          }
        });

    builder.setPositiveButton(R.string.profile_reg_ask_jumpto_profile_yes,
        new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();

            UserPreferences preferences = UserPreferences
                .getInstance();
            String userId = preferences.getUserId();

            if (!isFromMyPageFragment) {
              MyProfileFragment myProfileFragment = MyProfileFragment
                  .newInstance(userId,
                      MyProfileFragment.FROM_EDIT_PROFILE);
              mNavigationManager.addPage(myProfileFragment);
            } else {
              MyProfileFragment myProfileFragment = MyProfileFragment
                  .newInstance(userId,
                      MyProfileFragment.FROM_EDIT_PROFILE_MY_PAGE);
              mNavigationManager.addPage(myProfileFragment);
            }


          }
        });

    builder.create();
    android.app.AlertDialog element = builder.show();
    int dividerId = element.getContext().getResources()
        .getIdentifier("android:id/titleDivider", null, null);
    View divider = element.findViewById(dividerId);
    if (divider != null) {
      divider.setBackgroundColor(getResources().getColor(R.color.transparent));
    }
  }

  private void setTextIndicatorRequiredFields() {
    String pleaseFill = getString(R.string.profile_reg_please_fill_required_fields);
    int index = pleaseFill.indexOf("(");
    SpannableString span = new SpannableString(pleaseFill);
    span.setSpan(new ForegroundColorSpan(Color.RED), index + 1, index + 2,
        0);
    mTxtPleazFillRequired.setText(span);

    String star = getString(R.string.star);

    String text = "";
    String text_temp = getString(R.string.profile_update_profile_name);
    Spannable redStar = null;

    if (profileInfo.getName().equals("")) {
      text = mTxtIndicatorName.getText().toString() + star;
    } else {
      text = text_temp.toString() + star;
    }
    redStar = new SpannableString(text);
    redStar.setSpan(new ForegroundColorSpan(Color.RED), text.indexOf(star),
        text.indexOf(star) + 1, 0);
    mTxtIndicatorName.setText(redStar);

    text = mTxtIndicatorRegion.getText().toString() + star;
    redStar = new SpannableString(text);
    redStar.setSpan(new ForegroundColorSpan(Color.RED), text.indexOf(star),
        text.indexOf(star) + 1, 0);
    mTxtIndicatorRegion.setText(redStar);
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();

    Utility.hideSoftKeyboard(getActivity());
  }

  /**
   * Show dialog please fill profile only the first visit ProfileRegisterActivity
   */
  private void showDialogPleaseFillProfile() {
    UserPreferences userPreferences = UserPreferences.getInstance();
    int finishRegisterFlag = userPreferences.getFinishRegister();
    boolean checkDialogShow = userPreferences
        .getIsFillProfileDialogShowable();

    if (finishRegisterFlag == Constants.FINISH_REGISTER_NO
        && checkDialogShow) {
      ErrorApiDialog.showAlert(getActivity(),
          getString(R.string.flow_reg_dialog_fill_profile_title),
          getString(R.string.flow_reg_dialog_fill_profile_message));

      dialogPleazeFillProfileShowed = true;

      // set dialog showed
      userPreferences
          .saveIsFillProfileDialogShowable(Constants.IS_SHOWED_FLAG);
    }
  }

  public void backToFirstScreen() {
    if (getActivity() instanceof ProfileRegisterActivity) {
      UserPreferences userPreferences = UserPreferences.getInstance();
      userPreferences.clear();

      Intent intent = new Intent(getActivity(), SignUpActivity.class);
      getActivity().startActivity(intent);

      getActivity().finish();
    }
  }
}
