package com.application.connection.request;

import com.application.common.webview.WebViewFragment;
import com.google.gson.annotations.SerializedName;

public class GetStaticPageRequest extends RequestParams {

  private static final long serialVersionUID = 6202200810555031423L;

  // int: 0: Term of Service | 1: Privacy Policy | 2: Term of use
  @SerializedName("page_type")
  private int pageType;

  public GetStaticPageRequest(int pageType) {
    super();
    this.api = "static_page";
    switch (pageType) {
      case WebViewFragment.PAGE_TYPE_TERM_OF_SERVICE:
        this.pageType = WebViewFragment.STATIC_TYPE_TERM_OF_SERVICE;
        break;
      case WebViewFragment.PAGE_TYPE_PRIVACY_POLICY:
        this.pageType = WebViewFragment.STATIC_TYPE_PRIVACY_POLICY;
        break;
      case WebViewFragment.PAGE_TYPE_TERM_OF_USE:
        this.pageType = WebViewFragment.STATIC_TYPE_TERM_OF_USE;
        break;
    }
  }
}