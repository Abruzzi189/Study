package com.application.ui.meetpeople;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.application.ui.BaseFragment;
import com.application.ui.customeview.pulltorefresh.PullToRefreshBase;
import com.application.ui.customeview.pulltorefresh.PullToRefreshBase.Mode;
import com.application.ui.customeview.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.application.ui.customeview.pulltorefresh.PullToRefreshListView;
import com.application.util.LogUtils;
import com.application.util.preferece.UserPreferences;
import glas.bbsystem.R;
import java.util.ArrayList;
import java.util.List;


public class SelectMessageFragment extends BaseFragment {

  private View mView;
  private ListView mMessagesListView;
  private View mMessagesListFooter;
  private MessagesListAdapter mMessagesListAdapter;
  private PullToRefreshListView mPullToRefreshListView;
  private int mSkip = 0;
  private int mTake;
  private String[] mWinkMessages;
  private OnRefreshListener2<ListView> onRefreshListener = new OnRefreshListener2<ListView>() {
    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
      LogUtils.d(TAG, "onRefreshListener.onPullDownToRefresh Started");

      mMessagesListFooter.setVisibility(View.GONE);
      mPullToRefreshListView.setMode(Mode.BOTH);
      Resources resource = getResources();
      mPullToRefreshListView.setPullLabelFooter(resource
          .getString(R.string.pull_to_load_more_pull_label));
      mPullToRefreshListView.setReleaseLabelFooter(resource
          .getString(R.string.pull_to_load_more_release_label));

      mMessagesListAdapter.clearAllData();
      mSkip = 0;
      LongOperation lo = new LongOperation();
      lo.execute();

      LogUtils.d(TAG, "onRefreshListener.onPullDownToRefresh Ended");
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
      LogUtils.d(TAG, "onRefreshListener.onPullUpToRefresh Started");

      mSkip = mMessagesListAdapter.getCount();
      LongOperation lo = new LongOperation();
      lo.execute();

      LogUtils.d(TAG, "onRefreshListener.onPullUpToRefresh Ended");
    }
  };

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mTake = getResources()
        .getInteger(R.integer.take_select_mesage_fragment);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);

    mView = inflater.inflate(R.layout.fragment_select_message, container,
        false);

    initView(mView);

    return mView;
  }

  @Override
  protected void resetNavigationBar() {
    super.resetNavigationBar();
    getNavigationBar().setNavigationLeftLogo(R.drawable.nav_btn_back);
    getNavigationBar().setCenterTitle(
        R.string.meet_people_wink_bomb_select_message);
    getNavigationBar().setNavigationRightVisibility(View.GONE);
    getNavigationBar().setShowUnreadMessage(false);
  }

  @Override
  public void onNavigationLeftClick(View view) {
    super.onNavigationLeftClick(view);
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    mMessagesListAdapter = new MessagesListAdapter(getActivity());
    mMessagesListView.setAdapter(mMessagesListAdapter);
    mMessagesListView.setDividerHeight(0);
    mMessagesListView.setCacheColorHint(Color.TRANSPARENT);

    mSkip = 0;
    LongOperation lo = new LongOperation();
    lo.execute();
  }

  private void initView(View view) {
    mPullToRefreshListView = (PullToRefreshListView) view
        .findViewById(R.id.fr_message_list);
    mMessagesListView = mPullToRefreshListView.getRefreshableView();
    mPullToRefreshListView.setOnRefreshListener(onRefreshListener);
    mPullToRefreshListView.getLoadingLayoutProxy(true, false);
    mMessagesListView
        .setBackgroundResource(R.drawable.bg_common_layout_border);
    mMessagesListFooter = View.inflate(getActivity(),
        R.layout.common_list_no_more_items, null);
    mMessagesListView.addFooterView(mMessagesListFooter);
    mMessagesListFooter.setVisibility(View.GONE);

    mMessagesListView.setOnScrollListener(new OnScrollListener() {
      @Override
      public void onScrollStateChanged(AbsListView view, int scrollState) {
      }

      @Override
      public void onScroll(AbsListView view, int firstVisibleItem,
          int visibleItemCount, int totalItemCount) {
      }
    });

    mWinkMessages = getResources().getStringArray(R.array.wink_message);
  }

  private static class ViewHolder {

    public TextView textViewMessage;
  }

  private class LongOperation extends
      AsyncTask<String, Void, ArrayList<String>> {

    @Override
    protected ArrayList<String> doInBackground(String... params) {
      ArrayList<String> result = new ArrayList<String>();

      if (mWinkMessages != null) {
        int count = 0;
        for (int i = mSkip; i < mWinkMessages.length; i++) {
          try {
            Thread.sleep(50);
            result.add(mWinkMessages[i]);
            count++;
          } catch (InterruptedException ie) {
            ie.printStackTrace();
          }

          if (count == mTake) {
            break;
          }
        }
      }

      return result;
    }

    @Override
    protected void onPostExecute(ArrayList<String> messages) {
      boolean endOfList = false;

      mMessagesListAdapter.appendList(messages);

      if (messages.size() == 0) {
        endOfList = true;
      }

      if (mPullToRefreshListView != null) {
        mPullToRefreshListView.onRefreshComplete();
      }

      if (endOfList) {
        mMessagesListFooter.setVisibility(View.VISIBLE);
        mPullToRefreshListView.setMode(Mode.PULL_FROM_START);
      }
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected void onProgressUpdate(Void... values) {
    }
  }

  protected class MessagesListAdapter extends BaseAdapter {

    private List<String> mMessagesList = new ArrayList<String>();
    private Context mContext;
    private LayoutInflater mLayoutInflater;

    public MessagesListAdapter(Context context) {
      mContext = context;
      mLayoutInflater = (LayoutInflater) mContext
          .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
      return mMessagesList.size();
    }

    @Override
    public Object getItem(int position) {
      return mMessagesList.get(position);
    }

    @Override
    public long getItemId(int position) {
      return position;
    }

    public void appendList(List<String> messages) {
      mMessagesList.addAll(messages);
      this.notifyDataSetChanged();
    }

    public void clearAllData() {
      mMessagesList.clear();
      this.notifyDataSetChanged();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
      ViewHolder holder = null;

      if (convertView == null) {
        holder = new ViewHolder();
        convertView = mLayoutInflater.inflate(
            R.layout.item_list_wink_messages, parent, false);
        holder.textViewMessage = (TextView) convertView
            .findViewById(R.id.tv_item_list_wink_messages_message);
        convertView.setTag(holder);
      } else {
        holder = (ViewHolder) convertView.getTag();
      }

      holder.textViewMessage.setText(mMessagesList.get(position));

      holder.textViewMessage.setTag(position);
      holder.textViewMessage.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View v) {
          int position = Integer.parseInt(v.getTag().toString());
          UserPreferences.getInstance()
              .saveWinkMessageIndex(position);

          onNavigationLeftClick(mView);
        }
      });

      return convertView;
    }
  }
}
