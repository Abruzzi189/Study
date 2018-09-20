package com.application.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import com.application.chat.ChatUtils;
import com.application.connection.Request;
import com.application.connection.RequestBuilder;
import com.application.connection.RequestType;
import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.application.connection.ResponseReceiver;
import com.application.connection.request.GetBannedWordRequest;
import com.application.connection.request.GetNotificationSettingRequest;
import com.application.connection.request.GetPointRequest;
import com.application.connection.request.GetStickerCategoryRequest;
import com.application.connection.request.ListDefaultStickerCategoryRequest;
import com.application.connection.request.RegisterSipRequest;
import com.application.connection.response.GetBannedWordResponse;
import com.application.connection.response.GetNotificationSettingResponse;
import com.application.connection.response.GetPointResponse;
import com.application.connection.response.GetStickerCategoryResponse;
import com.application.connection.response.ListDefaultStickerCategoryResponse;
import com.application.entity.NotificationMessage;
import com.application.fcm.WLCFirebaseMessagingService;
import com.application.service.data.StickerCategoryInfo;
import com.application.stickers.StickersDBManager;
import com.application.util.DirtyWords;
import com.application.util.LogUtils;
import com.application.util.preferece.FavouritedPrefers;
import com.application.util.preferece.Preferences;
import com.application.util.preferece.UserPreferences;
import glas.bbsystem.R;
import java.util.ArrayList;
import java.util.List;


public class DataFetcherService extends IntentService {

  public static final int EXTRA_TYPE_CHECK_STICKER = 100;
  public static final int EXTRA_TYPE_DIRTY_WORD = 102;
  public static final int EXTRA_TYPE_NOTIFICATION_SETTING = 103;
  public static final int EXTRA_TYPE_NOTIFICATION_REGISTER_SIP = 104;
  public static final int EXTRA_TYPE_GET_POINT = 105;
  public static final String INTENT_TYPE = "TYPE_LOAD";
  public static final String INTENT_TOKEN = "token";
  public static final String ACTION_RELOAD_STICKER = "reload_sticker";
  public static final String ACTION_STICKER_DOWNLOAD_DONE = "sticker_download_done";
  private static final String TAG = "DataFetcherService";
  private static final int LOADER_ID_STICKER = 100;
  private static final int LOADER_ID_STICKER_CATEGORIES = 200;
  private static final int LOADER_DIRTY_WORD = 101;
  private static final int LOADER_SETTING_NOTIFY = 103;
  private static final int LOADER_NOTIFY_REGISTER_SIP = 104;
  private static final int LOADER_GET_POINT = 105;
  private List<StickerCategoryInfo> listDownloadSticker;
  private List<StickerCategoryInfo> listDeleteSticker;
  private ResponseReceiver mResponseReceiver = new ResponseReceiver() {
    @Override
    public void startRequest(int loaderId) {
      // Do nothing
    }

    @Override
    public void receiveResponse(Loader<Response> loader, Response response) {
      // Do nothing
    }

    @Override
    public Response parseResponse(int loaderID, ResponseData data,
        int requestType) {
      Response response = null;
      if (loaderID == LOADER_ID_STICKER) {
        response = new GetStickerCategoryResponse(data);
      } else if (loaderID == LOADER_ID_STICKER_CATEGORIES) {
        response = new ListDefaultStickerCategoryResponse(data);
      } else if (loaderID == LOADER_DIRTY_WORD) {
        response = new GetBannedWordResponse(data);
      } else if (loaderID == LOADER_SETTING_NOTIFY) {
        response = new GetNotificationSettingResponse(data);
      } else if (loaderID == LOADER_GET_POINT) {
        response = new GetPointResponse(data);
      }
      return response;

    }

    @Override
    public void onBaseLoaderReset(Loader<Response> loader) {
      // Do nothing
    }
  };

  public DataFetcherService() {
    super(TAG);
  }

  public static void startCheckSticker(Context context, String token) {
    Intent intent = new Intent(context, DataFetcherService.class);
    intent.putExtra(DataFetcherService.INTENT_TYPE,
        DataFetcherService.EXTRA_TYPE_CHECK_STICKER);
    intent.putExtra(DataFetcherService.INTENT_TOKEN, token);
    context.startService(intent);
  }

