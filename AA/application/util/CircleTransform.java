package com.application.util;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import com.squareup.picasso.Transformation;

public class CircleTransform implements Transformation {

  public final int SCALE_CENTER_INSIDE = 1;
  public final int SCALE_CENTER_CROP = 2;
  private final int BORDER_COLOR = Color.parseColor("#FEBD00");
  private final int BORDER_RADIUS = 0; // Width of border by dimension
  private int scaleType = SCALE_CENTER_INSIDE;

  public CircleTransform() {
    this.scaleType = SCALE_CENTER_INSIDE;
  }

  public CircleTransform(int scaleType) {
    this.scaleType = scaleType;
  }

  @Override
  public Bitmap transform(Bitmap source) {
    int size = 0;
    if (scaleType == SCALE_CENTER_INSIDE) {
      // Get smaller size between width and height
      size = Math.min(source.getWidth(), source.getHeight());
    } else {
      size = Math.max(source.getWidth(), source.getHeight());
    }

    // Get start point coordinates of the view
    int x = (source.getWidth() - size) / 2;
    int y = (source.getHeight() - size) / 2;

    // Create a new bitmap for the paint broad
    Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
    if (squaredBitmap != source) {
      source.recycle();
    }

    // Create result bitmap
    Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

    Canvas canvas = new Canvas(bitmap);
    Paint paint = new Paint();
    BitmapShader shader = new BitmapShader(squaredBitmap,
        BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
    paint.setShader(shader);
    paint.setAntiAlias(true);

    // The Radius of circle
    float r = size / 2f;

    // Prepare the background
    Paint paintBg = new Paint();
    paintBg.setColor(BORDER_COLOR);
    paintBg.setAntiAlias(true);

    // Draw the background circle
    canvas.drawCircle(r, r, r, paintBg);

    // Draw the image smaller than the background
    canvas.drawCircle(r, r, r - BORDER_RADIUS, paint);

    squaredBitmap.recycle();
    return bitmap;
  }

  @Override
  public String key() {
    return "circle";
  }
}