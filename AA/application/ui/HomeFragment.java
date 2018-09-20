package com.application.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.application.common.TabLayout;
import com.application.connection.response.GetAttendtionNumberResponse;
import com.application.constant.Constants;
import com.application.ui.buzz.BuzzFragment;
import com.application.ui.chat.ConversationsFragment;
import com.application.ui.customeview.BadgeTextView;
import com.application.ui.hotpage.HotPagePeopleFragment;
import com.application.util.LogUtils;
import com.application.util.Utility;
import com.application.util.preferece.UserPreferences;
import glas.bbsystem.R;
import java.util.List;


/**
 * Created by HungHN on 12/23/2015.
 */
public class HomeFragment extends BaseFragment implements TabLayout.OnTabSelectedListener {

  public static final int TAB_MEET_PEOPLE = 0;
  public static final int TAB_FOOTER_PRINT = 1;
  public static final int TAB_TIMELINE = 2;
  public static final int TAB_CHAT = 3;
  public static final int TAB_HOT_PAGE = 4;
  private static final String TAG = "HomeFragment";
  private static final String KEY_TAB_OPEN = "key_tab_open";
  private static final String KEY_FINISH_REG = "key_finish_reg";
  private static final int TAB_COUNT = 5;
  private MainActivity mainActivity;
  /* TabLayout */
  private View tabLayout;
  private int tabOpen = TAB_MEET_PEOPLE;

  private TabLayout mTabs;
  private View mTbConversations, mTbFootPrint, mTbTimeline, mTbTop, mTbHotPage;
  private BadgeTextView mBtvChatNoti;
  private BadgeTextView mBtvFootPrintNoti;

  // get finish register flag
  private int finishRegisterFlag = Constants.FINISH_REGISTER_NO;

  public static HomeFragment newInstance(int tab) {
    HomeFragment homeFragment = new HomeFragment();
    Bundle bundle = new Bundle();
    bundle.putInt(KEY_TAB_OPEN, tab);
    homeFragment.setArguments(bundle);
    MainActivity.checkactionbar = true;
    return homeFragment;
  }

  public static HomeFragment newInstance(int tab, int finishRegisterFlag) {
    HomeFragment homeFragment = new HomeFragment();
    Bundle bundle = new Bundle();
    bundle.putInt(KEY_TAB_OPEN, tab);
    bundle.putInt(KEY_FINISH_REG, finishRegisterFlag);
    homeFragment.setArguments(bundle);
    return homeFragment;
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    if (activity instanceof MainActivity) {
      mainActivity = (MainActivity) activity;
    }

//        if (activity instanceof CustomActionBarActivity) {
//            CustomActionBarActivity customActionBarActivity = ((CustomActionBarActivity) activity);
//            mActionBar = customActionBarActivity.getCustomActionBar();
//            mNavigationManager = customActionBarActivity.getNavigationManager();
//        } else if (activity instanceof CustomActionBar) {
//            mActionBar = (CustomActionBar) activity;
//        }
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (savedInstanceState != null) {
      initDataFromBundle(savedInstanceState);
    } else {
      initDataFromBundle(getArguments());
    }
  }

  private void initDataFromBundle(Bundle bundle) {
    if (bundle == null) {
      return;
    }
    tabOpen = bundle.getInt(KEY_TAB_OPEN);
    finishRegisterFlag = bundle.getInt(KEY_FINISH_REG);
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    outState.putInt(KEY_TAB_OPEN, tabOpen);
    outState.putInt(KEY_FINISH_REG, finishRegisterFlag);
    super.onSaveInstanceState(outState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_home, container, false);
    LogUtils.d(TAG, "Home Fragment - onCreateView");
    return view;
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    LogUtils.d(TAG, "Home Fragment - onViewCreated");
    if (savedInstanceState != null) {
      initDataFromBundle(savedInstanceState);
    } else {
      initDataFromBundle(getArguments());
    }
    initTabLayout(view);
  }

