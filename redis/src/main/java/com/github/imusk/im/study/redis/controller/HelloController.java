package com.github.imusk.im.study.redis.controller;

import com.github.imusk.im.study.redis.server.HelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @classname: HelloController
 * @description: 测试示例
 * @data: 2020-07-26 19:00
 * @author: Musk
 */
@Controller
@RequestMapping("/hello")
public class HelloController {

    @Autowired
    private HelloService helloService;


    @ResponseBody
    @RequestMapping("/a")
    public String a(String id) {
        return helloService.a(id);
    }

    @ResponseBody
    @RequestMapping("/b")
    public String b(String id) {
        return helloService.b(id);
    }

    @ResponseBody
    @RequestMapping("/c")
    public String c(String id) {
        return helloService.c(id);
    }


}
