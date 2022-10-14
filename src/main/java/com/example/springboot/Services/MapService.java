package com.example.springboot.Services;

import java.util.List;
import java.util.Map;

public interface MapService {
  public Map<Integer, List<Integer>> getNodeMap();
  public void buildMap();
}
