package com.application.ui.customeview;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.application.common.webview.WebViewActivity;
import com.application.common.webview.WebViewFragment;
import com.application.constant.Constants;
import com.application.constant.UserSetting;
import com.application.ui.CenterButtonDialogBuilder;
import com.application.ui.MainActivity;
import com.application.ui.backstage.ManageBackstageActivity;
import com.application.util.LogUtils;
import com.application.util.preferece.Preferences;
import com.application.util.preferece.UserPreferences;
import glas.bbsystem.R;


public class NotEnoughPointDialog {

  private static final String TAG = "NotEnoughPointDialog";
  private static Dialog dialog;

  public static void showForChat(Activity activity) {
    if (activity == null) {
      LogUtils.w(TAG, "Activity is null");
      return;
    }

    Preferences preferences = Preferences.getInstance();
    UserPreferences userPreferences = UserPreferences.getInstance();
    if (userPreferences.getGender() == UserSetting.GENDER_FEMALE) {
      return;
    }

    int payPoint = preferences.getChatPoint();
    String format = activity.getString(R.string.not_enough_point_msg_chat);
    String msg = String.format(format, String.valueOf(payPoint));

    showNotEnoughPointDialog(activity, msg, Constants.PACKAGE_POINT_CHAT);
  }

  public static void showForVoiceCall(Activity activity, int payPoint) {
    if (activity == null) {
      LogUtils.w(TAG, "Activity is null");
      return;
    }

    UserPreferences preferences = UserPreferences.getInstance();
    if (preferences.getGender() == UserSetting.GENDER_FEMALE) {
      return;
    }

    String format = activity
        .getString(R.string.not_enough_point_msg_voice_call);
    String msg = String.format(format, String.valueOf(payPoint));

    showNotEnoughPointDialog(activity, msg, Constants.PACKAGE_POINT_VOICE_CALL);
  }

  public static void showForVideoCall(Activity activity, int payPoint) {
    if (activity == null) {
      LogUtils.w(TAG, "Activity is null");
      return;
    }

    UserPreferences preferences = UserPreferences.getInstance();
    if (preferences.getGender() == UserSetting.GENDER_FEMALE) {
      return;
    }

    String format = activity
        .getString(R.string.not_enough_point_msg_video_call);
    String msg = String.format(format, String.valueOf(payPoint));

    showNotEnoughPointDialog(activity, msg, Constants.PACKAGE_POINT_VIDEO_CALL);
  }

  public static void showForCallRecever(Activity activity) {
    if (activity == null) {
      LogUtils.w(TAG, "Activity is null");
      return;
    }

    String msg = activity
        .getString(R.string.not_enough_point_msg_call_receiver);

    showNotEnoughPointAlert(activity, msg);
  }

  public static void showForCommentBuzz(Activity activity, int point) {
    if (activity == null) {
      LogUtils.w(TAG, "Activity is null");
      return;
    }

    UserPreferences preferences = UserPreferences.getInstance();
    if (preferences.getGender() == UserSetting.GENDER_FEMALE) {
      return;
    }

    String format = activity
        .getString(R.string.not_enough_point_msg_comment_buzz);
    String msg = String.format(format, String.valueOf(point));

    showNotEnoughPointDialog(activity, msg, Constants.PACKAGE_POINT_COMMENT);
  }

  public static void showForReply(Activity activity, int point) {
    if (activity == null) {
      LogUtils.w(TAG, "Activity is null");
      return;
    }

    UserPreferences preferences = UserPreferences.getInstance();
    if (preferences.getGender() == UserSetting.GENDER_FEMALE) {
      return;
    }

    String format = activity
        .getString(R.string.not_enough_point_msg_sub_comment_buzz);
    String msg = String.format(format, String.valueOf(point));

    showNotEnoughPointDialog(activity, msg, Constants.PACKAGE_POINT_SUB_COMMENT);
  }

