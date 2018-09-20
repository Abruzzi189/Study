package com.application.ui.settings;

import android.content.Intent;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Call application send email when click link "mailto:address@email.com"
 */
public class EmailWebViewClient extends WebViewClient {

  @Override
  public boolean shouldOverrideUrlLoading(WebView view, String url) {
    if (url == null) {
      return false;
    }
    if (url.startsWith("mailto:")) {
      url = url.replaceFirst("mailto:", "");
      //
      String theEmail = "", theEmailCC = "", theEmailBCC = "", theSubject = "", theBody = "";
      Boolean hasEmail = true, hasEmailCC = url.contains("&cc="), hasEmailBCC = url
          .contains("&bcc="), hasSubject = url.contains("&subject="), hasBody = url
          .contains("&body=");
      int posEmail = 0, posEmailCC = hasEmailCC ? url.indexOf("&cc=") : 0, posEmailBCC =
          hasEmailBCC ? url
              .indexOf("&bcc=") : 0, posSubject = hasSubject ? url
          .indexOf("&subject=") : 0, posBody = hasBody ? url
          .indexOf("&body=") : 0;
      //
      if (hasEmail && hasEmailCC) {
        theEmail = url.substring(posEmail, posEmailCC - posEmail);
      } else if (hasEmail && hasEmailBCC) {
        theEmail = url.substring(posEmail, posEmailBCC - posEmail);
      } else if (hasEmail && hasSubject) {
        theEmail = url.substring(posEmail, posSubject - posEmail);
      } else if (hasEmail && hasBody) {
        theEmail = url.substring(posEmail, posBody - posEmail);
      } else if (hasEmail) {
        theEmail = url;
      } else { /* theEmail = url; */
      }

      if (hasEmailCC && hasEmailBCC) {
        theEmailCC = url
            .substring(posEmailCC, posEmailBCC - posEmailCC);
      } else if (hasEmailCC && hasSubject) {
        theEmailCC = url.substring(posEmailCC, posSubject - posEmailCC);
      } else if (hasEmailCC && hasBody) {
        theEmailCC = url.substring(posEmailCC, posBody - posEmailCC);
      } else if (hasEmailCC) {
        theEmailCC = url.substring(posEmailCC);
      } else { /* theEmailCC = url.substring(posEmailCC); */
      }
      theEmailCC = theEmailCC.replace("&cc=", "");

      if (hasEmailBCC && hasSubject) {
        theEmailBCC = url.substring(posEmailBCC, posSubject
            - posEmailBCC);
      } else if (hasEmailBCC && hasBody) {
        theEmailBCC = url.substring(posEmailBCC, posBody - posEmailBCC);
      } else if (hasEmailBCC) {
        theEmailBCC = url.substring(posEmailBCC);
      } else { /* theEmailBCC = url.substring(posEmailBCC); */
      }
      theEmailBCC = theEmailBCC.replace("&bcc=", "");

      if (hasSubject && hasBody) {
        theSubject = url.substring(posSubject, posBody - posSubject);
      } else if (hasSubject) {
        theSubject = url.substring(posSubject);
      } else { /* theSubject = url.substring(posSubject); */
      }
      theSubject = theSubject.replace("&subject=", "");

      if (hasBody) {
        theBody = url.substring(posBody);
      } else { /* theBody = url.substring(posBody); */
      }
      theBody = theBody.replace("&body=", "");

      theSubject = theSubject.replace("%20", " ");
      theBody = theBody.replace("%20", " ").replace("%0A", "\n");
      //
      Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
      emailIntent.setType("message/rfc822");
      //
      emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
          new String[]{theEmail,});
      if (hasEmailCC) {
        emailIntent.putExtra(android.content.Intent.EXTRA_CC,
            theEmailCC);
      }
      if (hasEmailBCC) {
        emailIntent.putExtra(android.content.Intent.EXTRA_BCC,
            theEmailBCC);
      }
      if (hasSubject) {
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
            theSubject);
      }
      if (hasBody) {
        emailIntent
            .putExtra(android.content.Intent.EXTRA_TEXT, theBody);
      }
      //
      view.getContext().startActivity(emailIntent);
      //
      return true;
    }
    return false;
  }
}