  @Override
  public void onResume() {
    super.onResume();
    if (mainActivity != null) {
      mainActivity.showUnreadMessage();
      mainActivity.requestTotalUnreadMsg();
    }
  }

  @Override
  public void onStop() {
    super.onStop();
    Bundle bundle = getArguments();
    if (bundle != null) {
      bundle.putInt(KEY_TAB_OPEN, tabOpen);
    } else {
      bundle = new Bundle();
      bundle.putInt(KEY_TAB_OPEN, tabOpen);
//            setArguments(bundle);
    }
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
  }

  @SuppressLint("InflateParams")
  private void initTabLayout(View view) {
    tabLayout = view.findViewById(R.id.tabLayout);
    /* Tabs */
    mTabs = (TabLayout) view.findViewById(R.id.tabs);
    mTabs.setTabGravity(TabLayout.GRAVITY_FILL);
    mTabs.setTabMode(TabLayout.MODE_FIXED);
    mTabs.setOnTabSelectedListener(this);

    /* Tab */
    mTbTop = LayoutInflater.from(getActivity()).inflate(R.layout.tabbar_top, null);
    mTbFootPrint = LayoutInflater.from(getActivity()).inflate(R.layout.tabbar_foot_print, null);

    mTbConversations = LayoutInflater.from(getActivity())
        .inflate(R.layout.tabbar_conversations, null);
    mTbTimeline = LayoutInflater.from(getActivity()).inflate(R.layout.tabbar_timeline, null);

    /* Badge */
    mBtvChatNoti = (BadgeTextView) mTbConversations.findViewById(R.id.btv_chat_noti);
    mBtvFootPrintNoti = (BadgeTextView) mTbFootPrint.findViewById(R.id.btv_footprint_noti);

    mTbHotPage = LayoutInflater.from(getActivity()).inflate(R.layout.tabbar_hotpage, null);

    View customTabs[] = new View[]{mTbTop, mTbFootPrint, mTbTimeline, mTbConversations, mTbHotPage};

    /* Add Tab into TabLayout */
    for (int i = 0; i < TAB_COUNT; i++) {
      if (i == tabOpen) {
        mTabs.addTab(mTabs.newTab().setCustomView(customTabs[i]), i, true);
      } else {
        mTabs.addTab(mTabs.newTab().setCustomView(customTabs[i]), i, false);
      }
    }
    ((ViewGroup) mTabs.getChildAt(0)).getChildAt(4).setVisibility(View.GONE);
  }

  public void switchTab(int indexTab) {
    if (mTabs == null) {
      return;
    }
    mTabs.getTabAt(indexTab).select();
  }

  public Fragment getHomeActivePage() {
    FragmentManager manager = getChildFragmentManager();
    return manager.findFragmentById(R.id.fragment_main_content);
  }

  public void goToMeetPeople() {
    MainActivity.checkactionbar = true;
    if (getHomeActivePage() instanceof MeetPeopleFragment) {
      getSlidingMenu().showContent();
    } else if (getHomeActivePage() instanceof WhoCheckYouOutFragment
        || getHomeActivePage() instanceof BuzzFragment
        || getHomeActivePage() instanceof ConversationsFragment
        ) {
      switchTab(TAB_MEET_PEOPLE);
    } else {
      handleMenu(MainActivity.FUNCTION_MEET_PEOPLE);
      if (mTabs.getSelectedTab().getPosition() != TAB_MEET_PEOPLE) {
        mTabs.getTabAt(TAB_MEET_PEOPLE).change();
      }
    }
  }

