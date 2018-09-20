package com.application.util;

import android.widget.Toast;
import com.application.AndGApp;
import com.application.ui.BaseFragment;
import com.application.ui.HomeFragment;
import com.application.ui.MyPageFragment;
import com.application.ui.point.BuyPointActivity;
import java.util.regex.Pattern;

public class FreePageUtil {

  // act intent key
  public static final String ACT_INTENT = "act";
  public static final String UID_INTENT = "sid";
  // act key - value
  public static final String ACT_TOP = "toppage";
  public static final String ACT_MY_PROFILE = "myprofile";
  public static final String ACT_MY_PAGE = "mypage";
  public static final String ACT_GOOGLE_PLAY_PAGE = "googleplay";
  public static final String ACT_APPLE_STORE = "appstore";
  public static final String ACT_TERMS = "terms";
  public static final String ACT_PRIVACY = "privacy";
  public static final String ACT_CLOSE_APP = "close";
  // act login
  public static final String ACT_LOGIN_MOCOM = "mocomlogin";
  public static final String ACT_MOCOM_ID = "mocom_id";
  public static final String ACT_LOGIN_FAMU = "famulogin";
  public static final String ACT_FAMU_ID = "famu_id";
  private static final String TAG = "FreePageUtil";
  private static final String ERROR_INVALID_URL = "Invalid URL";

  /*
   * URL Structure: "http://abc.com:00/index.htm?love=minhngoc&miss=mupphin"
   */
  public static boolean handleUrl(String url, IUrlListener listener) {
    LogUtils.e(TAG, String.valueOf("URL:" + url));
    // Split URL by ? to get list expression
    final String HOOK = "?";
    String[] splitUrl = url.split(Pattern.quote(HOOK));

    // If array length < 2 then do not have ? in URl
    if (splitUrl.length < 2) {
      return false;
    }
    String listExpressionString = splitUrl[splitUrl.length - 1];

    // Split listExpression by & to get expression
    final String AMPERSAND = "&";
    String[] listExpression = listExpressionString.split(Pattern
        .quote(AMPERSAND));

    for (String expression : listExpression) {
      // Split expression by = to get operand
      final String COMPARE = "=";
      String[] listOperand = expression.split(Pattern.quote(COMPARE));

      // This code have to have the structure is a=b
      if (listOperand.length != 2) {
//				showErrorMsg(ERROR_INVALID_URL);
        return false;
      }

      // Get key and get value
      String key = listOperand[0];
      String value = listOperand[1];

      // Treat the action correlative
      if (ACT_INTENT.equals(key)) {
        onAct(value, listExpression, listener);
        return true;
      } else if (UID_INTENT.equals(key)) {
        // Do nothing
      }
    }
    return false;
  }

  private static void onAct(String value, String[] listExpression,
      IUrlListener listener) {
    if (ACT_TOP.equals(value)) {
      // Action for top page. Open top page
      if (listener != null) {
        HomeFragment fragment = HomeFragment.newInstance(HomeFragment.TAB_MEET_PEOPLE);
        listener.showPage(fragment);
      }
    } else if (ACT_MY_PROFILE.equals(value)) {
      // Open my profile
      if (listener != null) {
        MyPageFragment fragment = new MyPageFragment();
        listener.showPage(fragment);
      }
    } else if (ACT_MY_PAGE.equals(value)) {
      // Open my page
      if (listener != null) {
        MyPageFragment fragment = new MyPageFragment();
        listener.showPage(fragment);
      }
    } else if (ACT_TERMS.equals(value)) {
      // Open the term of service
      if (listener != null) {
      }
    } else if (ACT_PRIVACY.equals(value)) {
      // Open privacy
      if (listener != null) {
      }
    } else if (ACT_GOOGLE_PLAY_PAGE.equals(value)) {
      // Open buy point page
      if (listener != null) {
        listener.startActivity(BuyPointActivity.class);
      }
    } else if (ACT_CLOSE_APP.equals(value)) {
      // Close this application
      if (listener != null) {
        listener.closeApp();
      }
    } else if (ACT_LOGIN_MOCOM.equals(value)) {
      if (listener != null && listener instanceof ILogInByAnotherSystem) {
        for (String expression : listExpression) {
          // Split expression by = to get operand
          final String COMPARE = "=";
          String[] listOperand = expression.split(Pattern
              .quote(COMPARE));

          // This code have to have the structure is a=b
          if (listOperand.length != 2) {
            showErrorMsg(ERROR_INVALID_URL);
            ((ILogInByAnotherSystem) listener).onLoginFail();
          }

          // Get key and get value
          String keyId = listOperand[0];
          String valueId = listOperand[1];

          // Treat the action correlative
          if (ACT_MOCOM_ID.equals(keyId)) {
            ((ILogInByAnotherSystem) listener)
                .onMocomLogin(valueId);
          }
        }
      }
    } else if (ACT_LOGIN_FAMU.equals(value)) {
      if (listener != null && listener instanceof ILogInByAnotherSystem) {
        for (String expression : listExpression) {
          // Split expression by = to get operand
          final String COMPARE = "=";
          String[] listOperand = expression.split(Pattern
              .quote(COMPARE));

          // This code have to have the structure is a=b
          if (listOperand.length != 2) {
            showErrorMsg(ERROR_INVALID_URL);
            ((ILogInByAnotherSystem) listener).onLoginFail();
          }

          // Get key and get value
          String keyId = listOperand[0];
          String valueId = listOperand[1];

          // Treat the action correlative
          if (ACT_FAMU_ID.equals(keyId)) {
            ((ILogInByAnotherSystem) listener).onFamuLogin(valueId);
          }
        }
      }
    }
  }

  private static void showErrorMsg(String msg) {
    Toast.makeText(AndGApp.get(), msg, Toast.LENGTH_SHORT).show();
    LogUtils.e(TAG, msg);
  }

  public interface IUrlListener {

    public void showPage(BaseFragment fragment);

    public void startActivity(Class<?> cls);

    public void closeApp();
  }

  public interface ILogInByAnotherSystem extends IUrlListener {

    public void onMocomLogin(String mocomId);

    public void onFamuLogin(String famuId);

    public void onLoginFail();
  }
}