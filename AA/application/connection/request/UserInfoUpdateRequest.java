package com.application.connection.request;

import android.text.TextUtils;
import com.google.gson.annotations.SerializedName;

public class UserInfoUpdateRequest extends RequestParams {

  /**
   *
   */
  private static final long serialVersionUID = -6656809891073179715L;
  @SerializedName("user_name")
  private String userName;

  @SerializedName("abt")
  private String about;

  @SerializedName("gender")
  private int gender;

  @SerializedName("email")
  private String email;

  @SerializedName("pwd")
  private String password;

  @SerializedName("original_pwd")
  private String originalPass;

  @SerializedName("region")
  private int region;

  @SerializedName("auto_region")
  private int autoRegion;

  @SerializedName("bir")
  private String birthday;

  @SerializedName("job")
  private int job;

  @SerializedName("measurements")
  private int[] threeSizes;

  @SerializedName("cup")
  private int cupSize;

  @SerializedName("cute_type")
  private int cuteType;

  @SerializedName("join_hours")
  private int joinHours;

  @SerializedName("type_of_man")
  private String typeMan;

  @SerializedName("fetish")
  private String fetish;

  @SerializedName("hobby")
  private String hobby;

  public UserInfoUpdateRequest(Builder builder) {
    super();
    this.api = "upd_user_inf";
    this.about = builder.about;
    this.gender = builder.gender;
    this.token = builder.token;
    this.userName = builder.userName;
    this.email = builder.email;
    this.password = builder.password;
    this.originalPass = builder.originalPass;
    this.region = builder.region;
    this.autoRegion = builder.autoRegion;
    this.birthday = builder.birthday;
    this.job = builder.job;
    this.threeSizes = builder.threeSizes;
    this.cupSize = builder.cupSize;
    this.cuteType = builder.cuteType;
    this.joinHours = builder.joinHours;
    this.typeMan = builder.typeMan;
    this.fetish = builder.fetish;
    this.hobby = builder.hobby;
  }

  @Override
  public String toString() {
    String doubleQuote = "\"";
    String colon = ":";
    String openBracket = "{";
    String closeBracket = "}";
    String comma = ",";

    StringBuilder builder = new StringBuilder(openBracket);

    builder.append(doubleQuote).append("token").append(doubleQuote)
        .append(colon).append(doubleQuote).append(token)
        .append(doubleQuote).append(comma);

    builder.append(doubleQuote).append("auto_region").append(doubleQuote)
        .append(colon).append(autoRegion).append(comma);

    builder.append(doubleQuote).append("user_name").append(doubleQuote)
        .append(colon).append(doubleQuote).append(userName)
        .append(doubleQuote).append(comma);

    builder.append(doubleQuote).append("region").append(doubleQuote)
        .append(colon).append(region).append(comma);

    if (cupSize > -1) {
      builder.append(doubleQuote).append("cup").append(doubleQuote)
          .append(colon).append(cupSize).append(comma);
    }

    if (cuteType > -1) {
      builder.append(doubleQuote).append("cute_type").append(doubleQuote)
          .append(colon).append(cuteType).append(comma);
    }

    if (job > -1) {
      builder.append(doubleQuote).append("job").append(doubleQuote)
          .append(colon).append(job).append(comma);
    }

    if (joinHours > -1) {
      builder.append(doubleQuote).append("join_hours")
          .append(doubleQuote).append(colon).append(joinHours)
          .append(comma);
    }

    if (!TextUtils.isEmpty(birthday)) {
      builder.append(doubleQuote).append("bir").append(doubleQuote)
          .append(colon).append(doubleQuote).append(birthday)
          .append(doubleQuote).append(comma);
    }

    if (threeSizes != null && threeSizes.length >= 3) {
      builder.append(doubleQuote)
          .append("measurements")
          .append(doubleQuote)
          .append(colon)
          .append("[" + threeSizes[0] + comma + threeSizes[1] + comma
              + threeSizes[2] + "]").append(comma);
    }

    if (fetish != null) {
      fetish = fetish.replaceAll("\n", "\\\\n");
    }
    builder.append(doubleQuote).append("fetish").append(doubleQuote)
        .append(colon).append(doubleQuote).append(fetish)
        .append(doubleQuote).append(comma);

    if (typeMan != null) {
      typeMan = typeMan.replaceAll("\n", "\\\\n");
    }
    builder.append(doubleQuote).append("type_of_man").append(doubleQuote)
        .append(colon).append(doubleQuote).append(typeMan)
        .append(doubleQuote).append(comma);

    if (about != null) {
      about = about.replaceAll("\n", "\\\\n");
    }
    builder.append(doubleQuote).append("abt").append(doubleQuote)
        .append(colon).append(doubleQuote).append(about)
        .append(doubleQuote).append(comma);

    if (hobby != null) {
      hobby = hobby.replaceAll("\n", "\\\\n");
    }
    builder.append(doubleQuote).append("hobby").append(doubleQuote)
        .append(colon).append(doubleQuote).append(hobby)
        .append(doubleQuote).append(comma);

    builder.append(doubleQuote).append("api").append(doubleQuote)
        .append(colon).append(doubleQuote).append("upd_user_inf")
        .append(doubleQuote);

    builder.append(closeBracket);

    return builder.toString();
  }

