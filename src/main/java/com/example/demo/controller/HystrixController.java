package com.example.demo.controller;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.netflix.hystrix.contrib.javanica.command.AsyncResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.Future;

/**
 * Created by 10326 on 2019/7/6.
 */
@RestController
public class HystrixController {

    @RequestMapping(value= "/hello")
    @HystrixCommand(groupKey = "hystix", fallbackMethod = "fallCallBack",
            commandProperties = {
                    @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "500"),
                    @HystrixProperty(name = "execution.isolation.strategy", value = "THREAD")
            })
    public String helloword(){
        try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "nihao";
    }

    /**
     *  fallbackMethod的返回值和参数类型需要和被@HystrixCommand注解的方法完全一致。否则会在运行时抛出异常。
     * @return
     */
    public String fallCallBack(){
        return "fail";
    }


    //异步的执行
    @RequestMapping(value= "/hello1")
    @HystrixCommand(fallbackMethod = "getUserNameError")
    public Future<String> getUserName() {
        long id = 6;
        return new AsyncResult<String>() {
            @Override
            public String invoke() {
                int i = 1/0;//此处抛异常,测试服务降级
                return "小明:" + id;
            }
        };
    }

    public String getUserNameError() {
        return "faile";
    }
}
