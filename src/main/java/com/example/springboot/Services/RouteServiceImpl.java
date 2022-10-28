package com.example.springboot.Services;

import com.example.springboot.Models.MapEdge;
import com.example.springboot.Models.MapNode;
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

    /**
     * Return the nearest node's id to the given longtitude and latitude
     * 
     * @param longtitude
     * @param lattitude
     * @param refToNode
     * @return
     */
    public Long getClosestNode(float longitude, float latitude, Map<Long, MapNode> nodeMap) {
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
    public List<Long> buildRoute(float srcLon, float srcLat, float destLon, float destLat,
            Map<Long, MapNode> nodeMap, Map<Long, List<Long>> edgeMap,
            Map<Pair<Long, Long>, Double> weightMap) {

        long startNodeId = getClosestNode(srcLon, srcLat, nodeMap);
        long endNodeId = getClosestNode(destLon, destLat, nodeMap);

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

            for (long neighborNodeId : edgeMap.get(nextNodeId)) {
                Pair<Long, Long> currPair = new Pair<Long, Long>(nextNodeId, neighborNodeId);
                double weight = weightMap.get(currPair);
                edgeHeap.add(new MapEdge(weight, nextNodeId, neighborNodeId));
            }
        }

        return getRoute(preNodeMap, startNodeId);
    }

    public List<Long> getRoute(Map<Long, Long> preNodeMap, long startNodeId) {
        List<Long> result = new ArrayList<>();
        long currNodeId = startNodeId;

        while (preNodeMap.containsKey(currNodeId)) {
            result.add(currNodeId);
            startNodeId = preNodeMap.get(currNodeId);
        }

        return result;
    }
}
