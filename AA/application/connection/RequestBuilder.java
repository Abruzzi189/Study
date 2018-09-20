package com.application.connection;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import com.application.AndGApp;
import com.application.Config;
import com.application.connection.request.GetBackendSettingRequest;
import com.application.connection.request.LoginByEmailRequest;
import com.application.connection.request.LoginByFacebookRequest;
import com.application.connection.request.LoginRequest;
import com.application.connection.request.RequestParams;
import com.application.connection.response.GetBackendSettingResponse;
import com.application.connection.response.LoginResponse;
import com.application.entity.BackendSetting;
import com.application.service.ChatService;
import com.application.service.DataFetcherService;
import com.application.ui.BaseFragmentActivity;
import com.application.ui.account.AuthenticationData;
import com.application.ui.account.AuthenticationUtil;
import com.application.util.LogUtils;
import com.application.util.Utility;
import com.application.util.preferece.BlockUserPreferences;
import com.application.util.preferece.GoogleReviewPreference;
import com.application.util.preferece.Preferences;
import com.application.util.preferece.UserPreferences;
import com.google.firebase.iid.FirebaseInstanceId;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import org.json.JSONException;
import org.json.JSONObject;
import org.linphone.LinphoneService;

public class RequestBuilder {

  private static final String TAG = "RequestBuilder";
  private static Context mContext;
  private static RequestBuilder requestBuilder = new RequestBuilder();
  private RequestBuilder() {
  }

  public static RequestBuilder getInstance() {
    mContext = AndGApp.get();
    return requestBuilder;
  }

  /**
   * @author tungdx
   */
  public static InputStream sendLoadImageRequest(String inputString)
      throws IOException {
    LogUtils.i(TAG, "Url to load image=" + inputString);
    StringBuilder postData = new StringBuilder();
    URL u;
    HttpURLConnection conn = null;
    OutputStream out = null;
    u = new URL(Config.IMAGE_SERVER_URL);
    conn = (HttpURLConnection) u.openConnection();
    conn.setConnectTimeout(Config.TIMEOUT_CONNECT);
    conn.setReadTimeout(Config.TIMEOUT_READ);
    // post method
    conn.setDoOutput(true);
    conn.setRequestMethod("POST");
    // data to send
    postData.append(inputString);
    String encodedData = postData.toString();
    // send data by byte
    conn.setRequestProperty("Content-Language", "en-US");
    conn.setRequestProperty("Content-Type",
        "application/x-www-form-urlencoded");

    conn.setRequestProperty("Content-Length",
        Integer.toString(encodedData.getBytes().length));
    byte[] postDataByte = postData.toString().getBytes("UTF-8");
    out = conn.getOutputStream();
    out.write(postDataByte);
    // get data from server
    return conn.getInputStream();
  }

  public Request makeRequest(final int requestType,
      final RequestParams generalRequest,
      final ResponseReceiver responseReceiver, final int idLoader) {
    return makeRequest(requestType, generalRequest, responseReceiver,
        idLoader, Config.TIMEOUT_CONNECT, Config.TIMEOUT_READ);
  }

  public Request makeRequest(final int requestType,
      final RequestParams generalRequest,
      final ResponseReceiver responseReceiver, final int idLoader,
      final int timeoutConnect, final int timeoutRead) {

    Request request;
    request = new Request() {
      @Override
      public Response execute() {
        ResponseData responseData = new ResponseData();

        String data = generalRequest.toString();
        LogUtils.d(TAG, "Request Data - Api: " + generalRequest.getApi() + "\ndata =" + data);
        // check internet
        if (!Utility.isNetworkConnected(mContext)) {
          responseData.setStatus(Response.CLIENT_ERROR_NO_CONNECTION);
          return responseReceiver.parseResponse(idLoader,
              responseData, requestType);
        }

        String url;
        if (Method.POST == generalRequest.getMethod()) {
          url = Config.SERVER_URL;
        } else {
          url = Config.IMAGE_SERVER_URL + data;
        }
        ContentResponse outData = sendRequest(url,
            generalRequest.getMethod(), data, timeoutConnect,
            timeoutRead);

        if (outData.status == Response.CLIENT_SUCCESS) {
          responseData.setStatus(Response.CLIENT_SUCCESS);
          responseData.setText(outData.content);
          LogUtils.d(TAG, "Receive Data - Api: " + generalRequest.getApi() + "\ndata = "
              + outData.content);
          if (requestType == RequestType.JSON
              && responseData.getText() != null) {
            try {
              responseData.makeJSONObject();
              int code = getResponseCode(responseData
                  .getJSONObject());
              // if has code block or deactvie -> send broadcast
              // and return response =null
              if (code == Response.SERVER_BLOCKED_USER
                  || code == Response.SERVER_USER_NOT_EXIST) {
                sendBroadcastHasBlockAndDeactive(code);
              }
            } catch (JSONException e) {
              e.printStackTrace();
              outData.status = Response.CLIENT_ERROR_PARSE_JSON;
              responseData.setStatus(outData.status);
            }
          }
        } else {
          responseData.setStatus(outData.status);
        }
        // parse get data
        if (responseReceiver == null) {
          return null;
        }

        return responseReceiver.parseResponse(idLoader, responseData,
            requestType);
      }

      @Override
      public void setNewToken(String newToken) {
        generalRequest.setToken(newToken);
      }
    };
    return request;
  }

