
CREATE TABLE `smt_ts_task_lock` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `gmt_create` datetime NOT NULL COMMENT '创建时间',
  `gmt_modified` datetime DEFAULT NULL COMMENT '更新时间',
  `smc_start_time` bigint(20) NOT NULL COMMENT '开始执行时间,msg',
  `smc_def_id` bigint(20) NOT NULL COMMENT '任务定义id',
  `smc_def_pid` bigint(20) DEFAULT '-1' COMMENT '父任务定义id',
  `smc_time_out` bigint(20) NOT NULL DEFAULT '-1' COMMENT '任务执行超时时间，超时时需要释放锁',
  `smc_ip` varchar(16) NOT NULL COMMENT '调度的机器ip',
  `smc_status` int(11) NOT NULL COMMENT '执行状态，-1:失败，1:执行中，2:执行超时，3:执行完成',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_did_tt` (`smc_def_id`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4;

CREATE TABLE `smt_ts_task_def` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `gmt_create` datetime NOT NULL COMMENT '创建时间',
  `gmt_modified` datetime DEFAULT NULL COMMENT '更新时间',
  `smc_def_pid` bigint(20) DEFAULT '-1' COMMENT '父任务定义id',
  `smc_top_pid` bigint(20) DEFAULT '-1' COMMENT '顶级父任务定义id',
  `smc_task_name` varchar(512) NOT NULL COMMENT '任务名称',
  `app_service_name` varchar(64) NOT NULL COMMENT '注册到注册中心的服务名',
  `api_service_name` varchar(64) NOT NULL COMMENT '接口服务名',
  `api_method_name` varchar(64) NOT NULL COMMENT '接口方法名，为了简单期间，方法不要重载',
  `smc_customer_id` varchar(64) NOT NULL COMMENT '三方唯一 ID',
  `smc_conf_flag` int(11) DEFAULT '0' COMMENT '配置标记,预留字段',
  `smc_timeout` bigint(20) NOT NULL DEFAULT '-1' COMMENT '任务执行超时时间，单位ms，超时将视为执行失败，会重跑，-1表示永不超时',
  `smc_status` int(11) NOT NULL DEFAULT '1' COMMENT '任务是否启动，1:启动，-1:禁用',
  `smc_has_child` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否存在子节点，1:有，0:没有',
  `smc_ext` varchar(1024) DEFAULT NULL COMMENT '扩展参数',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_customer_key` (`smc_customer_id`,`app_service_name`,`api_service_name`,`api_method_name`),
  KEY `idx_top_pid` (`smc_top_pid`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4;

CREATE TABLE `smt_ts_task_timer` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `gmt_create` datetime NOT NULL COMMENT '创建时间',
  `gmt_modified` datetime DEFAULT NULL COMMENT '更新时间',
  `smc_def_id` bigint(20) NOT NULL COMMENT '任务定义ID',
  `smc_timer_type` int(11) DEFAULT '1' COMMENT '1:cron, 2: 固定定时，3:固定延时，4:一次延时',
  `smc_init_delay` bigint(20) DEFAULT '0' COMMENT '初始延时时间',
  `smc_once_delay` bigint(20) DEFAULT '0' COMMENT '延时时间',
  `smc_start_day` datetime DEFAULT NULL COMMENT '定时器有效开启时间',
  `smc_end_day` datetime DEFAULT NULL COMMENT '定时器有效结束时间',
  `smc_period` bigint(20) DEFAULT NULL COMMENT '定时周期',
  `smc_cron` varchar(16) DEFAULT NULL COMMENT 'cron表达式',
  `smc_status` int(11) DEFAULT '1' COMMENT '状态，-1:禁用，1:启动',
  PRIMARY KEY (`id`),
  KEY `idx_did_set` (`smc_def_id`,`smc_start_day`,`smc_end_day`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4;

-- 月表 smt_ts_task_record_202110 smt_ts_task_record_202111
CREATE TABLE `smt_ts_task_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `gmt_create` datetime NOT NULL COMMENT '创建时间',
  `gmt_modified` datetime DEFAULT NULL COMMENT '更新时间',
  `smc_stime` datetime DEFAULT NULL COMMENT '执行开始时间',
  `smc_etime` datetime DEFAULT NULL COMMENT '执行结束时间',
  `smc_def_id` bigint(20) NOT NULL COMMENT '任务定义ID',
  `smc_task_name` varchar(512) NOT NULL COMMENT '任务名称',
  `smc_timeout` bigint(20) NOT NULL DEFAULT '-1' COMMENT '任务执行超时时间，超时将视为执行失败，会重跑',
  `smc_status` int(11) NOT NULL COMMENT '任务状态，-1:失败，1:执行中，2:执行超时，3:执行完成',
  `smc_error` varchar(1024) DEFAULT NULL COMMENT '失败原因',
  `smc_desc` varchar(1024) DEFAULT NULL COMMENT '描述',
  `smc_ip` varchar(16) NOT NULL COMMENT '调度的机器ip',
  PRIMARY KEY (`id`),
  KEY `idx_def_id` (`smc_def_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3464 DEFAULT CHARSET=utf8mb4;


CREATE TABLE `smt_ts_task_msg` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `gmt_create` datetime NOT NULL COMMENT '创建时间',
  `gmt_modified` datetime DEFAULT NULL COMMENT '更新时间',
  `smc_def_id` bigint(20) NOT NULL COMMENT '任务定义ID',
  `smc_action` int(11) NOT NULL COMMENT '操作类型，0:新增，1:修改，2:删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_id_ct` (`id`,`gmt_create`),
  KEY `idx_gc` (`gmt_create`),
  KEY `idx_def_id` (`smc_def_id`)
) ENGINE=InnoDB AUTO_INCREMENT=109 DEFAULT CHARSET=utf8mb4;



CREATE TABLE `smt_ts_consume_progress` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `gmt_create` datetime NOT NULL COMMENT '创建时间',
  `gmt_modified` datetime DEFAULT NULL COMMENT '更新时间',
  `smc_ip` varchar(32) NOT NULL COMMENT '消费机器ip',
  `smc_msg_id` bigint(20) NOT NULL COMMENT '消费进度id',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_ip` (`smc_ip`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4;





