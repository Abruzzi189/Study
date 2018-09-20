package com.application.payment;

public class PointPackage {

  private String packageId;
  private int point;
  private String description;
  private String productId;
  private String price;
  private double amount;
  private String currency;
  private String text;

  public PointPackage(String packageId, int point, String description,
      String productId) {
    super();
    this.packageId = packageId;
    this.point = point;
    this.description = description;
    this.productId = productId;
  }

  /**
   * For Api 135 ListPointActionPacket
   */
  public PointPackage(String packageId, int point, String description,
      String productId, String text) {
    super();
    this.packageId = packageId;
    this.point = point;
    this.description = description;
    this.productId = productId;
    this.text = text;
  }

  public String getPrice() {
    return price;
  }

  public void setPrice(String price) {
    this.price = price;
  }

  public double getAmount() {
    return amount;
  }

  public void setAmount(double amount) {
    this.amount = amount;
  }

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getProductId() {
    return productId;
  }

  public int getPoint() {
    return point;
  }

  public String getPackageId() {
    return packageId;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }
}
