package com.application.uploader;

import com.j256.ormlite.field.DatabaseField;

public class UploadModel {

  @DatabaseField(id = true)
  private String id;
  @DatabaseField
  private String filePath;
  @DatabaseField
  private String urlUpload;
  @DatabaseField
  private int progress;
  @DatabaseField
  private int status;

  public UploadModel() {
  }

  public UploadModel(String id, String filePath, String urlUpload) {
    this.id = id;
    this.filePath = filePath;
    this.urlUpload = urlUpload;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public int getProgress() {
    return progress;
  }

  public void setProgress(int progress) {
    this.progress = progress;
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public String getFilePath() {
    return filePath;
  }

  public void setFilePath(String filePath) {
    this.filePath = filePath;
  }

  public String getUrlUpload() {
    return urlUpload;
  }

  public void setUrlUpload(String urlUpload) {
    this.urlUpload = urlUpload;
  }

  @Override
  public String toString() {
    return "UploadModel [id=" + id + ", filePath=" + filePath
        + ", urlUpload=" + urlUpload + ", progress=" + progress
        + ", status=" + status + "]";
  }

}