  public static void startLoadDirtyWord(Context context) {
    Intent intent = new Intent(context, DataFetcherService.class);
    intent.putExtra(DataFetcherService.INTENT_TYPE,
        DataFetcherService.EXTRA_TYPE_DIRTY_WORD);
    context.startService(intent);
  }

  public static void startLoadNotificationSetting(Context context) {
    Intent intent = new Intent(context, DataFetcherService.class);
    intent.putExtra(DataFetcherService.INTENT_TYPE,
        DataFetcherService.EXTRA_TYPE_NOTIFICATION_SETTING);
    context.startService(intent);
  }

  public static void startNotifyRegisterSipToServer(Context context) {
    Intent intent = new Intent(context, DataFetcherService.class);
    intent.putExtra(DataFetcherService.INTENT_TYPE,
        DataFetcherService.EXTRA_TYPE_NOTIFICATION_REGISTER_SIP);
    context.startService(intent);
  }

  public static void startGetPoint(Context context) {
    Intent intent = new Intent(context, DataFetcherService.class);
    intent.putExtra(DataFetcherService.INTENT_TYPE,
        DataFetcherService.EXTRA_TYPE_GET_POINT);
    context.startService(intent);
  }

  @Override
  protected void onHandleIntent(Intent intent) {
    int type = intent.getIntExtra(INTENT_TYPE, -1);
    LogUtils.d(TAG, "EXTRA TYPE:" + type);
    if (type == EXTRA_TYPE_CHECK_STICKER) {
      String token = intent.getStringExtra(INTENT_TOKEN);
      onLoadStickerCategory(token);
    } else if (type == EXTRA_TYPE_DIRTY_WORD) {
      onLoadDirtyWord();
    } else if (type == EXTRA_TYPE_NOTIFICATION_SETTING) {
      onLoadNotificationSetting();
    } else if (type == EXTRA_TYPE_NOTIFICATION_REGISTER_SIP) {
      onNotifyRegisterSip();
    } else if (type == EXTRA_TYPE_GET_POINT) {
      onGetPoint();
    }
  }

  private void onLoadStickerCategory(String token) {
    if (token == null || token.length() < 1) {
      token = UserPreferences.getInstance().getToken();
    } else {
      UserPreferences.getInstance().saveToken(token);
    }
    LogUtils.i(TAG, "Token: " + token);
    String lang = getApplication().getString(R.string.common_app_lang);
    int skip = 0;
    int take = Integer.MAX_VALUE / 2;
    ListDefaultStickerCategoryRequest requestCategory = new ListDefaultStickerCategoryRequest(
        token, lang, skip, take);
    Request request = RequestBuilder.getInstance().makeRequest(
        RequestType.JSON, requestCategory, mResponseReceiver,
        LOADER_ID_STICKER_CATEGORIES);
    new BasicLoader(token).execute(request);
  }

  private void onLoadStickers(String token) {
    if (TextUtils.isEmpty(token)) {
      LogUtils.i(TAG, "Invalid token");
      sendBroadcastDoneLoading();
      return;
    }

    LogUtils.i(TAG, "Download num: " + listDownloadSticker.size());
    LogUtils.i(TAG, "Delete num: " + listDeleteSticker.size());
    if (listDeleteSticker != null && !listDeleteSticker.isEmpty()) {
      StickersDBManager stickersDBManager = StickersDBManager
          .getInstance(getApplicationContext());
      for (StickerCategoryInfo sticker : listDeleteSticker) {
        stickersDBManager.delete(sticker);
        ChatUtils.deleteStickerCategory(getApplicationContext(),
            sticker.getId());
        sendBroadcastSticker();
        LogUtils.i(TAG, "Delete sticker: " + sticker.getId());
      }
    }

    if (listDownloadSticker.size() > 0) {
      for (StickerCategoryInfo sticker : listDownloadSticker) {
        String stickerId = sticker.getId();
        GetStickerCategoryRequest stickerCategoryRequest = new GetStickerCategoryRequest(
            token, stickerId);
        Request request = RequestBuilder.getInstance().makeRequest(
            RequestType.JSON, stickerCategoryRequest,
            mResponseReceiver, LOADER_ID_STICKER);
        LogUtils.i(TAG, "Download sticker: " + stickerId);
        new BasicLoader(sticker).execute(request);
      }
    } else {
      sendBroadcastDoneLoading();
    }
  }

