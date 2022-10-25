package com.example.springboot.Services;

import com.example.springboot.Models.MapNode;
import com.example.springboot.Models.Utilities;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RouteServiceImpl implements RouteService{
    /**
     * Given a particular location
     * return the reference of the closest map node to the location
     * @param longtitude
     * @param lattitude
     * @param refToNode
     * @return
     */
    public Long getClosest(float longitude, float latitude, Map<Long, MapNode> refToNode) {
        // Create a wrapping map node
        MapNode node = new MapNode(0, 0, "", "", 0, latitude, longitude);

        // Start comparing
        long res = -1L;  // this is for debug
        double smallestDist = Double.MAX_VALUE;
        for (Map.Entry<Long, MapNode> entry : refToNode.entrySet()) {
            double currDistance = Utilities.Distance(node, entry.getValue());
            if (currDistance < smallestDist) {
                smallestDist = currDistance;
                res = entry.getKey();
            }
        }

        // Return result
        return res;
    }

    public Map<Long, List<Double>> getWeight(Map<Long, List<Long>> adj) {
        // Prepare
        Map<Long, List<Double>> res = new HashMap<Long, List<Double>>();

        // Getting the weight with same structure as adj
        for (Map.Entry<Long, List<Long>> entry : adj.entrySet()) {
            Long key = entry.getKey();
            List<Long> neis = entry.getValue();
            // create the same list with 0 value
            List<Double> weights = Arrays.asList(new Double[neis.size()]);
            // add to result
            res.put(key, weights);
        }

        // Result
        return res;
    }
    @Override
    public List<Long> getRoute(float srcLon, float srcLat, float destLon, float destLat,
                                  Map<Long, MapNode> refToNode, Map<Long, List<Long>> adj) {
        // prepare
        Long start = getClosest(srcLon, srcLat, refToNode), end = getClosest(destLon, destLat, refToNode);
        // weight map initialized to 0
        Map<Long, List<Double>> weights = getWeight(adj);
        // get help from graph algorithm
        Graph graphAlgo = new Graph(start, end, refToNode, adj, weights);
        // return
        return graphAlgo.routeAlgo();
    }
}
