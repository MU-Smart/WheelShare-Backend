package com.example.springboot.Models;

import lombok.Data;

@Data
public class MapNode {
  private long nodeId;
  private int version;
  private long changeSet;
  private String timeStamp;
  private String user;
  private long userId;
  private double latitute;
  private double longtitude;

  public MapNode(long nodeId, int version, long changeSet, String timeStamp, String user, long userId, double latitude,
      double longtitude) {
    this.nodeId = nodeId;
    this.version = version;
    this.changeSet = changeSet;
    this.timeStamp = timeStamp;
    this.user = user;
    this.userId = userId;
    this.latitute = latitude;
    this.longtitude = longtitude;
  }

  public double distanceTo(double lat, double lon) {
    // * https://www.movable-type.co.uk/scripts/latlong.html

    double dLat = Math.toRadians(lat - this.latitute);
    double dLon = Math.toRadians(lon - this.longtitude);

    double radianLat1 = Math.toRadians(this.latitute);
    double radianLat2 = Math.toRadians(lat);

    double haversineFormula = Math.pow(Math.sin(dLat / 2), 2) +
        Math.pow(Math.sin(dLon / 2), 2) *
            Math.cos(radianLat1) * Math.cos(radianLat2);
    double rad = 6371;
    double c = 2 * Math.asin(Math.sqrt(haversineFormula));
    return rad * c;
  }
}