# 控制并发，提高系统可靠性和可用性

### 公式
        公式：用户id:api请求路径:最大并发线程数:超时时间
        bus:
            userRuleList:
                -   apiUrl: "/api1"
                    concurrency: 1
                    timeout: 100
            globalRuleList:
                -   apiUrl: "/api2"
                    concurrency: 2
                    timeout: 10
* 普通模式

        单机版的sentinel

* 分布式模式
        
        key: {用户id;api请求路径}:机器id:api
        value: 已发行的通行证
        expire: 30s
        使用lua脚本进行统计
        使用完释放通行证
        （释放失败（redis网络等问题）超时重试）
        参考redisson的看门狗对key进行续期（只要本地有线程在使用）
        
* 特性
  * 轻量版的sentinel
  * 代码少、侵入式低，可拓展性强
  * 开箱即用，引入jar即可
  * 解决防抖问题
  * 解决分布式锁问题
  * 解决单机、分布式限流问题
    
* TODO
      
        自定义注解针对普通办法控制
