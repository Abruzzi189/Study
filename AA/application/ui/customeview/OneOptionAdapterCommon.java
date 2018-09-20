package com.application.ui.customeview;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.TextView;
import com.application.entity.ItemCommonAdapter;
import glas.bbsystem.R;
import java.util.ArrayList;


public class OneOptionAdapterCommon extends ArrayAdapter<ItemCommonAdapter> {

  private ArrayList<ItemCommonAdapter> objects;
  private Dialog dialog;
  private TextView tvDisplay;

  public OneOptionAdapterCommon(Context context, int textViewResourceId,
      ArrayList<ItemCommonAdapter> objects, Dialog dialog,
      TextView tvDisplay) {
    super(context, textViewResourceId, objects);
    this.objects = objects;
    this.dialog = dialog;
    this.tvDisplay = tvDisplay;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    HolderView holder = null;
    ItemCommonAdapter itemCommon = getItem(position);
    if (convertView == null) {
      holder = new HolderView();
      LayoutInflater inflater = (LayoutInflater) getContext()
          .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      convertView = inflater.inflate(
          R.layout.item_one_option_dialog_edit_my_profile, null);
      holder.tvDisplay = (TextView) convertView
          .findViewById(R.id.dialog_view);
      holder.rdValue = (RadioButton) convertView
          .findViewById(R.id.dialog_rd);
      convertView.setTag(holder);
    } else {
      holder = (HolderView) convertView.getTag();
    }
    holder.tvDisplay.setText(itemCommon.getDisplay());
    holder.rdValue.setChecked(itemCommon.isCheck());
    holder.rdValue.setTag(position);
    holder.rdValue.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View view) {
        for (ItemCommonAdapter item : objects) {
          item.setCheck(false);
        }
        int index = Integer.valueOf(view.getTag().toString());
        objects.get(index).setCheck(true);
        tvDisplay.setText(objects.get(index).getDisplay());
        notifyDataSetChanged();
        dialog.dismiss();
      }
    });
    return convertView;
  }

  private class HolderView {

    public TextView tvDisplay;
    public RadioButton rdValue;
  }
}
