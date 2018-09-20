package com.application.util;

/**
 * Created by Robert on 28/12/2015.
 */

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Utils {

  private static final String TAG = "Utils";

  public static float pixelsToSp(Context context, float px) {
    float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
    return px / scaledDensity;
  }

  /**
   * This method converts dp unit to equivalent pixels, depending on device density.
   *
   * @param dp A value in dp (density independent pixels) unit. Which we need to convert into
   * pixels
   * @param context Context to get resources and device specific display metrics
   * @return A float value to represent px equivalent to dp depending on device density
   */
  public static float convertDpToPixel(float dp, Context context) {
    Resources resources = context.getResources();
    DisplayMetrics metrics = resources.getDisplayMetrics();
    float px = dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    return px;
  }

  /**
   * This method converts device specific pixels to density independent pixels.
   *
   * @param px A value in px (pixels) unit. Which we need to convert into db
   * @param context Context to get resources and device specific display metrics
   * @return A float value to represent dp equivalent to px value
   */
  public static float convertPixelsToDp(float px, Context context) {
    Resources resources = context.getResources();
    DisplayMetrics metrics = resources.getDisplayMetrics();
    float dp = px / ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    return dp;
  }

  /**
   * Convert byte array to hex string
   */
  public static String bytesToHex(byte[] bytes) {
    StringBuilder sbuf = new StringBuilder();
    for (int idx = 0; idx < bytes.length; idx++) {
      int intVal = bytes[idx] & 0xff;
      if (intVal < 0x10) {
        sbuf.append("0");
      }
      sbuf.append(Integer.toHexString(intVal).toUpperCase());
    }
    return sbuf.toString();
  }

  /**
   * Get utf8 byte array.
   *
   * @return array of NULL if error was found
   */
  public static byte[] getUTF8Bytes(String str) {
    try {
      return str.getBytes("UTF-8");
    } catch (Exception ex) {
      return null;
    }
  }

  /**
   * Load UTF8withBOM or any ansi text file.
   */
  public static String loadFileAsString(String filename) throws IOException {
    final int BUFLEN = 1024;
    BufferedInputStream is = new BufferedInputStream(new FileInputStream(filename), BUFLEN);
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream(BUFLEN);
      byte[] bytes = new byte[BUFLEN];
      boolean isUTF8 = false;
      int read, count = 0;
      while ((read = is.read(bytes)) != -1) {
        if (count == 0 && bytes[0] == (byte) 0xEF && bytes[1] == (byte) 0xBB
            && bytes[2] == (byte) 0xBF) {
          isUTF8 = true;
          baos.write(bytes, 3, read - 3); // drop UTF8 bom marker
        } else {
          baos.write(bytes, 0, read);
        }
        count += read;
      }
      return isUTF8 ? new String(baos.toByteArray(), "UTF-8") : new String(baos.toByteArray());
    } finally {
      try {
        is.close();
      } catch (Exception ex) {
      }
    }
  }

  public static float dp2px(Resources resources, float dp) {
    final float scale = resources.getDisplayMetrics().density;
    return dp * scale + 0.5f;
  }

  public static float sp2px(Resources resources, float sp) {
    final float scale = resources.getDisplayMetrics().scaledDensity;
    return sp * scale;
  }

  /**
   * TODO Dumping data keyword and value in intent object
   *
   * @author Created by Robert on 2015 Dec 11
   */
  public static String dumpIntent(Intent intent) {
    StringBuilder dumIntent = new StringBuilder("IntentData:");
    if (intent == null) {
      return dumIntent.toString();
    }
    try {
      Bundle bundle = intent.getExtras();
      if (bundle != null) {
        Set<String> keys = bundle.keySet();
        Iterator<String> it = keys.iterator();
        Log.e("DumpIntent", "------>Dumping Intent start");
        while (it.hasNext()) {
          String key = it.next();
          dumIntent.append("\nKey:" + key + "-->Value:" + StringUtils.nullToEmpty(bundle.get(key)));
          Log.e("DumpIntent", "[" + key + "=" + StringUtils.nullToEmpty(bundle.get(key)) + "]");
        }
        Log.e("DumpIntent", "------>Dumping Intent end");
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return dumIntent.toString();
  }

  /**
   * TODO Dumping data keyword and value in bundle object
   *
   * @author Created by Robert on 2015 Dec 11
   */
  public static String dumpBundle(Bundle bundle) {
    StringBuilder dumBundle = new StringBuilder("IntentData:");
    if (bundle == null) {
      return dumBundle.toString();
    }
    try {
      Set<String> keys = bundle.keySet();
      Iterator<String> it = keys.iterator();
      Log.e("DumpBundle", "------>Dumping Bundle start");
      while (it.hasNext()) {
        String key = it.next();
        dumBundle.append("\nKey:" + key + "-->Value:" + StringUtils.nullToEmpty(bundle.get(key)));
        Log.e("DumpBundle", "[" + key + "=" + StringUtils.nullToEmpty(bundle.get(key)) + "]");
      }
      Log.e("DumpBundle", "------>Dumping Bundle end");
    } catch (Exception e) {
      e.printStackTrace();
    }
    return dumBundle.toString();
  }

  public static String dumpMapData(Map<String, String> mMapData) {
    StringBuilder dumBundle = new StringBuilder("IntentData:");
    if (mMapData == null) {
      return dumBundle.toString();
    }
    try {
      Set<String> keys = mMapData.keySet();
      Iterator<String> it = keys.iterator();
      Log.e("DumpmMapData", "------>Dumping DapData start");
      while (it.hasNext()) {
        String key = it.next();
        dumBundle.append("\nKey:" + key + "-->Value:" + StringUtils.nullToEmpty(mMapData.get(key)));
        Log.e("DumpmMapData", "[" + key + "=" + StringUtils.nullToEmpty(mMapData.get(key)) + "]");
      }
      Log.e("DumpmMapData", "------>Dumping Bundle end");

            /*for(Map.Entry<String, String> entry : mMapData.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                LogUtils.i(TAG, "-->key=" + key + "|value=" + value);
            }*/
    } catch (Exception e) {
      e.printStackTrace();
    }
    return dumBundle.toString();
  }

    /*public static BitmapDrawable writeTextOnDrawable(Context mContext, int drawableId, String text, int textColor, int textSize) {

        Bitmap bm = BitmapFactory.decodeResource(mContext.getResources(), drawableId).copy(Bitmap.Config.ARGB_8888, true);

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(textColor);
        paint.setTextSize(textSize);

        Canvas canvas = new Canvas(bm);
        canvas.drawText(text, 0, bm.getHeight()/2, paint);

        return new BitmapDrawable(bm);
    }

    public static Bitmap writeTextOnBitmap(Context mContext, int drawableId, String text, int textColor, int textSize) {

        try {
            Bitmap bm = BitmapFactory.decodeResource(mContext.getResources(), drawableId).copy(Bitmap.Config.ARGB_8888, true);

            Paint paint = new Paint();
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(textColor);
            paint.setTextSize(textSize);

            Canvas canvas = new Canvas(bm);
            canvas.drawText(text, 0, bm.getHeight()/2, paint);
            Log.e("Utils", "---->go to here...text=" + text);
            return new BitmapDrawable(bm).getBitmap();
        } catch(Exception ex) {

        }
        Log.e("Utils", "---->NULL pa no roai...");
        return null;
    }*/

  public static synchronized Bitmap createBitmapFromView(View parentView, View view) {
    if (view == null) {
      return null;
    }
    try {
      view.setDrawingCacheEnabled(true);
      // this is the important code
      // Without it the view will have a dimension of 0,0 and the bitmap will be null

        /*view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));*/

      Log.e("Utils", "" + (view == null ? " IS NULL" : " NOT NULL"));
      view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

      //int expandSpec = View.MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, View.MeasureSpec.AT_MOST);
      //view.measure(parentView.getWidth(), parentView.getHeight());

      view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

      view.buildDrawingCache(true);
      Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
      view.setDrawingCacheEnabled(false); // clear drawing cache

      return bitmap;
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return null;
  }

  public static synchronized Bitmap createBitmapFromView(View view) {
    if (view == null) {
      return null;
    }
    try {
      view.setDrawingCacheEnabled(true);
      // this is the important code
      // Without it the view will have a dimension of 0,0 and the bitmap will be null

        /*view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));*/
      view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

      view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

      view.buildDrawingCache(true);
      Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
      view.setDrawingCacheEnabled(false); // clear drawing cache

      return bitmap;
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return null;
  }

  public static synchronized Bitmap getBitmapFromView(View view) {
    if (view == null) {
      return null;
    }
    try {
            /*view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));*/
      view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
      //Define a bitmap with the same size as the view
      Bitmap returnedBitmap = Bitmap
          .createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
      //Bind a canvas to it
      Canvas canvas = new Canvas(returnedBitmap);
      //Get the view's background
      Drawable bgDrawable = view.getBackground();
      if (bgDrawable != null)
      //has background drawable, then draw it on the canvas
      {
        bgDrawable.draw(canvas);
      } else
      //does not have background drawable, then draw white background on the canvas
      {
        canvas.drawColor(Color.WHITE);
      }
      // draw the view on the canvas
      view.draw(canvas);
      //return the bitmap
      return returnedBitmap;
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return null;
  }

  public static Bitmap drawTextToBitmap(Context mContext, int resourceId, String mText, int color,
      int textSize) {
    try {
      Resources resources = mContext.getResources();
      float scale = resources.getDisplayMetrics().density;
      Bitmap bitmap = BitmapFactory.decodeResource(resources, resourceId);

      Bitmap.Config bitmapConfig = bitmap.getConfig();
      // set default bitmap config if none
      if (bitmapConfig == null) {
        bitmapConfig = Bitmap.Config.ARGB_8888;
      }
      // resource bitmaps are mutable, so we need to convert it to mutable one
      bitmap = bitmap.copy(bitmapConfig, true);

      Canvas canvas = new Canvas(bitmap);
      // new antialiasing Paint
      Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
      // text color
      paint.setColor(color);//Color.rgb(110,110, 110)
      // text size in pixels
      paint.setTextSize(textSize * scale);//(int) (12 * scale)
      //// --text shadow
      //paint.setShadowLayer(1f, 0f, 1f, Color.DKGRAY);

      // draw text to the Canvas center
      Rect bounds = new Rect();
      paint.getTextBounds(mText, 0, mText.length(), bounds);
      //int x = (bitmap.getWidth() - bounds.width())/2;
      //int y = (bitmap.getHeight() + bounds.height())/2;
      //canvas.drawText(mText, x * scale, y * scale, paint);

      canvas.drawText(mText, (bitmap.getWidth() - bounds.width()) / 2,
          (bitmap.getHeight() + bounds.height()) / 2, paint);

      return bitmap;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  public static Drawable createMarkerIcon(Drawable backgroundImage, String text, int width,
      int height) {

    Bitmap canvasBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
    // Create a canvas, that will draw on to canvasBitmap.
    Canvas imageCanvas = new Canvas(canvasBitmap);

    // Set up the paint for use with our Canvas　ポイントを使って保護しますか？
    Paint imagePaint = new Paint();
    imagePaint.setTextAlign(Paint.Align.CENTER);
    imagePaint.setTextSize(16f);

    // Draw the image to our canvas
    backgroundImage.draw(imageCanvas);

    // Draw the text on top of our image
    imageCanvas.drawText(text, width / 2, height / 2, imagePaint);

    // Combine background and text to a LayerDrawable
    LayerDrawable layerDrawable = new LayerDrawable(
        new Drawable[]{backgroundImage, new BitmapDrawable(canvasBitmap)});
    return layerDrawable;
  }

  public static Drawable createDrawableFromXml(Resources res, int idResXmlFile) {
    Drawable myDrawable = null;
    try {
      myDrawable = Drawable.createFromXml(res, res.getXml(idResXmlFile));
    } catch (Exception ex) {
      Log.e(TAG, "Exception loading drawable");
    }
    return myDrawable;
  }

  public static Map<String, ?> getAllKeysInPreferenceManager(Context mContext) {
    if (mContext == null) {
      return null;
    }
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
    if (prefs == null) {
      return null;
    }
    Map<String, ?> keys = prefs.getAll();

        /*for(Map.Entry<String,?> entry : keys.entrySet()) {
            Log.e(TAG, "--->map values:" + entry.getKey() + ": " + entry.getValue().toString());
        }*/

    return keys;
  }


  /**
   * TODO Rotation image file if need
   *
   * @author Created by Robert on 2016 Dec 15
   */
  public static String rotationImageIfNeed(String photoPath) {

    try {
      ExifInterface ei = new ExifInterface(photoPath);
      int orientation = ei
          .getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
      int degree = 0;

      switch (orientation) {
        case ExifInterface.ORIENTATION_NORMAL:
          degree = 0;
          break;
        case ExifInterface.ORIENTATION_ROTATE_90:
          degree = 90;
          break;
        case ExifInterface.ORIENTATION_ROTATE_180:
          degree = 180;
          break;
        case ExifInterface.ORIENTATION_ROTATE_270:
          degree = 270;
          break;
        case ExifInterface.ORIENTATION_UNDEFINED:
          degree = 0;
          break;
        default:
          degree = 90;
      }

      return rotateImage(degree, photoPath);

    } catch (Exception e) {
      e.printStackTrace();
    }

    return photoPath;
  }

  /**
   * TODO Rotation image file with image path
   *
   * @author Created by Robet on 2016 Dec 15
   */
  public static String rotateImage(int degree, String imagePath) {

    if (degree <= 0) {
      return imagePath;
    }
    try {
      Bitmap b = BitmapFactory.decodeFile(imagePath);

      Matrix matrix = new Matrix();
      if (b.getWidth() > b.getHeight()) {
        matrix.setRotate(degree);
        b = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), matrix, true);
      }

      FileOutputStream fOut = new FileOutputStream(imagePath);
      String imageName = imagePath.substring(imagePath.lastIndexOf("/") + 1);
      String imageType = imageName.substring(imageName.lastIndexOf(".") + 1);

      FileOutputStream out = new FileOutputStream(imagePath);
      if (imageType.equalsIgnoreCase("png")) {
        b.compress(Bitmap.CompressFormat.PNG, 100, out);
      } else if (imageType.equalsIgnoreCase("jpeg") || imageType.equalsIgnoreCase("jpg")) {
        b.compress(Bitmap.CompressFormat.JPEG, 100, out);
      }
      fOut.flush();
      fOut.close();

      b.recycle();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return imagePath;
  }

  /**
   * TODO Get camera photo orientation
   *
   * @author Created by Robert on 2016 Dec 15
   */
  public static int getCameraPhotoOrientation(String photoPath) {
    int degree = 0;
    int orientation = 0;
    try {
      ExifInterface exif = new ExifInterface(photoPath);
      orientation = exif
          .getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

      switch (orientation) {
        case ExifInterface.ORIENTATION_NORMAL:
          degree = 0;
          break;
        case ExifInterface.ORIENTATION_ROTATE_90:
          degree = 90;
          break;
        case ExifInterface.ORIENTATION_ROTATE_180:
          degree = 180;
          break;
        case ExifInterface.ORIENTATION_ROTATE_270:
          degree = 270;
          break;
        case ExifInterface.ORIENTATION_UNDEFINED:
          degree = 0;
          break;
        default:
          degree = 90;
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
    LogUtils.i("RotateImage", "Exif orientation: " + orientation);
    LogUtils.i("RotateImage", "Rotate value: " + degree);
    return degree;
  }

  /**
   * TODO General method get color for all API level
   *
   * @author Created by Robert on 30 Dec 2016
   */
  public static final int getColor(Context context, int id) {
    final int version = Build.VERSION.SDK_INT;
    if (version >= 23) {
      return ContextCompat.getColor(context, id);
    }
    return context.getResources().getColor(id);
  }

  /**
   * TODO add multiple elements at once time into original array; addElement(int[], int) will still
   * work though.
   *
   * @author Robert created ob 08 Feb 2017
   */
  public static int[] addElement(int[] original, int... elements) {
    if (original == null && elements == null) {
      return null;
    }
    if (original != null && elements == null) {
      return original;
    }
    if (original == null && elements != null) {
      return elements;
    }

    int[] nEw = new int[original.length + elements.length];
    System.arraycopy(original, 0, nEw, 0, original.length);
    System.arraycopy(elements, 0, nEw, original.length, elements.length);
    return nEw;
  }

  public static String[] addElement(String[] original, String... elements) {
    if (original == null && elements == null) {
      return null;
    }
    if (original != null && elements == null) {
      return original;
    }
    if (original == null && elements != null) {
      return elements;
    }

    String[] nEw = new String[original.length + elements.length];
    System.arraycopy(original, 0, nEw, 0, original.length);
    System.arraycopy(elements, 0, nEw, original.length, elements.length);
    return nEw;
  }

  /**
   * TODO Convert String array contains all number values to Integer Array
   *
   * @return Int[]
   * @author Created by Robert on 08 Feb 2017
   */
  public static int[] convertStringArrayToIntArray(String[] original) {
    if (original == null) {
      return null;
    }
    int[] nEw = new int[original.length];
    for (int idx = 0; idx < original.length; idx++) {
      if (!StringUtils.isInteger(original[idx])) {
        return null;
      }
      nEw[idx] = Integer.parseInt(original[idx]);
    }
    return nEw;
  }

  public static boolean isOnline(Context context) {
    ConnectivityManager cm = (ConnectivityManager) context
        .getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo netInfo;
    if (cm != null) {
      netInfo = cm.getActiveNetworkInfo();
      return (netInfo != null && netInfo.isConnected());
    } else {
      return false;
    }
  }
}
