package com.application.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import glas.bbsystem.R;


public class ImageUtil {

  public static final int MAX_WIDTH_PIXEL = 512;
  public static final int MAX_HEIGHT_PIXEL = 1024;
  public static final int MAX_CROPED_PIXEL = 1024;
  private static final int IMAGE_COMPRESS_QUALITY = 100; // % quality

  public static void loadImage(Context context, String url,
      ImageView imageView) {
    Picasso.with(context).load(url).noFade().into(imageView);
  }

  public static void loadAvataImage(Context context, String url,
      ImageView imageView) {
    Picasso.with(context).load(url).placeholder(R.drawable.dummy_avatar)
        .noFade().into(imageView);
  }

  public static void loadAvataImage(Context context, String url,
      ImageView imageView, int width, int height) {
    Picasso.with(context).load(url).resize(width, height).centerCrop()
        .placeholder(R.drawable.dummy_avatar).noFade().into(imageView);
  }

  public static void loadAvataImage(Context context, String url,
      ImageView imageView, int size) {
    loadAvataImage(context, url, imageView, size, size);
  }

  public static void loadCircleAvataImage(Context context, String url,
      ImageView imageView) {
    Picasso.with(context).load(url).transform(new CircleTransform())
        .placeholder(R.drawable.dummy_circle_avatar).noFade()
        .into(imageView);
  }

  public static void loadCircleAvataImage(Context context, String url,
      ImageView imageView, int width, int height) {
    Picasso.with(context).load(url).transform(new CircleTransform())
        .resize(width, height).centerCrop()
        .placeholder(R.drawable.dummy_circle_avatar).noFade()
        .into(imageView);
  }

  public static void loadCircleAvataImage(Context context, String url,
      ImageView imageView, int size) {
    loadCircleAvataImage(context, url, imageView, size, size);
  }

  public static void loadBuzzImage(Context context, String url,
      ImageView imageView, int width, int height) {
    Picasso.with(context).load(url).resize(width, height).centerCrop()
        .placeholder(R.drawable.dummy_avatar).noFade().into(imageView);
  }

  public static void loadBuzzImage(Context context, String url,
      ImageView imageView, int size) {
    loadBuzzImage(context, url, imageView, size, size);
  }

  public static void loadGiftImage(Context context, String url,
      ImageView imageView, int width, int height) {
    Picasso.with(context).load(url).resize(width, height).centerCrop()
        .noFade().into(imageView);
  }

  public static void loadGiftImage(Context context, String url,
      ImageView imageView, int size) {
    loadGiftImage(context, url, imageView, size, size);
  }

  public static Bitmap resizeImage(String pathFile) {
    File f = new File(pathFile);
    try {
      // Decode file to bitmap to get bitmap size from options
      BitmapFactory.Options currentOptions = new BitmapFactory.Options();
      currentOptions.inJustDecodeBounds = true;
      BitmapFactory.decodeStream(new FileInputStream(f), null,
          currentOptions);

      // Find the correct scale value. It should be the power of 2.
      int scale = calculateInSampleSize(currentOptions, MAX_WIDTH_PIXEL,
          MAX_HEIGHT_PIXEL);

      // Decode with inSampleSize
      BitmapFactory.Options scaleOptions = new BitmapFactory.Options();
      scaleOptions.inSampleSize = scale;
      return BitmapFactory.decodeStream(new FileInputStream(f), null,
          scaleOptions);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static int calculateInSampleSize(BitmapFactory.Options options,
      int reqWidth, int reqHeight) {
    // Raw height and width of image
    int width = options.outWidth;
    int height = options.outHeight;
    int inSampleSize = 1;

    while (width / 2 >= reqWidth || height / 2 >= reqHeight) {
      width /= 2;
      height /= 2;
      inSampleSize *= 2;
    }

    return inSampleSize;
  }

  public static String saveTempResizedImage(Context context, String filePath) {
    Bitmap bitmap = resizeImage(filePath);
    if (bitmap == null) {
      return filePath;
    }

    FileOutputStream fileOutputStream = null;
    try {
      // Create new file and save to track
      File tempFile = StorageUtil.getPhotoFileTempByUser(context);
      tempFile.createNewFile();
      fileOutputStream = new FileOutputStream(tempFile);
      bitmap.compress(Bitmap.CompressFormat.JPEG, IMAGE_COMPRESS_QUALITY,
          fileOutputStream);

      fileOutputStream.flush();
      return tempFile.getAbsolutePath();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        if (fileOutputStream != null) {
          fileOutputStream.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    return filePath;
  }

  public static void rotateRightDirect(Bitmap bitmap, String filePath) {
    ExifInterface exif;
    try {
      exif = new ExifInterface(filePath);
      int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
          ExifInterface.ORIENTATION_NORMAL);
      int rotationInDegrees = getExifToDegrees(rotation);
      if (rotationInDegrees != 0) {
        Matrix matrix = new Matrix();
        matrix.postRotate(rotationInDegrees);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
            bitmap.getHeight(), matrix, true);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  // Rotate image with right gravity
  public static int getExifToDegrees(int exifOrientation) {
    if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
      return 90;
    } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
      return 180;
    } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
      return 270;
    }
    return 0;
  }

  public static String getMD5EncryptedString(File encTarget) {
    final String TAG = "MD5Encrypted";

    InputStream is = null;
    byte[] buffer = new byte[1024*8];
    int read;
    try {
      MessageDigest mdEnc = MessageDigest.getInstance("MD5");
      mdEnc.reset();
      is = new FileInputStream(encTarget);
      while ((read = is.read(buffer)) > 0) {
        mdEnc.update(buffer, 0, read);
      }

      byte[] md5sum = mdEnc.digest();
      StringBuilder builder = new StringBuilder();
      final int HEX = 16;
      int md5Size = md5sum.length;
      for (int i = 0; i < md5Size; i++) {
        builder.append(Integer.toString((md5sum[i] & 0xff) + 0x100, HEX).substring(1));
      }

      String output = builder.toString();
      LogUtils.e(TAG, output);
      return output;
    } catch (NoSuchAlgorithmException e) {
      LogUtils.e(TAG, "Exception while encrypting to md5");
      e.printStackTrace();
    } catch (FileNotFoundException e) {
      LogUtils.e(TAG, "Exception while getting FileInputStream");
      e.printStackTrace();
    } catch (IOException e) {
      LogUtils.e(TAG, "Unable to process file for MD5");
      e.printStackTrace();
    } finally {
      if (is != null) {
        try {
          is.close();
        } catch (IOException e) {
          e.printStackTrace();
          LogUtils.e(TAG, "Exception on closing MD5 input stream");
        }
      }
    }

    return "";
  }

  public static void loadBannerNewsImage(Context context, String url, ImageView imageView) {
    Glide.with(context)
        .load(url)
        .into(imageView);
  }
}