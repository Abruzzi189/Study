/*
 *
 */
package com.application.util;

import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.EditText;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// TODO: Auto-generated Javadoc

/**
 * The Class StringUtils.
 */
public class StringUtils {

  /**
   * The Constant NINE.
   */
  static final char NINE = (char) 0x39;

  /**
   * The Constant ZERO.
   */
  static final char ZERO = (char) 0x30;

  /**
   * The Constant CH_a.
   */
  static final char CH_a = 'a';

  /**
   * The Constant CH_z.
   */
  static final char CH_z = 'z';

  /**
   * The Constant CH_A.
   */
  static final char CH_A = 'A';

  /**
   * The Constant CH_Z.
   */
  static final char CH_Z = 'Z';
  /**
   * The Constant seperators.
   */
  final static String[] seperators = {" ", ".", ",", "-", "_", "=", "/"};
  // Regular Expression
  // you can change the expression based on your need
  private static final String EMAIL_REGEX = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

  // To replace a character at a specified position
  private static final String PHONE_REGEX =                       // sdd = space, dot, or dash
      "(\\+[0-9]+[\\- \\.]*)?"        // +<digits><sdd>*
          + "(\\([0-9]+\\)[\\- \\.]*)?"   // (<digits>)<sdd>*
          + "([0-9][0-9\\- \\.]+[0-9])";//"\\d{3}-\\d{7}";

  // replace char with string
  // Error Messages
  private static final String REQUIRED_MSG = "required";

  // To remove a character
  private static final String EMAIL_MSG = "invalid email";

  // To remove a character at a specified position
  private static final String PHONE_MSG = "###-#######";

  // .,*/abc --> abc
  /**
   * **********************************************************************
   */
  private static char[] charArray = null; // Holds an array of character (used

  // "a.b-c" --> "abc"
  /**
   * The random.
   */
  private static Random random = null; // random object
  /**
   * The number array.
   */
  private static char[] numberArray = null;

  // Create an arrays of characters (A--Z, 0--9)
  static {
    int numOfChars = 'Z' - 'A' + 1;
    int numOfDigits = '9' - '0' + 1;

    random = new Random(); // create a random object

    charArray = new char[numOfChars + numOfDigits];
    for (int i = 0; i < numOfChars; i++) {
      charArray[i] = (char) ('A' + i);
    }
    for (int i = 0; i < numOfDigits; i++) {
      charArray[numOfChars + i] = (char) ('0' + i);
    }
    // System.out.println(charArray);
  }

  static {
    int numOfDigits = 10;
    random = new Random();
    numberArray = new char[numOfDigits];
    for (int i = 0; i < numOfDigits; i++) {
      numberArray[i] = (char) (48 + i);
    }
  }

