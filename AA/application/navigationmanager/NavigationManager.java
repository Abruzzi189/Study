package com.application.navigationmanager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v4.app.FragmentTransaction;
import com.application.ui.CustomActionBarActivity;
import com.ntq.utils.MainThreadStack;
import glas.bbsystem.R;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;


public class NavigationManager {

  private final Stack mBackStack = new MainThreadStack();
  private CustomActionBarActivity mActivity;
  private FragmentManager mFragmentManager;
  private List<OnNavigationTabChangeListener> mTabChangeListener = new ArrayList<OnNavigationTabChangeListener>();
  private List<OnChangeFragmentListener> mFragmentChangeListeners = new ArrayList<OnChangeFragmentListener>();
  private int mPlaceholder;
  private Fragment mRootPage;

  public NavigationManager(CustomActionBarActivity mainactivity) {
    init(mainactivity);
  }

  /**
   * First only used for Send Gift (ChooseGiftToSend) to jump to the chat screen
   * @param bundle
   * @return
   * Created by Robert on 2018 July 18
   */
  public boolean goBack(Bundle bundle) {
    if (mActivity == null || mActivity.isStateSaved()
        || mFragmentManager.getBackStackEntryCount() == 0) {
      return false;
    }
    NavigationState state = (NavigationState) mBackStack.peek();
    if (state.placeholder != mPlaceholder) {
      return true;
    }
    mBackStack.pop();
    mFragmentManager.popBackStack();
    if (!mBackStack.isEmpty()) {
      NavigationState currentState = (NavigationState) mBackStack.peek();
      Fragment currentFragment = mFragmentManager.findFragmentByTag(currentState.backStackName);
      currentFragment.setArguments(bundle);
      notifyFragmentChange(currentFragment);
    } else {
      notifyFragmentChange(mRootPage);
    }
    return true;
  }

  /**
   * find root parent of fragment
   *
   * @param fragment child fragment
   * @return root parent of fragment
   */
  public static Fragment getRootParentFragment(Fragment fragment) {
    Fragment parent = fragment.getParentFragment();
    if (parent == null) {
      return fragment;
    } else {
      return getRootParentFragment(parent);
    }
  }

  public void addFragmentChangeListener(
      OnChangeFragmentListener fragmentChangeListener) {
    mFragmentChangeListeners.add(fragmentChangeListener);
  }

  public void removeFragmentChangeListener(
      OnChangeFragmentListener fragmentChangeListener) {
    mFragmentChangeListeners.remove(fragmentChangeListener);
  }

  public void notifyFragmentChange(Fragment fragment) {
    for (OnChangeFragmentListener fragmentChangeListener : mFragmentChangeListeners) {
      fragmentChangeListener.onFragmentChanged(fragment);
    }
  }

  public void addTabChangeListener(
      OnNavigationTabChangeListener tabChangeListener) {
    this.mTabChangeListener.add(tabChangeListener);
  }

  public void removeTabChangeListener(
      OnNavigationTabChangeListener tabChangeListener) {
    this.mTabChangeListener.remove(tabChangeListener);
  }

  public void notifyTabChange(int placeholder) {
    for (OnNavigationTabChangeListener tabChangeListener : mTabChangeListener) {
      tabChangeListener.onNavigationTabChange(placeholder);
    }
  }

  public void init(CustomActionBarActivity mainActivity) {
    mActivity = mainActivity;
    mFragmentManager = mActivity.getCustomFragmentManager();
    mPlaceholder = mActivity.getPlaceHolder();
  }

  public void addOnBackStackChangedListener(
      OnBackStackChangedListener backStackChangedListener) {
    mFragmentManager
        .addOnBackStackChangedListener(backStackChangedListener);
  }

  public void removeOnBackStackChangedListener(
      OnBackStackChangedListener onbackstackchangedlistener) {
    mFragmentManager
        .removeOnBackStackChangedListener(onbackstackchangedlistener);
  }

