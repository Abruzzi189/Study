package com.application.ui.account;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TableRow;
import android.widget.TextView;
import com.application.AndGApp;
import com.application.actionbar.NoFragmentActionBar;
import com.application.common.webview.WebViewActivity;
import com.application.common.webview.WebViewFragment;
import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.application.connection.request.SignupByFacebookRequest;
import com.application.connection.request.SignupRequest;
import com.application.connection.response.GetUserStatusResponse;
import com.application.connection.response.LoginResponse;
import com.application.connection.response.SignupResponse;
import com.application.constant.Constants;
import com.application.constant.UserSetting;
import com.application.entity.User;
import com.application.ui.BaseFragmentActivity;
import com.application.ui.CenterButtonDialogBuilder;
import com.application.ui.customeview.ErrorApiDialog;
import com.application.util.LogUtils;
import com.application.util.Utility;
import com.application.util.preferece.BlockUserPreferences;
import com.application.util.preferece.GoogleReviewPreference;
import com.application.util.preferece.Preferences;
import com.application.util.preferece.UserPreferences;
import com.google.firebase.iid.FirebaseInstanceId;
import com.ntq.adjust.AdjustSdk;
import glas.bbsystem.R;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


public class RegisterActivity extends BaseFragmentActivity implements
    OnClickListener {

  public static final String EXTRA_USER_DATA = "extra_user_data";
  private static final int LOADER_ID_REGISTER = 1;
  private final String TAG = "RegisterActivity";
  private Button mBtnRegister;
  private TableRow mTbrBirthday;
  private TableRow mTbrGender;
  private TextView mTxtBirthday;
  private TextView mTxtGender;
  private Date mBirthdaySelected;
  private ProgressDialog mDialog;
  private TextView mTxtUserAgree;
  private TextView mTxtPolicy;
  private User mUser;
  private boolean isGenderSelected = false;
  private NoFragmentActionBar mActionBar;

  @Override
  protected void onCreate(Bundle bundle) {
    super.onCreate(bundle);
    setContentView(R.layout.activity_register);
    initActionBar();

    Intent intent = getIntent();
    if (intent != null) {
      mUser = (User) intent.getSerializableExtra(EXTRA_USER_DATA);
      if (mUser != null) {
        LogUtils.d(TAG, "Register gender: " + mUser.getGender()
            + "\nRegister bithd: " + mUser.getBirthday()
            + "\nRegister name: " + mUser.getName());
      }
    }

    if (mUser == null) {
      mUser = new User();
    }

    initViews();
  }

  @Override
  public void onCreateContextMenu(ContextMenu menu, View v,
      ContextMenuInfo menuInfo) {
    super.onCreateContextMenu(menu, v, menuInfo);

    MenuInflater inflater = getMenuInflater();
    if (v == mTxtGender) {
      inflater.inflate(R.menu.menu_gender, menu);
      MenuItem item;
      switch (mUser.getGender()) {
        case UserSetting.GENDER_MALE:
          item = menu.findItem(R.id.gender_male);
          break;
        case UserSetting.GENDER_FEMALE:
          item = menu.findItem(R.id.gender_female);
          break;
        default:
          item = menu.findItem(R.id.gender_male);
          break;
      }

      if (isGenderSelected) {
        item.setChecked(true);
      }
    }
  }

  @Override
  public boolean onContextItemSelected(MenuItem item) {
    item.setChecked(true);
    switch (item.getItemId()) {
      case R.id.gender_male:
        mUser.setGender(UserSetting.GENDER_MALE);
        mTxtGender.setText(R.string.common_man);
        isGenderSelected = true;
        return true;

      case R.id.gender_female:
        mUser.setGender(UserSetting.GENDER_FEMALE);
        mTxtGender.setText(R.string.common_woman);
        isGenderSelected = true;
        return true;

      default:
        break;
    }
    return super.onContextItemSelected(item);
  }

  private void initViews() {
    mBtnRegister = (Button) findViewById(R.id.btn_register);
    mTbrBirthday = (TableRow) findViewById(R.id.tbr_birthday);
    mTbrGender = (TableRow) findViewById(R.id.tbr_gender);
    mTxtBirthday = (TextView) findViewById(R.id.txt_birthday);
    mTxtGender = (TextView) findViewById(R.id.txt_gender);
    mTxtUserAgree = (TextView) findViewById(R.id.txt_term);
    mTxtPolicy = (TextView) findViewById(R.id.txt_policy);

    mBtnRegister.setOnClickListener(this);
    mTbrBirthday.setOnClickListener(this);
    mTbrGender.setOnClickListener(this);
    mTxtBirthday.setOnClickListener(this);
    mTxtGender.setOnClickListener(this);
    mTxtUserAgree.setOnClickListener(this);
    mTxtPolicy.setOnClickListener(this);

    mDialog = new ProgressDialog(this);
    mDialog.setMessage(getString(R.string.creating_account_please_wait));
    mDialog.setCancelable(false);

    initData();
  }

  private void initData() {
    if (!TextUtils.isEmpty(mUser.getAnotherSystemId())) {
      displayDefaultBirthday();
      displayGender();
    }
  }

  @SuppressWarnings("unused")
  private void setUpNavigationBar() {
    getNavigationBar().setNavigationLeftLogo(R.drawable.navigation_back);
    getNavigationBar().setCenterTitle(R.string.title_register);
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.btn_register:
        register();
        break;

      case R.id.tbr_birthday:
      case R.id.txt_birthday:
        chooseBirthday();
        break;

      case R.id.tbr_gender:
      case R.id.txt_gender:
        registerContextMenu(mTxtGender);
        break;

      case R.id.txt_term:
        Intent intent = new Intent(this, WebViewActivity.class);
        intent.putExtra(WebViewFragment.INTENT_PAGE_TYPE,
            WebViewFragment.PAGE_TYPE_TERM_OF_SERVICE);
        startCustomeActivityForResult(intent);
        break;

      case R.id.txt_policy:
        Intent intentStaticPage = new Intent(this, WebViewActivity.class);
        intentStaticPage.putExtra(WebViewFragment.INTENT_PAGE_TYPE,
            WebViewFragment.PAGE_TYPE_PRIVACY_POLICY);
        startCustomeActivityForResult(intentStaticPage);

        break;

      default:
        break;
    }
  }

  /**
   * @return the date 20 years from now
   */
  private void displayDefaultBirthday() {
    Date birthday = null;
    if (mUser != null && mUser.getBirthday() != null) {
      birthday = mUser.getBirthday();

    }

    setBirthdayToDisplay(birthday);
  }

  private void displayGender() {
    if (mUser != null) {
      if (mUser.getGender() == UserSetting.GENDER_MALE) {
        mTxtGender.setText(R.string.common_man);
      } else if (mUser.getGender() == UserSetting.GENDER_FEMALE) {
        mTxtGender.setText(R.string.common_woman);
      }
    }
  }

  private Date getBirthDayFromText(String text) {
    SimpleDateFormat dateFormat = new SimpleDateFormat(
        Constants.DATE_FORMAT_DISPLAY, Locale.getDefault());
    try {
      return dateFormat.parse(text);
    } catch (ParseException e) {
      e.printStackTrace();
      return null;
    }
  }

  private void setBirthdayToDisplay(Date date) {
    if (date == null) {
      return;
    }

    try {
      SimpleDateFormat dateFormat = new SimpleDateFormat(
          Constants.DATE_FORMAT_DISPLAY, Locale.getDefault());
      String d = dateFormat.format(date);
      mTxtBirthday.setText(d);
      mBirthdaySelected = date;
      mUser.setBirthday(mBirthdaySelected);
    } catch (NullPointerException npe) {
      LogUtils.e(TAG, String.valueOf(npe.getMessage()));
    } catch (IllegalArgumentException iae) {
      LogUtils.e(TAG, String.valueOf(iae.getMessage()));
    }
  }

  private void registerContextMenu(View view) {
    registerForContextMenu(view);
    openContextMenu(view);
    unregisterForContextMenu(view);
  }

  private void chooseBirthday() {
    int year;
    int dayOfMonth;
    int monthOfYear;
    if (mTxtBirthday.getText().length() > 0) {
      String text = mTxtBirthday.getText().toString();
      Date date = getBirthDayFromText(text);
      Calendar calendar = Calendar.getInstance();
      if (date != null) {
        calendar.setTime(date);
      }
      year = calendar.get(Calendar.YEAR);
      dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
      monthOfYear = calendar.get(Calendar.MONTH);
    } else {
      Calendar calendar = Calendar.getInstance();
      Calendar calendarSend = Calendar.getInstance(TimeZone
          .getTimeZone("GMT"));
      int Y = calendarSend.get(Calendar.YEAR) - 18;
      int X = calendarSend.get(Calendar.MONTH);
      int Z = calendarSend.get(Calendar.DAY_OF_MONTH);
      calendar.set(Calendar.YEAR, Y);
      calendar.set(Calendar.MONTH, X);
      calendar.set(Calendar.DAY_OF_MONTH, Z);
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
        setBirthdayToDisplay(calendar.getTime());
      }
    };

    DatePickerDialog datePickerDialog = new DatePickerDialog(
        RegisterActivity.this, onDateSetListener, year, monthOfYear,
        dayOfMonth);
    datePickerDialog.show();
  }

  private void register() {
    if (mBirthdaySelected == null) {
      ErrorApiDialog.showAlert(this, getString(R.string.setup_profile),
          getString(R.string.birthday_value_is_invalid));
      return;
    }

    if (TextUtils.isEmpty(mTxtGender.getText())) {
      ErrorApiDialog.showAlert(this, getString(R.string.setup_profile),
          getString(R.string.gender_is_invalid));
      return;
    }

    if (Utility.checkbirthday(mBirthdaySelected) == false) {
      com.application.ui.customeview.AlertDialog
          .showAlert(this, R.string.common_error, Response.SERVER_INVALID_BIRTHDAY);
      return;
    }

    SignupRequest request = null;
    String anotherSystemId = mUser.getAnotherSystemId();

    SimpleDateFormat format = new SimpleDateFormat(
        Constants.DATE_FORMAT_SEND_TO_SERVER, Locale.getDefault());
    String birthday = format.format(mUser.getBirthday());
    String deviceId = Utility.getDeviceId(getApplicationContext());
    int gender = mUser.getGender();
    String loginTime = Utility.getLoginTime();
//        String notify_token = Preferences.getInstance().getGCMResitrationId();
    String notify_token = FirebaseInstanceId.getInstance().getToken();
    String appVersion = Utility.getAppVersionName(this);
    String adjustAdid = "";
    adjustAdid = Preferences.getInstance().getAdjustAdid();
    String applicationName = Utility.getApplicationName(this);

    if (TextUtils.isEmpty(anotherSystemId)) {
      request = new SignupRequest(birthday, gender, deviceId,
          notify_token, loginTime, appVersion, AndGApp.advertId, AndGApp.device_name,
          AndGApp.os_version, adjustAdid, applicationName);
    } else {
      LogUtils.d(TAG, "Register via another sys id: " + anotherSystemId);
      request = new SignupByFacebookRequest(anotherSystemId, birthday,
          gender, deviceId, notify_token, loginTime, appVersion, AndGApp.advertId,
          AndGApp.device_name, AndGApp.os_version, adjustAdid, applicationName);
    }
    //track register app
    AdjustSdk.trackRegisterApp();
    restartRequestServer(LOADER_ID_REGISTER, request);
  }

  @Override
  public void startRequest(int loaderId) {
    super.startRequest(loaderId);
    mDialog = new ProgressDialog(this);
    mDialog.setMessage(getString(R.string.creating_account_please_wait));
    mDialog.setCancelable(false);
    mDialog.show();
  }

  /*
   * @Override public void onNavigationLeftClick(View view) { finish(); }
   *
   * @Override public void onNavigationRightClick(View view) { // Do nothing
   * cause this screen has no right icon on navigation bar. }
   */

  @Override
  public Response parseResponse(int loaderID, ResponseData data,
      int requestType) {
    if (loaderID == LOADER_ID_REGISTER) {
      return new SignupResponse(data);
    } else if (loaderID == LOADER_RETRY_LOGIN) {
      return new LoginResponse(data);
    } else if (loaderID == LOADER_GET_USER_STATUS) {
      return new GetUserStatusResponse(data);
    } else {
      return null;
    }

  }

  @Override
  public void receiveResponse(Loader<Response> loader, Response response) {
    super.receiveResponse(loader, response);

    if (mDialog != null) {
      mDialog.dismiss();
    }

    if (loader.getId() == LOADER_ID_REGISTER) {
      if (response.getCode() == Response.SERVER_SUCCESS) {
        if (!(response instanceof SignupResponse)) {
          return;
        }

        SignupResponse signupResponse = (SignupResponse) response;

        UserPreferences userPreferences = UserPreferences.getInstance();
        String email = signupResponse.getEmail();
        String pass = signupResponse.getPassword();
        userPreferences.saveEmail(email);
        userPreferences.savePassword(pass);

        // Save login data
        AuthenticationData authenData = signupResponse
            .getAuthenticationData();
        AuthenticationUtil.saveAuthenticationSuccessData(authenData,
            true);

        // newly registered account
        userPreferences.setIsNewlyAccount(true);
//                userPreferences.setShowNewsPopup(false);

        // Save blocked users list
        String blockUser = signupResponse.getBlockedUserList();
        BlockUserPreferences.getInstance().saveBlockedUsersList(
            blockUser);

        GoogleReviewPreference googleReviewPreference = new GoogleReviewPreference();
        googleReviewPreference.saveTurnOffVersion(signupResponse
            .getSwitchBrowserVersion());
        googleReviewPreference.saveEnableGetFreePoint(signupResponse
            .isEnableGetFreePoint());
        googleReviewPreference.saveIsTurnOffUserInfo(signupResponse.isTurnOffUserInfo());

        // Home page for CM code
        Preferences preferences = Preferences.getInstance();
        preferences.saveHomePageUrl(signupResponse.getHomePage());

        SimpleDateFormat format = new SimpleDateFormat(
            Constants.DATE_FORMAT_SEND_TO_SERVER,
            Locale.getDefault());
        String birthday = format.format(mUser.getBirthday());
        userPreferences.saveBirthday(birthday);

        if (!TextUtils.isEmpty(mUser.getAnotherSystemId())) {
          userPreferences.saveFacebookId(mUser.getAnotherSystemId());
          userPreferences.saveFacebookAvatar(mUser.getAvatar());

          try {
            byte[] nameBytes = mUser.getName()
                .getBytes("Shift_JIS");

            if (nameBytes.length > Constants.MAX_LENGTH_NAME_IN_HALF_SIZE) {
              byte[] data = new byte[Constants.MAX_LENGTH_NAME_IN_HALF_SIZE - 1];

              for (int i = 0; i < Constants.MAX_LENGTH_NAME_IN_HALF_SIZE - 1; i++) {
                data[i] = nameBytes[i];
              }

              String shortName = new String(data, "Shift_JIS");
              userPreferences.saveUserName(shortName);
              LogUtils.d("TamNV", "Save fb short name --- "
                  + shortName);
            } else {

              userPreferences.saveUserName(mUser.getName());
            }

          } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
          }
        }

        Intent intent = null;
        int gender = userPreferences.getGender();
        int ageVerification = userPreferences.getAgeVerification();
        userPreferences
            .saveIsFillProfileDialogShowable(Constants.IS_NOT_SHOWED_FLAG);

//				if (gender == UserSetting.GENDER_FEMALE) {
//					intent = new Intent(this, WebViewActivity.class);
//					if (ageVerification == Constants.AGE_VERIFICATION_DINED
//							|| ageVerification == Constants.AGE_VERIFICATION_NONE) {
//						intent.putExtra(WebViewFragment.INTENT_PAGE_TYPE,
//								WebViewFragment.PAGE_TYPE_VERIFY_AGE);
//					} else {
//						intent.putExtra(WebViewFragment.INTENT_PAGE_TYPE,
//								WebViewFragment.PAGE_TYPE_AUTO_VERIFY_AGE);
//					}
//				} else {
        intent = new Intent(this, ProfileRegisterActivity.class);
//				}
        Constants.checkopenURLcmcode = true;
        startActivity(intent);
        customeFinishActivity();
      }

      int code = response.getCode();
      if (code == Response.SERVER_WRONG_DATA_FORMAT) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View customTitle = inflater.inflate(R.layout.dialog_customize, null);
        Builder builder = new CenterButtonDialogBuilder(this, false);

        ((TextView) customTitle.findViewById(R.id.tv_title_dialog_customize))
            .setText(R.string.common_error);
        builder.setCustomTitle(customTitle);

        //builder.setTitle(R.string.common_error);
        builder.setMessage(R.string.msg_common_registed_fail);
        builder.setPositiveButton(R.string.common_ok, null);
        AlertDialog element = builder.show();

        int dividerId = element.getContext().getResources()
            .getIdentifier("android:id/titleDivider", null, null);
        View divider = element.findViewById(dividerId);
        if (divider != null) {
          divider.setBackgroundColor(getResources().getColor(R.color.transparent));
        }

      } else {
        com.application.ui.customeview.ErrorApiDialog.showAlert(this,
            R.string.common_error, response.getCode());
      }
    }
  }

  @Override
  public boolean isNoTitle() {
    return false;
  }

  private void initActionBar() {
    getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
    mActionBar = new NoFragmentActionBar(this);
    mActionBar.syncActionBar();
  }

}
