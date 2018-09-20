package com.application.actionbar.popup;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import com.application.actionbar.popup.controllers.IRequestClass;
import com.application.actionbar.popup.controllers.OnPopupListener;
import com.application.connection.request.GetBasicInfoRequest;
import com.application.constant.Constants;
import com.application.entity.CallUserInfo;
import com.application.entity.GiftCategories;
import com.application.ui.BaseFragment;
import com.application.ui.chat.ChatMoreLayout;
import com.application.ui.chat.OnChatMoreListener;
import com.application.ui.gift.ChooseGiftToSend;
import com.application.util.Utility;
import com.application.util.preferece.FavouritedPrefers;
import com.application.util.preferece.UserPreferences;
import glas.bbsystem.R;


/**
 * Created by namit on 4/8/2016.
 */
public class CustomPopupWindow extends PopupWindow implements OnChatMoreListener {

  private OnPopupListener action;
  private ChatMoreLayout mChatMoreLayout;
  private Context context;
  private boolean isDefault = false;
  private RequestFragmentData defaultAction;
  private IRequestClass onRequestClass;

  public CustomPopupWindow(Context context) {
    super(context);
    init(context, null);
  }

  public CustomPopupWindow(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context, attrs);
  }

  public CustomPopupWindow(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context, attrs);
  }

  public CustomPopupWindow(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    init(context, attrs);
  }

  public void init(Context context, AttributeSet attrs) {
    this.context = context;
  }

  /**
   * Create action
   */
  public void setAction(@Nullable OnPopupListener action, @NonNull IRequestClass onRequestClass)
      throws Exception {
    this.action = action;
    this.onRequestClass = onRequestClass;
  }

  /**
   * Create action default
   */
  public void setAction(@NonNull IRequestClass onRequestClass) throws Exception {
    this.isDefault = true;
    this.onRequestClass = onRequestClass;
    onDefault(isDefault);
  }

  /**
   * function Default
   */
  private void onDefault(boolean isDefault) {
    if (!isDefault) {
      return;
    }
    try {
      defaultAction = new RequestFragmentData(
          onRequestClass.getFragment(),
          context,
          onRequestClass.getMain(),
          onRequestClass.onChatUser(),
          onRequestClass.getAlertDialog(),
          onRequestClass);

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * <p>Create a new popup window which can display the <tt>contentView</tt>. The dimension of the
   * window must be passed to this constructor.</p> <p> <p>The popup does not provide any
   * background. This should be handled by the content view.</p>
   *
   * @param contentView the popup's content
   * @param width the popup's width
   * @param height the popup's height
   * @param focusable true if the popup can be focused, false otherwise
   */
  public void onCreateView(View contentView, int width, int height, boolean focusable)
      throws Exception {
    mChatMoreLayout = new ChatMoreLayout(context, this,
        onRequestClass.onChatUser().getId(), onRequestClass.isVoiceCallWaiting(),
        onRequestClass.isVideoCallWaiting(), false, 0);

    setContentView(contentView);
    setWidth(width);
    setHeight(height);
    setFocusable(focusable);
    //-----
    showAsDropDown(onRequestClass.onAnchor());
  }

  /**
   * <p>Create a new popup window which can display the <tt>contentView</tt>. The dimension of the
   * window must be passed to this constructor.</p> <p> <p>The popup does not provide any
   * background. This should be handled by the content view.</p>
   *
   * @param width the popup's width
   * @param height the popup's height
   * @param focusable true if the popup can be focused, false otherwise
   */
  public void onCreateView(int width, int height, boolean focusable) throws Exception {
    mChatMoreLayout = new ChatMoreLayout(context, this,
        onRequestClass.onChatUser().getId(), onRequestClass.isVoiceCallWaiting(),
        onRequestClass.isVideoCallWaiting(), false, 0);

    setContentView(mChatMoreLayout);
    setWidth(width);
    setHeight(height);
    setFocusable(focusable);
    //-----
    showAsDropDown(onRequestClass.onAnchor());
  }

  /**
   * <p>Create a new popup window which can display the <tt>contentView</tt>. The dimension of the
   * window must be passed to this constructor.</p> <p> <p>The popup does not provide any
   * background. This should be handled by the content view.</p>
   */
  public void onCreateView() throws Exception {
    mChatMoreLayout = new ChatMoreLayout(context, this,
        onRequestClass.onChatUser().getId(), onRequestClass.isVoiceCallWaiting(),
        onRequestClass.isVideoCallWaiting(), false, 0);

    setContentView(mChatMoreLayout);
    setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
    setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
    setFocusable(false);
    //-----
    showAsDropDown(onRequestClass.onAnchor());
  }

  //============================================================//
  //======================= Function ===========================//
  //============================================================//
  private void restartRequestBasicUserInfo() throws Exception {
    UserPreferences preferences = UserPreferences.getInstance();
    String token = preferences.getToken();
    GetBasicInfoRequest request = new GetBasicInfoRequest(token,
        onRequestClass.getCallInfo().getUserId());
    ((BaseFragment) onRequestClass.getFragment())
        .restartRequestServer(action.LOADER_ID_BASIC_USER_INFO_CALL, request);
  }

  public void hideChatMoreOptions() {
    if (isShowing()) {
      dismiss();
      if (null != onRequestClass.getViewFreezed()) {
        onRequestClass.getViewFreezed().setVisibility(View.GONE);
      }
    }
  }

  //============================================================//
  //======================= Override ===========================//
  //============================================================//
  @Override
  public void onSendGift() {
    try {
      hideChatMoreOptions();
      onRequestClass.getMain().setUnbindChatOnStop(true);

      GiftCategories categories = new GiftCategories("get_all_gift", 0,
          context.getResources().getString(R.string.give_gift_all_title), 1);
      ChooseGiftToSend chooseGiftToSend = ChooseGiftToSend
          .newInstance(onRequestClass.onChatUser().getId(), onRequestClass.onChatUser().getName(),
              categories, false, true);
      chooseGiftToSend.setTargetFragment(onRequestClass.getFragment(), action.REQUEST_GIFT);
      ((BaseFragment) onRequestClass.getFragment()).replaceFragment(chooseGiftToSend);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void onFavorite() {
    try {
      hideChatMoreOptions();
      if (FavouritedPrefers.getInstance().hasContainFav(onRequestClass.onChatUser().getId())) {
        if (isDefault) {
          defaultAction.OnExecuteRemoveFromFavorites();
        } else {
          action.OnExecuteRemoveFromFavorites();
        }

      } else {
        onRequestClass.setNewAddFavoriteRequest(true);
        if (isDefault) {
          defaultAction.OnExecuteAddToFavorites();
        } else {
          action.OnExecuteAddToFavorites();
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void onBlock() {
    try {
      hideChatMoreOptions();
      if (isDefault) {
        defaultAction.OnExecuteBlockUser();
      } else {
        action.OnExecuteBlockUser();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void onReport() {
    try {
      hideChatMoreOptions();
      if (isDefault) {
        defaultAction.onExecuteReportUser();
      } else {
        action.onExecuteReportUser();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void onVoiceCall() {
    try {
      hideChatMoreOptions();
      if (isDefault) {
        defaultAction.onExecuteVoiceCall();
      } else {
        action.onExecuteVoiceCall();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void onVideoCall() {
    try {
      hideChatMoreOptions();
      if (Utility.isBlockedWithUser(context, onRequestClass.onChatUser().getId())) {
        if (isDefault) {
          defaultAction.onExitMeWhenBlocked();
        } else {
          action.onExitMeWhenBlocked();
        }
        return;
      }

      CallUserInfo userInfo = new CallUserInfo(onRequestClass.onChatUser().getName(),
          onRequestClass.onChatUser().getId(), onRequestClass.onChatUser().getAvatar(),
          onRequestClass.onChatUser().getGender());
      onRequestClass.setCallUserInfo(userInfo);
      onRequestClass.setCurrentCallType(Constants.CALL_TYPE_VIDEO);
      Utility.showDialogAskingVideoCall(onRequestClass.getMain(), userInfo,
          new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              try {
                dialog.dismiss();
                restartRequestBasicUserInfo();
              } catch (Exception e) {
                e.printStackTrace();
              }
            }
          });
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void onShown() {
    Handler handler = new Handler();
    handler.postDelayed(new Runnable() {
      @Override
      public void run() {
//                    getSlidingMenu().setSlidingEnabled(true);
      }
    }, 200);
  }

  @Override
  public void onAlertOnline() {

  }
}
