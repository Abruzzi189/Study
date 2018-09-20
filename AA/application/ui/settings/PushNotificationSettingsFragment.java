package com.application.ui.settings;

import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.application.connection.ResponseReceiver;
import com.application.connection.request.GetNotificationSettingRequest;
import com.application.connection.request.SaveNotificationSettingRequest;
import com.application.connection.response.GetNotificationSettingResponse;
import com.application.connection.response.SaveNotificationSettingResponse;
import com.application.ui.BaseFragment;
import com.application.ui.CenterButtonDialogBuilder;
import com.application.ui.customeview.AutofitTextView;
import com.application.ui.customeview.ErrorApiDialog;
import com.application.util.preferece.FavouritedPrefers;
import com.application.util.preferece.FriendPrefers;
import com.application.util.preferece.UserPreferences;
import glas.bbsystem.R;

/**
 * Receiver push from Server (alert) Receiver push from postTimeline (buzz) Receiver push from
 * msgChat (nothing, all, only fav)
 */

public class PushNotificationSettingsFragment extends BaseFragment implements
    OnCheckedChangeListener, ResponseReceiver {

  private static final int LOADER_SAVE_NOTI_SETTING = 100;
  private static final int LOADER_GET_NOTI_SETTING = 101;

  private static final String KEY_BUZZ_SERVER = "KEY_BUZZ_SERVER";
  private static final String KEY_CHECKOUT_SERVER = "KEY_CHECKOUT_SERVER";
  private static final String KEY_ALERT_SERVER = "KEY_ALERT_SERVER";
  private static final String KEY_CHAT_SERVER = "KEY_CHAT_SERVER";

  private static final String KEY_BUZZ_CLIENT = "KEY_BUZZ_CLIENT";
  private static final String KEY_CHECKOUT_CLIENT = "KEY_CHECKOUT_CLIENT";
  private static final String KEY_ALERT_CLIENT = "KEY_ALERT_CLIENT";
  private static final String KEY_CHAT_MESSAGE_CLIENT = "KEY_CHAT_CLIENT";

  private CheckBox mCkbBuzz;
  private CheckBox mCkbAlert;
  private ProgressDialog mProgressDialog;
  private TextView mtxtChatMessage;

  private int mBuzzServer;
  private int mCheckoutServer;
  private int mAlertServer;
  private int mChatMessageServer;

  private int mBuzzClient;
  private int mCheckoutClient;
  private int mAlertClient;
  private int mChatMessageClient;
  private ArrayAdapter<String> mOptions;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mOptions = new ArrayAdapter<String>(getActivity(),
        android.R.layout.select_dialog_singlechoice, getResources()
        .getStringArray(R.array.option_notify));

    restoreState(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_push_notification_settings, container, false);
    initView(view);
    return view;
  }

  private void restoreState(Bundle bundle) {
    if (bundle == null) {
      return;
    }
    mBuzzServer = bundle.getInt(KEY_BUZZ_SERVER);
    mAlertServer = bundle.getInt(KEY_ALERT_SERVER);
    mCheckoutServer = bundle.getInt(KEY_CHECKOUT_SERVER);
    mChatMessageServer = bundle.getInt(KEY_CHAT_SERVER);

    mBuzzClient = bundle.getInt(KEY_BUZZ_CLIENT);
    mAlertClient = bundle.getInt(KEY_ALERT_CLIENT);
    mCheckoutClient = bundle.getInt(KEY_CHECKOUT_CLIENT);
    mChatMessageClient = bundle.getInt(KEY_CHAT_MESSAGE_CLIENT);
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putInt(KEY_ALERT_SERVER, mAlertServer);
    outState.putInt(KEY_BUZZ_SERVER, mBuzzServer);
    outState.putInt(KEY_CHECKOUT_SERVER, mCheckoutServer);
    outState.putInt(KEY_CHAT_SERVER, mChatMessageServer);

    outState.putInt(KEY_ALERT_CLIENT, mAlertClient);
    outState.putInt(KEY_CHECKOUT_CLIENT, mCheckoutClient);
    outState.putInt(KEY_BUZZ_CLIENT, mBuzzClient);
    outState.putInt(KEY_CHAT_MESSAGE_CLIENT, mChatMessageClient);
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    getNotificationSetting();
  }

  @Override
  protected void resetNavigationBar() {
    super.resetNavigationBar();
    getNavigationBar().setNavigationLeftLogo(R.drawable.ic_action_navigation_arrow_back);
    getNavigationBar().setNavigationRightTitle(R.string.common_save);
  }

  private void initView(View view) {
    // open dialog choose msgChat push
    view.findViewById(R.id.fragment_notification_setting_ll_chatmessage)
        .setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            showPickerOptionChatMessage();
          }
        });

    mCkbAlert = (CheckBox) view.findViewById(R.id.fragment_notification_setting_chb_alert);
    mCkbBuzz = (CheckBox) view.findViewById(R.id.fragment_notification_setting_chb_buzz);
    mtxtChatMessage = (TextView) view
        .findViewById(R.id.fragment_notification_setting_txt_chatmessage);
    AutofitTextView mFitTextView = (AutofitTextView) view
        .findViewById(R.id.settings_chat_description);
    mFitTextView.setSizeToFit(true);
    mCkbAlert.setOnCheckedChangeListener(this);
    mCkbBuzz.setOnCheckedChangeListener(this);
  }

  private void getNotificationSetting() {
    String token = UserPreferences.getInstance().getToken();
    if (token == null || "".equals(token)) {
      return;
    }
    GetNotificationSettingRequest request = new GetNotificationSettingRequest(token);
    restartRequestServer(LOADER_GET_NOTI_SETTING, request);
  }

  @Override
  public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    switch (buttonView.getId()) {
      case R.id.fragment_notification_setting_chb_alert:
        mAlertClient = getValueWhenCheckBoxClicked(isChecked);
        break;
      case R.id.fragment_notification_setting_chb_buzz:
        mBuzzClient = getValueWhenCheckBoxClicked(isChecked);
        break;
      default:
        break;
    }
  }

  private int getValueWhenCheckBoxClicked(boolean isChecked) {
    if (isChecked) {
      return 1;
    } else {
      return 0;
    }
  }

  /**
   * Use when receive from server
   */
  private void setValueFromServer(int chat, int alert, int buzz,
      int checkout, String[] favList, String[] FriendList) {
    mAlertServer = alert;
    mBuzzServer = buzz;
    mCheckoutServer = checkout;
    mChatMessageServer = chat;

    mAlertClient = alert;
    mBuzzClient = buzz;
    mCheckoutClient = checkout;
    setVaueForChatMessageClient(chat);

    setStateForCheckBox(mCkbAlert, alert);
    setStateForCheckBox(mCkbBuzz, buzz);

    try {
      FavouritedPrefers favouritedPrefers = FavouritedPrefers.getInstance();
      favouritedPrefers.clearAll();
      favouritedPrefers.saveFavs(favList);

      FriendPrefers friendPrefers = new FriendPrefers();
      friendPrefers.cleverAll();
      friendPrefers.saveFriends(FriendList);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void setStateForCheckBox(CheckBox box, int value) {
    if (box == null) {
      return;
    }
    if (value == 0) {
      box.setChecked(false);
    } else {
      box.setChecked(true);
    }

  }

  private void showPickerOptionChatMessage() {
    Builder builder = new CenterButtonDialogBuilder(getActivity(), false);
    int index = getPositionInAdapter(mChatMessageClient);
    builder.setSingleChoiceItems(mOptions, index,
        new DialogInterface.OnClickListener() {

          @Override
          public void onClick(DialogInterface dialog, int which) {
            setVaueForChatMessageClient(getValueInServer(which));
          }
        });
    builder.setPositiveButton(R.string.ok, null);
    builder.setCancelable(false);
    builder.show();
  }

  private void setVaueForChatMessageClient(int value) {
    mChatMessageClient = value;
    int index = getPositionInAdapter(value);
    mtxtChatMessage.setText(mOptions.getItem(index));
  }

  private int getPositionInAdapter(int value) {
    return value + 1;
  }

  private int getValueInServer(int position) {
    return position - 1;
  }

  private boolean isUpdateToServer() {
    if (mAlertClient != mAlertServer || mBuzzClient != mBuzzServer
        || mCheckoutClient != mCheckoutServer
        || mChatMessageClient != mChatMessageServer) {
      return true;
    }
    return false;
  }

  private void savePreferencesToServer(int chatmsg, int alert, int buzz, int checkout) {
    String token = UserPreferences.getInstance().getToken();
    if (token == null || "".equals(token)) {
      return;
    }
    SaveNotificationSettingRequest request = new SaveNotificationSettingRequest(
        token, buzz, alert, chatmsg, checkout);
    restartRequestServer(LOADER_SAVE_NOTI_SETTING, request);
  }

  private void handleSavePreferencseSuccess() {
    // save to reference to check when notify
    UserPreferences.getInstance().saveChatNotificationType(mChatMessageClient);

    Runnable runnable = new Runnable() {
      @Override
      public void run() {
        mNavigationManager.goBack();
      }
    };
    Handler handler = new Handler();
    handler.post(runnable);
  }

  @Override
  public void startRequest(int loaderId) {
    switch (loaderId) {
      case LOADER_SAVE_NOTI_SETTING:
        mProgressDialog = ProgressDialog.show(getActivity(), "",
            getActivity().getString(R.string.saving_preferences),
            true, false);
        break;
      case LOADER_GET_NOTI_SETTING:
        mProgressDialog = ProgressDialog.show(getActivity(), "",
            getString(R.string.loading), true, false);
        break;

      default:
        break;
    }
  }

  @Override
  public void receiveResponse(Loader<Response> loader, Response response) {
    if (getActivity() == null || response == null) {
      return;
    }
    if (mProgressDialog != null) {
      mProgressDialog.dismiss();
    }
    getLoaderManager().destroyLoader(loader.getId());
    if (response.getCode() == Response.SERVER_SUCCESS) {
      switch (loader.getId()) {
        case LOADER_SAVE_NOTI_SETTING:
          handleSavePreferencseSuccess();
          break;
        case LOADER_GET_NOTI_SETTING:
          GetNotificationSettingResponse getResponse = (GetNotificationSettingResponse) response;
          setValueFromServer(getResponse.getNotifyChat(),
              getResponse.getNotifyAlert(),
              getResponse.getNotifyBuzz(),
              getResponse.getNotifyCheckout(),
              getResponse.getFavList(),
              getResponse.getFriendList());
          break;

        default:
          break;
      }
    } else {
      ErrorApiDialog.showAlert(getActivity(), R.string.common_error,
          response.getCode());
    }
  }

  @Override
  public Response parseResponse(int loaderID, ResponseData data,
      int requestType) {
    if (loaderID == LOADER_SAVE_NOTI_SETTING) {
      return new SaveNotificationSettingResponse(data);
    } else if (loaderID == LOADER_GET_NOTI_SETTING) {
      return new GetNotificationSettingResponse(data);
    }
    return null;
  }

  @Override
  public void onBaseLoaderReset(Loader<Response> loader) {
  }

  @Override
  public void onNavigationLeftClick(View view) {
    super.onNavigationLeftClick(view);
  }

  @Override
  public void onNavigationRightClick(View view) {
    super.onNavigationRightClick(view);
    onSave();
  }

  public void onSave() {
    if (isUpdateToServer()) {
      savePreferencesToServer(mChatMessageClient, mAlertClient, mBuzzClient, mCheckoutClient);
    } else {
      handleSavePreferencseSuccess();
    }
  }
}
