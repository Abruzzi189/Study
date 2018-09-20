package com.application.actionbar.popup.controllers;

/**
 * Created by vn apnic on 4/10/2016.
 */
public interface OnPopupListener extends IPopupRequest {

  /* Action OnChatMoreListener*/
  void onExecuteReportUser() throws Exception;

  void OnExecuteRemoveFromFavorites() throws Exception;

  void OnExecuteAddToFavorites() throws Exception;

  void onExecuteVoiceCall() throws Exception;

  void onExitMeWhenBlocked() throws Exception;

  void OnExecuteBlockUser() throws Exception;
  //------------------------------
}
