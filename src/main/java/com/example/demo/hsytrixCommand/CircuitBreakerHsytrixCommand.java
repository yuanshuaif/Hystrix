package com.example.demo.hsytrixCommand;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by 10326 on 2019/7/5.
 */
public class CircuitBreakerHsytrixCommand extends HystrixCommand<String> {

    String name = "";

     /*因为HystrixCommand没有默认的构造方法，所以此处需要给定构造方法*/
    // 在10s内，如果请求在5个及以上，且有50%失败的情况下，开启断路器；断路器开启1000ms后尝试关闭
    public CircuitBreakerHsytrixCommand(String name) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("ExampleGroup"))  //必须
                        .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                                .withExecutionTimeoutInMilliseconds(500)//请求的超时时间
                                .withCircuitBreakerRequestVolumeThreshold(5)
                                .withCircuitBreakerSleepWindowInMilliseconds(1000)
                                .withCircuitBreakerErrorThresholdPercentage(50)));
        this.name = name;
    }

    @Override
    protected String run() throws Exception {
        Thread.sleep(900);
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
        System.out.println("fail----" + isTimeout + ";" + isCircut);
        return null;
    }

    public static void main(String[] args){
        for(int i = 0; i < 10; i++) {
            Future<String> future = new CircuitBreakerHsytrixCommand("LSJ").queue();// 异步执行:同时触发多个业务依赖的调用
            try {
                String str2 = future.get();
                System.out.println(str2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }
}
