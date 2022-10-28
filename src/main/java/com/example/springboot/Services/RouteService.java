package com.example.springboot.Services;

import com.example.springboot.Models.MapNode;

import java.util.List;
import java.util.Map;
import org.javatuples.Pair;

public interface RouteService {
    List<Long> buildRoute (float srcLon, float srcLat, float destLon, float destLat,
                           Map<Long, MapNode> nodeMap, Map<Long, List<Long>> edgeMap, 
                           Map<Pair<Long, Long>, Double> weightMap);
}
