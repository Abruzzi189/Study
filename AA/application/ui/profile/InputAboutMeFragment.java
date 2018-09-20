package com.application.ui.profile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import com.application.service.DataFetcherService;
import com.application.ui.BaseFragment;
import com.application.util.Utility;
import com.application.util.preferece.UserPreferences;
import glas.bbsystem.R;


public class InputAboutMeFragment extends BaseFragment {

  public static final String EXTRA_ABOUT_ME = "extra_about_me";
  private EditText mEditText;

  public static InputAboutMeFragment newInstance(String about) {
    InputAboutMeFragment aboutMeFragment = new InputAboutMeFragment();
    Bundle bundle = new Bundle();
    bundle.putString(EXTRA_ABOUT_ME, about);
    aboutMeFragment.setArguments(bundle);
    return aboutMeFragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public void onResume() {
    super.onResume();
    if (mEditText.isFocused()) {
      mEditText.clearFocus();
    }
    mEditText.requestFocus();
    InputMethodManager keyboard = (InputMethodManager) mAppContext
        .getSystemService(Context.INPUT_METHOD_SERVICE);
    keyboard.showSoftInput(mEditText, InputMethodManager.SHOW_FORCED);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    requestDirtyWord();
    View view = inflater.inflate(R.layout.fragment_input_aboutme,
        container, false);
    mEditText = (EditText) view.findViewById(R.id.edtAbout);
    if (getArguments() != null
        && getArguments().containsKey(EXTRA_ABOUT_ME)) {
      String about = getArguments().getString(EXTRA_ABOUT_ME);
      if (!TextUtils.isEmpty(about)) {
        mEditText.setText(about);
      }
    }
    return view;
  }

  /**
   * Notify data service to load list dirty word
   */
  private void requestDirtyWord() {
    Activity activity = getActivity();
    if (activity != null) {
      String token = UserPreferences.getInstance().getToken();
      DataFetcherService.startCheckSticker(activity, token);
    }
  }

  @Override
  protected void resetNavigationBar() {
    super.resetNavigationBar();
    getNavigationBar().setNavigationLeftLogo(R.drawable.nav_btn_back);
    getNavigationBar().setCenterTitle(R.string.profile_title_the_about_me);
  }

  @Override
  public void onNavigationLeftClick(View view) {
    super.onNavigationLeftClick(view);
    Utility.hideSoftKeyboard(getActivity());
    if (!Utility.isContainDirtyWord(getActivity(), mEditText)) {
      Intent data = new Intent();
      data.putExtra(EXTRA_ABOUT_ME, mEditText.getText().toString());
      getTargetFragment().onActivityResult(getTargetRequestCode(),
          Activity.RESULT_OK, data);
    }
  }
}
