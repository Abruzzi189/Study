package com.application.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import com.application.connection.Response;
import com.application.connection.ResponseData;
import com.application.connection.ResponseReceiver;
import com.application.connection.request.AddTemplateRequest;
import com.application.connection.request.UpdateTemplateRequest;
import com.application.connection.response.AddTemplateResponse;
import com.application.connection.response.UpdateTemplateResponse;
import com.application.entity.Template;
import com.application.service.DataFetcherService;
import com.application.ui.customeview.ErrorApiDialog;
import com.application.util.Utility;
import com.application.util.preferece.UserPreferences;
import glas.bbsystem.R;


public class AddOrUpdateTemplateFragment extends BaseFragment implements
    OnClickListener, ResponseReceiver {

  public static final int FUNCTION_ADD = 1;
  public static final int FUNCTION_UPDATE = 2;

  private static final int LOADER_ADD_TEMPLATE = 0;
  private static final int LOADER_UPDATE_TEMPLATE = 1;

  private static final String KEY_FUNCTION = "key_function";
  private static final String KEY_TEMPLATE = "key_template";

  private static final int INPUT_TITLE = 1;
  private static final int INPUT_CONTENT = 2;

  private static final int LIMITED_CHARACTER_OF_TITLE = 30;
  private static final int LIMITED_CHARACTER_OF_CONTENT = 100;

  private static final int TITLE_FOCUSED = 1;
  private static final int CONTENT_FOCUSED = 2;

  private EditText mTitle, mContent;
  private TextView mCounter;
  private int mFunction = FUNCTION_ADD;
  private Template mTemplate;
  private int mFocus = TITLE_FOCUSED;

  private AlertDialog mAlertDialog;
  private OnFocusChangeListener focusListener = new OnFocusChangeListener() {
    public void onFocusChange(View v, boolean hasFocus) {
      if (hasFocus) {
        if (v.getId() == mTitle.getId()) {
          mFocus = TITLE_FOCUSED;
          mCounter.setText(mTitle.getText().length() + "/"
              + LIMITED_CHARACTER_OF_TITLE);
        } else if (v.getId() == mContent.getId()) {
          mFocus = CONTENT_FOCUSED;
          mCounter.setText(mContent.getText().length() + "/"
              + LIMITED_CHARACTER_OF_CONTENT);
        }
      }
    }
  };
  private TextWatcher textWatcherListener = new TextWatcher() {

    @Override
    public void onTextChanged(CharSequence s, int start, int before,
        int count) {
      if (mFocus == TITLE_FOCUSED) {
        mCounter.setText(mTitle.getText().length() + "/"
            + LIMITED_CHARACTER_OF_TITLE);
      } else if (mFocus == CONTENT_FOCUSED) {
        mCounter.setText(mContent.getText().length() + "/"
            + LIMITED_CHARACTER_OF_CONTENT);
      }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
        int after) {
    }

    @Override
    public void afterTextChanged(Editable s) {
    }
  };

  public static AddOrUpdateTemplateFragment newInstance(int function) {
    AddOrUpdateTemplateFragment fragment = new AddOrUpdateTemplateFragment();
    Bundle bundle = new Bundle();
    bundle.putInt(KEY_FUNCTION, function);
    fragment.setArguments(bundle);
    return fragment;
  }

  public static AddOrUpdateTemplateFragment newInstance(int function,
      Template template) {
    AddOrUpdateTemplateFragment fragment = new AddOrUpdateTemplateFragment();
    Bundle bundle = new Bundle();
    bundle.putInt(KEY_FUNCTION, function);
    bundle.putSerializable(KEY_TEMPLATE, template);
    fragment.setArguments(bundle);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (savedInstanceState != null) {
      mFunction = savedInstanceState.getInt(KEY_FUNCTION);
      mTemplate = (Template) savedInstanceState
          .getSerializable(KEY_TEMPLATE);
    } else {
      mFunction = getArguments().getInt(KEY_FUNCTION);
      mTemplate = (Template) getArguments().getSerializable(KEY_TEMPLATE);
    }

    // Request banned word each time user visit to chat screen
    requestDirtyWord();
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putInt(KEY_FUNCTION, mFunction);
    outState.putSerializable(KEY_TEMPLATE, mTemplate);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_add_or_update_template,
        container, false);
    return view;
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    initView(view);
  }

  private void initView(View view) {
    mTitle = (EditText) view.findViewById(R.id.edt_title);
    mContent = (EditText) view.findViewById(R.id.edt_content);
    mCounter = (TextView) view.findViewById(R.id.tv_counter);

    if (mTemplate != null) {
      mTitle.setText(mTemplate.getTempTitle());
      mContent.setText(mTemplate.getTempContent());
    }
    mTitle.requestFocus();
    mTitle.setSelection(mTitle.getText().length());

    InputMethodManager imm = (InputMethodManager) getActivity()
        .getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.showSoftInput(mTitle, InputMethodManager.SHOW_IMPLICIT);

    mTitle.setOnFocusChangeListener(focusListener);
    mContent.setOnFocusChangeListener(focusListener);
    view.findViewById(R.id.btn_save).setOnClickListener(this);

    mTitle.setOnFocusChangeListener(focusListener);
    mContent.setOnFocusChangeListener(focusListener);
    mTitle.addTextChangedListener(textWatcherListener);
    mContent.addTextChangedListener(textWatcherListener);

  }

  private void requestAddTemplate(String title, String content) {
    String token = UserPreferences.getInstance().getToken();
    AddTemplateRequest request = new AddTemplateRequest(token, title,
        content);
    restartRequestServer(LOADER_ADD_TEMPLATE, request);
  }

  private void requestUpdateTemplate(String id, String title, String content) {
    String token = UserPreferences.getInstance().getToken();
    UpdateTemplateRequest request = new UpdateTemplateRequest(token, id,
        title, content);
    restartRequestServer(LOADER_UPDATE_TEMPLATE, request);
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.btn_save:
        if (checkValid()
            && !Utility.isContainDirtyWord(getActivity(), mTitle)
            && !Utility.isContainDirtyWord(getActivity(), mContent)) {
          if (mFunction == FUNCTION_ADD) {
            requestAddTemplate(mTitle.getText().toString().trim(),
                mContent.getText().toString().trim());
          } else if (mFunction == FUNCTION_UPDATE) {
            requestUpdateTemplate(mTemplate.getTempId(), mTitle
                .getText().toString().trim(), mContent.getText()
                .toString().trim());
          }
        }
        break;
      default:
        break;
    }
  }

  private boolean checkValid() {
    if (TextUtils.isEmpty(mTitle.getText().toString().trim())) {
      showDialogNotifyInput(INPUT_TITLE);
      return false;
    } else if (TextUtils.isEmpty(mContent.getText().toString().trim())) {
      showDialogNotifyInput(INPUT_CONTENT);
      return false;
    } else {
      return true;
    }
  }

  /**
   * Notify data service to load list dirty word
   */
  private void requestDirtyWord() {
    DataFetcherService.startLoadDirtyWord(mAppContext);
//    Activity activity = getActivity();
//    if (activity != null) {
//      DataFetcherService.startLoadDirtyWord(activity);
//    }
  }

  private void showDialogNotifyInput(int type) {
    Builder builder = new CenterButtonDialogBuilder(getContext(), false);
    if (type == INPUT_TITLE) {
      builder.setMessage(R.string.template_notify_input_title);
    } else if (type == INPUT_CONTENT) {
      builder.setMessage(R.string.template_notify_input_content);
    }
    builder.setPositiveButton(R.string.common_ok, null);
    mAlertDialog = builder.create();
    mAlertDialog.show();
  }

  private void handleAddTemplateResponse() {
    getLoaderManager().destroyLoader(LOADER_ADD_TEMPLATE);
    Handler handler = new Handler();
    handler.post(new Runnable() {
      @Override
      public void run() {
        mNavigationManager.goBack();
      }
    });
  }

  private void handleUpdateTemplateResponse() {
    getLoaderManager().destroyLoader(LOADER_UPDATE_TEMPLATE);
    Handler handler = new Handler();
    handler.post(new Runnable() {
      @Override
      public void run() {
        mNavigationManager.goBack();
      }
    });
  }

  @Override
  public void startRequest(int loaderId) {
    showWaitingDialog();
  }

  @Override
  public void receiveResponse(Loader<Response> loader, Response response) {
    if (getActivity() == null) {
      return;
    }
    hideWaitingDialog();
    if (response.getCode() != Response.SERVER_SUCCESS) {
      ErrorApiDialog.showAlert(getActivity(), R.string.common_error,
          response.getCode());
      getLoaderManager().destroyLoader(loader.getId());
      return;
    }

    if (response instanceof AddTemplateResponse) {
      handleAddTemplateResponse();
    } else if (response instanceof UpdateTemplateResponse) {
      handleUpdateTemplateResponse();
    }
  }

  @Override
  public Response parseResponse(int loaderID, ResponseData data,
      int requestType) {
    Response response = null;
    if (loaderID == LOADER_ADD_TEMPLATE) {
      response = new AddTemplateResponse(data);
    } else if (loaderID == LOADER_UPDATE_TEMPLATE) {
      response = new UpdateTemplateResponse(data);
    }
    return response;
  }

  @Override
  public void onBaseLoaderReset(Loader<Response> loader) {
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    hideWaitingDialog();
    Utility.hideSoftKeyboard(getActivity());
    if (mAlertDialog != null && mAlertDialog.isShowing()) {
      mAlertDialog.dismiss();
    }
  }
}
