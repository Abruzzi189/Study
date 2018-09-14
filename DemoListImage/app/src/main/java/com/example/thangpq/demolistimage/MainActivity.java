package com.example.thangpq.demolistimage;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.MergeCursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btnClick = findViewById(R.id.btnCLick);
        initPermission();
        btnClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getListAllImage();
            }
        });
    }

    private void getListAllImage() {
        GetMediaFileAsyn getMediaFileAsyn = new GetMediaFileAsyn();
        getMediaFileAsyn.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,this);
    }
    public void initPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {


                //Register permission
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO,Manifest.permission.CAMERA}, 1);

            }
        }
    }
    private class GetMediaFileAsyn extends AsyncTask<Context, Void, List<MediaFile>>
    {
        public String[] projection;

        public GetMediaFileAsyn() {
            // projection: get all column in db
            projection = new String[]{
                    MediaStore.Video.Media.TITLE, // title
                    MediaStore.Video.Media.DATA, // path
                    MediaStore.Video.Media.BUCKET_DISPLAY_NAME, //folder
                    MediaStore.Video.Media.DATE_TAKEN, //time
                    MediaStore.Video.Media.MIME_TYPE // long
            };

        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected List<MediaFile> doInBackground(Context... contexts) {
            Context context = contexts[0];
            List<MediaFile> mediaFiles = new ArrayList<>();

            //query data by Cursor
            Cursor cursor = new MergeCursor(new Cursor[]
            {
                    //truy van du lieu o trong may, merge ca audio va image
                context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, null)
//                context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,projection,null,null,null)

            }) {
            };
            if(cursor.moveToFirst())
            {
                int colName = cursor.getColumnIndex(MediaStore.Video.Media.TITLE);
                int colPath = cursor.getColumnIndex(MediaStore.Video.Media.DATA);
                int colFolder = cursor.getColumnIndex(MediaStore.Video.Media.BUCKET_DISPLAY_NAME);
                int colTime = cursor.getColumnIndex(MediaStore.Video.Media.DATE_TAKEN);
                int colMimeType = cursor.getColumnIndex(MediaStore.Video.Media.MIME_TYPE);
                while(cursor.moveToNext())
                {
                    mediaFiles.add(new MediaFile(cursor.getString(colName),cursor.getString(colPath),cursor.getString(colFolder),cursor.getLong(colTime),cursor.getString(colMimeType)));
                    cursor.moveToNext();
                }
            }
            cursor.close();

            return mediaFiles;
        }

        @Override
        protected void onPostExecute(List<MediaFile> mediaFiles) {
            super.onPostExecute(mediaFiles);
            Intent intent = new Intent(MainActivity.this,ListImageActivity.class);
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("list", (ArrayList<? extends Parcelable>) mediaFiles);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }
}
