package com.application.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import com.application.provider.UriCompat;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class StorageUtil {

  private final static String TAG = "StorageUtil";
  private final static String ROOT_FOLDER = "Switch";
  private final static String VIDEO_FOLDER = "Switch_Video";
  private final static String AUDIO_FOLDER = "Switch_Audio";
  private final static String PHOTO_FOLDER = "Switch_Photo";
  private final static String FILE_SENT = "maps";
  private static final String DEFAULT_IMAGE_FOLDER = "Switch";
  private static final String NAME_FORMAT = "yyyyMMddHHmmssSSS";
  private static final String IMAGE_FORMAT_TYPE = "jpg";
  private static final String VIDEO_FORMAT = "mp4";
  private static final String AUDIO_FORMAT = "mp3";

  private static File getFileSentByUser(Context context, String userId) {
    File file = new File(getFileSentFolder(context), userId);
    if (!file.exists()) {
      try {
        file.createNewFile();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return file;
  }

  /**
   * Mapping giua messageId va duong dan toi file.
   *
   * (1) Su dung khi sent file di. Can su dung ngay khi su message 1 di, de khi upload bi loi thi
   * van co the biet duoc la sent file nao. (2) Su dung khi download file thanh cong
   */
  public static void saveMessageIdAndFilePathByUser(Context context,
      String userId, String fileId, String filePath) {
    String data = fileId + " " + filePath + "\n";
    File file = getFileSentByUser(context, userId);
    if (file.exists()) {
      try {
        FileWriter fileWriter = new FileWriter(file, true);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        bufferedWriter.write(data);
        bufferedWriter.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public static void removeLineInFile(Context context, String userId,
      String fileId) {
    File file = getFileSentByUser(context, userId);
    File fileTemp = new File(file.getAbsoluteFile() + ".tmp");
    if (file.exists()) {
      BufferedReader bufferedReader = null;
      BufferedWriter bufferedWriter = null;
      try {
        bufferedReader = new BufferedReader(new FileReader(file));
        bufferedWriter = new BufferedWriter(new FileWriter(fileTemp));
        while (bufferedReader.ready()) {
          String data = bufferedReader.readLine();
          if (!data.contains(fileId)) {
            bufferedWriter.write(data + "\n");
          }
        }
        file.delete();
        fileTemp.renameTo(file);
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        if (bufferedReader != null) {
          try {
            bufferedReader.close();
            bufferedWriter.close();
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }
    }
  }

  public static String getFilePathByUserIdAndFileId(Context context,
      String userId, String fileId) {
    File file = getFileSentByUser(context, userId);
    if (file.exists()) {
      BufferedReader bufferedReader = null;
      try {
        bufferedReader = new BufferedReader(new FileReader(file));
        while (bufferedReader.ready()) {
          String data = bufferedReader.readLine();
          if (data.contains(fileId)) {
            String[] paths = data.split(" ");
            return paths[1];
          }
        }
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        if (bufferedReader != null) {
          try {
            bufferedReader.close();
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }
    }
    return null;
  }

  private static File getFileSentFolder(Context context) {
    File file = new File(context.getExternalFilesDir(null), FILE_SENT);
    if (!file.exists()) {
      file.mkdirs();
    }
    return file;
  }

  public static File getAudioRecord(Context context) {
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS",
        Locale.getDefault());
    Calendar calendar = Calendar.getInstance();
    String fileName = "Switch_audio_" + dateFormat.format(calendar.getTime())
        + Utility.TAG_MP3;
    File file = new File(getChildFolder(context, AUDIO_FOLDER), fileName);
    return file;
  }

  private static File getChildFolder(Context context, String folderType) {
    if (Environment.getExternalStorageState().equals(
        Environment.MEDIA_MOUNTED)) {
      File file = new File(getRootFolder(context), folderType);
      if (!file.exists()) {
        file.mkdir();
      }
      return file;
    } else {
      File file = new File(getRootFolder(context), folderType);
      if (!file.exists()) {
        file.mkdir();
      }
      return file;
    }
  }

  private static File getRootFolder(Context context) {
    if (Environment.getExternalStorageState().equals(
        Environment.MEDIA_MOUNTED)) {
      File file = new File(Environment.getExternalStorageDirectory(),
          ROOT_FOLDER);
      if (!file.exists()) {
        file.mkdir();
      }
      return file;
    } else {
      File file = new File(context.getFilesDir(), ROOT_FOLDER);
      if (!file.exists()) {
        file.mkdir();
      }
      return file;
    }
  }

  public static File createFileTemp(Context context, String fileName)
      throws IOException {
    if (Environment.getExternalStorageState().equals(
        Environment.MEDIA_MOUNTED)) {
      File file = new File(getRootFolder(context), fileName);
      if (!file.exists()) {
        file.createNewFile();
      }
      return file;
    } else {
      File file = new File(getRootFolder(context), fileName);
      if (!file.exists()) {
        file.createNewFile();
      }
      return file;
    }
  }

  private static File getAlbumStorageDir(String albumName) {
    // Get the directory for the user's public pictures directory.
    File file = new File(
        Environment
            .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
        albumName);
    if (!file.mkdirs()) {
    }
    return file;
  }

  private static File getAlbumStorageDir() {
    return getAlbumStorageDir(DEFAULT_IMAGE_FOLDER);
  }

  public static File getAndGStorageDir(String fileName) {
    return new File(StorageUtil.getAlbumStorageDir(), fileName);
  }

  public static File getVideoFileTempByUser(Context context) {
    return new File(getChildFolder(context, VIDEO_FOLDER),
        getFileName(VIDEO_FORMAT));
  }

  public static File getAudioFileTempByUser(Context context) {
    return new File(getChildFolder(context, AUDIO_FOLDER),
        getFileName(AUDIO_FORMAT));
  }

  public static File getPhotoFileTempByUser(Context context) {
    return new File(getChildFolder(context, PHOTO_FOLDER),
        getFileName(IMAGE_FORMAT_TYPE));
  }

  public static File getPhotoFileTempToDownload(Context context) {
    return new File(context.getExternalFilesDir(null), getFileName("0"));
  }

  private static String getFileName(String fileFormat) {
    SimpleDateFormat dateFormat = new SimpleDateFormat(NAME_FORMAT,
        Locale.getDefault());
    Calendar calendar = Calendar.getInstance();
    StringBuilder fileName = new StringBuilder();
    fileName.append(DEFAULT_IMAGE_FOLDER + "_")
        .append(dateFormat.format(calendar.getTime())).append(".")
        .append(fileFormat);
    return fileName.toString();
  }

  public static String getImageFileName() {
    return getFileName(IMAGE_FORMAT_TYPE);
  }

  public static File getImageFileToSave() {
    File dir = getAlbumStorageDir();
    SimpleDateFormat dateFormat = new SimpleDateFormat(NAME_FORMAT,
        Locale.getDefault());
    Calendar calendar = Calendar.getInstance();
    String dateName = dateFormat.format(calendar.getTime());
    StringBuilder imageName = new StringBuilder();
    imageName.append(DEFAULT_IMAGE_FOLDER).append("_").append(dateName)
        .append(".").append(IMAGE_FORMAT_TYPE);
    File file = new File(dir, imageName.toString());
    int imageNum = 0;
    while (file.exists()) {
      imageNum++;
      imageName = new StringBuilder();
      imageName.append(DEFAULT_IMAGE_FOLDER).append("_").append(dateName)
          .append("(").append(imageNum).append(")").append(".")
          .append(IMAGE_FORMAT_TYPE);
      file = new File(dir, imageName.toString());
    }
    return file;
  }

  public static boolean saveImage(Context context, File input) {
    File output = StorageUtil.getImageFileToSave();
    FileInputStream imageFileIn = null;
    FileOutputStream imageFileOut = null;
    try {
      imageFileIn = new FileInputStream(input);
      imageFileOut = new FileOutputStream(output);
      byte[] buffer = new byte[512];
      int length;
      while ((length = imageFileIn.read(buffer)) > 0) {
        imageFileOut.write(buffer, 0, length);
      }
      imageFileIn.close();
      imageFileOut.flush();
      imageFileOut.close();
    } catch (FileNotFoundException e) {
      LogUtils.e(TAG, e.getMessage());
      return false;
    } catch (IOException e) {
      LogUtils.e(TAG, e.getMessage());
      return false;
    } finally {
      if (imageFileIn != null) {
        try {
          imageFileIn.close();
        } catch (IOException e) {
          LogUtils.e(TAG, e.getMessage());
        }
      }
      if (imageFileOut != null) {
        try {
          imageFileOut.flush();
        } catch (IOException e) {
          LogUtils.e(TAG, e.getMessage());
        }
      }
      if (imageFileOut != null) {
        try {
          imageFileOut.close();
        } catch (IOException e) {
          LogUtils.e(TAG, e.getMessage());
        }
      }
    }
    StorageUtil.galleryAddPic(context, output.getPath());
    return true;
  }

  public static boolean hasExternalStorage() {
    return Environment.MEDIA_MOUNTED.equals(Environment
        .getExternalStorageState());
  }

  public static void galleryAddPic(Context context, String pathFile) {
    Intent mediaScanIntent = new Intent(
        Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
    File f = new File(pathFile);
    Uri contentUri = UriCompat.fromFile(context, f);
    mediaScanIntent.setData(contentUri);
    context.sendBroadcast(mediaScanIntent);
  }

  public static boolean savePhotoChatDetail(Context context, String pathFile) {
    File fileInput = new File(pathFile);
    File fileOutput = getPhotoFileTempByUser(context);
    FileInputStream fileInputStream = null;
    FileOutputStream fileOutputStream = null;
    boolean isSuccess = false;
    try {
      fileInputStream = new FileInputStream(fileInput);
      fileOutputStream = new FileOutputStream(fileOutput);
      byte[] b = new byte[fileInputStream.available()];
      int length = 0;
      while ((length = fileInputStream.read(b)) != -1) {
        fileOutputStream.write(b, 0, length);
      }
      fileOutputStream.flush();
      fileOutputStream.close();
      fileInputStream.close();
      isSuccess = true;
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        if (fileInputStream != null) {
          fileInputStream.close();
        }
        if (fileOutputStream != null) {
          fileOutputStream.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    StorageUtil.galleryAddPic(context, fileOutput.getPath());
    return isSuccess;
  }
}
