package com.application.connection.request;

public class ReportRequest extends RequestParams {

  private static final long serialVersionUID = 5348959062852609958L;
  private String subject_id;    //Id of the subject will be reported
  private int rpt_type;    //Type of report - define in array xml file
  private int subject_type;  //Type of this content - buzz, image, user

  public ReportRequest(String token, String subject_id, int rpt_type, int subject_type) {
    super();
    this.api = "rpt";
    this.token = token;
    this.subject_id = subject_id;
    this.rpt_type = rpt_type;
    this.subject_type = subject_type;
  }
}