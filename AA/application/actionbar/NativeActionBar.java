package com.application.actionbar;

import android.app.Activity;
import android.graphics.Color;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.application.common.webview.WebViewFragment;
import com.application.navigationmanager.NavigationManager;
import com.application.ui.BaseFragment;
import com.application.ui.BaseFragmentActivity;
import com.application.ui.ChatFragment;
import com.application.ui.ChooseRegionSearchSettingFragment;
import com.application.ui.CustomActionBarActivity;
import com.application.ui.EthnicityFragment;
import com.application.ui.HomeFragment;
import com.application.ui.MainActivity;
import com.application.ui.MeetPeopleFragment;
import com.application.ui.MyPageFragment;
import com.application.ui.RegionSearchSettingFragment;
import com.application.ui.SearchByNameFragment;
import com.application.ui.SearchSettingFragment;
import com.application.ui.SettingsFragment;
import com.application.ui.WhoCheckYouOutFragment;
import com.application.ui.account.ChangePasswordFragment;
import com.application.ui.account.EditProfileFragment;
import com.application.ui.account.ProfileTextFragment;
import com.application.ui.account.ThreeSizesFragment;
import com.application.ui.buzz.BuzzDetail;
import com.application.ui.buzz.BuzzFragment;
import com.application.ui.buzz.ShareMyBuzzFragment;
import com.application.ui.buzz.UserBuzzListFragment;
import com.application.ui.chat.CallLogFragment;
import com.application.ui.chat.ConversationsFragment;
import com.application.ui.chat.IncomingSettingFragment;
import com.application.ui.connection.ConnectionFragment;
import com.application.ui.gift.ChooseGiftToSend;
import com.application.ui.gift.GiveGiftFragment;
import com.application.ui.hotpage.HotPagePeopleFragment;
import com.application.ui.meetpeople.SelectMessageFragment;
import com.application.ui.notification.ManageOnlineAlertFragment;
import com.application.ui.notification.NotificationFragment;
import com.application.ui.profile.InputAboutMeFragment;
import com.application.ui.profile.MyProfileFragment;
import com.application.ui.profile.SliderProfileFragment;
import com.application.ui.region.ChooseRegionFragment;
import com.application.ui.region.RegionSettingFragment;
import com.application.ui.settings.AboutFragment;
import com.application.ui.settings.AccountSettingsFragment;
import com.application.ui.settings.BlockedUsersFragment;
import com.application.ui.settings.DeactivateAccountConfirmFragment;
import com.application.ui.settings.DeactivateAccountFragment;
import com.application.ui.settings.DistanceSettingFragment;
import com.application.ui.settings.NotificationSettingsFragment;
import com.application.ui.settings.OnlineAlertFragment;
import com.application.ui.settings.PushNotificationSettingsFragment;
import glas.bbsystem.R;


/**
 * Created by vietthangif on 7/10/2014.
 */
public class NativeActionBar implements CustomActionBar {

  private final static String TAG = "NativeActionBar";
  private static final int DELAY_TIME = 200;
  protected NavigationManager mNavigationManager;
  protected Button mBtnLeft;
  protected Button mBtnRight;
  protected TextView mTxtCenter;
  protected ImageView mImgLeft;
  protected ImageView mImgCenter;
  protected ImageView mImgRight;
  protected ImageView mImgProfile;
  protected TextView mTxtViewRemain;
  private ActionBar mActionBar;
  private int mCurrentResId;
  private BaseFragmentActivity baseFragmentActivity;
  private long mLastClickTime = 0;
  private View.OnClickListener defaultBackButtonClickListener = new View.OnClickListener() {

    @Override
    public void onClick(View v) {
      mNavigationManager.goBack();
    }
  };

  @Override
  public void backButtonClicked(Activity activity) {
  }

  @Override
  public void initialize(NavigationManager navigationManager,
      AppCompatActivity activity) {
    mNavigationManager = navigationManager;
    mActionBar = activity.getSupportActionBar();
    mNavigationManager
        .addFragmentChangeListener(new NavigationManager.OnChangeFragmentListener() {
          @Override
          public void onFragmentChanged(Fragment fragment) {
            syncActionBar(fragment);
          }
        });
  }

