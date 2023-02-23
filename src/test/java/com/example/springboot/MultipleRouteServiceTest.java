package com.example.springboot;

import com.example.springboot.Models.MapNode;
import com.example.springboot.Models.MapRoute;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.security.InvalidAlgorithmParameterException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.junit.Test;
import org.javatuples.Pair;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.springboot.Services.RouteService;

@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
public class MultipleRouteServiceTest {
  private Map<Long, MapNode> nodeMapTest = new HashMap<>();
  private Map<Long, List<Long>> edgeMapTest = new HashMap<>();
  private Map<Pair<Long, Long>, Double> weightMapTest = new HashMap<>();

  @Before
  public void init() {
    // Init data for the nodeMap
    MapNode node0 = new MapNode(0, 5, 67510357, "2019-02-24T08:16:29Z", "Minh Nguyen", 33757, 0.0, 0.0);
    MapNode node1 = new MapNode(1, 5, 67510357, "2019-02-24T08:16:29Z", "Minh Nguyen", 33757, 1.0, 2.0);
    MapNode node2 = new MapNode(2, 5, 67510357, "2019-02-24T08:16:29Z", "Minh Nguyen", 33757, 3.0, 2.0);
    MapNode node3 = new MapNode(3, 5, 67510357, "2019-02-24T08:16:29Z", "Minh Nguyen", 33757, 4.0, -1.0);
    MapNode node4 = new MapNode(4, 5, 67510357, "2019-02-24T08:16:29Z", "Minh Nguyen", 33757, 6.0, -1.0);
    MapNode node5 = new MapNode(5, 5, 67510357, "2019-02-24T08:16:29Z", "Minh Nguyen", 33757, 5.0, 0.0);
    MapNode node6 = new MapNode(6, 5, 67510357, "2019-02-24T08:16:29Z", "Minh Nguyen", 33757, 7.0, 2.0);

    nodeMapTest.put(0L, node0);
    nodeMapTest.put(1L, node1);
    nodeMapTest.put(2L, node2);
    nodeMapTest.put(3L, node3);
    nodeMapTest.put(4L, node4);
    nodeMapTest.put(5L, node5);
    nodeMapTest.put(6L, node6);

    // init data for the edgeMap
    List<Long> node0List = Arrays.asList(1L);
    List<Long> node1List = Arrays.asList(0L, 2L, 6L);
    List<Long> node2List = Arrays.asList(1L, 3L, 4L);
    List<Long> node3List = Arrays.asList(2L, 5L);
    List<Long> node4List = Arrays.asList(2L, 5L, 6L);
    List<Long> node5List = Arrays.asList(3L, 4L, 6L);
    List<Long> node6List = Arrays.asList(1L, 4L, 5L);

    edgeMapTest.put(0L, node0List);
    edgeMapTest.put(1L, node1List);
    edgeMapTest.put(2L, node2List);
    edgeMapTest.put(3L, node3List);
    edgeMapTest.put(4L, node4List);
    edgeMapTest.put(5L, node5List);
    edgeMapTest.put(6L, node6List);

    // init data for the weight map
    weightMapTest.put(new Pair<Long, Long>(0L, 1L), 10.0);
    weightMapTest.put(new Pair<Long, Long>(1L, 0L), 10.0);
    weightMapTest.put(new Pair<Long, Long>(1L, 2L), 7.0);
    weightMapTest.put(new Pair<Long, Long>(2L, 1L), 7.0);
    weightMapTest.put(new Pair<Long, Long>(2L, 3L), 2.0);
    weightMapTest.put(new Pair<Long, Long>(3L, 2L), 2.0);
    weightMapTest.put(new Pair<Long, Long>(1L, 6L), 20.0);
    weightMapTest.put(new Pair<Long, Long>(6L, 1L), 20.0);
    weightMapTest.put(new Pair<Long, Long>(2L, 4L), 18.0);
    weightMapTest.put(new Pair<Long, Long>(4L, 2L), 18.0);
    weightMapTest.put(new Pair<Long, Long>(4L, 5L), 4.0);
    weightMapTest.put(new Pair<Long, Long>(5L, 4L), 4.0);
    weightMapTest.put(new Pair<Long, Long>(3L, 5L), 3.0);
    weightMapTest.put(new Pair<Long, Long>(5L, 3L), 3.0);
    weightMapTest.put(new Pair<Long, Long>(4L, 6L), 12.0);
    weightMapTest.put(new Pair<Long, Long>(6L, 4L), 12.0);
    weightMapTest.put(new Pair<Long, Long>(6L, 5L), 5.0);
    weightMapTest.put(new Pair<Long, Long>(5L, 6L), 5.0);
  }

  @Autowired
  private RouteService routeService;

  private static final Logger log = LoggerFactory.getLogger(MultipleRouteServiceTest.class);

  @Test
  public void buildMultipleRouteTest() {
    // path from node 0 to node 6
    List<List<Long>> routeList = routeService.buildMultipleRoute(0, 0, 7.0, 2.0, 1.2, nodeMapTest, edgeMapTest);
    List<Long> route1 = Arrays.asList(0L, 1L, 2L, 3L, 5L, 4L, 6L);
    List<Long> route2 = Arrays.asList(0L, 1L, 2L, 3L, 5L, 6L);
    List<Long> route3 = Arrays.asList(0L, 1L, 2L, 4L, 5L, 6L);
    List<Long> route4 = Arrays.asList(0L, 1L, 2L, 4L, 6L);
    List<Long> route5 = Arrays.asList(0L, 1L, 6L);
    assertTrue(routeList.contains(route1));
    assertTrue(routeList.contains(route2));
    assertTrue(routeList.contains(route3));
    assertTrue(routeList.contains(route4));
    assertTrue(routeList.contains(route5));
    assertEquals(5, routeList.size());
  }

  @Test
  public void routeStatisticGenerationTest1() throws InvalidAlgorithmParameterException {
    // path from node 0 to node 6
    List<Long> route = Arrays.asList(0L, 1L, 2L, 4L);
    MapRoute mapRoute = routeService.routeStatisticGeneration(route, nodeMapTest, weightMapTest);
    assertEquals(35.0, mapRoute.getTotalUncomfortScore());
    assertEquals(18.0, mapRoute.getMaxUncomfortScore());
    assertEquals(11.67, mapRoute.getAverageUncomfortScore());
    log.info(mapRoute.toString());
  }

  @Test
  public void routeStatisticGenerationTest2() throws InvalidAlgorithmParameterException {
    // path from node 0 to node 6
    List<Long> route = Arrays.asList(2L, 3L, 5L, 4L, 6L);
    MapRoute mapRoute = routeService.routeStatisticGeneration(route, nodeMapTest, weightMapTest);
    assertEquals(21.0, mapRoute.getTotalUncomfortScore());
    assertEquals(12.0, mapRoute.getMaxUncomfortScore());
    assertEquals(5.25, mapRoute.getAverageUncomfortScore());
    log.info(mapRoute.toString());
  }

}