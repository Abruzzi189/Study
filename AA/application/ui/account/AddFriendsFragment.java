package com.application.ui.account;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;
import android.widget.TextView;
import com.application.ui.BaseFragment;
import com.application.util.LogUtils;
import glas.bbsystem.R;


public class AddFriendsFragment extends BaseFragment implements
    OnTabChangeListener {

  private static final String TAG = "AddFriendsFragment";
  private final String TAB_FACEBOOK = "Facebook";
  private final String TAB_CONTACT = "Contacts";
  private final String TAB_KEY = "tab";
  // 1 - tab contact, 0 - tab fb
  private final int DEFAULT_TAB = 1;
  private TabHost mTabHost;
  private TabWidget mTabWidget;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_addfriends, container,
        false);
    initView(view);
    return view;
  }

  @Override
  public void onActivityCreated(Bundle bundle) {
    super.onActivityCreated(bundle);
    if (bundle != null) {
      mTabHost.setCurrentTabByTag(bundle.getString(TAB_KEY));
    } else {
      mTabHost.setCurrentTab(DEFAULT_TAB);
    }
  }

  @Override
  protected boolean isControlNavigation() {
    return false;
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putString(TAB_KEY, mTabHost.getCurrentTabTag());
  }

  private void initView(View view) {
    mTabHost = (TabHost) view.findViewById(android.R.id.tabhost);
    mTabWidget = (TabWidget) view.findViewById(android.R.id.tabs);
    mTabHost.setup();
    TabSpec tabSpec = mTabHost.newTabSpec(TAB_FACEBOOK);
    tabSpec.setIndicator(getTabIndicator(mTabHost.getContext(),
        R.id.tab_add_friend_widget_title_facebook,
        R.string.tab_facebook));
    tabSpec.setContent(R.id.fragment_addfriends_tabcontent_facebook);
    mTabHost.addTab(tabSpec);
    tabSpec = mTabHost.newTabSpec(TAB_CONTACT);
    tabSpec.setIndicator(getTabIndicator(mTabHost.getContext(),
        R.id.tab_add_friend_widget_title_contact,
        (R.string.tab_contact)));
    tabSpec.setContent(R.id.fragment_addfriends_tabcontent_contact);
    mTabHost.addTab(tabSpec);
    mTabHost.setOnTabChangedListener(this);
  }

  private View getTabIndicator(Context context, int idView, int idTitle) {
    View view = LayoutInflater.from(context).inflate(
        R.layout.tab_add_friend_widget, null);
    TextView tv = null;
    tv = (TextView) view.findViewById(idView);
    tv.setVisibility(View.VISIBLE);
    tv.setText(idTitle);
    return view;
  }

  @Override
  public void onTabChanged(String tabId) {
    LogUtils.i(TAG, "Tab select=" + tabId);
    FragmentManager fm = getFragmentManager();
    BaseFragment fragment = null;
    if (fm.findFragmentByTag(tabId) == null) {
      int placeholder = -1;
      if (tabId.equals(TAB_FACEBOOK)) {
        placeholder = R.id.fragment_addfriends_tabcontent_facebook;
        fragment = new FacebookFragment();
      } else {
        placeholder = R.id.fragment_addfriends_tabcontent_contact;
        fragment = new ContactsListFragment();
      }
      fm.beginTransaction().replace(placeholder, fragment, tabId)
          .commit();
    } else {
      if (tabId.equals(TAB_FACEBOOK)) {
        FacebookFragment facebookFragment = (FacebookFragment) fm
            .findFragmentByTag(TAB_FACEBOOK);
        if (facebookFragment == null) {
          fragment = new FacebookFragment();
          fm.beginTransaction()
              .replace(
                  R.id.fragment_addfriends_tabcontent_facebook,
                  facebookFragment, tabId).commit();
        }
        facebookFragment.ensureOpenSession();
      } else {
        ContactsListFragment contactFragment = (ContactsListFragment) fm
            .findFragmentByTag(TAB_CONTACT);
        if (contactFragment == null) {
          fragment = new ContactsListFragment();
          fm.beginTransaction()
              .replace(
                  R.id.fragment_addfriends_tabcontent_contact,
                  contactFragment, tabId).commit();
        }
      }
    }
  }
}