  private int getResponseCode(JSONObject jsonObject) throws JSONException {
    return jsonObject.getInt("code");

  }

  private void sendBroadcastHasBlockAndDeactive(int code) {
    Intent intent = new Intent(Response.ACTION_BLOCK_DEACTIVE);
    if (code == Response.SERVER_BLOCKED_USER) {
      intent.putExtra(Response.EXTRA_CODE, Response.SERVER_BLOCKED_USER);
    } else if (code == Response.SERVER_USER_NOT_EXIST) {
      intent.putExtra(Response.EXTRA_CODE, Response.SERVER_USER_NOT_EXIST);
    }
    LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
  }

  private ContentResponse sendRequest(String url, int method,
      String inputString, int timeoutConnect, int timeoutRead) {
    String outputData = null;
    StringBuilder postData = new StringBuilder();
    URL u;
    HttpURLConnection conn = null;
    OutputStream out = null;
    InputStreamReader isr;
    BufferedReader buf;

    try {
      u = new URL(url);
      conn = (HttpURLConnection) u.openConnection();
      conn.setConnectTimeout(timeoutConnect);
      conn.setReadTimeout(timeoutRead);

      if (method == Method.POST) {
        // post method
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        // data to send
        postData.append(inputString);
        String encodedData = postData.toString();
        // send data by byte
        conn.setRequestProperty("Content-Language", "en-US");
        conn.setRequestProperty("Content-Type",
            "application/x-www-form-urlencoded");

        conn.setRequestProperty("Content-Length",
            Integer.toString(encodedData.getBytes().length));
        byte[] postDataByte = postData.toString().getBytes("UTF-8");
        out = conn.getOutputStream();
        out.write(postDataByte);
      } else {
        // do nothing
      }

      // get data from server
      isr = new InputStreamReader(conn.getInputStream(), "UTF-8");
      buf = new BufferedReader(isr);
      // write
      outputData = buf.readLine();
      if (out != null) {
        out.close();
      }
      isr.close();
      buf.close();
      return new ContentResponse(Response.CLIENT_SUCCESS, outputData);
    } catch (MalformedURLException e) {
      e.printStackTrace();
    } catch (ProtocolException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (conn != null) {
        conn.disconnect();
      }
    }
    return new ContentResponse(Response.CLIENT_ERROR_CAN_NOT_CONNECTION,
        outputData);
  }