  public void goToHotPage() {
    MainActivity.checkactionbar = true;
    if (getHomeActivePage() instanceof HotPagePeopleFragment) {
      getSlidingMenu().showContent();
    } else if (getHomeActivePage() instanceof WhoCheckYouOutFragment
        || getHomeActivePage() instanceof BuzzFragment
        || getHomeActivePage() instanceof ConversationsFragment
        || getHomeActivePage() instanceof MeetPeopleFragment
        ) {
      switchTab(TAB_HOT_PAGE);
    } else {
      handleMenu(MainActivity.FUNCTION_HOT_PAGE);
      if (mTabs.getSelectedTab().getPosition() != TAB_HOT_PAGE) {
        mTabs.getTabAt(TAB_HOT_PAGE).change();
      }
    }
  }

  private Fragment handleMenu(int function) {
    FragmentManager manager = getChildFragmentManager();
    Fragment fragment = manager.findFragmentById(R.id.fragment_main_content);
    String tag = null;
    switch (function) {
      case MainActivity.FUNCTION_MEET_PEOPLE:
//                MainActivity.checkactionbar = true;
        tabOpen = TAB_MEET_PEOPLE;
        tag = MainActivity.TAG_FRAGMENT_MEETPEOPLE;
        if (!(fragment instanceof MeetPeopleFragment)) {
          Utility.hideSoftKeyboard(getActivity());
          fragment = MeetPeopleFragment.newInstance();
          if (finishRegisterFlag == Constants.FINISH_REGISTER_YES) {
            Bundle bundle = new Bundle();
            bundle.putInt(Constants.ARGUMENT_FINISH_REGISTER_FLAG,
                finishRegisterFlag);
            fragment.setArguments(bundle);
            finishRegisterFlag = Constants.FINISH_REGISTER_NO;
          }
        }
        break;
      case MainActivity.FUNCTION_FOOT_PRINT:
        tag = MainActivity.TAG_FRAGMENT_FOOTPRINT;
        getSlidingMenu().setSlidingEnabled(true);
        if (!(fragment instanceof WhoCheckYouOutFragment)) {
          fragment = new WhoCheckYouOutFragment();
        }
        tabOpen = TAB_FOOTER_PRINT;
        break;
      case MainActivity.FUNCTION_BUZZ:
        tag = MainActivity.TAG_FRAGMENT_MY_BUZZ;
        if (!(fragment instanceof BuzzFragment)) {
          fragment = new BuzzFragment();
        }
        tabOpen = TAB_TIMELINE;
        break;
      case MainActivity.FUNCTION_MY_CHATS:
        tag = MainActivity.TAG_FRAGMENT_MY_CHATS;
        if (!(fragment instanceof ConversationsFragment)) {
          fragment = new ConversationsFragment();
        }
        tabOpen = TAB_CHAT;
        break;

      case MainActivity.FUNCTION_HOT_PAGE:
//                MainActivity.checkactionbar = true;
        tabOpen = TAB_HOT_PAGE;
        tag = MainActivity.TAG_FRAGMENT_HOT_PAGE;
        if (!(fragment instanceof HotPagePeopleFragment)) {
          Utility.hideSoftKeyboard(getActivity());
          fragment = HotPagePeopleFragment.newInstance();
        }
        break;
    }
    if (null == fragment || null == tag) {
      throw new IllegalArgumentException("Function invalid:" + function);
    }
    mTabs.getTabAt(tabOpen).change();
    FragmentTransaction transaction = manager.beginTransaction();
    transaction.replace(R.id.fragment_main_content, fragment);
    transaction.commit();
    if (mainActivity != null) {
      mainActivity.showContent();
    }
    LogUtils.d(TAG, "Tag open: " + tag);
    return fragment;
  }


  public void goToWhoCheckMeOut() {
    if (getHomeActivePage() instanceof WhoCheckYouOutFragment) {
      getSlidingMenu().showContent();
    } else if (getHomeActivePage() instanceof MeetPeopleFragment
        || getHomeActivePage() instanceof BuzzFragment
        || getHomeActivePage() instanceof ConversationsFragment
        ) {
      switchTab(TAB_FOOTER_PRINT);
    } else {
      handleMenu(MainActivity.FUNCTION_FOOT_PRINT);
      if (mTabs.getSelectedTab().getPosition() != TAB_FOOTER_PRINT) {
        mTabs.getTabAt(TAB_FOOTER_PRINT).change();
      }
    }
  }