  /**
   * convert 1 way int array into string
   *
   * @return string of all array items
   */
  public static String convertArrayToString(int[] data) {
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < data.length; i++) {
      builder.append(data[i]);
      if (i < data.length - 1) {
        builder.append(",");
      }
    }
    return builder.toString();
  }

  /**
   * Append string.
   *
   * @param oldS the old s
   * @param pos the pos
   * @param s the s
   * @return the string
   */
  public static String appendString(String oldS, int pos, String s) {
    return (oldS.substring(0, pos) + s + oldS.substring(pos));
  }

  /**
   * Replace char at.
   *
   * @param s the s
   * @param pos the pos
   * @param c the c
   * @return the string
   */
  public static String replaceCharAt(String s, int pos, char c) {
    // return s.substring(0, pos) + c + s.substring(pos + 1);
    StringBuffer buf = new StringBuffer(s);
    buf.setCharAt(pos, c);
    return buf.toString();
  }

  /**
   * Replace char.
   *
   * @param s the s
   * @param a the a
   * @param b the b
   * @return the string
   */
  public static String replaceChar(String s, char a, String b) {
    if (s == null) {
      return null;
    }

    StringBuffer newString = new StringBuffer();
    for (int i = 0; i < s.length(); i++) {
      char cur = s.charAt(i);
      if (cur == a) {
        newString.append(b);
      } else {
        newString.append(cur);
      }
    }
    return newString.toString();
  }

  /**  ***********************************************************************. */
  /* GENERATE RANDOM STRING OF CHARACTERS */

  /**
   * Removes the char.
   *
   * @param s the s
   * @param c the c
   * @return the string
   */
  public static String removeChar(String s, char c) {
    if (s == null) {
      return null;
    }

    StringBuffer newString = new StringBuffer();
    for (int i = 0; i < s.length(); i++) {
      char cur = s.charAt(i);
      if (cur != c) {
        newString.append(cur);
      }
    }
    return newString.toString();
  }
  // to get the random character for
  // the random string)

  /**
   * Removes the char at.
   *
   * @param s the s
   * @param pos the pos
   * @return the string
   */
  public static String removeCharAt(String s, int pos) {
    // return s.substring(0, pos) + s.substring(pos + 1);
    StringBuffer buf = new StringBuffer(s.length() - 1);
    buf.append(s.substring(0, pos)).append(s.substring(pos + 1));
    return buf.toString();
  }

  /**
   * Removes the special chars in front.
   *
   * @param s the s
   * @return the string
   */
  public static String removeSpecialCharsInFront(String s) {
    if (s == null) {
      return null;
    }
    String result = "";
    char currChar;
    for (int i = 0; i < s.length(); i++) {
      currChar = s.charAt(i);
      if ((currChar >= ZERO && currChar <= NINE)
          || (currChar >= CH_a && currChar <= CH_z)
          || (currChar >= CH_A && currChar <= CH_Z)) {
        result = s.substring(i);
        break;
      }
    }
    return result;
  }

  // returns a random string of chars: A--Z, 0--9

  /**
   * Removes the special chars in string.
   *
   * @param s the s
   * @return the string
   */
  public static String removeSpecialCharsInString(String s) {
    if (s == null) {
      return null;
    }
    StringBuffer buffer = new StringBuffer();
    char ch;
    for (int i = 0; i < s.length(); i++) {
      ch = s.charAt(i);
      if ((ch >= ZERO && ch <= NINE) || (ch >= CH_a && ch <= CH_z)
          || (ch >= CH_A && ch <= CH_Z)) {
        buffer.append(ch);
      }
    }
    return buffer.toString();
  }

  /**
   * Only one space between2 words.
   *
   * @param text the text
   * @return the string
   */
  public static String onlyOneSpaceBetween2Words(String text) {
    if (text == null) {
      return null;
    }

    StringBuffer buffer = new StringBuffer();
    boolean lastCharIsSpace = false;
    for (int i = 0; i < text.length(); i++) {
      char ch = text.charAt(i);
      if (ch == 0x20) {
        if (lastCharIsSpace) {
          continue;
        } else {
          lastCharIsSpace = true;
        }
      } else if (lastCharIsSpace) {
        lastCharIsSpace = false;
      }
      buffer.append(ch);
    }
    return buffer.toString();
  }

  /**
   * Checks if is numberic.
   *
   * @param sNumber the s number
   * @return true, if is numberic
   */
  public static boolean isNumberic(String sNumber) {
    if (sNumber == null || "".equals(sNumber)) {
      return false;
    }
    char ch_max = (char) 0x39;
    char ch_min = (char) 0x30;

    for (int i = 0; i < sNumber.length(); i++) {
      char ch = sNumber.charAt(i);
      if ((ch < ch_min) || (ch > ch_max)) {
        return false;
      }
    }
    return true;
  }

  /**
   * ************************************************************************* check the input
   * string is an positive integer?.
   *
   * @param sInput the s input
   * @return boolean
   * @author Hoang Minh Duc ************************************************************************
   */
  public static boolean isInteger(String sInput) {
    try {
      if (isEmptyOrNull(sInput)) {
        return false;
      }
      return sInput.matches("^\\d+(\\\\d+)?$");
    } catch (Exception e) {
      // TODO: handle exception
    }
    return false;
  }

  public static boolean isInteger(Object sInput) {
    try {
      if (isEmptyOrNull(sInput)) {
        return false;
      }
      return isInteger(nullToEmpty(sInput));
    } catch (Exception e) {
      // TODO: handle exception
    }
    return false;
  }

  /**
   * Checks if is alphabet.
   *
   * @param text the text
   * @return true, if is alphabet
   */
  public static boolean isAlphabet(String text) {
    try {
      if (text == null || "".equals(text.trim())) {
        return false;
      }
      return text.matches("[^a-zA-Z0-9_]+$");
    } catch (Exception e) {
      // TODO: handle exception
    }
    return false;
  }

  /**
   * ************************************************************************* check the input
   * string is an integer?.
   *
   * @param sInput the s input
   * @return boolean
   * @author Hoang Minh Duc ************************************************************************
   */
  public static boolean isNumeric(String sInput) {
    if (sInput == null || "".equals(sInput.trim())) {
      return false;
    }
    return sInput.matches("((-|\\+)?[0-9]+(\\.[0-9]+)?)+$");// ^-\\d+(\\.\\d+)?$
  }

  /**
   * Generate random string.
   *
   * @param length the length
   * @return the string
   */
  public static String randomString(int length) {
    char[] ch = new char[length];
    for (int i = 0; i < length; i++) {
      ch[i] = charArray[random.nextInt(charArray.length)];
    }
    return new String(ch);
  }

  /**
   * Generate random number.
   *
   * @param length the length
   * @return the string
   */
  public static String randomNumberString(int length) {
    char[] ch = new char[length];
    for (int i = 0; i < length; i++) {
      ch[i] = numberArray[random.nextInt(numberArray.length)];
    }
    return new String(ch);
  }

  /**
   * Created at 31-10-2014.
   *
   * @param lenOfRndNumber the len of rnd number
   * @return the string
   * @author DucHM
   */
  public static String randNumberString(int lenOfRndNumber) {
    try {
      Random fixRand = new Random();
      String result = "";
      //Tao 1 so ngau nhien nguyen thuoc [0..10]
      for (int i = 0; i < lenOfRndNumber; i++) {
        result += String.valueOf(fixRand.nextInt(10));
      }
      return result;
    } catch (Exception ex) {
      System.out.println(ex.getMessage());
    }
    return null;
  }

  /**
   * Created at 28-02-2015
   *
   * @param lenOfRndNumber the length of random number
   * @return the string
   * @author DucHM
   */
  public static String randNumberStringNotStartWithsZero(int lenOfRndNumber) {
    try {
      Random fixRand = new Random();
      String result = "";
      int min = 1, max = 9;
      //Tao 1 so ngau nhien nguyen tu 0-9
      for (int i = 0; i < lenOfRndNumber; i++) {
        result += String.valueOf(fixRand.nextInt((max - min) + 1) + min);
      }
      return result;
    } catch (Exception ex) {
      System.out.println(ex.getMessage());
    }
    return null;
  }

  /**
   * Created at 28-02-2015.
   *
   * @param min The smallest integer
   * @param max The largest integer
   * @return the integer
   * @author DucHM
   */
  public static int randIntInBetween(int min, int max) {

    // Usually this can be a field rather than a method variable
    Random rand = new Random();

    // nextInt is normally exclusive of the top value,
    // so add 1 to make it inclusive
    int randomNum = rand.nextInt((max - min) + 1) + min;

    return randomNum;
  }

  /**
   * DucHM.
   *
   * @param s the s
   * @return the string
   */
  public static String normal(String s) {
    try {
      if (isEmptyOrNull(s)) {
        return "";
      }
      return s.replaceAll("[^a-zA-Z0-9]+", "");
    } catch (Exception e) {
      // TODO: handle exception
    }
    return "";
  }

  /**
   * Tests if a code point is "whitespace" as defined in the HTML spec.
   *
   * @param c code point to test
   * @return true if code point is whitespace, false otherwise
   */
  public static boolean isWhitespace(int c) {
    return c == ' ' || c == '\t' || c == '\n' || c == '\f' || c == '\r';
  }

  /**
   * Normalise the whitespace within this string; multiple spaces collapse to a single, and all
   * whitespace characters (e.g. newline, tab) convert to a simple space
   *
   * @param string content to normalise
   * @return normalised string
   */
  public static String normaliseWhitespace(String string) {
    StringBuilder sb = new StringBuilder(string.length());
    appendNormalisedWhitespace(sb, string, false);
    return sb.toString();
  }

  /**
   * After normalizing the whitespace within a string, appends it to a string builder.
   *
   * @param accum builder to append to
   * @param string string to normalize whitespace within
   * @param stripLeading set to true if you wish to remove any leading whitespace
   */
  public static void appendNormalisedWhitespace(StringBuilder accum, String string,
      boolean stripLeading) {
    boolean lastWasWhite = false;
    boolean reachedNonWhite = false;

    int len = string.length();
    int c;
    for (int i = 0; i < len; i += Character.charCount(c)) {
      c = string.codePointAt(i);
      if (isWhitespace(c)) {
        if ((stripLeading && !reachedNonWhite) || lastWasWhite) {
          continue;
        }
        accum.append(' ');
        lastWasWhite = true;
      } else {
        accum.appendCodePoint(c);
        lastWasWhite = false;
        reachedNonWhite = true;
      }
    }
  }