  @Override
  public final void syncActionBar(Fragment activePage) {
    if (activePage == null) {
      return;
    }
    int newResId = findResourceIdForActionbar(activePage);
    if (newResId != mCurrentResId) {
      mCurrentResId = newResId;
      removeAllChildViews();
      inflateView();
      findChildViews();
      setupChildViews();
    }
    syncChildViews(activePage);
  }

  protected int findResourceIdForActionbar(Fragment activePage) {
    int resId = R.layout.cv_native_actionbar_standard;
    if (activePage instanceof BaseFragment) {
      BaseFragment baseFragment = (BaseFragment) activePage;
      if (baseFragment instanceof PushNotificationSettingsFragment
          || baseFragment instanceof RegionSettingFragment
          || baseFragment instanceof ChooseRegionFragment
          || baseFragment instanceof ChooseRegionSearchSettingFragment) {
        resId = R.layout.cv_native_actionbar_button_right;
      } else if (baseFragment instanceof ChatFragment
          || baseFragment instanceof MyProfileFragment
          || baseFragment instanceof SliderProfileFragment) {
        resId = R.layout.cv_native_actionbar_chat;
      } else if (baseFragment instanceof EditProfileFragment) {
        resId = R.layout.cv_native_actionbar_button_right;
      } else if (baseFragment instanceof RegionSearchSettingFragment) {
        resId = R.layout.cv_native_actionbar_button_right;
      } else if (baseFragment instanceof ThreeSizesFragment) {
        resId = R.layout.cv_native_actionbar_button_right;
      } else if (baseFragment instanceof ProfileTextFragment) {
        resId = R.layout.cv_native_actionbar_button_right;
      } else if (baseFragment instanceof IncomingSettingFragment
          || baseFragment instanceof ChangePasswordFragment) {
        resId = R.layout.cv_native_actionbar_button_right;
      } else if (baseFragment instanceof ManageOnlineAlertFragment) {
        resId = R.layout.cv_native_actionbar_button_right;
      } else if (baseFragment instanceof MeetPeopleFragment
          || baseFragment instanceof WhoCheckYouOutFragment
          || baseFragment instanceof BuzzFragment
          || baseFragment instanceof HotPagePeopleFragment
          || baseFragment instanceof ConversationsFragment
          || baseFragment instanceof HomeFragment) {
        resId = R.layout.cv_native_actionbar_top;
      }
    }

    return resId;

  }

  protected void removeAllChildViews() {
    long start = System.currentTimeMillis();
    View actionbarView = mActionBar.getCustomView();
    if (actionbarView instanceof ViewGroup) {
      ((ViewGroup) actionbarView).removeAllViews();
    }
    long last = System.currentTimeMillis();
    Log.d(TAG, "remove all views " + String.valueOf(last - start));
  }

  protected void inflateView() {
    mActionBar.setDisplayShowHomeEnabled(false);
    mActionBar.setDisplayShowCustomEnabled(true);
    mActionBar.setDisplayShowTitleEnabled(false);
    mActionBar.setCustomView(mCurrentResId);

    View customView = mActionBar.getCustomView();
    ViewParent parent = customView.getParent();
    if (parent instanceof Toolbar) {
      ((Toolbar) parent).setContentInsetsAbsolute(0, 0);
    }
  }

  protected void findChildViews() {
    long start = System.currentTimeMillis();
    View actionbarView = mActionBar.getCustomView();
    mBtnLeft = (Button) actionbarView
        .findViewById(R.id.cv_navigation_bar_btn_left);
    mBtnRight = (Button) actionbarView
        .findViewById(R.id.cv_navigation_bar_btn_right);
    mTxtCenter = (TextView) actionbarView
        .findViewById(R.id.cv_navigation_bar_txt_center);
    mImgCenter = (ImageView) actionbarView
        .findViewById(R.id.cv_navigation_bar_img_center);
    mImgLeft = (ImageView) actionbarView
        .findViewById(R.id.cv_navigation_bar_img_left);
    mImgRight = (ImageView) actionbarView
        .findViewById(R.id.cv_navigation_bar_img_right);
    mImgProfile = (ImageView) actionbarView
        .findViewById(R.id.cv_navigation_bar_img_profile);
    mTxtViewRemain = (TextView) actionbarView
        .findViewById(R.id.cv_navigation_bar_tv_remain);
    long last = System.currentTimeMillis();
    Log.d(TAG, "find views " + String.valueOf(last - start));

  }

