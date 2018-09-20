package com.application.ui.customeview;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;
import com.application.constant.UserSetting;
import com.application.util.preferece.UserPreferences;
import glas.bbsystem.R;


public class NewNotificationsDialog extends Dialog {

  private static Context mContext;

  public int mTotalNewNotifications = 0;

  public NewNotificationsDialog(Context context) {
    super(context);
    mContext = context;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    setContentView(R.layout.fragment_new_notifications);
    getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

  }

  @Override
  protected void onStart() {
    super.onStart();
    TextView tvTotalNewNotifications = (TextView) findViewById(
        R.id.tv_fragment_new_notifications_total);
    tvTotalNewNotifications.setText(String.format(
        mContext.getString(R.string.new_notifications),
        mTotalNewNotifications));
    TextView txtTitle = (TextView) findViewById(R.id.title);
    int gender = UserPreferences.getInstance().getGender();
    if (gender == UserSetting.GENDER_FEMALE) {
      txtTitle.setText(R.string.new_notifications_welcome_back_female);
    } else {
      txtTitle.setText(R.string.new_notifications_welcome_back_male);
    }
    //hiepuh
//		Button btDone = (Button) findViewById(R.id.bt_fragment_new_notifications_done);
//		btDone.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				dismiss();
//			}
//		});
    //end
  }
}
