package com.example.springboot.Services;

import com.example.springboot.Models.MapEdge;
import com.example.springboot.Models.MapNode;

import ch.qos.logback.core.recovery.ResilientFileOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import org.javatuples.Pair;

@Service
public class RouteServiceImpl implements RouteService {
    private static final Logger log = LoggerFactory.getLogger(RouteServiceImpl.class);

    /**
     * Return the nearest node's id to the given latitude and longtitude
     * 
     * @param latitude
     * @param longtitude
     * @param nodeMap
     * @return
     */
    public Long getClosestNode(double latitude, double longitude, Map<Long, MapNode> nodeMap) {
        long nearestNodeId = -1L;
        double minDistance = Double.MAX_VALUE;

        for (long nodeId : nodeMap.keySet()) {
            MapNode currNode = nodeMap.get(nodeId);
            double currDistance = currNode.distanceTo(longitude, latitude);

            if (currDistance < minDistance) {
                minDistance = currDistance;
                nearestNodeId = nodeId;
            }
        }
        return nearestNodeId;
    }

    @Override
    public List<Long> buildRoute(double srcLat, double srcLon, double destLon, double destLat,
            Map<Long, MapNode> nodeMap, Map<Long, List<Long>> edgeMap,
            Map<Pair<Long, Long>, Double> weightMap) {

        long startNodeId = getClosestNode(srcLat, srcLon, nodeMap);
        long endNodeId = getClosestNode(destLat, destLon, nodeMap);

        Map<Long, Long> preNodeMap = new HashMap<Long, Long>();
        PriorityQueue<MapEdge> edgeHeap = new PriorityQueue<>();
        Set<Long> visitedNodeSet = new HashSet<Long>();

        visitedNodeSet.add(endNodeId);

        for (long neighborNodeId : edgeMap.get(endNodeId)) {
            Pair<Long, Long> currPair = new Pair<Long, Long>(endNodeId, neighborNodeId);
            double weight = weightMap.get(currPair);
            edgeHeap.add(new MapEdge(weight, endNodeId, neighborNodeId));
        }

        while (!preNodeMap.containsKey(startNodeId) && !edgeHeap.isEmpty()) {
            // Pop the max edge out
            MapEdge maxEdge = edgeHeap.remove();
            long nextNodeId = maxEdge.getEndNodeId();
            long currNodeId = maxEdge.getStartNodeId();

            if (visitedNodeSet.contains(nextNodeId)) {
                continue;
            }

            preNodeMap.put(nextNodeId, currNodeId);
            visitedNodeSet.add(nextNodeId);

            for (long neighborNodeId : edgeMap.get(nextNodeId)) {
                if (visitedNodeSet.contains(neighborNodeId)) {
                    continue;
                }
                Pair<Long, Long> currPair = new Pair<Long, Long>(nextNodeId, neighborNodeId);
                double weight = weightMap.get(currPair);
                edgeHeap.add(new MapEdge(weight, nextNodeId, neighborNodeId));
            }
        }
        
        log.info("Done");
        log.info(preNodeMap.toString());
        return getRoute(preNodeMap, startNodeId, endNodeId);
    }

    public List<Long> getRoute(Map<Long, Long> preNodeMap, long startNodeId, long endNodeId) {
        List<Long> result = new ArrayList<>();
        long currNodeId = startNodeId;

        while (currNodeId != endNodeId) {
            result.add(currNodeId);
            currNodeId = preNodeMap.get(currNodeId);
        }

        result.add(endNodeId);
        
        return result;
    }
}