  protected boolean canNavigate() {
    return !(mActivity == null || mActivity.isStateSaved());
  }

  public boolean goBack() {
    if (mActivity == null || mActivity.isStateSaved()
        || mFragmentManager.getBackStackEntryCount() == 0) {
      return false;
    }
    NavigationState state = (NavigationState) mBackStack.peek();
    if (state.placeholder != mPlaceholder) {
      return true;
    }
    mBackStack.pop();
    mFragmentManager.popBackStack();
    if (!mBackStack.isEmpty()) {
      NavigationState currentState = (NavigationState) mBackStack.peek();
      Fragment currentFragment = mFragmentManager
          .findFragmentByTag(currentState.backStackName);
      notifyFragmentChange(currentFragment);
    } else {
      notifyFragmentChange(mRootPage);
    }
    return true;
  }

  public boolean goBackSteps(int step) {
    if (mActivity == null || mActivity.isStateSaved()
        || mBackStack.isEmpty()) {
      return false;
    }
    for (int i = 0; i < step; i++) {
      mBackStack.pop();
    }
    mFragmentManager.popBackStack(
        mFragmentManager.getBackStackEntryAt(mFragmentManager.getBackStackEntryCount() - step)
            .getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
    if (!mBackStack.isEmpty()) {
      NavigationState currentState = (NavigationState) mBackStack.peek();
      Fragment currentFragment = mFragmentManager
          .findFragmentByTag(currentState.backStackName);
      notifyFragmentChange(currentFragment);
    } else {
      notifyFragmentChange(mRootPage);
    }
    return true;
  }

  public void setPlaceHolder(int placeHolder) {
    mPlaceholder = placeHolder;
  }

  public void addPage(Fragment fragment) {
    addPage(fragment, true);
  }

  public void addPageStateLoss(Fragment fragment) {
    addPageStateLoss(fragment, true);
  }

  /**
   * Navigate to new fragment and add old fragment to stack
   */
  public void addPage(Fragment fragment, boolean hasAnimation) {
    if (!canNavigate()) {
      return;
    }
    FragmentTransaction transaction = mFragmentManager.beginTransaction();
    if (hasAnimation) {
      transaction.setCustomAnimations(R.anim.fragment_enter,
          R.anim.fragment_exit, R.anim.fragment_pop_enter,
          R.anim.fragment_pop_exit);
    }
    NavigationState navigationState = new NavigationState(mPlaceholder);
    transaction.replace(mPlaceholder, fragment,
        navigationState.backStackName);
    transaction.addToBackStack(fragment.getClass().getCanonicalName());
    transaction.commit();
    mBackStack.push(navigationState);
    notifyFragmentChange(fragment);
  }

  /**
   * Navigate to new fragment and add old fragment to stack
   */
  public void addPageTemplate(Fragment fragment, boolean hasAnimation) {
    if (!canNavigate()) {
      return;
    }
    FragmentTransaction transaction = mFragmentManager.beginTransaction();
    if (hasAnimation) {
      transaction.setCustomAnimations(R.anim.fragment_enter,
          R.anim.fragment_exit, R.anim.fragment_pop_enter,
          R.anim.fragment_pop_exit);
    }
    NavigationState navigationState = new NavigationState(mPlaceholder);
    transaction.add(mPlaceholder, fragment,
        navigationState.backStackName);
    transaction.addToBackStack(null);
    transaction.commit();
    mBackStack.push(navigationState);
    notifyFragmentChange(fragment);
  }

  public void addPageStateLoss(Fragment fragment, boolean hasAnimation) {
    if (!canNavigate()) {
      return;
    }
    FragmentTransaction transaction = mFragmentManager.beginTransaction();
    if (hasAnimation) {
      transaction.setCustomAnimations(R.anim.fragment_enter,
          R.anim.fragment_exit, R.anim.fragment_pop_enter,
          R.anim.fragment_pop_exit);
    }
    NavigationState navigationState = new NavigationState(mPlaceholder);
    transaction.replace(mPlaceholder, fragment,
        navigationState.backStackName);
    transaction.addToBackStack(null);
    transaction.commitAllowingStateLoss();
    mBackStack.push(navigationState);
    notifyFragmentChange(fragment);
  }

  /**
   * Navigate to new fragment, clear all old fragment to stack
   */
  public void switchPage(Fragment fragment) {
    switchPage(fragment, false);
  }

  public void switchPage(Fragment fragment, boolean isForceSwitchPage) {
    mRootPage = fragment;
    Fragment currentFragment = mFragmentManager
        .findFragmentById(mPlaceholder);
    if (!isForceSwitchPage) {
      if (currentFragment != null
          && fragment.getClass() == currentFragment.getClass()) {
        return;
      }
    }
    if (!canNavigate()) {
      return;
    }

    int backStackCount = mFragmentManager.getBackStackEntryCount();
    for (int i = 0; i < backStackCount; i++) {

      // Get the back stack fragment id.
      int backStackId = mFragmentManager.getBackStackEntryAt(i).getId();

      mFragmentManager.popBackStack(backStackId, FragmentManager.POP_BACK_STACK_INCLUSIVE);

    } /* end of for */

    mBackStack.clear();

    // add new fragment to stack
    FragmentTransaction transaction = mFragmentManager.beginTransaction();
    transaction.replace(mPlaceholder, fragment);
    transaction.commit();

    // notify listener
    notifyTabChange(mPlaceholder);
    notifyFragmentChange(fragment);
  }

  /**
   * Navigate to new fragment, don't add old fragment to stack
   */
  public void swapPage(Fragment fragment, boolean isForceSwitchPage) {
    if (!canNavigate()) {
      return;
    }
    Fragment currentFragment = mFragmentManager
        .findFragmentById(mPlaceholder);
    if (!isForceSwitchPage) {
      if (currentFragment != null
          && fragment.getClass() == currentFragment.getClass()) {
        return;
      }
    }
    // Set animation
    FragmentTransaction transaction = mFragmentManager.beginTransaction();
    transaction.setCustomAnimations(0, 0, 0, 0);

    // Remove old fragment in stack
    int numberFragments = mFragmentManager.getBackStackEntryCount();
    if (numberFragments > 0) {
      mFragmentManager.popBackStack();
      mBackStack.pop();
    }

    // Change fragment
    NavigationState navigationState = new NavigationState(mPlaceholder);
    transaction.replace(mPlaceholder, fragment,
        navigationState.backStackName);
    transaction.addToBackStack(null);
    mBackStack.push(navigationState);
    transaction.commit();
    notifyFragmentChange(fragment);
  }

  public void terminate() {
    mActivity = null;
  }

  public boolean isBackStackEmpty() {
    return mBackStack.isEmpty();
  }

  public Fragment getActivePage() {
    if (mBackStack.isEmpty()) {
      return mRootPage;
    }
    return mFragmentManager.findFragmentById(mPlaceholder);
  }

  public void serialize(Bundle bundle) {
    if (mBackStack != null && !mBackStack.isEmpty()) {
      bundle.putParcelableArrayList("nm_state", new ArrayList(mBackStack));
    }
  }

  public void deserialize(Bundle bundle) {
    ArrayList arraylist = bundle.getParcelableArrayList("nm_state");
    if (arraylist != null && arraylist.size() != 0) {
      for (Object navigationState : arraylist) {
        mBackStack.push(navigationState);
      }
    }
  }

  public CustomActionBarActivity getActivity() {
    return mActivity;
  }

  public interface OnChangeFragmentListener {

    public void onFragmentChanged(Fragment fragment);
  }

  public interface OnNavigationTabChangeListener {

    public void onNavigationTabChange(int placeholder);
  }
}
