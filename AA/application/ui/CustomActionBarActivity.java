package com.application.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import com.application.actionbar.CustomActionBar;
import com.application.navigationmanager.NavigationManager;

public interface CustomActionBarActivity {

  public void initNavigationManager(Bundle savedInstanceState);

  public void initCustomActionBar();

  public NavigationManager getNavigationManager();

  public CustomActionBar getCustomActionBar();

  public boolean isStateSaved();

  public FragmentManager getCustomFragmentManager();

  public int getPlaceHolder();

  public void showActionBar();

  public void hideActionBar();
}
