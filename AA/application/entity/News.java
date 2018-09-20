package com.application.entity;

import android.text.TextUtils;
import com.application.util.Utility;
import java.io.Serializable;

/**
 * Created by HungHN on 4/5/2016.
 */
public class News implements Serializable {

  /*
  <a>              - hyperlink.
  <b>              - bold, use as last resort <h1>-<h3>, <em>, and <strong> are preferred.
  <blockquote>     - specifies a section that is quoted from another source.
  <code>           - defines a piece of computer code.
  <del>            - delete, used to indicate modifications.
  <dd>             - describes the item in a <dl> description list.
  <dl>             - description list.
  <dt>             - title of an item in a <dl> description list.
  <em>             - emphasized.
  <h1>, <h2>, <h3> - headings.
  <i>              - italic.
  <img>            - specifies an image tag.
  <kbd>            - represents user input (usually keyboard input).
  <li>             - list item in an ordered list <ol> or an unordered list <ul>.
  <ol>             - ordered list.
  <p>              - paragraph.
  <pre>            - pre-element displayed in a fixed width font and and unchanged line breaks.
  <s>              - strikethrough.
  <sup>            - superscript text appears 1/2 character above the baseline used for footnotes and other formatting.
  <sub>            - subscript appears 1/2 character below the baseline.
  <strong>         - defines important text.
  <strike>         - strikethrough is deprecated, use <del> instead.
  <ul>             - unordered list.
  <br>             - line break.
  <hr>*/
  private static final String[] TAG_LIST = {"a", "b", "blockquote", "code", "del", "dd", "dl",
      "dt", "em", "h1", "h2", "h3", "i", "img", "kbd", "li", "ol", "p", "pre",
      "s", "sup", "sub", "strong", "strike", "ul", "br", "hr", "u"};
  private String id;
  private String banner;
  private String title;
  private String fromDate;
  private String toDate;
  private String htmlContent;
  //hiepuh
  private int haveCMcode;
  private String url;

  public News() {
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getHtmlContent() {
    return htmlContent;
  }

  public void setHtmlContent(String htmlContent) {
    // Replace all \n (enter) character to <br/>
    htmlContent = htmlContent.replace("\n", "<br/>");
    String nonHtml = createHtmlCss(htmlContent);
//        // Find regex to get all xml tag
//        final Pattern tagPattern = Pattern.compile("<([^\\s>/]+).*?>");
//        // Get all xml tag
//        final Matcher tagMatcher = tagPattern.matcher(htmlContent);
//        // New message builder
//        final StringBuffer sb = new StringBuffer(htmlContent.length());
//        // Remove all tag but keep some special tag
//        while (tagMatcher.find()) {
//            String group = tagMatcher.group(1);
//            String replace = "";
//
//            if (group.equalsIgnoreCase("a")) {
//                // Check if not be a url, remove a tag
//                final String URL_PATTERN = "(https?)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
//                final Pattern aPattern = Pattern.compile(URL_PATTERN);
//                final Matcher aMatcher = aPattern.matcher(tagMatcher.group());
//                if(aMatcher.find()){
//                    replace = tagMatcher.group();
//                }
//            } else {
//                for (String tag : TAG_LIST) {
//                    if (group.equalsIgnoreCase(tag)) {
//                        replace = tagMatcher.group();
//                        break;
//                    }
//                }
//            }
//
//            LogUtils.w("Tam dep trai", replace);
//            tagMatcher.appendReplacement(sb, replace);
//        }
//        String nonHtml = tagMatcher.appendTail(sb).toString().trim();
    this.htmlContent = nonHtml;
  }

  private String createHtmlCss(String html) {
    StringBuilder builder = new StringBuilder(
        "<body id = \"body_app\" style = \" left: 0; right: 0; height: auto; width: auto; \">");
    builder.append(html);
    builder.append("</body>");
    return builder.toString();
  }

  public String getBanner() {
    return banner;
  }

  public void setBanner(String banner) {
    this.banner = banner;
  }

  public String getDeadline() {
    return Utility.convertDateToJapanDate(Utility.FULL_DATE_JP_DATETIME_FORMAT, fromDate)
        + " ~ " + Utility.convertDateToJapanDate(Utility.FULL_DATE_JP_TIME_FORMAT, toDate);
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public boolean hasBanner() {
    return !TextUtils.isEmpty(banner);
  }

  public String getFromDate() {
    return fromDate;
  }

  public void setFromDate(String fromDate) {
    this.fromDate = fromDate;
  }

  public String getToDate() {
    return toDate;
  }

  public void setToDate(String toDate) {
    this.toDate = toDate;
  }

  //hiepuh
  public int getHaveCMcode() {
    return haveCMcode;
  }

  public void setHaveCMcode(int haveCMcode) {
    this.haveCMcode = haveCMcode;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }
}
