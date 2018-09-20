package com.application.util;

import android.text.TextUtils;
import glas.bbsystem.R;
import java.util.ArrayList;
import java.util.List;


public class EmojiUtils {

  /* ========== Emoji dictionary ========== */
  public static final List<Emoji> DICTIONARY = new ArrayList<Emoji>();

  static {

    DICTIONARY.add(new Emoji(R.drawable.emoji_01, ">-)", "emoji_01"));
    DICTIONARY.add(new Emoji(R.drawable.emoji_02, "o:)", "emoji_02"));
    DICTIONARY.add(new Emoji(R.drawable.emoji_03, "xo", "emoji_03"));
    DICTIONARY.add(new Emoji(R.drawable.emoji_04, "=d>", "emoji_04"));
    DICTIONARY.add(new Emoji(R.drawable.emoji_05, "0-+", "emoji_05"));

    DICTIONARY.add(new Emoji(R.drawable.emoji_06, "~x(", "emoji_06"));
    DICTIONARY.add(new Emoji(R.drawable.emoji_07, ";;)", "emoji_07"));
    DICTIONARY.add(new Emoji(R.drawable.emoji_08, "b-(", "emoji_08"));
    DICTIONARY.add(new Emoji(R.drawable.emoji_09, ":bz", "emoji_09"));
    DICTIONARY.add(new Emoji(R.drawable.emoji_10, ":z", "emoji_10"));

    DICTIONARY.add(new Emoji(R.drawable.emoji_11, ":p", "emoji_11"));
    DICTIONARY.add(new Emoji(R.drawable.emoji_12, "o=>", "emoji_12"));
    DICTIONARY.add(new Emoji(R.drawable.emoji_13, "\">=\">", "emoji_13"));
    DICTIONARY.add(new Emoji(R.drawable.emoji_14, ">:/", "emoji_14"));
    DICTIONARY.add(new Emoji(R.drawable.emoji_15, "=((", "emoji_15"));

//		DICTIONARY.add(new Emoji(R.drawable.emoji_041, ">-)", "emoji_041"));
//		DICTIONARY.add(new Emoji(R.drawable.emoji_042, "o:)", "emoji_042"));
//		DICTIONARY.add(new Emoji(R.drawable.emoji_043, "xo", "emoji_043"));
//		DICTIONARY.add(new Emoji(R.drawable.emoji_044, "=d>", "emoji_044"));
//		DICTIONARY.add(new Emoji(R.drawable.emoji_045, "0-+", "emoji_045"));
//
//		DICTIONARY.add(new Emoji(R.drawable.emoji_046, "~x(", "emoji_046"));
//		DICTIONARY.add(new Emoji(R.drawable.emoji_047, ";;)", "emoji_047"));
//		DICTIONARY.add(new Emoji(R.drawable.emoji_048, "b-(", "emoji_048"));
//		DICTIONARY.add(new Emoji(R.drawable.emoji_049, ":bz", "emoji_049"));
//		DICTIONARY.add(new Emoji(R.drawable.emoji_050, ":z", "emoji_050"));
//
//		DICTIONARY.add(new Emoji(R.drawable.emoji_051, ":p", "emoji_051"));
//		DICTIONARY.add(new Emoji(R.drawable.emoji_052, "o=>", "emoji_052"));
//		DICTIONARY.add(new Emoji(R.drawable.emoji_053, "\">=\">", "emoji_053"));
//		DICTIONARY.add(new Emoji(R.drawable.emoji_054, ">:/", "emoji_054"));
//		DICTIONARY.add(new Emoji(R.drawable.emoji_055, "=((", "emoji_055"));
//
//		DICTIONARY.add(new Emoji(R.drawable.emoji_056, "=:)", "emoji_056"));
//		DICTIONARY.add(new Emoji(R.drawable.emoji_057, ":-c", "emoji_057"));
//		DICTIONARY.add(new Emoji(R.drawable.emoji_058, ":-))", "emoji_058"));
//		DICTIONARY.add(new Emoji(R.drawable.emoji_059, "~:>", "emoji_059"));
//		DICTIONARY.add(new Emoji(R.drawable.emoji_060, ":o)", "emoji_060"));
//
//		DICTIONARY.add(new Emoji(R.drawable.emoji_061, ":-/", "emoji_061"));
//		DICTIONARY.add(new Emoji(R.drawable.emoji_062, ":-??", "emoji_062"));
//		DICTIONARY.add(new Emoji(R.drawable.emoji_063, "b-)", "emoji_063"));
//		DICTIONARY.add(new Emoji(R.drawable.emoji_064, "3:-o", "emoji_064"));
//		DICTIONARY.add(new Emoji(R.drawable.emoji_065, "(sun)", "emoji_065"));
//
//		DICTIONARY.add(new Emoji(R.drawable.emoji_066, ":((", "emoji_066"));
//		DICTIONARY.add(new Emoji(R.drawable.emoji_067, "~o)", "emoji_067"));
//		DICTIONARY.add(new Emoji(R.drawable.emoji_068, "\\:d/", "emoji_068"));
//		DICTIONARY.add(new Emoji(R.drawable.emoji_069, "8->", "emoji_069"));
//		DICTIONARY.add(new Emoji(R.drawable.emoji_070, ">:)", "emoji_070"));
//
//		DICTIONARY.add(new Emoji(R.drawable.emoji_071, ":-$", "emoji_071"));
//		DICTIONARY.add(new Emoji(R.drawable.emoji_072, ":o3", "emoji_072"));
//		DICTIONARY.add(new Emoji(R.drawable.emoji_073, "#-o", "emoji_073"));
//		DICTIONARY.add(new Emoji(R.drawable.emoji_074, "=p~", "emoji_074"));
//		DICTIONARY.add(new Emoji(R.drawable.emoji_075, "%%-", "emoji_075"));
//
//		DICTIONARY.add(new Emoji(R.drawable.emoji_076, ":-l", "emoji_076"));
//		DICTIONARY.add(new Emoji(R.drawable.emoji_077, ";))", "emoji_077"));
//		DICTIONARY.add(new Emoji(R.drawable.emoji_078, "o->", "emoji_078"));
//		DICTIONARY.add(new Emoji(R.drawable.emoji_079, ":!!", "emoji_079"));
//		DICTIONARY.add(new Emoji(R.drawable.emoji_080, "o-)", "emoji_080"));
  }

  public static List<Emoji> getListEmoji() {
    return DICTIONARY;
  }

  public static String convertTag(String text) {
    if (TextUtils.isEmpty(text)) {
      return text;
    }
    text = text.replaceAll("&", "&amp;").replaceAll("<", "&lt;");
    return convertCodeToEmoji(text);
  }

  public static String convertCodeToEmoji(String text) {
    if (TextUtils.isEmpty(text)) {
      return text;
    }

    for (Emoji emoji : DICTIONARY) {
      text = emoji.codeToTag(text);
    }
    return text;
  }

  public static String convertEmojiToCode(String text) {
    if (TextUtils.isEmpty(text)) {
      return text;
    }

    for (Emoji emoji : DICTIONARY) {
      text = emoji.tagToCode(text);
    }
    return text;
  }

  public static boolean hasEmojiCode(String text) {
    if (TextUtils.isEmpty(text)) {
      return false;
    }

    for (Emoji emoji : DICTIONARY) {
      if (text.indexOf(emoji.getCode()) >= 0) {
        return true;
      }
    }
    return false;
  }
}