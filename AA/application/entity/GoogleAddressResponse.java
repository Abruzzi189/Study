package com.application.entity;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class GoogleAddressResponse {

  @SerializedName(value = "results")
  public List<Result> results;
  @SerializedName(value = "status")
  public String status;

  public class Result {

    @SerializedName(value = "formatted_address")
    public String formatted_address;
    @SerializedName(value = "types")
    public List<String> types;
    @SerializedName(value = "address_components")
    public List<AddressComponent> address_components;
  }

  public class AddressComponent {

    @SerializedName(value = "long_name")
    public String long_name;

    @SerializedName(value = "short_name")
    public String short_name;

    @SerializedName(value = "types")
    public List<String> types;
  }
}
