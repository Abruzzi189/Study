package com.application.actionbar;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import com.application.navigationmanager.NavigationManager;

public interface CustomActionBar {

  public void backButtonClicked(Activity activity);

  public abstract void initialize(NavigationManager navigationmanager,
      AppCompatActivity activity);

  public void setBackButtonClickListener(View.OnClickListener onClickListener);

  public void resetBackButtonClickListener();

  public void setTextCenterTitle(String title);

  public void setTextCenterTitle(int resId);

  public void syncActionBar(Fragment activePage);

  public void syncActionBar();

  public void syncShakeToChat(Fragment activePage);

  public void setTextRightTitle(String title);

  public void disableActionBarWhenShakeToChat();

  public void enableActionBarAfterShakeToChat();

  public void displayTimeRemainingHiddenChat(int timeRemaining);

  public void stopHiddenChat();

  public void switchToHiddenChat();

  public void resetWhenNotHiddenChat();

  public int getHeight();

  public void setVisibility(int visible);

  public void startAnimation(Animation animation);

  public View getView();

  public void displayEditButton(boolean isEditing);

  public void disableEditRightButton();

  public void enableEditRightButton();

  public void setAllEnable(boolean enable);

  public void show();

  public void hide();

  public void setProfileVisibility(int visibility);

  public void setRightVisibility(int visibility);

}
