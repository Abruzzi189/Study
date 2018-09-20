package com.application.actionbar.popup.controllers;

import android.app.AlertDialog;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import com.application.entity.CallUserInfo;
import com.application.model.ChatUser;
import com.application.ui.MainActivity;

/**
 * Created by vn apnic on 4/10/2016.
 */
public interface IRequestClass extends IPopupRequest {

  /**
   * @return this fragment
   */
  @NonNull
  Fragment getFragment() throws Exception;

  /**
   * @return ChatUser
   */
  @NonNull
  ChatUser onChatUser() throws Exception;

  /**
   * @return MainActivity
   */
  @NonNull
  MainActivity getMain();

  /**
   * @param callUserInfo setCallInfo
   */
  @NonNull
  void setCallUserInfo(CallUserInfo callUserInfo) throws Exception;

  CallUserInfo getCallInfo() throws Exception;

  /**
   * @param currentCallType setType Call
   */
  @NonNull
  void setCurrentCallType(int currentCallType) throws Exception;


  @NonNull
  boolean isVoiceCallWaiting() throws Exception;

  @NonNull
  boolean isVideoCallWaiting() throws Exception;

  /**
   * @return NavigationBar
   */
  @NonNull
  View onAnchor() throws Exception;

  @Nullable
  View getViewFreezed();

  @NonNull
  AlertDialog getAlertDialog() throws Exception;

  @NonNull
  boolean isNewAddFavoriteRequest() throws Exception;

  @NonNull
  void setNewAddFavoriteRequest(boolean isNewAddFavoriteRequest) throws Exception;

}
