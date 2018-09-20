package com.application.connection;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import com.application.util.preferece.Preferences;
import com.application.util.preferece.UserPreferences;

/**
 * Access API in server to get data for request
 *
 * @author tungdx
 */
public class DataLoader extends AsyncTaskLoader<Response> {

  private static final Object OBJECT = new Object();
  private Request request;
  private Response mResponse;

  public DataLoader(Context context, Request request) {
    super(context);
    this.request = request;
  }

  public void setRequest(Request request) {
    this.request = request;
  }

  @Override
  public Response loadInBackground() {
    if (mResponse != null) {
      Response response = request.execute();
      if (response.getCode() == Response.SERVER_SUCCESS) {
        mResponse.appendResponse(response);
        mResponse = mResponse.copyInstance(mResponse);
      } else {
        mResponse.setCode(response.getCode());
        mResponse = mResponse.copyInstance(mResponse);
      }
    } else {
      mResponse = request.execute();
      if (mResponse != null) {
        mResponse.setResponse(mResponse);
      }
    }

    if (mResponse != null) {
      int code = mResponse.getCode();
      if (code == Response.SERVER_INVALID_TOKEN) {
        synchronized (OBJECT) {
          Preferences preferences = Preferences.getInstance();
          if (!preferences.haslogginedEarly()) {
            // Re-login
            RequestBuilder.getInstance().relogin();
          }
        }
        String token = UserPreferences.getInstance().getToken();
        request.setNewToken(token);
        mResponse = request.execute();
      } else if (code == Response.SERVER_CHANGE_BACKEND_SETTING) {
        synchronized (OBJECT) {
          RequestBuilder.getInstance().updateBackendSetting();
          mResponse = request.execute();
        }
      } else {
        RequestBuilder.getInstance().performErrorCodeFromServer(code);
      }
    }

    return mResponse;
  }

  @Override
  protected void onStartLoading() {
    // super.onStartLoading();
    if (mResponse != null) {
      deliverResult(mResponse);
    }
    if (takeContentChanged() || mResponse == null) {
      forceLoad();
    }
  }

  /**
   * {@inheritDoc AsyncTaskLoader}
   */
  @Override
  protected void onStopLoading() {
    // super.onStopLoading();
    cancelLoad();
  }

  @Override
  public void deliverResult(Response data) {
    if (isReset()) {
      releaseResource(data);
      return;
    }
    Response oldResponse = mResponse;
    mResponse = data;
    if (isStarted()) {
      super.deliverResult(data);
    }
    if (oldResponse != null && oldResponse != data) {
      releaseResource(oldResponse);
    }

  }

  @Override
  protected void onReset() {
    // super.onReset();
    onStopLoading();
    if (mResponse != null) {
      releaseResource(mResponse);
    }
  }

  @Override
  public void onCanceled(Response data) {
    super.onCanceled(data);
    releaseResource(data);
  }

  @Override
  public boolean cancelLoad() {
    return super.cancelLoad();
  }

  /**
   * Release any resouce of {@link DataLoader}
   */
  private void releaseResource(Response response) {
    mResponse = null;
  }
}