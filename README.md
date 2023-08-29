
<div style="margin-top: 40px; margin-bottom: -30px;">
    <p align="center" style="display: flex; justify-content: center; gap: 20px;">
      <img src="https://img.shields.io/badge/timer%20task%20scheduler-v0.1%20release-blue" style="max-width: 100px; height: auto;">
      <img src="https://img.shields.io/badge/Source-github-d021d6?style=flat&logo=GitHub" style="max-width: 100px; height: auto;">
      <img src="https://img.shields.io/badge/JDK-1.8+-ffcc00" style="max-width: 100px; height: auto;">
      <img src="https://img.shields.io/badge/Apache_License-2.0-33ccff" style="max-width: 100px; height: auto;">
    </p>
</div>

## 一、需求

构建一个统一的调度系统，用于触发定时任务的调度。


## 二、方案设计

### 2.1 方案1：快速迭代方案-基于数据库的集群调度

编写一个基于数据库锁（定时任务 id 做唯一键）与公司rpc框架相结合的轻量级定时任务调度中心

### 2.2 方案2：基于master选举的分布式定时任务调度方案

（可使用开源项目框架，但目前基本都是ZK的，受限与大部分小公司现状，可自行开发redis版本） -》分布式任务调度：https://blog.csdn.net/qq_27785239/article/details/120578171?spm=1001.2014.3001.5502
<br /> 注意：定时任务调度任务唯一标识：任务id


## 三、架构设计

根据大部分小公司现状与简化开发工作量，我们选择方案1，方案1的大体架构如下：

