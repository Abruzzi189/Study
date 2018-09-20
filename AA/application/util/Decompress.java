package com.application.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Decompress {

  private String _zipFile;
  private String _location;

  public Decompress(String zipFile, String unZipfile) {
    _zipFile = zipFile;
    _location = unZipfile;

    _dirChecker("");
  }

  public void unzip() {
    try {
      FileInputStream fin = new FileInputStream(_zipFile);
      ZipInputStream zin = new ZipInputStream(fin);
      ZipEntry ze = zin.getNextEntry();
      while (ze != null) {
        LogUtils.v("Decompress", "Unzipping " + ze.getName());

        if (ze.isDirectory()) {
          _dirChecker(ze.getName());
        } else {
          FileOutputStream fout = new FileOutputStream(_location
              + File.separator + ze.getName());
          byte[] b = new byte[1024];
          int length = -1;
          while ((length = zin.read(b)) != -1) {
            fout.write(b, 0, length);
          }
          zin.closeEntry();
          fout.flush();
          fout.close();
        }
        ze = zin.getNextEntry();
      }
      zin.close();
    } catch (Exception e) {
      LogUtils.e("Decompress", e.getMessage());
    }
    // delete zip file
    File file = new File(_zipFile);
    if (file.exists()) {
      file.delete();
    }
  }

  private void _dirChecker(String dir) {
    File f = new File(_location + dir);

    if (!f.isDirectory()) {
      f.mkdirs();
    }
  }
}
