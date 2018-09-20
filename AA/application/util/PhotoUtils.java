package com.application.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Build;
import java.io.IOException;

public class PhotoUtils {

  public static float rotationForImage(Context context, String path) {
    try {
      ExifInterface exif = new ExifInterface(path);
      int rotation = (int) exifOrientationToDegrees(exif.getAttributeInt(
          ExifInterface.TAG_ORIENTATION,
          ExifInterface.ORIENTATION_NORMAL));
      return rotation;
    } catch (IOException e) {
      LogUtils.e("TAG", e.getMessage());
    }
    return 0f;
  }

  public static float exifOrientationToDegrees(int exifOrientation) {
    if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
      return 90;
    } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
      return 180;
    } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
      return 270;
    }
    return 0;
  }

  public static Matrix matrix(Context context, String path) {
    Matrix matrix = new Matrix();
    float rotation = rotationForImage(context, path);
    if (rotation != 0f) {
      matrix.preRotate(rotation);
    }
    return matrix;
  }

  public static Bitmap decodeSampledBitmapFromFile(String pathFile,
      int reqWidth, int reqHeight) {

    // First decode with inJustDecodeBounds=true to check dimensions
    final BitmapFactory.Options options = new BitmapFactory.Options();
    options.inJustDecodeBounds = true;
    BitmapFactory.decodeFile(pathFile, options);

    // Calculate inSampleSize
    options.inSampleSize = ImageUtil.calculateInSampleSize(options,
        reqWidth, reqHeight);

    // Decode bitmap with inSampleSize set
    options.inJustDecodeBounds = false;
    return BitmapFactory.decodeFile(pathFile, options);
  }

  public static Bitmap decodeSampledBitmapFromFile(Context context,
      String pathFile) {
    Bitmap bitmap;
    if (Build.VERSION.SDK_INT < 11) {
      int width = context.getResources().getDisplayMetrics().widthPixels;
      bitmap = PhotoUtils.decodeSampledBitmapFromFile(pathFile, width,
          width);
    } else {
      bitmap = BitmapFactory.decodeFile(pathFile);
    }
    return bitmap;
  }
}
