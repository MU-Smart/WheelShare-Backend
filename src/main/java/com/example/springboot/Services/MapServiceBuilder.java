package com.example.springboot.Services;

import java.util.List;
import java.util.Map;

import org.javatuples.Pair;

import com.example.springboot.Models.MapNode;

public interface MapServiceBuilder {
  public Map<Long, MapNode> getNodeMap();
  public Map<Long, List<Long>> getEdgeMap();
  public Map<Pair<Long, Long>, Double> getWeightMap();
  public void buildMap();
}
