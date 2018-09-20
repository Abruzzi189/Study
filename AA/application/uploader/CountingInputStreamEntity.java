package com.application.uploader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.http.entity.InputStreamEntity;

public class CountingInputStreamEntity extends InputStreamEntity {

  private UploadListener listener;
  private long length;

  public CountingInputStreamEntity(InputStream instream, long length) {
    super(instream, length);
    this.length = length;
  }

  public void setUploadListener(CountingInputStreamEntity.UploadListener listener) {
    this.listener = listener;
  }

  @Override
  public void writeTo(OutputStream outstream) throws IOException {
    super.writeTo(new CountingOutputStream(outstream));
  }

  public static interface UploadListener {

    public void onChange(int percent);
  }

  class CountingOutputStream extends OutputStream {

    private long counter = 0l;
    private OutputStream outputStream;

    public CountingOutputStream(OutputStream outputStream) {
      this.outputStream = outputStream;
    }

    @Override
    public void write(int oneByte) throws IOException {
      this.outputStream.write(oneByte);
      counter++;
      if (listener != null) {
        int percent = (int) ((counter * 100) / length);
        listener.onChange(percent);
      }
    }
  }

}
