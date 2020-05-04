package com.example.demo.hsytrixCommand;

import com.netflix.hystrix.*;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by 10326 on 2019/7/5.
 * 1.配置线程池的名称及线程池的相关参数
 * 2.withQueueSizeRejectionThreshold 在队列中的线程数超过该数目走拒绝的降级策略
 * 3.按照线程池的名称进行分组
 * (线程池默认分组：HystrixCommandGroupKey；HystrixThreadPoolKey提供了更细力度的分组)
 * commandKey分组内唯一，HystrixCommand和分组、线程池是多对1的关系。分组和线程池没关系。
 */
public class ThreadPoolHsytrixCommand extends HystrixCommand<String> {

    String key = "";

    /*因为HystrixCommand没有默认的构造方法，所以此处需要给定构造方法*/
    public ThreadPoolHsytrixCommand(String key) {
        super(Setter
                //分组key
                .withGroupKey(HystrixCommandGroupKey.Factory.asKey("helloWorldGroup"))
                //commandKey
//                .andCommandKey(HystrixCommandKey.Factory.asKey("commandHelloWorld"))
                //command属性配置
//                .andCommandPropertiesDefaults(HystrixPropertiesCommandDefault.Setter().withCircuitBreakerEnabled(true).withCircuitBreakerForceOpen(true))
                //线程池key
                .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey(key))
                //线程池属性配置
                .andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter().withCoreSize(10).withMaxQueueSize(20). withQueueSizeRejectionThreshold(19).withMaximumSize(20)));
        this.key = key;
    }

    @Override
    protected String run() throws Exception {
        Thread.sleep(300);
        return "Hello " + key + "!" + Thread.currentThread().getName();
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
        System.out.println("fail----" + isRejected);
        return null;
    }

    public static void main(String[] args){
        for(int i = 0; i < 30; i++) {
            new Thread(() -> {
                String str1 = new ThreadPoolHsytrixCommand("BOB").execute();// 阻塞执行
                System.out.println(str1);
             }).start();
        }
        for(int i = 0; i < 30; i++) {
            new Thread(() -> {
                String str1 = new ThreadPoolHsytrixCommand("TOM").execute();// 阻塞执行
                System.out.println(str1);
            }).start();
        }
    }


}
