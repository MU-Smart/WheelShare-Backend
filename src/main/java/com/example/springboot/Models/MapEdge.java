package com.example.springboot.Models;

import lombok.Data;

@Data
public class MapEdge implements Comparable<MapEdge> {
  private final double weight;
  private final long startNodeId;
  private final long endNodeId;

  public MapEdge(double weight, long startNodeId, long endNodeId) {
    this.weight = weight;
    this.startNodeId = startNodeId;
    this.endNodeId = endNodeId;
}

  @Override
  public int compareTo(MapEdge other) {
    if (this.weight < other.weight) {
      return -1;
    } else if (this.weight > other.weight)  {
      return 1;
    } else  {
      return 0;
    }
  }

  @Override
  public String toString() {
      return String.format("(%f, %d, %d)", weight, startNodeId, endNodeId);
  }

}
