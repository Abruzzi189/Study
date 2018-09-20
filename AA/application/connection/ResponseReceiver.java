package com.application.connection;

import android.support.v4.content.Loader;

public interface ResponseReceiver {

  public void startRequest(int loaderId);

  public void receiveResponse(Loader<Response> loader, Response response);

  public Response parseResponse(int loaderID, ResponseData data, int requestType);

  public void onBaseLoaderReset(Loader<Response> loader);
}
