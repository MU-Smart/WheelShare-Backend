package com.example.springboot.Services;

import com.example.springboot.Models.MapEdge;
import com.example.springboot.Models.MapNode;

import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
     * @param latitude the latitude of the node
     * @param longtitude the longtitude of the node
     * @param nodeMap the hashmap containing the node
     * @return
     */
    public Long getClosestNode(double latitude, double longitude, Map<Long, MapNode> nodeMap) {
        long nearestNodeId = -1L;
        double minDistance = Double.MAX_VALUE;

        for (long nodeId : nodeMap.keySet()) {
            MapNode currNode = nodeMap.get(nodeId);
            double currDistance = currNode.distanceTo(latitude, longitude);

            if (currDistance < minDistance) {
                minDistance = currDistance;
                nearestNodeId = nodeId;
            }   else if (currDistance == minDistance && nodeId < nearestNodeId)   {
                nearestNodeId = nodeId;
            }
        }
        return nearestNodeId;
    }

    /**
     * Build the route given two coordinates using the algorithm from our research project
     * 
     * @param srcLat latitude of source
     * @param srcLon longtitude of source
     * @param destLat latitude of destination
     * @param destLon longtitude of destination
     */
    @Override
    public List<Long> buildRoute(double srcLat, double srcLon, double destLat, double destLon,
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

        return getRoute(preNodeMap, startNodeId, endNodeId);
    }

    /**
     * Build the whole path from start to end using the preNode map
     * 
     * @param preNodeMap the map containing the node as key and nextNode is value
     * @param startNodeId id of the start node
     * @param endNodeId id of the end node
     */
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

    public List<List<Long>> test(double srcLat, double srcLon, double destLat, double destLon,
    Map<Long, MapNode> nodeMap, Map<Long, List<Long>> edgeMap,
    Map<Pair<Long, Long>, Double> weightMap) {
        List<List<Long>> multiPathResult = new ArrayList<>();

        long startNodeId = getClosestNode(srcLat, srcLon, nodeMap);
        long endNodeId = getClosestNode(destLat, destLon, nodeMap);
        MapNode startNode = nodeMap.get(startNodeId);
        MapNode endNode = nodeMap.get(endNodeId);

        double centerLatitude = (startNode.getLatitute() + endNode.getLatitute()) / 2;
        double centerLongtitude = (startNode.getLongtitude() + endNode.getLongtitude()) / 2;
        double radius = 2 * Math.max(startNode.distanceTo(centerLatitude, centerLongtitude), endNode.distanceTo(centerLatitude, centerLongtitude));
        // log.info(Double.toString(radius));
        // log.info(Double.toString(nodeMap.get(7213516553L).distanceTo(centerLatitude, centerLongtitude)));

        Set<Long> nodeSet = new HashSet<>();
        List<Long> currentPath = new ArrayList<>();
        test2(multiPathResult, edgeMap, nodeMap, currentPath, nodeSet, startNodeId, endNodeId, radius, centerLatitude, centerLongtitude);
        log.info("-----------------");
        log.info(multiPathResult.toString());
        log.info("-----------------");
        return multiPathResult;
    }

    public void test2(List<List<Long>> multiPathResult, Map<Long, List<Long>> map, Map<Long, MapNode> nodeMap,
    List<Long> currentPath, Set<Long> visited, long currNodeId, long endNodeId, 
    double radius, double centerLatitude, double centerLongtitude)  {
        currentPath.add(currNodeId);
        visited.add(currNodeId);
        if (currNodeId == endNodeId)    {
            log.info(currentPath.toString());
            List<Long> resultPath = new ArrayList<>();

            for (Long nodeId : currentPath) {
                resultPath.add(nodeId);
            }
            multiPathResult.add(resultPath);

            visited.remove(currNodeId);
            currentPath.remove(currentPath.size() - 1);
            return;
        }
        
        List<Long> neighborList = map.get(currNodeId);

        for (long neighbor : neighborList)  {
            MapNode neighborNode = nodeMap.get(neighbor);
            if (neighborNode.distanceTo(centerLatitude, centerLongtitude) > radius) {
                log.info(Long.toString(neighborNode.getNodeId()));
                continue;
            }
            if (visited.contains(neighbor))    {
                continue;
            }
            test2(multiPathResult, map, nodeMap, currentPath, visited, neighbor, endNodeId, radius, centerLatitude, centerLongtitude);
        }

        visited.remove(currNodeId);
        currentPath.remove(currentPath.size() - 1);
    }
}
