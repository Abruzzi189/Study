package com.application.provider;

import android.content.Context;
import android.net.Uri;
import java.io.File;

/**
 * get uri from file via provider
 */
public class UriCompat {

  /**
   * from android N file:// URIs are not allowed anymore. We should use content:// URIs instead
   *
   * @param context to get package name, it's safe enough to use activity context here
   * @param file destination
   * @return Uri of file capability with android N
   */
  public static Uri fromFile(Context context, File file) {
    return LegacyCompatFileProvider
        .getUriForFile(context, context.getPackageName() + ".com.application.provider", file);
  }

  /**
   * @see #fromFile(Context, File)
   */
  public static Uri fromFile(Context context, String filePath) {
    return LegacyCompatFileProvider
        .getUriForFile(context, context.getPackageName() + ".com.application.provider",
            new File(filePath));
  }
}
