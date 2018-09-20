package com.application.ui.chat;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import com.application.ui.BaseFragment;
import glas.bbsystem.R;


public class CallLogFragment extends BaseFragment {

  public static final int INDEX_RECEIVER = 0;
  public static final int INDEX_SENDER = 1;
  private static final String KEY_CURRENT_TAB_INDEX = "current_tab_index";
  private static final String TAB_RECEIVER = "tab_receiver";
  private static final String TAB_SENDER = "tab_sender";
  private int mCurrentIndexTab = 0;
  private TabHost mTabHost;

  public static CallLogFragment getInstance(int index) {
    CallLogFragment fragment = new CallLogFragment();
    Bundle bundle = new Bundle();
    bundle.putInt(KEY_CURRENT_TAB_INDEX, index);
    fragment.setArguments(bundle);
    return fragment;

  }

  public Bundle saveInstanceState() {
    Bundle state = new Bundle();
    state.putInt(KEY_CURRENT_TAB_INDEX, mCurrentIndexTab);
    return state;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (savedInstanceState == null) {
      mCurrentIndexTab = getArguments().getInt(KEY_CURRENT_TAB_INDEX);
    } else {
      mCurrentIndexTab = savedInstanceState.getInt(KEY_CURRENT_TAB_INDEX);
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_call_log, container,
        false);
    initView(view);
    return view;
  }

  private void initView(View view) {
    mTabHost = (TabHost) view.findViewById(android.R.id.tabhost);
    mTabHost.setup();
    mTabHost.getTabWidget().setDividerDrawable(
        android.R.drawable.divider_horizontal_bright);
    String receiverTitle = getString(R.string.call_log_tab_receiver_title);
    mTabHost.addTab(newTabSpec(mTabHost.getContext(), TAB_RECEIVER,
        R.id.call_log_received, receiverTitle));
    String senderTitle = getString(R.string.call_log_tab_sender_title);
    mTabHost.addTab(newTabSpec(mTabHost.getContext(), TAB_SENDER,
        R.id.call_log_called, senderTitle));
    mTabHost.setOnTabChangedListener(new OnTabChangeListener() {
      @Override
      public void onTabChanged(String tabId) {
        FragmentManager fm = getChildFragmentManager();
        BaseFragment baseFragment = (BaseFragment) fm
            .findFragmentByTag(tabId);
        // handleTitle(tabId);
        if (baseFragment == null) {
          int placeholder = -1;
          if (tabId.equals(TAB_RECEIVER)) {
            placeholder = R.id.call_log_received;
            baseFragment = BaseCallLogListFragment
                .getInstance(BaseCallLogListFragment.TYPE_RECEIVER);
          } else if (tabId.equals(TAB_SENDER)) {
            placeholder = R.id.call_log_called;
            baseFragment = BaseCallLogListFragment
                .getInstance(BaseCallLogListFragment.TYPE_SENDER);
          }
          if (baseFragment != null) {
            fm.beginTransaction()
                .replace(placeholder, baseFragment, tabId)
                .commit();
          }
        } else {
          int placeholder = -1;
          if (tabId.equals(TAB_RECEIVER)) {
            placeholder = R.id.call_log_received;
          } else if (tabId.equals(TAB_SENDER)) {
            placeholder = R.id.call_log_called;
          }
          fm.beginTransaction()
              .replace(placeholder, baseFragment, tabId).commit();
        }
      }
    });
    FragmentManager fm = getChildFragmentManager();
    // handleTitle(TAB_RECEIVER);
    int placeholder = R.id.call_log_received;
    BaseFragment baseFragment = BaseCallLogListFragment
        .getInstance(BaseCallLogListFragment.TYPE_RECEIVER);
    fm.beginTransaction().replace(placeholder, baseFragment, TAB_RECEIVER)
        .commit();
    mTabHost.setCurrentTab(mCurrentIndexTab);
    // set Title
    if (mActionBar != null) {
      mActionBar.setTextCenterTitle(R.string.call_log_title);
    }

  }

  private TabSpec newTabSpec(Context context, String tag, int idContentView,
      String title) {
    TabSpec tabSpec = mTabHost.newTabSpec(tag);
    tabSpec.setIndicator(getTabIndicator(context, tag, title));
    tabSpec.setContent(idContentView);
    return tabSpec;
  }

  private View getTabIndicator(Context context, String tag, String title) {
    View view = View.inflate(context, R.layout.tab_call_log_widget, null);
    TextView tv = (TextView) view.findViewById(R.id.title);
    if (tv != null) {
      tv.setVisibility(View.VISIBLE);
      tv.setText(title);
    }
    return view;
  }
}