  public void goToTimeline() {
    if (getHomeActivePage() instanceof BuzzFragment) {
      getSlidingMenu().showContent();
    } else if (getHomeActivePage() instanceof MeetPeopleFragment
        || getHomeActivePage() instanceof WhoCheckYouOutFragment
        || getHomeActivePage() instanceof ConversationsFragment
        || getHomeActivePage() instanceof HotPagePeopleFragment
        ) {
      switchTab(TAB_TIMELINE);
    } else {
      handleMenu(MainActivity.FUNCTION_BUZZ);
      if (mTabs.getSelectedTab().getPosition() != TAB_TIMELINE) {
        mTabs.getTabAt(TAB_TIMELINE).change();
      }
    }
  }

  public void goToConversation() {
    if (getHomeActivePage() instanceof ConversationsFragment) {
      getSlidingMenu().showContent();
    } else if (getHomeActivePage() instanceof WhoCheckYouOutFragment
        || getHomeActivePage() instanceof BuzzFragment
        || getHomeActivePage() instanceof MeetPeopleFragment) {
      switchTab(TAB_CHAT);
    } else {
      handleMenu(MainActivity.FUNCTION_MY_CHATS);
      if (mTabs.getSelectedTab().getPosition() != TAB_CHAT) {
        mTabs.getTabAt(TAB_CHAT).change();
      }
    }
  }

  @Override
  public void onTabSelected(TabLayout.Tab tab) {
    openTabSelected(tab.getPosition());
  }

  private void openTabSelected(int position) {
    switch (position) {
      case TAB_MEET_PEOPLE:
        handleMenu(MainActivity.FUNCTION_MEET_PEOPLE);
        break;
      case TAB_FOOTER_PRINT:
        handleMenu(MainActivity.FUNCTION_FOOT_PRINT);
        break;
      case TAB_TIMELINE:
        handleMenu(MainActivity.FUNCTION_BUZZ);
        break;
      case TAB_CHAT:
        handleMenu(MainActivity.FUNCTION_MY_CHATS);
        break;
      case TAB_HOT_PAGE:
        handleMenu(MainActivity.FUNCTION_HOT_PAGE);
        break;
      default:
        break;
    }
  }

  // TabChangeListener
  @Override
  public void onTabUnselected(TabLayout.Tab tab) {

  }

  @Override
  public void onTabReselected(TabLayout.Tab tab) {

  }

  public Fragment getChildFragment() {
    FragmentManager manager = getChildFragmentManager();
    return manager.findFragmentById(R.id.fragment_main_content);
  }

  public void showUnreadMessage(int numUnreadMessage) {
    mBtvChatNoti.setTextNumber(numUnreadMessage);
  }

  public void showChatAndFootPrintNotification(GetAttendtionNumberResponse mResponse) {
    int new_checkout_num = mResponse.getNew_checkout_num();
    int new_fvt_num = mResponse.getNew_fvt_num();
    int fvt_num = mResponse.getFvt_num();
    int checkout_num = mResponse.getCheckout_num();

    UserPreferences userPreferences = UserPreferences.getInstance();
    userPreferences.saveUnlockWhoCheckMeOut(checkout_num);
    userPreferences.saveNumberFavoritedMe(fvt_num);
    mBtvFootPrintNoti.setTextNumber(new_checkout_num);
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    LogUtils.d(TAG, "onActivityResult");
    super.onActivityResult(requestCode, resultCode, data);
    List<Fragment> fragments = getChildFragmentManager().getFragments();
    if (fragments != null) {
      for (Fragment fragment : fragments) {
        fragment.onActivityResult(requestCode, resultCode, data);
      }
    }
  }
}
