package com.application.ui.viewholders;

import com.application.connection.request.RequestParams;
import com.application.imageloader.ImageFetcher;
import com.application.ui.BaseFragment;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

/**
 * Created by vn apnic on 4/4/2016.
 */
public interface IOnActionConversations extends IOnActionViewHolder {

  void onRestartRequestServer(int loaderID, int requestType,
      RequestParams data) throws Exception;

  void onRestartRequestServer(int loaderID, RequestParams data) throws Exception;

  void onRestartRequestServer(int loaderID, RequestParams data,
      int timeoutConnect, int timeoutRead) throws Exception;

  boolean isCurrentChatAnonymous() throws Exception;

  boolean isChatAnonymousEmpty() throws Exception;

  String onHiddenUserId() throws Exception;

  /* TimeSpan */
  String onGetTimeSpan() throws Exception;

  void onSetTimeSpan(String timeSpan) throws Exception;

  /* Remain in MainActivity */
  int onRemain() throws Exception;

  /* ImageFetcher */
  ImageFetcher getImageFetcher() throws Exception;

  String[] onGetListMarkAsReadUser() throws Exception;

  void onSetListMarkAsReadUser(String[] strings) throws Exception;

  void onRequestTotalUnreadMsg();

  boolean isRequestDeleteAll();

  void setRequestDeleteAll(boolean isRequestDeleteAll);

  SlidingMenu getSlidingMenu();

  void onReplaceFragment(BaseFragment fragment, String tag);

}