  protected void setupChildViews() {
    long start = System.currentTimeMillis();
    if (mBtnLeft != null) {
      setupLeftButton();
    }
    if (mBtnRight != null) {
      setupRightButton();
    }
    if (mTxtCenter != null) {
      setupCenterTextview();
    }
    if (mImgCenter != null) {
      setupImgCenter();
    }
    if (mImgLeft != null) {
      setupImgLeft();
    }
    if (mImgRight != null) {
      setupImgRight();
    }
    if (mImgProfile != null) {
      setupImgProfile();
    }
    if (mTxtViewRemain != null) {
      setupTextViewRemain();
    }
    long last = System.currentTimeMillis();
    Log.d(TAG, "setup views " + String.valueOf(last - start));
  }

  protected void setupTextViewRemain() {

  }

  protected void setupImgProfile() {
    if (mImgProfile != null) {
      mImgProfile.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          if (SystemClock.elapsedRealtime() - mLastClickTime < DELAY_TIME) {
            return;
          }
          mLastClickTime = SystemClock.elapsedRealtime();
          Fragment activePage = mNavigationManager.getActivePage();
          if (activePage instanceof ChatFragment) {
            ((ChatFragment) activePage).clickMoreOptions();
          } else if (activePage instanceof MyProfileFragment) {
            ((MyProfileFragment) (activePage)).clickMoreOptions();
          } else if (activePage instanceof SliderProfileFragment) {
            ((SliderProfileFragment) (activePage)).clickMoreOptions();

          }
        }
      });
      mImgProfile.setBackgroundColor(Color.TRANSPARENT);
    }
  }

  protected void setupImgRight() {
    if (mImgRight != null) {
      mImgRight.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          if (SystemClock.elapsedRealtime() - mLastClickTime < DELAY_TIME) {
            return;
          }
          mLastClickTime = SystemClock.elapsedRealtime();
          Fragment activePage = mNavigationManager.getActivePage();
          if (activePage instanceof IncomingSettingFragment) {
            ((IncomingSettingFragment) activePage)
                .onRightNaviButtonClicked();
          } else if (activePage instanceof ManageOnlineAlertFragment) {
            ((ManageOnlineAlertFragment) activePage)
                .onRightNaviButtonClicked();
          } else if (activePage instanceof SliderProfileFragment) {
            ((SliderProfileFragment) activePage).hideChatMoreOptions();
            ((SliderProfileFragment) activePage).getSlidingMenu()
                .showSecondaryMenu();
            ((SliderProfileFragment) activePage).getSlidingMenu()
                .setSlidingEnabled(true);
          } else if (activePage instanceof BaseFragment) {
            // show conversion list
            BaseFragment baseFragment = (BaseFragment) activePage;
            if (baseFragment instanceof ChatFragment) {
              ChatFragment chatFragment = ((ChatFragment) baseFragment);
              chatFragment.hideChatMoreOptions();

              // #12145: hide panel and keyboard
              chatFragment.hidePanel();
              chatFragment.hideKeyboard();
            }
            if (baseFragment instanceof MyProfileFragment) {
              ((MyProfileFragment) baseFragment)
                  .hideChatMoreOptions();
              ((MyProfileFragment) activePage).getSlidingMenu()
                  .setSlidingEnabled(true);
            }
            if (baseFragment instanceof SliderProfileFragment) {
              ((SliderProfileFragment) baseFragment)
                  .hideChatMoreOptions();
            }
            baseFragment.showRightSlidingMenu();
          }
        }
      });
    }
  }

  protected void setupImgLeft() {
    View.OnClickListener clickListener = new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        //HIEPUH fix home actionbar left button click event
        if (SystemClock.elapsedRealtime() - mLastClickTime < DELAY_TIME) {
          return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        Fragment activePage = mNavigationManager.getActivePage();
        if (activePage instanceof ChatFragment) {
          ((ChatFragment) activePage).goBack();
        } else if (activePage instanceof MyProfileFragment) {
          ((MyProfileFragment) activePage).goBack();
        } else if (mNavigationManager.isBackStackEmpty()) {
          if (activePage instanceof HomeFragment
              && ((HomeFragment) activePage).getChildFragment() instanceof HotPagePeopleFragment) {
            mNavigationManager.addPage(new SettingsFragment());
          } else if (activePage instanceof HomeFragment
              && ((HomeFragment) activePage).getChildFragment() instanceof WhoCheckYouOutFragment) {
            ((HomeFragment) activePage).goToHotPage();
          } else if (activePage instanceof HomeFragment
              && ((HomeFragment) activePage).getChildFragment() instanceof BuzzFragment) {
            ((HomeFragment) activePage).goToHotPage();
          } else if (activePage instanceof HomeFragment
              && ((HomeFragment) activePage).getChildFragment() instanceof ConversationsFragment) {
            ((HomeFragment) activePage).goToHotPage();
          } else if (activePage instanceof HomeFragment
              && ((HomeFragment) activePage).getChildFragment() instanceof MeetPeopleFragment) {
            ((HomeFragment) activePage).goToHotPage();
          } else {
            if (baseFragmentActivity instanceof MainActivity) {
              MainActivity mainActivity = (MainActivity) baseFragmentActivity;
              mainActivity.changeTabActive(HomeFragment.TAB_HOT_PAGE);
            }
            if (activePage instanceof NotificationFragment) {
              Fragment fragment = HomeFragment.newInstance(HomeFragment.TAB_HOT_PAGE);
              mNavigationManager.switchPage(fragment);
            }

            if (activePage instanceof MyPageFragment) {
              Fragment fragment = HomeFragment.newInstance(HomeFragment.TAB_HOT_PAGE);
              mNavigationManager.switchPage(fragment);
            }
            if (activePage instanceof WebViewFragment) {
              Fragment fragment = HomeFragment.newInstance(HomeFragment.TAB_HOT_PAGE);
              mNavigationManager.switchPage(fragment);
            }
            if (activePage instanceof SettingsFragment) {
              Fragment fragment = HomeFragment.newInstance(HomeFragment.TAB_HOT_PAGE);
              mNavigationManager.switchPage(fragment);
            }
          }
        } else if (activePage instanceof BuzzFragment) {
          Fragment fragment = HomeFragment.newInstance(HomeFragment.TAB_HOT_PAGE);
          mNavigationManager.switchPage(fragment);
        } else {
          defaultBackButtonClickListener.onClick(v);
        }
      }
    };
    if (mImgLeft != null) {
      mImgLeft.setOnClickListener(clickListener);
    }
  }

  protected void setupImgCenter() {

  }

  protected void setupNotificationTextView() {

  }

  protected void setupCenterTextview() {
    if (mTxtCenter != null) {
      mTxtCenter.setOnClickListener(new View.OnClickListener() {

        @Override
        public void onClick(View v) {
          if (SystemClock.elapsedRealtime() - mLastClickTime < DELAY_TIME) {
            return;
          }
          mLastClickTime = SystemClock.elapsedRealtime();
          Fragment activePage = mNavigationManager.getActivePage();
          if (activePage instanceof ChatFragment) {
            ((ChatFragment) activePage).gotoUserProfile();
          }
        }
      });
    }
  }

  protected void setupRightButton() {
    View.OnClickListener clickListener = new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < DELAY_TIME) {
          return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        Fragment activePage = mNavigationManager.getActivePage();
        if (activePage instanceof PushNotificationSettingsFragment) {
          ((PushNotificationSettingsFragment) activePage).onSave();
        } else if (activePage instanceof EditProfileFragment) {
          ((EditProfileFragment) activePage).editProfile();
        } else if (activePage instanceof RegionSearchSettingFragment) {
          ((RegionSearchSettingFragment) activePage).onSave(v);
        } else if (activePage instanceof ThreeSizesFragment) {
          ((ThreeSizesFragment) activePage).onSave();
        } else if (activePage instanceof ProfileTextFragment) {
          ((ProfileTextFragment) activePage).onSave();
        } else if (activePage instanceof IncomingSettingFragment) {
          ((IncomingSettingFragment) activePage).onDone();
        } else if (activePage instanceof ManageOnlineAlertFragment) {
          ((ManageOnlineAlertFragment) activePage)
              .onRightNaviButtonClicked();
        } else if (activePage instanceof RegionSettingFragment) {
          ((RegionSettingFragment) activePage).onSave();
        } else if (activePage instanceof ChooseRegionFragment) {
          ((ChooseRegionFragment) activePage).onSave();
        } else if (activePage instanceof ChooseRegionSearchSettingFragment) {
          ((ChooseRegionSearchSettingFragment) activePage).onDone();
        } else if (activePage instanceof ChangePasswordFragment) {
          ((ChangePasswordFragment) activePage)
              .onChangeEmailPassword();
        }
      }
    };

    if (mBtnRight != null) {
      mBtnRight.setOnClickListener(clickListener);
    }

  }

  protected void setupLeftButton() {
    View.OnClickListener clickListener = new View.OnClickListener() {

      @Override
      public void onClick(View v) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < DELAY_TIME) {
          return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        Fragment activePage = mNavigationManager.getActivePage();
        if (mNavigationManager.isBackStackEmpty()) {
          if (activePage instanceof BaseFragment) {
            ((BaseFragment) activePage).showLeftSlidingMenu();
          }
        } else {
          defaultBackButtonClickListener.onClick(v);
        }
      }
    };
    if (mBtnLeft != null) {
      mBtnLeft.setOnClickListener(clickListener);
    }
  }

  protected void syncChildViews(Fragment activePage) {
    long start = System.currentTimeMillis();
    if (mBtnLeft != null) {
      syncLeftButton(activePage);
    }
    if (mBtnRight != null) {
      syncRightButton(activePage);
    }
    if (mTxtCenter != null) {
      syncCenterTextview(activePage);
    }
    if (mImgCenter != null) {
      syncImgCenter(activePage);
    }
    if (mImgLeft != null) {
      syncImgLeft(activePage);
    }
    if (mImgRight != null) {
      syncImgRight(activePage);
    }
    if (mImgProfile != null) {
      syncImgProfile(activePage);
    }
    if (mTxtViewRemain != null) {
      syncTextViewRemain(activePage);
    }
    long last = System.currentTimeMillis();
    Log.d(TAG, "sync views " + String.valueOf(last - start));
  }

  protected void syncTextViewRemain(Fragment activePage) {

  }

  protected void syncImgProfile(Fragment activePage) {

  }

  protected void syncImgRight(Fragment activePage) {
    if (mImgRight != null) {
      mImgRight.setVisibility(View.VISIBLE);
      int resId = R.drawable.ic_action_navigation_menu;
      mImgRight.setImageResource(resId);
      mImgRight.setBackgroundColor(Color.TRANSPARENT);
    }
  }

  protected void syncImgLeft(Fragment activePage) {

    if (mImgLeft != null) {
      if (activePage instanceof HomeFragment
          || activePage instanceof HotPagePeopleFragment) {
        mImgLeft.setBackgroundResource(0);
        mImgLeft.setImageResource(R.drawable.ic_action_settings_white);
      } else if (activePage instanceof WhoCheckYouOutFragment
          || activePage instanceof BuzzFragment
          || activePage instanceof MeetPeopleFragment
          || activePage instanceof ConversationsFragment) {
        mImgLeft.setBackgroundResource(0);
        mImgLeft.setImageResource(R.drawable.ic_action_action_home_white);
      } else {
        mImgLeft.setBackgroundResource(R.drawable.nav_btn_background);
        mImgLeft.setImageResource(R.drawable.ic_action_navigation_arrow_back);
      }
      mImgLeft.setBackgroundColor(Color.TRANSPARENT);
    }
  }

  protected void syncImgCenter(Fragment activePage) {

  }

  protected void syncCenterTextview(Fragment activePage) {
    if (mTxtCenter != null) {
      mTxtCenter.setVisibility(View.VISIBLE);
      int resIdTitle = -1;
      if (activePage instanceof AccountSettingsFragment) {
        resIdTitle = R.string.account_settings;
      } else if (activePage instanceof NotificationSettingsFragment) {
        resIdTitle = R.string.settings_notification;
      } else if (activePage instanceof AboutFragment) {
        resIdTitle = R.string.about;
      } else if (activePage instanceof ChangePasswordFragment) {
        resIdTitle = R.string.changepassword;
      } else if (activePage instanceof EthnicityFragment) {
        resIdTitle = R.string.ethnicity_title;
      } else if (activePage instanceof SearchSettingFragment) {
        resIdTitle = R.string.search_setting_title;
      } else if (activePage instanceof SettingsFragment) {
        resIdTitle = R.string.settings;
//            } else if (activePage instanceof ShakeToChatFragment) {
//                resIdTitle = R.string.shake_to_chat;
      } else if (activePage instanceof BuzzDetail) {
        resIdTitle = R.string.buzz_responses;
      } else if (activePage instanceof ShareMyBuzzFragment) {
        resIdTitle = R.string.share_my_buzz_title;
      } else if (activePage instanceof UserBuzzListFragment) {
        resIdTitle = R.string.item_profile_info_view_all_buzz;
      } else if (activePage instanceof SelectMessageFragment) {
        resIdTitle = R.string.meet_people_wink_bomb_select_message;
//            } else if (activePage instanceof WinkBombFragment) {
//                resIdTitle = R.string.meet_people_wink_bomb;
      } else if (activePage instanceof InputAboutMeFragment) {
        resIdTitle = R.string.profile_title_the_about_me;
      } else if (activePage instanceof BlockedUsersFragment) {
        resIdTitle = R.string.settings_account_block_users;
      } else if (activePage instanceof DeactivateAccountConfirmFragment) {
        resIdTitle = R.string.settings_account_deactivate;
      } else if (activePage instanceof DeactivateAccountFragment) {
        resIdTitle = R.string.settings_account_deactivate;
      } else if (activePage instanceof DistanceSettingFragment) {
        resIdTitle = R.string.distance_in;
      } else if (activePage instanceof NotificationFragment) {
        resIdTitle = R.string.notifications;
      } else if (activePage instanceof OnlineAlertFragment) {
        resIdTitle = R.string.settings_online_alert;
      } else if (activePage instanceof GiveGiftFragment) {
        resIdTitle = R.string.title_textview_my_profile_give_gift;
      } else if (activePage instanceof ManageOnlineAlertFragment) {
        resIdTitle = R.string.fragment_manage_online_alert_title;
      } else if (activePage instanceof EditProfileFragment) {
        resIdTitle = R.string.edit_my_profile_title;
      } else if (activePage instanceof RegionSearchSettingFragment
          || activePage instanceof ChooseRegionSearchSettingFragment) {
        resIdTitle = R.string.select_region_title;
      } else if (activePage instanceof MyPageFragment) {
        resIdTitle = R.string.mypage_title;
      } else if (activePage instanceof ThreeSizesFragment) {
        resIdTitle = R.string.profile_reg_three_sizes;
      } else if (activePage instanceof PushNotificationSettingsFragment) {
        resIdTitle = R.string.notification_fragment_title;
      } else if (activePage instanceof RegionSettingFragment) {
        resIdTitle = R.string.search_setting_location_region;
      } else if (activePage instanceof ChooseRegionFragment) {
        resIdTitle = R.string.select_region_title;
      } else if (activePage instanceof IncomingSettingFragment) {
        resIdTitle = R.string.st_callwaiting;
      } else if (activePage instanceof CallLogFragment) {
        resIdTitle = R.string.mypage_call_waiting;
      } else if (activePage instanceof ChooseGiftToSend) {
        resIdTitle = R.string.gift_title;
      } else if (activePage instanceof WebViewFragment) {
        int mPageType = ((WebViewFragment) activePage).getPageType();

        if (mPageType == WebViewFragment.PAGE_TYPE_TERM_OF_SERVICE) {
          resIdTitle = R.string.settings_terms_of_service_terms_of_service;
        } else if (mPageType == WebViewFragment.PAGE_TYPE_PRIVACY_POLICY) {
          resIdTitle = R.string.settings_terms_of_service_privacy_policy;
        } else if (mPageType == WebViewFragment.PAGE_TYPE_NOTICE) {
          resIdTitle = R.string.settings_terms_of_service_terms_of_use;
        } else if (mPageType == WebViewFragment.PAGE_TYPE_VERIFY_AGE) {
          resIdTitle = R.string.title_age_verification;
        } else if (mPageType == WebViewFragment.PAGE_TYPE_FREE_POINT) {
          resIdTitle = R.string.free_point_title;
        } else if (mPageType == WebViewFragment.PAGE_TYPE_HOW_TO_USE) {
          resIdTitle = R.string.how_to_use;
        } else if (mPageType == WebViewFragment.PAGE_TYPE_SUPPORT) {
          resIdTitle = R.string.support;
        } else if (mPageType == WebViewFragment.PAGE_TYPE_BUY_PONIT) {
          resIdTitle = R.string.buy_points;
        } else if (mPageType == WebViewFragment.PAGE_TYPE_WEB_VIEW) {
          resIdTitle = R.string.common_app_name;
        } else if (mPageType == WebViewFragment.PAGE_TYPE_LOGIN_OTHER_SYS) {
          resIdTitle = R.string.common_app_name;
        } else if (mPageType == WebViewFragment.PAGE_TYPE_ANDG_HOMEPAGE) {
          resIdTitle = R.string.common_app_name;
        } else if (mPageType == WebViewFragment.PAGE_TYPE_ABOUT_PAYMENT) {
          resIdTitle = R.string.common_app_name;
        } else {
          resIdTitle = R.string.common_app_name;
        }
      } else if (activePage instanceof SearchByNameFragment) {
        resIdTitle = R.string.search_by_name_title;
      } else if (activePage instanceof ConnectionFragment) {
        resIdTitle = R.string.mypage_favourites;
      }

      if (resIdTitle != -1) {
        mTxtCenter.setText(resIdTitle);
      } else {
        mTxtCenter.setText("");
      }
    }
  }

  protected void syncRightButton(Fragment activePage) {
    if (mBtnRight != null) {
      mBtnRight.setVisibility(View.VISIBLE);
      int resId = -1;
      if (activePage instanceof PushNotificationSettingsFragment) {
        resId = R.string.common_done;
      } else if (activePage instanceof EditProfileFragment) {
        resId = R.string.profile_reg_done;
      } else if (activePage instanceof RegionSearchSettingFragment
          || activePage instanceof ChooseRegionSearchSettingFragment) {
        resId = R.string.common_done;
      } else if (activePage instanceof ThreeSizesFragment) {
        resId = R.string.common_save;
      } else if (activePage instanceof ProfileTextFragment) {
        resId = R.string.common_save;
      } else if (activePage instanceof IncomingSettingFragment) {
        resId = R.string.common_done;
      } else if (activePage instanceof ManageOnlineAlertFragment) {
        resId = R.string.common_done;
      } else if (activePage instanceof RegionSettingFragment) {
        resId = R.string.common_done;
      } else if (activePage instanceof ChooseRegionFragment) {
        resId = R.string.common_done;
      } else if (activePage instanceof ChangePasswordFragment) {
        resId = R.string.common_done;
      }
      if (resId != -1) {
        mBtnRight.setText(resId);
      }
      mBtnRight.setVisibility((resId == -1) ? View.INVISIBLE
          : View.VISIBLE);
    }
  }

  protected void syncLeftButton(Fragment activePage) {
    if (mBtnLeft != null) {
      mBtnLeft.setVisibility(View.VISIBLE);
      int resId = -1;
      if (activePage instanceof EthnicityFragment) {
        resId = R.string.ethnicity_navigator_left;
      } else if (activePage instanceof SearchSettingFragment) {
        resId = R.string.search_setting_navigator_left;
//            } else if (activePage instanceof WinkBombFragment) {
//                resId = R.string.search_setting_navigator_left;
      }
      if (resId != -1) {
        mBtnLeft.setVisibility(View.VISIBLE);
        mBtnLeft.setText(resId);
      } else {
        mBtnLeft.setVisibility(View.INVISIBLE);
      }
    }
  }

  @Override
  public void setBackButtonClickListener(View.OnClickListener onClickListener) {

  }

  @Override
  public void resetBackButtonClickListener() {

  }

  @Override
  public void setTextCenterTitle(String title) {
    if (mTxtCenter != null) {
      mTxtCenter.setText(title);
    }
  }

  @Override
  public void setTextCenterTitle(int resId) {
    if (mTxtCenter != null) {
      mTxtCenter.setText(mActionBar.getThemedContext().getString(resId));
    }
  }

  @Override
  public void syncShakeToChat(Fragment activePage) {

  }

  @Override
  public void setTextRightTitle(String title) {

  }

  @Override
  public void disableActionBarWhenShakeToChat() {

  }

  @Override
  public void enableActionBarAfterShakeToChat() {

  }

  @Override
  public void displayTimeRemainingHiddenChat(int timeRemaining) {
    if (mTxtViewRemain != null) {
      mTxtViewRemain.setText(String.valueOf(timeRemaining));
    }
  }

  @Override
  public void stopHiddenChat() {
    if (mImgProfile != null) {
      mImgProfile.setVisibility(View.VISIBLE);
    }

    if (mTxtViewRemain != null) {
      mTxtViewRemain.setVisibility(View.INVISIBLE);
    }
  }

  @Override
  public void switchToHiddenChat() {
    if (mImgProfile != null) {
      mImgProfile.setVisibility(View.INVISIBLE);
    }
    if (mTxtViewRemain != null) {
      mTxtViewRemain.setVisibility(View.VISIBLE);
    }

    if (mTxtCenter != null) {
      mTxtCenter.setText(R.string.chat_screen_hidden_user_name);
    }
  }

  @Override
  public void resetWhenNotHiddenChat() {
    if (mImgProfile != null) {
      mImgProfile.setVisibility(View.VISIBLE);
    }
    if (mTxtViewRemain != null) {
      mTxtViewRemain.setVisibility(View.INVISIBLE);
    }
  }

  @Override
  public int getHeight() {
    return 0;
  }

  @Override
  public void setVisibility(int visible) {

  }

  @Override
  public void startAnimation(Animation animation) {

  }

  @Override
  public View getView() {
    return null;
  }

  @Override
  public void displayEditButton(boolean isEditing) {

  }

  @Override
  public void disableEditRightButton() {
    Fragment activePage = mNavigationManager.getActivePage();

    if (activePage instanceof EditProfileFragment
        || activePage instanceof ChangePasswordFragment) {
      if (mBtnRight != null) {
        mBtnRight.setEnabled(false);
      }
    }
  }

  @Override
  public void enableEditRightButton() {
    Fragment activePage = mNavigationManager.getActivePage();

    if (activePage instanceof EditProfileFragment
        || activePage instanceof ChangePasswordFragment) {
      if (mBtnRight != null) {
        mBtnRight.setEnabled(true);
      }
    }
  }

  @Override
  public void setAllEnable(boolean enable) {
    if (mActionBar.getCustomView() instanceof ViewGroup) {
      ViewGroup root = (ViewGroup) mActionBar.getCustomView();
      for (int i = 0; i < root.getChildCount(); i++) {
        View child = root.getChildAt(i);
        child.setEnabled(enable);
        child.setClickable(enable);
        if (child instanceof ViewGroup) {
          setEnableViewGroup((ViewGroup) child, enable);
        }
      }
    }
  }

  private void setEnableViewGroup(ViewGroup view, boolean enable) {
    for (int i = 0; i < view.getChildCount(); i++) {
      View child = view.getChildAt(i);
      child.setEnabled(enable);
      child.setClickable(enable);
      if (child instanceof ViewGroup) {
        setEnableViewGroup((ViewGroup) child, enable);
      }
    }
  }

  @Override
  public void syncActionBar() {
    syncActionBar(mNavigationManager.getActivePage());
  }

  @Override
  public void show() {
    CustomActionBarActivity currActivity = mNavigationManager.getActivity();
    if (currActivity != null) {
      currActivity.showActionBar();
    }
  }

  @Override
  public void hide() {
    CustomActionBarActivity currActivity = mNavigationManager.getActivity();
    if (currActivity != null) {
      currActivity.hideActionBar();
    }
  }

  @Override
  public void setProfileVisibility(int visibility) {
    if (mImgProfile != null) {
      mImgProfile.setVisibility(visibility);
    }
  }

  @Override
  public void setRightVisibility(int visibility) {
    if (mImgRight != null) {
      mImgRight.setVisibility(visibility);
      switch (visibility) {
        case View.VISIBLE: {
          setSlidingEnabled(true);
        }
        break;
        case View.GONE: {
          setSlidingEnabled(false);
        }
        break;
        default:
          break;
      }
    }
  }

  /**
   * Set Sliding Enabled
   */
  private void setSlidingEnabled(boolean isEnable) {
    if (baseFragmentActivity instanceof MainActivity) {
      MainActivity mainActivity = (MainActivity) baseFragmentActivity;
      mainActivity.getSlidingMenu().setSlidingEnabled(isEnable);
    }
  }

}
