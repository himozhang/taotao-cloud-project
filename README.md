#  <p align="center"><strong> taotao-cloud-project </strong></p>

[comment]: <> (# <center>**taotao-cloud-project**</center>)

<p align="center">
  <img src='https://img.shields.io/badge/license-Apac****he%202-green' alt='License'/>
  <img src="https://img.shields.io/badge/Spring-5.3.22-red" alt="Downloads"/>
  <img src="https://img.shields.io/badge/Spring%20Boot-2.7.2-orange" alt="Downloads"/>
  <img src="https://img.shields.io/badge/Spring%20Cloud-2021.0.3-yellowgreen" alt="Downloads"/>
  <img src="https://img.shields.io/badge/Spring%20Cloud%20alibaba-2021.0.1.0-blue" alt="Downloads"/>
  <img src="https://img.shields.io/badge/Netty-4.1.79.Final-blue" alt="Downloads"/>
  <img src="https://img.shields.io/badge/Spring%20Security-5.7.2-brightgreen" alt="Downloads"/>
  <img src="https://img.shields.io/badge/Elasticsearch-7.13.7-green" alt="Downloads"/>
  <img src="https://img.shields.io/badge/Mybatis%20Plus-3.5.2-yellow" alt="Downloads"/>
  <img src="https://img.shields.io/badge/Knife4j-3.0.3-brightgreen" alt="Downloads"/>
  <img src="https://img.shields.io/badge/Swagger-3.0.0-red" alt="Downloads"/>
  <img src="https://img.shields.io/badge/Redisson-3.17.5-lightgrey" alt="Downloads"/>
  <img src="https://img.shields.io/badge/Hutool-5.8.4-green" alt="Downloads"/>
</p>

## 1. 如果您觉得有帮助，请点右上角 "Star" 支持一下谢谢

**taotao cloud project** 仓库的目的: 工作以来的技术总结和技术沉淀(业余时间进行开发) **仓库代码中不涉及公司任何业务代码** 主要包括如下几部分

- **大数据模块** 集成基于**spark、hive**的日志数据处理和分析, 用户行为分析、推荐系统, **flink、spark streaming**离线/流式计算, 
  **hadoop hive tidb**离线数据仓库, **apache hudi**数据湖 **presto**计算框架等大数据处理
  

- **微服务模块** 基于**spring cloud alibaba**微服务基础脚手架框架,用于基础服务的集成和跟业务无关的基础技术集成, 
  提供**大量的starters组件**作为技术底层支持,同时基础框架集中统一优化中间件相关服务及使用,
  提供高性能,更方便的基础服务接口及工具，完全可以在实际工作中使用


- **商城模块** 基于**微服务模块**构建的前后端分离的B2B2C商城系统, 支持商家入驻支, 持分布式部署, 使用**github action CI/CD**持续集成, 前后端均使用**kubernetes**部署，
  各个API独立, 管理前端使用**vue3 ant-design-vue**开发, 移动端使用**taro taro-ui**开发, **系统全端全部代码开源**
  

- **前端模块** 主要使用**react antd**进行前后端分离开发, 集成以**taro, taro-ui, react native**为主的多端合一框架。
  

- **python模块** 主要是集成了基于**django**的web开发, 基于**scrapy**爬虫开发, **homeassistant**家庭自动化框架原理的分析

总之基于**spring cloud alibaba**的微服务架构 **spark hive hudi flink**等大数据处理实践。旨在提供技术框架的基础能力的封装，减少开发工作，只关注业务

## 2. spring cloud 微服务架构图
![mark](./snapshot/architecture.jpg)


## 3. spring cloud 微服务分层图
![mark](./snapshot/springcloud微服务分层图.png)


## 4. dependencies
Requires:
```
JAVA_VERSION >= 17 (推荐使用amazon-corretto-17)
GRALE_VERSION >= 7.5.1
IDEA_VERSION >= 2022.2
```

Gradle:
```
dependencyManagement{
  imports {
    mavenBom "io.github.shuigedeng:taotao-cloud-dependencies:2022.08"
  }
}

api "io.github.shuigedeng:taotao-cloud-starter-web"
```

Maven:
```
<dependentyManagement>
  <dependencies>
    <dependency>
      <groupId>io.github.shuigedeng</groupId>
      <artifactId>taotao-cloud-dependencies</artifactId>
      <version>2022.08</version>
      <type>pom</type>
      <scope>import</scope>
    </dependency>
  </dependencies>
</dependentyManagement>


<dependencies>
    <dependency>
      <groupId>io.github.shuigedeng</groupId>
      <artifactId>taotao-cloud-starter-web</artifactId>
    </dependency>
</dependencies>
```


## 5. 核心依赖 
依赖 | 版本
---|---
Spring |  5.3.22
Spring Boot |  2.7.2
Spring Cloud | 2021.0.3
Spring Cloud alibaba | 2021.0.1.0
Spring Security | 5.7.2
Mybatis Plus | 3.5.2
Hutool | 5.5.9
Mysql | 8.0.29
Querydsl | 5.0.0
Swagger | 3.3.0
Knife4j | 3.0.2
Redisson | 3.15.0
Lettuce | 6.0.2.RELEASE
Elasticsearch | 7.1.2
Xxl-job | 2.2.0
EasyCaptcha | 1.6.2
Guava | 29.0-jre

## 6. 演示地址
* Github: [https://github.com/shuigedeng/taotao-cloud-project](https://github.com/shuigedeng/taotao-cloud-project)
* Gitee: [https://gitee.com/dtbox/taotao-cloud-project](https://gitee.com/dtbox/taotao-cloud-project) 


* 博客地址: [https://blog.taotaocloud.top](https://blog.taotaocloud.top) 源码地址: [taotao-cloud-blog](https://github.com/shuigedeng/shuigedeng.git)
* 代码质量检测结果地址: [https://qodana.taotaocloud.top](https://qodana.taotaocloud.top) (带宽有限, 需多刷新几次)
* 商城首页地址: [https://taotaocloud.top](https://taotaocloud.top) 源码地址: [taotao-cloud-front](https://github.com/shuigedeng/taotao-cloud-project/tree/master/taotao-cloud-microservice/taotao-cloud-front)
* 大屏展示地址: [https://datav.taotaocloud.top](https://datav.taotaocloud.top) 源码地址: [taotao-cloud-datav](https://github.com/shuigedeng/taotao-cloud-project/tree/master/taotao-cloud-ui/taotao-cloud-datav)
* 平台管理地址(进度15%): [https://manager.taotaocloud.top](https://manager.taotaocloud.top) (admin/123456) 源码地址: [taotao-cloud-manager](https://github.com/shuigedeng/taotao-cloud-project/tree/master/taotao-cloud-ui/taotao-cloud-manager)
* 商户管理地址(进度5%): [https://merchant.taotaocloud.top](https://merchant.taotaocloud.top) (taotao/123456) 源码地址: [taotao-cloud-merchant](https://github.com/shuigedeng/taotao-cloud-project/tree/master/taotao-cloud-web/taotao-cloud-merchant)
* 开放平台地址(进度15%): [https://open.taotaocloud.top](https://open.taotaocloud.top) (taotao/123456) 源码地址: [taotao-cloud-open](https://github.com/shuigedeng/taotao-cloud-project/tree/master/taotao-cloud-ui/taotao-cloud-open)
* 移动端在线预览(进度5%) 源码地址: [taotao-cloud-mall](https://github.com/shuigedeng/taotao-cloud-project/tree/master/taotao-cloud-web/taotao-cloud-mall)

| <center>移动端 ReactNative</center>                                                                                                                                                                                                                                                         | <center>小程序</center>     | <center>H5</center>                                       |
|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|--------------------------|-----------------------------------------------------------|
| 安卓：[滔滔商城.apk](https://github.com/wuba/Taro-Mortgage-Calculator/raw/e0c432bdc6096a08d9020542e7ce401861026bfa/app-arm64-v8a-release.apk.1.zip) <br> IOS：[滔滔商城.app](https://github.com/wuba/Taro-Mortgage-Calculator/raw/a67459bc6667b0478978621482d33103d04e7538/taroDemo.app.zip)(目前暂不可用) | ![](snapshot/qrcode.png) | ![](snapshot/h5.png)<br>https://m.taotaocloud.top |


## 7. 功能特点

* **微服务技术框架**: 前后端分离的企业级微服务架构、主要针对解决微服务和业务开发时常见的**非功能性需求** 
* **主体框架**：采用最新的Spring Boot 2.7.1、Spring Cloud 2021.0.2、Spring Cloud Alibaba 2021.0.1.0版本进行设计
* **统一注册**：支持Nacos作为注册中心，实现多配置、分群组、分命名空间、多业务模块的注册和发现功能
* **统一认证**：统一Oauth2认证协议，采用jwt的方式，实现统一认证，完备的RBAC权限管理、数据权限处理、网关统一鉴权、灰度发布
* **业务监控**：利用Spring Boot admin 监控各个独立服务的运行状态
* **日志分析**：集成kafka、elk、prometheus实时监控日志(请求日志、系统日志、数据变更日志、用户日志)
* **分布式事务**：集成spring cloud alibaba seata分布式事务处理
* **业务熔断**：采用spring cloud alibaba Sentinel实现业务熔断处理，避免服务之间出现雪崩
* **链路追踪**：自定义traceId的方式，实现简单的链路追踪功能、集成skywalking、sleuth、zipkin链路监控
* **分布式任务**：集成xxl-job分布式定时任务处理
* **内部调用**：集成了Feign和Dubbo两种模式支持内部调用，并且可以实现无缝切换
* **身份注入**：通过注解的方式，实现用户登录信息的快速注入
* **在线文档**：通过接入Knife4j，实现在线API文档的查看与调试
* **消息中心**：集成消息中间件RocketMQ、kafka，对业务进行异步处理
* **业务分离**：采用前后端分离的框架设计，前端采用react antd脚手架快速开放
* **多租户功能**：集成Mybatis Plus、jpa,实现saas多租户功能  
* **容器化支持**: Docker、Kubernetes、Rancher2 支持  
* **webflux**支持: lambda、stream api、webflux 的生产实践
* **开放平台**: 提供应用管理，方便第三方系统接入，**支持多租户(应用隔离)**
* **组件化**: 引入组件化的思想实现高内聚低耦合并且高度可配置化
* **代码规范**: 注重代码规范，严格控制包依赖

> PS: 借鉴了其他开源项目

## 8. 模块说明
```
taotao-cloud-project -- 父项目
│  ├─taotao-cloud-bigdata -- 大数据模块
│  ├─taotao-cloud-container -- 容器模块
│  ├─taotao-cloud-dependencies -- 全局公共依赖模块
│  ├─taotao-cloud-go -- go模块
│  ├─taotao-cloud-java -- java模块
│  ├─taotao-cloud-microservice -- 微服务业务模块
│  │  ├─taotao-cloud-monitor  -- 监控模块
│  │  ├─taotao-cloud-auth  -- oauth2认证中心模块
│  │  ├─taotao-cloud-customer  -- 客服模块
│  │  ├─taotao-cloud-distribution  -- 营销/分销模块
│  │  ├─taotao-cloud-front  -- 前端pc模块
│  │  ├─taotao-cloud-gateway  -- 网关模块
│  │  ├─taotao-cloud-goods  -- 商品模块
│  │  ├─taotao-cloud-graphql  -- graphql模块
│  │  ├─taotao-cloud-member  -- 会员模块
│  │  ├─taotao-cloud-message  -- 消息模块
│  │  ├─taotao-cloud-open  -- 开放模块
│  │  ├─taotao-cloud-operation  -- 运营模块
│  │  ├─taotao-cloud-order  -- 订单模块
│  │  ├─taotao-cloud-payment -- 支付模块
│  │  ├─taotao-cloud-promotion  -- 促销模块
│  │  ├─taotao-cloud-recommend  -- 推荐模块
│  │  ├─taotao-cloud-report  -- 报表模块
│  │  ├─taotao-cloud-starter  -- starter组件模块
│  │  ├─taotao-cloud-stock  -- 库存模块
│  │  ├─taotao-cloud-store  -- 店铺模块
│  │  ├─taotao-cloud-sys  -- 系统模块
│  │  ├─taotao-cloud-xxljob  -- xxl-job模块
│  ├─taotao-cloud-netty -- netty模块
│  ├─taotao-cloud-nod -- node模块
│  ├─taotao-cloud-plugin -- 插件模块
│  ├─taotao-cloud-python -- python模块
│  ├─taotao-cloud-reactive -- spring web响应式模块
│  ├─taotao-cloud-rpc -- rpc模块
│  ├─taotao-cloud-scala -- scala模块
│  ├─taotao-cloud-spring-native -- spring模块
│  ├─taotao-cloud-standlone -- 单项目模块
│  ├─taotao-cloud-warehouse -- 数仓模块
│  │  ├─taotao-cloud-offline-warehouse  -- 离线仓库模块
│  │  ├─taotao-cloud-offline-weblog -- 离线日志分析模块
│  │  ├─taotao-cloud-realtime-datalake  -- 准实时数据湖模块
│  │  ├─taotao-cloud-realtime-mall -- 商城日志分析模块
│  │  ├─taotao-cloud-realtime-recommend -- 实时推荐模块
│  │  ├─taotao-cloud-realtime-travel -- 实时旅游模块
│  ├─taotao-cloud-ui -- 前端模块
│  │  ├─taotao-cloud-datav -- 大屏PC端展示模块
│  │  ├─taotao-cloud-front -- 商城PC端模块
│  │  ├─taotao-cloud-mall  -- 商城移动端模块
│  │  ├─taotao-cloud-manager  -- 平台管理端模块
│  │  ├─taotao-cloud-merchant  -- 商户管理端模块
│  │  ├─taotao-cloud-open -- 开放平台PC端模块
```

## 9.开源共建

1. 欢迎提交 [pull request](https://github.com/shuigedeng/taotao-cloud-project)，注意对应提交对应 `dev` 分支

2. 欢迎提交 [issue](https://github.com/shuigedeng/taotao-cloud-project/issues)，请写清楚遇到问题的原因、开发环境、复显步骤。

3. 不接受`功能请求`的 [issue](https://github.com/shuigedeng/taotao-cloud-project/issues)，功能请求可能会被直接关闭。  

4. mail: <a href="981376577@qq.com">981376577@qq.com</a> | <a target="_blank" href="http://wpa.qq.com/msgrd?v=3&uin=3130998334&site=qq&menu=yes"> QQ: 981376577</a>    

## 10.参与贡献

开发: 目前个人独立开放

## 11.项目截图
<table>
    <tr>
        <td><img alt="调度任务中心" src="snapshot/project/1.png"/></td>
        <td><img alt="nacos服务注册" src="snapshot/project/2.png"/></td>
    </tr>
	<tr>
        <td><img alt="granfana页面" src="snapshot/project/3.png"/></td>
        <td><img alt="prometheus页面" src="snapshot/project/4.png"/></td>
    </tr>
	<tr>
        <td><img alt="skywalking页面" src="snapshot/project/5.png"/></td>
        <td><img alt="sentinel页面" src="snapshot/project/6.png"/></td>
    </tr>
    <tr>
        <td><img alt="kibana页面" src="snapshot/project/7.png"/></td>
        <td><img alt="zipkin页面" src="snapshot/project/8.png"/></td>
    </tr>
    <tr>
        <td><img alt="springadmin页面" src="snapshot/project/9.png"/></td>
        <td><img alt="knife4j页面" src="snapshot/project/10.png"/></td>
    </tr>
    <tr>
        <td><img alt="swagger页面" src="snapshot/project/11.png"/></td>
        <td><img alt="arthas页面" src="snapshot/project/12.png"/></td>
    </tr>

[comment]: <> (    <tr>)

[comment]: <> (        <td><img alt="日志中心02" src="https://gitee.com/zlt2000/images/raw/master/%E6%97%A5%E5%BF%97%E4%B8%AD%E5%BF%8302.png"/></td>)

[comment]: <> (        <td><img alt="慢查询sql" src="https://gitee.com/zlt2000/images/raw/master/%E6%85%A2%E6%9F%A5%E8%AF%A2sql.png"/></td>)

[comment]: <> (    </tr>)

[comment]: <> (    <tr>)

[comment]: <> (        <td><img alt="nacos-discovery" src="https://gitee.com/zlt2000/images/raw/master/nacos-discovery.png"/></td>)

[comment]: <> (        <td><img alt="应用吞吐量监控" src="https://gitee.com/zlt2000/images/raw/master/%E5%BA%94%E7%94%A8%E5%90%9E%E5%90%90%E9%87%8F%E7%9B%91%E6%8E%A7.png"/></td>)

[comment]: <> (    </tr>)
</table>
