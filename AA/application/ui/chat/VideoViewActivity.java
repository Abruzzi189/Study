package com.application.ui.chat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.VideoView;
import com.application.ui.BaseFragmentActivity;
import com.application.ui.CenterButtonDialogBuilder;
import com.application.util.LogUtils;
import glas.bbsystem.R;


public class VideoViewActivity extends BaseFragmentActivity implements
    OnInfoListener {

  public static final String INTENT_URL = "url";
  public static final String INTENT_PATH = "path";
  private final String TAG = "VideoViewActivity";
  private String mUrl;
  private String mPath;

  private VideoView videoView;
  private MediaController mediaController;
  private ProgressBar progressBar;

  @Override
  protected void onCreate(Bundle bundle) {
    super.onCreate(bundle);
    setContentView(R.layout.ac_video_view);

    // Assign view
    progressBar = (ProgressBar) findViewById(R.id.progress);

    // Get data
    if (bundle != null) {
      mUrl = bundle.getString(INTENT_URL);
      mPath = bundle.getString(INTENT_PATH);
    } else {
      Intent intent = getIntent();
      if (intent != null) {
        mUrl = intent.getStringExtra(INTENT_URL);
        mPath = intent.getStringExtra(INTENT_PATH);
      } else {
        onErrorVideo(false);
      }
    }

    if (!TextUtils.isEmpty(mUrl)) {
      startVideo(mUrl);
    } else if (!TextUtils.isEmpty(mPath)) {
      startVideo(mPath);
    } else {
      onErrorVideo(false);
    }
  }

  private void startVideo(String url) {
    if (url != null && url.length() > 0) {
      LogUtils.i(TAG, url);
      // Initial view
      videoView = (VideoView) findViewById(R.id.video);
      Uri videoUri = Uri.parse(url);
      videoView.setVideoURI(videoUri);
      videoView.setOnErrorListener(new OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
          switch (what) {
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
              LogUtils.e(TAG, "Problem: MEDIA_ERROR_UNKNOWN");
              break;
            case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
              LogUtils.e(TAG,
                  "Problem: MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK ");
              break;
          }

          switch (extra) {
            case MediaPlayer.MEDIA_ERROR_IO:
              LogUtils.e(TAG, "Extra: MEDIA_ERROR_IO");
              break;
            case MediaPlayer.MEDIA_ERROR_MALFORMED:
              LogUtils.e(TAG, "Extra: MEDIA_ERROR_MALFORMED");
              break;
            case MediaPlayer.MEDIA_ERROR_UNSUPPORTED:
              LogUtils.e(TAG, "Extra: MEDIA_ERROR_IO");
              break;
            case MediaPlayer.MEDIA_ERROR_TIMED_OUT:
              LogUtils.e(TAG, "Extra: MEDIA_ERROR_MALFORMED");
              break;
          }

          onErrorVideo(true);
          return true;
        }
      });

      // Setting play back control
      mediaController = new MediaController(this);
      videoView.setMediaController(mediaController);
      mediaController.setAnchorView(videoView);

      // Play video
      videoView.requestFocus();
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
        videoView.setOnInfoListener(this);
      }
      videoView.setOnPreparedListener(new OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
          videoView.start();
          progressBar.setVisibility(View.GONE);
        }
      });
    } else {
      onErrorVideo(false);
    }
  }

  @Override
  protected void onPause() {
    super.onPause();
    videoView.stopPlayback();
  }

  private void onErrorVideo(boolean isRetriable) {
    AlertDialog.Builder builder = new CenterButtonDialogBuilder(this, false);
    builder.setMessage(R.string.can_not_play_file_not_found);
    builder.setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        finish();
      }
    });
    if (isRetriable) {
      builder.setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
          if (!TextUtils.isEmpty(mUrl)) {
            startVideo(mUrl);
          } else if (!TextUtils.isEmpty(mPath)) {
            startVideo(mPath);
          } else {
            onErrorVideo(false);
          }
        }
      });
    }
    AlertDialog alertDialog = builder.create();
    alertDialog.setCanceledOnTouchOutside(false);
    alertDialog.show();
  }

  @Override
  public boolean onInfo(MediaPlayer mp, int what, int extra) {
    switch (what) {
      case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
        progressBar.setVisibility(View.GONE);
        break;
      case MediaPlayer.MEDIA_INFO_BUFFERING_START:
        if (videoView.isPlaying()) {
          videoView.pause();
          progressBar.setVisibility(View.VISIBLE);
        }
        break;
      case MediaPlayer.MEDIA_INFO_BUFFERING_END:
        videoView.start();
        progressBar.setVisibility(View.GONE);
        break;
    }
    return true;
  }
}