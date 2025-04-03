package com.dofinal.RG.controller.demo;

import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * &#064;Classname GreetController
 * &#064;Description  TODO
 * &#064;Date 2024/5/4 16:39
 * &#064;Created MuJue
 */
//只想要请求数据
//这个注解会将返回的对象默认转换为.json
@RestController
public class GreetController {
    // http://localhost:8080/greet
    // http://localhost:8080/greet?name=cdh
    /* value:就是URL。可以使用正则表达式。**表示可以多级访问，比如，/**.json,
     /a.json, /a/b.json, /a/b/c.json都可以匹配。
     method: GET, POST, PUT, DELETE
     */
    @RequestMapping(value = "/greet", method = RequestMethod.GET)
    @Trace
    public String greet(String name){
        return "Fuck you, " + name;
    }
    @RequestMapping(value = "greet2", method = RequestMethod.GET)
    //在使用@RequestParam后，required为true,则必须要传入一个nickname的参数。
    @Trace
    public String greet2(@RequestParam(value = "nickname",required = false)String name){
        return "Wow, you have nickname, " + name;
    }
}
