package com.application.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.application.entity.StickerCategory;
import com.ntq.adapters.CardListAdapter;
import com.ntq.api.model.BucketedList;
import glas.bbsystem.R;


public class StickerCategoryListAdapter extends
    CardListAdapter<StickerCategory> {

  public StickerCategoryListAdapter(Context context,
      BucketedList<StickerCategory> bucketedList, ImageLoader imageLoader) {
    super(context, bucketedList, imageLoader);
  }

  @Override
  protected View getViewByData(int position, View convertView,
      ViewGroup parent) {
    ViewHolder holder = null;
    if (convertView == null) {
      holder = new ViewHolder();
      convertView = inflate(R.layout.item_list_stickercategory, parent,
          false);
      holder.txtTitle = (TextView) convertView.findViewById(R.id.title);
      holder.txtDescription = (TextView) convertView
          .findViewById(R.id.description);
      holder.networkImageView = (NetworkImageView) convertView
          .findViewById(R.id.avatar);
      convertView.setTag(holder);
    } else {
      holder = (ViewHolder) convertView.getTag();
    }
    StickerCategory stickerCategory = mBucketedList.getItem(position);
    holder.txtDescription.setText(stickerCategory.getDescription());
    holder.txtTitle.setText(stickerCategory.getName());
    // holder.networkImageView.setImageUrl(stickerCategory.get, imageLoader)
    return convertView;
  }

  @Override
  protected int getItemAdapterViewType(int position) {
    return 2;
  }

  @Override
  public int getViewTypeCount() {
    return super.getViewTypeCount() + 1;
  }

  @Override
  public void onDestroyView() {

  }

  private class ViewHolder {

    public TextView txtTitle;
    public TextView txtDescription;
    public NetworkImageView networkImageView;
  }

}
