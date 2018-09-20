package com.application.call;

import com.application.entity.DataObject;

public interface OnAndGCallListener {

  public void onRemoteUnhold(DataObject dataObject);

  public void onRemoteHold(DataObject dataObject);

  public void onInviteUpdated(DataObject dataObject);

  public void onInviteTrying(DataObject dataObject);

  public void onInviteRinging(DataObject dataObject);

  public void onInviteClosed(DataObject dataObject);

  public void onInviteAnswered(DataObject dataObject);

  public void onTelePhoneCall(DataObject dataObject);

  public void onInviteConfirming(DataObject dataObject);

  public void onInviteClosedAfterRetry();

  public void onRetryCall();

  public void onRegisterSuccess();

  public void onRegisterFail();
}
