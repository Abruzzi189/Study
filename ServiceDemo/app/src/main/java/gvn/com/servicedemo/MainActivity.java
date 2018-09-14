package gvn.com.servicedemo;

import android.content.Intent;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
  public static final String THANGTHANG = "123";
  private PopupWindow mPopupChatMoreOptions;
  ChatMoreLayout chatMoreLayout;
  LinearLayout linearLayout;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Button button = findViewById(R.id.btnClick);
    linearLayout = findViewById(R.id.llLayout);
    button.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent intent = new Intent(MainActivity.this,MyIntentService.class);
        Log.e("ThangPham","startService");
        startService(intent);
      }
    });


    SurfaceView surfaceView = findViewById(R.id.sfView);
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
      surfaceView.getHolder().setType(
          SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

  }
  private void showChatMoreOptions() {
    chatMoreLayout = new ChatMoreLayout(this);

    mPopupChatMoreOptions = new PopupWindow(chatMoreLayout,
        android.view.ViewGroup.LayoutParams.MATCH_PARENT,
        android.view.ViewGroup.LayoutParams.WRAP_CONTENT, false);

    mPopupChatMoreOptions.showAsDropDown(linearLayout);

  }
}
