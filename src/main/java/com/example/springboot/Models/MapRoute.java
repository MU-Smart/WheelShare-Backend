package com.example.springboot.Models;

import java.util.List;

import lombok.Data;

@Data
public class MapRoute {
  private List<MapNode> nodeList;
  private double totalUncomfortScore;
  private double averageUncomfortScore;
  private double maxUncomfortScore;
  private int nodeCount;

  public MapRoute() {}
}
