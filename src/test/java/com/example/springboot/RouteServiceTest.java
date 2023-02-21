package com.example.springboot;

import com.example.springboot.Models.MapNode;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
public class RouteServiceTest {
  private Map<Long, MapNode> nodeMapTest = new HashMap<>();
  private Map<Long, List<Long>> edgeMapTest = new HashMap<>();
  private Map<Pair<Long, Long>, Double> weightMapTest = new HashMap<>();
  private Map<Long, Long> preNodeMap = new HashMap<>();
  

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
    MapNode node7 = new MapNode(7, 5, 67510357, "2019-02-24T08:16:29Z", "Minh Nguyen", 33757, 1.0, -1.0);
    MapNode node8 = new MapNode(8, 5, 67510357, "2019-02-24T08:16:29Z", "Minh Nguyen", 33757, 3.0, 1.0);
    MapNode node9 = new MapNode(9, 5, 67510357, "2019-02-24T08:16:29Z", "Minh Nguyen", 33757, 3.0, -1.0);
    MapNode node10 = new MapNode(10, 5, 67510357, "2019-02-24T08:16:29Z", "Minh Nguyen", 33757, 7.0, -1.0);

    nodeMapTest.put(0L, node0);
    nodeMapTest.put(1L, node1);
    nodeMapTest.put(2L, node2);
    nodeMapTest.put(3L, node3);
    nodeMapTest.put(4L, node4);
    nodeMapTest.put(5L, node5);
    nodeMapTest.put(6L, node6);
    nodeMapTest.put(7L, node7);
    nodeMapTest.put(8L, node8);
    nodeMapTest.put(9L, node9);
    nodeMapTest.put(10L, node10);

    // init data for the edgeMap
    List<Long> node0List = Arrays.asList(1L,7L);
    List<Long> node1List = Arrays.asList(0L,2L);
    List<Long> node2List = Arrays.asList(1L,6L, 8L);
    List<Long> node3List = Arrays.asList(4L,5L,9L);
    List<Long> node4List = Arrays.asList(3L,10L);
    List<Long> node5List = Arrays.asList(3L,6L,10L);
    List<Long> node6List = Arrays.asList(2L,5L,10L);
    List<Long> node7List = Arrays.asList(0L,8L);
    List<Long> node8List = Arrays.asList(2L,7L,9L);
    List<Long> node9List = Arrays.asList(3L,8L);
    List<Long> node10List = Arrays.asList(4L,5L,6L);

    edgeMapTest.put(0L, node0List);
    edgeMapTest.put(1L, node1List);
    edgeMapTest.put(2L, node2List);
    edgeMapTest.put(3L, node3List);
    edgeMapTest.put(4L, node4List);
    edgeMapTest.put(5L, node5List);
    edgeMapTest.put(6L, node6List);
    edgeMapTest.put(7L, node7List);
    edgeMapTest.put(8L, node8List);
    edgeMapTest.put(9L, node9List);
    edgeMapTest.put(10L, node10List);

