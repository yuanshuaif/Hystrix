package com.example.demo.hsytrixCommand;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by 10326 on 2019/7/5.
 * 1.继承HystrixCommand
 * 2.设置HystrixCommandGroupKey
 * 3.new MyBaseHsytrixCommand("BOB").execute() 同步执行
 * 4.new MyBaseHsytrixCommand("LSJ").queue() 异步执行  future.get() 等待返回结果同步执行
 * 5.getFallback 降级方案
 */
public class MyBaseHsytrixCommand extends HystrixCommand<String> {

    String name = "";

    /*因为HystrixCommand没有默认的构造方法，所以此处需要给定构造方法*/
    public MyBaseHsytrixCommand(String name) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("ExampleGroup"))  //必须
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        .withExecutionTimeoutInMilliseconds(2000)));
        this.name = name;
    }

    @Override
    protected String run() throws Exception {
        Thread.sleep(3000);
        return "Hello " + name + "!";
    }

    /**
     * 断路器打开、线程池/信号量请求拒绝、异常、超时走降级方案
     * @return
     */
    protected String getFallback(){
        boolean isRejected = this.isResponseRejected();
        boolean isException = this.isFailedExecution();
        boolean isTimeout = this.isResponseTimedOut();
        boolean isCircut = this.isCircuitBreakerOpen();
        System.out.println("fail----" + isTimeout);
        return null;
    }

    public static void main(String[] args){
        String str1 = new MyBaseHsytrixCommand("BOB").execute();// 阻塞执行
        System.out.println(str1);
        Future<String> future = new MyBaseHsytrixCommand("LSJ").queue();// 异步执行 (相当于开启了一个线程去处理对应的方法,future是异步返回的结果)
        System.out.println("nihao");
        try {
            String str2 = future.get();// **** 此处需要等待异步处理的返回结果，然后顺序执行
            System.out.println(str2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}
