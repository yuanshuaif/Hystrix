1.hystrix
2.SpringCloud+Hystrix
    导包
    添加注解@EnableHystrix
    @HystrixCommand
         熔断、线程池隔离、降级、超时（均可以通过 @HystrixCommand的commandProperties参数配置实现）
         缓存的使用：
            1.@HystrixCommand(commandKey = "commandKey1")
            2.@CacheResult(cacheKeyMethod = "getCacheKey")
            3.@CacheRemove(commandKey = "commandKey1", cacheKeyMethod = "getCacheKey")
            或
             1.@HystrixCommand(commandKey = "commandKey1")
             2.@CacheResult          结合@CacheKey实现
             3.@CacheRemove(commandKey = "commandKey1")            结合@CacheKey实现
hystrix+actutor+dashboard