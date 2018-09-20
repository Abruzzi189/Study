package com.application.ui.point;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.application.actionbar.NoFragmentActionBar;
import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.application.connection.ResponseReceiver;
import com.application.connection.request.LogPurchaseRequest;
import com.application.connection.response.GetUserStatusResponse;
import com.application.connection.response.LogPurchaseResponse;
import com.application.connection.response.LoginResponse;
import com.application.constant.Constants;
import com.application.payment.PointPackage;
import com.application.payment.PurchaseHandler;
import com.application.payment.PurchaseHandler.OnPointPackagePayment;
import com.application.ui.BaseFragmentActivity;
import com.application.ui.CenterButtonDialogBuilder;
import com.application.ui.customeview.ErrorApiDialog;
import com.application.util.LogUtils;
import com.application.util.preferece.UserPreferences;
import glas.bbsystem.R;
import java.text.MessageFormat;
import java.util.List;


public class BuyPointActivity extends BaseFragmentActivity implements
    OnPointPackagePayment, ResponseReceiver {

  public static final String PARAM_ACTION_TYPE = "param_action_type";
  private static final String TAG = "BuyPointActivity";
  private static final int REQUEST_LOG_PURCHASE = 1;
  protected ListView mListView;
  private PurchaseHandler mPaymentHandler;
  private PointPackageAdapter mPointPackageAdapter;
  private ProgressDialog mProgressDialog;
  private LinearLayout mContent;
  private TextView mtxtEmpty;
  private TextView mTxtUserPoint;
  private NoFragmentActionBar mActionBar;
  private String formatPoint;
  private String tempProductId;
  private int mActionType;
  private String packetId = "";
  private OnPointPaymentSelected mOPointPaymentSelected = new OnPointPaymentSelected() {
    @Override
    public void onPointPaymentSelect(String packetId, String productId) {
      BuyPointActivity.this.packetId = packetId;
      requestLogPurchase(packetId, productId);
    }
  };

  /**
   * handle event click on footer change activity to FreePointActivity
   */
  /*
   * private OnClickListener getPointClickListener = new OnClickListener() {
   *
   * @Override public void onClick(View v) { Intent intent = new
   * Intent(BuyPointActivity.this, FreePointGetActivity.class);
   * intent.putExtra(Constants.FROM_FREE_POINT, Constants.FROM_BUY_POINT);
   * startActivity(intent); } };
   */
  @Override
  protected void onCreate(Bundle bundle) {
    super.onCreate(bundle);

    initActionBar();
    setContentView(getLayoutResId());
    initView();
    initialNotificationVew();
    formatPoint = getResources().getString(R.string.point_suffix);
    mActionType = getIntent().getIntExtra(PARAM_ACTION_TYPE, Constants.PACKAGE_DEFAULT);
    mPaymentHandler = new PurchaseHandler(getApplicationContext(), mActionType);
    mPaymentHandler.setOnPointPackagePaymentListener(this);
    mPaymentHandler.setRestorePayload(mPaymentHandler
        .getPayloadRestored(bundle));
  }

  protected int getLayoutResId() {
    return R.layout.activity_buy_point;
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    mPaymentHandler.onSaveInstance(outState);
  }

  @Override
  protected void onResume() {
    super.onResume();
    int point = UserPreferences.getInstance().getNumberPoint();
    mTxtUserPoint.setText(MessageFormat.format(formatPoint, point));
  }

  private void showDialogTryagain() {
    LayoutInflater inflater = LayoutInflater.from(this);
    View customTitle = inflater.inflate(R.layout.dialog_customize, null);
    android.app.AlertDialog.Builder builder = new CenterButtonDialogBuilder(this, false);
    ((TextView) customTitle.findViewById(R.id.tv_title_dialog_customize))
        .setText(R.string.common_error);
    builder.setCustomTitle(customTitle);
    //builder.setTitle(R.string.common_error);
    builder.setMessage(R.string.can_not_purchase_try_again);
    builder.setPositiveButton(R.string.ok,
        new DialogInterface.OnClickListener() {

          @Override
          public void onClick(DialogInterface dialog, int which) {
            finish();
          }
        });
    builder.setCancelable(false);
    AlertDialog element = builder.show();
    int dividerId = element.getContext().getResources()
        .getIdentifier("android:id/titleDivider", null, null);
    View divider = element.findViewById(dividerId);
    if (divider != null) {
      divider.setBackgroundColor(getResources().getColor(R.color.transparent));
    }
  }

  protected void initView() {
    mContent = (LinearLayout) findViewById(R.id.content);
    mListView = (ListView) findViewById(R.id.activity_buy_point_list);
    mtxtEmpty = (TextView) findViewById(R.id.activity_buy_point_txt_empty);
    updateUI(false);
    LayoutInflater inflater = LayoutInflater.from(this);

    View header = inflater.inflate(R.layout.header_buy_point, null);
    mTxtUserPoint = (TextView) header.findViewById(R.id.point_txt);
    mListView.addHeaderView(header);
  }

  private void updateUI(boolean showContent) {
    if (showContent) {
//			mListView.setVisibility(View.VISIBLE);
      mContent.setVisibility(View.VISIBLE);
      mtxtEmpty.setVisibility(View.GONE);
    } else {
      mtxtEmpty.setVisibility(View.VISIBLE);
//			mListView.setVisibility(View.GONE);
      mContent.setVisibility(View.GONE);
    }
  }

  /**
   * Button close buy point dialog activity onClickListener
   */
  public void close(View v) {
    // does something very interesting
    this.finish();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    if (mPaymentHandler != null) {
      mPaymentHandler.dispose();
    }
  }

  private void dismissDialog() {
    if (mProgressDialog != null) {
      mProgressDialog.dismiss();
    }
  }

  private void showDialog(String msg) {
    mProgressDialog = ProgressDialog.show(this, "", msg, true, false);
  }

  @Override
  public void onStartGetPointPackage() {
    updateUI(false);
    mtxtEmpty.setText(R.string.loading);
  }

  @Override
  public void onGetPointPackageSuccess(List<PointPackage> list) {
    if (list == null) {
      return;
    }
    updateUI(true);
    mPointPackageAdapter = new PointPackageAdapter(list);
    mListView.setAdapter(mPointPackageAdapter);
    mListView.setOnItemClickListener(null);
  }

  @Override
  public void onGetPointPackageFailure(int code) {
    updateUI(false);
    mtxtEmpty.setText(R.string.no_more_items_to_show);
    ErrorApiDialog.showAlert(this, R.string.common_error, code);
  }

  @Override
  public void onStartPurchaseConfirm() {
    showDialog(getString(R.string.waiting));
  }

  @Override
  public void onConfirmPurchaseSuccess(int totalPoint) {
    dismissDialog();
    Toast.makeText(getApplicationContext(), R.string.buy_point_success,
        Toast.LENGTH_LONG).show();
    // update point
    UserPreferences.getInstance().saveNumberPoint(totalPoint);
    mTxtUserPoint.setText(MessageFormat.format(formatPoint, totalPoint));
  }

  @Override
  public void onConfirmPurchaseFailure(int code) {
    dismissDialog();
    ErrorApiDialog.showAlert(this, R.string.common_error, code);
  }

  @Override
  public void onConfirmPurchaseCancel(String transactionId, String packId) {
    UserPreferences preferences = UserPreferences.getInstance();
    String token = preferences.getToken();
    LogPurchaseRequest request = new LogPurchaseRequest(token, packId, transactionId);
    restartRequestServer(REQUEST_LOG_PURCHASE, request);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent arg2) {
    super.onActivityResult(requestCode, resultCode, arg2);
    if (mPaymentHandler != null) {
      mPaymentHandler.onActivityResult(requestCode, resultCode, arg2);
    }
  }

  @Override
  public boolean hasShowNotificationView() {
    return true;
  }

  public int getColorForText(String text) {
    int colorId = R.color.color_hint_bold;
    if (text != null && text.matches(".*\\d.*")) {
      colorId = R.color.color_bg_number_mess;
    }
    return colorId;
  }

  @Override
  public boolean isNoTitle() {
    return false;
  }

  protected void initActionBar() {
    getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
    mActionBar = new NoFragmentActionBar(this);
    mActionBar.syncActionBar();
    mActionBar.setTextCenterColor(R.color.primary);
  }

  private void requestLogPurchase(String packId, String productId) {
    tempProductId = productId;
    UserPreferences preferences = UserPreferences.getInstance();
    String token = preferences.getToken();
    LogPurchaseRequest request = new LogPurchaseRequest(token, packId);
    restartRequestServer(REQUEST_LOG_PURCHASE, request);
  }

  @Override
  public void startRequest(int loaderId) {
    super.startRequest(loaderId);
    showDialog(getString(R.string.waiting));
  }

  @Override
  public void receiveResponse(Loader<Response> loader, Response response) {
    super.receiveResponse(loader, response);

    int responseCode = response.getCode();
    dismissDialog();
    if (responseCode == Response.SERVER_SUCCESS
        && response instanceof LogPurchaseResponse) {
      String tranId = ((LogPurchaseResponse) response).getTransactionId();

      if (mPaymentHandler != null) {
        try {
          mPaymentHandler.startPayment(this, tempProductId, tranId, packetId);
        } catch (Exception exception) {
          exception.printStackTrace();
          LogUtils.e(TAG, "Start payment error");
          showDialogTryagain();
        }
      }
    } else {
      LayoutInflater inflater = LayoutInflater.from(this);
      View customTitle = inflater.inflate(R.layout.dialog_customize, null);
      Builder builder = new Builder(this);
      ((TextView) customTitle.findViewById(R.id.tv_title_dialog_customize))
          .setText(R.string.common_error);
      builder.setCustomTitle(customTitle);
      //builder.setTitle(R.string.common_error);
      builder.setMessage(R.string.msg_common_no_connection);
      builder.setNegativeButton(R.string.common_ok, null);
      AlertDialog dialog = builder.create();
      dialog.show();
      int dividerId = dialog.getContext().getResources()
          .getIdentifier("android:id/titleDivider", null, null);
      View divider = dialog.findViewById(dividerId);
      if (divider != null) {
        divider.setBackgroundColor(getResources().getColor(R.color.transparent));
      }
    }
  }

  @Override
  public Response parseResponse(int loaderID, ResponseData data,
      int requestType) {
    Response response = null;
    switch (loaderID) {
      case REQUEST_LOG_PURCHASE:
        response = new LogPurchaseResponse(data);
        break;
      case LOADER_RETRY_LOGIN:
        response = new LoginResponse(data);
        break;
      case LOADER_GET_USER_STATUS:
        response = new GetUserStatusResponse(data);
        break;

      default:
        response = super.parseResponse(loaderID, data, requestType);
        break;
    }
    return response;
  }

  public interface OnPointPaymentSelected {

    public void onPointPaymentSelect(String packetId, String productId);
  }

  private class PointPackageAdapter extends BaseAdapter {

    private List<PointPackage> pointPackages;

    public PointPackageAdapter(List<PointPackage> pointPackages) {
      this.pointPackages = pointPackages;
    }

    @Override
    public int getCount() {
      return pointPackages.size();
    }

    @Override
    public Object getItem(int position) {
      return pointPackages.get(position);
    }

    @Override
    public long getItemId(int position) {
      return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      ViewHolder holder = null;
      if (convertView == null) {
        holder = new ViewHolder();
        convertView = View.inflate(getApplicationContext(),
            R.layout.item_list_buy_point, null);
        holder.txtDes = (TextView) convertView
            .findViewById(R.id.item_list_buy_point_txt_des);
        holder.txtPoint = (TextView) convertView
            .findViewById(R.id.item_list_buy_point_txt_price);
        holder.btnPrice = (Button) convertView
            .findViewById(R.id.price_btn);
        convertView.setTag(holder);
      } else {
        holder = (ViewHolder) convertView.getTag();
      }

      // point
      final PointPackage pointPackage = pointPackages.get(position);
      holder.txtPoint.setText(MessageFormat.format(formatPoint,
          pointPackage.getPoint()));

      // description
      String text = pointPackage.getText();
      String des = pointPackage.getDescription();
      int color = getResources().getColor(getColorForText(des));
      if (mActionType == Constants.PACKAGE_DEFAULT) {
        holder.txtDes.setText(des);
      } else {
        holder.txtDes.setText(text);
      }

      //    holder.txtDes.setTextColor(color);

      // price
      String price = pointPackage.getPrice();
      holder.btnPrice.setText(price);
      holder.btnPrice.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View v) {
          mOPointPaymentSelected.onPointPaymentSelect(
              pointPackage.getPackageId(),
              pointPackage.getProductId());
        }
      });
      return convertView;
    }

    private class ViewHolder {

      TextView txtPoint;
      TextView txtDes;
      Button btnPrice;
    }
  }
}