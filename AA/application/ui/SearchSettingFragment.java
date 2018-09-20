package com.application.ui;

import static com.application.navigationmanager.NavigationManager.getRootParentFragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.application.connection.response.MeetPeopleSettingResponse;
import com.application.constant.Constants;
import com.application.ui.customeview.ErrorApiDialog;
import com.application.ui.settings.MeetPeopleSetting;
import com.application.util.RegionUtils;
import com.michaelnovakjr.numberpicker.AgeBetweenPickerDialog;
import glas.bbsystem.R;

/**
 * Setting screen for meet another people
 *
 * @author Nin_in_the_winD
 */
public class SearchSettingFragment extends BaseFragment implements
    OnClickListener {

  public static final String MEET_PEOPLE_SETTING = "meet_people_setting";
  private static final int REQUEST_REGION = 101;
  private View mRootView;
  private AgeBetweenPickerDialog mDialogAgePicker;
  private android.app.AlertDialog mDialogSelectSortType;
  private android.app.AlertDialog.Builder builder;
  private TextView txtAgeRange;
  private TextView txtSortType;
  private CheckBox cbxFilterNewRegister;
  private CheckBox cbxFilterNewLogin;
  private CheckBox cbxFilterCall;
  private TextView mtxtRegionOption;
  private RelativeLayout mRlSearchByName;
  private String[] sortTypes;
  private MeetPeopleSettingResponse settingResponse;
  private DialogInterface.OnClickListener mDialogListener = new DialogInterface.OnClickListener() {

    @Override
    public void onClick(DialogInterface dialog, int item) {
      switch (item) {
        case 0:
          // sort via time login
          txtSortType.setText(sortTypes[0]);
          settingResponse
              .setSort_type(MeetPeopleSetting.SORT_LOGIN_TIME);
          break;
        case 1:
          // sort via time sign up
          txtSortType.setText(sortTypes[1]);
          settingResponse
              .setSort_type(MeetPeopleSetting.SORT_REGISTER_TIME);
          break;
        default:
          break;
      }
      mDialogSelectSortType.dismiss();
    }
  };

  public static SearchSettingFragment newInstance(
      MeetPeopleSettingResponse settingResponse) {
    SearchSettingFragment fragment = new SearchSettingFragment();
    Bundle bundle = new Bundle();
    bundle.putSerializable(MEET_PEOPLE_SETTING, settingResponse);
    fragment.setArguments(bundle);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Bundle bundle = getArguments();
    if (bundle != null) {
      settingResponse = (MeetPeopleSettingResponse) bundle
          .getSerializable(MEET_PEOPLE_SETTING);
    } else if (savedInstanceState != null) {
      settingResponse = (MeetPeopleSettingResponse) savedInstanceState
          .getSerializable(MEET_PEOPLE_SETTING);
    }
    sortTypes = new String[]{
        getResources().getString(
            R.string.search_setting_sort_time_login),
        getResources().getString(
            R.string.search_setting_sort_time_signup)};
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    mRootView = inflater.inflate(R.layout.fragment_search_setting,
        container, false);
    initView(mRootView);
    fillDataSearchSettingFromServer();
    registerListener();
    return mRootView;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putSerializable(MEET_PEOPLE_SETTING, settingResponse);
  }

  @Override
  public void onStart() {
    super.onStart();
    int[] regions = settingResponse.getRegion();
    if (regions == null || regions.length < 1) {
      settingResponse.setDistance(MeetPeopleSetting.WORLD_VALUE);
      fillRegion();
    }
  }

  private void initView(View view) {
    txtAgeRange = (TextView) view.findViewById(R.id.range_age);
    txtSortType = (TextView) view.findViewById(R.id.sort_type_txt);

    cbxFilterNewRegister = (CheckBox) view
        .findViewById(R.id.checkbox_filter_new_register);
    cbxFilterCall = (CheckBox) view.findViewById(R.id.checkbox_filter_call);
    cbxFilterNewLogin = (CheckBox) view
        .findViewById(R.id.checkbox_filter_new_login);
    mtxtRegionOption = (TextView) view.findViewById(R.id.state_txt);
    mRlSearchByName = (RelativeLayout) view.findViewById(R.id.search_by_name_row);
  }

  private void fillDataSearchSettingFromServer() {
    fillAgeRange();
    fillRegion();
    fillFilter();
    fillSortType();
  }

  private void registerListener() {
    mRootView.findViewById(R.id.search_age).setOnClickListener(this);
    mRootView.findViewById(R.id.search_state).setOnClickListener(this);
    mRootView.findViewById(R.id.sort_txt).setOnClickListener(this);
    mRootView.findViewById(R.id.search_btn).setOnClickListener(this);
    cbxFilterNewRegister.setOnClickListener(this);
    cbxFilterCall.setOnClickListener(this);
    cbxFilterNewLogin.setOnClickListener(this);
    mtxtRegionOption.setOnClickListener(this);
    mRlSearchByName.setOnClickListener(this);
  }

  /**
   * Create a dialog to set minimum and maximum age
   */
  private void showAgeDialogPicker() {
    if (mDialogAgePicker == null) {
      mDialogAgePicker = new AgeBetweenPickerDialog(getActivity(),
          R.string.dialog_age_min_and_max_title,
          new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              int min = mDialogAgePicker.getMinAge();
              int max = mDialogAgePicker.getMaxAge();
              if (min > max) {
                ErrorApiDialog
                    .showAlert(
                        getActivity(),
                        getResources()
                            .getString(
                                R.string.title_error_user_age),
                        getResources()
                            .getString(
                                R.string.error_age_min_bigger_max));
              } else {
                settingResponse.setLower_age(min);
                settingResponse.setUpper_age(max);
                txtAgeRange.setText(getTextRangeAge(min, max));
              }
            }
          });

      mDialogAgePicker.setRangeOne(
          Constants.SEARCH_SETTING_AGE_MIN_LIMIT,
          Constants.SEARCH_SETTING_AGE_MAX_LIMIT);
      mDialogAgePicker.setRangeTwo(
          Constants.SEARCH_SETTING_AGE_MIN_LIMIT,
          Constants.SEARCH_SETTING_AGE_MAX_LIMIT);
      mDialogAgePicker.getWindow().setLayout(LayoutParams.MATCH_PARENT,
          LayoutParams.MATCH_PARENT);
      mDialogAgePicker.setInverseBackgroundForced(true);
      int flag = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
      mDialogAgePicker.getWindow().setFlags(flag, flag);
    } /*
     * else { if (!mDialogAgePicker.isShowing()) {
     * mDialogAgePicker.setInitialValue(this.mDataAgeMin, this.mDataAgeMax);
     * mDialogAgePicker.show(); } }
     */
    if (!mDialogAgePicker.isShowing()) {
      mDialogAgePicker.setInitialValue(settingResponse.getLowerAge(),
          settingResponse.getUpperAge());
      mDialogAgePicker.show();
    }
  }

  private void showDialogSort() {

    int selectedSortType = settingResponse.getSortType();
    int selectedChoice = -1;
    if (selectedSortType == MeetPeopleSetting.SORT_LOGIN_TIME) {
      selectedChoice = 0;
    }
    if (selectedSortType == MeetPeopleSetting.SORT_REGISTER_TIME) {
      selectedChoice = 1;
    }

    builder = new android.app.AlertDialog.Builder(getActivity());
    builder.setSingleChoiceItems(sortTypes, selectedChoice, mDialogListener);
    mDialogSelectSortType = builder.create();
    mDialogSelectSortType.show();
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == REQUEST_REGION && resultCode == Activity.RESULT_OK) {
      if (data != null) {
        int[] regions = data
            .getIntArrayExtra(RegionSearchSettingFragment.RETURN_REGION);
        int distance = data.getIntExtra(
            RegionSearchSettingFragment.RETURN_DISTANCE, -1);
        settingResponse.setRegion(regions);
        if (distance >= 0) {
          settingResponse.setDistance(distance);
        }
      }
    }
  }

  @Override
  public void onClick(View view) {

    switch (view.getId()) {
      case R.id.search_age:
        showAgeDialogPicker();
        break;
      case R.id.search_state:
      case R.id.state_txt:
        RegionSearchSettingFragment regionFragment = RegionSearchSettingFragment
            .newInstance(settingResponse.getRegion(),
                settingResponse.getDistance());
        regionFragment.setTargetFragment(getRootParentFragment(this), REQUEST_REGION);
        mNavigationManager.addPage(regionFragment);
        break;
      case R.id.sort_txt:
        showDialogSort();
        break;
      case R.id.search_btn:
        search();
        break;
      case R.id.checkbox_filter_new_login:
        settingResponse.setIs_new_login(cbxFilterNewLogin.isChecked());
        break;

      case R.id.search_by_name_row:
        SearchByNameFragment searchByNameFragment = SearchByNameFragment.newInstance();
        mNavigationManager.addPage(searchByNameFragment);
        break;

      default:
        break;
    }
  }

  public void search() {
    // save search setting to cache
    MeetPeopleSetting.getInstance(mAppContext).saveResponse(
        settingResponse);
    // notify refresh at top screen
    getTargetFragment().onActivityResult(getTargetRequestCode(),
        Activity.RESULT_OK, null);
    // back to top screen
    mNavigationManager.goBack();
  }

  private String getTextRangeAge(int minAge, int maxAge) {
    StringBuffer ageRange = new StringBuffer();
    ageRange.append(minAge).append(" ~ ").append(maxAge);
    return ageRange.toString();
  }

  private void fillAgeRange() {
    txtAgeRange.setText(getTextRangeAge(settingResponse.getLowerAge(),
        settingResponse.getUpperAge()));
  }

  private void fillRegion() {
    switch (settingResponse.getDistance()) {
      case MeetPeopleSetting.NEAR_VALUE:
        mtxtRegionOption
            .setText(getString(R.string.region_search_setting_near_region_text));
        break;
      case MeetPeopleSetting.CITY_VALUE:
        mtxtRegionOption
            .setText(getString(R.string.region_search_setting_city_region_text));
        break;
      case MeetPeopleSetting.REGION_VALUE:
        int[] regions = settingResponse.getRegion();
        String text = getString(R.string.region_search_setting_state_region_text);
        if (regions != null && regions.length > 0) {
          text += getRegionNameList(regions);
        }
        mtxtRegionOption.setText(text);
        break;
      case MeetPeopleSetting.COUNTRY_VALUE:
        mtxtRegionOption
            .setText(getString(R.string.region_search_setting_country_region_text));
        break;
      case MeetPeopleSetting.WORLD_VALUE:
      default:
        mtxtRegionOption
            .setText(getString(R.string.region_search_setting_world_region_text));
        break;
    }
  }

  private void fillSortType() {
    if (settingResponse.getSortType() == MeetPeopleSetting.SORT_LOGIN_TIME) {
      txtSortType.setText(R.string.search_setting_sort_time_login);
    } else {
      txtSortType.setText(R.string.search_setting_sort_time_signup);
    }
  }

  private void fillFilter() {
    if (settingResponse.getFilter() == MeetPeopleSetting.FILTER_NEW_REGISTER) {
      cbxFilterNewRegister.setChecked(true);
      cbxFilterCall.setChecked(false);
    } else if (settingResponse.getFilter() == MeetPeopleSetting.FILTER_CALL_WAITING) {
      cbxFilterNewRegister.setChecked(false);
      cbxFilterCall.setChecked(true);
    } else {
      // filter all
      cbxFilterNewRegister.setChecked(false);
      cbxFilterCall.setChecked(false);
    }
    cbxFilterNewLogin.setChecked(settingResponse.isNewLogin());
  }

  private String getRegionNameList(int[] regions) {
    if (regions == null || regions.length <= 0) {
      return null;
    }
    RegionUtils regionUtils = new RegionUtils(mAppContext);
    StringBuilder builder = new StringBuilder();
    builder.append(" (");
    int length = regions.length;
    for (int i = 0; i < length; i++) {
      builder.append(regionUtils.getRegionName(regions[i]));
      if (i != length - 1) {
        builder.append(", ");
      } else {
        builder.append(")");
      }
    }
    return builder.toString();
  }
}