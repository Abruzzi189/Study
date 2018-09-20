package com.application.ui.hotpage;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import com.application.connection.request.CircleImageRequest;
import com.application.entity.NewPostItem;
import com.application.ui.customeview.CircleImageView;
import com.application.util.preferece.UserPreferences;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import glas.bbsystem.R;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ThoNh on 12/12/2017.
 */

public class HotPagerNewPostAdapter extends
    RecyclerView.Adapter<HotPagerNewPostAdapter.ViewHolder> {

  private static final String TAG = HotPagerNewPostAdapter.class.getSimpleName();
  private List<NewPostItem> mData = new ArrayList<>();
  private ProgressBar mRefreshView;
  private RecyclerView mRecyclerView;
  private IOnNewPostListener mListener;
  private LinearHorizontalLayoutManager mLayoutManager;
  private LinearLayout mLinearLayout;
  private boolean isEnableSwipeLeftToRight;
  private boolean isRefreshing = false;
  private boolean isLoading = false;
  private int pointDownX;
  private NewPostItem mItemLoad = null;

  public HotPagerNewPostAdapter(LinearLayout linearLayout, RecyclerView recyclerView,
      LinearHorizontalLayoutManager layoutManager,
      ProgressBar refreshView, IOnNewPostListener listener) {
    this.mRecyclerView = recyclerView;
    this.mRefreshView = refreshView;
    this.mLayoutManager = layoutManager;
    this.mListener = listener;
    this.mLinearLayout = linearLayout;

    initRecyclerViewRefresh();
    initRecyclerViewLoadMore();
  }

  public void setData(List<NewPostItem> data) {
    if (data != null && !data.isEmpty()) {
      int startPosition = mData.size();
      this.mData.addAll(data);
      notifyItemRangeInserted(startPosition, mData.size() - 1);
    }
  }

  public void clearData() {
    if (mData != null && !mData.isEmpty()) {
      mData.clear();
      notifyDataSetChanged();
    }
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return new ViewHolder(LayoutInflater.from(parent.getContext())
        .inflate(R.layout.item_new_post_hot_page, parent, false));
  }

  @Override
  public void onBindViewHolder(ViewHolder holder, final int position) {

    // load avatar that has cover by gender
    if (mData.get(position) != null) {
      holder.mImageView.setVisibility(View.VISIBLE);
      holder.mProgressBar.setVisibility(View.GONE);
      String token = UserPreferences.getInstance().getToken();
      CircleImageRequest imageRequest = new CircleImageRequest(token, mData.get(position).avaId);
      Glide.with(holder.mImageView.getContext())
          .load(imageRequest.toURL())
          .apply(RequestOptions.placeholderOf(R.drawable.dummy_circle_avatar))
          .into(holder.mImageView);

      holder.itemView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          if (mListener != null) {
            mListener.onNewPostClick(mData.get(position), position);
          }
        }
      });

    } else {
      holder.mImageView.setVisibility(View.GONE);
      holder.mProgressBar.setVisibility(View.VISIBLE);
    }
  }

  @Override
  public int getItemCount() {
    return mData.size();
  }

  private void initRecyclerViewLoadMore() {

    mRefreshView.postOnAnimation(new Runnable() {
      @Override
      public void run() {
        mRefreshView.animate().translationX(-mRefreshView.getWidth()).setDuration(0).start();
      }
    });

    mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override
      public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        int firstVisibleItem = mLayoutManager.findFirstCompletelyVisibleItemPosition();
        isEnableSwipeLeftToRight = firstVisibleItem == 0;

        int lastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
        if (lastVisibleItem + 1 >= mData.size() && !isLoading) {
          // last item
          mData.add(mItemLoad);
          notifyDataSetChanged();
          isLoading = true;
          mListener.startLoadMore();
        }
      }
    });
  }

  private void initRecyclerViewRefresh() {

    mRecyclerView.setOnTouchListener(new View.OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {

        if (isEnableSwipeLeftToRight && !isRefreshing) {

          mLayoutManager.setScrollEnable(false);

          switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
              int delta = (int) (event.getX() - pointDownX);
              if (delta < 800) {
                mRefreshView.animate().translationX(delta - mRefreshView.getWidth()).setDuration(0)
                    .start();
                float alpha = (1.0f - (float) delta / 800);
                mRecyclerView.setAlpha(alpha);
              } else {
                startRefresh();
                return true;
              }

              break;
            case MotionEvent.ACTION_UP:
              int delta2 = (int) (event.getX() - pointDownX);

              if (delta2 < 500) {
                refreshComplete();

              } else if (delta2 > 500) {
                startRefresh();
              }

              break;
          }
        }
        return false;
      }
    });

    mLinearLayout.setOnTouchListener(new View.OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {

        if (isEnableSwipeLeftToRight && !isRefreshing) {
          switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
              pointDownX = (int) event.getX();
              break;
          }
        }
        return false;
      }
    });
  }

  /**
   * Call when retrive data success from server
   */
  public void refreshComplete() {
    mRefreshView.animate().translationX(-mRefreshView.getWidth()).setDuration(200).start();
    mRecyclerView.setAlpha(1.0f);
    mLayoutManager.setScrollEnable(true);
    isRefreshing = false;
  }

  public void startRefresh() {
    mRefreshView.post(new Runnable() {
      @Override
      public void run() {
        mRefreshView.animate().translationX(0).setDuration(200).start();
        isRefreshing = true;
        mLayoutManager.setScrollEnable(false);
        mRecyclerView.setAlpha(0.1f);
      }
    });

    if (mListener != null) {
      mListener.startRefresh();

    }
    isLoading = false;
  }

  public void loadMoreComplete(List<NewPostItem> data) {
    this.mData.remove(mItemLoad);
    isLoading = false;
    if (data != null) {
      this.mData.addAll(data);
      notifyDataSetChanged();
    }
  }

  public interface IOnNewPostListener {

    void startRefresh();

    void startLoadMore();

    void onNewPostClick(NewPostItem item, int position);
  }

  public class ViewHolder extends RecyclerView.ViewHolder {

    CircleImageView mImageView;
    ProgressBar mProgressBar;

    public ViewHolder(View itemView) {
      super(itemView);
      mImageView = (CircleImageView) itemView.findViewById(R.id.item_image);
      mProgressBar = (ProgressBar) itemView.findViewById(R.id.progress_bar);
    }
  }
}
