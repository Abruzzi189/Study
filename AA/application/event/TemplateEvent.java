package com.application.event;

import com.application.entity.Template;

public class TemplateEvent {

  public static final int INSERT = 0;
  public static final int UPDATE = 1;
  public static final int DELETE = 2;

  private Template template;
  private int mode;
  private int position;

  public TemplateEvent(int mode) {
    this.mode = mode;
  }

  public TemplateEvent(int mode, Template template) {
    this.mode = mode;
    this.template = template;
  }

  public TemplateEvent(int mode, Template template, int position) {
    this.mode = mode;
    this.template = template;
    this.position = position;
  }

  public Template getTemplate() {
    return template;
  }

  public void setTemplate(Template template) {
    this.template = template;
  }

  public int getMode() {
    return mode;
  }

  public void setMode(int mode) {
    this.mode = mode;
  }

  public int getPosition() {
    return position;
  }

  public void setPosition(int position) {
    this.position = position;
  }

}
