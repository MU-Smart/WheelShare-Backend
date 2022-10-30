package com.example.springboot.Services;

import com.example.springboot.Models.MapNode;

import java.util.List;
import java.util.Map;
import org.javatuples.Pair;

public interface RouteService {
    Long getClosestNode(double latitude, double longitude, Map<Long, MapNode> nodeMap);

    List<Long> buildRoute (double srcLon, double srcLat, double destLon, double destLat,
                           Map<Long, MapNode> nodeMap, Map<Long, List<Long>> edgeMap, 
                           Map<Pair<Long, Long>, Double> weightMap);
    
    List<Long> getRoute(Map<Long, Long> preNodeMap, long startNodeId, long endNodeId);
}
