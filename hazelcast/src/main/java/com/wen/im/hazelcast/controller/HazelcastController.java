package com.wen.im.hazelcast.controller;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Queue;

/**
 * @author: Musk
 * @data: 2020-07-29 19:06
 * @classname: HazelcastController
 * @description: HazelcastController
 */
@RestController
@RequestMapping("/hazelcast")
public class HazelcastController {

    private final Logger logger = LoggerFactory.getLogger(HazelcastController.class);

    @Autowired
    private HazelcastInstance hazelcastInstance;

    @PostMapping(value = "/write-data")
    public String writeDataToHazelcast(@RequestParam String key, @RequestParam String value) {
        Map<String, String> hazelcastMap = hazelcastInstance.getMap("my-map");
        hazelcastMap.put(key, value);
        return "Data is stored.";
    }

    @GetMapping(value = "/read-data")
    public String readDataFromHazelcast(@RequestParam String key) {
        Map<String, String> hazelcastMap = hazelcastInstance.getMap("my-map");
        return hazelcastMap.get(key);
    }

    @GetMapping(value = "/read-all-data")
    public Map<String, String> readAllDataFromHazelcast() {
        Map<String, String> hazelcastMap = hazelcastInstance.getMap("my-map");
        return hazelcastInstance.getMap("my-map");
    }

    @PostMapping(value = "/save")
    public String saveMapData(@RequestParam String key, @RequestParam String value) {
        Map<String, String> hazelcastMap = hazelcastInstance.getMap("hazelcastMap");
        hazelcastMap.put(key, value);
        return "success";
    }

    @GetMapping(value = "/get")
    public String getMapData(@RequestParam String key) {
        Map<String, String> hazelcastMap = hazelcastInstance.getMap("hazelcastMap");
        return hazelcastMap.get(key);
    }

    @GetMapping(value = "/all")
    public Map<String, String> all() {
        return hazelcastInstance.getMap("hazelcastMap");
    }

    @GetMapping(value = "/list")
    public String saveList(@RequestParam(required = false) String value) {
        // 创建集群List
        IList<Object> clusterList = hazelcastInstance.getList("myList");
        clusterList.add(value);
        return "success";
    }

    @GetMapping(value = "/showList")
    public IList<Object> showList() {
        return hazelcastInstance.getList("myList");
    }

    @GetMapping(value = "/clearList")
    public String clearList() {
        IList<Object> clusterList = hazelcastInstance.getList("myList");
        clusterList.clear();
        return "success";
    }

    @GetMapping(value = "/queue")
    public String saveQueue(@RequestParam String value) {
        // 创建集群Queue
        Queue<String> clusterQueue = hazelcastInstance.getQueue("myQueue");
        clusterQueue.offer(value);
        return "success";
    }

    @GetMapping(value = "/showQueue")
    public Queue<String> showQueue() {
        Queue<String> clusterQueue = hazelcastInstance.getQueue("myQueue");
        for (String obj : clusterQueue) {
            logger.warn("value=" + obj);
        }
        return clusterQueue;
    }

    @GetMapping(value = "/clearQueue")
    public String clearQueue() {
        Queue<String> clusterQueue = hazelcastInstance.getQueue("myQueue");
        clusterQueue.clear();
        return "success";
    }


}