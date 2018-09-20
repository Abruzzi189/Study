package com.application.status;

public interface IStatusChatChanged {

  void create(MessageInDB msgInDB);

  void update(MessageInDB msgInDB);

  void resendFile(MessageInDB msgInDB);
}