  public boolean updateBackendSetting() {
    ResponseData responseData = new ResponseData();

    UserPreferences userPreferences = UserPreferences.getInstance();
    String token = userPreferences.getToken();
    GetBackendSettingRequest request = new GetBackendSettingRequest(token);
    String data = request.toString();

    LogUtils.d(TAG, "GetBackendSettingRequest: " + data);
    if (!Utility.isNetworkConnected(mContext)) {
      responseData.setStatus(Response.CLIENT_ERROR_NO_CONNECTION);
      return false;
    }

    ContentResponse outData = sendRequest(Config.SERVER_URL, Method.POST,
        data, Config.TIMEOUT_CONNECT, Config.TIMEOUT_READ);

    if (outData.status == Response.CLIENT_SUCCESS) {
      responseData.setStatus(Response.CLIENT_SUCCESS);
      responseData.setText(outData.content);

      LogUtils.d(TAG, "ReceiveData=" + outData.content);
      if (!TextUtils.isEmpty(responseData.getText())) {
        try {
          responseData.makeJSONObject();
          int code = getResponseCode(responseData.getJSONObject());
          if (code == Response.SERVER_SUCCESS) {
            GetBackendSettingResponse backendSettingResponse = new GetBackendSettingResponse(
                responseData);
            BackendSetting backendSetting = backendSettingResponse.getBackendSetting();
            if (backendSetting != null) {
              saveBackendInfo(backendSetting);
            } else {
              LogUtils.d(TAG, "get backend setting response is empty");
            }
            return true;
          } else {
            performErrorCodeFromServer(code);
            return false;
          }
        } catch (JSONException e) {
          e.printStackTrace();
          outData.status = Response.CLIENT_ERROR_PARSE_JSON;
          responseData.setStatus(outData.status);
        }
      }
    }

    return false;
  }

  private void saveBackendInfo(BackendSetting backendSetting) {
    Preferences preferences = Preferences.getInstance();
    GoogleReviewPreference reviewPreference = new GoogleReviewPreference();

    preferences.saveDailyBonusPoints(backendSetting.getDailyBonusPoints());
    preferences.saveSaveImagePoints(backendSetting.getSaveImagePoints());
    preferences.saveOnlineAlertPoints(backendSetting.getOnlineAlertPoints());
    preferences.saveViewImageChatPoint(backendSetting.getViewImagePoint());
    preferences.saveTimeBackstage(backendSetting.getBackstageTime());
    preferences.saveBackstagePrice(backendSetting.getBackstagePrice());
    preferences.saveCommentPoint(backendSetting.getCommentBuzzPoint());

    reviewPreference.saveTurnOffVersion(backendSetting.getSwitchBrowserVersion());
    reviewPreference.saveEnableGetFreePoint(!backendSetting.isEnableGetFreePoint());
    reviewPreference.saveIsTurnOffUserInfo(!backendSetting.isTurnOffUserInfo());
  }

  public boolean relogin() {
    ResponseData responseData = new ResponseData();

    // Authentication data
    UserPreferences userPreferences = UserPreferences.getInstance();
    String email = userPreferences.getRegEmail();
    String facebookId = userPreferences.getFacebookId();
    String mocomId = userPreferences.getMocomId();
    String famuId = userPreferences.getFamuId();
    String pass = userPreferences.getPassword();

    // Get basic login data
    String device_id = Utility.getDeviceId(mContext);
//		String notify_token = Preferences.getInstance().getGCMResitrationId();
    String notify_token = FirebaseInstanceId.getInstance().getToken();
    String login_time = Utility.getLoginTime();
    String appVersion = Utility.getAppVersionName(mContext);
    LoginRequest loginRequest = null;
    String adjustAdid = "";
    String applicationName = Utility.getApplicationName(mContext);
    adjustAdid = Preferences.getInstance().getAdjustAdid();
    if (!TextUtils.isEmpty(email)) {
      loginRequest = new LoginByEmailRequest(email, pass, device_id,
          notify_token, login_time, appVersion, AndGApp.advertId, AndGApp.device_name,
          AndGApp.os_version, adjustAdid, applicationName);
    } else if (!TextUtils.isEmpty(facebookId)) {
      loginRequest = new LoginByFacebookRequest(facebookId, device_id,
          notify_token, login_time, appVersion, AndGApp.advertId, AndGApp.device_name,
          AndGApp.os_version, adjustAdid, applicationName);
    } else if (!TextUtils.isEmpty(mocomId)) {
//			loginRequest = new LoginByMocomRequest(mocomId, device_id,
//					notify_token, login_time, appVersion);
    } else if (!TextUtils.isEmpty(famuId)) {
//			loginRequest = new LoginByFamuRequest(famuId, device_id,
//					notify_token, login_time, appVersion);
    } else {
      responseData.setStatus(Response.CLIENT_ERROR_NO_DATA);
      LogUtils.d(TAG, "Relogin: don't enough user info for login!");
      return false;
    }

    String data = loginRequest.toString();

    LogUtils.d(TAG, "Relogin: " + data);
    if (!Utility.isNetworkConnected(mContext)) {
      responseData.setStatus(Response.CLIENT_ERROR_NO_CONNECTION);
      return false;
    }
    ContentResponse outData = sendRequest(Config.SERVER_URL, Method.POST,
        data, Config.TIMEOUT_CONNECT, Config.TIMEOUT_READ);

    if (outData.status == Response.CLIENT_SUCCESS) {
      responseData.setStatus(Response.CLIENT_SUCCESS);
      responseData.setText(outData.content);

      LogUtils.d(TAG, "ReceiveData=" + outData.content);
      if (!TextUtils.isEmpty(responseData.getText())) {
        try {
          responseData.makeJSONObject();
          int code = getResponseCode(responseData.getJSONObject());
          if (code == Response.SERVER_SUCCESS) {
            LoginResponse loginResponse = new LoginResponse(
                responseData);
            performTasksWhenLoginSuccess(loginResponse, false);
            // when invalid_token if reLogin then show popup news.
            userPreferences.setShowNewsPopup(loginResponse.isShowNews());
            return true;
          } else {
            performErrorCodeFromServer(code);
            return false;
          }
        } catch (JSONException e) {
          e.printStackTrace();
          outData.status = Response.CLIENT_ERROR_PARSE_JSON;
          responseData.setStatus(outData.status);
        }
      }
    }
    return false;
  }

