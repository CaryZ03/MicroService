package com.dofinal.RG.controller.demo;

import com.dofinal.RG.entity.user.User;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * &#064;Classname PostController
 * &#064;Description  TODO
 * &#064;Date 2024/5/4 17:23
 * &#064;Created MuJue
 */
@RestController
public class PostController {
    @RequestMapping(value = "/post", method = RequestMethod.POST)
    @Trace
    public String post(String name){
        return "this is post, stupid " + name;
    }
    @RequestMapping(value = "/post2", method = RequestMethod.POST)
    @Trace
    public String post2(User user){
        return "this is post, stupid" + user.getId();
    }
    @RequestMapping(value = "/post3", method = RequestMethod.POST)
    @Trace
    public String post3(@RequestBody User user){//传入的是.json文件
        return "this is post, stupid" + user.getId();
    }
}