/*	public static boolean isEmptyOrNull(StringBuilder input) {
        if (input == null)
			return true;
		int cs = 0;
		while (input.length() > 0 && input.charAt(cs) == ' ') {
			input = input.deleteCharAt(cs);
		}
		if ("".equals(input))
			return true;
		if (input != null && input.length() == 0) return true;
		return false;
	}*/

  /**
   * ************************************************************************* Method
   * replaceString.
   *
   * @param sStr the s str
   * @param oldStr the old str
   * @param newStr the new str
   * @return String ************************************************************************
   * @author Hoang Minh Duc <p> Replace substring oldStr in string sStr by newStr
   */
  public static String replaceString(String sStr, String oldStr, String newStr) {
    sStr = (sStr == null ? "" : sStr);
    String strVar = sStr;
    String tmpStr = "";
    String finalStr = "";
    int stpos = 0, endpos = 0, strLen = 0;
    while (true) {
      strLen = strVar.length();
      stpos = 0;
      endpos = strVar.indexOf(oldStr, stpos);
      if (endpos == -1) {
        break;
      }
      tmpStr = strVar.substring(stpos, endpos);
      tmpStr = tmpStr.concat(newStr);
      strVar = strVar.substring(
          endpos + oldStr.length() > sStr.length() ? endpos : endpos
              + oldStr.length(), strLen);
      finalStr = finalStr.concat(tmpStr);
      stpos = endpos;
    }
    finalStr = finalStr.concat(strVar);
    return finalStr;
  }

  /**
   * Checks if is empty or null.
   *
   * @param input the input
   * @return true, if is empty or null
   */
  public static boolean isEmptyOrNull(String input) {
    if (input == null) {
      return true;
    }
    if (input.trim().length() == 0 || "null".equalsIgnoreCase(input.trim())) {
      return true;
    }
    return false;
  }

  /**
   * Checks if is empty or null.
   *
   * @param input the object
   * @return true, if is empty or null
   */
  public static boolean isEmptyOrNull(Object input) {
    if (input == null) {
      return true;
    }
    if (nullToEmpty(input).trim().length() == 0) {
      return true;
    }
    return false;
  }

  /**
   * ************************************************************************* Method check
   * StringBuffer isEmptyOrNull.
   *
   * @param input the input
   * @return boolean * @author Hoang Minh Duc ************************************************************************
   */
  public static boolean isEmptyOrNull(StringBuffer input) {
    if (input == null) {
      return true;
    }
    String sInput = input.toString();
    if (sInput.length() == 0) {
      return true;
    }
    return false;
  }

  // /<summary>
  // / Chuan hoa mang
  // / VD: A{"aa","er","","45","","","4df","sdf",""}
  // / Sau khi chuan hoa:A{"aa","er","45","4df","sdf"}
  // /</summary>

  /**
   * ************************************************************************* Method check
   * StringBuilder isEmptyOrNull.
   *
   * @param input the input
   * @return boolean * @author Hoang Minh Duc ************************************************************************
   */
  public static boolean isEmptyOrNull(StringBuilder input) {
    if (input == null) {
      return true;
    }
    String sInput = nullToEmpty(input).trim();
    //if ("".equals(sInput.trim())) return true;
    if (sInput.length() == 0) {
      return true;
    }
    return false;
  }

  /**
   * Null to empty.
   *
   * @param input the input
   * @return the string
   */
  public static String nullToEmpty(Object input) {
    return (input == null ? "" : ("null".equals(input) ? "" : input.toString()));
  }

  /**
   * Find number.
   *
   * @param sNumber the s number
   * @return the int
   */
  public static int findNumber(String sNumber) {

    int result = -1;
    if (sNumber == null || "".equals(sNumber)) {
      return result;
    }
    char ch_max = (char) 0x39;
    char ch_min = (char) 0x30;

    for (int i = 0; i < sNumber.length(); i++) {
      char ch = sNumber.charAt(i);
      if ((ch < ch_min) || (ch > ch_max)) {

      } else {
        result = i;
        break;
      }
    }
    return result;
  }

  /**
   * Find char.
   *
   * @param sNumber the s number
   * @return the int
   */
  public static int findChar(String sNumber) {

    int result = 0;
    if (sNumber == null || "".equals(sNumber)) {
      return result;
    }
    char ch_max = (char) 0x39;
    char ch_min = (char) 0x30;

    for (int i = 0; i < sNumber.length(); i++) {
      char ch = sNumber.charAt(i);
      if ((ch < ch_min) || (ch > ch_max)) {
        result = i;
        break;
      }
    }
    return result;
  }

  /**
   * S chuan hoa ten.
   *
   * @param input the input
   * @return the string
   */
  public static String sChuanHoaTen(String input) {
    if (StringUtils.isEmptyOrNull(input)) {
      return "";
    }
    input = input.trim();
    StringBuffer sb = new StringBuffer();
    sb.append(String.valueOf(input.charAt(0)).toUpperCase());
    for (int i = 1; i < input.length(); i++) {
      if (input.charAt(i - 1) == ' ') {
        sb.append(String.valueOf(input.charAt(i)).toUpperCase());
      } else {
        sb.append(String.valueOf(input.charAt(i)).toLowerCase());
      }
    }
    return sb.toString();
  }

  /**
   * Normalize array.
   *
   * @param Arr the arr
   * @return the string[]
   */
  public static String[] normalizeArray(String[] Arr) {
    try {
      if (Arr == null) {
        return null;
      }
      int i = 0;
      int len = Arr.length;
      while (i < len) {
        if ("".equals(Arr[i]) || " ".equals(Arr[i]) || (Arr[i] == null)) {
          for (int j = i; j < len - 1; j++) {
            Arr[j] = Arr[j + 1];
          }
          len = len - 1;
        } else {
          i++;
        }
      }
      String[] temp = new String[len];
      for (int k = 0; k < len; k++) {
        temp[k] = Arr[k];
      }
      return temp;
    } catch (Exception e) {
    }
    return null;
  }

  /**
   * Concatenation two array input into array destination.
   *
   * @param One the one
   * @param Two the two
   * @return the string[]
   * @author Hoang Minh Duc<br>
   */
  public static String[] addUpTowArray(String[] One, String[] Two) {
    try {
      if ((One == null) && (Two == null)) {
        return null;
      }
      if ((One == null) && (Two != null)) {
        return Two;
      }
      if ((One != null) && (Two == null)) {
        return One;
      }
      String[] ArrTemp = new String[One.length + Two.length];
      int element = -1;
      for (int i = 0; i < One.length; i++) {
        element = element + 1;
        ArrTemp[element] = One[i];
      }
      for (int j = 0; j < Two.length; j++) {
        element = element + 1;
        ArrTemp[element] = Two[j];
      }
      return ArrTemp;
    } catch (Exception e) {
    }
    return null;
  }

  /**
   * concatenates two arrays of strings.
   *
   * @param s1 the first array of strings.
   * @param s2 the second array of strings.
   * @return the resulting array with all strings in s1 and s2
   * @author Hoang Minh Duc: 0989664386<br>
   */
  public static String[] concatArray(String[] s1, String[] s2) {
    try {
      if ((s1 == null) && (s2 == null)) {
        return null;
      }
      if ((s1 == null) && (s2 != null)) {
        return s2;
      }
      if ((s1 != null) && (s2 == null)) {
        return s1;
      }

      String[] result = new String[s1.length + s2.length];
      System.arraycopy(s1, 0, result, 0, s1.length);
      System.arraycopy(s2, 0, result, s1.length, s2.length);
      return result;
    } catch (Exception e) {
    }
    return null;
  }

  /**
   * Attach elements of tow array.
   *
   * @param s1 the s1
   * @param s2 the s2
   * @return the string[]
   */
  public static String[] attachElementsOfTowArray(String[] s1, String[] s2) {
    try {
      if ((s1 == null) && (s2 == null)) {
        return null;
      }
      if ((s1 == null) && (s2 != null)) {
        return s2;
      }
      if ((s1 != null) && (s2 == null)) {
        return s1;
      }

      if (s1.length != s2.length) {
        return null;
      }

      String[] result = new String[s1.length];
      for (int i = 0; i < result.length; i++) {
        result[i] = s1[i].concat(s2[i]);
      }
      return result;
    } catch (Exception e) {
    }
    return null;
  }

  /**
   * Attach elements of array.
   *
   * @param array the array
   * @param attach the attach
   * @param beforeOrAfter the before or after
   * @return the string[]
   */
  public static String[] attachElementsOfArray(String[] array, String attach,
      boolean beforeOrAfter) {
    try {
      if (array == null) {
        return null;
      }
      if ((array != null) && StringUtils.isEmptyOrNull(attach)) {
        return array;
      }
      String[] result = new String[array.length];
      if (beforeOrAfter) {
        for (int i = 0; i < result.length; i++) {
          result[i] = attach.concat(array[i]);
        }
      } else {
        for (int i = 0; i < result.length; i++) {
          result[i] = array[i].concat(attach);
        }
      }
      return result;
    } catch (Exception e) {
    }
    return null;
  }

  /**
   * ************************************************************************* Method split String
   * input with parameter compare is regex<br>.
   *
   * @param input the input
   * @param param the param
   * @return String[] ************************************************************************
   * @author Hoang Minh Duc<br>
   */
  public static String[] splits(String input, String param) {
    try {
      if (input == null || "".equals(input.trim())) {
        return null;
      }
      if (input.indexOf(param) < 0) {
        return new String[]{input};
      }
      Vector v = new Vector();
      int index = 0;
      while ((index = input.indexOf(param)) >= 0) {
        String s = input.substring(0, index);
        input = input.substring(index + param.length());
        v.addElement(s);
      }
      v.addElement(input);
      String[] arr = new String[v.size()];
      v.copyInto(arr);
      return arr;
    } catch (Exception e) {
    }
    return null;
  }

  /**
   * Normalize whitespaces.
   *
   * @param input the input
   * @return the string
   */
  public static String normalizeWhitespaces(String input) {
    if (StringUtils.isEmptyOrNull(input)) {
      return null;
    }
    StringBuffer res = new StringBuffer();
    int prevIndex = 0;
    int currIndex = -1;
    int stringLength = input.length();
    String searchString = "  ";
    while ((currIndex = input.indexOf(searchString, currIndex + 1)) >= 0) {
      res.append(input.substring(prevIndex, currIndex + 1));
      while (currIndex < stringLength && input.charAt(currIndex) == ' ') {
        currIndex++;
      }
      prevIndex = currIndex;
    }
    res.append(input.substring(prevIndex));
    return res.toString();
  }

  public static boolean contains(String needle, String... haystack) {
    for (String hay : haystack) {
      if (hay.equals(needle)) {
        return true;
      }
    }
    return false;
  }

  public static boolean containsIgnoreCase(String needle, String... haystack) {
    for (String hay : haystack) {
      if (hay.equalsIgnoreCase(needle)) {
        return true;
      }
    }
    return false;
  }

  /**
   * ************************************************************************* Method copy many
   * element from array source in to array destination\n start position first to position last\n.
   *
   * @param source the source
   * @param nBegin the n begin
   * @param nEnd the n end
   * @return String[]\n VD: source{"10","20","90","99","100"} nBegin=1;nEnd=3\n KQ:
   * target{"20","90","99"} ************************************************************************
   * @author Hoang Minh Duc\n
   */
  public static String[] copyArray(String[] source, int nBegin, int nEnd) {
    try {
      if ((source == null) || (source != null && source.length == 0) || (nBegin > nEnd)) {
        return null;
      }
      if (nEnd >= source.length) {
        nEnd = source.length - 1;
      }
      String[] target = new String[nEnd - nBegin + 1];
      int cs = 0;
      for (int i = nBegin; i <= nEnd; i++) {
        target[cs] = source[i];
        cs++;
      }
      return target;
    } catch (Exception e) {
    }
    return null;
  }

  /**
   * ************************************************************************* Method remove element
   * of array source at position index is value input\n.
   *
   * @param source the source
   * @param index the index
   * @return String[]\n VD: source{"10","20","90","99","100"} index=1\n KQ:
   * target{"10","90","99","100"} ************************************************************************
   * @author Hoang Minh Duc\n
   */
  public static String[] removeElementOfArray(String[] source, int index) {
    try {
      if (source == null || (source != null && source.length == 0)) {
        return null;
      }
      if (source != null && index > source.length) {
        return source;
      }
      String[] target = new String[source.length - 1];
      int cs = 0;
      for (int i = 0; i < source.length; i++) {
        if (i != index) {
          target[cs] = source[i];
          cs++;
        }
      }
      return target;
    } catch (Exception e) {
    }
    return null;
  }

  /**
   * repalce all String parameter replace with String parameter with in data String.
   *
   * @param input the input
   * @param replace the replace
   * @param with the with
   * @return the string
   * @author Hoang Minh Duc: 0989664386<br>
   */
  public static String replaceAll(String input, String replace, String with) {
    try {
      if (input == null || "".equals(input)) {
        return null;
      }
      if (replace == null || "".equals(replace)) {
        return input;
      }
      if (input.length() < replace.length()) {
        return input;
      }
      int from = -1;
      while ((from = input.indexOf(replace)) > -1) {
        input = input.substring(0, from) + with + input.substring(from + replace.length());
      }
      return input;
    } catch (Exception e) {
    }
    return null;
  }

  /**
   * repalce String parameter replace with String parameter with in data String.
   *
   * @param input the input
   * @param replace the replace
   * @param with the with
   * @return the string
   * @author Hoang Minh Duc: 0989664386<br>
   */
  public static String replace(String input, String replace, String with) {
    try {
      if (input == null || "".equals(input)) {
        return null;
      }
      if (replace == null || "".equals(replace)) {
        return input;
      }
      if (input.length() < replace.length()) {
        return input;
      }
      int from = input.indexOf(replace);
      if (from > -1) {
        input = input.substring(0, from) + with + input.substring(from + replace.length());
      }
      return input;
    } catch (Exception e) {
    }
    return null;
  }
  // dd-MM-yyyy hh:mm
  // dd/MM/yyyy hh:mm

  /**
   * repalce String parameter replace with String parameter with in data String.
   *
   * @param input the input
   * @param replace the replace
   * @param with the with
   * @return the string
   * @author Hoang Minh Duc: 0989664386<br>
   */
  public static String replaceAllWithoutLastElement(String input, String replace, String with) {
    try {
      if (input == null || "".equals(input)) {
        return null;
      }
      if (replace == null || "".equals(replace)) {
        return input;
      }
      if (input.length() < replace.length()) {
        return input;
      }
      int from = -1;
      int lastIndexOf = input.lastIndexOf(replace);
      while ((from = input.indexOf(replace)) > -1 && from != lastIndexOf) {
        input = input.substring(0, from) + with + input.substring(from + replace.length());
      }
      return input;
    } catch (Exception e) {
    }
    return null;
  }

  /**
   * repalce all String parameter replace with String parameter with in data String[].
   *
   * @param input the input
   * @param replace the replace
   * @param with the with
   * @return the string[]
   * @author Hoang Minh Duc: 0989664386<br>
   */
  public static String[] replaceAll(String[] input, String replace, String with) {
    try {
      if (input == null) {
        return null;
      }
      if (replace == null || "".equals(replace)) {
        return input;
      }
      for (int i = 0; i < input.length; i++) {
        input[i] = replaceAll(input[i], replace, with);
      }
      return input;
    } catch (Exception e) {
    }
    return null;
  }

  /**
   * ************************************************************************* Method copy many
   * element from array source in to array destination\n start position first to position last\n.
   *
   * @param from the from
   * @param to the to
   * @param source the source
   * @return String[]\n VD: source{"10","20","90","99","100"} from = 1;to = 3\n KQ:
   * target{"20","90","99"} ************************************************************************
   * @author Hoang Minh Duc\n
   */
  public static String[] copyArray(int from, int to, String[] source) {
    try {
      if (source == null) {
        return null;
      }
      if (from < 0) {
        return null;
      }
      if (from > to) {
        return null;
      }
      if (to >= source.length) {
        return null;
      }
      String[] target = new String[to - from + 1];
      System.arraycopy(source, from, target, 0, target.length);
      return target;
    } catch (Exception e) {
    }
    return null;
  }
  // get formatDate for String

  /**
   * ************************************************************************* Method copy
   * numElement from array source in to array destination\n start position first to position
   * last\n.
   *
   * @param from the from
   * @param numElement the num element
   * @param source the source
   * @return String[]\n VD: source{"10","20","90","99","100"} from = 1;numElement = 3\n KQ:
   * target{"20","90","99"} ************************************************************************
   * @author Hoang Minh Duc\n
   */
  public static String[] copyElementsOfArray(int from, int numElement, String[] source) {
    try {
      if (source == null) {
        return null;
      }
      if (from < 0) {
        return null;
      }
      if (from + numElement > source.length) {
        return null;
      }
      String[] target = new String[numElement];
      System.arraycopy(source, from, target, 0, target.length);
      return target;
    } catch (Exception e) {
    }
    return null;
  }

  /**
   * method get index of element in string array .
   *
   * @param input the input
   * @param element the element
   * @return the index element of array
   */
  public static int getIndexElementOfArray(String[] input, String element) {
    if (input == null || isEmptyOrNull(element)) {
      return -1;
    }
    int idx = -1;// not exists
    for (int i = 0; i < input.length; i++) {
      if (element.equals(input[i])) {
        return i;
      }
    }
    return idx;
  }

  /**
   * String to datedd m myyyyhhmm.
   *
   * @param ddMMyyyyhhmm the dd m myyyyhhmm
   * @return the date
   */
  public static Date stringToDateddMMyyyyhhmm(String ddMMyyyyhhmm) {
    if (isEmptyOrNull(ddMMyyyyhhmm)) {
      return null;
    }
    ddMMyyyyhhmm = ddMMyyyyhhmm.replace('/', '-');
    Date dt;
    Calendar cal = Calendar.getInstance();
    String strArr[] = splits(ddMMyyyyhhmm, " ");
    if (strArr.length > 0) {
      String strDMY[] = splits(strArr[0], "-");
      String strHM[] = splits(strArr[1], ":");
      cal.setTime(new Date());
      cal.set(Calendar.YEAR, Integer.parseInt("0" + strDMY[2]));
      cal.set(Calendar.MONTH, Integer.parseInt("0" + strDMY[1]));
      cal.set(Calendar.DATE, Integer.parseInt("0" + strDMY[0]));
      cal.set(Calendar.HOUR, Integer.parseInt("0" + strHM[1]));
      cal.set(Calendar.MINUTE, Integer.parseInt("0" + strHM[0]));
      dt = cal.getTime();
      return dt;
    }
    return null;
  }

  /**
   * String to datedd m myyyy.
   *
   * @param ddMMyyyy the dd m myyyy
   * @return the date
   */
  public static Date stringToDateddMMyyyy(String ddMMyyyy) {
    if (isEmptyOrNull(ddMMyyyy)) {
      return null;
    }
    ddMMyyyy = ddMMyyyy.replace('/', '-');
    Date dt;
    Calendar cal = Calendar.getInstance();
    String strArr[] = splits(ddMMyyyy, " ");
    if (strArr.length > 0) {
      String strDMY[] = splits(strArr[0], "-");
      cal.setTime(new Date());
      cal.set(Calendar.YEAR, Integer.parseInt("0" + strDMY[2]));
      cal.set(Calendar.MONTH, Integer.parseInt("0" + strDMY[1]));
      cal.set(Calendar.DATE, Integer.parseInt("0" + strDMY[0]));
      dt = cal.getTime();
      return dt;
    }
    return null;
  }

  /**
   * ************************************************************************* Method check String
   * isValidDate.
   *
   * @param ddMMyyyy the dd m myyyy
   * @return boolean
   * @author Hoang Minh Duc ************************************************************************
   */
  public static boolean isValidDate(String ddMMyyyy) {
    try {
      if (isEmptyOrNull(ddMMyyyy) || "null".equals(ddMMyyyy)) {
        return false;
      }
      if (ddMMyyyy.startsWith("/") || ddMMyyyy.startsWith(" ") || ddMMyyyy.startsWith("-")) {
        ddMMyyyy = ddMMyyyy.substring(1);
      }
      if (ddMMyyyy.endsWith("/") || ddMMyyyy.endsWith(" ") || ddMMyyyy.endsWith("-")) {
        ddMMyyyy = ddMMyyyy.substring(0, ddMMyyyy.length() - 1);
      }
      if (ddMMyyyy.length() != 10 && ddMMyyyy.length() != 8) {
        return false;
      }
      if (ddMMyyyy.length() == 8 && (ddMMyyyy.indexOf("/") != -1 || ddMMyyyy.indexOf("-") != -1
          || ddMMyyyy.indexOf(" ") != -1)) {
        return false;
      }
      if (ddMMyyyy.length() == 8) {
        ddMMyyyy =
            ddMMyyyy.substring(0, 2) + "-" + ddMMyyyy.substring(2, 4) + "-" + ddMMyyyy.substring(4);
      }
      ddMMyyyy = replaceAll(ddMMyyyy, "/", "-");
      ddMMyyyy = replaceAll(ddMMyyyy, " ", "-");
      String[] arrDate = splits(ddMMyyyy, "-");
      if (arrDate == null) {
        return false;
      }
      if (arrDate.length != 3) {
        return false;
      }
      int dd = 0;
      int mm = 0;
      int yyyy = 0;
      try {
        dd = Integer.parseInt(arrDate[0]);
        mm = Integer.parseInt(arrDate[1]);
        yyyy = Integer.parseInt(arrDate[1]);
      } catch (NumberFormatException e) {
        return false;
      }
      if (arrDate[0].length() == 2 && dd > 0 && dd < 32 && arrDate[1].length() == 2 && mm > 0
          && mm < 13 && arrDate[2].length() == 4 && yyyy != 0) {
        return true;
      }
    } catch (Exception e) {
    }
    return false;
  }

  /**
   * Format date.
   *
   * @param ddMMyyyy the dd m myyyy
   * @return the string
   */
  public static String formatDate(String ddMMyyyy) {
    try {
      String regex = "/";
      if (isEmptyOrNull(ddMMyyyy) || "null".equals(ddMMyyyy)) {
        return "";
      }
      if (ddMMyyyy.startsWith("/") || ddMMyyyy.startsWith(" ") || ddMMyyyy.startsWith("-")) {
        ddMMyyyy = ddMMyyyy.substring(1);
      }
      if (ddMMyyyy.endsWith("/") || ddMMyyyy.endsWith(" ") || ddMMyyyy.endsWith("-")) {
        ddMMyyyy = ddMMyyyy.substring(0, ddMMyyyy.length() - 1);
      }
      if (ddMMyyyy.length() != 10 && ddMMyyyy.length() != 8) {
        return "";
      }
      if (ddMMyyyy.length() == 8 && (ddMMyyyy.indexOf("/") != -1 || ddMMyyyy.indexOf("-") != -1
          || ddMMyyyy.indexOf(" ") != -1)) {
        return "";
      }
      if (ddMMyyyy.length() == 8) {
        ddMMyyyy =
            ddMMyyyy.substring(0, 2) + "-" + ddMMyyyy.substring(2, 4) + "-" + ddMMyyyy.substring(4);
      }
      if (ddMMyyyy.indexOf("/") != -1) {
        regex = "/";
      }
      if (ddMMyyyy.indexOf("-") != -1) {
        regex = "-";
      }
      if (ddMMyyyy.indexOf(" ") != -1) {
        regex = " ";
      }

      ddMMyyyy = replaceAll(ddMMyyyy, "/", "-");
      ddMMyyyy = replaceAll(ddMMyyyy, " ", "-");
      String[] arrDate = splits(ddMMyyyy, "-");
      if (arrDate == null) {
        return "";
      }
      if (arrDate.length != 3) {
        return "";
      }
      int dd = 0;
      int mm = 0;
      int yyyy = 0;
      try {
        dd = Integer.parseInt(arrDate[0]);
        mm = Integer.parseInt(arrDate[1]);
        yyyy = Integer.parseInt(arrDate[1]);
      } catch (NumberFormatException e) {
        return "";
      }
      if (arrDate[0].length() == 2 && dd > 0 && dd < 32 && arrDate[1].length() == 2 && mm > 0
          && mm < 13 && arrDate[2].length() == 4 && yyyy != 0) {
        return "dd" + regex + "mm" + regex + "yyyy";
      }
    } catch (Exception e) {
    }
    return "";
  }

  /**
   * Checks if is valid date.
   *
   * @param ddMMyyyy the dd m myyyy
   * @return true, if is valid date
   */
  public static boolean isValidDate(StringBuffer ddMMyyyy) {
    if (isEmptyOrNull(ddMMyyyy)) {
      return false;
    }
    return (isValidDate(ddMMyyyy.toString()));
  }

  /**
   * Checks if is valid date.
   *
   * @param ddMMyyyy the dd m myyyy
   * @return true, if is valid date
   */
  public static boolean isValidDate(StringBuilder ddMMyyyy) {
    if (isEmptyOrNull(ddMMyyyy)) {
      return false;
    }
    return (isValidDate(ddMMyyyy.toString()));
  }

  /**
   * Convert string array to vector.
   *
   * @param input the input
   * @return the vector
   */
  public static Vector<String> convertStringArrayToVector(String[] input) {
    if (input == null) {
      return null;
    }
    Vector<String> vec = new Vector<String>();
    vec.addAll(Arrays.asList(input));
    return vec;
  }

  /**
   * Validate whether the argument string can be parsed into a legal date.<br /> <p> Does check for
   * formating errors and illegal data (so an invalid month or day number is detected).
   *
   * @param dateStr the date str
   * @param allowPast set to true to allow dates in the past, false if only dates in the future
   * should be allowed.
   * @param formatStr date format string to be used to validate against
   * @return true if a correct date and conforms to the restrictions
   * @author Hoang Minh Duc
   */
  public static boolean isValidDate(String dateStr, boolean allowPast, String formatStr) {
    if (formatStr == null) {
      return false; // or throw some kinda exception, possibly a InvalidArgumentException
    }
    SimpleDateFormat df = new SimpleDateFormat(formatStr);
    Date testDate = null;
    try {
      testDate = df.parse(dateStr);
    } catch (java.text.ParseException e) {
      // invalid date format
      return false;
    }
    if (!allowPast) {
      // initialise the calendar to midnight to prevent the current day from being rejected
      Calendar cal = Calendar.getInstance();
      cal.set(Calendar.HOUR_OF_DAY, 0);
      cal.set(Calendar.MINUTE, 0);
      cal.set(Calendar.SECOND, 0);
      cal.set(Calendar.MILLISECOND, 0);
      if (cal.getTime().after(testDate)) {
        return false;
      }
    }
    // now test for legal values of parameters
    if (!df.format(testDate).equals(dateStr)) {
      return false;
    }
    return true;
  }

  /**
   * Checks if is valid date new.
   *
   * @param sDate the s date
   * @param fomat the fomat
   * @return true, if is valid date new
   * @author Hoang Minh Duc
   */
  public static boolean isValidDateNew(String sDate, String fomat) {
    if (isEmptyOrNull(sDate)) {
      return false;
    }
    //set the format to use as a constructor argument
    SimpleDateFormat dateFormat = new SimpleDateFormat(fomat);
    if (sDate.trim().length() != dateFormat.toPattern().length()) {
      return false;
    }
    dateFormat.setLenient(false);
    //parse the sDate parameter
    try {
      dateFormat.parse(sDate.trim());
    } catch (java.text.ParseException e) {
      return false;
    }
    return true;
  }

  /**
   * Checks if is 8x93 service number.
   *
   * @param sServiceNumber the s service number
   * @return true, if is 8x93 service number
   */
  public static boolean is8x93ServiceNumber(String sServiceNumber) {
    String s8x93ServiceNumber = "8093,8193,8293,8393,8493,8593,8693,8793";
    if (isInteger(sServiceNumber) && s8x93ServiceNumber.contains(sServiceNumber)) {
      return true;
    }
    return false;
  }

  /**
   * Checks if is phone number.
   *
   * @param sPhoneNumber the s phone number
   * @return true, if is phone number
   */
  public static boolean isPhoneNumber(String sPhoneNumber) {
    if (sPhoneNumber == null || "".equals(sPhoneNumber.trim())) {
      return false;
    }
    return sPhoneNumber.matches("^[0-9]{10,11}");
  }

  /**
   * check a String is vietnames mobile phone.
   *
   * @param sPhoneNumber the s phone number
   * @return true, if is viet name mobile phone
   * @author Hoàng Minh Đức: 0989664386
   */
  public static boolean isVietNameMobilePhone(String sPhoneNumber) {
    if (sPhoneNumber == null || "".equals(sPhoneNumber.trim())) {
      return false;
    }
    return sPhoneNumber.matches("^(09\\d{8}|01\\d{9}|84\\d{9,10})");
  }

  public final static boolean isValidEmail(CharSequence target) {
    if (TextUtils.isEmpty(target)) {
      return false;
    }
    return Patterns.EMAIL_ADDRESS.matcher(target).matches();
  }

  /**
   * Checks if is email.
   *
   * @param sEmail the s email
   * @return true, if is email
   */
  public static boolean isEmail(String sEmail) {
    if (sEmail == null || "".equals(sEmail.trim())) {
      return false;
    }
    return sEmail.matches("\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}\\b");
    //^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$
  }

  /**
   * Checks if is email with rfc2822.
   *
   * @param sEmail the s email
   * @return true, if is email with rfc2822
   */
  public static boolean isEmailWithRfc2822(String sEmail) {
    if (sEmail == null || "".equals(sEmail.trim())) {
      return false;
    }
    return sEmail.matches(
        "^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$");
  }

  /**
   * Split.
   *
   * @param str the str
   * @param sep the sep
   * @param maxNum the max num
   * @return the string[]
   */
  public static String[] split(String str, char sep, int maxNum) {
    if ((str == null) || (str.length() == 0)) {
      return new String[0];
    }

    Vector results = maxNum == 0 ? new Vector() : new Vector(maxNum);

    StringBuffer buf = new StringBuffer();
    for (int i = 0; i < str.length(); i++) {
      char c = str.charAt(i);

      if (c == sep) {
        if ((maxNum != 0) && (results.size() + 1 == maxNum)) {
          for (; i < str.length(); i++) {
            buf.append(str.charAt(i));
          }
        }
        results.addElement(buf.toString());
        buf.setLength(0);
      } else {
        buf.append(c);
      }
    }

    if (buf.length() > 0) {
      results.addElement(buf.toString());
    }

    return toStringArray(results);
  }

  /**
   * To string array.
   *
   * @param strings the strings
   * @return the string[]
   */
  private static String[] toStringArray(Vector strings) {
    String[] result = new String[strings.size()];
    for (int i = 0; i < strings.size(); i++) {
      result[i] = strings.elementAt(i).toString();
    }
    return result;
  }

  /**
   * Chomp.
   *
   * @param inStr the in str
   * @return the string
   */
  public static String chomp(String inStr) {
    if ((inStr == null) || ("".equals(inStr))) {
      return inStr;
    }

    char lastChar = inStr.charAt(inStr.length() - 1);
    if (lastChar == '\r') {
      return inStr.substring(0, inStr.length() - 1);
    }
    if (lastChar == '\n') {
      String tmp = inStr.substring(0, inStr.length() - 1);
      if ("".equals(tmp)) {
        return tmp;
      }
      lastChar = tmp.charAt(tmp.length() - 1);
      if (lastChar == '\r') {
        return tmp.substring(0, tmp.length() - 1);
      }

      return tmp;
    }

    return inStr;
  }

  /**
   * Create a random string of letters and digits alternating, no duplicate letters and numbers.
   *
   * @param LenOfRndNumber the len of rnd number
   * @return String
   * @author mobile.apps.pro.vn@gmail.com
   */
  public static String genRandomString(int LenOfRndNumber) {
    String result = "";
    try {
      String[] chars = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o",
          "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};
      String[] numbers = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
      Random fixRand = new Random();

      int status = fixRand.nextInt(2);// 0 is random number; 1 is random number
      int idx = 0;
      int i = 0;
      while (i < LenOfRndNumber) {
        idx = fixRand.nextInt(10);// 0 <= idx < 10
        if (status == 0 && !result.contains(numbers[idx])) {
          status = 1;
          result += numbers[idx];
          i++;
        } else if (status == 1 && !result.contains(chars[idx])) {
          status = 0;
          result += chars[idx];
          i++;
        }
      }
    } catch (Exception e) {
    }
    return result;
  }

  /**
   * Create a random string of letters and digits alternating, no duplicate letters and numbers.
   *
   * @param LenOfRndNumber the len of rnd number
   * @return String
   * @author duchm@vtm.vn
   */
  public static String genRandomStringNew(int LenOfRndNumber) {
    String result = "";
    try {
      String[] chars = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o",
          "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};
      Random fixRand = new Random();

      int status = fixRand.nextInt(2);// 0 is random number; 1 is random number
      int idx = 0;
      int i = 0;
      while (i < LenOfRndNumber) {
        idx = fixRand.nextInt(10);// 0 <= idx < 10
        if (status == 0 && !result.contains("" + idx)) {
          status = 1;
          result += "" + idx;
          i++;
        } else if (status == 1 && !result.contains(chars[idx])) {
          status = 0;
          result += chars[idx];
          i++;
        }
      }
    } catch (Exception e) {
    }
    return result;
  }

  /**
   * Gets the random int in between.
   *
   * @param min the min
   * @param max the max
   * @return the random int in between
   */
  public static int randomIntInBetween(int min, int max) {
    Random rand = new Random();
    return min + rand.nextInt(max - min);
  }

  /**
   * Checks url is correct.
   *
   * @param url the input
   * @return true, if is empty or null
   */
  public static boolean isUrl(String url) {
    Pattern pattern = Patterns.WEB_URL;
    Matcher m = pattern.matcher(url);
    if (m.find()) {
      return true;
    }
    return false;
  }

  /**
   * Convert String to Double.
   */
  public static Double toDouble(String sNumber) {
    if (sNumber == null || sNumber.length() == 0) {
      return 0.00d;
    }
    if (!isNumeric(sNumber)) {
      return 0.00d;
    }
    return Double.parseDouble(sNumber);
  }

  /**
   * Convert String to Float.
   */
  public static Float toFloat(String sNumber) {
    if (sNumber == null || sNumber.length() == 0) {
      return 0.00f;
    }
    if (!isNumeric(sNumber)) {
      return 0.00f;
    }
    return Float.parseFloat(sNumber);
  }

  /**
   * Gets the query map.
   *
   * @param query the query
   * @return the query map
   */
  public static Map<String, String> getQueryMap(String query) {
    if (isEmptyOrNull(query)) {
      return null;
    }
    String[] params = query.split("&");
    Map<String, String> map = new HashMap<String, String>();
    for (String param : params) {
      try {
        String name = param.split("=")[0];
        String value = param.split("=")[1];
        map.put(name, value);
      } catch (Exception e) {
      }
    }
    return map;
  }

  /**
   * String to int.
   *
   * @param strVal the str val
   * @param intDefault the int default
   * @return the int
   */
  public static int stringToInt(String strVal, int intDefault) {
    try {
      return Integer.parseInt(strVal);
    } catch (NumberFormatException e) {
      return intDefault;
    }
  }

  /**
   * Returns a string containing the tokens joined by delimiters.
   *
   * @param delimiter the delimiter
   * @param tokens an array objects to be joined. Strings will be formed from the objects by calling
   * object.toString().
   * @return the string
   */
  public static String join(CharSequence delimiter, Object[] tokens) {
    StringBuilder sb = new StringBuilder();
    boolean firstTime = true;
    for (Object token : tokens) {
      if (firstTime) {
        firstTime = false;
      } else {
        sb.append(delimiter);
      }
      sb.append(token);
    }
    return sb.toString();
  }

  /**
   * Returns a string containing the tokens joined by delimiters.
   *
   * @param delimiter the delimiter
   * @param tokens an array objects to be joined. Strings will be formed from the objects by calling
   * object.toString().
   * @return the string
   */
  public static String join(CharSequence delimiter, Iterable tokens) {
    StringBuilder sb = new StringBuilder();
    boolean firstTime = true;
    for (Object token : tokens) {
      if (firstTime) {
        firstTime = false;
      } else {
        sb.append(delimiter);
      }
      sb.append(token);
    }
    return sb.toString();
  }

  // call this method when you need to check email validation
  public static boolean isEmailAddress(EditText editText, boolean required) {
    return isValid(editText, EMAIL_REGEX, EMAIL_MSG, required);
  }

  // call this method when you need to check phone number validation
  public static boolean isPhoneNumber(EditText editText, boolean required) {
    return isValid(editText, PHONE_REGEX, PHONE_MSG, required);
  }

  // return true if the input field is valid, based on the parameter passed
  public static boolean isValid(EditText editText, String regex, String errMsg, boolean required) {

    String text = editText.getText().toString().trim();
    // clearing the error, if it was previously set by some other values
    editText.setError(null);

    // text required and editText is blank, so return false
    if (required && !hasText(editText)) {
      return false;
    }

    // pattern doesn't match so returning false
    if (required && !Pattern.matches(regex, text)) {
      Log.e("Matches", "Pattern.matches=" + text);
      editText.setError(errMsg);
      return false;
    }

    return true;
  }

  // check the input field has any text or not
  // return true if it contains text otherwise false
  public static boolean hasText(EditText editText) {

    String text = editText.getText().toString().trim();
    editText.setError(null);

    Log.e("HasText", "textH=" + text);

    // length 0 means there is no text
    if (text.length() == 0) {
      editText.setError(REQUIRED_MSG);
      return false;
    }

    return true;
  }

  /**
   * Returns true if s contains any character other than letters, numbers, or spaces.  Returns false
   * otherwise.
   */
  public static boolean containsSpecialCharacter(String input) {
    return (input == null) ? false : input.matches("[^A-Za-z0-9 ]+$");
  }

  /**
   * Returns true if s contains any character other than letters, numbers, or spaces.  Returns false
   * otherwise.
   */
  public static boolean containsSpecialCharacter(String input, boolean ignoreCase) {
    Pattern pattern;
    if (ignoreCase) {
      pattern = Pattern.compile("[^a-z0-9 ]+$", Pattern.CASE_INSENSITIVE);
    } else {
      pattern = Pattern.compile("[^a-z0-9 ]+$");
    }
    Matcher m = pattern.matcher(input);
    return m.find();
  }

  /**
   * @param regex If regex String is null or empty then set default equals [^A-Za-z0-9 ]+$
   * @author DucHM Returns true if s contains any character other than letters, numbers, or spaces.
   * Returns false otherwise.
   */
  public static boolean containsSpecialCharacter(String input, String regex) {
    if (isEmptyOrNull(regex)) {
      regex = "[^A-Za-z0-9 ]+$";
    }
    return (input == null) ? false : input.matches(regex);
  }

  /**
   * Replace all start regex String in resource string to new String input Use String.trim() method
   * to get rid of whitespaces (spaces, new lines etc.) from the beginning and end of the string.
   *
   * @author Created by Robert on 17 Feb 2017
   */
  public static String replaceAllStartsWith(Object inputObject, String stringNeedReplace,
      String replacement) {
    if (isEmptyOrNull(inputObject)) {
      return "";
    }
    String inputString = nullToEmpty(inputObject);
    while (inputString.startsWith(stringNeedReplace)) {
      inputString = nullToEmpty(inputString.replaceFirst(stringNeedReplace, replacement)).trim();
    }
    return inputString;
  }

  /**
   * @author Created by Robert on 17 Feb 2017 Replace last regex String in resource string to new
   * String input using regex expression
   */
  public static String replaceLast(String input, String regex, String to) {
    if (isEmptyOrNull(input)) {
      return "";
    }
    return input.replaceFirst("(^.+)" + regex + "(.+$)", "$1" + to + "$2");
  }

  /**
   * @author Created by Robert on 17 Feb 2017 Replace last regex String in resource string to new
   * String input
   */
  public static String replaceLastString(String input, String from, String to) {
    if (isEmptyOrNull(input)) {
      return "";
    }
    int lastIndex = input.lastIndexOf(from);
    if (lastIndex < 0) {
      return input;
    }
    String tail = input.substring(lastIndex).replaceFirst(from, to);
    return input.substring(0, lastIndex) + tail;
  }

  /**
   * Replace all last regex String in resource string to new String input Use String.trim() method
   * to get rid of whitespaces (spaces, new lines etc.) from the beginning and end of the string.
   *
   * @author Created by Robert on 17 Feb 2017
   */
  public static String replaceAllEndsWith(Object inputObject, String stringNeedReplace,
      String replacement) {
    if (isEmptyOrNull(inputObject)) {
      return "";
    }
    String inputString = nullToEmpty(inputObject);
    while (inputString.endsWith(stringNeedReplace)) {
      inputString = nullToEmpty(replaceLast(inputString, stringNeedReplace, replacement)).trim();
    }
    return inputString;
  }

  /**
   * TODO Trim all line breaks appear and in String resource
   *
   * @return boolean value
   * @author Created by Robert on 17 Feb 2017
   */
  public static String trimLineBreaksAndWhiteSpaces(String input) {

    //String newline = System.getProperty("line.separator");
    input = nullToEmpty(input).trim();

    return input;
  }

  /**
   * TODO Check if Object is only contains line breaks
   *
   * @return boolean value
   * @author Created by Robert on 17 Feb 2017
   */
  public static String trimLineBreaksAndWhiteSpaces(Object input) {

    String sInput = nullToEmpty(input);

    return trimLineBreaksAndWhiteSpaces(sInput);
  }

  /**
   * TODO Check if String is only contains line breaks
   *
   * @return boolean value
   * @author Created by Robert on 17 Feb 2017
   */
  public static boolean isOnlyLineBreaks(String input) {
    String newline = System.getProperty("line.separator");
    String regex = "[" + newline + "]+";
    return nullToEmpty(input).matches(regex);
  }

  /**
   * TODO Check if Object is only contains line breaks
   *
   * @return boolean value
   * @author Created by Robert on 17 Feb 2017
   */
  public static boolean isOnlyLineBreaks(Object input) {
    return isOnlyLineBreaks(nullToEmpty(input));
  }

  /**
   * The main method.
   *
   * @param args the arguments
   */
  public static void main(String[] args) {
    System.out.println(replaceAllWithoutLastElement(
        "/storage/emulated/0/.Okazu/okazu_design/adr_849345438151432306483page5.2.jpg", ".", "_"));

    System.out.println("isAlphabet=" + isAlphabet("半角英数だけで入力"));
    // startItem=1 && numItems=5 & len = 6;
    //String[] result = new String[] {"10","20","90","99","100"};
    //attachElementsOfArray(result, " lần", true);
        /*result = copyElementsOfArray(1, 4, result);
		for (int i = 0; i < result.length; i++) {
			System.out.println("result[" + i + "]=" + result[i]);
		}
		*/
//		System.out.println("------------------------------------------------------------------");
//		StringBuffer sBff = new StringBuffer().append("23/08/2011");
//		System.out.println("sBff=" + isValidDate(sBff));
//		System.out.println("------------------------------------------------------------------");
//		System.out.println("IM NAP 0348729814|8693".substring("IM NAP 0348729814|8693".indexOf("|") + 1));
    //---------------------------------------------------//
    //System.out.println(isValidDate("11/12/2011"));

    //String s = "abc<b>-123df-345-ds-ertm-mre</b>";
    //System.out.println(s.substring(s.indexOf("</b>") + "</b>".length()));
  }
}
