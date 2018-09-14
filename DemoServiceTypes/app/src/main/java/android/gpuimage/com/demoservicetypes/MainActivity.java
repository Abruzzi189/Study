package android.gpuimage.com.demoservicetypes;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    public ServiceConnection serviceConnection;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button1 = findViewById(R.id.btnClick1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //unbound service
                Intent intent = new Intent(MainActivity.this,MyService.class);
                startForegroundService(intent);

            }
        });
        Button button2 = findViewById(R.id.btnClick2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //unbound service
                Intent intent = new Intent(MainActivity.this,MyService.class);
                intent.putExtra("123","123");
                bindService(intent,serviceConnection,BIND_AUTO_CREATE);
            }
        });
        Button button3 = findViewById(R.id.btnClick3);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //unbound service
                Intent intent = new Intent(MainActivity.this,MyService.class);
//                unbindService(serviceConnection);
                stopService(intent);
            }
        });
         serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {

            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
    }

}
