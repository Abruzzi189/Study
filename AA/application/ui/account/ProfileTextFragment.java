package com.application.ui.account;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import com.application.constant.UserSetting;
import com.application.ui.BaseFragment;
import com.application.util.Utility;
import glas.bbsystem.R;


public class ProfileTextFragment extends BaseFragment {

  public static final int FIELD_FETISH = 100;
  public static final int FIELD_HOBBY = 101;
  public static final int FIELD_MESSAGE = 102;
  public static final int FIELD_TYPE_MAN = 103;

  protected static final String KEY_DATA = "key_data";
  protected static final String KEY_FIELD = "key_field";
  protected static final String KEY_GENDER = "key_gender";

  private EditText mEdtData;
  private String data;
  private int field;
  private int gender;

  //set limit text msg
//	boolean limited =false;
//	public InputFilter [] fArray = new InputFilter[1];
  public static ProfileTextFragment newInstance(String data, int field,
      int gender) {
    ProfileTextFragment fragment = new ProfileTextFragment();
    Bundle bundle = new Bundle();
    bundle.putString(KEY_DATA, data);
    bundle.putInt(KEY_FIELD, field);
    bundle.putInt(KEY_GENDER, gender);
    fragment.setArguments(bundle);
    return fragment;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_profile_text, container,
        false);

    Bundle bundle = getArguments();
    if (bundle != null) {
      data = bundle.getString(KEY_DATA);
      field = bundle.getInt(KEY_FIELD);
      gender = bundle.getInt(KEY_GENDER);
    }

    initViews(view);
    return view;
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    Utility.hideSoftKeyboard(getActivity());
  }

  @Override
  public void onStart() {
    super.onStart();
    if (mEdtData != null) {
      Utility.showDelayKeyboard(mEdtData, 0);
    }
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    fillDataToUI();
  }

  private void initViews(View view) {
    mEdtData = (EditText) view.findViewById(R.id.edt_data);
    mEdtData.clearFocus();
    setMaxLengthForFields();
//		mEdtData.addTextChangedListener(new TextWatcher() {
//			@Override
//			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//				String msg = s.toString();
//				if(java.text.Normalizer.normalize(msg, Normalizer.Form.NFKC).length()==2000)
//				{
//					limited = true;
//					fArray[0] = new InputFilter.LengthFilter(msg.length());
//					mEdtData.setFilters(fArray);
//				}
//				else
//				{
//					if(limited)
//					{
//						limited = false;
//						fArray[0] = new InputFilter.LengthFilter(Integer.MAX_VALUE);
//						mEdtData.setFilters(fArray);
//					}
//				}
//			}

//			@Override
//			public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//			}
//
//			@Override
//			public void afterTextChanged(Editable s) {
//
//			}
//		});

  }

  private void setMaxLengthForFields() {
    int maxLength = 100;
    if (field == FIELD_MESSAGE) {
      maxLength = 200;
    }

    InputFilter[] fArray = new InputFilter[1];
    fArray[0] = new InputFilter.LengthFilter(maxLength);
    mEdtData.setFilters(fArray);
  }

  private void fillDataToUI() {
    if (!TextUtils.isEmpty(data)) {
      mEdtData.setText(data);
    }

    int resIdTitle = -1;
    switch (field) {
      case ProfileTextFragment.FIELD_FETISH:
        resIdTitle = R.string.profile_reg_fetish;
        break;
      case ProfileTextFragment.FIELD_HOBBY:
        resIdTitle = R.string.profile_reg_hobby;
        break;
      case ProfileTextFragment.FIELD_MESSAGE:
        if (gender == UserSetting.GENDER_FEMALE) {
          resIdTitle = R.string.profile_reg_message_female;
        } else {
          resIdTitle = R.string.profile_reg_message_male;
        }
        break;
      case ProfileTextFragment.FIELD_TYPE_MAN:
        resIdTitle = R.string.profile_reg_type_man;
        break;
      default:
        break;
    }

    if (resIdTitle != -1) {
      mActionBar.setTextCenterTitle(resIdTitle);
    }
  }

  public void onSave() {
    Utility.hideSoftKeyboard(getActivity());
    String data = mEdtData.getText().toString().replace("\u3000", " ")
        .trim();

    // Validate data
    if (Utility.isContainDirtyWord(getActivity(), mEdtData)) {
      return;
    }

    // Send saved data
    Intent intent = new Intent();
    intent.putExtra(KEY_DATA, data);
    intent.putExtra(KEY_FIELD, field);
    getTargetFragment().onActivityResult(getTargetRequestCode(),
        Activity.RESULT_OK, intent);

    mNavigationManager.goBack();
  }

  public int getField() {
    return field;
  }

  public int getGender() {
    return gender;
  }

  public void setGender(int gender) {
    this.gender = gender;
  }
}