package com.example.springboot.Models;

import lombok.Data;

@Data
public class MapNode {
  private boolean isVisble;
  private int version;
  private long changeSet;
  private String timeStamp;
  private String user;
  private long userId;
  private float latitute;
  private float longtitude;

  public MapNode(boolean isVisble, int version, long changeSet, String timeStamp, String user, long userId, float latitude, float longtitude) {
    this.isVisble = isVisble;
    this.version = version; 
    this.changeSet = changeSet;
    this.timeStamp = timeStamp;
    this.user = user;
    this.userId = userId;
    this.latitute = latitude;
    this.longtitude = longtitude;
  }
}