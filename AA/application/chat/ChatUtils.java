package com.application.chat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.util.Base64;
import com.application.chat.EmojiPanel.MEDIA_TYPE;
import com.application.chat.EmojiPanel.Media;
import com.application.chat.MessageContent.MessageContentType;
import com.application.provider.UriCompat;
import com.application.service.data.StickerCategoryInfo;
import com.application.util.Decompress;
import com.application.util.Emoji;
import com.application.util.EmojiUtils;
import com.application.util.LogUtils;
import com.application.util.PhotoUtils;
import com.application.util.Utility;
import glas.bbsystem.R;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import vn.com.ntqsolution.chatserver.pojos.message.Message;
import vn.com.ntqsolution.chatserver.pojos.message.messagetype.MessageType;

public class ChatUtils {

  public static final String IMG_EXTENSION = ".png";
  // Setting grid size of Emoji panel
  public final static int NUM_COLUM_EMOJI_ON_PAGE = 5;
  public final static int NUM_COLUM_STICKER_ON_PAGE = 4;
  public final static int NUM_ITEM_EMOJI_ON_PAGE = NUM_COLUM_EMOJI_ON_PAGE * 4;
  public final static int NUM_ITEM_STICKER_ON_PAGE = NUM_COLUM_STICKER_ON_PAGE * 2;
  private static final String TAG = "ChatUtils";
  private final static String patternSticker = "<n3\\?t=s>(.+)<J8\\+>";
  private final static String patternLocation = "<n3\\?t=l>(.+)<J8\\+>";
  private final static String STICKER = "stickers";
  private final static String THUMBNAIL = "thumbnail";
  private final static String INDEX = "index";
  private final static String ORDER = "order";

  public static void parseMessage(String text, MessageContent messageContent) {
    if (messageContent == null || text == null || text.length() == 0) {
      return;
    }
    messageContent.mMessageContentType = MessageContentType.MSG_CONTENT_TYPE_TEXT_EMOJI;
    messageContent.mContent = text;

    do {
      Pattern ppp = Pattern.compile(patternSticker);
      Matcher m = ppp.matcher(text);

      if (m.find()) {
        messageContent.mMessageContentType = MessageContentType.MSG_CONTENT_TYPE_STICKER;
        messageContent.mContent = m.group(1);
        break;
      }

      ppp = Pattern.compile(patternLocation, Pattern.DOTALL);
      m = ppp.matcher(text);
      if (m.find()) {
        messageContent.mMessageContentType = MessageContentType.MSG_CONTENT_TYPE_LOCATION;
        messageContent.mContent = m.group(1);
        break;
      }
    } while (false);
  }

  public static boolean parseLocationMessage(String text, LocationMessage lm) {
    boolean result = true;

    try {
      String[] part = text.split("\\|");
      if (part == null || part.length != 3) {
        result = false;
      } else {
        lm.setLongitude(Double.parseDouble(part[0]));
        lm.setLatitude(Double.parseDouble(part[1]));
        lm.setAddress(part[2]);
      }
    } catch (NullPointerException npe) {
      result = false;
    } catch (NumberFormatException nfe) {
      result = false;
    }

    return result;
  }

  public static String getPathStickerByPackageAndId(Context context,
      String contentMsg) {
    try {
      String[] data = contentMsg.split("_");
      String packageId = data[0];
      String idSticker = data[1];
      File file = new File(context.getExternalFilesDir(null), STICKER);
      File packageSticker = new File(file, packageId);
      File sticker = new File(packageSticker, idSticker);
      return sticker.getPath();
    } catch (Exception exception) {
      exception.printStackTrace();
    }

    return null;
  }

  public static String getStickerId(String contentMsg) {
    String[] data = contentMsg.split("_");
    String idSticker = data[1];
    if (idSticker != null) {
      return idSticker;
    } else {
      return "";
    }
  }

  public static String getStickerPackageId(String contentMsg) {
    String[] data = contentMsg.split("_");
    String packageId = data[0];
    if (packageId != null) {
      return packageId;
    } else {
      return "";
    }
  }

  public static String getPathStickerById(Context context, String idSticker) {
    File file = new File(context.getExternalFilesDir(null), STICKER);
    return getSticker(file, idSticker);
  }

