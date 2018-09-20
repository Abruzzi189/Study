package com.application.ui.customeview.pulltorefresh;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.WrapperListAdapter;
import java.util.ArrayList;

public class GridViewWithHeaderAndFooter extends GridView {

  private ArrayList<FixedViewInfo> mHeaderViewInfos = new ArrayList<FixedViewInfo>();
  private ArrayList<FixedViewInfo> mFooterViewInfos = new ArrayList<FixedViewInfo>();
  private int mRequestedNumColumns;
  private int mNumColmuns = 1;
  public GridViewWithHeaderAndFooter(Context context) {
    super(context);
    super.setClipChildren(false);
  }

  public GridViewWithHeaderAndFooter(Context context, AttributeSet attrs) {
    super(context, attrs);
    super.setClipChildren(false);
  }

  public GridViewWithHeaderAndFooter(Context context, AttributeSet attrs,
      int defStyle) {
    super(context, attrs, defStyle);
    super.setClipChildren(false);
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    if (mRequestedNumColumns != AUTO_FIT) {
      mNumColmuns = mRequestedNumColumns;
    }

    if (mNumColmuns < 1) {
      mNumColmuns = 1;
    }

    ListAdapter adapter = getAdapter();
    if (adapter != null && adapter instanceof NestedAdapter) {
      ((NestedAdapter) adapter).setNumColumns(getNumColumns());
    }
  }

  @Override
  public void setClipChildren(boolean clipChildren) {
    return;
  }

  public void addHeaderView(View v, Object data, boolean isSelectable) {
    ListAdapter adapter = getAdapter();

    if (adapter != null && !(adapter instanceof NestedAdapter)) {
      throw new IllegalStateException(
          "Cannot add header view to grid -- setAdapter has already been called.");
    }

    FixedViewInfo info = new FixedViewInfo();
    FrameLayout fl = new FullWidthFixedViewLayout(getContext());
    fl.addView(v);
    info.view = v;
    info.viewContainer = fl;
    info.data = data;
    info.isSelectable = isSelectable;
    mHeaderViewInfos.add(info);

    // in the case of re-adding a header view, or adding one later on,
    // we need to notify the observer
    if (adapter != null) {
      ((NestedAdapter) adapter).notifyDataSetChanged();
    }
  }

  public void addHeaderView(View v) {
    addHeaderView(v, null, true);
  }

  public void addFooterView(View v, Object data, boolean isSelectable) {
    ListAdapter adapter = getAdapter();

    if (adapter != null && !(adapter instanceof NestedAdapter)) {
      throw new IllegalStateException(
          "Cannot add footer view to grid -- setAdapter has already been called.");
    }

    FixedViewInfo info = new FixedViewInfo();
    FrameLayout fl = new FullWidthFixedViewLayout(getContext());
    fl.addView(v);
    info.view = v;
    info.viewContainer = fl;
    info.data = data;
    info.isSelectable = isSelectable;
    mFooterViewInfos.add(info);

    // in the case of re-adding a header view, or adding one later on,
    // we need to notify the observer
    if (adapter != null) {
      ((NestedAdapter) adapter).notifyDataSetChanged();
    }
  }

  public void addFooterView(View v) {
    addFooterView(v, null, true);
  }

  public int getHeaderViewCount() {
    return mHeaderViewInfos.size();
  }

  public int getFooterViewsCount() {
    return mFooterViewInfos.size();
  }

  public boolean removeHeaderView(View v) {
    if (mHeaderViewInfos.size() > 0) {
      boolean result = false;
      ListAdapter adapter = getAdapter();
      if (adapter != null && ((NestedAdapter) adapter).removeHeader(v)) {
        result = true;
      }
      removeFixedViewInfo(v, mHeaderViewInfos);
      return result;
    }
    return false;
  }

  public boolean removeFooterView(View v) {
    if (mFooterViewInfos.size() > 0) {
      boolean result = false;
      ListAdapter adapter = getAdapter();
      if (adapter != null && ((NestedAdapter) adapter).removeFooter(v)) {
        result = true;
      }
      removeFixedViewInfo(v, mFooterViewInfos);
      return result;
    }
    return false;
  }

  private void removeFixedViewInfo(View v, ArrayList<FixedViewInfo> where) {
    int len = where.size();
    for (int i = 0; i < len; ++i) {
      FixedViewInfo info = where.get(i);
      if (info.view == v) {
        info.viewContainer.removeAllViews();
        where.remove(i);
        break;
      }
    }
  }

