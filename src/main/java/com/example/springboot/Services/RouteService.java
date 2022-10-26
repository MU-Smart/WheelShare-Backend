package com.example.springboot.Services;

import com.example.springboot.Models.MapNode;

import java.util.List;
import java.util.Map;

public interface RouteService {
    List<Long> getRoute(float srcLon, float srcLat, float destLon, float destLat,
                           Map<Long, MapNode> refToNode, Map<Long, List<Long>> adj);
}
