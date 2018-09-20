package com.application.ui.notification;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.Loader;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;
import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.application.connection.ResponseReceiver;
import com.application.connection.request.AddOnlineAlertRequest;
import com.application.connection.request.CircleImageRequest;
import com.application.connection.request.GetOnlineAlertRequest;
import com.application.connection.response.AddOnlineAlertResponse;
import com.application.connection.response.GetOnlineAlertResponse;
import com.application.constant.Constants;
import com.application.ui.BaseFragment;
import com.application.ui.BaseFragmentActivity;
import com.application.ui.CenterButtonDialogBuilder;
import com.application.ui.profile.MyProfileFragment;
import com.application.util.preferece.UserPreferences;
import glas.bbsystem.R;


public class ManageOnlineAlertFragment extends BaseFragment implements
    ResponseReceiver {

  public static final int ALERT_NO = 0;
  public static final int ALERT_YES = 1;
  private static String KEY_USER_ID = "user_id";
  private static String KEY_USER_NAME = "user_name";
  private static String KEY_USER_AVATAR_ID = "avatar_id";
  private static String KEY_USER_IS_ALERT = "is_alert";
  private final int LOADER_GET_ONLINE_ALERT = 1;
  private final int LOADER_ADD_ONLINE_ALERT = 2;
  private String mUserId;
  private String mUserName;
  private String mAvatarId;
  private int isAlert;
  private int valueWhen = Constants.MANAGE_ONLINE_NEVER;
  private String[] mValueWhenStrArray;

  private CheckBox cbxAlert;
  private TextView txtWhen;
  private TextView txtWhenTitle;
  private View rltLayout;
  private ProgressDialog mProgressDialog;
  private AlertDialog dialogWhen;
  private AlertDialog dialogAddOnlineAlert;
  private String strWhen;

  public static ManageOnlineAlertFragment newInstance(String userId,
      String avatarid, String userName, int isAlt) {
    ManageOnlineAlertFragment fragment = new ManageOnlineAlertFragment();
    Bundle bundle = new Bundle();
    bundle.putString(KEY_USER_ID, userId);
    bundle.putString(KEY_USER_NAME, userName);
    bundle.putString(KEY_USER_AVATAR_ID, avatarid);
    bundle.putInt(KEY_USER_IS_ALERT, isAlt);
    fragment.setArguments(bundle);
    return fragment;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_manage_online_alert,
        container, false);
    if (getArguments() != null) {
      mUserId = getArguments().getString(KEY_USER_ID);
      mUserName = getArguments().getString(KEY_USER_NAME);
      mAvatarId = getArguments().getString(KEY_USER_AVATAR_ID);
      isAlert = getArguments().getInt(KEY_USER_IS_ALERT);
    }
    initialView(view);
    requestGetOnlineAlert();
    return view;
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    if (mProgressDialog != null && mProgressDialog.isShowing()) {
      mProgressDialog.dismiss();
    }
    if (dialogWhen != null && dialogWhen.isShowing()) {
      dialogWhen.dismiss();
    }
  }

  private void initialView(View view) {
    ImageView imgAvatar = (ImageView) view.findViewById(R.id.iv_avatar);
    String token = UserPreferences.getInstance().getToken();
    CircleImageRequest imageRequest = new CircleImageRequest(token,
        mAvatarId);
    getImageFetcher().loadImage(imageRequest, imgAvatar,
        imgAvatar.getWidth());

    final TextView txtUserName = (TextView) view
        .findViewById(R.id.txtUserName);
    txtUserName.getViewTreeObserver().addOnGlobalLayoutListener(
        new OnGlobalLayoutListener() {
          @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
          @SuppressWarnings("deprecation")
          @Override
          public void onGlobalLayout() {
            float textViewWidth = txtUserName.getWidth();
            if (textViewWidth <= 0) {
              return;
            }
            final String ELLIPSIS = "...";
            String formatUsername = getString(R.string.fragment_manage_online_alert_text_user);
            boolean isLongName = false;
            TextPaint paint = txtUserName.getPaint();
            String userName = mUserName;
            float textWidth = paint.measureText(userName);
            float formatWidth = paint.measureText(String.format(
                formatUsername, ""));
            textViewWidth -= formatWidth;
            if (textWidth > textViewWidth) {
              textViewWidth = textViewWidth
                  - paint.measureText(ELLIPSIS);
            }
            while (textWidth > textViewWidth) {
              isLongName = true;
              int length = userName.length();
              if (length < 1) {
                break;
              }
              userName = userName.substring(0, length - 1);
              textWidth = paint.measureText(userName);
            }
            if (isLongName) {
              userName = userName.trim() + ELLIPSIS;
            }
            txtUserName.setText(String.format(formatUsername,
                userName));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
              txtUserName.getViewTreeObserver()
                  .removeOnGlobalLayoutListener(this);
            } else {
              txtUserName.getViewTreeObserver()
                  .removeGlobalOnLayoutListener(this);
            }
          }
        });

    View cbxLayout = view.findViewById(R.id.cbx_is_alert_layout);
    cbxAlert = (CheckBox) view.findViewById(R.id.cbx_is_alert);
    cbxAlert.setChecked(isAlert != ALERT_NO);
    cbxAlert.setOnCheckedChangeListener(new OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView,
          boolean isChecked) {
        rltLayout.setEnabled(isChecked);
        if (isChecked) {
          txtWhen.setTextColor(getResources().getColor(
              R.color.color_text_default));
          txtWhenTitle.setTextColor(getResources().getColor(
              R.color.color_text_default));
        } else {
          txtWhen.setTextColor(getResources().getColor(
              R.color.color_hint_bold));
          txtWhenTitle.setTextColor(getResources().getColor(
              R.color.color_hint_bold));
        }
      }
    });
    cbxLayout.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        cbxAlert.setChecked(!cbxAlert.isChecked());
      }
    });
    rltLayout = view.findViewById(R.id.rlt_when);
    rltLayout.setEnabled(cbxAlert.isChecked());
    rltLayout.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        if (dialogWhen != null) {
          dialogWhen.show();
        }
      }
    });
    txtWhen = (TextView) view.findViewById(R.id.txt_when);
    txtWhenTitle = (TextView) view.findViewById(R.id.txt_when_title);
    if (cbxAlert.isChecked()) {
      txtWhen.setTextColor(getResources().getColor(R.color.color_text_default));
      txtWhenTitle.setTextColor(getResources().getColor(
          R.color.color_text_default));
    } else {
      txtWhen.setTextColor(getResources().getColor(R.color.color_hint_bold));
      txtWhenTitle.setTextColor(getResources().getColor(
          R.color.color_hint_bold));
    }
    initDialogs();
  }

  private void initDialogs() {
    mValueWhenStrArray = getResources().getStringArray(
        R.array.online_alert_array);
    AlertDialog.Builder dialogMoreBuilder = new AlertDialog.Builder(
        getActivity());
    dialogMoreBuilder.setItems(R.array.online_alert_array,
        new DialogInterface.OnClickListener() {

          @Override
          public void onClick(DialogInterface dialog, int which) {
            switch (which) {
              case 0:
              default:
                valueWhen = Constants.MANAGE_ONLINE_EVERY_TIME;
                break;
              case 1:
                valueWhen = Constants.MANAGE_ONLINE_MAX_TEN;
                break;
              case 2:
                valueWhen = Constants.MANAGE_ONLINE_MAX_FIVE;
                break;
              case 3:
                valueWhen = Constants.MANAGE_ONLINE_ONCE_PER_DAY;
                break;
            }
            strWhen = mValueWhenStrArray[which];
            txtWhen.setText(strWhen);
            dialogWhen.dismiss();
          }
        });
    dialogWhen = dialogMoreBuilder.create();
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    BaseFragmentActivity baseFragmentActivity = (BaseFragmentActivity) activity;
    baseFragmentActivity.setOnNavigationClickListener(this);
  }

  private void requestGetOnlineAlert() {
    String token = UserPreferences.getInstance().getToken();
    GetOnlineAlertRequest request = new GetOnlineAlertRequest(token,
        mUserId);
    restartRequestServer(LOADER_GET_ONLINE_ALERT, request);
  }

  private void requestAddOnlineAlert() {
    String token = UserPreferences.getInstance().getToken();
    if (cbxAlert.isChecked()) {
      isAlert = ALERT_YES;
    } else {
      isAlert = ALERT_NO;
    }
    int num = valueWhen;
    AddOnlineAlertRequest onlineAlertRequest = new AddOnlineAlertRequest(
        token, mUserId, isAlert, num);
    restartRequestServer(LOADER_ADD_ONLINE_ALERT, onlineAlertRequest);
  }

  private void onResponseGetOnlineAlert(GetOnlineAlertResponse response) {

    cbxAlert.setChecked(response.getIs_alt() != ALERT_NO);
    valueWhen = response.getAltNumber();
    switch (response.getAltNumber()) {
      case Constants.MANAGE_ONLINE_EVERY_TIME:
        strWhen = mValueWhenStrArray[0];
        break;
      case Constants.MANAGE_ONLINE_MAX_TEN:
        strWhen = mValueWhenStrArray[1];
        break;
      case Constants.MANAGE_ONLINE_MAX_FIVE:
        strWhen = mValueWhenStrArray[2];
        break;
      case Constants.MANAGE_ONLINE_ONCE_PER_DAY:
        strWhen = mValueWhenStrArray[3];
        break;
      default:
        strWhen = "";
        break;
    }
    txtWhen.setText(strWhen);
  }

  private void onResponseAddOnlineAlert() {
    getLoaderManager().destroyLoader(LOADER_ADD_ONLINE_ALERT);
    LayoutInflater inflater = LayoutInflater.from(getActivity());
    View customTitle = inflater.inflate(R.layout.dialog_customize, null);
    AlertDialog.Builder builder = new CenterButtonDialogBuilder(getActivity(), false);
    String title = "";
    String strFormat = "";
    String message = "";
    title = getResources().getString(
        R.string.fragment_manage_online_alert_confirm_title);
    if (!cbxAlert.isChecked()) {
      strFormat = getResources()
          .getString(
              R.string.fragment_manage_online_alert_confirm_content_never);
      message = String.format(strFormat, mUserName);
    } else {
      String strResource = getResources()
          .getString(
              R.string.fragment_manage_online_alert_confirm_content_other);
      message = String.format(strResource, strWhen, mUserName);
    }
    ((TextView) customTitle.findViewById(R.id.tv_title_dialog_customize)).setText(title);
    builder.setCustomTitle(customTitle);
    //builder.setTitle(title);
    builder.setMessage(message);
    DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        getLoaderManager().destroyLoader(LOADER_ADD_ONLINE_ALERT);
        Handler handler = new Handler();
        handler.post(new Runnable() {
          @Override
          public void run() {
            notifyBackMyProfile();
            mNavigationManager.goBack();
          }
        });
      }
    };
    builder.setPositiveButton(R.string.common_ok, clickListener);
    builder.setCancelable(false);
    dialogAddOnlineAlert = builder.create();
    dialogAddOnlineAlert.show();
    int dividerId = dialogAddOnlineAlert.getContext().getResources()
        .getIdentifier("android:id/titleDivider", null, null);
    View divider = dialogAddOnlineAlert.findViewById(dividerId);
    if (divider != null) {
      divider.setBackgroundColor(getResources().getColor(R.color.transparent));
    }
  }

  private void notifyBackMyProfile() {
    if (getTargetFragment() != null && (getTargetFragment() instanceof MyProfileFragment)) {
      getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, null);
    }
  }

  public void onRightNaviButtonClicked() {
    requestAddOnlineAlert();
  }

  @Override
  public void startRequest(int loaderId) {
    if (mProgressDialog == null) {
      mProgressDialog = ProgressDialog.show(getActivity(), "",
          getString(R.string.waiting), true, false);
      mProgressDialog.setCanceledOnTouchOutside(false);
    }
    if (!mProgressDialog.isShowing()) {
      mProgressDialog.show();
    }
  }

  @Override
  public void receiveResponse(Loader<Response> loader, Response response) {
    if (getActivity() == null) {
      return;
    }
    if (mProgressDialog != null && mProgressDialog.isShowing()) {
      mProgressDialog.dismiss();
    }
    if (response.getCode() == Response.SERVER_SUCCESS) {
      int loaderId = loader.getId();
      switch (loaderId) {
        case LOADER_ADD_ONLINE_ALERT:
          onResponseAddOnlineAlert();
          break;
        case LOADER_GET_ONLINE_ALERT:
          onResponseGetOnlineAlert((GetOnlineAlertResponse) response);
          break;
        default:
          break;
      }
    } else {
      com.application.ui.customeview.ErrorApiDialog.showAlert(
          getActivity(), R.string.common_error, response.getCode(),
          new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
          }, false);

    }
  }

  @Override
  public Response parseResponse(int loaderID, ResponseData data,
      int requestType) {
    Response response = null;
    switch (loaderID) {
      case LOADER_ADD_ONLINE_ALERT:
        response = new AddOnlineAlertResponse(data);
        break;
      case LOADER_GET_ONLINE_ALERT:
        response = new GetOnlineAlertResponse(data);
        break;
    }
    return response;
  }

  @Override
  public void onBaseLoaderReset(Loader<Response> loader) {

  }

  @Override
  public void onStop() {
    super.onStop();
    if (mProgressDialog != null && mProgressDialog.isShowing()) {
      mProgressDialog.dismiss();
    }
    if (dialogWhen != null && dialogWhen.isShowing()) {
      dialogWhen.dismiss();
    }
  }
}