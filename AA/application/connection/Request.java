package com.application.connection;

public interface Request {

  public Response execute();

  public void setNewToken(String newToken);
}
