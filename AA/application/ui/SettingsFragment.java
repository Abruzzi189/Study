package com.application.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.application.Config;
import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.application.connection.ResponseReceiver;
import com.application.connection.request.LogoutRequest;
import com.application.connection.response.LogoutResponse;
import com.application.ui.account.EditProfileFragment;
import com.application.ui.account.SignUpActivity;
import com.application.ui.chat.IncomingSettingFragment;
import com.application.ui.customeview.CustomConfirmDialog;
import com.application.ui.customeview.ErrorApiDialog;
import com.application.ui.customeview.NavigationBar.OnNavigationClickListener;
import com.application.ui.settings.AboutFragment;
import com.application.ui.settings.AccountSettingsFragment;
import com.application.ui.settings.NotificationSettingsFragment;
import com.application.ui.settings.OnLogoutTask;
import com.application.util.LogUtils;
import com.application.util.Utility;
import com.application.util.preferece.UserPreferences;
import com.google.firebase.iid.FirebaseInstanceId;
import glas.bbsystem.BuildConfig;
import glas.bbsystem.R;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.IllegalFormatException;
import java.util.Locale;


public class SettingsFragment extends BaseFragment implements OnClickListener,
    OnNavigationClickListener, ResponseReceiver, OnCheckedChangeListener {

  private static final String TAG = SettingsFragment.class.getSimpleName();

  private static final int LOADER_LOGOUT_ID = 1;
  private ProgressDialog mProgressDialog;
  private AlertDialog confirmDialogLogout;
  private OnLogoutTask mLogoutTask;
  private String mPackageName = BuildConfig.APPLICATION_ID;
  private TextView mTxtAppVersion;

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    mLogoutTask = (OnLogoutTask) activity;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_settings, container, false);
    initView(view);
    resetNavigationBar();
    return view;
  }

  /**
   * Check whether or not register GCM ID
   */
  private void initView(View rootView) {
    try {
      RelativeLayout layoutStProfile = rootView.findViewById(R.id.llStProfile);
      RelativeLayout layoutStAccount = rootView.findViewById(R.id.llStAccount);
      RelativeLayout layoutStCall = rootView.findViewById(R.id.llStCall);
      RelativeLayout layoutStNotification = rootView.findViewById(R.id.llStNotification);
      RelativeLayout layoutStNReview = rootView.findViewById(R.id.llStNReview);
      RelativeLayout layoutStNAbout = rootView.findViewById(R.id.llStNAbout);

      mTxtAppVersion = rootView.findViewById(R.id.tv_settings_terms_of_service_version);

      // TODO : 14/6/2017 update by ThoNH about ticket http://10.64.100.201/issues/8930
      PackageInfo packageInfo = null;
      try {
        packageInfo = getContext().getPackageManager().getPackageInfo(mPackageName, 0);
        if (packageInfo != null) {
          Date buildDate = new Date(BuildConfig.EXPORT_TIMESTAMP);
          SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd  HH:mm", Locale.US);
          String timeBuild = sdf.format(buildDate);

          StringBuilder builder = new StringBuilder();
          builder.append(packageInfo.versionCode);
          builder.append("(");
          builder.append(packageInfo.versionName);
          builder.append(")  ");
          if (!Config.IS_PRODUCT_SERVER) {
            builder.append(" - STG - ");
          }

          builder.append(timeBuild);
          mTxtAppVersion.setText(builder.toString());
        }
      } catch (PackageManager.NameNotFoundException e) {
        e.printStackTrace();
      }

      layoutStProfile.setOnClickListener(this);
      layoutStAccount.setOnClickListener(this);
      layoutStNotification.setOnClickListener(this);
      layoutStNReview.setOnClickListener(this);
      layoutStNAbout.setOnClickListener(this);
      layoutStCall.setOnClickListener(this);

//			// tungdx added: init data for sound, vibratation
      UserPreferences userPreferences = UserPreferences.getInstance();
//			mchbSound.setChecked(userPreferences.isSoundOn());
//			mchbVibration.setChecked(userPreferences.isVibration());
//
//			// tungdx ended
//			mllNotification.setOnClickListener(this);

      FragmentActivity fragmentActivity = getActivity();
//			if (fragmentActivity != null) {
//				// Get package name of application.
//				mPackageName = fragmentActivity.getPackageName();
//
//				// Set version of application
//				PackageInfo packageInfo = fragmentActivity.getPackageManager()
//						.getPackageInfo(mPackageName, 0);
//				TextView textViewVersion = (TextView) rootView
//						.findViewById(R.id.tv_settings_terms_of_service_version);
//				String format = getString(R.string.settings_terms_of_service_version);
//				if (textViewVersion != null && format != null && packageInfo != null && packageInfo.versionName != null) {
//					String version = String.format(format,"1.2");
//					textViewVersion.setText(version);
//				}
//
////				// In login by Facebook case, hide [Change Password] field.
////				if (TextUtils.isEmpty(userPreferences.getEmail())) {
////					mtbrChangePassword.setVisibility(View.GONE);
////				} else {
////					mtbrChangePassword.setVisibility(View.VISIBLE);
////				}
//			}
//		} catch (NameNotFoundException nnfe) {
      // NOP
    } catch (IllegalFormatException ife) {
      // NOP
      ife.printStackTrace();
    }
  }

  protected void resetNavigationBar() {
    super.resetNavigationBar();
    if (getNavigationBar() == null) {
      return;
    }
    getNavigationBar().setNavigationLeftLogo(R.drawable.nav_menu);
    getNavigationBar().setNavigationRightTitle(R.string.logout);
    getNavigationBar().setCenterTitle(R.string.settings);
    getNavigationBar().setShowUnreadMessage(false);
  }

  @Override
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.llStProfile:
        openStProfile();
        break;
      case R.id.llStAccount:
        openStAccount();
        break;
      case R.id.llStNotification:
        openStNotifications();
        break;
      case R.id.llStNReview:
        openReview();
        break;
      case R.id.llStCall:
        openStIncoming();
        break;
      case R.id.llStNAbout:
        openAbout();
        break;
      default:
        break;
    }
  }

  //hiepuh

  private void openStIncoming() {
    UserPreferences preferences = UserPreferences.getInstance();
    boolean isVoice = preferences.isEnableVoiceCall();
    boolean isVideo = preferences.isEnableVideoCall();
    IncomingSettingFragment incomingSettingFragment =
        IncomingSettingFragment.getInstance(isVoice, isVideo);
    mNavigationManager.addPage(incomingSettingFragment);
  }

  private void openStProfile() {
    mNavigationManager.addPage(EditProfileFragment.newInstance());
  }

  private void openStAccount() {
    mNavigationManager.addPage(AccountSettingsFragment.newInstance());
  }

  private void openStNotifications() {
    mNavigationManager.addPage(NotificationSettingsFragment.newInstance());
  }

  private void openReview() {
    initVariables();
    if (mPackageName != null && mPackageName.length() > 0) {
      try {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=" + mPackageName));
        startActivity(intent);
      } catch (ActivityNotFoundException anfe) {
        LogUtils.e(TAG, anfe.getMessage());
      }
    }
  }

  private void openAbout() {
    mNavigationManager.addPage(AboutFragment.newInstance());
  }

  private void initVariables() {
    FragmentActivity fragmentActivity = getActivity();
    if (fragmentActivity != null) {
      // Get package name of application.
      mPackageName = fragmentActivity.getPackageName();
    }
  }


  @Override
  public void startRequest(int loaderId) {
    switch (loaderId) {
      case LOADER_LOGOUT_ID:
        mProgressDialog = ProgressDialog
            .show(getActivity(), "", getActivity().getString(R.string.logout), true, false);
        break;
      default:
        break;
    }
  }

  private void logout() {
    UserPreferences userPreferences = UserPreferences.getInstance();
    String token = userPreferences.getToken();
//        String notify_token = Preferences.getInstance().getGCMResitrationId();
    String notify_token = FirebaseInstanceId.getInstance().getToken();
    LogoutRequest logoutRequest = new LogoutRequest(token, notify_token);
    restartRequestServer(LOADER_LOGOUT_ID, logoutRequest);
  }

  private void executeRateApplication() {
    if (mPackageName != null && mPackageName.length() > 0) {
      try {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=" + mPackageName));
        startActivity(intent);
      } catch (ActivityNotFoundException anfe) {
        LogUtils.e(TAG, anfe.getMessage());
      }
    }
  }


  @Override
  public void onPause() {
    super.onPause();
    Utility.hideSoftKeyboard(baseFragmentActivity);
  }

  @Override
  public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

  }

  @Override
  public void onNavigationRightClick(View view) {
    if (confirmDialogLogout == null) {
      String title = getString(R.string.settings_logout_dialog_title);
      String msg = getString(R.string.settings_logout_dialog_content);
      confirmDialogLogout = new CustomConfirmDialog(getActivity(), title, msg, true)
          .setPositiveButton(0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              logout();
            }
          })
          .create();
    }
    confirmDialogLogout.show();
    int dividerId = confirmDialogLogout.getContext().getResources()
        .getIdentifier("android:id/titleDivider", null, null);
    View divider = confirmDialogLogout.findViewById(dividerId);
    if (divider != null) {
      divider.setBackgroundColor(
          confirmDialogLogout.getContext().getResources().getColor(R.color.transparent));
    }
  }

  @Override
  public Response parseResponse(int loaderID, ResponseData data, int requestType) {
    if (loaderID == LOADER_LOGOUT_ID) {
      return new LogoutResponse(data);
    }
    return null;
  }

  @Override
  public void receiveResponse(Loader<Response> loader, Response response) {
    if (mProgressDialog != null) {
      mProgressDialog.dismiss();
    }
    if (response != null) {
      ErrorApiDialog.showAlert(getActivity(), R.string.common_app_name,
          response.getCode());
      if (response.getCode() == Response.SERVER_SUCCESS) {
        mLogoutTask.executeLogoutTask();
        // Finish current activity and back to login screen.
        Toast.makeText(getActivity(), R.string.logout_success,
            Toast.LENGTH_LONG).show();
        Intent intent = new Intent(getActivity(), SignUpActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        getActivity().finish();
      }
    }
  }

  @Override
  public void onBaseLoaderReset(Loader<Response> loader) {

  }
}