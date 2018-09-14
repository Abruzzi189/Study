package gvn.com.bedge;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import me.leolin.shortcutbadger.ShortcutBadger;

public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);setContentView(R.layout.activity_main);
    int count =1;
    ShortcutBadger.applyCount(this,count);
  }

}
