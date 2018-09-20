package com.application.facebook;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.Toast;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Session;
import com.facebook.Session.NewPermissionsRequest;
import com.facebook.Session.StatusCallback;
import com.facebook.SessionLoginBehavior;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import glas.bbsystem.R;
import java.util.Collection;
import java.util.List;

public class FacebookController implements Session.StatusCallback {

  protected static final String TAG = "FacebookController";
  private Context mContext;
  private Activity mActivity;
  private Fragment mFragment;
  private UiLifecycleHelper mUiLifecycleHelper;
  private IFacebookPermission mFacebookPermission;

  public FacebookController(Activity activity) {
    mActivity = activity;
    mUiLifecycleHelper = new UiLifecycleHelper(mActivity, this);
    mContext = mActivity.getApplicationContext();
  }

  public FacebookController(Fragment fragment) {
    mFragment = fragment;
    mActivity = fragment.getActivity();
    mUiLifecycleHelper = new UiLifecycleHelper(mActivity, this);
    mContext = mActivity.getApplicationContext();
  }

  public void setPermission(IFacebookPermission permission) {
    mFacebookPermission = permission;
  }

  public boolean isOpened() {
    Session session = Session.getActiveSession();
    return session != null
        && session.isOpened()
        && isSubsetOf(session.getPermissions(),
        mFacebookPermission.getPermissions()) ? true : false;
  }

  public boolean hasPermission() {
    return isSubsetOf(mFacebookPermission.getPermissions(), Session
        .getActiveSession().getPermissions()) ? true : false;
  }

  public void requestNewPermission(List<String> permissions) {
    Session session = Session.getActiveSession();
    // Session.OpenRequest openRequest = new Session.OpenRequest(mFragment);
    session.requestNewPublishPermissions(new NewPermissionsRequest(
        mActivity, permissions));
  }

  private boolean isSubsetOf(Collection<String> subset,
      Collection<String> superset) {
    if (subset.size() == 0 && superset.size() > 0) {
      return false;
    }
    if (subset.size() == 0 && superset.size() == 0) {
      return true;
    }

    for (String string : subset) {
      if (!superset.contains(string)) {
        return false;
      }
    }
    return true;
  }

  // public void loginActivity() {
  // Session session = new Session.Builder(mActivity).setApplicationId(
  // mActivity.getString(R.string.app_facebook_id)).build();
  // Session.setActiveSession(session);
  // session.openForPublish(new Session.OpenRequest(mActivity).setCallback(
  // new StatusCallback() {
  //
  // @Override
  // public void call(Session session, SessionState state,
  // Exception exception) {
  // if (session != null && state.isOpened()) {
  // Toast.makeText(mActivity, "receive info",
  // Toast.LENGTH_LONG).show();
  // // getFacebookUserInfo(session);
  // // if (mFacebookJob != null)
  // // mFacebookJob.performFacebookJob(session);
  // }
  // }
  // }).setPermissions(mFacebookPermission.getPermissions()));
  // }

  public void share(final ILoginFacebook loginFacebook,
      final List<String> permissionList) {
    Session session = Session.getActiveSession();
    if (session == null) {
      session = new Session.Builder(mContext).setApplicationId(
          mActivity.getString(R.string.app_facebook_id)).build();
      Session.setActiveSession(session);
      session.addCallback(new StatusCallback() {
        public void call(Session session, SessionState state,
            Exception exception) {
          if (state == SessionState.OPENED) {
            Session.OpenRequest openRequest = new Session.OpenRequest(
                mFragment);
            openRequest
                .setLoginBehavior(SessionLoginBehavior.SSO_WITH_FALLBACK);
            session.requestNewPublishPermissions(new Session.NewPermissionsRequest(
                mFragment, permissionList));
          } else if (state == SessionState.OPENED_TOKEN_UPDATED) {
            loginFacebook.onLoginFBSuccess();
          } else if (state == SessionState.CLOSED_LOGIN_FAILED) {
            session.closeAndClearTokenInformation();
            loginFacebook.onLoginFBFailure();
          } else if (state == SessionState.CLOSED) {
            session.close();
            loginFacebook.onLoginFBFailure();
          }
        }
      });
    }

    if (!session.isOpened()) {
      Session.OpenRequest openRequest = new Session.OpenRequest(mFragment);
      openRequest
          .setLoginBehavior(SessionLoginBehavior.SSO_WITH_FALLBACK);
      session.openForRead(openRequest);
    } else {
      loginFacebook.onLoginFBSuccess();
    }
  }

