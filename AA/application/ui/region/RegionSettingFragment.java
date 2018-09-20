package com.application.ui.region;

import static com.application.navigationmanager.NavigationManager.getRootParentFragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import com.application.ui.BaseFragment;
import com.application.ui.CenterButtonDialogBuilder;
import com.application.ui.region.RegionRequester.RegionListener;
import com.application.util.LocationUtils.Region;
import com.application.util.LogUtils;
import com.application.util.RegionUtils;
import com.application.util.preferece.UserPreferences;
import glas.bbsystem.R;


public class RegionSettingFragment extends BaseFragment implements
    OnClickListener, RegionListener {

  public static final String EXTRA_REGION_SELECTED = "region_selected";
  public static final String EXTRA_AUTO_REGION_SELECTED = "auto_region_selected";
  public static final int REQUEST_CODE_CHOOSE_REGION = 0;
  private static final String KEY_REGION = "region";
  private static final String KEY_AUTO_REGION = "auto_region";
  private int mRegion;
  private boolean mIsAutoSet;

  private TextView mTxtIsAuto;
  private TextView mTxtRegion;
  private OnRegionSelected mOnRegionSelected;
  private AlertDialog dialog;
  private RegionRequester mRegionRequester;

  public static RegionSettingFragment newInstance(int regionSelected,
      boolean isAuto) {
    RegionSettingFragment regionSettingFragment = new RegionSettingFragment();
    Bundle bundle = new Bundle();
    bundle.putInt(KEY_REGION, regionSelected);
    bundle.putBoolean(KEY_AUTO_REGION, isAuto);
    regionSettingFragment.setArguments(bundle);
    return regionSettingFragment;
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    if (activity instanceof OnRegionSelected) {
      mOnRegionSelected = (OnRegionSelected) activity;
    }
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    if (dialog != null && dialog.isShowing()) {
      dialog.dismiss();
    }
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    mRegionRequester.onDestroy();
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mRegionRequester = new RegionRequester(getActivity(), this);
    mRegionRequester.onCreate();
    if (savedInstanceState != null) {
      setRegion(savedInstanceState.getInt(KEY_REGION));
      setAutoSet(savedInstanceState.getBoolean(KEY_AUTO_REGION));
    } else {
      Bundle bundle = getArguments();
      setRegion(bundle.getInt(KEY_REGION));
      setAutoSet(bundle.getBoolean(KEY_AUTO_REGION, mIsAutoSet));
    }
  }

  @Override
  public void onStart() {
    super.onStart();
  }

  @Override
  public void onStop() {
    super.onStop();
    mRegionRequester.onStop();
  }

  @Override
  public void onPause() {
    super.onPause();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    int resourceLayout = R.layout.fragment_region_setting;
    View view = inflater.inflate(resourceLayout, container, false);
    initView(view);
    return view;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    mNavigationManager
        .addOnBackStackChangedListener(new OnBackStackChangedListener() {
          @Override
          public void onBackStackChanged() {
            if (mNavigationManager.getActivePage() instanceof RegionSettingFragment) {
              initActionBarListener();
            }
          }
        });
    initActionBarListener();
    showRegion();

    if (mIsAutoSet) {
      setAutoOn();
      mRegionRequester.requestLocation();
    }
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == REQUEST_CODE_CHOOSE_REGION
        && resultCode == Activity.RESULT_OK) {
      mRegion = data.getIntExtra(
          ChooseRegionFragment.EXTRA_REGION_SELECTED, -1);
      mIsAutoSet = data.getBooleanExtra(
          ChooseRegionFragment.EXTRA_IS_AUTO, mIsAutoSet);
      showRegion();

      UserPreferences.getInstance().saveAutoDetectRegion(mIsAutoSet);
      LogUtils.d("TamNV", "mIsAutoSet --- " + mIsAutoSet);
      if (mIsAutoSet) {
        setAutoOn();
      } else {
        setAutoOff();
      }
    }
  }

  private void initActionBarListener() {
    mActionBar.setBackButtonClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        // pass data to interface
        if (mOnRegionSelected != null) {
          mOnRegionSelected.onRegionSelected(mRegion, mIsAutoSet);
        }
        // pass data to fragment
        Fragment fragment = getTargetFragment();
        if (fragment != null) {
          Intent intent = new Intent();
          intent.putExtra(EXTRA_REGION_SELECTED, mRegion);
          intent.putExtra(EXTRA_AUTO_REGION_SELECTED, mIsAutoSet);
          fragment.onActivityResult(getTargetRequestCode(),
              Activity.RESULT_OK, intent);
        }
        if (!mNavigationManager.goBack()) {
          if (mOnRegionSelected != null) {
            mOnRegionSelected.onRegionSelectedFinish();
          }
        }
      }
    });
  }

  public int getRegion() {
    return this.mRegion;
  }

  public void setRegion(int region) {
    this.mRegion = region;
  }

  public boolean isAutoSet() {
    return this.mIsAutoSet;
  }

  public void setAutoSet(boolean isAuto) {
    this.mIsAutoSet = isAuto;
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putInt(KEY_REGION, mRegion);
    outState.putBoolean(KEY_AUTO_REGION, mIsAutoSet);
  }

  private void initView(View view) {
    mTxtIsAuto = (TextView) view.findViewById(R.id.choose_region_auto);
    mTxtRegion = (TextView) view.findViewById(R.id.choose_region_show_more);
    mTxtIsAuto.setOnClickListener(this);
    mTxtRegion.setOnClickListener(this);
    if (mIsAutoSet) {
      if (mRegion == Region.REGION_NOT_SET) {
        setAutoOn();
      } else {
        mTxtIsAuto.setText(R.string.fg_region_setting_btn_is_auto_on);
        mTxtIsAuto.setSelected(mIsAutoSet);
      }
    } else {
      setAutoOff();
    }
    showRegion();
  }

  private void showRegion() {
    if (mRegion == Region.REGION_NOT_SET) {
      mTxtRegion
          .setText(R.string.fg_region_setting_btn_choose_region_manual);
    } else {
      RegionUtils regionUtils = new RegionUtils(getActivity());
      mTxtRegion.setText(regionUtils.getRegionName(mRegion));
    }
  }

  private void showChooseRegion() {
    ChooseRegionFragment chooseRegionFragment = ChooseRegionFragment
        .newInstance(mRegion, mIsAutoSet);
    chooseRegionFragment.setTargetFragment(getRootParentFragment(this), REQUEST_CODE_CHOOSE_REGION);
    mNavigationManager.addPage(chooseRegionFragment);
  }

  private void onAutoButtonClick() {
    if (!mTxtIsAuto.isSelected()) {
      setAutoOn();
      mRegionRequester.requestLocation();
    } else {
      setAutoOff();
    }
  }

  private void setAutoOn() {
    mIsAutoSet = true;
    mTxtIsAuto.setText(R.string.fg_region_setting_btn_is_auto_on);
    mTxtIsAuto.setTextColor(getResources().getColor(R.color.color_white));
    mTxtIsAuto.setSelected(mIsAutoSet);
  }

  private void setAutoOff() {
    mIsAutoSet = false;
    mTxtIsAuto.setText(R.string.fg_region_setting_btn_is_auto_off);
    mTxtIsAuto.setTextColor(getResources().getColor(R.color.color_text_default));
    mTxtIsAuto.setSelected(mIsAutoSet);
  }

  private void showAutoRegionError() {
    showRegion();
    LayoutInflater inflater = LayoutInflater.from(getActivity());
    View customTitle = inflater.inflate(R.layout.dialog_customize, null);

    Builder builder = new CenterButtonDialogBuilder(getActivity(), false);
    ((TextView) customTitle.findViewById(R.id.tv_title_dialog_customize))
        .setText(R.string.dg_region_auto_error_title);
    builder.setCustomTitle(customTitle);

    //builder.setTitle(R.string.dg_region_auto_error_title);
    builder.setMessage(R.string.dg_region_auto_error_content);
    builder.setPositiveButton(R.string.common_ok, null);
    builder.setCancelable(false);
    dialog = builder.create();
    dialog.show();
    int dividerId = dialog.getContext().getResources()
        .getIdentifier("android:id/titleDivider", null, null);
    View divider = dialog.findViewById(dividerId);
    if (divider != null) {
      divider.setBackgroundColor(dialog.getContext().getResources().getColor(R.color.transparent));
    }

    setAutoOff();
  }

  @Override
  public void onClick(View v) {
    int id = v.getId();
    switch (id) {
      case R.id.choose_region_auto:
        onAutoButtonClick();
        break;
      case R.id.choose_region_show_more:
        showChooseRegion();
        break;
      default:
    }
  }

  @Override
  public void onGetRegionSuccess(String regionName, int regionCode) {
    mRegion = regionCode;
    mTxtRegion.setText(regionName);
  }

  @Override
  public void onGetRegionFailed() {
    showAutoRegionError();
  }

  public void onSave() {
    UserPreferences.getInstance().saveAutoDetectRegion(mIsAutoSet);

    Fragment fragment = getTargetFragment();
    if (fragment != null) {
      Intent intent = new Intent();
      intent.putExtra(EXTRA_REGION_SELECTED, mRegion);
      intent.putExtra(EXTRA_AUTO_REGION_SELECTED, mIsAutoSet);
      fragment.onActivityResult(getTargetRequestCode(),
          Activity.RESULT_OK, intent);
    }
    mNavigationManager.goBack();
  }

  public interface OnRegionSelected {

    public void onRegionSelected(int gender, boolean isAuto);

    public void onRegionSelectedFinish();
  }
}