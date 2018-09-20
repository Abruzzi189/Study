package com.application.connection.request;

public class ListDefaultStickerCategoryRequest extends RequestParams {

  private static final long serialVersionUID = 5348959062852609958L;
  private String language; // Id of the subject will be reported
  private int skip; // Type of report - define in array xml file
  private int take; // Type of this content - buzz, image, user

  public ListDefaultStickerCategoryRequest(String token, String lang,
      int skip, int take) {
    super();
    this.api = "list_default_sticker_category";
    this.token = token;
    this.language = lang;
    this.skip = skip;
    this.take = take;
  }
}