![yuque_diagram (1)](https://user-images.githubusercontent.com/20855002/137705613-f8d7437c-8edf-4dfd-bdb9-6ba501c6ef27.jpg)


从架构上看，架构总体分为 调度中心（只调度，不做具体业务处理）与业务中心（把用户的机器当作worker处理任务），从技术选型上来看它只使用了MySql，rpc框架，这对于一个互联网公司来说是标配。

它有以下几个优点：

- 只需要MySql，RPC框架（dubbo，springCloud等都行）。
- 调度中心与worker机器的通信可通过rpc框架来支撑
- 负载均衡与worker机器的下线上线，心跳全由rpc框架处理
- 采用数据库锁控制调度中心集群对任务的唯一调度
- 如果任务支持分片，那么可以实现任务分片回调，通过rpc框架获取到的机器地址进行分发

缺点：

- 基于数据库锁，如果调度中心集群比较大，会给数据库造成压力。


## 四、业务流程图

调度中心某节点大致内部流程

![yuque_diagram (2)](https://user-images.githubusercontent.com/20855002/137705664-934dc454-0ebf-4ed9-9f19-10aa2b3b79e9.jpg)

## 五、数据库表设计

### 5.1 核心表设计

- 调度任务分布式锁

用于调度中心集群触发任务时的分布式锁，避免同时调度

```sql
create table ts_task_lock (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `gmt_create` datetime NOT NULL COMMENT '创建时间',
  	`gmt_modified` datetime DEFAULT NULL COMMENT '更新时间',
	`smc_start_time` bigint(20) NOT NULL COMMENT '开始执行时间,msg',
	`smc_def_id` bigint(20) not null COMMENT '任务定义id',
	`smc_def_pid` bigint(20) default -1 COMMENT '父任务定义id',
	`smc_time_out` bigint(20) not null default -1  COMMENT '任务执行超时时间，超时时需要释放锁',
	`smc_ip` varchar(16) not null COMMENT '调度的机器ip',
	`smc_status` int not null COMMENT '执行状态，-1:失败，1:执行中，2:执行超时，3:执行完成',
  	PRIMARY KEY (`id`),
	unique uk_did_tt(smc_def_id)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;
```



- 任务定义

用于记录该任务如何调度

```sql
create table ts_task_def (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `gmt_create` datetime NOT NULL COMMENT '创建时间',
  	`gmt_modified` datetime DEFAULT NULL COMMENT '更新时间',
	`smc_def_pid` bigint(20) default -1 COMMENT '父任务定义id',
	`smc_top_pid` bigint(20) default -1 COMMENT '顶级父任务定义id',
	`smc_task_name` varchar(512) not null COMMENT '任务名称',
	`app_service_name` varchar(64) not null COMMENT '注册到注册中心的服务名',
	`api_service_name` varchar(64) not null COMMENT '接口服务名',
	`api_method_name` varchar(64) not null COMMENT '接口方法名，为了简单明了，方法不要重载',
	`smc_conf_flag` int default 0 COMMENT '配置标记,预留字段',
	`smc_timeout` bigint(20) not null default -1 COMMENT '任务执行超时时间，单位ms，超时将视为执行失败，会重跑，-1表示永不超时',
	`smc_status` int not null default 1 COMMENT '任务是否启动，1:启动，-1:禁用',
	`smc_has_child` tinyint not null default 0 COMMENT '是否存在子节点，1:有，0:没有',
		
  	PRIMARY KEY (`id`),
	key idx_top_pid(smc_top_pid)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;
```

- 任务定时器

配置任务的定时器，每个任务可以有多个定时器，根据时间段来启动

```sql
create table ts_task_timer (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `gmt_create` datetime NOT NULL COMMENT '创建时间',
  	`gmt_modified` datetime DEFAULT NULL COMMENT '更新时间',
    `smc_def_id` bigint not null COMMENT '任务定义ID',
	`smc_timer_type` int  default 1 COMMENT '1:cron, 2: 固定定时，3:固定延时，4:一次延时',
	`smc_init_delay` bigint default 0 COMMENT '初始延时时间',
	`smc_once_delay` bigint default 0 COMMENT '延时时间',
	`smc_start_day` datetime COMMENT '定时器有效开启时间',
	`smc_end_day` datetime COMMENT '定时器有效结束时间',
	`smc_period` bigint COMMENT '定时周期',
	`smc_cron` varchar(16) COMMENT 'cron表达式',
	`smc_status` int default 1 COMMENT '状态，-1:禁用，1:启动',
		
  	PRIMARY KEY (`id`),
	key idx_did_set(`smc_def_id`,`smc_start_day`,`smc_end_day`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;
```

- 任务记录

每个任务的触发记录，按月份分表

```sql
create table ts_task_record (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `gmt_create` datetime NOT NULL COMMENT '创建时间',
  	`gmt_modified` datetime DEFAULT NULL COMMENT '更新时间',
	`smc_stime` datetime COMMENT '执行开始时间',
	`smc_etime` datetime COMMENT '执行结束时间',
    `smc_def_id` bigint  not null COMMENT '任务定义ID',
	`smc_task_name` varchar(512) not null COMMENT '任务名称',
	`smc_timeout` bigint(20) not null default -1 COMMENT '任务执行超时时间，超时将视为执行失败，会重跑',
	`smc_status` int not null COMMENT '任务状态，-1:失败，1:执行中，2:执行超时，3:执行完成',
	`smc_error` varchar(1024) COMMENT '失败原因',
	`smc_desc` varchar(1024) COMMENT '描述',
	`smc_ip` varchar(16) not null COMMENT '调度的机器ip',
  	PRIMARY KEY (`id`),
	key idx_def_id(smc_def_id)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

```

- 更新消息表
- 
用于任务新增，更新和删除时，对对应的定时任务做cancel处理，uk_id_ct 唯一索引用于获取某机器的消费进度

```sql
create table ts_task_msg (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `gmt_create` datetime NOT NULL COMMENT '创建时间',
  	`gmt_modified` datetime DEFAULT NULL COMMENT '更新时间',
    `smc_def_id` bigint  not null COMMENT '任务定义ID',
	`smc_action` int not null COMMENT '操作类型，0:新增，1:修改，2:删除',
  	PRIMARY KEY (`id`),
	unique uk_id_ct(`id`,`gmt_create`),
	key idx_gc(`gmt_create`),
	key idx_def_id(smc_def_id)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

```
- 消费进度

当发生定时任务的更新与删除时，每个机器需要更新自身任务，比如取消，删除等操作

```sql
create table ts_consume_progress (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `gmt_create` datetime NOT NULL COMMENT '创建时间',
  	`gmt_modified` datetime DEFAULT NULL COMMENT '更新时间',
    `smc_ip` varchar(32) not null COMMENT '消费机器ip',
	`smc_msg_id` bigint(20) not null COMMENT '消费进度id',
  	PRIMARY KEY (`id`),
	unique uk_ip(smc_ip)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

```

## 六、快速开始
- springboot worker端自动装配 jar 工程 git地址：https://gitee.com/yomea/task-scheduler-starter
- 调度中心 git地址：https://gitee.com/yomea/timer-task-scheduler

代码中使用的rpc框架为本人自己开发的rpc框架（https://gitee.com/yomea/hanggu-rpc），原理与其他开源的rpc框架是一样的

### 6.1 引入jar

```xml
<dependency>
	<groupId>com.hangu.task</groupId>
  <artifactId>task-scheduler-starter</artifactId>
  <version>1.0.0-SNAPSHOT</version>
</dependency>
```

### 6.2 application.yml

```yaml
task:
  scheduler:
    enable: true # 开启定时任务调度
    app-service-name: ${app.name} # 应用服务名，调度中心通过该服务名获取应用所在机器地址，将该机器当作worker处理任务
    task-deal-core-thread-num: 20 # worker处理任务的核心线程数
    task-deal-max-thread-num: 20 # worker处理任务的最大线程数
    task-deal-max-queue-size: 10000 # 处理任务的最大队列长度
```

如果任务处理比较慢， 可适当增加 task-deal-max-queue-size 的大小，如果处理不过来，可适当
横向扩展机器


### 6.3 任务实现

```java
@TaskScheduleService(apiServiceName="hangu")
public interface Ihangu {
	
	//返回值为void，如果调用完成不抛出错误，即任务任务执行成功
	@TaskScheduleMethod(name="task1")
	void task1(TaskContext taskContext);
	
	//返回值为int，使用枚举，返回0为成功
	int task2();
	
	//返回success为成功
	ApiResult task2(TaskContext taskContext);
	
	ApiResult task3(TaskContext taskContext);
}
```
Ihangu是一个用户方的业务接口，方法格式按照以上来写即可
如果参数中有 TaskContext 参数，那么会把此次调用的任务id传过来，用于业务方进行幂等性校验等操作
注意：注解需要标注在接口上才生效


### 6.4 任务新增或修改

- rpc 接口： com.hangu.task.hangu.impl.TaskSchedulerFacade#submitTimerTask

代码演示：

```java
@Resource
private TaskSchedulerFacade taskSchedulerFacade;

public void test() {
	TimerTaskRequest timerTaskRequest = TimerTaskRequest.builder().taskName("测试呀！")
			.timeout(10000L).appServiceName("das-model").apiServiceName("buildModelService")
			.apiMethodName("doBuild").taskDefStatus(TaskStatusEnum.ENABLE.getStatus())
			.addDelayTimerTask(DelayTimerTask.builder().delay(10000L).build())
			.addCronTimerTask(CronTimerTask.builder().cron("0/5 * * * * * ?").startDateTime(new Date()).endDateTime(DateUtils.addDays(new Date(), 10)).build())
									.build();
	ApiResult<Long> apiResult = taskSchedulerFacade.submitTimerTask(timerTaskRequest);
}
```

- http 接口：http://localhost:9222/task-scheduler/task/submitTimerTask


http:
```json
{
	"taskInfoConf":{
		"status":TaskStatusEnum.ENABLE.getStatus(),//任务是否启动，1:启动，-1:禁用
		"taskId":123,//如果是新增，这个字段不传，如果是修改请加上这个参数
		"taskName":"hangu",
		"timeout":1000,//超时时间，单位ms
		"appServiceName":"das-sources",//注册到注册中心的服务名
		"apiServiceName":"apiDataSourceGet",//接口服务名
		"apiMethodName":"exec",//接口方法名，为了简单期间，方法不要重载
		"subTaskList":[
			{
			"status":TaskStatusEnum.ENABLE.getStatus(),//任务是否启动，1:启动，-1:禁用
			"taskId":123,//如果是新增，这个字段不传，如果是修改请加上这个参数
			"taskName":"hangu",
			"timeout":1000,//超时时间，单位ms
			"appServiceName":"das-sources",//注册到注册中心的服务名
			"apiServiceName":"apiDataSourceGet",//接口服务名
			"apiMethodName":"exec",//接口方法名，为了简单期间，方法不要重载
			"subTaskList":[
				{
				"status":TaskStatusEnum.ENABLE.getStatus(),//任务是否启动，1:启动，-1:禁用
				"taskId":123,//如果是新增，这个字段不传，如果是修改请加上这个参数
				"taskName":"hangu",
				"timeout":1000,//超时时间，单位ms
				"appServiceName":"das-sources",//注册到注册中心的服务名
				"apiServiceName":"apiDataSourceGet",//接口服务名
				"apiMethodName":"exec"//接口方法名，为了简单期间，方法不要重载
				}
			}
	]
		},
	"taskScheduleConf":{
		"timerList":[
			{
			"timerType":1,
			"cron":"12 12 12 0/2 * *",
			"startDateTime":"2021-08-09",
			"endDateTime":"2021-10-01"
			},
			{
			"timerType":4,
			"delay":10000,//ms
			"startDateTime":"2021-08-09",
			"endDateTime":"2021-10-01"
			}
		]
	}
	
}
```

响应

```json

{
 "code":200,
 "msg":"",
 "data":{
 	"taskId":112
 }
}

```
返回一个任务id，这样任务提交方按照自己业务是否需要保存这个任务id，如果有对任务进行操作的需求，那么建议保存

### 6.5 任务删除

- rpc：com.hangu.task.hangu.impl.TaskSchedulerFacade#timerTaskDel

```json
{
	"taskId":122
}
```

### 6.6 任务禁用

- rpc：com.hangu.task.hangu.impl.TaskSchedulerFacade#disableTask
- http：http://localhost:9222/task-scheduler/task/disableTask

参数 taskId

### 6.7 任务启用

- rpc：com.hangu.task.hangu.impl.TaskSchedulerFacade#enableTask
- http：http://localhost:9222/task-scheduler/task/enableTask

参数 taskId

### 6.8 立即执行某任务

- rpc：com.hangu.task.hangu.impl.TaskSchedulerFacade#execTaskImmediately
- http：http://localhost:9222/task-scheduler/task/execTaskImmediately

参数 taskId


