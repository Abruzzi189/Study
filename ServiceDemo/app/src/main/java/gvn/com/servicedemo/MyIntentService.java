package gvn.com.servicedemo;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

public class MyIntentService extends IntentService {

  public MyIntentService(String name) {
    super("123");
  }
  public MyIntentService() {
    super("123");
  }

  @Override
  protected void onHandleIntent(@Nullable Intent intent) {
    Log.e("ThangPham","start intent service");
  }
}
