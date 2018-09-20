package com.application.adapters;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import com.application.entity.Template;
import com.application.event.TemplateEvent;
import com.application.navigationmanager.NavigationManager;
import com.application.ui.AddOrUpdateTemplateFragment;
import com.application.ui.CenterButtonDialogBuilder;
import com.application.ui.TemplateFragment;
import com.application.util.preferece.ChatMessagePreference;
import de.greenrobot.event.EventBus;
import glas.bbsystem.R;
import java.util.ArrayList;


public class TemplateAdapter extends BaseExpandableListAdapter {

  private static final int DELAY_TIME = 200;
  private Context mContext;
  private ArrayList<Template> mListData;
  private NavigationManager mNavigationManager;
  private AlertDialog mAlertDialog;
  private int mStyle;
  private String mFriendId;
  private long mLastClickTime = 0;

  public TemplateAdapter(Context context, ArrayList<Template> listChildData,
      NavigationManager navigationManager, int style, String friendId) {
    this.mContext = context;
    this.mListData = listChildData;
    this.mNavigationManager = navigationManager;
    this.mStyle = style;
    this.mFriendId = friendId;
  }

  @Override
  public Object getChild(int groupPosition, int childPosititon) {
    return this.mListData.get(groupPosition);
  }

  @Override
  public long getChildId(int groupPosition, int childPosition) {
    return childPosition;
  }

  @Override
  public View getChildView(int groupPosition, final int childPosition,
      boolean isLastChild, View convertView, ViewGroup parent) {
    final Template template = mListData.get(groupPosition);
    final String content = template.getTempContent();

    if (convertView == null) {
      LayoutInflater infalInflater = (LayoutInflater) this.mContext
          .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      convertView = infalInflater.inflate(R.layout.item_child_template,
          null);
    }

    TextView tvContent = (TextView) convertView
        .findViewById(R.id.tv_content);
    tvContent.setText(content);

    TextView btnInsert = (TextView) convertView.findViewById(R.id.btn_insert);
    TextView btnEdit = (TextView) convertView.findViewById(R.id.btn_edit);

    // Edit template
    btnEdit.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < DELAY_TIME) {
          return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        mNavigationManager.addPage(AddOrUpdateTemplateFragment
            .newInstance(AddOrUpdateTemplateFragment.FUNCTION_UPDATE, template));
      }
    });

    if (mStyle == TemplateFragment.STYLE_CHAT) {
      btnInsert.setVisibility(View.VISIBLE);
      btnInsert.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View v) {
          if (SystemClock.elapsedRealtime() - mLastClickTime < DELAY_TIME) {
            return;
          }
          mLastClickTime = SystemClock.elapsedRealtime();
          ChatMessagePreference preference = ChatMessagePreference.getInstance();
          if (preference.getMessage(mFriendId) == null) {
            preference.saveMessage(mFriendId, template.getTempContent());
          } else {
            preference.saveMessage(mFriendId,
                preference.getMessage(mFriendId) + template.getTempContent());
          }
          mNavigationManager.goBack();
        }
      });
    } else {
      btnInsert.setVisibility(View.GONE);
      LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
          LayoutParams.MATCH_PARENT,
          LayoutParams.MATCH_PARENT, 2.0f);
      btnEdit.setLayoutParams(params);
    }

    return convertView;
  }

  @Override
  public int getChildrenCount(int groupPosition) {
    return 1;
  }

  @Override
  public Object getGroup(int groupPosition) {
    return this.mListData.get(groupPosition);
  }

  @Override
  public int getGroupCount() {
    return this.mListData.size();
  }

  @Override
  public long getGroupId(int groupPosition) {
    return groupPosition;
  }

  @Override
  public View getGroupView(final int groupPosition, boolean isExpanded,
      View convertView, ViewGroup parent) {
    final Template template = mListData.get(groupPosition);
    final String title = template.getTempTitle();

    if (convertView == null) {
      LayoutInflater infalInflater = (LayoutInflater) this.mContext
          .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      convertView = infalInflater.inflate(R.layout.item_header_template,
          null);
    }

    TextView tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
    tvTitle.setText(title);

    TextView btnDelete = (TextView) convertView.findViewById(R.id.btn_delete);
    btnDelete.setFocusable(false);
    btnDelete.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < DELAY_TIME) {
          return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        showDialogConfirmDelete(template, groupPosition);
      }
    });

    return convertView;
  }

  private void showDialogConfirmDelete(final Template template, final int position) {
    Builder builder = new CenterButtonDialogBuilder(mContext, true);
    builder.setMessage(R.string.template_confirm_delete);
    builder.setPositiveButton(R.string.common_yes,
        new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            EventBus.getDefault().post(new TemplateEvent(TemplateEvent.DELETE, template, position));
          }
        });
    builder.setNegativeButton(R.string.common_no,
        new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            mAlertDialog.dismiss();
          }
        });
    mAlertDialog = builder.create();
    mAlertDialog.show();
  }

  @Override
  public boolean hasStableIds() {
    return false;
  }

  @Override
  public boolean isChildSelectable(int groupPosition, int childPosition) {
    return true;
  }
}