package com.application.ui.viewholders;

import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by vn apnic on 4/4/2016.
 */
public abstract class BaseViewHolder {

  protected View root;
  protected AppCompatActivity context;
  protected IOnActionViewHolder actionViewHolder;

  public BaseViewHolder(View root, AppCompatActivity context,
      IOnActionViewHolder actionViewHolder) {
    try {
      this.root = root;
      this.context = context;
      this.actionViewHolder = actionViewHolder;
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  protected abstract void initView(View root, AppCompatActivity context) throws Exception;

  protected abstract void initRegenerativeFunction(AppCompatActivity context) throws Exception;

  public abstract void onStart() throws Exception;

  public abstract void onResume() throws Exception;

  public abstract void onPause() throws Exception;

  public abstract void onSaveInstanceState() throws Exception;

  public abstract void onDestroy() throws Exception;

  public abstract void onDestroyView() throws Exception;
}
