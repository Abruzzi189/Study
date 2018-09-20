package com.application.util.preferece;

public class ChatMessagePreference extends BasePrefers {

  private static ChatMessagePreference preference;

  private ChatMessagePreference() {
    super();
  }

  public static synchronized ChatMessagePreference getInstance() {
    if (preference == null) {
      preference = new ChatMessagePreference();
    }

    return preference;
  }

  @Override
  protected String getFileNamePrefers() {
    return "chat_message";
  }

  public void saveMessage(String userId, String msg) {
    getEditor().putString(userId, msg).commit();
  }

  public String getMessage(String userId) {
    return getPreferences().getString(userId, "");
  }

  public void removeMessage(String userId) {
    getEditor().remove(userId).commit();
  }

  public void cleverAll() {
    getEditor().clear().commit();
  }
}