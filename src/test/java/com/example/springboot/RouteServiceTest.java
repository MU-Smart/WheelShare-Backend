package com.example.springboot;

import com.example.springboot.Models.MapNode;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
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

  @Before
  public void initNodeMapTest() {
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

  }

  @Autowired
  private RouteService routeService;

  @Test
  public void getClosestNodeTest() {
    assertEquals(0L, routeService.getClosestNode(-1.0, -1.0, nodeMapTest));
  }

}