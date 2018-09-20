package com.application.actionbar.popup;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.application.actionbar.popup.controllers.IPopupRequestData;
import com.application.actionbar.popup.controllers.IRequestClass;
import com.application.connection.request.AddBlockUserRequest;
import com.application.connection.request.AddFavoriteRequest;
import com.application.connection.request.GetBasicInfoRequest;
import com.application.connection.request.RemoveFavoriteRequest;
import com.application.connection.request.ReportRequest;
import com.application.constant.Constants;
import com.application.entity.CallUserInfo;
import com.application.model.ChatUser;
import com.application.ui.BaseFragment;
import com.application.ui.MainActivity;
import com.application.ui.MeetPeopleFragment;
import com.application.ui.customeview.CustomConfirmDialog;
import com.application.util.LogUtils;
import com.application.util.Utility;
import com.application.util.preferece.UserPreferences;
import glas.bbsystem.R;


/**
 * Created by namit on 4/8/2016.
 */
public class RequestFragmentData implements IPopupRequestData {

  private final String TAG = RequestFragmentData.class.getName();

  private Fragment fragment;
  private Context context;
  private MainActivity activity;
  private android.app.AlertDialog mAlertDialog;
  private ChatUser mFriend;
  private IRequestClass onRequestClass;

  public RequestFragmentData(Fragment fragment,
      Context context,
      MainActivity activity,
      ChatUser mFriend,
      android.app.AlertDialog mAlertDialog,
      IRequestClass onRequestClass) {
    this.fragment = fragment;
    this.context = context;
    this.activity = activity;
    this.mFriend = mFriend;
    this.mAlertDialog = mAlertDialog;
    this.onRequestClass = onRequestClass;
  }

  @Override
  public void onExecuteReportUser() throws Exception {
    LogUtils.d(TAG, "executeReportUser Started");

    if (UserPreferences.getInstance().getInRecordingProcess()) {
      LogUtils.d(TAG, "executeReportUser Ended (1)");
      return;
    }

    LayoutInflater inflater = LayoutInflater.from(activity.getApplication());
    View customTitle = inflater.inflate(R.layout.dialog_customize, null);

    Resources resource = context.getResources();
    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
    String title = "";
    String[] items = null;

    title = resource.getString(R.string.dialog_confirm_report_user_title);
    items = resource.getStringArray(R.array.report_user_type);

    // builder.setTitle(title);

    ((TextView) customTitle.findViewById(R.id.tv_title_dialog_customize)).setText(title);
    builder.setCustomTitle(customTitle);

    ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
        android.R.layout.select_dialog_item, items);

    builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        LogUtils.d(TAG, "onClick Started");

        LogUtils.d(TAG,
            String.format("executeReportUser: which = %d", which));

        if (which > 0) {
          int reportType = 0;
          Resources resource = context.getResources();
          String[] reportTypes = resource
              .getStringArray(R.array.report_type);
          String[] reportUsers = resource
              .getStringArray(R.array.report_user_type);
          String reportString = reportUsers[which];
          int length = reportTypes.length;
          for (int i = 0; i < length; i++) {
            if (reportString.equals(reportTypes[i])) {
              reportType = i;
            }
          }
          String token = UserPreferences.getInstance().getToken();
          String subject_id = UserPreferences.getInstance()
              .getCurentFriendChat();
          ReportRequest reportRequest = new ReportRequest(token,
              subject_id, reportType, Constants.REPORT_TYPE_USER);
          ((BaseFragment) fragment).restartRequestServer(LOADER_ID_REPORT_USER, reportRequest);
        }

