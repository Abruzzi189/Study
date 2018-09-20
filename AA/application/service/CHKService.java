package com.application.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import com.application.connection.Request;
import com.application.connection.RequestBuilder;
import com.application.connection.RequestType;
import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.application.connection.ResponseReceiver;
import com.application.connection.request.MinusPointRequest;
import com.application.connection.response.MinusPointResponse;
import com.application.util.preferece.UserPreferences;

public class CHKService extends Service {

  public static final String ACTION_CHECK_POINT_CHAT = "chk_point_chat";
  public static final String ACTION_CHECK_POINT_VOICE = "chk_point_voice";
  public static final String ACTION_CHECK_POINT_VIDEO = "chk_point_video";
  public static final String EXTRA_RESULT = "chk_result";
  public static final String EXTRA_POINT = "chk_point";
  private static final String EXTRA_CHECK_TYPE = "check_type";
  private static final String EXTRA_USER_ID = "user_id";
  private static final String EXTRA_IS_CHECK_POINT = "is_check_point";
  private static final int LOADER_ID_CHECK_POINT = 100;
  private ResponseReceiver mResponseReceiver = new ResponseReceiver() {

    @Override
    public void startRequest(int loaderId) {

    }

    @Override
    public void receiveResponse(Loader<Response> loader, Response response) {
    }

    @Override
    public Response parseResponse(int loaderID, ResponseData data,
        int requestType) {
      Response response = null;
      if (loaderID == LOADER_ID_CHECK_POINT) {
        response = new MinusPointResponse(data);
      }
      return response;

    }

    @Override
    public void onBaseLoaderReset(Loader<Response> loader) {

    }
  };

  public static void checkPointChat(Bundle bundle, Context context,
      String userId, int isCheckPoint) {
    checkPoint(bundle, context, MinusPointRequest.CHAT, userId,
        ACTION_CHECK_POINT_CHAT, isCheckPoint);
  }

  public static void checkPointVoice(Bundle bundle, Context context,
      String userId, int isCheckPoint) {
    checkPoint(bundle, context, MinusPointRequest.VOICE, userId,
        ACTION_CHECK_POINT_VOICE, isCheckPoint);
  }

  public static void checkPointVideo(Bundle bundle, Context context,
      String userId, int isCheckPoint) {
    checkPoint(bundle, context, MinusPointRequest.VIDEO, userId,
        ACTION_CHECK_POINT_VIDEO, isCheckPoint);
  }

  public static void checkPoint(Bundle bundle, Context context,
      int checkType, String userId, String action, int isCheckPoint) {
    Intent intent = new Intent(context, CHKService.class);
    intent.setAction(action);
    intent.putExtra(EXTRA_CHECK_TYPE, checkType);
    intent.putExtra(EXTRA_USER_ID, userId);
    intent.putExtra(EXTRA_IS_CHECK_POINT, isCheckPoint);
    intent.putExtras(bundle);
    context.startService(intent);
  }

  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    if (intent != null) {
      int checkType = intent.getIntExtra(EXTRA_CHECK_TYPE, -1);
      String userId = intent.getStringExtra(EXTRA_USER_ID);
      int isCheckPoint = intent.getIntExtra(EXTRA_IS_CHECK_POINT,
          MinusPointRequest.CHECK_POINT);
      startCheckPoint(intent, checkType, userId, isCheckPoint);
    }

    return super.onStartCommand(intent, flags, startId);
  }

  private void startCheckPoint(Intent intent, int checkType, String userId,
      int checkPoint) {
    String token = UserPreferences.getInstance().getToken();
    MinusPointRequest minusPointRequest = new MinusPointRequest(token,
        checkType, userId, checkPoint);
    Request request = RequestBuilder.getInstance().makeRequest(
        RequestType.JSON, minusPointRequest, mResponseReceiver,
        LOADER_ID_CHECK_POINT);
    // new BasicLoader(intent).execute(request);
    new Thread(new PointChecker(intent, request)).start();
  }

  private class PointChecker implements Runnable {

    Intent intent;
    Request mRequest;

    public PointChecker(Intent intent, Request request) {
      this.intent = intent;
      mRequest = request;
    }

    @Override
    public void run() {
      Response response = mRequest.execute();
      if (response.getCode() == Response.SERVER_INVALID_TOKEN) {
        // relogin
        boolean relogin = RequestBuilder.getInstance().relogin();
        if (relogin) {
          mRequest.setNewToken(UserPreferences.getInstance()
              .getToken());
          response = mRequest.execute();
        }
      }

      if (response.getCode() == Response.SERVER_CHANGE_BACKEND_SETTING) {
        if (RequestBuilder.getInstance().updateBackendSetting()) {
          response = mRequest.execute();
        }
      }

      if (response instanceof MinusPointResponse) {
        handleCheckPointResponse(response.getCode(),
            ((MinusPointResponse) response).getPoint());
      }
    }

    private void handleCheckPointResponse(int responseCode, int point) {
      intent.putExtra(EXTRA_RESULT, responseCode);
      intent.putExtra(EXTRA_POINT, point);
      LocalBroadcastManager.getInstance(getApplicationContext())
          .sendBroadcast(intent);
      if (responseCode == Response.SERVER_SUCCESS) {
        UserPreferences.getInstance().saveNumberPoint(point);
      }
    }
  }
}