  public static class Builder {

    @SerializedName("token")
    private String token;
    @SerializedName("user_name")
    private String userName;
    @SerializedName("abt")
    private String about;
    @SerializedName("gender")
    private int gender;
    @SerializedName("relsh_stt")
    private int relationshipStatus;
    @SerializedName("bdy_tpe")
    private int[] bodyType;
    @SerializedName("inters_in")
    private int interestedIn;
    @SerializedName("lkg_for")
    private int[] lookingFor;
    @SerializedName("height")
    private double height;
    @SerializedName("ethn")
    private int ethnicity;
    @SerializedName("inters")
    private int[] interests;
    @SerializedName("email")
    private String email;
    @SerializedName("pwd")
    private String password;
    @SerializedName("original_pwd")
    private String originalPass;
    @SerializedName("region")
    private int region;
    @SerializedName("auto_region")
    private int autoRegion;
    @SerializedName("bir")
    private String birthday;
    @SerializedName("job")
    private int job;
    @SerializedName("measurements")
    private int[] threeSizes;
    @SerializedName("cup")
    private int cupSize;
    @SerializedName("cute_type")
    private int cuteType;
    @SerializedName("join_hours")
    private int joinHours;
    @SerializedName("type_of_man")
    private String typeMan;
    @SerializedName("fetish")
    private String fetish;
    @SerializedName("hobby")
    private String hobby;

    public Builder() {
    }

    public String getEmail() {
      return this.email;
    }

    public Builder setEmail(String email) {
      this.email = validateData(email);
      return this;
    }

    public String getPassword() {
      return this.password;
    }

    public Builder setPassword(String password) {
      this.password = password;
      return this;
    }

    public Builder setOriginalPass(String originalPass) {
      this.originalPass = originalPass;
      return this;
    }

    public Builder setRegion(int region) {
      this.region = region;
      return this;
    }

    public Builder setToken(String token) {
      this.token = validateData(token);
      return this;
    }

    public String getUserName() {
      return this.userName;
    }

    public Builder setUserName(String userName) {
      this.userName = validateData(userName);
      return this;
    }

    public Builder setAbout(String about) {
      this.about = validateData(about);
      return this;
    }

    public Builder setGender(int gender) {
      this.gender = gender;
      return this;
    }

    public Builder setRelationshipStatus(int relationshipStatus) {
      this.relationshipStatus = relationshipStatus;
      return this;
    }

    public Builder setBodyType(int[] bodyType) {
      this.bodyType = bodyType;
      return this;
    }

    public Builder setInterestedIn(int interestedIn) {
      this.interestedIn = interestedIn;
      return this;
    }

    public Builder setLookingFor(int[] lookingFor) {
      this.lookingFor = lookingFor;
      return this;
    }

    public Builder setHeight(double height) {
      this.height = height;
      return this;
    }

    public Builder setEthnicity(int ethnicity) {
      this.ethnicity = ethnicity;
      return this;
    }

    public Builder setInterests(int[] interests) {
      this.interests = interests;
      return this;
    }

    public String getBirthday() {
      return birthday;
    }

    public void setBirthday(String birthday) {
      this.birthday = birthday;
    }

    public int getJob() {
      return job;
    }

    public void setJob(int job) {
      this.job = job;
    }

    public int[] getThreeSizes() {
      return threeSizes;
    }

    public void setThreeSizes(int[] threeSizes) {
      this.threeSizes = threeSizes;
    }

    public int getCupSize() {
      return cupSize;
    }

    public void setCupSize(int cupSize) {
      this.cupSize = cupSize;
    }

    public int getCuteType() {
      return cuteType;
    }

    public void setCuteType(int cuteType) {
      this.cuteType = cuteType;
    }

    public int getJoinHours() {
      return joinHours;
    }

    public void setJoinHours(int joinHours) {
      this.joinHours = joinHours;
    }

    public String getTypeMan() {
      return typeMan;
    }

    public void setTypeMan(String typeMan) {
      this.typeMan = validateData(typeMan);
    }

    public String getFetish() {
      return fetish;
    }

    public void setFetish(String fetish) {
      this.fetish = validateData(fetish);
    }

    public String getHobby() {
      return hobby;
    }

    public void setHobby(String hobby) {
      this.hobby = validateData(hobby);
    }

    private String validateData(String input) {
      if (TextUtils.isEmpty(input)) {
        return "";
      } else {
        return input.replace("\\", "\\\\").replace("\"", "\\\"");
      }
    }
  }
}
