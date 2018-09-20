package com.application.payment;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.content.Loader;
import com.application.connection.Request;
import com.application.connection.RequestBuilder;
import com.application.connection.RequestType;
import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.application.connection.ResponseReceiver;
import com.application.connection.request.ConfirmPurchaseRequest;
import com.application.connection.response.ConfirmPurchaseResponse;
import com.application.util.LogUtils;
import com.application.util.preferece.PurchasePreferences;
import com.application.util.preferece.UserPreferences;
import glas.bbsystem.BuildConfig;

public class RetryPurchaseHandler {

  private static final String TAG = "RetryPurchaseHandler";
  private static final int LOADER_ID_RETRY = 1000;
  private Context mContext;
  private OnRetryPurchaseListener mRetryPurchaseListener;
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
      return new ConfirmPurchaseResponse(data);

    }

    @Override
    public void onBaseLoaderReset(Loader<Response> loader) {

    }
  };

  public RetryPurchaseHandler(Context context,
      OnRetryPurchaseListener listener) {
    mContext = context;
    mRetryPurchaseListener = listener;
  }

  public void retryPurchase() {
    PurchasePreferences preferences = new PurchasePreferences(mContext);
    String[] orderList = preferences.getOrderDetailList();
    String token = UserPreferences.getInstance().getToken();
    if (null == orderList || null == token || "".equals(token)) {
      return;
    }
    try {
      for (int i = 0; i < orderList.length; i++) {
        String orderDetail = orderList[i];
        LogUtils.d(TAG, "orderDetail=" + orderDetail);

        String orderId = ""; // Index 0
        String packageId = ""; // Index 1
        String purchaseData = ""; // Index 2
        String signature = ""; // Index 3
        String transactionId = ""; // Index 4

        String[] listData = orderDetail
            .split(PurchasePreferences.DECODE_SOURCE);
        int length = listData.length;
        if (length > 0) {
          orderId = listData[0];
          LogUtils.i(TAG, "orderId:" + orderId);
        }
        if (length > 1) {
          packageId = listData[1];
          LogUtils.i(TAG, "packageId:" + packageId);
        }
        if (length > 2) {
          purchaseData = listData[2];
          LogUtils.i(TAG, "purchaseData:" + purchaseData);
        }
        if (length > 3) {
          signature = listData[3];
          LogUtils.i(TAG, "signature:" + signature);
        }
        if (length > 4) {
          transactionId = listData[4];
          LogUtils.i(TAG, "transactionId:" + transactionId);
        }

        Purchase purchase = new Purchase(
            PurchaseHandler.ITEM_TYPE_INAPP, purchaseData,
            signature);

        ConfirmPurchaseRequest confirmPurchaseRequest = new ConfirmPurchaseRequest(
            token, packageId, purchaseData, signature,
            transactionId, BuildConfig.SANDBOX_PURCHASE);
        Request request = RequestBuilder.getInstance().makeRequest(
            RequestType.JSON, confirmPurchaseRequest,
            mResponseReceiver, LOADER_ID_RETRY);
        new RetryPurchaseConfirmer(mContext, purchase).execute(request);
      }
    } catch (Exception exception) {
      exception.printStackTrace();
    }

  }

  public interface OnRetryPurchaseListener {

    public void onRetryPurchaseStart();

    public void onRetryPurchaseFailure(int code);

    public void onRetryPurchaseSuccess(int totalPoint);
  }

  public class RetryPurchaseConfirmer extends
      AsyncTask<Request, Void, Response> {

    private Context mContext;
    private Purchase mPurchase;

    public RetryPurchaseConfirmer(Context context, Purchase purchase) {
      mPurchase = purchase;
      mContext = context;
    }

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
      if (mRetryPurchaseListener != null) {
        mRetryPurchaseListener.onRetryPurchaseStart();
      }
    }

    @Override
    protected Response doInBackground(Request... params) {
      if (params == null || params.length == 0) {
        return null;
      }
      Response response = null;
      // Request add point in AndG Server
      Request request = params[0];
      response = request.execute();

      return response;
    }

    @Override
    protected void onPostExecute(Response result) {
      super.onPostExecute(result);
      if (result == null) {
        return;
      }
      if (result.getCode() == Response.SERVER_SUCCESS) {
        // remove order purchase
        PurchasePreferences preferences = new PurchasePreferences(
            mContext);
        preferences.removeOrderId(mPurchase.getOrderId());
        // notify purchase success
        if (mRetryPurchaseListener != null) {
          ConfirmPurchaseResponse purchaseResponse = (ConfirmPurchaseResponse) result;
          mRetryPurchaseListener
              .onRetryPurchaseSuccess(purchaseResponse.getPoint());
        }
      } else if (result.getCode() == Response.SERVER_ALREADY_PURCHASE) {
        // remove order purchase
        PurchasePreferences preferences = new PurchasePreferences(
            mContext);
        preferences.removeOrderId(mPurchase.getOrderId());
        // notify purchase error
        if (mRetryPurchaseListener != null) {
          mRetryPurchaseListener.onRetryPurchaseFailure(result
              .getCode());
        }
      } else {
        if (mRetryPurchaseListener != null) {
          // notify purchase error
          mRetryPurchaseListener.onRetryPurchaseFailure(result
              .getCode());
        }
      }
    }
  }
}
