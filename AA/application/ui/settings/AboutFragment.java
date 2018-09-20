package com.application.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.application.common.webview.WebViewFragment;
import com.application.ui.BaseFragment;
import com.ntq.utils.Utils;
import glas.bbsystem.R;

public class AboutFragment extends BaseFragment implements View.OnClickListener {

  private TextView tvCurrentVersion;

  public static AboutFragment newInstance() {
    return new AboutFragment();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_about, container, false);
    initViews(view);
    return view;
  }

  private void initViews(View rootView) {
    tvCurrentVersion = (TextView) rootView.findViewById(R.id.tvCurrentVersion);

    RelativeLayout layoutCurrentVersion = (RelativeLayout) rootView
        .findViewById(R.id.rlCurrentVersion);
    RelativeLayout layoutTermsOfService = (RelativeLayout) rootView
        .findViewById(R.id.rlTermsOfService);
    RelativeLayout layoutPrivacyPolicy = (RelativeLayout) rootView
        .findViewById(R.id.rlPrivacyPolicy);
    RelativeLayout layoutSpecificTrade = (RelativeLayout) rootView
        .findViewById(R.id.rlSpecificTrade);

    setCurrentVersion();

    layoutCurrentVersion.setOnClickListener(this);
    layoutTermsOfService.setOnClickListener(this);
    layoutPrivacyPolicy.setOnClickListener(this);
    layoutSpecificTrade.setOnClickListener(this);
  }

  private void setCurrentVersion() {
    tvCurrentVersion.setText(Utils.getCurrentVersion(mAppContext));
  }

  @Override
  public void onClick(View view) {
    BaseFragment fragment = null;
    switch (view.getId()) {
      case R.id.rlTermsOfService:
        fragment = WebViewFragment
            .newInstance(WebViewFragment.PAGE_TYPE_TERM_OF_SERVICE);

        break;
      case R.id.rlPrivacyPolicy:
        fragment = WebViewFragment
            .newInstance(WebViewFragment.PAGE_TYPE_PRIVACY_POLICY);
        break;
      case R.id.rlSpecificTrade:
        fragment = WebViewFragment
            .newInstance(WebViewFragment.PAGE_TYPE_NOTICE);
        break;
      default:
        break;
    }
    if (fragment != null) {
      mNavigationManager.addPage(fragment);
    }
  }

}
