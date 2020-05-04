package com.example.demo.hsytrixCommand;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixRequestCache;
import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategyDefault;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;

/**
 * Created by 10326 on 2019/7/5.
 * 1.getCacheKey()
 * 2.HystrixRequestContext.initializeContext()
 * 3.清除缓存：HystrixRequestCache.getInstance(COMMAND_KEY, HystrixConcurrencyStrategyDefault.getInstance()).clear(name);
 */
public class CacheHsytrixCommand extends HystrixCommand<String> {

    String name = "";
    /*因为HystrixCommand没有默认的构造方法，所以此处需要给定构造方法*/
    public CacheHsytrixCommand(String name) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("CachedCommand")).andCommandKey(HystrixCommandKey.Factory.asKey(name)));
        this.name = name;
    }

    @Override
    protected String run() throws Exception {
        System.out.println("laozizhaorit");
        return "Hello " + name + "!";
    }

    /**
     * 缓存的使用
     * @return
     */
    protected String getCacheKey() {
        return this.name;
    }

    /**
     * 缓存的清除（针对某一个command操作） 把command对应的key删掉
     * @param name // 缓存的key
     */
    protected static void flushCache(String name) {
        HystrixRequestCache.getInstance(HystrixCommandKey.Factory.asKey(name), HystrixConcurrencyStrategyDefault.getInstance()).clear(name);
    }

    public static void main(String[] args){
        HystrixRequestContext context = HystrixRequestContext.initializeContext();// 使用缓存的前提
        String str1 = new CacheHsytrixCommand("BOB").execute();// 阻塞执行
        System.out.println(str1);
        String str2 = new CacheHsytrixCommand("BOB").execute();// 阻塞执行
        System.out.println(str2);

        flushCache("BOB");
        String str3 = new CacheHsytrixCommand("BOB").execute();// 阻塞执行
        System.out.println(str3);
        context.shutdown();
    }
}
