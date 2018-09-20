package com.application.ui.buzz;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import com.application.constant.Constants;
import com.application.entity.BuzzListItem;
import com.application.ui.BaseFragment;
import com.application.ui.buzz.BaseBuzzListFragment.ListType;
import com.application.ui.buzz.BaseBuzzListFragment.MakeBuzzListener;
import com.application.ui.buzz.BaseBuzzListFragment.OnActionNoRefresh;
import com.application.ui.buzz.BaseBuzzListFragment.OnActionRefresh;
import com.application.ui.buzz.BaseBuzzListFragment.OnAddBuzzFromFavorite;
import com.application.ui.customeview.NavigationBar.OnNavigationClickListener;
import com.application.util.LogUtils;
import com.application.util.Utility;
import com.application.util.preferece.Preferences;
import glas.bbsystem.R;
import java.util.List;


public class BuzzFragment extends BaseFragment implements /* OnTabChangeListener, */
    OnNavigationClickListener, OnClickListener, OnActionNoRefresh, OnActionRefresh {

  // private TabHost mTabHost;
  public static final String TAB_KEY = "tab";
  public static final String TAB_LOCAL = "Local";
  public static final String TAB_MINE = "Buzz_Mine";
  public static final String TAB_FAVORITES = "Buzz_Favorites";
  public static final String KEY_HAS_BACKNAVIGATION = "has_back_navigation";
  private static final String TAG = "BuzzFragment";
  private static final int[] fragmentIDs = {R.id.buzz_frame_all,
      R.id.buzz_frame_favorite, R.id.buzz_frame_mine};
  public boolean mHasBackNaviagtion;
  private String mCurrentTab = TAB_LOCAL;
  private String tabSelected = "";
  private TextView mBtnAll;
  private TextView mBtnFavorites;
  private TextView mBtnMine;
  private View mViewSelectAll;
  private View mViewSelectFavorites;
  private View mViewSelectMine;
  private View mSelectAll, mFavorites, mMine;
  private OnAddBuzzFromFavorite mOnAddBuzzFromFavorite = new OnAddBuzzFromFavorite() {

    @Override
    public void onAddBuzz(BuzzListItem buzzItem) {
      LogUtils.d("ToanTk123", buzzItem.getBuzzId());
      Fragment fragment = getChildFragmentManager().findFragmentById(
          R.id.buzz_frame_mine);
      if (fragment != null && fragment instanceof BaseBuzzListFragment) {
        ((BaseBuzzListFragment) fragment).addBuzz(buzzItem);
      }
    }

  };
  private MakeBuzzListener mMakeBuzzListener = buzzID -> {

//    @Override
//    public void onSuccess(String buzzID) {
//      if (TAB_FAVORITES.equals(mCurrentTab)) {
//        mCurrentTab = TAB_LOCAL;
//        setTab(true, buzzID);
//      }
//    }
    if (TAB_FAVORITES.equals(mCurrentTab)) {
      mCurrentTab = TAB_LOCAL;
      setTab(true, buzzID);
    }
    };

  public static BuzzFragment newInstance(boolean hasBackNavigation) {
    Bundle bundle = new Bundle();
    BuzzFragment buzzFragment = new BuzzFragment();
    bundle.putBoolean(KEY_HAS_BACKNAVIGATION, hasBackNavigation);
    buzzFragment.setArguments(bundle);
    return buzzFragment;
  }

  public static BuzzFragment newInstance(boolean hasBackNavigation, String currentTab) {
    Bundle bundle = new Bundle();
    BuzzFragment buzzFragment = new BuzzFragment();
    bundle.putBoolean(KEY_HAS_BACKNAVIGATION, hasBackNavigation);
    bundle.putString(TAB_KEY, currentTab);
    buzzFragment.setArguments(bundle);
    return buzzFragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    LogUtils.d(TAG, "onCreate Started");
    if (getArguments() != null
        && getArguments().containsKey(KEY_HAS_BACKNAVIGATION)) {
      mHasBackNaviagtion = getArguments().getBoolean(
          KEY_HAS_BACKNAVIGATION);
    } else if (savedInstanceState != null
        && savedInstanceState.containsKey(KEY_HAS_BACKNAVIGATION)) {
      mHasBackNaviagtion = savedInstanceState
          .getBoolean(KEY_HAS_BACKNAVIGATION);
    }

    if (getArguments() != null
        && getArguments().containsKey(TAB_KEY)) {
      tabSelected = getArguments().getString(TAB_KEY);
    } else if (savedInstanceState != null
        && savedInstanceState.containsKey(TAB_KEY)) {
      tabSelected = getArguments().getString(TAB_KEY);
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    LogUtils.d(TAG, "onCreateView Started");
    // removeAllTab();
    View view = inflater.inflate(R.layout.fragment_buzz, container, false);
    initView(view);
    return view;
  }

  @Override
  public void onActivityCreated(Bundle bundle) {
    LogUtils.d(TAG, "onActivityCreated Started");
    super.onActivityCreated(bundle);
    mActionBar.syncActionBar(this);
    initTab();
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    LogUtils.d(TAG, "onSaveInstanceState Started");
    super.onSaveInstanceState(outState);
    outState.putBoolean(KEY_HAS_BACKNAVIGATION, mHasBackNaviagtion);
    outState.putString(TAB_KEY, mCurrentTab);
  }

  @Override
  public void onStart() {
    LogUtils.d(TAG, "onStart Started");
    super.onStart();
  }

  @Override
  public void onDestroyView() {
    LogUtils.d(TAG, "onDestroyView Started");
    Preferences.getInstance().saveBuzzTab(mCurrentTab);
    super.onDestroyView();
  }

  @Override
  public void onDestroy() {
    LogUtils.d(TAG, "onDestroy Started");
    Preferences.getInstance().saveBuzzTab(
        BuzzFragment.TAB_LOCAL);
    super.onDestroy();
  }

  private void initView(View view) {
    LogUtils.d(TAG, "initView Started");

    mBtnAll = (TextView) view.findViewById(R.id.btn_all);
    mBtnFavorites = (TextView) view.findViewById(R.id.btn_favorites);
    mBtnMine = (TextView) view.findViewById(R.id.btn_mine);

    mViewSelectAll = (View) view.findViewById(R.id.view_select_all);
    mViewSelectFavorites = (View) view
        .findViewById(R.id.view_select_favorites);
    mViewSelectMine = (View) view.findViewById(R.id.view_select_mine);

    mBtnAll.setOnClickListener(this);
    mBtnFavorites.setOnClickListener(this);
    mBtnMine.setOnClickListener(this);
    mSelectAll = view.findViewById(R.id.buzz_frame_all);
    mFavorites = view.findViewById(R.id.buzz_frame_favorite);
    mMine = view.findViewById(R.id.buzz_frame_mine);
  }

  @Override
  protected void resetNavigationBar() {
    LogUtils.d(TAG, "resetNavigationBar Started");
    super.resetNavigationBar();
    if (getNavigationBar() == null) {
      return;
    }
    if (mHasBackNaviagtion) {
      getNavigationBar().setNavigationLeftLogo(R.drawable.nav_btn_back);
    } else {
      getNavigationBar().setNavigationLeftLogo(R.drawable.nav_menu);
    }
    getNavigationBar().setCenterTitle(R.string.buzz);
    getNavigationBar().setNavigationRightLogo(R.drawable.nav_message);
    getNavigationBar().setShowUnreadMessage(true);
  }

  private void initTab() {
    String tabId;
    if (TextUtils.isEmpty(tabSelected)) {
      tabId = Preferences.getInstance().getBuzzTab();
    } else {
      tabId = tabSelected;
    }
    LogUtils.d(TAG, String.format("initTab: Tab = %s", tabId));
    // tabId = TAB_LOCAL;
    mCurrentTab = tabId;

    setTab(false, null);
  }

  @Override
  public void onNavigationLeftClick(View view) {
    super.onNavigationLeftClick(view);
  }

  @Override
  public void onNavigationRightClick(View view) {
    getSlidingMenu().showSecondaryMenu(true);
  }

  @Override
  public void clearAllFocusableFields() {
    FragmentManager fm = getChildFragmentManager();
    if (fm != null) {
      Fragment currentTab = fm.findFragmentByTag(mCurrentTab);
      if (currentTab != null
          && currentTab instanceof BaseBuzzListFragment) {
        ((BaseBuzzListFragment) currentTab).clearAllFocusableFields();
      }
    }
  }

  @Override
  public void onClick(View v) {
    FragmentManager fm = getChildFragmentManager();

    if (fm == null) {
      return;
    }

    if (v.getId() == R.id.btn_all) {
      Utility.hideSoftKeyboard(getActivity());
      mCurrentTab = TAB_LOCAL;
      // mViewSelectAll.setVisibility(View.VISIBLE);
      // mViewSelectFavorites.setVisibility(View.GONE);
      // mViewSelectMine.setVisibility(View.GONE);

      // showFragment(TAB_LOCAL);
      // BaseBuzzListFragment fragment = new LocalBuzzListFragment();
      // fragment.setPagingParams(LocalBuzzListFragment.PAGING_TAKE);
      // fragment.mListType = ListType.LOCAL;
      // fragment.setMakeBuzzListener(mMakeBuzzListener);
      //
      // fm.beginTransaction().add(R.id.buzz_frame, fragment).commit();

    } else if (v.getId() == R.id.btn_favorites) {
      Utility.hideSoftKeyboard(getActivity());
      mCurrentTab = TAB_FAVORITES;
      // mViewSelectAll.setVisibility(View.GONE);
      // mViewSelectFavorites.setVisibility(View.VISIBLE);
      // mViewSelectMine.setVisibility(View.GONE);
      // showFragment(TAB_FAVORITES);
      //
      // BaseBuzzListFragment fragment = new FavoritesBuzzListFragment();
      // fragment.setPagingParams(FavoritesBuzzListFragment.PAGING_TAKE);
      // fragment.mListType = ListType.FAVORITES;
      // fragment.setMakeBuzzListener(mMakeBuzzListener);
      //
      // fm.beginTransaction().add(R.id.buzz_frame, fragment).commit();

    } else if (v.getId() == R.id.btn_mine) {
      Utility.hideSoftKeyboard(getActivity());
      mCurrentTab = TAB_MINE;
      // mViewSelectAll.setVisibility(View.GONE);
      // mViewSelectFavorites.setVisibility(View.GONE);
      // mViewSelectMine.setVisibility(View.VISIBLE);
      // showFragment(TAB_MINE);
      // BaseBuzzListFragment fragment = new MineBuzzListFragment();
      // fragment.setPagingParams(MineBuzzListFragment.PAGING_TAKE);
      // fragment.mListType = ListType.USER;
      // fragment.setMakeBuzzListener(mMakeBuzzListener);
      //
      // fm.beginTransaction()
      // .replace(R.id.buzz_frame, fragment, mCurrentTab).commit();
    }
    initViewTab(mCurrentTab);
    showFragment(mCurrentTab);
  }

  private void setTab(boolean isRefresh, String newBuzzIDFromFavorite) {
    LogUtils.d("ToanTK", "settab");
    FragmentManager fm = getChildFragmentManager();
    if (fm != null) {
      Utility.hideSoftKeyboard(getActivity());
      BaseBuzzListFragment fragment = null;
      initViewTab(mCurrentTab);
      boolean isMakeNew = false;
      if (mCurrentTab.equals(TAB_FAVORITES)) {
        Fragment frg = fm.findFragmentById(R.id.buzz_frame_favorite);
        if (frg == null || isRefresh) {
          fragment = new FavoritesBuzzListFragment();
          fragment.setPagingParams(FavoritesBuzzListFragment.PAGING_TAKE);
          fragment.mListType = ListType.FAVORITES;
          isMakeNew = true;
        }
      } else if (mCurrentTab.equals(TAB_LOCAL)) {
        Fragment frg = fm.findFragmentById(R.id.buzz_frame_all);
        if (frg == null || isRefresh) {
          fragment = new LocalBuzzListFragment();
          fragment.setPagingParams(LocalBuzzListFragment.PAGING_TAKE);
          fragment.mListType = ListType.LOCAL;
          if (newBuzzIDFromFavorite != null) {
            fragment.setNewBuzzID(newBuzzIDFromFavorite);
            fragment.setOnAddBuzzFromFavorite(mOnAddBuzzFromFavorite);
          }
          isMakeNew = true;
        }
      } else if (mCurrentTab.equals(TAB_MINE)) {
        Fragment frg = fm.findFragmentById(R.id.buzz_frame_mine);
        if (frg == null || isRefresh) {
          fragment = new MineBuzzListFragment();
          fragment.setPagingParams(MineBuzzListFragment.PAGING_TAKE);
          fragment.mListType = ListType.USER;
          isMakeNew = true;
        }
      }
      if (fragment != null) {
        fragment.setMakeBuzzListener(mMakeBuzzListener);
        fragment.setOnActionNoRefresh(this);
        fragment.setOnActionRefresh(this);
      }
      if (isMakeNew) {
        if (fragment instanceof FavoritesBuzzListFragment) {
          fm.beginTransaction()
              .replace(R.id.buzz_frame_favorite, fragment,
                  mCurrentTab).commit();
        }
        if (fragment instanceof LocalBuzzListFragment) {
          fm.beginTransaction()
              .replace(R.id.buzz_frame_all, fragment, mCurrentTab)
              .commit();
        }
        if (fragment instanceof MineBuzzListFragment) {
          fm.beginTransaction()
              .replace(R.id.buzz_frame_mine, fragment,
                  mCurrentTab).commit();
        }
      }

    }
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

  private void showFragment(String currentTab) {
    LogUtils.d("ToanTK", "showFragment");
    FragmentManager fm = getChildFragmentManager();

    if (fm == null) {
      LogUtils.e("ToanTK", "fragment Manager null");
      return;
    }
    Fragment frg = null;

    if (currentTab.equals(TAB_LOCAL)) {
      frg = getChildFragmentManager().findFragmentById(R.id.buzz_frame_all);
      if (frg == null) {
        LogUtils.e("ToanTK", "tao moi All");
        BaseBuzzListFragment fragment = new LocalBuzzListFragment();
        fragment.setPagingParams(LocalBuzzListFragment.PAGING_TAKE);
        fragment.mListType = ListType.LOCAL;
        fragment.setMakeBuzzListener(mMakeBuzzListener);
        fragment.setOnActionNoRefresh(this);
        fragment.setOnActionRefresh(this);
        fm.beginTransaction()
            .replace(R.id.buzz_frame_all, fragment, currentTab)
            .commit();
      } else if (((BaseBuzzListFragment) frg).isOpenShareStatus()) {
        ((BaseBuzzListFragment) frg).closeShareMyStatusView();
      }
    } else if (currentTab.equals(TAB_FAVORITES)) {
      frg = getChildFragmentManager().findFragmentById(
          R.id.buzz_frame_favorite);
      // mSelectAll.setVisibility(View.GONE);
      // mMine.setVisibility(View.GONE);
      // mFavorites.setVisibility(View.VISIBLE);
      // Load New Tab Favorite
      if (frg == null) {
        LogUtils.e("ToanTK", "tao moi Favorite");
        BaseBuzzListFragment fragment = new FavoritesBuzzListFragment();
        fragment.setPagingParams(FavoritesBuzzListFragment.PAGING_TAKE);
        fragment.mListType = ListType.FAVORITES;
        fragment.setMakeBuzzListener(mMakeBuzzListener);
        fragment.setOnActionNoRefresh(this);
        fragment.setOnActionRefresh(this);
        fm.beginTransaction()
            .replace(R.id.buzz_frame_favorite, fragment, currentTab)
            .commit();
      } else if (((BaseBuzzListFragment) frg).isOpenShareStatus()) {
        ((BaseBuzzListFragment) frg).closeShareMyStatusView();
      }
    } else if (currentTab.equals(TAB_MINE)) {
      frg = getChildFragmentManager().findFragmentById(
          R.id.buzz_frame_mine);
      // mSelectAll.setVisibility(View.GONE);
      // mFavorites.setVisibility(View.GONE);
      // mMine.setVisibility(View.VISIBLE);
      // Load New Tab Mine
      if (frg == null) {
        LogUtils.e("ToanTK", "tao moi Mine");
        BaseBuzzListFragment fragment = new MineBuzzListFragment();
        fragment.setPagingParams(MineBuzzListFragment.PAGING_TAKE);
        fragment.mListType = ListType.USER;
        fragment.setMakeBuzzListener(mMakeBuzzListener);
        fragment.setOnActionNoRefresh(this);
        fragment.setOnActionRefresh(this);
        fm.beginTransaction()
            .replace(R.id.buzz_frame_mine, fragment, currentTab)
            .commit();
      } else if (((BaseBuzzListFragment) frg).isOpenShareStatus()) {
        ((BaseBuzzListFragment) frg).closeShareMyStatusView();
      }
    }

    if ((frg != null) && (frg instanceof BaseBuzzListFragment)) {
      ((BaseBuzzListFragment) frg).updateData();
    }
    // if (currentTab == TAB_FAVORITES) {
    //
    // } else if (currentTab == TAB_LOCAL) {
    //
    // } else if (currentTab == TAB_MINE) {
    //
    // }

  }

  private void initViewTab(String currentTab) {
    if (currentTab.equals(TAB_LOCAL)) {
//			mViewSelectAll.setVisibility(View.VISIBLE);
//			mViewSelectFavorites.setVisibility(View.GONE);
//			mViewSelectMine.setVisibility(View.GONE);
      mBtnAll.setTextColor(getResources().getColor(R.color.white));
      mBtnAll.setBackgroundResource(R.color.primary);
      mBtnFavorites.setTextColor(getResources().getColor(R.color.color_text_gray));
      mBtnFavorites.setBackgroundResource(R.color.white);
      mBtnMine.setTextColor(getResources().getColor(R.color.color_text_gray));
      mBtnMine.setBackgroundResource(R.color.white);

      mSelectAll.setVisibility(View.VISIBLE);
      mFavorites.setVisibility(View.GONE);
      mMine.setVisibility(View.GONE);
    } else if (currentTab.equals(TAB_FAVORITES)) {
//			mViewSelectAll.setVisibility(View.GONE);
//			mViewSelectFavorites.setVisibility(View.VISIBLE);
//			mViewSelectMine.setVisibility(View.GONE);
      mBtnFavorites.setTextColor(getResources().getColor(R.color.white));
      mBtnFavorites.setBackgroundResource(R.color.primary);
      mBtnAll.setTextColor(getResources().getColor(R.color.color_text_gray));
      mBtnAll.setBackgroundResource(R.color.white);
      mBtnMine.setTextColor(getResources().getColor(R.color.color_text_gray));
      mBtnMine.setBackgroundResource(R.color.white);

      mSelectAll.setVisibility(View.GONE);
      mFavorites.setVisibility(View.VISIBLE);
      mMine.setVisibility(View.GONE);
    } else if (currentTab.equals(TAB_MINE)) {
//			mViewSelectAll.setVisibility(View.GONE);
//			mViewSelectFavorites.setVisibility(View.GONE);
//			mViewSelectMine.setVisibility(View.VISIBLE);
      mBtnMine.setTextColor(getResources().getColor(R.color.white));
      mBtnMine.setBackgroundResource(R.color.primary);
      mBtnFavorites.setTextColor(getResources().getColor(R.color.color_text_gray));
      mBtnFavorites.setBackgroundResource(R.color.white);
      mBtnAll.setTextColor(getResources().getColor(R.color.color_text_gray));
      mBtnAll.setBackgroundResource(R.color.white);

      mSelectAll.setVisibility(View.GONE);
      mFavorites.setVisibility(View.GONE);
      mMine.setVisibility(View.VISIBLE);
    }
  }

  @Override
  public void onDeleteBuzzSuccess(BuzzListItem item) {
    LogUtils.d("ToanTK", "Vao DeleteBuzz");
    if (item != null) {
      for (int fragmentID : fragmentIDs) {
        BaseBuzzListFragment frg = (BaseBuzzListFragment) (getChildFragmentManager()
            .findFragmentById(fragmentID));
        if (frg != null) {
          frg.removeItem(item);
        }
      }
    }
  }

  @Override
  public void onDeleteCommentSuccess(BuzzListItem item, String commentID) {
    if (item != null) {
      for (int fragmentID : fragmentIDs) {
        BaseBuzzListFragment frg = (BaseBuzzListFragment) (getChildFragmentManager()
            .findFragmentById(fragmentID));
        if (frg != null) {
          frg.removeComment(item, commentID);
        }
      }
    }
  }

  @Override
  public void onLikeSuccess(BuzzListItem item, int newLikeType,
      int newLikeNumber) {
    if (item != null) {
      for (int fragmentID : fragmentIDs) {
        BaseBuzzListFragment frg = (BaseBuzzListFragment) (getChildFragmentManager()
            .findFragmentById(fragmentID));
        if (frg != null) {
          frg.likeBuzz(item, newLikeType, newLikeNumber);
        }
      }
    }
  }

  @Override
  public void onAddBuzzSuccess(BuzzListItem item) {
    if (item != null) {
      for (int fragmentID : fragmentIDs) {
        if (fragmentID == R.id.buzz_frame_all
            || fragmentID == R.id.buzz_frame_mine) {
          BaseBuzzListFragment frg = (BaseBuzzListFragment) (getChildFragmentManager()
              .findFragmentById(fragmentID));
          if (frg != null) {
            frg.addBuzz(item);
          }
        }
      }
    }
  }

  @Override
  public void onAddCommentSuccess(BuzzListItem item) {
    if (item != null) {
      for (int fragmentID : fragmentIDs) {
        BaseBuzzListFragment frg = (BaseBuzzListFragment) (getChildFragmentManager()
            .findFragmentById(fragmentID));
        if (frg != null) {
          frg.addComment(item);
        }
      }
    }
  }

  @Override
  public void onRefresh() {
    for (int fragmentID : fragmentIDs) {
      BaseBuzzListFragment frg = (BaseBuzzListFragment) (getChildFragmentManager()
          .findFragmentById(fragmentID));
      if (frg != null) {
        frg.setRefresh();
      }
    }
  }

  public void setFocusEditStatus() {
    Fragment fragment = getChildFragmentManager().findFragmentById(
        R.id.buzz_frame_all);
    if (fragment != null && fragment instanceof BaseBuzzListFragment) {
      ((BaseBuzzListFragment) fragment).openShareMyStatusView(Constants.BUZZ_TYPE_STATUS);

    }
  }
}