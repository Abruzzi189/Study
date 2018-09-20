package com.application.payment;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.ServiceConnection;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.content.Loader;
import com.android.vending.billing.IInAppBillingService;
import com.application.connection.Request;
import com.application.connection.RequestBuilder;
import com.application.connection.RequestType;
import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.application.connection.ResponseReceiver;
import com.application.connection.request.ConfirmPurchaseRequest;
import com.application.connection.request.ListPointActionPacketRequest;
import com.application.connection.request.PointPackageRequest;
import com.application.connection.response.ConfirmPurchaseResponse;
import com.application.connection.response.ListPointActionPacketResponse;
import com.application.connection.response.PointPackageResponse;
import com.application.constant.Constants;
import com.application.util.LogUtils;
import com.application.util.preferece.PurchasePreferences;
import com.application.util.preferece.UserPreferences;
import com.ntq.adjust.AdjustSdk;
import glas.bbsystem.BuildConfig;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.json.JSONException;
import org.json.JSONObject;

public class PurchaseHandler {

  public static final int REQUEST_PAYMENT_CODE = 1000;
  public static final int REQUEST_PURCHASE_LIST = 1001;
  // Billing response codes
  public static final int BILLING_RESPONSE_RESULT_OK = 0;
  public static final int BILLING_RESPONSE_RESULT_USER_CANCELED = 1;
  public static final int BILLING_RESPONSE_RESULT_BILLING_UNAVAILABLE = 3;
  public static final int BILLING_RESPONSE_RESULT_ITEM_UNAVAILABLE = 4;
  public static final int BILLING_RESPONSE_RESULT_DEVELOPER_ERROR = 5;
  public static final int BILLING_RESPONSE_RESULT_ERROR = 6;
  public static final int BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED = 7;
  public static final int BILLING_RESPONSE_RESULT_ITEM_NOT_OWNED = 8;
  // Keys for the responses from InAppBillingService
  public static final String RESPONSE_CODE = "RESPONSE_CODE";
  public static final String RESPONSE_GET_SKU_DETAILS_LIST = "DETAILS_LIST";
  public static final String RESPONSE_BUY_INTENT = "BUY_INTENT";
  public static final String RESPONSE_INAPP_PURCHASE_DATA = "INAPP_PURCHASE_DATA";
  public static final String RESPONSE_INAPP_SIGNATURE = "INAPP_DATA_SIGNATURE";
  public static final String RESPONSE_INAPP_ITEM_LIST = "INAPP_PURCHASE_ITEM_LIST";
  public static final String RESPONSE_INAPP_PURCHASE_DATA_LIST = "INAPP_PURCHASE_DATA_LIST";
  public static final String RESPONSE_INAPP_SIGNATURE_LIST = "INAPP_DATA_SIGNATURE_LIST";
  public static final String INAPP_CONTINUATION_TOKEN = "INAPP_CONTINUATION_TOKEN";
  public static final String INAPP_PURCHASE_ITEM_LIST = "INAPP_PURCHASE_ITEM_LIST";
  public static final String INAPP_PURCHASE_DATA_LIST = "INAPP_PURCHASE_DATA_LIST";
  public static final String INAPP_DATA_SIGNATURE_LIST = "INAPP_DATA_SIGNATURE_LIST";
  // Item types
  public static final String ITEM_TYPE_INAPP = "inapp";
  // some fields on the getSkuDetails response bundle
  public static final String GET_SKU_DETAILS_ITEM_LIST = "ITEM_ID_LIST";
  private static final int VERSION = 3;
  private static final String TAG = "PaymentHandler";
  private static final int LOADER_ID_POINT_PACKAGE = 1000;
  private static final int LOADER_ID_CONFIRM_ADD_POINT = 1001;
  private static final int LOADER_ID_LIST_ACTION_POINT_PACKAGE = 1002;
  private static final String KEY_PAYLOAD = "key_payload";
  private Context mContext;
  private boolean isServiceConnected = false;
  private OnPointPackagePayment mOnPointPackagePurchaseListener;
  private IInAppBillingService mService;
  private String mDeveloperPayload = "";
  private List<PointPackage> mPointPackageList;
  private String transactionId;
  private int mActionType;
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
      if (loaderID == LOADER_ID_POINT_PACKAGE) {
        return new PointPackageResponse(data);
      } else if (loaderID == LOADER_ID_CONFIRM_ADD_POINT) {
        return new ConfirmPurchaseResponse(data);
      } else if (loaderID == LOADER_ID_LIST_ACTION_POINT_PACKAGE) {
        return new ListPointActionPacketResponse(data);
      }
      return null;

    }

    @Override
    public void onBaseLoaderReset(Loader<Response> loader) {

    }
  };
  ServiceConnection mServiceConnection = new ServiceConnection() {

    @Override
    public void onServiceDisconnected(ComponentName name) {
      mService = null;
      isServiceConnected = false;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
      mService = IInAppBillingService.Stub.asInterface(service);
      isServiceConnected = true;

      if (mActionType == Constants.PACKAGE_DEFAULT) {
        String token = UserPreferences.getInstance().getToken();
        PointPackageRequest pointPackageRequest = new PointPackageRequest(
            token);
        Request request = RequestBuilder.getInstance().makeRequest(
            RequestType.JSON, pointPackageRequest, mResponseReceiver,
            LOADER_ID_POINT_PACKAGE);

        // fix issue #12342 async delayed execute
        new PointPackageLoader().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, request);
      } else {
        String token = UserPreferences.getInstance().getToken();
        ListPointActionPacketRequest listPointActionPacketRequest = new ListPointActionPacketRequest(
            token, mActionType);
        Request request = RequestBuilder.getInstance().makeRequest(
            RequestType.JSON, listPointActionPacketRequest, mResponseReceiver,
            LOADER_ID_LIST_ACTION_POINT_PACKAGE);

        // fix issue #12342 async delayed execute
        new ListPointActionPacketLoader()
            .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, request);
      }

    }
  };

  public PurchaseHandler(Context context, int actionType) {
    mContext = context;
    mActionType = actionType;
    bindPayment();
  }

  public void setOnPointPackagePaymentListener(
      OnPointPackagePayment onPointPackagePayment) {
    mOnPointPackagePurchaseListener = onPointPackagePayment;
  }

  public void setRestorePayload(String payload) {
    mDeveloperPayload = payload;
  }

  private void bindPayment() {
    Intent serviceIntent = new Intent(
        "com.android.vending.billing.InAppBillingService.BIND");
    serviceIntent.setPackage("com.android.vending");
    List<ResolveInfo> resolveInfos = mContext.getPackageManager()
        .queryIntentServices(serviceIntent, 0);
    if (resolveInfos != null && !resolveInfos.isEmpty()) {
      // service available to handle that Intent
      mContext.bindService(serviceIntent, mServiceConnection,
          Context.BIND_AUTO_CREATE);
    } else {
      // no service available to handle that Intent
      if (mOnPointPackagePurchaseListener != null) {
        mOnPointPackagePurchaseListener
            .onGetPointPackageFailure(Response.CLIENT_ERROR_BILLING_UNAVAIABLE);
      }
    }
  }

  private List<PointPackage> mergePointPackageWithSkuDetails(List<PointPackage> pointPackageList,
      List<SkuDetails> skuList) {
    if (pointPackageList == null || skuList == null) {
      return null;
    }

    List<PointPackage> mergeList = new ArrayList<>();
    int size = pointPackageList.size() <= skuList.size() ? pointPackageList.size() : skuList.size();
    for (int i = 0; i < size; i++) {
      for (SkuDetails skuDetails : skuList) {
        PointPackage pointPackage = pointPackageList.get(i);
        if (skuDetails.getSku().equals(pointPackage.getProductId())) {
          pointPackage.setPrice(skuDetails.getPrice());
          pointPackage.setAmount(skuDetails.getAmount());
          pointPackage.setCurrency(skuDetails.getCurrency());
//                    pointPackage.setDescription(skuDetails.getDescription());
          mergeList.add(pointPackage);
          break;
        }
      }
    }
    return mergeList;
  }

  private Bundle getSkuDetails(String[] productId) {
    if (productId == null) {
      return null;
    }
    ArrayList<String> skuList = new ArrayList<>(Arrays.asList(productId));
    Bundle querySkus = new Bundle();
    querySkus.putStringArrayList(GET_SKU_DETAILS_ITEM_LIST, skuList);
    try {
      return mService.getSkuDetails(VERSION, mContext.getPackageName(),
          ITEM_TYPE_INAPP, querySkus);
    } catch (RemoteException e) {
      LogUtils.e(TAG, String.valueOf(e.getMessage()));
      e.printStackTrace();
    }
    return null;
  }

  /**
   * @param skuDetails input data
   * @param packageResponse to set error if response code = BILLING_RESPONSE_RESULT_OK
   * @return null: unable to purchase else otherwise
   */
  @Nullable
  private List<SkuDetails> parseSkuDetails(Bundle skuDetails, Response packageResponse) {
    if (skuDetails == null) {
      return null;
    }
    List<SkuDetails> listSkuDetails = null;
    int response = skuDetails.getInt(RESPONSE_CODE);
    if (response == BILLING_RESPONSE_RESULT_OK) {
      ArrayList<String> responseList = skuDetails
          .getStringArrayList(RESPONSE_GET_SKU_DETAILS_LIST);
      //HIEPUH thuat toan sap xep
      try {
        for (int i = 0; i < responseList.size() - 1; i++) {
          for (int j = i + 1; j < responseList.size(); j++) {
            JSONObject obj = new JSONObject(responseList.get(i));
            String price = obj.getString("price_amount_micros");
            long a = Long.parseLong(price);
            JSONObject obj1 = new JSONObject(responseList.get(j));
            String price1 = obj1.getString("price_amount_micros");
            long a1 = Long.parseLong(price1);
            if (a1 < a) {
              String k;
              k = responseList.get(i);
              responseList.set(i, responseList.get(j));
              responseList.set(j, k);
            }
          }
        }
      } catch (JSONException e) {
        e.printStackTrace();
      }
      //HIEPUH end
      listSkuDetails = new ArrayList<>();
      try {
        for (String thisResponse : responseList) {
          SkuDetails details;
          details = new SkuDetails(thisResponse);
          listSkuDetails.add(details);
        }
      } catch (JSONException e) {
        e.printStackTrace();
      }
    } else {
      packageResponse.setCode(Response.CLIENT_ERROR_BILLING_UNAVAIABLE);
    }
    return listSkuDetails;
  }

  private String getPackageIdFromProductId(String productId) {
    for (PointPackage iterable : mPointPackageList) {
      if (iterable.getProductId().equalsIgnoreCase(productId)) {
        return iterable.getPackageId();
      }
    }
    return null;
  }

  public void dispose() {
    if (mServiceConnection != null && isServiceConnected) {
      mContext.unbindService(mServiceConnection);
      isServiceConnected = false;
      mServiceConnection = null;
    }
  }

  private void confirmAddPoint(Purchase purchase, String transactionId) {
    UserPreferences userPreferences = UserPreferences.getInstance();
    String token = userPreferences.getToken();
    if (token == null || "".equals(token)) {
      LogUtils.e(TAG, "Token invalid");
      return;
    }
    if (!isAuthenticated(purchase.getPackageName(), purchase.getDeveloperPayload())) {
      LogUtils.e(TAG, "Authentication to confirm invalid");
      return;
    }

    // get packageId of Point from productId
    String packageId = getPackageIdFromProductId(purchase.getSku());
    String purchaseData = purchase.getOriginalJson();
    String signature = purchase.getSignature();
    LogUtils.i(TAG, "packageId: " + packageId);
    LogUtils.i(TAG, "purchaseData: " + purchaseData);
    LogUtils.i(TAG, "signature: " + signature);
    ConfirmPurchaseRequest confirmPurchaseRequest = new ConfirmPurchaseRequest(
        token, packageId, purchaseData, signature, transactionId, BuildConfig.SANDBOX_PURCHASE);

    Request request = RequestBuilder.getInstance().makeRequest(
        RequestType.JSON, confirmPurchaseRequest, mResponseReceiver,
        LOADER_ID_CONFIRM_ADD_POINT);

    if (mOnPointPackagePurchaseListener != null) {
      mOnPointPackagePurchaseListener.onStartPurchaseConfirm();
    }
    new PurchaseConfirmer(purchase, packageId, transactionId)
        .execute(request);
  }

  private String removeMoneyUnit(String price) {
    int length = price.length();
    int prefix = 0;
    for (int i = 0; i < length; i++) {
      if (!isMoneyChar(price.charAt(i))) {
        prefix++;
      } else {
        break;
      }
    }

    int suffix = length - 1;
    for (int i = length - 1; i > 0 - 1; i--) {
      if (!isMoneyChar(price.charAt(i))) {
        suffix--;
      } else {
        break;
      }
    }

    if (prefix >= suffix) {
      return "";
    } else {
      return price.substring(prefix, suffix);
    }
  }

  private boolean isMoneyChar(char c) {
    switch (c) {
      case '0':
      case '1':
      case '2':
      case '3':
      case '4':
      case '5':
      case '6':
      case '7':
      case '8':
      case '9':
      case '.':
        return true;
      default:
        return false;
    }
  }

  private void handlePurchaseResult(int responseCode, String purchaseData,
      String dataSignature) {
    if (purchaseData == null || dataSignature == null) {
      return;
    }
    if (responseCode == BILLING_RESPONSE_RESULT_OK) {
      try {
        Purchase purchase = new Purchase(ITEM_TYPE_INAPP, purchaseData, dataSignature);
        // revenue, currency, receipt, transactionID
        int size = mPointPackageList.size();
        for (int i = 0; i < size; i++) {
          PointPackage pointPackage = mPointPackageList.get(i);
          if (pointPackage.getProductId().equals(purchase.getSku())) {
            AdjustSdk.trackGooglePurchase(pointPackage.getAmount(), pointPackage.getCurrency());
            break;
          }
        }
        confirmAddPoint(purchase, transactionId);
      } catch (JSONException e) {
        e.printStackTrace();
        LogUtils.e(TAG, "handlePurchaseResult has been JSONException");
        if (mOnPointPackagePurchaseListener != null) {
          mOnPointPackagePurchaseListener
              .onConfirmPurchaseFailure(Response.CLIENT_ERROR_PARSE_JSON);
        }
      }

    }
  }

  private boolean isAuthenticated(String packageName, String developerPayload) {
    return mContext.getPackageName().equals(packageName)
        && developerPayload.equals(this.mDeveloperPayload);
  }

  private String generateDeveloperPayload() {
    return UUID.randomUUID().toString();
  }

  public void onActivityResult(int requestCode, int resultCode, Intent intent) {
    if (requestCode == REQUEST_PAYMENT_CODE) {
      if (resultCode == Activity.RESULT_OK) {
        int responseCode = intent.getIntExtra(RESPONSE_CODE, 0);
        String purchaseData = intent
            .getStringExtra(RESPONSE_INAPP_PURCHASE_DATA);
        String dataSignature = intent
            .getStringExtra(RESPONSE_INAPP_SIGNATURE);
        handlePurchaseResult(responseCode, purchaseData, dataSignature);
      }
    }
  }

  public void onSaveInstance(Bundle outSaved) {
    outSaved.putString(KEY_PAYLOAD, mDeveloperPayload);
  }

  public String getPayloadRestored(Bundle bundle) {
    if (bundle != null) {
      return bundle.getString(KEY_PAYLOAD);
    }
    return "";
  }

  public void startPayment(Activity activity, String skuId,
      String transactionId, String pageID) {
    mDeveloperPayload = generateDeveloperPayload();
    Bundle buyIntentBundle;
    try {
      buyIntentBundle = mService.getBuyIntent(VERSION,
          mContext.getPackageName(), skuId, ITEM_TYPE_INAPP,
          mDeveloperPayload);
      int responseCode = buyIntentBundle.getInt(RESPONSE_CODE);
      switch (responseCode) {
        case BILLING_RESPONSE_RESULT_OK:
          PendingIntent pendingIntent = buyIntentBundle
              .getParcelable(RESPONSE_BUY_INTENT);
          this.transactionId = transactionId;
          activity.startIntentSenderForResult(
              pendingIntent.getIntentSender(),
              REQUEST_PAYMENT_CODE, new Intent(), 0, 0, 0);
          break;
        case BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED:
          Bundle ownedItems = mService.getPurchases(VERSION,
              mContext.getPackageName(), ITEM_TYPE_INAPP, null);
          int code = ownedItems.getInt(RESPONSE_CODE);
          if (code == BILLING_RESPONSE_RESULT_OK) {
            ArrayList<String> ownedSkus = ownedItems
                .getStringArrayList(INAPP_PURCHASE_ITEM_LIST);
            ArrayList<String> purchaseDataList = ownedItems
                .getStringArrayList(INAPP_PURCHASE_DATA_LIST);
            ArrayList<String> signatureList = ownedItems
                .getStringArrayList(INAPP_DATA_SIGNATURE_LIST);
            int position = ownedSkus.indexOf(skuId);
            if (position != -1
                && position < purchaseDataList.size()
                && position < signatureList.size()) {
              String skuDetail = purchaseDataList.get(position);
              String dataSignature = signatureList.get(position);
              Purchase purchase = new Purchase(ITEM_TYPE_INAPP,
                  skuDetail, dataSignature);
              // must have this, beacuse try again consume and add
              // point
              mDeveloperPayload = purchase.getDeveloperPayload();
              confirmAddPoint(purchase, transactionId);
            }
          }
          break;
        case BILLING_RESPONSE_RESULT_BILLING_UNAVAILABLE:
          break;
        case BILLING_RESPONSE_RESULT_DEVELOPER_ERROR:
          break;
        case BILLING_RESPONSE_RESULT_ITEM_NOT_OWNED:
          break;
        case BILLING_RESPONSE_RESULT_ITEM_UNAVAILABLE:
          break;
        case BILLING_RESPONSE_RESULT_USER_CANCELED:
          mOnPointPackagePurchaseListener.onConfirmPurchaseCancel(transactionId, pageID);
          break;
        default:
          break;
      }
    } catch (RemoteException e1) {
      e1.printStackTrace();
    } catch (SendIntentException e) {
      e.printStackTrace();
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  public interface OnPointPackagePayment {

    void onStartGetPointPackage();

    void onGetPointPackageSuccess(List<PointPackage> list);

    void onGetPointPackageFailure(int code);

    void onStartPurchaseConfirm();

    void onConfirmPurchaseSuccess(int totalPoint);

    void onConfirmPurchaseFailure(int code);

    void onConfirmPurchaseCancel(String transactionId, String packId);
  }

  private class ListPointActionPacketLoader extends AsyncTask<Request, Void, Response> {

    @Override
    protected Response doInBackground(Request... params) {
      if (params == null || params.length == 0) {
        return null;
      }
      // request point package from AndG Server
      Request request = params[0];
      Response response = request.execute();
      ListPointActionPacketResponse packageResponse = (ListPointActionPacketResponse) response;

      // request point package from GPlay
      Bundle bundle = getSkuDetails(packageResponse.getProductIdList());
      List<SkuDetails> skuList = parseSkuDetails(bundle, packageResponse);

      // merger 2 list: point with money
      List<PointPackage> list = mergePointPackageWithSkuDetails(packageResponse.getPointPackages(),
          skuList);

      packageResponse.setPointPackageList(list);
      return packageResponse;
    }

    @Override
    protected void onPostExecute(Response result) {
      super.onPostExecute(result);
      if (result.getCode() == Response.SERVER_SUCCESS) {
        ListPointActionPacketResponse pointPackageResponse = (ListPointActionPacketResponse) result;
        if (mOnPointPackagePurchaseListener != null) {
          mPointPackageList = pointPackageResponse.getPointPackages();
          mOnPointPackagePurchaseListener
              .onGetPointPackageSuccess(pointPackageResponse.getPointPackages());
        }
      } else {
        if (mOnPointPackagePurchaseListener != null) {
          mOnPointPackagePurchaseListener.onGetPointPackageFailure(result.getCode());
        }
      }
    }
  }

  private class PointPackageLoader extends AsyncTask<Request, Void, PointPackageResponse> {

    @Override
    protected PointPackageResponse doInBackground(Request... params) {
      if (params == null || params.length == 0) {
        return null;
      }

      // request point package from AndG Server
      Request request = params[0];
      Response response = request.execute();
      PointPackageResponse packageResponse = (PointPackageResponse) response;

      // request point package from GPlay
      Bundle bundle = getSkuDetails(packageResponse.getProductIdList());

      // check if unable to purchase
      int responseCode = bundle.getInt(RESPONSE_CODE);
      if (responseCode != BILLING_RESPONSE_RESULT_OK) {
        packageResponse.setCode(Response.CLIENT_ERROR_BILLING_UNAVAIABLE);
      }

      List<SkuDetails> skuList = parseSkuDetails(bundle, packageResponse);

      // merger 2 list: point with money
      List<PointPackage> list = mergePointPackageWithSkuDetails(packageResponse.getPointPackages(),
          skuList);

      packageResponse.setPointPackageList(list);
      return packageResponse;
    }

    @Override
    protected void onPostExecute(PointPackageResponse result) {
      super.onPostExecute(result);
      if (result.getCode() == Response.SERVER_SUCCESS) {
        if (mOnPointPackagePurchaseListener != null) {
          mPointPackageList = result.getPointPackages();
          mOnPointPackagePurchaseListener.onGetPointPackageSuccess(mPointPackageList);
        }
      } else {
        if (mOnPointPackagePurchaseListener != null) {
          mOnPointPackagePurchaseListener.onGetPointPackageFailure(result.getCode());
        }
      }
    }
  }

  private class PurchaseConfirmer extends AsyncTask<Request, Void, Response> {

    private Purchase mPurchase;
    private String packageId;
    private String transactionId;

    public PurchaseConfirmer(Purchase purchase, String packageId,
        String transactionId) {
      mPurchase = purchase;
      this.packageId = packageId;
      this.transactionId = transactionId;
    }

    @Override
    protected Response doInBackground(Request... params) {
      if (params == null || params.length == 0) {
        return null;
      }
      Response response = null;
      try {
        // Add orderdetail to references
        PurchasePreferences preferences = new PurchasePreferences(
            mContext);
        preferences.saveOrderDetail(mPurchase.getOrderId(), packageId,
            mPurchase.getOriginalJson(), mPurchase.getSignature(),
            transactionId);
        // consume product in GPlay
        int responseCode = mService.consumePurchase(VERSION,
            mContext.getPackageName(), mPurchase.getToken());
        LogUtils.d(TAG, "Status consume=" + responseCode);
        if (responseCode == BILLING_RESPONSE_RESULT_OK) {
          // Request add point in AndG Server
          Request request = params[0];
          response = request.execute();
        }
      } catch (Exception execute) {
        execute.printStackTrace();
      }
      return response;
    }

    @Override
    protected void onPostExecute(Response result) {
      super.onPostExecute(result);
      if (mOnPointPackagePurchaseListener != null) {
        if (result != null) {
          if (result.getCode() == Response.SERVER_SUCCESS) {
            // remove order purchase
            PurchasePreferences preferences = new PurchasePreferences(
                mContext);
            preferences.removeOrderId(mPurchase.getOrderId());
            // notify purchase success
            ConfirmPurchaseResponse purchaseResponse = (ConfirmPurchaseResponse) result;
            mOnPointPackagePurchaseListener
                .onConfirmPurchaseSuccess(purchaseResponse
                    .getPoint());
            LogUtils.d("Hiepuh", "mua point ok");
          } else if (result.getCode() == Response.SERVER_ALREADY_PURCHASE) {
            // remove order purchase
            PurchasePreferences preferences = new PurchasePreferences(
                mContext);
            preferences.removeOrderId(mPurchase.getOrderId());
            // notify purchase error
            mOnPointPackagePurchaseListener
                .onConfirmPurchaseFailure(result.getCode());
            LogUtils.d("Hiepuh", "mua point fail");
          } else {
            // notify purchase error
            mOnPointPackagePurchaseListener
                .onConfirmPurchaseFailure(result.getCode());
            LogUtils.d("Hiepuh", "mua point error");
          }
        } else {
          // notify purchase error
          mOnPointPackagePurchaseListener
              .onConfirmPurchaseFailure(Response.SERVER_UNKNOWN_ERROR);
        }
      }
    }
  }
}
