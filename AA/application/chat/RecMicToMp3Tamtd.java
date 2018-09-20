/*
 * Copyright (c) 2011-2012 Yuichi Hirano
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.application.chat;

import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.TextView;
import com.application.AndGApp;
import com.application.ui.ChatFragment;
import com.application.ui.customeview.AudioRecordViewCustom;
import com.itsherpa.andg.chat.AudioLame;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidParameterException;

public class RecMicToMp3Tamtd {

  public static final int MSG_REC_STARTED = 0;
  public static final int MSG_REC_STOPPED = 1;
  public static final int MSG_ERROR_GET_MIN_BUFFERSIZE = 2;
  public static final int MSG_ERROR_CREATE_FILE = 3;
  public static final int MSG_ERROR_REC_START = 4;
  public static final int MSG_ERROR_AUDIO_RECORD = 5;
  public static final int MSG_ERROR_AUDIO_ENCODE = 6;
  public static final int MSG_ERROR_WRITE_FILE = 7;
  public static final int MSG_ERROR_CLOSE_FILE = 8;

  static {
    System.loadLibrary("mp3lame");
  }

  private final int LIMIT_TIME = 60;
  private final int SECOND_COUNT = 10;
  public long mStartTime = System.currentTimeMillis();
  private String mFilePath;
  private int mSampleRate;
  private boolean mIsRecording = false;
  private boolean mIsRunable = true;
  private boolean mIsContinuable = false;
  private Handler mHandler;
  private TextView mTxtRecorderTime;
  private AudioRecordViewCustom visualizer = null;
  private Handler mHandlerTimeCount = new Handler();
  private float currentLevel = 0;
  private Runnable mRunnableTimeCount = new Runnable() {
    @Override
    public void run() {
      if (visualizer != null) {
        if (mTxtRecorderTime != null) {
          long currentTime = System.currentTimeMillis();
          long deltaTime = currentTime - mStartTime;
          int second = (int) deltaTime / 1000;
          if (second > LIMIT_TIME) {
            second = LIMIT_TIME;
          }

          int percentSecond = (int) deltaTime / 100 % 10;
          if (second == LIMIT_TIME) {
            percentSecond = 0;
          }

          int time = second * 10 + percentSecond;
          visualizer.addLevel(currentLevel, time);

          if (second < LIMIT_TIME) {
            mTxtRecorderTime.setText(String.format("%s:%s", second, percentSecond));
          } else {
            visualizer.addFinalLevels();
          }
        }
        visualizer.invalidate();
      }

      if (mIsRecording) {
        mHandlerTimeCount.postDelayed(mRunnableTimeCount, 100);
      }
    }
  };
  public RecMicToMp3Tamtd(int sampleRate) {
    if (sampleRate <= 0) {
      throw new InvalidParameterException(
          "Invalid sample rate specified.");
    }
    this.mSampleRate = sampleRate;
  }

  public void linkToVisualizer(AudioRecordViewCustom view) {
    visualizer = view;
    visualizer.setBlock((LIMIT_TIME - 1) * SECOND_COUNT);
  }

  private void initStartRecord() {
    mIsRunable = false;
    mIsRecording = true;
    mIsContinuable = true;
    mStartTime = System.currentTimeMillis();
    if (visualizer != null) {
      visualizer.clear();
    }
    android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
  }

  public void start() {
    if (mIsRecording) {
      return;
    }
    new Thread() {
      @Override
      public void run() {
        if (!mIsRunable) {
          return;
        }
        initStartRecord();
        mHandlerTimeCount.post(mRunnableTimeCount);
        int minBufferSize = AudioRecord.getMinBufferSize(mSampleRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT);
        if (minBufferSize < 0) {
          if (mHandler != null) {
            mHandler.sendEmptyMessage(MSG_ERROR_GET_MIN_BUFFERSIZE);
          }
          return;
        }
        AudioRecord audioRecord = new AudioRecord(
            MediaRecorder.AudioSource.MIC, mSampleRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT, minBufferSize * 2);

        // PCM buffer size (5sec)
        // SampleRate[Hz] * 16bits * Mono * 5secs
        short[] buffer = new short[mSampleRate * (16 / 8) * 1 * 5];
        byte[] mp3buffer = new byte[(int) (7200 + buffer.length * 2 * 1.25)];

        // Lame init
        AudioLame.init(mSampleRate, 1, mSampleRate, 32);

        try {
          try {
            audioRecord.startRecording();
          } catch (IllegalStateException e) {
            if (mHandler != null) {
              mHandler.sendEmptyMessage(MSG_ERROR_REC_START);
            }
            return;
          }
          FileOutputStream output = null;
          try {
            if (mHandler != null) {
              mHandler.sendEmptyMessage(MSG_REC_STARTED);
            }

            int readSize;
            try {
              output = new FileOutputStream(new File(
                  getFilePath()));
            } catch (FileNotFoundException e) {
              if (mHandler != null) {
                mHandler.sendEmptyMessage(MSG_ERROR_CREATE_FILE);
              }
              return;
            }
            while (mIsContinuable) {
              long currentTime = System.currentTimeMillis();
              long deltaTime = currentTime - mStartTime;
              int second = (int) deltaTime / 1000;
              if (second >= LIMIT_TIME) {
                mIsRecording = false;
                Intent intent = new Intent(ChatFragment.ACTION_STOP_RECORD);
                LocalBroadcastManager.getInstance(AndGApp.get()).sendBroadcast(intent);
                break;
              }
              readSize = audioRecord.read(buffer, 0, minBufferSize);
              if (readSize < 0) {
                if (mHandler != null) {
                  mHandler.sendEmptyMessage(MSG_ERROR_AUDIO_RECORD);
                }
                break;
              } else if (readSize > 0) {
                // Store current level for for using later
                double sum = 0;
                for (int i = 0; i < readSize; i++) {
                  sum += buffer[i] * buffer[i];
                }
                currentLevel = (float) Math
                    .sqrt(sum / readSize);
                int encResult = AudioLame.encode(buffer,
                    buffer, readSize, mp3buffer);
                if (encResult < 0) {
                  if (mHandler != null) {
                    mHandler.sendEmptyMessage(MSG_ERROR_AUDIO_ENCODE);
                  }
                  break;
                }
                if (encResult != 0) {
                  try {
                    output.write(mp3buffer, 0, encResult);
                  } catch (IOException e) {
                    if (mHandler != null) {
                      mHandler.sendEmptyMessage(MSG_ERROR_WRITE_FILE);
                    }
                    break;
                  }
                }
              }
            }
            audioRecord.stop();
            int flushResult = AudioLame.flush(mp3buffer);
            if (flushResult <= 0) {
              if (mHandler != null) {
                mHandler.sendEmptyMessage(MSG_ERROR_AUDIO_ENCODE);
              }
            } else {
              try {
                output.write(mp3buffer, 0, flushResult);
              } catch (IOException e) {
                if (mHandler != null) {
                  mHandler.sendEmptyMessage(MSG_ERROR_WRITE_FILE);
                }
              }
            }
          } finally {
            try {
              output.close();
            } catch (IOException e) {
              if (mHandler != null) {
                mHandler.sendEmptyMessage(MSG_ERROR_CLOSE_FILE);
              }
            }
            audioRecord.stop();
            audioRecord.release();
            mIsRecording = false;
          }
        } finally {
          AudioLame.close();
        }
        if (!mIsContinuable) {
          mIsRecording = false;
        } else {
          mIsContinuable = false;
        }
        mIsRunable = true;

        if (mHandler != null) {
          mHandler.sendEmptyMessage(MSG_REC_STOPPED);
        }
      }
    }.start();
  }

  public boolean isRecording() {
    return mIsRecording;
  }

  public void stop() {
    mIsContinuable = false;
  }

  public void setHandle(Handler handler) {
    this.mHandler = handler;
  }

  public String getFilePath() {
    return mFilePath;
  }

  public void setFilePath(String filePath) {
    this.mFilePath = filePath;
  }

  public void setTxtRecorderTime(TextView txtRecorderTime) {
    this.mTxtRecorderTime = txtRecorderTime;
  }
}