  private void onLoadDirtyWord() {
    Preferences preferences = Preferences.getInstance();
    UserPreferences userPreferences = UserPreferences.getInstance();
    String token = userPreferences.getToken();
    int version = preferences.getVersionDirtyWord();
    GetBannedWordRequest getBannedWordRequest = new GetBannedWordRequest(
        token, version);

    Request request = RequestBuilder.getInstance().makeRequest(
        RequestType.JSON, getBannedWordRequest, mResponseReceiver,
        LOADER_DIRTY_WORD);
    requestBannedWord(request);
  }

  private void onLoadNotificationSetting() {
    UserPreferences userPreferences = UserPreferences.getInstance();
    String token = userPreferences.getToken();
    GetNotificationSettingRequest requestNotification = new GetNotificationSettingRequest(
        token);
    Request request = RequestBuilder.getInstance().makeRequest(
        RequestType.JSON, requestNotification, mResponseReceiver,
        LOADER_SETTING_NOTIFY);
    new BasicLoader().execute(request);
  }

  private void onNotifyRegisterSip() {
    UserPreferences userPreferences = UserPreferences.getInstance();
    String token = userPreferences.getToken();
    String email = userPreferences.getEmail();

    RegisterSipRequest registerSipRequest;
    if (TextUtils.isEmpty(email)) {
      registerSipRequest = new RegisterSipRequest(token, null);
    } else {
      String pwd = userPreferences.getPassword();
      registerSipRequest = new RegisterSipRequest(token, pwd);
    }
    Request request = RequestBuilder.getInstance().makeRequest(
        RequestType.JSON, registerSipRequest, mResponseReceiver,
        LOADER_NOTIFY_REGISTER_SIP);
    new BasicLoader().execute(request);
  }

  private void onGetPoint() {
    UserPreferences userPreferences = UserPreferences.getInstance();
    String token = userPreferences.getToken();
    GetPointRequest requestPoint = new GetPointRequest(token);

    Request request = RequestBuilder.getInstance().makeRequest(
        RequestType.JSON, requestPoint, mResponseReceiver,
        LOADER_GET_POINT);
    new BasicLoader().execute(request);
  }

  private void requestBannedWord(Request request) {
    Response response = request.execute();
    if (response instanceof GetBannedWordResponse) {
      handleGetBannedWordResponse((GetBannedWordResponse) response);
    }
  }

  // ===== ===== Handler ===== =====

  private void handleLoadSticker(GetStickerCategoryResponse response,
      StickerCategoryInfo stickerInfo) {
    String packageId = response.getPackageId();
    LogUtils.d(TAG, "Saving sticker: " + packageId);
    Context context = getApplicationContext();
    String data = response.getData();
    LogUtils.d(TAG, "Sticker Data: " + packageId);
    String order = response.getOrder();
    boolean isSaved = ChatUtils.saveFileStickerFromString(context,
        packageId, data, order);
    if (isSaved) {
      Handler handler = new Handler(Looper.getMainLooper());
      handler.post(new Runnable() {
        @Override
        public void run() {
          // Broadcast must call on Main UI thread
          sendBroadcastSticker();
        }
      });
      if (stickerInfo != null) {
        LogUtils.i(TAG, "Saved sticker: " + packageId);
        StickersDBManager.getInstance(getApplicationContext())
            .insertOrUpdate(stickerInfo);
      }
    }
  }

  private void handleLoadCategory(
      ListDefaultStickerCategoryResponse response, String token) {
    if (response.getCode() == Response.SERVER_SUCCESS) {
      if (response.getListCategory() != null) {
        listDownloadSticker = new ArrayList<StickerCategoryInfo>();
        listDeleteSticker = new ArrayList<StickerCategoryInfo>();
        List<StickerCategoryInfo> listStickersServer = response
            .getListCategory();
        List<StickerCategoryInfo> listStickersDB = StickersDBManager
            .getInstance(getApplicationContext())
            .getListStickerCategoryInfos();
        LogUtils.i(TAG, "Number of sticker from server: "
            + listStickersServer.size());
        LogUtils.i(TAG,
            "Number of sticker from DB: " + listStickersDB.size());

        for (StickerCategoryInfo stickerServer : listStickersServer) {
          boolean existed = false;
          boolean existedButExpired = false;

          for (StickerCategoryInfo stickerDB : listStickersDB) {
            if (stickerServer.getId().equals(stickerDB.getId())) {
              existed = true;
              if (stickerServer.getVersion() != stickerDB
                  .getVersion()) {
                existedButExpired = true;
              }
              break;
            }
          }

          if (!existed || existedButExpired) {
            listDownloadSticker.add(stickerServer);
          }
        }

        for (StickerCategoryInfo stickerDB : listStickersDB) {
          boolean needToDelete = true;

          for (StickerCategoryInfo stickerServer : listStickersServer) {
            if (stickerDB.getId().equals(stickerServer.getId())
                && stickerDB.getVersion() == stickerServer
                .getVersion()) {

              needToDelete = false;
              break;
            }
          }

          if (needToDelete) {
            listDeleteSticker.add(stickerDB);
          }
        }
        onLoadStickers(token);
      }
    }
  }

