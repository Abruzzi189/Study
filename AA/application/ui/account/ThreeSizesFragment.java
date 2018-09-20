package com.application.ui.account;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import com.application.ui.BaseFragment;
import com.application.util.Utility;
import glas.bbsystem.R;


public class ThreeSizesFragment extends BaseFragment {

  protected static final String MEASUREMENT = "measurement";
  private EditText mEdtSizeB;
  private EditText mEdtSizeW;
  private EditText mEdtSizeH;
  private int[] threeSizes;

  public static ThreeSizesFragment newInstance(int[] threeSizes) {
    ThreeSizesFragment fragment = new ThreeSizesFragment();
    Bundle bundle = new Bundle();
    bundle.putIntArray(MEASUREMENT, threeSizes);
    fragment.setArguments(bundle);

    return fragment;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    View view = inflater.inflate(R.layout.fragment_three_sizes, container,
        false);

    Bundle bundle = getArguments();
    if (bundle != null) {
      threeSizes = bundle.getIntArray(MEASUREMENT);
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
    if (mEdtSizeB != null) {
      Utility.showDelayKeyboard(mEdtSizeB, 10);
    }
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    fillDataForUI();
  }

  private void initViews(View view) {
    mEdtSizeB = (EditText) view.findViewById(R.id.edt_b);
    mEdtSizeW = (EditText) view.findViewById(R.id.edt_w);
    mEdtSizeH = (EditText) view.findViewById(R.id.edt_h);
  }

  private void fillDataForUI() {
    if (threeSizes != null && threeSizes.length == 3) {
      mEdtSizeB.setText(String.valueOf(threeSizes[0]));
      mEdtSizeW.setText(String.valueOf(threeSizes[1]));
      mEdtSizeH.setText(String.valueOf(threeSizes[2]));
    }
  }

  public void onSave() {
    Utility.hideSoftKeyboard(getActivity());

    threeSizes = new int[3];

    String b = mEdtSizeB.getText().toString().trim();
    String w = mEdtSizeW.getText().toString().trim();
    String h = mEdtSizeH.getText().toString().trim();

    threeSizes[0] = TextUtils.isEmpty(b) ? 0 : Integer.valueOf(b);
    threeSizes[1] = TextUtils.isEmpty(w) ? 0 : Integer.valueOf(w);
    threeSizes[2] = TextUtils.isEmpty(h) ? 0 : Integer.valueOf(h);

    Intent intent = new Intent();
    intent.putExtra(MEASUREMENT, threeSizes);
    getTargetFragment().onActivityResult(getTargetRequestCode(),
        Activity.RESULT_OK, intent);
    mNavigationManager.goBack();
  }
}
