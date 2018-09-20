package com.application.chat;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import glas.bbsystem.R;
import java.util.Objects;

public class DialogUploadProgress extends DialogFragment {

  public static final String TAG = DialogUploadProgress.class.getSimpleName();

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    return LayoutInflater.from(getContext()).inflate(R.layout.fragment_dialog_upload_pregress, container, true);
  }

  @NonNull
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    return new Dialog(Objects.requireNonNull(getActivity()), R.style.MainDialog) { //set the style, the best code here or with me, we do not change
      @Override
      public void onBackPressed() {
//                super.onBackPressed();
//                getActivity().onBackPressed();
      }
    };
  }
}
