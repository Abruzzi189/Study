package android.gpuimage.com.notification;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    //region variable
    private static final String TAG = "MainActivity";
    RecyclerView rvListSong;
    List<MusicModel> listMusic;
    ListMusicAdapter mListMusicAdapter;
    SwipeRefreshLayout swipeRefreshLayout;
    public static MusicModel saveMusicModel;
    //endregion

    //region function
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupUI();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                // implement Handler to wait for 3 seconds and then update UI means update value of TextView
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // cancle the Visual indication of a refresh
                        swipeRefreshLayout.setRefreshing(false);
                        // Generate a random integer number
                        listMusic =null;
                        setData();
                        mListMusicAdapter.notifyDataSetChanged();
                    }
                }, 1000);
            }
        });
        setData();

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (MusicHandler.mediaPlayer != null) {
            MusicHandler.stopHandler();
            NotificationMusic.notificationManager.cancelAll();

        }
    }

    private void setData() {
        listMusic = new ArrayList<>();

        MusicModel musicModel1 = new MusicModel("Người âm phủ", this.getResources().getIdentifier("song1", "raw", getPackageName()), "http://data.chiasenhac.com/data/cover/87/86355.jpg");
        MusicModel musicModel2 = new MusicModel("Đừng như thói quen", this.getResources().getIdentifier("song2", "raw", getPackageName()), "https://zmp3-photo.zadn.vn/thumb/240_240/cover/e/4/9/3/e493fc8488a6989f0f08b711ea1dbbdd.jpg");
        MusicModel musicModel3 = new MusicModel("Cô Gái M52", this.getResources().getIdentifier("song3", "raw", getPackageName()), "https://i.ytimg.com/vi/_RRdDAj_Oj0/mqdefault.jpg");
        MusicModel musicModel4 = new MusicModel("Cô Gái M52", this.getResources().getIdentifier("song3", "raw", getPackageName()), "https://i.ytimg.com/vi/_RRdDAj_Oj0/mqdefault.jpg");
        MusicModel musicModel5 = new MusicModel("Cô Gái M52", this.getResources().getIdentifier("song3", "raw", getPackageName()), "https://i.ytimg.com/vi/_RRdDAj_Oj0/mqdefault.jpg");
        MusicModel musicModel6 = new MusicModel("Cô Gái M52", this.getResources().getIdentifier("song3", "raw", getPackageName()), "https://i.ytimg.com/vi/_RRdDAj_Oj0/mqdefault.jpg");
        MusicModel musicModel7 = new MusicModel("Cô Gái M52", this.getResources().getIdentifier("song3", "raw", getPackageName()), "https://i.ytimg.com/vi/_RRdDAj_Oj0/mqdefault.jpg");
        MusicModel musicModel8 = new MusicModel("Cô Gái M52", this.getResources().getIdentifier("song3", "raw", getPackageName()), "https://i.ytimg.com/vi/_RRdDAj_Oj0/mqdefault.jpg");
        MusicModel musicModel9 = new MusicModel("Cô Gái M52", this.getResources().getIdentifier("song3", "raw", getPackageName()), "https://i.ytimg.com/vi/_RRdDAj_Oj0/mqdefault.jpg");

        listMusic.add(musicModel1);
        listMusic.add(musicModel2);
        listMusic.add(musicModel3);
        listMusic.add(musicModel4);
        listMusic.add(musicModel5);
        listMusic.add(musicModel6);
        listMusic.add(musicModel7);
        listMusic.add(musicModel8);
        listMusic.add(musicModel8);
        listMusic.add(musicModel8);
        listMusic.add(musicModel8);
        listMusic.add(musicModel8);
        listMusic.add(musicModel8);
        listMusic.add(musicModel8);
        listMusic.add(musicModel8);
        listMusic.add(musicModel8);
        listMusic.add(musicModel8);

        mListMusicAdapter = new ListMusicAdapter(listMusic, this);
        rvListSong.setLayoutManager(new LinearLayoutManager(this));
        rvListSong.setAdapter(mListMusicAdapter);

    }

    private void setupUI() {
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.simpleSwipeRefreshLayout);
        rvListSong = findViewById(R.id.rv_list_song);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG, "onStop: ");
        if (MusicHandler.mediaPlayer != null) {
            NotificationMusic.setupNewNotification(this,saveMusicModel);
        }

    }
    //endregion


}
