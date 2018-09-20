package com.application.uploader;

import com.application.Config;
import com.application.connection.ResponseData;
import com.application.connection.request.RequestParams;
import java.io.File;

public class FileUploadRequest extends RequestParams implements UploadRequest {

  private static final long serialVersionUID = -103215869607615999L;
  public String mHashSum;
  private String mToken;
  private File mFile;
  private String mFileName;
  private String mType;

  public FileUploadRequest(String token, File file, String fileName, String type) {
    this.api = "upl_file_version_2";
    mToken = token;
    mFile = file;
    mFileName = fileName;
    mType = type;
  }

  @Override
  public String toURL() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(Config.IMAGE_SERVER_URL);
    stringBuilder.append("api=");
    stringBuilder.append(this.api);
    stringBuilder.append("&token=");
    stringBuilder.append(this.mToken);
    stringBuilder.append("&sum=");
    stringBuilder.append(this.mHashSum);
    return stringBuilder.toString();
  }

  @Override
  public File getFile() {
    return mFile;
  }

  @Override
  public String getFileName() {
    return mFileName;
  }

  @Override
  public String getType() {
    return mType;
  }

  @Override
  public UploadResponse parseResponseData(ResponseData responseData) {
    return new FileUploadResponse(responseData);
  }

  @Override
  public String getToken() {
    return mToken;
  }

  @Override
  public void setHashSum(String hashSum) {
    this.mHashSum = hashSum;
  }
}