  private static String getSticker(File file, String id) {
    if (file.isDirectory()) {
      for (File child : file.listFiles()) {
        if (child.isDirectory()) {
          return getSticker(child, id);
        } else {
          String name = child.getName();
          if (name.equalsIgnoreCase(id)) {
            return child.getPath();
          }
        }
      }
    }
    return null;
  }

  public static void copy(Context context) {
    File file = new File(context.getExternalFilesDir(null), STICKER
        + "/sticker1");
    file.mkdirs();
    copyStickerAssetToSdCard(context, "index");
    copyStickerAssetToSdCard(context, "100000.png");
    copyStickerAssetToSdCard(context, "100001.png");
    copyStickerAssetToSdCard(context, "100002.png");
    copyStickerAssetToSdCard(context, "100003.png");
    copyStickerAssetToSdCard(context, "100004.png");
    copyStickerAssetToSdCard(context, "100005.png");
    copyStickerAssetToSdCard(context, "100006.png");
    copyStickerAssetToSdCard(context, "100007.png");

  }

  public static void copyStickerAssetToSdCard(Context context, String name) {
    InputStream inputStream;
    try {
      inputStream = context.getAssets().open(name);
      File file = new File(new File(context.getExternalFilesDir(null),
          STICKER + "/sticker1"), name);

      OutputStream outputStream = new FileOutputStream(file);
      byte[] b = new byte[1024];
      int length = 0;
      while ((length = inputStream.read(b)) != -1) {
        outputStream.write(b, 0, length);
      }
      outputStream.flush();
      outputStream.close();
      inputStream.close();
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  private static boolean saveStickerFile(Context context, String fileName,
      byte[] data, String index) {
    int targSize = context.getResources().getDimensionPixelSize(
        R.dimen.item_grid_media_ticker_chat_size_pixel);

    File fileZip = new File(new File(context.getExternalFilesDir(null),
        STICKER), "sticker_tmp");
    File fileUnzip = new File(new File(context.getExternalFilesDir(null),
        STICKER), fileName);
    if (!fileUnzip.exists()) {
      fileUnzip.mkdir();
    }
    FileOutputStream fileOutputStream;
    try {
      fileOutputStream = new FileOutputStream(fileZip);
      fileOutputStream.write(data);
      fileOutputStream.flush();
      fileOutputStream.close();

      // save index file
      FileOutputStream outputStream = new FileOutputStream(fileUnzip
          + File.separator + "index");
      outputStream.write(index.getBytes());
      outputStream.flush();
      outputStream.close();

      // decompress file
      Decompress des = new Decompress(fileZip.getPath(),
          fileUnzip.getPath());
      des.unzip();

      // resize ticker after unzip
      if (fileUnzip.listFiles() != null) {
        for (File child : fileUnzip.listFiles()) {
          if (child.isDirectory()) {
            LogUtils.e("ChatUtils-lines 214", "isDirectory");
          } else {
            String name = child.getName();
            if (!name.equals(INDEX)) {
              String folderPatch = fileUnzip.getPath();
              createThumbnailSticker(folderPatch, name, targSize,
                  targSize);
            }
          }
        }
      }

      return true;
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return false;
  }

  public static List<Uri> getListSticker(Context context, String fileName) {
    List<Uri> list = new ArrayList<Uri>();
    File f = new File(context.getExternalFilesDir(null), STICKER);
    File fileFolderSticker = new File(f, fileName);
    File fileListSticker = new File(fileFolderSticker, INDEX);
    StringBuilder builder = new StringBuilder();
    try {
      InputStream inputStream = new FileInputStream(fileListSticker);
      BufferedReader bufferedReader = new BufferedReader(
          new InputStreamReader(inputStream));
      String line;
      while ((line = bufferedReader.readLine()) != null) {
        builder.append(line);
      }
      inputStream.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    String data = builder.toString();
    try {
      JSONObject jsonObject = new JSONObject(data);
      JSONArray jsonArray = jsonObject.getJSONArray(ORDER);
      for (int i = 0; i < jsonArray.length(); i++) {
        // add uri with id
        Uri uri = UriCompat.fromFile(context, fileFolderSticker);
        String idSticker = jsonArray.getString(i) + IMG_EXTENSION;
        uri = Uri.withAppendedPath(uri, idSticker);
        list.add(uri);
      }
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return list;
  }

  /**
   * Generate message sticker
   */
  public static String generateMessageSticker(Uri uriSticker) {
    String pathSticker = uriSticker.toString();
    File file = new File(pathSticker);
    File parentFile = file.getParentFile();

    String packageSticker = parentFile.getName();
    String idSticker = uriSticker.getLastPathSegment();

    StringBuilder builder = new StringBuilder();
    builder.append(packageSticker);
    builder.append("_");
    builder.append(idSticker);
    return builder.toString();
  }

  public static String generateLocationMessage(LocationMessage locationMessage) {
    StringBuilder builder = new StringBuilder();
    builder.append(locationMessage.getLongitude());
    builder.append("|");
    builder.append(locationMessage.getLatitude());
    builder.append("|");
    builder.append(locationMessage.getAddress());
    return builder.toString();
  }

  /**
   * Get all emoji and paging
   */
  public static List<Media> getItemMediaEmoji() {
    List<Emoji> emojiList = EmojiUtils.getListEmoji();
    List<Media> itemMedias = new ArrayList<Media>();

    for (int i = 0; i < emojiList.size(); i++) {
      int k = emojiList.size() - i;
      if (k > 0) {
        if (k > NUM_ITEM_EMOJI_ON_PAGE) {
          k = NUM_ITEM_EMOJI_ON_PAGE;
        }
        List<Emoji> emoList = new ArrayList<Emoji>();
        for (int j = 0; j < k; j++) {
          emoList.add(emojiList.get(i));
          i++;
        }
        i--;
        Media itemMedia = new Media();
        itemMedia.type = MEDIA_TYPE.EMOJI;
        itemMedia.emojis = new ArrayList<Emoji>();
        itemMedia.emojis.addAll(emoList);

        itemMedias.add(itemMedia);
      }
    }
    return itemMedias;
  }

  /**
   * Get all sticker in folder and paging
   */
  public static List<Media> getItemMediaSticker(Context context,
      String folderName) {
    List<Uri> stickers = ChatUtils.getListSticker(context, folderName);
    List<Media> itemMedias = new ArrayList<Media>();

    for (int i = 0; i < stickers.size(); i++) {
      int k = stickers.size() - i;
      if (k > 0) {
        if (k > NUM_ITEM_STICKER_ON_PAGE) {
          k = NUM_ITEM_STICKER_ON_PAGE;
        }
        List<Uri> uri = new ArrayList<Uri>();
        for (int j = 0; j < k; j++) {
          uri.add(stickers.get(i));
          i++;
        }
        i--;
        Media itemMedia = new Media();
        itemMedia.type = MEDIA_TYPE.STICKER;
        itemMedia.stickers = new ArrayList<Uri>();
        itemMedia.stickers.addAll(uri);

        itemMedias.add(itemMedia);
      }
    }
    return itemMedias;
  }

  /**
   * Get all folder contain sticker -> save to List
   */
  public static List<String> getListFolderSticker(Context context) {
    File file = new File(context.getExternalFilesDir(null), STICKER);
    List<String> list = new ArrayList<String>();
    if (!file.exists()) {
      return list;
    }
    for (File child : file.listFiles()) {
      if (child.isDirectory()) {
        list.add(child.getName());
      }
    }
    return list;
  }

  /**
   * get thumbnail of forder sticker
   */
  public static Uri getThumbnailStickerFolder(Context context,
      String folderName) {
    try {
      File file = new File(context.getExternalFilesDir(null), STICKER);
      File folderSticker = new File(file, folderName);
      File fileThumbnail = new File(folderSticker, folderName
          + IMG_EXTENSION);
      return UriCompat.fromFile(context, fileThumbnail);
    } catch (Exception exception) {
      exception.printStackTrace();
      return null;
    }
  }

  /**
   * get FileType from content message has type FILE
   */
  public static String getFileType(String value) {
    if (value.contains("|")) {
      try {
        String[] data = value.split("\\|");
        String type = data[1];
        return type;
      } catch (Exception e) {
        e.printStackTrace();
        return "";
      }
    } else {
      return value;
    }
  }

  public static String getMessageByFileType(String type, Context context) {
    int id = -1;
    if (type.equalsIgnoreCase(ChatManager.PHOTO)) {
      id = R.string.sent_a_picture;
    } else if (type.equalsIgnoreCase(ChatManager.AUDIO)) {
      id = R.string.sent_a_audio;
    } else if (type.equalsIgnoreCase(ChatManager.VIDEO)) {
      id = R.string.sent_a_video;
    }
    if (id != -1) {
      return context.getString(id);
    }
    return context.getString(R.string.unknown);
  }

  public static boolean isConfirmMessage(String content) {
    if (content == null) {
      return false;
    }
    if (content.contains("|")) {
      return true;
    }
    return false;
  }

  public static String decryptMessageReceived(String content) {
    if (content == null) {
      return null;
    }
    content = content.replaceAll("&dq", "\"");
    content = content.replaceAll("&bs", "\\\\");
    content = content.replaceAll("&nl", "\n");
    return content;
  }

  public static String encryptMessageToSend(String content) {
    if (content == null) {
      return null;
    }
    content = content.replaceAll("\"", "&dq");
    content = content.replaceAll("\\\\", "&bs");
    content = content.replaceAll("\n", "&nl");
    return content;
  }

  public static boolean saveFileStickerFromString(Context context,
      String fileName, String fileData, String index) {
    File file = new File(context.getExternalFilesDir(null), STICKER);
    if (!file.exists()) {
      file.mkdir();
    }
    try {
      return saveStickerFile(context, fileName, decodeFile(fileData),
          index);
    } catch (Exception exception) {
      LogUtils.e(TAG, String.valueOf(exception.getMessage()));
    }
    return false;
  }

  public static boolean hasAlreadySticker(Context context,
      List<StickerCategoryInfo> list) {
    File file = new File(context.getExternalFilesDir(null), STICKER);
    if (!file.exists() || file.listFiles() != null) {
      return false;
    }

    File[] listFile = file.listFiles();
    int numOfCate = list.size();
    if (numOfCate != listFile.length) {
      return false;
    }

    for (int i = 0; i < numOfCate; i++) {
      if (listFile[i].listFiles() == null) {
        return false;
      }
      if (listFile[i].listFiles().length != list.get(i).getNum()) {
        return false;
      }
    }

    return true;
  }

  public static void deleteStickerCategory(Context context, String stikerCatId) {
    File folderAllStickersCats = new File(
        context.getExternalFilesDir(null), STICKER);
    File folderStickersCat = new File(folderAllStickersCats, stikerCatId);
    deleteFolder(folderStickersCat);
  }

  public static void deleteFolder(File folder) {
    if (folder == null) {
      return;
    }

    if (folder.isDirectory()) {
      File[] childs = folder.listFiles();
      if (childs != null) {
        for (File child : childs) {
          deleteFolder(child);
        }
      }
    }

    folder.delete();
  }

  /**
   * Decodes the base64 string into byte array
   *
   * @param imageDataString - a {@link java.lang.String}
   * @return byte array
   */
  private static byte[] decodeFile(String imageDataString)
      throws IllegalArgumentException {
    return Base64.decode(imageDataString, Base64.DEFAULT);
  }

  public static String getVoipMessage1(Context context, int voipType) {
    context = context.getApplicationContext();
    switch (voipType) {
      case ChatMessage.VoIPActionVideoEnd:
        return context.getString(R.string.voip_action_video_end);
      case ChatMessage.VoIPActionVideoEndBusy:
        return context.getString(R.string.voip_action_video_end_miss1);
      case ChatMessage.VoIPActionVideoEndNoAnswer:
        return context.getString(R.string.voip_action_video_end_no_answer1);
      case ChatMessage.VoIPActionVideoStart:
        return context.getString(R.string.voip_action_video_start);
      case ChatMessage.VoIPActionVoiceEnd:
        return context.getString(R.string.voip_action_voice_end);
      case ChatMessage.VoIPActionVoiceEndBusy:
        return context.getString(R.string.voip_action_voice_end_miss1);
      case ChatMessage.VoIPActionVoiceEndNoAnswer:
        return context.getString(R.string.voip_action_voice_end_no_answer1);
      case ChatMessage.VoIPActionVoiceStart:
        return context.getString(R.string.voip_action_voice_start);
      default:
        return "Not found voip type";
    }
  }

  public static String getVoipMessage(Context context, int voipType) {
    context = context.getApplicationContext();
    switch (voipType) {
      case ChatMessage.VoIPActionVideoEnd:
        return context.getString(R.string.voip_action_video_end);
      case ChatMessage.VoIPActionVideoEndBusy:
        return context.getString(R.string.voip_action_video_end_miss);
      case ChatMessage.VoIPActionVideoEndNoAnswer:
        return context
            .getString(R.string.voip_action_video_end_no_answer);
      case ChatMessage.VoIPActionVideoStart:
        return context.getString(R.string.voip_action_video_start);
      case ChatMessage.VoIPActionVoiceEnd:
        return context.getString(R.string.voip_action_voice_end);
      case ChatMessage.VoIPActionVoiceEndBusy:
        return context.getString(R.string.voip_action_voice_end_miss);
      case ChatMessage.VoIPActionVoiceEndNoAnswer:
        return context
            .getString(R.string.voip_action_voice_end_no_answer);
      case ChatMessage.VoIPActionVoiceStart:
        return context.getString(R.string.voip_action_voice_start);
      default:
        return "Not found voip type";
    }
  }

  public static String getCallDuration1(Context context, CallInfo callInfo) {
    context = context.getApplicationContext();
    String callingDuration;
    String msgVoip = getVoipMessage1(context, callInfo.voipType);
    switch (callInfo.voipType) {
      case ChatMessage.VoIPActionVideoEnd:
      case ChatMessage.VoIPActionVoiceEnd:
        callingDuration = String.format(msgVoip,
            Utility.getCallingDuration(callInfo.duration));
        break;
      case ChatMessage.VoIPActionVideoEndBusy:
      case ChatMessage.VoIPActionVideoEndNoAnswer:
      case ChatMessage.VoIPActionVideoStart:
      case ChatMessage.VoIPActionVoiceEndBusy:
      case ChatMessage.VoIPActionVoiceEndNoAnswer:
      case ChatMessage.VoIPActionVoiceStart:
        callingDuration = msgVoip;
        break;
      default:
        callingDuration = context.getString(R.string.voip_action_none);
        break;
    }
    return callingDuration;
  }

  public static int getVoipIcon(int voipType, boolean isSender) {
    int resourceId = -1;
    switch (voipType) {
      case ChatMessage.VoIPActionVideoEnd:
        resourceId = R.drawable.ic_voip_call_ended;
        break;
      case ChatMessage.VoIPActionVideoEndBusy:
        resourceId = isSender ? R.drawable.ic_voip_call_no_answer_receiver
            : R.drawable.ic_voip_call_busy_receiver;
        break;
      case ChatMessage.VoIPActionVideoEndNoAnswer:
        resourceId = isSender ? R.drawable.ic_voip_call_busy_sender
            : R.drawable.ic_voip_call_no_answer_receiver;
        break;
      case ChatMessage.VoIPActionVideoStart:
        resourceId = R.drawable.ic_voip_call_started;
        break;
      case ChatMessage.VoIPActionVoiceEnd:
        resourceId = R.drawable.ic_voip_call_ended;
        break;
      case ChatMessage.VoIPActionVoiceEndBusy:
        resourceId = isSender ? R.drawable.ic_voip_call_no_answer_receiver
            : R.drawable.ic_voip_call_busy_receiver;
        break;
      case ChatMessage.VoIPActionVoiceEndNoAnswer:
        resourceId = isSender ? R.drawable.ic_voip_call_busy_sender
            : R.drawable.ic_voip_call_no_answer_receiver;
        break;
      case ChatMessage.VoIPActionVoiceStart:
        resourceId = R.drawable.ic_voip_call_started;
        break;
      default:
        // tungdx: if not found -> use default icon to avoid crash app
        resourceId = R.drawable.ic_launcher;
        break;
    }
    return resourceId;
  }

  public static CallInfo getCallInfo(String content) {
    CallInfo call = new CallInfo();
    try {
      call.voipType = Integer.valueOf(content);
    } catch (NumberFormatException nfe) {
      String[] parts = content.split("\\|");
      if (parts != null && parts.length > 1) {
        try {
          call.voipType = Integer.valueOf(parts[0]);
          call.duration = Integer.valueOf(parts[2]);
        } catch (NumberFormatException innernfe) {
          innernfe.printStackTrace();
        }
      }
    }
    return call;
  }

  public static String getCallDuration(Context context, CallInfo callInfo) {
    context = context.getApplicationContext();
    String callingDuration;
    String msgVoip = getVoipMessage(context, callInfo.voipType);
    switch (callInfo.voipType) {
      case ChatMessage.VoIPActionVideoEnd:
      case ChatMessage.VoIPActionVoiceEnd:
        callingDuration = String.format(msgVoip,
            Utility.getCallingDuration(callInfo.duration));
        break;
      case ChatMessage.VoIPActionVideoEndBusy:
      case ChatMessage.VoIPActionVideoEndNoAnswer:
      case ChatMessage.VoIPActionVideoStart:
      case ChatMessage.VoIPActionVoiceEndBusy:
      case ChatMessage.VoIPActionVoiceEndNoAnswer:
      case ChatMessage.VoIPActionVoiceStart:
        callingDuration = msgVoip;
        break;
      default:
        callingDuration = context.getString(R.string.voip_action_none);
        break;
    }
    return callingDuration;
  }

  /**
   * Nhanv: resize bitmap with new width and new height
   */
  public static void createThumbnailSticker(String folderPatch,
      String stickerName, int repWidth, int repHeight) {
    // resize bitmap
    String imgPatch = folderPatch + File.separator + stickerName;
    Bitmap resizeImg = PhotoUtils.decodeSampledBitmapFromFile(imgPatch,
        repWidth, repHeight);

    // create folder thumbnail sticker if not exists
    File thumFolder = new File(folderPatch, THUMBNAIL);
    if (!thumFolder.exists()) {
      thumFolder.mkdirs();
    }

    // create thumbnail for sticker
    File file = new File(thumFolder, stickerName);
    if (file.exists()) {
      file.delete();
    }

    try {
      FileOutputStream outputStream = new FileOutputStream(file);
      resizeImg.compress(CompressFormat.PNG, 100, outputStream);

      outputStream.flush();
      outputStream.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Nhanv: get list thumbnail sticker
   */
  public static List<Uri> getListThumbnailSticker(Context context,
      String fileName) {
    List<Uri> list = new ArrayList<Uri>();
    File f = new File(context.getExternalFilesDir(null), STICKER);
    // folder sticker
    File fileFolderSticker = new File(f, fileName);

    // folder thumbnail of sticker
    File fileFolderThumbnailSticker = new File(fileFolderSticker, THUMBNAIL);

    // filePatch of index file
    File fileListSticker = new File(fileFolderSticker, INDEX);
    StringBuilder builder = new StringBuilder();
    try {
      InputStream inputStream = new FileInputStream(fileListSticker);
      BufferedReader bufferedReader = new BufferedReader(
          new InputStreamReader(inputStream));
      String line;
      while ((line = bufferedReader.readLine()) != null) {
        builder.append(line);
      }
      inputStream.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    String data = builder.toString();
    try {
      JSONObject jsonObject = new JSONObject(data);
      JSONArray jsonArray = jsonObject.getJSONArray(ORDER);
      for (int i = 0; i < jsonArray.length(); i++) {
        // add uri with id
        Uri uri = UriCompat.fromFile(context, fileFolderThumbnailSticker);
        String idSticker = jsonArray.getString(i) + IMG_EXTENSION;
        uri = Uri.withAppendedPath(uri, idSticker);
        list.add(uri);
      }
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return list;
  }

  /**
   * Nhanv: get list thumbnail of sticker
   */
  public static List<Media> getItemMediaThumbnailSticker(Context context,
      String folderName) {
    // get list thumbnail of sticker
    List<Uri> stickers = ChatUtils.getListThumbnailSticker(context,
        folderName);
    List<Media> itemMedias = new ArrayList<Media>();

    for (int i = 0; i < stickers.size(); i++) {
      int k = stickers.size() - i;
      if (k > 0) {
        if (k > NUM_ITEM_STICKER_ON_PAGE) {
          k = NUM_ITEM_STICKER_ON_PAGE;
        }
        List<Uri> uri = new ArrayList<Uri>();
        for (int j = 0; j < k; j++) {
          uri.add(stickers.get(i));
          i++;
        }
        i--;
        Media itemMedia = new Media();
        itemMedia.type = MEDIA_TYPE.STICKER;
        itemMedia.stickers = new ArrayList<Uri>();
        itemMedia.stickers.addAll(uri);

        itemMedias.add(itemMedia);
      }
    }
    return itemMedias;
  }

  /**
   * Only use for message come from outside (not from local)
   */
  private static ChatMessage convertToChatMessage(Message message,
      String chatMessageType) {
    return new ChatMessage(message.id, message.from, false, message.value,
        Utility.getTimeInGMT(), chatMessageType);
  }

  public static ChatMessage convertToPPChatMessage(Message message) {
    ChatMessage chatMessage = convertToChatMessage(message, ChatMessage.PP);
    String msg = ChatUtils.decryptMessageReceived(chatMessage.getContent());
    msg = EmojiUtils.convertTag(msg);
    chatMessage.setContent(msg);
    return chatMessage;
  }

  public static ChatMessage convertToCALLRQChatMessage(Message message) {
    ChatMessage chatMessage = convertToChatMessage(message, ChatMessage.CALLREQUEST);
    String msg = ChatUtils.decryptMessageReceived(chatMessage.getContent());
    msg = EmojiUtils.convertTag(msg);
    chatMessage.setContent(msg);
    return chatMessage;
  }

  public static ChatMessage convertToWinkChatMessage(Message message) {
    return convertToChatMessage(message, ChatMessage.WINK);
  }

  private static String convertFileTypeMessage(String type) {
    String t = null;
    if (type.equals(ChatManager.PHOTO)) {
      t = ChatMessage.PHOTO;
    } else if (type.equals(ChatManager.AUDIO)) {
      t = ChatMessage.AUDIO;
    } else if (type.equals(ChatManager.VIDEO)) {
      t = ChatMessage.VIDEO;
    }
    return t;
  }

  public static ChatMessage convertToFileChatMessage(FileMessage fileMessage) {
    boolean isStartSend = fileMessage.isStartSent();
    String t = convertFileTypeMessage(fileMessage.getFileType());
    if (t == null) {
      return null;
    }
    ChatMessage chatMessage = null;
    String id = null;
    if (isStartSend) {
      id = fileMessage.getMessage().id;
    } else {
      id = fileMessage.getMessageId();
    }
    chatMessage = new ChatMessage(id, fileMessage.getMessage().from, false,
        fileMessage.getMessage().value, Utility.getTimeInGMT(), t,
        fileMessage);
    return chatMessage;
  }

  public static ChatMessage convertToTypingChatMessage(Message message) {
    return convertToChatMessage(message, ChatMessage.TYPING);
  }

  public static ChatMessage convertToGiftChatMessage(Message message) {
    return convertToChatMessage(message, ChatMessage.GIFT);
  }

  public static ChatMessage convertToLocationChatMessage(Message message) {
    return convertToChatMessage(message, ChatMessage.LOCATION);
  }

  public static ChatMessage convertToStickerChatMessage(Message message) {
    return convertToChatMessage(message, ChatMessage.STICKER);
  }

  /**
   * @param isLocal if message is local -> isLocal=true, else isLocal=false
   */
  public static ChatMessage convertToCallChatMessage(Message message,
      boolean isLocal) {
    String msgType = "";
    if (message.msgType == MessageType.SVIDEO) {
      msgType = ChatMessage.STARTVIDEO;
    } else if (message.msgType == MessageType.EVIDEO) {
      msgType = ChatMessage.ENDVIDEO;
    } else if (message.msgType == MessageType.SVOICE) {
      msgType = ChatMessage.STARTVOICE;
    } else if (message.msgType == MessageType.CALLREQ) {
      msgType = ChatMessage.CALLREQUEST;
    } else {
      msgType = ChatMessage.ENDVOICE;
    }
    ChatMessage chatMessage = new ChatMessage(message.id, message.from,
        isLocal, message.value, Utility.getTimeInGMT(), msgType);
    return chatMessage;
  }

  public static class CallInfo {

    public int voipType = ChatMessage.VoIPActionNone;
    public int duration;
  }

  /**
   * TODO Fetching index of by message index.
   *
   * @param msgSearchNeed        the msg search need
   * @param mMessageListResource the message list
   * @return the int number index of message in Message list resource
   * @author Created by Robert Hoang on 21 Mar 2017
   */
  public static int fetchingIndexMsgResourceOfByIndex(ChatMessage msgSearchNeed, final List<ChatMessage> mMessageListResource) {
    if (msgSearchNeed == null || mMessageListResource == null || mMessageListResource.isEmpty())
      return -1;
    try {
      int size = mMessageListResource.size();

      for (int idxResource = 0; idxResource < size; idxResource++) {
        ChatMessage message = mMessageListResource.get(idxResource);

        if (msgSearchNeed.getMessageId().equals(message.getMessageId())) {
          return idxResource;
        }
        if (msgSearchNeed.getContent().startsWith(message.getMessageId() + "|")) {
          return idxResource;
        }

        if (msgSearchNeed.getContent().contains("|" + message.getMessageId() + "|")) {
          return idxResource;
        }
      }
    } catch (Exception e) {
    }
    return -1;
  }
}
