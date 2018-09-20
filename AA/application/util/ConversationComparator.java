package com.application.util;

import com.application.entity.ConversationItem;
import java.text.ParseException;
import java.util.Comparator;
import java.util.Date;
import java.util.TimeZone;

public class ConversationComparator implements Comparator<ConversationItem> {

  @Override
  public int compare(ConversationItem item1, ConversationItem item2) {
    Date date1 = new Date();
    Date date2 = new Date();
    try {
      Utility.YYYYMMDDHHMMSSSSS.setTimeZone(TimeZone.getTimeZone("GMT"));
      date1 = Utility.YYYYMMDDHHMMSSSSS.parse(item1.getSentTime());
      date2 = Utility.YYYYMMDDHHMMSSSSS.parse(item2.getSentTime());
    } catch (ParseException e) {
      e.printStackTrace();
      LogUtils.e("ParseException", "ParseException" + e.getMessage());
    }
    return date2.compareTo(date1);
  }

}