  private void handleGetBannedWordResponse(GetBannedWordResponse response) {
    int code = response.getCode();
    switch (code) {
      case Response.SERVER_SUCCESS:
        Preferences preferences = Preferences.getInstance();
        int version = response.getVersion();
        preferences.setVersionDirtyWord(version);
        DirtyWords dirtyWords = DirtyWords
            .getInstance(getApplicationContext());
        dirtyWords.clear();
        ArrayList<String> listWords = response.getListWord();
        dirtyWords.saveAllWord(listWords);
        break;
      case Response.SERVER_NO_CHANGE:
        break;
    }
  }

  private void handleNotificationSettingResponse(
      GetNotificationSettingResponse settingResponse) {
    int code = settingResponse.getCode();
    if (code == Response.SERVER_SUCCESS) {
      try {
        FavouritedPrefers favouritedPrefers = FavouritedPrefers
            .getInstance();
        favouritedPrefers.clearAll();
        favouritedPrefers.saveFavs(settingResponse.getFavList());

        UserPreferences.getInstance().saveChatNotificationType(
            settingResponse.getNotifyChat());
      } catch (Exception c) {
        c.printStackTrace();
      }
    }
  }

  private void handleGetPoint(GetPointResponse response) {
    int code = response.getCode();
    if (code == Response.SERVER_SUCCESS) {
      UserPreferences.getInstance().saveNumberPoint(response.getPoint());
    }
  }

  public void sendBroadcastReceiveMessage(NotificationMessage message) {
    Intent intent = new Intent(WLCFirebaseMessagingService.ACTION_GCM_RECEIVE_MESSAGE);
    intent.putExtra(WLCFirebaseMessagingService.EXTRA_NOTIFICATION_MESSAGE, message);
    LocalBroadcastManager.getInstance(getApplicationContext())
        .sendBroadcast(intent);
  }

  // ===== ===== Connect to main UI ===== =====

  private void sendBroadcastSticker() {
    Intent intent = new Intent(ACTION_RELOAD_STICKER);
    intent.putExtra(ACTION_RELOAD_STICKER, "");
    sendBroadcast(intent);
  }

  private void sendBroadcastDoneLoading() {
    Intent intent = new Intent(ACTION_STICKER_DOWNLOAD_DONE);
    sendBroadcast(intent);
  }

  private class BasicLoader extends AsyncTask<Request, Void, Response> {

    private StickerCategoryInfo sticker;
    private String token;

    public BasicLoader() {
    }

    public BasicLoader(StickerCategoryInfo sticker) {
      this.sticker = sticker;
    }

    public BasicLoader(String token) {
      this.token = token;
    }

    @Override
    protected Response doInBackground(Request... params) {
      Request request = params[0];
      Response response = request.execute();
      if (response instanceof GetStickerCategoryResponse) {
        handleLoadSticker((GetStickerCategoryResponse) response,
            sticker);
      } else if (response instanceof ListDefaultStickerCategoryResponse) {
        handleLoadCategory(
            (ListDefaultStickerCategoryResponse) response, token);
      } else if (response instanceof GetBannedWordResponse) {
        handleGetBannedWordResponse((GetBannedWordResponse) response);
      } else if (response instanceof GetNotificationSettingResponse) {
        handleNotificationSettingResponse((GetNotificationSettingResponse) response);
      } else if (response instanceof GetPointResponse) {
        handleGetPoint((GetPointResponse) response);
      }
      return response;
    }

    @Override
    protected void onPostExecute(Response result) {
      super.onPostExecute(result);
    }
  }
}