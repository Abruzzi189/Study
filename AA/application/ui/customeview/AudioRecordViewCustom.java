package com.application.ui.customeview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import java.util.ArrayList;

public class AudioRecordViewCustom extends View {

  private static final String TAG = "AudioRecordViewCustom";
  private static final int AMPLITUDE_MAX = 32 * 1024;
  private static int mBlock = 0;
  private final float RADIUS = 10;
  private float width;
  private float height;
  private int mCurrentTime = 0;

  private Path clipPath;
  private Paint levelPaint;
  private Paint currentLevelPaint;
  private Paint dimLayerPaint;
  private float startX;
  private float stopX;
  private float stepX;

  private ArrayList<Level> levels;

  private boolean measured = false;

  public AudioRecordViewCustom(Context context) {
    super(context);
    initializeView();
  }

  public AudioRecordViewCustom(Context context, AttributeSet attrs) {
    super(context, attrs);
    initializeView();
  }

  public void setBlock(int block) {
    mBlock = block;
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);

    if (measured) {
      // Set clip path
      canvas.clipPath(clipPath);

      // Draw all levels
      for (int i = 0; i < levels.size(); i++) {
        canvas.drawLine(levels.get(i).startX, levels.get(i).startY, levels.get(i).stopX,
            levels.get(i).stopY, levelPaint);
        canvas.drawRect(levels.get(i).startX, 0, levels.get(i).stopX, height, dimLayerPaint);
      }

      // Draw current level
      if (levels.size() > 0) {
        canvas.drawLine(levels.get(levels.size() - 1).stopX, 0, levels.get(levels.size() - 1).stopX,
            height, currentLevelPaint);
      }
    }
  }

  private void initializeView() {
    levelPaint = new Paint();
    levelPaint.setAntiAlias(true);
    levelPaint.setColor(Color.BLACK);
    levelPaint.setStyle(Paint.Style.STROKE);
    levelPaint.setStrokeJoin(Paint.Join.ROUND);
    levelPaint.setStrokeWidth(1f);

    currentLevelPaint = new Paint();
    currentLevelPaint.setAntiAlias(true);
    currentLevelPaint.setColor(Color.DKGRAY);
    currentLevelPaint.setStyle(Paint.Style.STROKE);
    currentLevelPaint.setStrokeJoin(Paint.Join.ROUND);
    currentLevelPaint.setStrokeWidth(1f);

    dimLayerPaint = new Paint();
    dimLayerPaint.setAntiAlias(true);
    dimLayerPaint.setColor(Color.LTGRAY);
    dimLayerPaint.setStrokeWidth(1f);
    dimLayerPaint.setAlpha(95);

    levels = new ArrayList<>();

    mCurrentTime = 0;
    startX = 0;
  }

  public void addLevel(float amplitude) {
    if (!measured) {
      measured = true;
      width = getWidth();
      height = getHeight();
      stepX = width / mBlock;
      clipPath = new Path();
      RectF rect = new RectF(0, 0, width, height);
      clipPath.addRoundRect(rect, RADIUS, RADIUS, Direction.CCW);
    }

    Level level = new Level();
    startX = stopX;
    stopX = stopX + stepX;
    level.startX = startX;
    level.stopX = stopX;
    float deciben = (2 * amplitude * height) / AMPLITUDE_MAX;
    level.startY = (height - deciben) / 2;
    if (level.startY < 0) {
      level.startY = 0;
    }
    level.stopY = (level.startY + deciben);
    levels.add(level);
  }

  public void addLevel(float amplitude, int time) {
    if (!measured) {
      measured = true;
      width = getWidth();
      height = getHeight();
      clipPath = new Path();
      RectF rect = new RectF(0, 0, width, height);
      clipPath.addRoundRect(rect, RADIUS, RADIUS, Direction.CCW);
    }

    int expend = 1;
    if (time > mCurrentTime) {
      expend = time - mCurrentTime;
    }
    mCurrentTime = time;
    stepX = (width / mBlock) * expend;

    Level level = new Level();
    startX = stopX;
    stopX = stopX + stepX;
    level.startX = startX;
    level.stopX = stopX;
    float deciben = (2 * amplitude * height) / AMPLITUDE_MAX;
    level.startY = (height - deciben) / 2;
    if (level.startY < 0) {
      level.startY = 0;
    }
    level.stopY = (level.startY + deciben);
    levels.add(level);
  }

  public void addFinalLevels() {
    Level level = new Level();
    startX = stopX;
    stopX = width;
    level.startY = height / 2;
    level.stopY = height / 2;
    levels.add(level);
  }

  public void clear() {
    levels.clear();
    mCurrentTime = 0;
    startX = 0;
    stopX = 0;
    measured = false;
  }

  private static class Level {

    public float startX;
    public float startY;
    public float stopX;
    public float stopY;
  }
}