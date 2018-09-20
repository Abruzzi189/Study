/**
 *
 */
package com.application.ui.buzz;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.application.constant.Constants;
import com.application.entity.BuzzListCommentItem;
import com.application.entity.BuzzListItem;
import com.application.ui.buzz.BuzzItemListView.OnActionBuzzListener;
import com.application.ui.buzz.CommentItemBuzz.OnActionCommentListener;
import com.application.ui.buzz.SubCommentItemBuzz.OnDeleteSubCommentListener;
import java.util.ArrayList;
import java.util.List;

/**
 * @author MTROL
 */
public class BuzzListAdapter extends BaseAdapter {

  private static final int MAX_TYPE = 3;
  private List<BuzzListItem> mBuzzList = new ArrayList<BuzzListItem>();
  private Context mContext;
  private OnActionCommentListener mDeleteBuzzCommentListener;
  private OnDeleteSubCommentListener mDeleteSubCommentListener;
  private OnActionBuzzListener mActionBuzzListener;
  private BaseBuzzListFragment mBaseBuzzListFragment;

  public BuzzListAdapter(Context context,
      OnActionCommentListener deleteBuzzCommentListener,
      OnDeleteSubCommentListener deleteSubCommentListener,
      OnActionBuzzListener actionBuzzListener) {
    mContext = context;
    mDeleteBuzzCommentListener = deleteBuzzCommentListener;
    mDeleteSubCommentListener = deleteSubCommentListener;
    mActionBuzzListener = actionBuzzListener;
  }

  public void setBaseBuzzListFragment(BaseBuzzListFragment mBase) {
    this.mBaseBuzzListFragment = mBase;
  }

  @Override
  public int getCount() {
    return mBuzzList.size();
  }

  @Override
  public Object getItem(int position) {
    if (position >= mBuzzList.size()) {
      return null;
    }

    return mBuzzList.get(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  public Object remove(int position) {
    if (position >= mBuzzList.size()) {
      return null;
    }

    return mBuzzList.remove(position);
  }

  public List<BuzzListItem> getListItems() {
    return mBuzzList;
  }

  public boolean contains(String buzzId) {
    int size = mBuzzList.size();

    for (int i = 0; i < size; i++) {
      if (mBuzzList.get(i).getBuzzId().equals(buzzId)) {
        return true;
      }
    }
    return false;
  }

  public int getPosition(BuzzListItem item) {
    int position = -1;
    int size = mBuzzList.size();

    for (int i = 0; i < size; i++) {
      if (mBuzzList.get(i).getBuzzId().equals(item.getBuzzId())) {
        return i;
      }
    }
    return position;
  }

  public BuzzListItem getItemByBuzzID(String buzzID) {
    for (BuzzListItem item : mBuzzList) {
      if (item.getBuzzId().equals(buzzID)) {
        return item;
      }
    }
    return null;
  }

  public void addBuzzListItem(BuzzListItem item, int position) {
    if (position > -1) {
      mBuzzList.add(position, item);
    }
    this.notifyDataSetChanged();
  }

  public BuzzListItem getItemByCommentID(String commentID) {
    for (BuzzListItem item : mBuzzList) {
      ArrayList<BuzzListCommentItem> commentList = item.getCommentList();
      for (int i = 0; i < commentList.size(); i++) {
        BuzzListCommentItem commentItem = commentList.get(i);
        if (commentItem.cmt_id.equals(commentID)) {
          return item;
        }
      }
    }
    return null;
  }

  @Override
  public int getItemViewType(int position) {
    if (position >= mBuzzList.size()) {
      return -1;
    }

    return mBuzzList.get(position).getBuzzType();
  }

  @Override
  public int getViewTypeCount() {
    return MAX_TYPE;
  }

  @Override
  public View getView(final int position, View convertView, ViewGroup parent) {
    ViewHolder holder = null;
    final BuzzListItem entity = mBuzzList.get(position);
    int type = getItemViewType(position);
    if (convertView == null) {
      holder = new ViewHolder();
      switch (type) {
        case Constants.BUZZ_TYPE_GIFT:
        case Constants.BUZZ_TYPE_IMAGE:
        case Constants.BUZZ_TYPE_STATUS:
          convertView = new BuzzItemListView(mContext, type, true);
          ((BuzzItemListView) convertView)
              .setBaseBuzzListFragment(this.mBaseBuzzListFragment);

          break;
        default:
          return null;
      }

      holder.mItem = (BuzzItemListView) convertView;
      convertView.setTag(holder);
    } else {
      holder = (ViewHolder) convertView.getTag();
    }

    holder.mItem.updateView(entity, true, position,
        mDeleteBuzzCommentListener, mDeleteSubCommentListener, mActionBuzzListener);

    return convertView;
  }

  public void appendList(List<BuzzListItem> buzzList) {
    mBuzzList.addAll(buzzList);
    this.notifyDataSetChanged();
  }

  /**
   * Remove all elements from this adapter, leaving it empty
   */
  public void clearAllData() {
    mBuzzList.clear();
    this.notifyDataSetChanged();
  }

  public void addBuzzToTop(BuzzListItem item) {
    mBuzzList.add(0, item);
    this.notifyDataSetChanged();
  }

  private static class ViewHolder {

    public BuzzItemListView mItem;
  }
}