  public void performErrorCodeFromServer(int code) {
    switch (code) {
      case Response.SERVER_INCORRECT_PASSWORD:
        onErrorIncorrectPassword();
        break;
      case Response.SERVER_LOOKED_USER:
        onErrorDisableUser();
        break;
      case Response.SERVER_INVALID_EMAIL:
      case Response.SERVER_EMAIL_NOT_FOUND:
        onErrorEmail();
        break;
      case Response.SERVER_OUT_OF_DATE_API:
        onErrorOldAPI();
        break;
      case Response.SERVER_OLD_VERSION:
        onErrorOldVersion();
        break;
      default:
        // Do nothing
    }
  }

  private void onErrorIncorrectPassword() {
    Intent intent = new Intent(BaseFragmentActivity.ACTION_INVALID_PASSWORD);
    LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
  }

  private void onErrorDisableUser() {
    Intent intent = new Intent(BaseFragmentActivity.ACTION_ACCOUNT_DISABLED);
    LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
  }

  private void onErrorEmail() {
    Intent intent = new Intent(BaseFragmentActivity.ACTION_INVALID_EMAIl);
    LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
  }

  private void onErrorOldAPI() {
    Intent intent = new Intent(BaseFragmentActivity.ACTION_INVALID_API);
    LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
  }

  private void onErrorOldVersion() {
    Intent intent = new Intent(BaseFragmentActivity.ACTION_INVALID_VERSION);
    LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
  }

  private void performTasksWhenLoginSuccess(LoginResponse loginResponse,
      boolean isFirstLogin) {
    // Save info when login success
    AuthenticationData authenData = loginResponse.getAuthenticationData();
    AuthenticationUtil.saveAuthenticationSuccessData(authenData,
        isFirstLogin);

    // Save blocked users list
    String blockUser = loginResponse.getBlockedUsersList();
    BlockUserPreferences.getInstance().saveBlockedUsersList(blockUser);

    // Start Call Service
    LinphoneService.startLogin(mContext);

    // Start Chat Service
    Intent intent = new Intent(mContext, ChatService.class);
    intent.putExtra(ChatService.EXTRA_OPTION, ChatService.AUTHEN);
    mContext.startService(intent);

    // Start load Notification Setting
    DataFetcherService.startLoadNotificationSetting(mContext);

    GoogleReviewPreference googleReviewPreference = new GoogleReviewPreference();
    googleReviewPreference.saveTurnOffVersion(loginResponse
        .getSwitchBrowserVersion());
    googleReviewPreference.saveEnableGetFreePoint(loginResponse
        .isEnableGetFreePoint());
    googleReviewPreference.saveIsTurnOffUserInfo(loginResponse.isTurnOffUserInfo());
  }

  private class ContentResponse {

    private int status;
    private String content;

    public ContentResponse(int status, String content) {
      super();
      this.status = status;
      this.content = content;
    }
  }
}