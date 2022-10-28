package com.example.springboot.Models;

import lombok.Data;

@Data
public class MapNode {
  private int version;
  private long changeSet;
  private String timeStamp;
  private String user;
  private long userId;
  private float latitute;
  private float longtitude;

  public MapNode(int version, long changeSet, String timeStamp, String user, long userId, float latitude, float longtitude) {
    this.version = version; 
    this.changeSet = changeSet;
    this.timeStamp = timeStamp;
    this.user = user;
    this.userId = userId;
    this.latitute = latitude;
    this.longtitude = longtitude;
  }

  public double distanceTo(float lon, float lat) {
    double distance = 1.00;
    // TODO Implement the Haversine Formula
    // * https://www.movable-type.co.uk/scripts/latlong.html
    return distance;
}
}