    // init data for the weight map
    weightMapTest.put(new Pair<Long,Long>(0L, 1L), 30.0);
    weightMapTest.put(new Pair<Long,Long>(1L, 0L), 30.0);
    weightMapTest.put(new Pair<Long,Long>(0L, 7L), 20.0);
    weightMapTest.put(new Pair<Long,Long>(7L, 0L), 20.0);
    weightMapTest.put(new Pair<Long,Long>(1L, 2L), 25.0);
    weightMapTest.put(new Pair<Long,Long>(2L, 1L), 25.0);
    weightMapTest.put(new Pair<Long,Long>(7L, 8L), 18.0);
    weightMapTest.put(new Pair<Long,Long>(8L, 7L), 18.0);
    weightMapTest.put(new Pair<Long,Long>(2L, 8L), 32.0);
    weightMapTest.put(new Pair<Long,Long>(8L, 2L), 32.0);
    weightMapTest.put(new Pair<Long,Long>(8L, 9L), 32.0);
    weightMapTest.put(new Pair<Long,Long>(9L, 8L), 32.0);
    weightMapTest.put(new Pair<Long,Long>(2L, 6L), 25.0);
    weightMapTest.put(new Pair<Long,Long>(6L, 2L), 25.0);
    weightMapTest.put(new Pair<Long,Long>(9L, 3L), 2.0);
    weightMapTest.put(new Pair<Long,Long>(3L, 9L), 2.0);
    weightMapTest.put(new Pair<Long,Long>(3L, 4L), 2.0);
    weightMapTest.put(new Pair<Long,Long>(4L, 3L), 2.0);
    weightMapTest.put(new Pair<Long,Long>(4L, 10L), 2.0);
    weightMapTest.put(new Pair<Long,Long>(10L, 4L), 2.0);
    weightMapTest.put(new Pair<Long,Long>(3L, 5L), 50.0);
    weightMapTest.put(new Pair<Long,Long>(5L, 3L), 50.0);
    weightMapTest.put(new Pair<Long,Long>(5L, 6L), 50.0);
    weightMapTest.put(new Pair<Long,Long>(6L, 5L), 50.0);
    weightMapTest.put(new Pair<Long,Long>(6L, 10L), 5.0);
    weightMapTest.put(new Pair<Long,Long>(10L, 6L), 5.0);
    weightMapTest.put(new Pair<Long,Long>(5L, 10L), 5.0);
    weightMapTest.put(new Pair<Long,Long>(10L, 5L), 100.0);

    // init data for the preNode map
    // 1 -> 2, 2 -> 5, 5 -> 6, 6 -> 9, 9 -> 3, 3 -> 4, 10 -> 0, 0 -> 5
    preNodeMap.put(1L, 2L);
    preNodeMap.put(2L, 5L);
    preNodeMap.put(5L, 6L);
    preNodeMap.put(6L, 9L);
    preNodeMap.put(6L, 9L);
    preNodeMap.put(9L, 3L);
    preNodeMap.put(3L, 4L);
    preNodeMap.put(10L, 0L);
    preNodeMap.put(0L, 5L);

  }

  @Autowired
  private RouteService routeService;

  /**
   * Test for the getClosestNode function from the routeService
   * ! Given the same coordinate, the horizontal (latitude-wise) distance is always 
   * ! greater than the vertical (longtitude-wise) distance
   * * For example, given a fixed point A(3,0), B(4,0), C(2,0), D(3,-1)
   * * d(A,B) = d(A,C) > d(A,D)
   *  */ 
  @Test
  public void getClosestNodeTest() {
    assertEquals(0L, routeService.getClosestNode(-1.0, -1.0, nodeMapTest));
    assertEquals(1L, routeService.getClosestNode(1.0, 3.0, nodeMapTest));
    assertEquals(7L, routeService.getClosestNode(2.0, -1.0, nodeMapTest));
    assertEquals(2L, routeService.getClosestNode(4.0, 4.0, nodeMapTest));
    assertEquals(5L, routeService.getClosestNode(5.0, -1.0, nodeMapTest));
  }

  @Test
  public void buildMapTest() {
    // path from node 0 to node 5
    List<Long> expected1 = Arrays.asList(0L,1L,2L,6L,5L);
    assertEquals(expected1, routeService.buildSingleRoute(0, 0, 5, 0, nodeMapTest, edgeMapTest, weightMapTest));
    
    // path from node 7 to node 3
    List<Long> expected2 = Arrays.asList(7L,0L,1L,2L,6L,5L,3L);
    assertEquals(expected2, routeService.buildSingleRoute(1, -1, 4, -1, nodeMapTest, edgeMapTest, weightMapTest));

    // path from node 4 to node 3
    List<Long> expected3 = Arrays.asList(4L,10L,5L,3L);
    assertEquals(expected3, routeService.buildSingleRoute(6, -1, 4, -1, nodeMapTest, edgeMapTest, weightMapTest));
  }

  @Test
  public void getRouteTest() {
    // path from node 1 to node 3
    List<Long> expected1 = Arrays.asList(1L,2L,5L,6L,9L,3L);
    assertEquals(expected1, routeService.getSingleRoute(preNodeMap, 1L, 3L));

    // path from node 10 to node 4
    List<Long> expected2 = Arrays.asList(10L,0L,5L,6L,9L,3L,4L);
    assertEquals(expected2, routeService.getSingleRoute(preNodeMap, 10L, 4L));
    
  }
}