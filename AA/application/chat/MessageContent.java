package com.application.chat;

public class MessageContent {

  public MessageContentType mMessageContentType;

  ;
  public String mContent;
  public MessageContent() {

  }

  public enum MessageContentType {
    MSG_CONTENT_TYPE_TEXT_EMOJI,
    MSG_CONTENT_TYPE_STICKER,
    MSG_CONTENT_TYPE_LOCATION,
    MSG_CONTENT_TYPE_PHOTO,
    MSG_CONTENT_TYPE_AUDIO,
  }
}