  @Override
  public void setAdapter(ListAdapter adapter) {
    if (mHeaderViewInfos.size() > 0 || mFooterViewInfos.size() > 0) {
      NestedAdapter hadapter = new NestedAdapter(mHeaderViewInfos,
          mFooterViewInfos, adapter);
      int numColumns = getNumColumns();
      if (numColumns > 1) {
        hadapter.setNumColumns(numColumns);
      }
      super.setAdapter(hadapter);
    } else {
      super.setAdapter(adapter);
    }
  }

  @Override
  @SuppressLint("NewApi")
  public int getNumColumns() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
      return super.getNumColumns();
    }

    // Return value for less than Honeycomb.
    return mNumColmuns;
  }

  @Override
  public void setNumColumns(int numColumns) {
    super.setNumColumns(numColumns);
    // Store specified value for less than Honeycomb.
    mRequestedNumColumns = numColumns;
  }

  private static class FixedViewInfo {

    public View view;
    public ViewGroup viewContainer;
    public Object data;
    public boolean isSelectable;
  }

  private static class NestedAdapter implements WrapperListAdapter,
      Filterable {

    private final DataSetObservable mDataSetObservable = new DataSetObservable();
    private final ListAdapter mAdapter;
    private final boolean mIsFilterable;
    ArrayList<FixedViewInfo> mHeaderInfos;
    ArrayList<FixedViewInfo> mFooterInfos;

    boolean mAreAllFixedViewsSelectable;
    private int mNumColumns = 1;

    public NestedAdapter(ArrayList<FixedViewInfo> headerInfos,
        ArrayList<FixedViewInfo> footerInfos, ListAdapter adapter) {
      mAdapter = adapter;
      mIsFilterable = adapter instanceof Filterable;

      // Validate and set list header view
      if (headerInfos == null) {
        throw new IllegalArgumentException(
            "headerViewInfos cannot be null");
      }
      mHeaderInfos = headerInfos;

      // Validate and set list footer view
      if (footerInfos == null) {
        throw new IllegalArgumentException(
            "footerViewInfos cannot be null");
      }
      mFooterInfos = footerInfos;

      notifyChanged();
    }

    private void notifyChanged() {
      mAreAllFixedViewsSelectable = (areAllListInfosSelectable(mHeaderInfos)
          && areAllListInfosSelectable(mFooterInfos));
      mDataSetObservable.notifyChanged();
    }

    public int getHeadersCount() {
      return mHeaderInfos.size();
    }

    public int getHeadersSlotCount() {
      return getHeadersCount() * mNumColumns;
    }

    public int getFootersCount() {
      return mFooterInfos.size();
    }

    public int getFootersSlotCount() {
      return (getFootersCount() * mNumColumns);
    }

    @Override
    public boolean isEmpty() {
      return (mAdapter == null || mAdapter.isEmpty())
          && getHeadersCount() == 0 && getFootersCount() == 0;
    }

    public void setNumColumns(int numColumns) {
      if (numColumns < 1) {
        throw new IllegalArgumentException(
            "Number of columns must be 1 or more");
      }

      if (mNumColumns != numColumns) {
        mNumColumns = numColumns;
        notifyDataSetChanged();
      }
    }

    private boolean areAllListInfosSelectable(ArrayList<FixedViewInfo> infos) {
      if (infos != null) {
        for (FixedViewInfo info : infos) {
          if (!info.isSelectable) {
            return false;
          }
        }
      }
      return true;
    }

    public boolean removeHeader(View v) {
      for (int i = 0; i < mHeaderInfos.size(); i++) {
        FixedViewInfo info = mHeaderInfos.get(i);
        if (info.view == v) {
          info.viewContainer.removeView(v);
          mHeaderInfos.remove(i);
          notifyChanged();
          return true;
        }
      }
      return false;
    }

    public boolean removeFooter(View v) {
      for (int i = 0; i < mFooterInfos.size(); i++) {
        FixedViewInfo info = mFooterInfos.get(i);
        if (info.view == v) {
          info.viewContainer.removeView(v);
          mFooterInfos.remove(i);
          notifyChanged();
          return true;
        }
      }
      return false;
    }

    @Override
    public int getCount() {
      int fixedViewSlotCount = getHeadersSlotCount()
          + getFootersSlotCount();
      if (mAdapter != null) {
        final int lastRowItemCount = (mAdapter.getCount() % mNumColumns);
        final int emptyItemCount = ((lastRowItemCount == 0) ? 0
            : mNumColumns - lastRowItemCount);
        return fixedViewSlotCount + mAdapter.getCount()
            + emptyItemCount;
      } else {
        return fixedViewSlotCount;
      }
    }

    @Override
    public boolean areAllItemsEnabled() {
      if (mAdapter != null) {
        return mAreAllFixedViewsSelectable
            && mAdapter.areAllItemsEnabled();
      } else {
        return true;
      }
    }

    @Override
    public boolean isEnabled(int position) {
      // Position in header view area
      final int headerSlotCount = getHeadersSlotCount();
      final int colum = position % mNumColumns;
      if (position < headerSlotCount) {
        final int headerRow = position / mNumColumns;
        return (colum == 0) && mHeaderInfos.get(headerRow).isSelectable;
      }

      int count = 0;
      if (mAdapter != null) {
        count = mAdapter.getCount();
      }

      // Position in adapter item view area
      if (position < headerSlotCount + count) {
        final int adjPosition = position - headerSlotCount;
        return mAdapter.isEnabled(adjPosition);
      }

      // Position in last row view area
      final int lastRowItemCount = (count % mNumColumns);
      final int emptyItemCount = ((lastRowItemCount == 0) ? 0
          : mNumColumns - lastRowItemCount);
      if (position < headerSlotCount + count + emptyItemCount) {
        return false;
      }

      // Position in footer view area
      int footerSlotCount = getFootersSlotCount();
      if (position < headerSlotCount + count + emptyItemCount
          + footerSlotCount) {
        return (colum == 0)
            && mFooterInfos
            .get((position - headerSlotCount - count - emptyItemCount)
                / mNumColumns).isSelectable;
      }

      throw new ArrayIndexOutOfBoundsException(position);
    }

    @Override
    public Object getItem(int position) {
      // Position in header view area
      int headerSlotCount = getHeadersSlotCount();
      final int colum = position % mNumColumns;
      if (position < headerSlotCount) {
        final int headerRow = position / mNumColumns;
        if (colum == 0) {
          return mHeaderInfos.get(headerRow).data;
        }
        return null;
      }

      int count = 0;
      if (mAdapter != null) {
        count = mAdapter.getCount();
      }

      // Position in adapter item view area
      if (position < headerSlotCount + count) {
        final int adjPosition = position - headerSlotCount;
        if (adjPosition < count) {
          return mAdapter.getItem(adjPosition);
        }
      }

      // Position in empty view area
      final int lastRowItemCount = (count % mNumColumns);
      final int emptyItemCount = ((lastRowItemCount == 0) ? 0
          : mNumColumns - lastRowItemCount);
      if (position < headerSlotCount + count + emptyItemCount) {
        return null;
      }

      // Position in footer view area
      int footerSlotCount = getFootersSlotCount();
      final int footerPosition = position - headerSlotCount + count
          + emptyItemCount;
      if (footerPosition < footerSlotCount) {
        if (colum == 0) {
          return mFooterInfos.get(footerPosition / mNumColumns).data;
        }
      }

      throw new ArrayIndexOutOfBoundsException(position);
    }

    @Override
    public long getItemId(int position) {
      final int adapterStartIndex = getHeadersSlotCount();
      if (mAdapter != null) {
        if (position >= adapterStartIndex
            && position < adapterStartIndex + mAdapter.getCount()) {
          int adjPosition = position - adapterStartIndex;
          return mAdapter.getItemId(adjPosition);
        }
      }
      return -1;
    }

    @Override
    public boolean hasStableIds() {
      if (mAdapter != null) {
        return mAdapter.hasStableIds();
      }
      return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      // Position in header view area
      int headerSlotCount = getHeadersSlotCount();
      if (position < headerSlotCount) {
        View headerViewContainer = mHeaderInfos.get(position
            / mNumColumns).viewContainer;
        if (position % mNumColumns == 0) {
          headerViewContainer.setX(0);
          return headerViewContainer;
        } else {
          convertView = new View(parent.getContext());
          convertView.setMinimumHeight(headerViewContainer
              .getHeight());
          convertView.setVisibility(View.INVISIBLE);
          return convertView;
        }
      }

      int count = 0;
      if (mAdapter != null) {
        count = mAdapter.getCount();
      }
      // Position in adapter item view area
      if (position < headerSlotCount + count) {
        final int adjPosition = position - headerSlotCount;
        if (adjPosition < count) {
          convertView = mAdapter.getView(adjPosition, convertView,
              parent);
          convertView.setVisibility(View.VISIBLE);
          return convertView;
        }
      }

      // Position in empty view area
      final int lastRowItemCount = (count % mNumColumns);
      final int emptyItemCount = ((lastRowItemCount == 0) ? 0
          : mNumColumns - lastRowItemCount);
      if (position < headerSlotCount + count + emptyItemCount) {
        convertView = mAdapter.getView(count - 1, convertView, parent);
        convertView.setVisibility(View.INVISIBLE);
        return convertView;
      }

      // Position in footer view area
      int footerSlotCount = getFootersSlotCount();
      final int beforeFooterCount = headerSlotCount + count
          + emptyItemCount;
      final int totalItem = beforeFooterCount + footerSlotCount;
      if (position < totalItem) {
        final int footerIndex = (position - beforeFooterCount)
            / mNumColumns;
        FixedViewInfo viewInfo = mFooterInfos.get(footerIndex);

        View footerViewContainer = viewInfo.viewContainer;
        if (position % mNumColumns == 0) {
          footerViewContainer.setX(0);
          return footerViewContainer;
        } else {
          convertView = new View(parent.getContext());
          convertView.setMinimumHeight(footerViewContainer
              .getHeight());
          convertView.setVisibility(View.INVISIBLE);
          return convertView;
        }
      }

      throw new ArrayIndexOutOfBoundsException(position);
    }

    @Override
    public int getItemViewType(int position) {
      int numHeadersAndPlaceholders = getHeadersCount() * mNumColumns;
      if (position < numHeadersAndPlaceholders
          && (position % mNumColumns != 0)) {
        // Placeholders get the last view type number
        return mAdapter != null ? mAdapter.getViewTypeCount() : 1;
      }
      if (mAdapter != null
          && position >= numHeadersAndPlaceholders
          && position < numHeadersAndPlaceholders
          + mAdapter.getCount()
          + (mNumColumns - (mAdapter.getCount() % mNumColumns))) {
        int adjPosition = position - numHeadersAndPlaceholders;
        int adapterCount = mAdapter.getCount();
        if (adjPosition < adapterCount) {
          return mAdapter.getItemViewType(adjPosition);
        } else if (adapterCount != 0 && mNumColumns != 1) {
          return mAdapter.getItemViewType(adapterCount - 1);
        }
      }
      int numFootersAndPlaceholders = getFootersCount() * mNumColumns;
      if (mAdapter != null
          && position < numHeadersAndPlaceholders
          + mAdapter.getCount() + numFootersAndPlaceholders) {
        return mAdapter != null ? mAdapter.getViewTypeCount() : 1;
      }

      return AdapterView.ITEM_VIEW_TYPE_HEADER_OR_FOOTER;
    }

    @Override
    public int getViewTypeCount() {
      if (mAdapter != null) {
        return mAdapter.getViewTypeCount() + 1;
      }
      return 2;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
      mDataSetObservable.registerObserver(observer);
      if (mAdapter != null) {
        mAdapter.registerDataSetObserver(observer);
      }
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
      mDataSetObservable.unregisterObserver(observer);
      if (mAdapter != null) {
        mAdapter.unregisterDataSetObserver(observer);
      }
    }

    @Override
    public Filter getFilter() {
      if (mIsFilterable) {
        return ((Filterable) mAdapter).getFilter();
      }
      return null;
    }

    @Override
    public ListAdapter getWrappedAdapter() {
      return mAdapter;
    }

    public void notifyDataSetChanged() {
      mDataSetObservable.notifyChanged();
    }
  }

  private class FullWidthFixedViewLayout extends FrameLayout {

    public FullWidthFixedViewLayout(Context context) {
      super(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
      int targetWidth = GridViewWithHeaderAndFooter.this
          .getMeasuredWidth()
          - GridViewWithHeaderAndFooter.this.getPaddingLeft()
          - GridViewWithHeaderAndFooter.this.getPaddingRight();
      widthMeasureSpec = MeasureSpec.makeMeasureSpec(targetWidth,
          MeasureSpec.getMode(widthMeasureSpec));
      super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
  }
}