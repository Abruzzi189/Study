package com.application.facebook;

import java.util.Arrays;
import java.util.List;

public interface IFacebookPermission {

  public List<String> PUBLISH_STREAM = Arrays.asList("publish_stream", "publish_actions");
  public List<String> READ = Arrays.asList("basic_info");

  public List<String> getPermissions();
}