  public static void showForGiveGift(Activity activity, int payPoint) {
    if (activity == null) {
      LogUtils.w(TAG, "Activity is null");
      return;
    }

    String msg = "";
    UserPreferences preferences = UserPreferences.getInstance();
    int gender = preferences.getGender();
    if (gender == UserSetting.GENDER_MALE) {
      String format = activity
          .getString(R.string.not_enough_point_msg_send_gift_male);
      msg = String.format(format, String.valueOf(payPoint));
      showNotEnoughPointDialog(activity, msg, Constants.PACKAGE_POINT_GIFT);
    } else {
      String format = activity
          .getString(R.string.not_enough_point_msg_send_gift_female);
      int currentPoint = preferences.getNumberPoint();
      msg = String.format(format, String.valueOf(payPoint),
          String.valueOf(currentPoint));
      showNotEnoughPointDialogForFemale(activity, msg, Constants.PACKAGE_POINT_GIFT);
    }
  }

  public static void showForSaveChatPicture(Activity activity, int payPoint) {
    if (activity == null) {
      LogUtils.w(TAG, "Activity is null");
      return;
    }
    String format = activity
        .getString(R.string.not_enough_point_msg_save_chat_pic);
    int currentPoint = UserPreferences.getInstance().getNumberPoint();
    String msg = String.format(format, String.valueOf(payPoint),
        String.valueOf(currentPoint));

    showNotEnoughPointDialog(activity, msg, Constants.PACKAGE_DEFAULT);
  }

  public static void showForSavePicture(Activity activity, int payPoint) {
    if (activity == null) {
      LogUtils.w(TAG, "Activity is null");
      return;
    }
    String format = activity
        .getString(R.string.not_enough_point_msg_save_pic);
    int currentPoint = UserPreferences.getInstance().getNumberPoint();
    String msg = String.format(format, String.valueOf(payPoint),
        String.valueOf(currentPoint));

    showNotEnoughPointDialog(activity, msg, Constants.PACKAGE_DEFAULT);
  }

  public static void showForWinkBomb(Activity activity, int payPoint,
      int winkNum, int currentPoint) {
    if (activity == null) {
      LogUtils.w(TAG, "Activity is null");
      return;
    }
    String format = activity
        .getString(R.string.not_enough_point_msg_wink_bomb);
    String msg = String.format(format, String.valueOf(payPoint),
        String.valueOf(winkNum), String.valueOf(currentPoint));

    showNotEnoughPointDialog(activity, msg, Constants.PACKAGE_DEFAULT);
  }

  public static void showForUnlockBackstage(Activity activity, int payPoint) {
    if (activity == null) {
      LogUtils.w(TAG, "Activity is null");
      return;
    }
    String format = activity
        .getString(R.string.not_enough_point_msg_unlock_backstage);
    String msg = String.format(format, String.valueOf(payPoint));

    showNotEnoughPointDialog(activity, msg, Constants.PACKAGE_DEFAULT);
  }

  public static void showForUnlockViewImage(Activity activity, int price,
      int currentPoint) {
    if (activity == null) {
      LogUtils.w(TAG, "Activity is null");
      return;
    }
    String format = activity
        .getString(R.string.not_enough_point_msg_unlock_image);
    String msg = String.format(format, String.valueOf(price),
        String.valueOf(currentPoint));

    showNotEnoughPointDialog(activity, msg, Constants.PACKAGE_DEFAULT);
  }

  public static void showForUnlockListenAudio(Activity activity, int price,
      int currentPoint) {
    if (activity == null) {
      LogUtils.w(TAG, "Activity is null");
      return;
    }
    String format = activity
        .getString(R.string.not_enough_point_msg_unlock_voice);
    String msg = String.format(format, String.valueOf(price),
        String.valueOf(currentPoint));

    showNotEnoughPointDialog(activity, msg, Constants.PACKAGE_DEFAULT);
  }

  public static void showForUnlockWatchVideo(Activity activity, int price,
      int currentPoint) {
    if (activity == null) {
      LogUtils.w(TAG, "Activity is null");
      return;
    }
    String format = activity
        .getString(R.string.not_enough_point_msg_unlock_video);
    String msg = String.format(format, String.valueOf(price),
        String.valueOf(currentPoint));

    showNotEnoughPointDialog(activity, msg, Constants.PACKAGE_DEFAULT);
  }

