package com.example.springboot.Services;

import com.example.springboot.Models.MapNode;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.javatuples.Pair;

public interface RouteService {
    Long getClosestNode(double latitude, double longitude, Map<Long, MapNode> nodeMap);

    List<Long> buildSingleRoute (double srcLat, double srcLon, double destLat, double destLon,
                           Map<Long, MapNode> nodeMap, Map<Long, List<Long>> edgeMap, 
                           Map<Pair<Long, Long>, Double> weightMap);
    
    List<Long> getSingleRoute(Map<Long, Long> preNodeMap, long startNodeId, long endNodeId);

    List<List<Long>> buildMultipleRoute(double srcLat, double srcLon, double destLat, double destLon, double radiusCoefficent, 
    Map<Long, MapNode> nodeMap, Map<Long, List<Long>> edgeMap, Map<Pair<Long, Long>, Double> weightMap);

    void multipleRouteRecursion(long currNodeId, long endNodeId, double radius, double centerLatitude, double centerLongtitude, 
    Map<Long, List<Long>> map, Map<Long, MapNode> nodeMap, List<List<Long>> multiPathResult, List<Long> currentPath, Set<Long> visited);

}
