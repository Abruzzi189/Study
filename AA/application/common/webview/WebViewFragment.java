package com.application.common.webview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.os.Process;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.Purchase;
import com.application.AndGApp;
import com.application.Config;
import com.application.actionbar.CustomActionBar;
import com.application.common.WebviewNavibar;
import com.application.common.WebviewNavibar.IOnNaviButtonClicked;
import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.application.connection.ResponseReceiver;
import com.application.connection.request.CheckTokenRequest;
import com.application.connection.request.ConfirmPurchaseRequest;
import com.application.connection.request.LogPurchaseRequest;
import com.application.connection.response.CheckTokenResponse;
import com.application.connection.response.ConfirmPurchaseResponse;
import com.application.connection.response.GetStaticPageResponse;
import com.application.connection.response.LogPurchaseResponse;
import com.application.constant.Constants;
import com.application.payment.extra.BillingCallback;
import com.application.payment.extra.BillingManager;
import com.application.ui.BaseFragment;
import com.application.ui.BaseFragmentActivity;
import com.application.ui.CustomActionBarActivity;
import com.application.ui.HomeFragment;
import com.application.ui.MainActivity;
import com.application.ui.MyPageFragment;
import com.application.ui.account.EditProfileFragment;
import com.application.ui.customeview.CustomConfirmDialog;
import com.application.ui.customeview.ErrorApiDialog;
import com.application.ui.point.BuyPointActivity;
import com.application.ui.profile.MyProfileFragment;
import com.application.util.FreePageUtil;
import com.application.util.LogUtils;
import com.application.util.preferece.UserPreferences;
import com.example.tux.mylab.MediaPickerBaseActivity;
import com.example.tux.mylab.camera.Camera;
import com.example.tux.mylab.gallery.Gallery;
import com.example.tux.mylab.gallery.data.MediaFile;
import com.example.tux.mylab.utils.Utils;
import glas.bbsystem.BuildConfig;
import glas.bbsystem.R;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class WebViewFragment extends BaseFragment implements
    IOnNaviButtonClicked, ResponseReceiver, BillingCallback {

  //Package
  public static final String PACKAGE_LINE = "jp.naver.line.android";
  public static final String PACKAGE_TWITTER = "com.twitter.android";
  public static final String EMAIL_SUBJECT = "";
  public static final String EMAIL_ADDRESS = "";
  // Intent key
  public static final String INTENT_PAGE_TYPE = "page_type";
  public static final String INTENT_PAGE_URL = "page_url";
  public static final String INTENT_PAGE_CONTENT = "page_content";
  public static final String INTENT_PAGE_TITLE = "page_title";
  public static final String INTENT_LOGIN_MOCOM_ID = "mocom_id";
  public static final String INTENT_LOGIN_FAMU_ID = "famu_id";
  // Page type
  public static final int PAGE_TYPE_NOT_SET = 0;
  public static final int PAGE_TYPE_WEB_VIEW = 1;
  public static final int PAGE_TYPE_LOGIN_OTHER_SYS = 2;
  public static final int PAGE_TYPE_TERM_OF_SERVICE = 3;
  public static final int PAGE_TYPE_PRIVACY_POLICY = 4;
  public static final int PAGE_TYPE_TERM_OF_USE = 5;
  public static final int PAGE_TYPE_VERIFY_AGE = 6;
  public static final int PAGE_TYPE_AUTO_VERIFY_AGE = 7;
  public static final int PAGE_TYPE_ANDG_HOMEPAGE = 8;
  public static final int PAGE_TYPE_ABOUT_PAYMENT = 9;
  public static final int PAGE_TYPE_HOW_TO_USE = 10;
  public static final int PAGE_TYPE_SUPPORT = 11;
  public static final int PAGE_TYPE_FREE_POINT = 12;
  public static final int PAGE_TYPE_BUY_PONIT = 13;
  public static final int PAGE_TYPE_CONTACT = 14;
  public static final int PAGE_TYPE_NOTICE = 15;
  public static final int PAGE_TYPE_INFORMATION = 19;
  public static final int PAGE_TYPE_NEWS = 16;
  public static final int PAGE_TYPE_URL_CMCODE = 20;
  public static final String PRODUCT_ID = "productid";
  public static final String PCK_ID = "packageid";
  // Static page type to server
  // @param int: 0: Term of Service | 1: Privacy Policy | 2: Term of use
  public static final int STATIC_TYPE_TERM_OF_SERVICE = 0;
  public static final int STATIC_TYPE_PRIVACY_POLICY = 1;
  public static final int STATIC_TYPE_TERM_OF_USE = 2;
  private static final int CONFIRM_PURCHASE_LOADER = 99;
  private static final int LOG_BEFORE_PURCHASE_LOADER = 100;
  private static final String DOMAIN_WEB_STG = "http://stg.app.switch-app.net";
  private static final String DOMAIN_WEB_PRO = "http://app.switch-app.net";
  private static final String LINK_GOOGLE_MAKET = "market://details?id=%1$s";
  private static final String LINK_GOOGLE_PLAY = "https://play.google.com/store/apps/details?id=%1$s";
  private static final int REQUEST_FILECHOOSER = 2888;
  public static String EMAIL_CONTENT = "";
  public static String SMS_CONTENT = "";
  // Act intent key
  public final String ACT_INTENT = "act";
  public final String TOKEN_INTENT = "%%token%%";
  private final String TAG = "WebViewFragment";
  private final String ERROR_INVALID_URL = "Invalid URL";
  // Loader identify
  private final int LOADER_ID_LOAD_STATIC_PAGE = 0;
  private final int LOADER_ID_CHECK_TOKEN = 1;
  // Link defined
  private final String LINK_LOGIN_OTHER_SYS = "http://api.bbsystem-app.com/";
  //    private final String LINK_NOTICE = "http://app.switch-app.net/notice.php?sid=%%token%%";
//    private final String LINK_NOTICE_STG = "http://stg.app.switch-app.net/notice.php?sid=%%token%%";
  private final String LINK_NOTICE = DOMAIN_WEB_PRO + "/notice.php?sid=%%token%%";
  private final String LINK_NOTICE_STG = DOMAIN_WEB_STG + "/notice.php?sid=%%token%%";
  //    private final String LINK_TERM_OF_USE_REGISTER = "http://app.switch-app.net/agreement.php";
//    private final String LINK_TERM_OF_USE_REGISTER_STG = "http://stg.app.switch-app.net/agreement.php";
  private final String LINK_TERM_OF_USE_REGISTER = DOMAIN_WEB_PRO + "/agreement.php";
  private final String LINK_TERM_OF_USE_REGISTER_STG = DOMAIN_WEB_STG + "/agreement.php";
  //    private final String LINK_TERM_OF_USE = "http://app.switch-app.net/agreement.php?sid=%%token%%";
//    private final String LINK_TERM_OF_USE_STG = "http://stg.app.switch-app.net/agreement.php?sid=%%token%%";
  private final String LINK_TERM_OF_USE = DOMAIN_WEB_PRO + "/agreement.php?sid=%%token%%";
  private final String LINK_TERM_OF_USE_STG = DOMAIN_WEB_STG + "/agreement.php?sid=%%token%%";
  //    private final String LINK_TERM_OF_SERVICE = "http://app.switch-app.net/agreement.php";
//    private final String LINK_TERM_OF_SERVICE_STG = "http://stg.app.switch-app.net/agreement.php";
  private final String LINK_TERM_OF_SERVICE = DOMAIN_WEB_PRO + "/agreement.php";
  private final String LINK_TERM_OF_SERVICE_STG = DOMAIN_WEB_STG + "/agreement.php";
  //    private final String LINK_PRIVACY_REGISTER = "http://app.switch-app.net/privacy.php";
//    private final String LINK_PRIVACY_REGISTER_STG = "http://stg.app.switch-app.net/privacy.php";
  private final String LINK_PRIVACY_REGISTER = DOMAIN_WEB_PRO + "/privacy.php";
  private final String LINK_PRIVACY_REGISTER_STG = DOMAIN_WEB_STG + "/privacy.php";
  //    private final String LINK_PRIVACY = "http://app.switch-app.net/privacy.php?sid=%%token%%";
//    private final String LINK_PRIVACY_STG = "http://stg.app.switch-app.net/privacy.php?sid=%%token%%";
  private final String LINK_PRIVACY = DOMAIN_WEB_PRO + "/privacy.php?sid=%%token%%";
  private final String LINK_PRIVACY_STG = DOMAIN_WEB_STG + "/privacy.php?sid=%%token%%";
  //    private final String LINK_HOW_TO_USE = "http://app.switch-app.net/howto.php?sid=%%token%%";
//    private final String LINK_HOW_TO_USE_STG = "http://stg.app.switch-app.net/howto.php?sid=%%token%%";
  private final String LINK_HOW_TO_USE = DOMAIN_WEB_PRO + "/howto.php?sid=%%token%%";
  private final String LINK_HOW_TO_USE_STG = DOMAIN_WEB_STG + "/howto.php?sid=%%token%%";
  //    private final String LINK_SUPPORT = "http://app.switch-app.net/help.php?sid=%%token%%";
//    private final String LINK_SUPPORT_STG = "http://stg.app.switch-app.net/help.php?sid=%%token%%";
  private final String LINK_SUPPORT = DOMAIN_WEB_PRO + "/help.php?sid=%%token%%";
  private final String LINK_SUPPORT_STG = DOMAIN_WEB_STG + "/help.php?sid=%%token%%";
  // private final String LINK_VERIFY_AGE =
  // "http://bbsystem-app.com/auth/?sid=%%token%%";
  private final String LINK_AUTO_VERIFY_AGE = "http://bbsystem-app.com/auth/sample/?sid=%%token%%";
  private final String LINK_AUTO_VERIFY_AGE_TEST = "http://202.221.140.192/dummy_page/age_vertification.html";
  private final String LINK_ANDG_HOMEPAGE = "http://and-g.info";
  private final String LINK_ABOUT_PAYMENT = "http://202.221.140.200/point/explain/?sid=%%token%%";
  //    private final String LINK_HOW_TO_USE_FEMALE = "http://app.switch-app.net/manual/manual_photo_01.php?sid=%%token%%";
//    private final String LINK_HOW_TO_USE_FEMALE_STG = "http://stg.app.switch-app.net/manual/manual_photo_01.php?sid=%%token%%";
  private final String LINK_HOW_TO_USE_FEMALE =
      DOMAIN_WEB_PRO + "/manual/manual_photo_01.php?sid=%%token%%";
  private final String LINK_HOW_TO_USE_FEMALE_STG =
      DOMAIN_WEB_STG + "/manual/manual_photo_01.php?sid=%%token%%";
  //    private final String LINK_FREE_POINT = "http://app.switch-app.net/otameshi_select.php?sid=%%token%%";
//    private final String LINK_FREE_POINT_STG = "http://stg.app.switch-app.net/otameshi_select.php?sid=%%token%%";
  private final String LINK_FREE_POINT = DOMAIN_WEB_PRO + "/otameshi_select.php?sid=%%token%%";
  private final String LINK_FREE_POINT_STG = DOMAIN_WEB_STG + "/otameshi_select.php?sid=%%token%%";
  //    private final String LINK_FREE_POINT_TEST = "http://app.switch-app.net/test/otameshi_select.php?sid=%%token%%";
//    private final String LINK_FREE_POINT_TEST_STG = "http://stg.app.switch-app.net/test/otameshi_select.php?sid=%%token%%";
  private final String LINK_FREE_POINT_TEST =
      DOMAIN_WEB_PRO + "/test/otameshi_select.php?sid=%%token%%";
  private final String LINK_FREE_POINT_TEST_STG =
      DOMAIN_WEB_STG + "/test/otameshi_select.php?sid=%%token%%";
  //    private final String LINK_BUY_POINT = "http://app.switch-app.net/point.php?sid=%%token%%";
//    private final String LINK_BUY_POINT_STG = "http://stg.app.switch-app.net/point.php?sid=%%token%%";
  private final String LINK_BUY_POINT = DOMAIN_WEB_PRO + "/point.php?sid=%%token%%";
  private final String LINK_BUY_POINT_STG = DOMAIN_WEB_STG + "/point.php?sid=%%token%%";
  //    private final String LINK_INFORMATION = "http://app.switch-app.net/information.php?sid=%%token%%";
//    private final String LINK_INFORMATION_STG = "http://stg.app.switch-app.net/information.php?sid=%%token%%";
  private final String LINK_INFORMATION = DOMAIN_WEB_PRO + "/information.php?sid=%%token%%";
  private final String LINK_INFORMATION_STG = DOMAIN_WEB_STG + "/information.php?sid=%%token%%";
  /* CUONGNV : Link contact phía dưới cần bổ sung thêm. Hiện tại đang dùng tạm với link support */
//    private final String LINK_CONTACT = "http://app.switch-app.net/contact.php?sid=%%token%%";
//    private final String LINK_CONTACT_STG = "http://stg.app.switch-app.net/contact.php?sid=%%token%%";
  private final String LINK_CONTACT = DOMAIN_WEB_PRO + "/contact.php?sid=%%token%%";
  private final String LINK_CONTACT_STG = DOMAIN_WEB_STG + "/contact.php?sid=%%token%%";
  // View
  private WebviewNavibar navibar;
  private WebView webView;
  private ProgressBar progressBar;
  private ProgressDialog progressDialog;
  // Data
  private int mPageType;
  private String mUrl = "";
  private String mContent = "";
  private String mTitle = "";
  private String startUrl = "";
  private ValueCallback<Uri> mUploadMessage;
  private Uri mCapturedImageURI = null;
  private BillingManager mBillingManager;
  private String productId;
  private String pckId;

  public static String getGooglePlayLink(String packageName) {
    return String.format(LINK_GOOGLE_PLAY, String.valueOf(packageName));
  }

  public static String getGoogleMaketLink(String packageName) {
    return String.format(LINK_GOOGLE_MAKET, String.valueOf(packageName));
  }

  public static WebViewFragment newInstance(String url, String content,
      String title) {
    WebViewFragment fragment = new WebViewFragment();
    fragment.setArguments(newBundle(PAGE_TYPE_WEB_VIEW, url, content, title));
    return fragment;
  }

  public static WebViewFragment newInstance(String url, String content) {
    WebViewFragment fragment = new WebViewFragment();
    fragment.setArguments(newBundle(PAGE_TYPE_WEB_VIEW, url, content, ""));
    return fragment;
  }

  public static WebViewFragment newInstance(int pageType, String url,
      String title) {
    WebViewFragment fragment = new WebViewFragment();
    fragment.setArguments(newBundle(pageType, url, "", title));
    return fragment;
  }

  public static WebViewFragment newInstance(int pageType, String title) {
    return newInstance(pageType, "", title);
  }

  public static WebViewFragment newInstance(int pageType) {
    return newInstance(pageType, "", "");
  }


  private static Bundle newBundle(int pageType, String url, String content,
      String title) {
    Bundle bundle = new Bundle();
    bundle.putString(INTENT_PAGE_URL, url);
    bundle.putString(INTENT_PAGE_CONTENT, content);
    bundle.putString(INTENT_PAGE_TITLE, title);
    bundle.putInt(INTENT_PAGE_TYPE, pageType);
    return bundle;
  }

  private void initDataFromBundle(Bundle bundle) {
    mPageType = bundle.getInt(INTENT_PAGE_TYPE);
    mUrl = bundle.getString(INTENT_PAGE_URL);
    if (mUrl == null) {
      mUrl = "";
    }

    mContent = bundle.getString(INTENT_PAGE_CONTENT);
    if (mContent == null) {
      mContent = "";
    }
    mTitle = bundle.getString(INTENT_PAGE_TITLE);
    if (mTitle == null) {
      mTitle = "";
    }
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    Bundle bundle = newBundle(mPageType, mUrl, mContent, mTitle);
    outState.putAll(bundle);
    super.onSaveInstanceState(outState);
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // Initial data from bundle
    if (savedInstanceState != null) {
      initDataFromBundle(savedInstanceState);
    } else {
      initDataFromBundle(getArguments());
    }

    // billing
    mBillingManager = new BillingManager();
    mBillingManager.init(getActivity(), this);
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    // Load URL send to this activity.
    checkPageType();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fg_free_page, container, false);
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    // Initial data from bundle
    if (savedInstanceState != null) {
      initDataFromBundle(savedInstanceState);
    } else {
      initDataFromBundle(getArguments());
    }

    // Initial view
    initView(view);
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    if (progressDialog != null && progressDialog.isShowing()) {
      progressDialog.dismiss();
    }
  }

  @Override
  public void onDestroy() {
    mBillingManager.release();
    super.onDestroy();
  }

  @SuppressLint("SetJavaScriptEnabled")
  private void initView(View v) {
    // Loading view
    progressBar = (ProgressBar) v.findViewById(R.id.progress);

    // Bottom navigation bar
    navibar = (WebviewNavibar) v.findViewById(R.id.webviewnavigation);
    navibar.setOnNaviButtonClicked(this);

    // Content web view
    webView = (WebView) v.findViewById(R.id.webview);
    // Get web setting to setting web view
    WebSettings settings = webView.getSettings();
    // Enable JavaScript
    settings.setJavaScriptEnabled(true);
    // Enable web view Overview mode. Fit larger web content
    settings.setLoadWithOverviewMode(true);
    settings.setAllowFileAccess(true);
    settings.setSupportZoom(true);
    settings.setDefaultTextEncodingName("utf-8");
    webView.setWebViewClient(new WebViewClient() {
      @Override
      public boolean shouldOverrideUrlLoading(WebView view, String url) {
        mUrl = url;
        //hiepuh
        if (url.contains("openweb")) {
          view.getContext().startActivity(
              new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
          return true;
        }

        if (url.contains("line://msg/text/")) {
          Intent intent = mAppContext.getPackageManager().getLaunchIntentForPackage(PACKAGE_LINE);
          if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return true;
          } else {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setData(Uri.parse("market://details?id=" + PACKAGE_LINE));
            startActivity(intent);
            return true;
          }
        }

        if (url.contains("twitter://post")) {
          Intent intent = mAppContext.getPackageManager()
              .getLaunchIntentForPackage(PACKAGE_TWITTER);
          if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return true;
          } else {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setData(Uri.parse("market://details?id=" + PACKAGE_TWITTER));
            startActivity(intent);
            return true;
          }
        }

        if (url.contains("mailto:?body")) {
          EMAIL_CONTENT = getResources().getString(R.string.sms_email_content);
          final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
          emailIntent.setType("plain/text");
          emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{EMAIL_ADDRESS});
          emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, EMAIL_SUBJECT);
          emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, EMAIL_CONTENT);
          getContext().startActivity(Intent.createChooser(emailIntent, "Send mail..."));
          return true;
        }

        if (url.contains("sms:?body")) {
          SMS_CONTENT = getResources().getString(R.string.sms_email_content);
          Intent smsIntent = new Intent(Intent.ACTION_VIEW);
          smsIntent.setType("vnd.android-dir/mms-sms");
          smsIntent.putExtra("sms_body", SMS_CONTENT);
          startActivity(smsIntent);
          return true;
        }

        if (url.startsWith("tel:")) {
          Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
          startActivity(intent);
          view.reload();
          return true;
        }
        if (url.contains("play.google.com")) {
          view.getContext().startActivity(
              new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
          return true;
        }

        if (!checkAction(url)) {
          if (!checkToken(url)) {
            return false;
          }
        }
        return true;
      }

      @Override
      public void onLoadResource(WebView view, String url) {
        super.onLoadResource(view, url);
        navibar.notifyDataSetChanged(view.canGoBack(),
            view.canGoForward());
        Log.e("ThoNH", "onLoadResource() ---> " + url);
      }

      @Override
      public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        startUrl = url;
        showLoading();
        navibar.notifyDataSetChanged(view.canGoBack(),
            view.canGoForward());

        // #12206: scroll to top when click refresh
        view.scrollTo(0, 0);
      }

      @Override
      public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        hideLoading();
        navibar.notifyDataSetChanged(view.canGoBack(),
            view.canGoForward());
        if (mNavigationManager == null
            || mNavigationManager.getActivePage() instanceof WebViewFragment) {
          if (url.startsWith("http:") || url.startsWith("https:")) {
            setScreenTitle(view.getTitle());
          }
        }
      }
    });

    // Enable Upload file
    webView.setWebChromeClient(new WebChromeClient() {
      public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
        mUploadMessage = uploadMsg;
//                MediaOptions.Builder mediaBuilder = new MediaOptions.Builder();
//                MediaOptions options = mediaBuilder.setIsCropped(false).setFixAspectRatio(false).selectPhoto().build();
//                MediaPickerActivity.open(getActivity(), REQUEST_FILECHOOSER, options);

        new Gallery.Builder()
            .cropOutput(false)
            .fixAspectRatio(false)
            .multiChoice(false)
            .viewType(Gallery.VIEW_TYPE_PHOTOS_ONLY)
            .build()
            .start(WebViewFragment.this);
      }

      // openFileChooser for Android < 3.0
      @SuppressWarnings("unused")
      public void openFileChooser(ValueCallback<Uri> uploadMsg) {
        openFileChooser(uploadMsg, "");
      }

      // openFileChooser for other Android versions
      @SuppressWarnings("unused")
      public void openFileChooser(ValueCallback<Uri> uploadMsg,
          String acceptType, String capture) {
        openFileChooser(uploadMsg, acceptType);
      }

      // Get web client message
      public boolean onConsoleMessage(ConsoleMessage cm) {
        onConsoleMessage(cm.message(), cm.lineNumber(), cm.sourceId());
        return true;
      }

      public void onConsoleMessage(String message, int lineNumber,
          String sourceID) {
        StringBuilder builder = new StringBuilder();
        builder.append("Console message: ").append(message);
        builder.append("\nLine num: ").append(lineNumber);
        builder.append("\nSource id: ").append(sourceID);
        LogUtils.i(TAG, builder.toString());
      }
    });
  }

  private void checkTokenWithAutoload(String url) {
    if (!checkToken(url)) {
      loadURL(url);
    }
  }

  private boolean checkToken(String url) {
    // Check token include before load page
    if (isConstantToken(url)) {
      LogUtils.i(TAG, String.valueOf("Check URL:" + url));

      // Check token parameter to send token
      UserPreferences preferences = UserPreferences.getInstance();
      String token = preferences.getToken();
      CheckTokenRequest request = new CheckTokenRequest(token);
      restartRequestServer(LOADER_ID_CHECK_TOKEN, request);
      mUrl = url;
      return true;
    }
    return false;
  }

  private void loadURLwithToken(String url, String token) {
    if (!TextUtils.isEmpty(token)) {
      url = url.replace(TOKEN_INTENT, token);
    }
    loadURL(url);
  }

  private void loadURL(String url) {
    webView.loadUrl(url);
  }

  private boolean isConstantToken(String url) {
    return url.contains(TOKEN_INTENT);
  }

  private void loadContent(String content) {
    if (content != null) {
      LogUtils.i(TAG, String.valueOf("Content:" + content));
      webView.loadData(content, "text/html; charset=utf-8", null);
    }
  }

  private void checkPageType() {
    updateScreenTitle();
    if (mUrl.trim().length() > 0) {
      // Check action before check token
      if (!checkAction(mUrl)) {
        checkTokenWithAutoload(mUrl);
      }
    } else {
      switch (mPageType) {
        //hiepuh
        case PAGE_TYPE_INFORMATION:
          checkTokenWithAutoload(
              Config.IS_PRODUCT_SERVER ? LINK_INFORMATION : LINK_INFORMATION_STG);
          break;
        case PAGE_TYPE_URL_CMCODE:
          checkTokenWithAutoload(mUrl);
          break;
        case PAGE_TYPE_NOTICE:
          checkTokenWithAutoload(Config.IS_PRODUCT_SERVER ? LINK_NOTICE : LINK_NOTICE_STG);
          break;
        //end
        case PAGE_TYPE_WEB_VIEW:
        case PAGE_TYPE_NEWS:
          loadContent(mContent);
          break;
        case PAGE_TYPE_LOGIN_OTHER_SYS:
          checkTokenWithAutoload(
              Config.IS_PRODUCT_SERVER ? LINK_LOGIN_OTHER_SYS : LINK_LOGIN_OTHER_SYS);
          break;
        case PAGE_TYPE_TERM_OF_SERVICE:
          checkTokenWithAutoload(
              Config.IS_PRODUCT_SERVER ? LINK_TERM_OF_SERVICE : LINK_TERM_OF_SERVICE_STG);
          break;
        case PAGE_TYPE_PRIVACY_POLICY:
          checkTokenWithAutoload(
              Config.IS_PRODUCT_SERVER ? LINK_PRIVACY_REGISTER : LINK_PRIVACY_REGISTER_STG);
//                    GetStaticPageRequest request = new GetStaticPageRequest(
//                            mPageType);
//                    restartRequestServer(LOADER_ID_LOAD_STATIC_PAGE, request);
          break;
        case PAGE_TYPE_TERM_OF_USE:
          checkTokenWithAutoload(
              Config.IS_PRODUCT_SERVER ? LINK_TERM_OF_USE : LINK_TERM_OF_USE_STG);
          break;
        case PAGE_TYPE_VERIFY_AGE:
        case PAGE_TYPE_AUTO_VERIFY_AGE:
          if (Config.IS_PRODUCT_SERVER) {
            checkTokenWithAutoload(LINK_AUTO_VERIFY_AGE);
          } else {
            checkTokenWithAutoload(LINK_AUTO_VERIFY_AGE_TEST);
          }
          break;
        case PAGE_TYPE_ANDG_HOMEPAGE:
          checkTokenWithAutoload(
              Config.IS_PRODUCT_SERVER ? LINK_ANDG_HOMEPAGE : LINK_ANDG_HOMEPAGE);
          break;
        case PAGE_TYPE_ABOUT_PAYMENT:
          checkTokenWithAutoload(LINK_ABOUT_PAYMENT);
          break;
        case PAGE_TYPE_HOW_TO_USE:
          checkTokenWithAutoload(Config.IS_PRODUCT_SERVER ? LINK_HOW_TO_USE : LINK_HOW_TO_USE_STG);
          break;
        case PAGE_TYPE_SUPPORT:
          checkTokenWithAutoload(Config.IS_PRODUCT_SERVER ? LINK_SUPPORT : LINK_SUPPORT_STG);
          break;
        case PAGE_TYPE_CONTACT:
          checkTokenWithAutoload(Config.IS_PRODUCT_SERVER ? LINK_CONTACT : LINK_CONTACT_STG);
          break;
        case PAGE_TYPE_FREE_POINT:
          checkTokenWithAutoload(Config.IS_PRODUCT_SERVER ? LINK_FREE_POINT : LINK_FREE_POINT_STG);
          break;
        case PAGE_TYPE_BUY_PONIT:
          checkTokenWithAutoload(Config.IS_PRODUCT_SERVER ? LINK_BUY_POINT : LINK_BUY_POINT_STG);
          break;
        default:
          // Do not know what to do
          break;
      }
    }
  }

  /*
   * URL Structure: "http://abc.com:00/index.htm?love=minhngoc&miss=mupphin"
   *
   * @return false when do not want to load the page.
   */
  public boolean checkAction(String url) {

    Uri uri = Uri.parse(url);
    if ("httpnew".equals(uri.getScheme()) == true) {
      String[] urls = url.split("://", 0);
      String transition_url = "http://" + urls[1];
      getActivity().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(transition_url)));
      return true;
    }

    LogUtils.i(TAG, String.valueOf("URL:" + url));

    // Check HTTP request
    if (!url.startsWith("http:") && !url.startsWith("https:")) {
      try {
        // Otherwise allow the OS to handle it
        if (!TextUtils.isEmpty(startUrl)) {
          Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(startUrl));
          startActivity(intent);
        }

        return true;
      } catch (ActivityNotFoundException ex) {
        ex.printStackTrace();
      }
      return false;
    }

    // Remove last /
    int urlLength = url.length();
    final String SPLASH = "/";
    if (url.lastIndexOf(SPLASH) == urlLength - 1) {
      url = url.substring(0, urlLength - 1);
      urlLength = url.length();
    }

    // Split URL by ? to get list expression
    final String HOOK = "?";
    int lastParamIndex = url.lastIndexOf(HOOK);
    if (lastParamIndex < 0 || lastParamIndex + 1 > urlLength) {
      lastParamIndex = url.lastIndexOf(SPLASH);
      if (lastParamIndex < 0 || lastParamIndex + 1 > urlLength) {
        return false;
      }
    }

    String listExpressionString = url.substring(lastParamIndex + 1,
        urlLength);
    if (listExpressionString == null || listExpressionString.length() <= 0) {
      return false;
    }

    // Split listExpression by & to get expression
    final String AMPERSAND = "&";
    String[] listExpression = listExpressionString.split(Pattern
        .quote(AMPERSAND));

    List<Parameter> parameters = new ArrayList<WebViewFragment.Parameter>();

    for (String expression : listExpression) {
      // Split expression by = to get operand
      final String COMPARE = "=";
      String[] listOperand = expression.split(Pattern.quote(COMPARE));

      // This code have to have the structure is a=b
      if (listOperand.length != 2) {
//                showErrorMsg(ERROR_INVALID_URL);
//                return false;
        continue;
      }

      // Get key and get value
      String param = listOperand[0];
      String value = listOperand[1];
      parameters.add(new Parameter(param, value));
    }

    return checkAct(parameters);
  }

  private boolean checkAct(List<Parameter> parameters) {
    // flow purchase
    // check enough info to purchase
    // passing parameter to buy point
    pckId = getValueParameter(parameters, PCK_ID);
    productId = getValueParameter(parameters, PRODUCT_ID);
    if (productId != null && pckId != null) {
      String token = UserPreferences.getInstance().getToken();
      LogPurchaseRequest request = new LogPurchaseRequest(token, pckId);

      showLoading();
      restartRequestServer(LOG_BEFORE_PURCHASE_LOADER, request);
      return true;
    }

    // Initial new value
    Parameter actParam = null;

    // Check act parameter
    for (Parameter parameter : parameters) {
      String param = parameter.getParam();
      if (ACT_INTENT.equals(param)) {
        actParam = parameter;
        break;
      }
    }

    // Do not have act parameter
    if (actParam == null) {
      return false;
    }
    // Remove the act parameter from list
    parameters.remove(actParam);
    String valueAct = actParam.getValue();
    ActionParam actionParam = new ActionParam();
    if (actionParam.TOP.equals(valueAct)) {
      // Action for top page. Open top page
      // TODO: 4/20/2017 updated by Hiepnk about http://10.64.100.201/issues/8142
      final HomeFragment fragment = HomeFragment.newInstance(HomeFragment.TAB_HOT_PAGE);
//            showPage(fragment);
      if (getActivity() instanceof MainActivity) {
        Handler handler = new Handler();
        handler.post(new Runnable() {
          @Override
          public void run() {
            mNavigationManager.switchPage(fragment, false);
          }
        });
      }
    } else if (actionParam.MY_PROFILE.equals(valueAct)) {
      // Open my profile
      EditProfileFragment fragment = EditProfileFragment.newInstance();
      showPage(fragment);
    } else if (valueAct.contains(actionParam.USER_PROFILE)) {
      String id = valueAct.replace(actionParam.USER_PROFILE_ID, "");
      if (!TextUtils.isEmpty(id)) {
        MyProfileFragment fragment = MyProfileFragment.newInstance(id);
        showPage(fragment);
      }
    } else if (actionParam.MY_PAGE.equals(valueAct)) {
      // Open my page
      MyPageFragment fragment = new MyPageFragment();
      showPage(fragment);
    } else if (actionParam.TERMS.equals(valueAct)) {
      // Open the term of service
      mPageType = PAGE_TYPE_TERM_OF_SERVICE;
      mUrl = "";
      checkPageType();
    } else if (actionParam.PRIVACY.equals(valueAct)) {
      // Open privacy
      mPageType = PAGE_TYPE_PRIVACY_POLICY;
      mUrl = "";
      checkPageType();
    } else if (actionParam.GOOGLE_PLAY_PAGE.equals(valueAct)) {
      // Open buy point page
      startActivity(BuyPointActivity.class);
    } else if (actionParam.CLOSE_APP.equals(valueAct)) {
      // Close this application
      closeApplication();
    } else if (actionParam.LOGIN_MOCOM.equals(valueAct)) {
      if (parameters.size() < 1) {
        onLoginFail();
      }

      // Check id parameter
      for (Parameter parameter : parameters) {
        String param = parameter.getParam();
        if (actionParam.MOCOM_ID.equals(param)) {
          String valueId = parameter.getValue();
          onLoginMocom(valueId);
          return true;
        }
      }
    } else if (actionParam.LOGIN_FAMU.equals(valueAct)) {
      if (parameters.size() < 1) {
        onLoginFail();
      }

      // Check id parameter
      for (Parameter parameter : parameters) {
        String param = parameter.getParam();
        if (actionParam.FAMU_ID.equals(param)) {
          String valueId = parameter.getValue();
          onLoginFamu(valueId);
          return true;
        }
      }
    }

    return false;
  }

  /**
   * @param parameters list pair (param: value)
   * @param param name of param
   * @return value of parameter in list
   */
  private String getValueParameter(List<Parameter> parameters, String param) {
    if (parameters != null && !parameters.isEmpty()) {
      for (Parameter parameter : parameters) {
        if (parameter.param.equals(param)) {
          return parameter.value;
        }
      }
    }
    return null;
  }

  private void showPage(final BaseFragment fragment) {
    Activity activity = getActivity();
    if (activity instanceof MainActivity) {
      Handler handler = new Handler();
      handler.post(new Runnable() {
        @Override
        public void run() {
          mNavigationManager.swapPage(fragment, false);
        }
      });
    } else if (activity instanceof WebViewActivity) {
      Intent intent = new Intent(activity, MainActivity.class);
      intent.putExtra(FreePageUtil.ACT_INTENT, mUrl);
      activity.startActivity(intent);
      activity.setResult(BaseFragmentActivity.RESULT_EXIT);
      activity.finish();
    }
  }

  private void startActivity(Class<?> cls) {
    Intent intent = new Intent(getActivity(), cls);
    getActivity().startActivity(intent);
  }

  private void closeApplication() {
    AlertDialog mDialog = new CustomConfirmDialog(getActivity(),
        null, getString(R.string.message_end_app), true)
        .setPositiveButton(0, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            Process.killProcess(Process.myPid());
            System.exit(1);
          }
        })
        .setNegativeButton(0, null)
        .create();
    mDialog.show();

    int dividerId = mDialog.getContext().getResources()
        .getIdentifier("android:id/titleDivider", null, null);
    View divider = mDialog.findViewById(dividerId);
    if (divider != null) {
      divider.setBackgroundColor(mDialog.getContext().getResources().getColor(R.color.transparent));
    }
  }

  private void onLoginMocom(String id) {
    Activity activity = getActivity();
    Intent intent = new Intent();
    intent.putExtra(INTENT_LOGIN_MOCOM_ID, id);
    activity.setResult(Activity.RESULT_OK, intent);
    activity.finish();
  }

  private void onLoginFamu(String id) {
    Activity activity = getActivity();
    Intent intent = new Intent();
    intent.putExtra(INTENT_LOGIN_FAMU_ID, id);
    activity.setResult(Activity.RESULT_OK, intent);
    activity.finish();
  }

  private void onLoginFail() {
    Activity activity = getActivity();
    activity.setResult(Activity.RESULT_CANCELED);
    activity.finish();
  }

  private void showErrorMsg(String msg) {
    Toast.makeText(AndGApp.get(), msg, Toast.LENGTH_SHORT).show();
    LogUtils.e(TAG, msg);
  }

  /**
   * Update screen title. Only update when screen type changed
   */
  private void updateScreenTitle() {
    String title = mTitle;
    int titleId = 0;

    switch (mPageType) {
      //hiepuh
      case PAGE_TYPE_INFORMATION:
        titleId = R.string.information;
        break;
      //end
      case PAGE_TYPE_TERM_OF_SERVICE:
        titleId = R.string.settings_terms_of_service_terms_of_service;
        break;
      case PAGE_TYPE_PRIVACY_POLICY:
        titleId = R.string.settings_terms_of_service_privacy_policy;
        break;
      case PAGE_TYPE_TERM_OF_USE:
        titleId = R.string.settings_terms_of_service_terms_of_use;
        break;
      case PAGE_TYPE_VERIFY_AGE:
        titleId = R.string.title_age_verification;
        break;
      case PAGE_TYPE_FREE_POINT:
        titleId = R.string.free_point_title;
        break;
      case PAGE_TYPE_HOW_TO_USE:
        titleId = R.string.how_to_use;
        break;
      case PAGE_TYPE_SUPPORT:
        titleId = R.string.support;
        break;
      case PAGE_TYPE_BUY_PONIT:
        titleId = R.string.buy_points;
        break;
      case PAGE_TYPE_WEB_VIEW:
      case PAGE_TYPE_LOGIN_OTHER_SYS:
      case PAGE_TYPE_ANDG_HOMEPAGE:
      case PAGE_TYPE_ABOUT_PAYMENT:
      case PAGE_TYPE_NEWS:
      default:
        titleId = R.string.common_app_name;
        break;
    }

    // Set title
    if (title.length() > 0) {
      setScreenTitle(title);
    } else {
      setScreenTitle(titleId);
    }
  }

  private void setScreenTitle(String title) {
    LogUtils.i(TAG, String.valueOf(title));
    Activity activity = getActivity();
    if (activity instanceof WebViewActivity) {
      ((WebViewActivity) activity).getCenterTitle().setText(title);
    } else if (activity instanceof MainActivity) {
      if (activity instanceof CustomActionBarActivity) {
        CustomActionBarActivity customActionBarActivity = ((CustomActionBarActivity) activity);
        customActionBarActivity.getCustomActionBar()
            .setTextCenterTitle(title);
      } else if (activity instanceof CustomActionBar) {
        ((CustomActionBar) activity).setTextCenterTitle(title);
      } else {
        getNavigationBar().setCenterTitle(title);
      }
    }
  }

  private void setScreenTitle(int title) {
    Activity activity = getActivity();
    if (activity instanceof WebViewActivity) {
      ((WebViewActivity) activity).getCenterTitle().setText(title);
    } else if (activity instanceof MainActivity) {
      if (activity instanceof CustomActionBarActivity) {
        CustomActionBarActivity customActionBarActivity = ((CustomActionBarActivity) activity);
        customActionBarActivity.getCustomActionBar()
            .setTextCenterTitle(title);
      } else if (activity instanceof CustomActionBar) {
        ((CustomActionBar) activity).setTextCenterTitle(title);
      } else {
        getNavigationBar().setCenterTitle(title);
      }
    }
  }

  @Override
  public void onGoBackClick() {
    if (webView.canGoBack()) {
      webView.goBack();
    }
  }

  @Override
  public void onGoForwardClick() {
    if (webView.canGoForward()) {
      webView.goForward();
    }
  }

  @Override
  public void onRefreshClick() {
    webView.reload();
  }

  @Override
  public void onHomeClick() {
    UserPreferences preferences = UserPreferences.getInstance();
    if (preferences.getUserId() != null
        && preferences.getUserId().length() > 0
        && preferences.getFinishRegister() == Constants.FINISH_REGISTER_YES) {
      Activity activity = getActivity();
      if (activity instanceof MainActivity) {
        HomeFragment fragment = HomeFragment.newInstance(HomeFragment.TAB_HOT_PAGE);
        mNavigationManager.switchPage(fragment);
      } else if (activity instanceof WebViewActivity) {
        if (mPageType == WebViewFragment.PAGE_TYPE_NEWS) {
          Intent intent = new Intent(activity, MainActivity.class);
          intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
          activity.startActivity(intent);
          activity.setResult(BaseFragmentActivity.RESULT_EXIT);
          activity.finish();
        } else {
          activity.finish();
        }
      }
    }
  }

  @Override
  public void onPurchaseError(int errorCode) {
    Log.d(TAG, "onPurchaseError: " + errorCode);
  }

  @Override
  public void onCancelPurchaseByUser() {
    Log.d(TAG, "onCancelPurchaseByUser: ");
  }

  @Override
  public void onConfirmPurchase(Purchase purchase, String transactionId) {
    UserPreferences userPreferences = UserPreferences.getInstance();
    String token = userPreferences.getToken();
    if (token == null || "".equals(token)) {
      LogUtils.e(TAG, "Token invalid");
      return;
    }
    String purData = purchase.getOriginalJson();
    String signature = purchase.getSignature();

    ConfirmPurchaseRequest confirmPurchaseRequest = new ConfirmPurchaseRequest(token, pckId,
        purData, signature, transactionId, BuildConfig.SANDBOX_PURCHASE);
    showLoading();
    restartRequestServer(CONFIRM_PURCHASE_LOADER, confirmPurchaseRequest);
  }

  @Override
  public void startRequest(int loaderId) {
    switch (loaderId) {
      case LOADER_ID_LOAD_STATIC_PAGE:
        showLoading();
        break;
      case LOADER_ID_CHECK_TOKEN:
        if (progressDialog == null) {
          progressDialog = new ProgressDialog(getActivity());
          String waiting = getActivity().getString(R.string.waiting);
          progressDialog.setMessage(waiting);
        }
        if (!progressDialog.isShowing()) {
          progressDialog.show();
        }
        break;
    }
  }

  private void responseStaticPage(GetStaticPageResponse response) {
    int code = response.getCode();
    if (response.getCode() == Response.SERVER_SUCCESS) {
      String content = response.getContent();
      loadContent(content);
    } else {
      int title = R.string.common_error;
      ErrorApiDialog.showAlert(getActivity(), title, code);
    }
  }

  private void responseCheckToken(CheckTokenResponse response) {
    int code = response.getCode();
    if (code == Response.SERVER_SUCCESS || code == Response.SERVER_AGE_DENY) {
      String token = response.getToken();
      loadURLwithToken(mUrl, token);
      mUrl = "";
    } else {
      int title = R.string.common_error;
      ErrorApiDialog.showAlert(getActivity(), title, code);
    }
  }

  @Override
  public void receiveResponse(Loader<Response> loader, Response response) {
    int loaderID = loader.getId();
    switch (loaderID) {
      case LOADER_ID_LOAD_STATIC_PAGE:
        responseStaticPage((GetStaticPageResponse) response);
        hideLoading();
        break;
      case LOADER_ID_CHECK_TOKEN:
        if (progressDialog != null && progressDialog.isShowing()) {
          progressDialog.dismiss();
        }
        responseCheckToken((CheckTokenResponse) response);
        break;
      case CONFIRM_PURCHASE_LOADER:
        ConfirmPurchaseResponse purchaseResponse = (ConfirmPurchaseResponse) response;
        Toast.makeText(getContext(), "CURRENT POINTS: " + purchaseResponse.getPoint(),
            Toast.LENGTH_LONG).show();
        hideLoading();
        pckId = null;
        productId = null;
        break;
      case LOG_BEFORE_PURCHASE_LOADER:
        LogPurchaseResponse logPurchaseResponse = (LogPurchaseResponse) response;
        String transactionId = logPurchaseResponse.getTransactionId();
        hideLoading();
        mBillingManager.purchase(productId, BillingClient.SkuType.INAPP, transactionId);
        break;
    }
  }

  @Override
  public Response parseResponse(int loaderID, ResponseData data, int type) {
    Response response = null;
    switch (loaderID) {
      case LOADER_ID_LOAD_STATIC_PAGE:
        response = new GetStaticPageResponse(data);
        break;
      case LOADER_ID_CHECK_TOKEN:
        response = new CheckTokenResponse(data);
        break;
      case CONFIRM_PURCHASE_LOADER:
        response = new ConfirmPurchaseResponse(data);
        break;
      case LOG_BEFORE_PURCHASE_LOADER:
        response = new LogPurchaseResponse(data);
        break;
    }
    return response;
  }

  @Override
  public void onBaseLoaderReset(Loader<Response> loader) {
  }

  private void showLoading() {
    if (progressBar == null) {
      return;
    }

    if (progressBar.getVisibility() != View.VISIBLE) {
      progressBar.setVisibility(View.VISIBLE);
    }
  }

  private void hideLoading() {
    if (progressBar == null) {
      return;
    }

    if (progressBar.getVisibility() == View.VISIBLE) {
      progressBar.setVisibility(View.GONE);
    }
  }

  /**
   * Get type of this screen. Using for draw navigation bar
   */
  public int getPageType() {
    if (mPageType == PAGE_TYPE_NOT_SET) {
      Bundle bundle = getArguments();
      if (bundle != null) {
        mPageType = bundle.getInt(INTENT_PAGE_TYPE);
      }
    }
    return mPageType;
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode == Activity.RESULT_OK) {
      switch (requestCode) {
        case Camera.REQUEST_CODE_CAMERA:
        case Gallery.REQUEST_CODE_GALLERY:
          Parcelable[] files = data.getParcelableArrayExtra(MediaPickerBaseActivity.RESULT_KEY);
          if (files != null) {
            for (Parcelable parcelable : files) {
              MediaFile file = (MediaFile) parcelable;
              if (mUploadMessage == null) {
                return;
              }

              Uri result = Utils.getFileUri(getContext(), new File(file.getPath()));
              mUploadMessage.onReceiveValue(result);
              mUploadMessage = null;
            }
          } else {
            LogUtils.e(TAG, "Error to get media, NULL");
            mUploadMessage.onReceiveValue(null);
            mUploadMessage = null;
          }
          break;
      }

//        if (requestCode == REQUEST_FILECHOOSER) {
//            ArrayList<MediaItem> mMediaSelectedList = MediaPickerActivity.getMediaItemSelected(data);
//            if (mMediaSelectedList != null) {
//                for (final MediaItem mediaItem : mMediaSelectedList) {
//                    if (mUploadMessage == null)
//                        return;
//
//                    Uri result = UriCompat.fromFile(getContext(), mediaItem.getOriginPath());
//                    mUploadMessage.onReceiveValue(result);
//                    mUploadMessage = null;
//                }
//            } else {
//                LogUtils.e(TAG, "Error to get media, NULL");
//                mUploadMessage.onReceiveValue(null);
//                mUploadMessage = null;
//            }
    }
  }

  public class Parameter {

    private String param;
    private String value;

    public Parameter(String param, String value) {
      this.param = param;
      this.value = value;
    }

    public String getParam() {
      return param;
    }

    public String getValue() {
      return value;
    }
  }
}