  private synchronized static void showNotEnoughPointDialog(
      final Activity activity, String message, final int actionType) {
    if (dialog == null || !dialog.isShowing()) {
      LayoutInflater inflater = LayoutInflater.from(activity);
      View customTitle = inflater.inflate(R.layout.dialog_customize, null);
      Builder builder = new CenterButtonDialogBuilder(activity, true);
      builder.setCancelable(true);
      // Setting dialog title
      ((TextView) customTitle.findViewById(R.id.tv_title_dialog_customize))
          .setText(R.string.not_enough_point_title);
      builder.setCustomTitle(customTitle);
      //builder.setTitle(R.string.not_enough_point_title);

      // Setting dialog message
      builder.setMessage(message);

      // Setting button positive
      builder.setPositiveButton(R.string.not_enough_point_positive,
          new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              if (activity instanceof ManageBackstageActivity) {
                activity.finish();
              }

              if (activity instanceof MainActivity) {
                Fragment fragment = WebViewFragment
                    .newInstance(WebViewFragment.PAGE_TYPE_BUY_PONIT);
                ((MainActivity) activity)
                    .getNavigationManager().addPage(
                    fragment);
//								Intent intent = new Intent(activity, BuyPointDialogActivity.class);
//								intent.putExtra(BuyPointActivity.PARAM_ACTION_TYPE, actionType);
//								activity.startActivity(intent);
              } else {
                Intent intent = new Intent(activity,
                    WebViewActivity.class);
                intent.putExtra(
                    WebViewFragment.INTENT_PAGE_TYPE,
                    WebViewFragment.PAGE_TYPE_BUY_PONIT);
                activity.startActivity(intent);
              }
            }
          });

      // Setting button negative
      builder.setNegativeButton(R.string.not_enough_point_negative,
          new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              dialog.dismiss();
            }
          });

      // Show dialog
      dialog = builder.create();
      dialog.show();
      int dividerId = dialog.getContext().getResources()
          .getIdentifier("android:id/titleDivider", null, null);
      View divider = dialog.findViewById(dividerId);
      if (divider != null) {
        divider
            .setBackgroundColor(dialog.getContext().getResources().getColor(R.color.transparent));
      }
    }
  }
  private synchronized static void showNotEnoughPointDialogForFemale(
      final Activity activity, String message, final int actionType) {
    if (dialog == null || !dialog.isShowing()) {
      LayoutInflater inflater = LayoutInflater.from(activity);
      View customTitle = inflater.inflate(R.layout.dialog_customize, null);
      Builder builder = new CenterButtonDialogBuilder(activity, true);
      builder.setCancelable(true);
      // Setting dialog title
      ((TextView) customTitle.findViewById(R.id.tv_title_dialog_customize))
          .setText(R.string.not_enough_point_title);
      builder.setCustomTitle(customTitle);
      builder.setMessage(message);
      builder.setNegativeButton(R.string.common_ok,
          new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              dialog.dismiss();
            }
          });

      // Show dialog
      dialog = builder.create();
      dialog.show();
      int dividerId = dialog.getContext().getResources()
          .getIdentifier("android:id/titleDivider", null, null);
      View divider = dialog.findViewById(dividerId);
      if (divider != null) {
        divider
            .setBackgroundColor(dialog.getContext().getResources().getColor(R.color.transparent));
      }
    }
  }
  private synchronized static void showNotEnoughPointAlert(
      final Activity activity, String message) {
    if (dialog == null || !dialog.isShowing()) {
      LayoutInflater inflater = LayoutInflater.from(activity);
      View customTitle = inflater.inflate(R.layout.dialog_customize, null);
      Builder builder = new CenterButtonDialogBuilder(activity, false);
      builder.setCancelable(true);
      // Setting dialog title
      ((TextView) customTitle.findViewById(R.id.tv_title_dialog_customize))
          .setText(R.string.not_enough_point_title);
      builder.setCustomTitle(customTitle);
      //builder.setTitle(R.string.not_enough_point_title);

      // Setting dialog message
      builder.setMessage(message);

      // Setting button positive
      builder.setPositiveButton(R.string.common_ok,
          new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              dialog.dismiss();
            }
          });

      // Show dialog
      dialog = builder.create();
      dialog.show();
      int dividerId = dialog.getContext().getResources()
          .getIdentifier("android:id/titleDivider", null, null);
      View divider = dialog.findViewById(dividerId);
      if (divider != null) {
        divider
            .setBackgroundColor(dialog.getContext().getResources().getColor(R.color.transparent));
      }
    }
  }
}