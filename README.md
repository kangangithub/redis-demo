# redis-demo
Redis各种操作

#### 项目介绍
Spring Boot 2.0集成Redis(CRUD,缓存,发布订阅)

####  组织架构
```$xslt
redisdemo
|
|--redis  Redis 模块
|   |--cache  Redis 做缓存
|   |   |--controller  控制层
|   |   |--dao  Mapper接口层  
|   |   |--service  业务层
|   |   
|   |--config Redis 配置+自定义Redis序列化方式
|   |--crud  Redis 的CRUD操作
|   |--pubsub  Redis 的发布订阅

```

####  技术选项

技术|名称
---|---|
Spring Boot 2.0|springboot框架 
hikari|数据库连接池
Lombok|代码简化插件
Redis|Redis数据库
Mybatis-Plus|Mybatis的加强版
Maven|项目构建管理

#### 模块说明

1. redis -- Redis操作(做缓存, CRUD, 发布订阅)
    * POST http://localhost:1004/cache/redis/get/110  
    * POST http://localhost:1004/cache/redis/modify/110  
        `
            {
              "id": 110,
              "name": "IT支持",
              "value": "3",
              "type": "profession_type",
              "description": "职业类型",
              "sn": 3,
              "parentId": 0,
              "createBy": 1,
              "updateBy": 1,
              "createAt": null,
              "updateAt": null,
              "remarks": null,
              "delFlag": 0
            }
        `  