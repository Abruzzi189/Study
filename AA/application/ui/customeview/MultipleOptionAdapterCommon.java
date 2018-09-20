package com.application.ui.customeview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import com.application.entity.ItemCommonAdapter;
import glas.bbsystem.R;
import java.util.ArrayList;


public class MultipleOptionAdapterCommon extends
    ArrayAdapter<ItemCommonAdapter> {

  private ArrayList<ItemCommonAdapter> objects;
  private boolean isEthnicity;

  public MultipleOptionAdapterCommon(Context context, int textViewResourceId,
      ArrayList<ItemCommonAdapter> objects) {
    super(context, textViewResourceId, objects);
    this.objects = objects;
  }

  public MultipleOptionAdapterCommon(Context context, int textViewResourceId,
      ArrayList<ItemCommonAdapter> objects, boolean isEthnicity) {
    super(context, textViewResourceId, objects);
    this.objects = objects;
    this.isEthnicity = isEthnicity;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    HolderViewMultipleOption holde = null;
    ItemCommonAdapter itemCommon = getItem(position);
    if (convertView == null) {
      holde = new HolderViewMultipleOption();
      LayoutInflater inflater = (LayoutInflater) getContext()
          .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      convertView = inflater.inflate(
          R.layout.item_multiple_option_dialog_edit_my_profile, null);
      holde.tvDisplay = (TextView) convertView
          .findViewById(R.id.dialog_view);
      holde.cbValue = (CheckBox) convertView.findViewById(R.id.dialog_cb);
      convertView.setTag(holde);
    } else {
      holde = (HolderViewMultipleOption) convertView.getTag();
    }
    holde.tvDisplay.setText(itemCommon.getDisplay());
    holde.cbValue.setChecked(itemCommon.isCheck());
    holde.cbValue.setTag(position);
    holde.cbValue.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        CheckBox cbAdapter = (CheckBox) view;
        int index = Integer.parseInt(cbAdapter.getTag().toString());
        // if (isEthnicity) {
        // if (index == 0) {
        // for (ItemCommonAdapter item : objects) {
        // item.setCheck(false);
        // }
        // objects.get(index).setCheck(true);
        // } else {
        // if (objects.get(0).isCheck()) {
        // objects.get(0).setCheck(false);
        // }
        // objects.get(index).setCheck(
        // cbAdapter.isChecked() ? true : false);
        // }
        // notifyDataSetChanged();
        // } else {
        // objects.get(index).setCheck(
        // cbAdapter.isChecked() ? true : false);
        // }
        if (index == 0) {
          for (ItemCommonAdapter item : objects) {
            item.setCheck(false);
          }
          objects.get(0).setCheck(true);
        } else {
          if (objects.get(0).isCheck()) {
            objects.get(0).setCheck(false);
          }
          objects.get(index).setCheck(
              cbAdapter.isChecked() ? true : false);
        }
        notifyDataSetChanged();
      }
    });
    return convertView;
  }

  public ArrayList<ItemCommonAdapter> getList() {
    return objects;
  }
}
