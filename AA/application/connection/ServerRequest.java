package com.application.connection;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import com.application.Config;
import com.application.connection.request.RequestParams;

public class ServerRequest implements LoaderCallbacks<Response> {

  protected static final String EXTRA_REQUEST_TYPE = "EXTRA_REQUEST_TYPE";
  protected static final String EXTRA_REQUEST_DATA = "EXTRA_REQUEST_DATA";
  protected static final String EXTRA_REQUEST_TIMEOUT_CONNECT = "EXTRA_REQUEST_TIMEOUT_CONNECT";
  protected static final String EXTRA_REQUEST_TIMEOUT_READ = "EXTRA_REQUEST_TIMEOUT_READ";

  private LoaderManager loaderMgr;
  private Context mContext;
  private ResponseReceiver responseReceiver;

  public ServerRequest(LoaderManager _loaderMgr, Context _context,
      ResponseReceiver _responseReceiver) {
    loaderMgr = _loaderMgr;
    mContext = _context;
    responseReceiver = _responseReceiver;
  }

  public void initLoader(int loaderID, int requestType, RequestParams data) {
    initLoader(loaderID, requestType, data, Config.TIMEOUT_CONNECT,
        Config.TIMEOUT_READ);
  }

  public void initLoader(int loaderID, int requestType, RequestParams data,
      int timeoutConnect, int timeoutRead) {
    Bundle bundle = new Bundle();
    bundle.putSerializable(EXTRA_REQUEST_DATA, data);
    bundle.putInt(EXTRA_REQUEST_TYPE, requestType);
    bundle.putInt(EXTRA_REQUEST_TIMEOUT_CONNECT, timeoutConnect);
    bundle.putInt(EXTRA_REQUEST_TIMEOUT_READ, timeoutRead);
    loaderMgr.initLoader(loaderID, bundle, this);
  }

  public void restartLoader(int loaderID, int requestType,
      RequestParams data, int timeoutConnect, int timeoutRead) {
    Bundle bundle = new Bundle();
    bundle.putSerializable(EXTRA_REQUEST_DATA, data);
    bundle.putInt(EXTRA_REQUEST_TYPE, requestType);
    bundle.putInt(EXTRA_REQUEST_TIMEOUT_CONNECT, timeoutConnect);
    bundle.putInt(EXTRA_REQUEST_TIMEOUT_READ, timeoutRead);
    loaderMgr.restartLoader(loaderID, bundle, this);
  }

  public void restartLoader(int loaderID, int requestType, RequestParams data) {
    restartLoader(loaderID, requestType, data, Config.TIMEOUT_CONNECT,
        Config.TIMEOUT_READ);
  }

  @Override
  public Loader<Response> onCreateLoader(int loaderID, Bundle bundle) {
    if (responseReceiver != null) {
      responseReceiver.startRequest(loaderID);
    }
    int requestType = bundle.getInt(EXTRA_REQUEST_TYPE);
    int timeConnect = bundle.getInt(EXTRA_REQUEST_TIMEOUT_CONNECT);
    int timeRead = bundle.getInt(EXTRA_REQUEST_TIMEOUT_READ);
    RequestParams generalRequest = (RequestParams) bundle
        .getSerializable(EXTRA_REQUEST_DATA);
    return new DataLoader(mContext, RequestBuilder.getInstance()
        .makeRequest(requestType, generalRequest, responseReceiver,
            loaderID, timeConnect, timeRead));
  }

  @Override
  public void onLoadFinished(Loader<Response> loader, Response response) {
    if (responseReceiver != null && response != null) {
      responseReceiver.receiveResponse(loader, response);
    }
  }

  @Override
  public void onLoaderReset(Loader<Response> loader) {
    if (responseReceiver != null) {
      responseReceiver.onBaseLoaderReset(loader);
    }
  }
}