        LogUtils.d(TAG, "onClick Ended");
      }
    });

    mAlertDialog = builder.create();
    mAlertDialog.show();

    int dividerId = mAlertDialog.getContext().getResources()
        .getIdentifier("android:id/titleDivider", null, null);
    View divider = mAlertDialog.findViewById(dividerId);
    if (divider != null) {
      divider.setBackgroundColor(context.getResources().getColor(R.color.transparent));
    }

    LogUtils.d(TAG, "executeReportUser Ended (2)");
  }

  @Override
  public void OnExecuteRemoveFromFavorites() throws Exception {
    String token = UserPreferences.getInstance().getToken();
    RemoveFavoriteRequest removeFavoriteRequest = new RemoveFavoriteRequest(
        token, mFriend.getId());
    ((BaseFragment) fragment).restartRequestServer(LOADER_ID_REMOVE_FROM_FAVORITES,
        removeFavoriteRequest);
  }

  @Override
  public void OnExecuteAddToFavorites() throws Exception {
    String token = UserPreferences.getInstance().getToken();
    AddFavoriteRequest addFavoriteRequest = new AddFavoriteRequest(token,
        mFriend.getId());
    ((BaseFragment) fragment).restartRequestServer(LOADER_ID_ADD_TO_FAVORITES, addFavoriteRequest);
  }

  @Override
  public void onExecuteVoiceCall() throws Exception {
    if (Utility.isBlockedWithUser(context, mFriend.getId())) {
      OnExecuteBlockUser();
      return;
    }

    CallUserInfo userInfo = new CallUserInfo(mFriend.getName(),
        mFriend.getId(), mFriend.getAvatar(), mFriend.getGender());
    onRequestClass.setCallUserInfo(userInfo);

    onRequestClass.setCurrentCallType(Constants.CALL_TYPE_VOICE);
    Utility.showDialogAskingVoiceCall(activity, userInfo,
        new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
            try {
              restartRequestBasicUserInfo();
            } catch (Exception e) {
              e.printStackTrace();
            }
          }
        });
  }

  @Override
  public void onExitMeWhenBlocked() throws Exception {
    Runnable run = new Runnable() {
      @Override
      public void run() {
        // Navigate to Meet People screen
        if (context != null) {
          ((MainActivity) activity).replaceAllFragment(
              new MeetPeopleFragment(),
              MainActivity.TAG_FRAGMENT_MEETPEOPLE);
        }
      }
    };

    Handler handler = new Handler();
    handler.post(run);
  }

  @Override
  public void OnExecuteBlockUser() throws Exception {
    LogUtils.d(TAG, "executeBlockUser Started");

    UserPreferences userPreferences = UserPreferences.getInstance();
    if (userPreferences.getInRecordingProcess()) {
      LogUtils.d(TAG, "executeBlockUser Ended (1)");
      return;
    }

    String title = "";
    String message = "";

    title = context.getString(R.string.chat_screen_block_dialog_title);
    message = String.format(
        context.getString(R.string.chat_screen_block_dialog_message),
        mFriend.getName());
    AlertDialog mConfirmDialog = new CustomConfirmDialog(activity, title, message, true)
        .setPositiveButton(0, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            UserPreferences userPreferences = UserPreferences.getInstance();
            String token = userPreferences.getToken();
            String userToSend = userPreferences.getCurentFriendChat();
            AddBlockUserRequest abur = new AddBlockUserRequest(token,
                userToSend);
            ((BaseFragment) fragment).restartRequestServer(LOADER_ID_ADD_BLOCK_USER, abur);
          }
        })
        .create();
    mConfirmDialog.show();

    int dividerId = mConfirmDialog.getContext().getResources()
        .getIdentifier("android:id/titleDivider", null, null);
    View divider = mConfirmDialog.findViewById(dividerId);
    if (divider != null) {
      divider.setBackgroundColor(
          mConfirmDialog.getContext().getResources().getColor(R.color.transparent));
    }
    LogUtils.d(TAG, "executeBlockUser Ended (2)");
  }


  private void restartRequestBasicUserInfo() throws Exception {
    UserPreferences preferences = UserPreferences.getInstance();
    String token = preferences.getToken();
    GetBasicInfoRequest request = new GetBasicInfoRequest(token,
        onRequestClass.getCallInfo().getUserId());
    ((BaseFragment) fragment).restartRequestServer(LOADER_ID_BASIC_USER_INFO_CALL, request);
  }
}
