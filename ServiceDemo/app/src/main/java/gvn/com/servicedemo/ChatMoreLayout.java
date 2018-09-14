package gvn.com.servicedemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

public class ChatMoreLayout extends LinearLayout {

  public ChatMoreLayout(Context context) {
    super(context);
    LayoutInflater inflater = (LayoutInflater) context
        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    inflater.inflate(R.layout.chat_more_option, this, true);
  }
}
