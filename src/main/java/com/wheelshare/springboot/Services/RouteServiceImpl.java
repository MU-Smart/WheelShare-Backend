package com.wheelshare.springboot.Services;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.wheelshare.springboot.Models.Constant;
import com.wheelshare.springboot.Models.Errors;
import com.wheelshare.springboot.Models.MapEdge;
import com.wheelshare.springboot.Models.MapNode;
import com.wheelshare.springboot.Models.MapRoute;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.InvalidAlgorithmParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
     * Return the if the node if within out map bound
     * 
     * @param srcLat  latitude of source
     * @param srcLon  longtitude of source
     * @param destLat latitude of destination
     * @param destLon longtitude of destination
     * @return
     */
    public boolean checkMapBound(double latitude, double longitude) {
        return (latitude <= Constant.MAX_LAT.getConstant() &&
                latitude >= Constant.MIN_LAT.getConstant() &&
                longitude <= Constant.MAX_LON.getConstant() &&
                longitude >= Constant.MIN_LON.getConstant());
    }

    /**
     * Return the nearest node's id to the given latitude and longtitude
     * 
     * @param latitude   the latitude of the node
     * @param longtitude the longtitude of the node
     * @param nodeMap    the hashmap containing the node
     * @return
     */
    public Long getClosestNode(double latitude, double longitude,
            Map<Long, MapNode> nodeMap, Map<Long, List<Long>> edgeMap) {
        if (!checkMapBound(latitude, longitude)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, Errors.NODE_OUT_OF_BOUND.getMessage());
        }

        long nearestNodeId = -1L;
        double minDistance = Double.MAX_VALUE;

        for (long nodeId : nodeMap.keySet()) {
            MapNode currNode = nodeMap.get(nodeId);
            double currDistance = currNode.distanceTo(latitude, longitude);

            // * We do not consider nodes that is unconnected
            if (!edgeMap.containsKey(nodeId)) {
                continue;
            }

            if (currDistance < minDistance) {
                minDistance = currDistance;
                nearestNodeId = nodeId;
            } else if (currDistance == minDistance && nodeId < nearestNodeId) {
                nearestNodeId = nodeId;
            }
        }
        return nearestNodeId;
    }

    /**
     * Build the route given two coordinates using the algorithm from our research
     * project
     * 
     * @param srcLat  latitude of source
     * @param srcLon  longtitude of source
     * @param destLat latitude of destination
     * @param destLon longtitude of destination
     */
    @Override
    public List<Long> buildSingleRoute(double srcLat, double srcLon, double destLat, double destLon,
            Map<Long, MapNode> nodeMap, Map<Long, List<Long>> edgeMap, Map<Pair<Long, Long>, Double> weightMap) {

        // Check if the two nodes are within our map bound
        if (!checkMapBound(destLat, destLon) || !checkMapBound(srcLat, srcLon)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, Errors.NODE_OUT_OF_BOUND.getMessage());
        }

        long startNodeId = getClosestNode(srcLat, srcLon, nodeMap, edgeMap);
        long endNodeId = getClosestNode(destLat, destLon, nodeMap, edgeMap);
        MapNode endNode = nodeMap.get(endNodeId);

        Map<Long, Long> preNodeMap = new HashMap<Long, Long>();
        Set<Long> visitedNodeSet = new HashSet<Long>();
        PriorityQueue<Pair<MapEdge, Double>> edgeHeap = new PriorityQueue<>(new Comparator<Pair<MapEdge, Double>>() {
            @Override
            public int compare(Pair<MapEdge, Double> pair1, Pair<MapEdge, Double> pair2) {
                int mapEdgeComparison = pair1.getValue0().compareTo(pair2.getValue0());
                if (mapEdgeComparison != 0) {
                    // If the MapEdges are not equal, return their comparison result.
                    return mapEdgeComparison;
                } else {
                    // If the MapEdges are equal, compare the Double values.
                    return Double.compare(pair1.getValue1(), pair2.getValue1());
                }
            }
        });

        visitedNodeSet.add(startNodeId);

        for (long neighborNodeId : edgeMap.get(startNodeId)) {
            // Construct the edge
            Pair<Long, Long> currPair = new Pair<Long, Long>(startNodeId, neighborNodeId);
            double weight = weightMap.get(currPair);
            MapEdge newEgde = new MapEdge(weight, startNodeId, neighborNodeId);

            // Construct the distance value
            MapNode currNode = nodeMap.get(neighborNodeId);
            double distanceToEndNode = endNode.distanceTo(currNode.getLatitute(), currNode.getLongtitude());

            // Add to the edgeHeap
            Pair<MapEdge, Double> newEdgeDistancePair = new Pair<MapEdge, Double>(newEgde, distanceToEndNode);
            edgeHeap.add(newEdgeDistancePair);
        }

        while (!preNodeMap.containsKey(endNodeId) && !edgeHeap.isEmpty()) {
            // * Pop the max edge + distance pair out
            Pair<MapEdge, Double> currEdgeDistancePair = edgeHeap.remove();
            MapEdge maxEdge = currEdgeDistancePair.getValue0();
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
                // Construct the edge
                Pair<Long, Long> currPair = new Pair<Long, Long>(nextNodeId, neighborNodeId);
                double weight = weightMap.get(currPair);
                MapEdge newEgde = new MapEdge(weight, nextNodeId, neighborNodeId);

                // Construct the distance value
                MapNode currNode = nodeMap.get(neighborNodeId);
                double distanceToStartNode = endNode.distanceTo(currNode.getLatitute(), currNode.getLongtitude());

                // Add to the edgeHeap
                Pair<MapEdge, Double> newEdgeDistancePair = new Pair<MapEdge, Double>(newEgde, distanceToStartNode);
                edgeHeap.add(newEdgeDistancePair);
            }
        }
        return getSingleRoute(preNodeMap, startNodeId, endNodeId);
    }

    /**
     * Build the whole path from start to end using the preNode map
     * 
     * @param preNodeMap  the map containing the node as key and nextNode is value
     * @param startNodeId id of the start node
     * @param endNodeId   id of the end node
     */
    public List<Long> getSingleRoute(Map<Long, Long> preNodeMap, long startNodeId, long endNodeId) {
        List<Long> result = new ArrayList<>();
        long currNodeId = endNodeId;

        while (currNodeId != startNodeId) {
            result.add(currNodeId);
            currNodeId = preNodeMap.get(currNodeId);
        }

        result.add(startNodeId);
        Collections.reverse(result);
        return result;
    }

    /**
     * Generate all possible routes given 2 coordinates
     * Since this function uses recursion, we have to have a boundary to limit the
     * search space
     * The boundary would be a circle defined by the distance between the 2 start
     * and end node with the radius coefficient
     * 
     * @param srcLat            latitude of source
     * @param srcLon            longtitude of source
     * @param destLat           latitude of destination
     * @param destLon           longtitude of destination
     * @param radiusCoefficient the coefficient to decide how big the circle
     *                          boundary is
     */
    public List<List<Long>> buildMultipleRoute(double srcLat, double srcLon, double destLat, double destLon,
            double radiusCoefficent,
            Map<Long, MapNode> nodeMap, Map<Long, List<Long>> edgeMap) {

        // Check if the two nodes are within our map bound
        if (!checkMapBound(destLat, destLon) || !checkMapBound(srcLat, srcLon)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, Errors.NODE_OUT_OF_BOUND.getMessage());
        }

        List<List<Long>> multiPathResult = new ArrayList<>();

        long startNodeId = getClosestNode(srcLat, srcLon, nodeMap, edgeMap);
        long endNodeId = getClosestNode(destLat, destLon, nodeMap, edgeMap);
        MapNode startNode = nodeMap.get(startNodeId);
        MapNode endNode = nodeMap.get(endNodeId);

        double centerLatitude = (startNode.getLatitute() + endNode.getLatitute()) / 2;
        double centerLongtitude = (startNode.getLongtitude() + endNode.getLongtitude()) / 2;
        // * Have to do Math.max since the distance from the center to the startNode and
        // * endNode can be different
        double radius = radiusCoefficent * Math.max(startNode.distanceTo(centerLatitude, centerLongtitude),
                endNode.distanceTo(centerLatitude, centerLongtitude));

        Set<Long> nodeSet = new HashSet<>();
        List<Long> currentPath = new ArrayList<>();
        multipleRouteRecursion(startNodeId, endNodeId, radius, centerLatitude, centerLongtitude, edgeMap, nodeMap,
                multiPathResult, currentPath, nodeSet);
        return multiPathResult;
    }

    /**
     * Backtracking helper function to perform the DFS search
     * 
     * @param srcLat            latitude of source
     * @param srcLon            longtitude of source
     * @param destLat           latitude of destination
     * @param destLon           longtitude of destination
     * @param radiusCoefficient the coefficient to decide how big the circle
     *                          boundary is
     */

    public void multipleRouteRecursion(long currNodeId, long endNodeId, double radius, double centerLatitude,
            double centerLongtitude, Map<Long, List<Long>> edgeMap, Map<Long, MapNode> nodeMap,
            List<List<Long>> multiPathResult,
            List<Long> currentPath, Set<Long> visited) {
        currentPath.add(currNodeId);
        visited.add(currNodeId);

        if (currNodeId == endNodeId) {
            List<Long> resultPath = new ArrayList<>();

            for (Long nodeId : currentPath) {
                resultPath.add(nodeId);
            }
            multiPathResult.add(resultPath);

            visited.remove(currNodeId);
            currentPath.remove(currentPath.size() - 1);
            return;
        }

        List<Long> neighborList = edgeMap.get(currNodeId);
        for (long neighbor : neighborList) {
            MapNode neighborNode = nodeMap.get(neighbor);
            if (neighborNode.distanceTo(centerLatitude, centerLongtitude) > radius) {
                continue;
            }
            if (visited.contains(neighbor)) {
                continue;
            }
            multipleRouteRecursion(neighbor, endNodeId, radius, centerLatitude, centerLongtitude, edgeMap, nodeMap,
                    multiPathResult, currentPath, visited);
        }

        visited.remove(currNodeId);
        currentPath.remove(currentPath.size() - 1);
    }

    /**
     * Helper function to return a MapRoute Object with all of its statistic
     * 
     * @param nodeIdList list containing the ids of all the nodes on the route
     * @param nodeMap    the hashmap containing the node
     * @param weightMap  the hashmap containing the weight
     * 
     * @throws InvalidAlgorithmParameterException
     */

    public MapRoute routeStatisticGeneration(List<Long> nodeIdList, Map<Long, MapNode> nodeMap,
            Map<Pair<Long, Long>, Double> weightMap) throws InvalidAlgorithmParameterException {
        // ! Size of the nodeList is less than 2 -> Invalid Route
        if (nodeIdList.size() < 2) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, Errors.INVALID_PATH.getMessage());
        }

        MapRoute mapRoute = new MapRoute();

        double maxUncomfortScore = 0;
        double totalUncomfortScore = 0;
        List<MapNode> nodeList = new ArrayList<>();

        for (Long nodeId : nodeIdList) {
            nodeList.add(nodeMap.get(nodeId));
        }

        for (int i = 0; i < nodeIdList.size() - 1; i++) {
            Long currNode = nodeIdList.get(i);
            Long nextNode = nodeIdList.get(i + 1);
            Pair<Long, Long> nodePair = new Pair<Long, Long>(currNode, nextNode);

            Double edgeWeight = weightMap.get(nodePair);
            maxUncomfortScore = Math.max(maxUncomfortScore, edgeWeight);
            totalUncomfortScore += edgeWeight;
        }

        mapRoute.setNodeList(nodeList);
        mapRoute.setTotalUncomfortScore(totalUncomfortScore);
        mapRoute.setAverageUncomfortScore(round(totalUncomfortScore / (nodeIdList.size() - 1), 2));
        mapRoute.setMaxUncomfortScore(maxUncomfortScore);
        mapRoute.setNodeCount(nodeIdList.size());

        return mapRoute;
    }

    /**
     * Round double values to a specific decimal places
     * 
     * @param value  the value to be rounded
     * @param places decimal places
     * @return
     * @throws InvalidAlgorithmParameterException
     */

    private static double round(double value, int places) throws InvalidAlgorithmParameterException {
        if (places < 0)
            throw new InvalidAlgorithmParameterException(Errors.INVALID_DECIMAL_VALUE.getMessage());

        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