  public boolean isvalid(Session session) {
    SessionState sessionState = session.getState();
    if (sessionState == SessionState.CLOSED_LOGIN_FAILED
        || sessionState == SessionState.CLOSED) {
      return false;
    }
    return true;
  }

  public void loginFragment(final ILoginFacebook loginFacebook) {
    Session.openActiveSession(mContext, mFragment, true,
        new StatusCallback() {
          // to only confirm permission one time
          private boolean isRequestPublishPermission = false;

          @Override
          public void call(Session session, SessionState state,
              Exception exception) {
            if (state.isOpened()) {
              if (!isSubsetOf(
                  mFacebookPermission.getPermissions(),
                  session.getPermissions())) {
                if (!isRequestPublishPermission) {
                  session.requestNewPublishPermissions(new NewPermissionsRequest(
                      mFragment, mFacebookPermission
                      .getPermissions()));
                  isRequestPublishPermission = true;
                } else {
                  isRequestPublishPermission = false;
                }
              } else {
                loginFacebook.onLoginFBSuccess();
              }
            } else {
              int numCurrentPer = session.getPermissions().size();
              int numOldPer = Session.getActiveSession()
                  .getPermissions().size();
              if (numCurrentPer < numOldPer) {
                session.closeAndClearTokenInformation();
                isRequestPublishPermission = false;
              }
              loginFacebook.onLoginFBFailure();
            }
          }
        });
  }

  /**
   * Message and bytes of picture to share msg: message bytes: byte of picture, bytes== then only
   * share msg
   */
  public void shareStatus(String msg, byte[] bytes) {
    String graphPath = "me/feed";
    Bundle postParams = new Bundle();
    postParams.putString("message", msg);
    if (bytes != null) {
      postParams.putByteArray("picture", bytes);
      graphPath = "me/photos";
    }
    Request.Callback callback = new Request.Callback() {
      public void onCompleted(com.facebook.Response response) {
        if (response.getError() == null) {
          Toast.makeText(mContext, R.string.share_fb_success,
              Toast.LENGTH_LONG).show();
        } else {
          if (isNeedRelogin(response.getError().getErrorCode())) {
            Session.getActiveSession()
                .closeAndClearTokenInformation();
          }
          Toast.makeText(mContext, R.string.share_fb_fail,
              Toast.LENGTH_LONG).show();
        }
      }
    };

    Request request = new Request(Session.getActiveSession(), graphPath,
        postParams, HttpMethod.POST, callback);
    RequestAsyncTask task = new RequestAsyncTask(request);
    task.execute();
  }

  public void onCreate(Bundle savedInstanceState) {
    mUiLifecycleHelper.onCreate(savedInstanceState);
  }

  public void onResume() {
    mUiLifecycleHelper.onResume();
  }

  public void onPause() {
    mUiLifecycleHelper.onPause();
  }

  public void onSaveInstance(Bundle state) {
    mUiLifecycleHelper.onSaveInstanceState(state);
  }

  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    mUiLifecycleHelper.onActivityResult(requestCode, resultCode, data);
    Session.getActiveSession().onActivityResult(mActivity, requestCode,
        resultCode, data);

  }

  private boolean isNeedRelogin(int requestErrorCode) {
    // 200 that mean require permission
    if (requestErrorCode == 200) {
      return true;
    }
    return false;
  }

  @Override
  public void call(Session session, SessionState state, Exception exception) {
    if (session != null && state.isOpened()) {
    }

  }

}
