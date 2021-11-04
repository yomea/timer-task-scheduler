
CREATE TABLE xxx_im.smt_ts_task_lock (
  id BIGSERIAL NOT NULL PRIMARY KEY,
  gmt_create timestamp NOT NULL ,
  gmt_modified timestamp DEFAULT NULL ,
  smc_start_time bigint NOT NULL ,
  smc_def_id bigint NOT NULL ,
  smc_def_pid bigint DEFAULT -1 ,
  smc_time_out bigint NOT NULL DEFAULT -1 ,
  smc_ip varchar(16) NOT NULL ,
  smc_status int NOT NULL
);

create unique index on xxx_im.smt_ts_task_lock(smc_def_id);

CREATE TABLE xxx_im.smt_ts_task_def (
  id BIGSERIAL NOT NULL PRIMARY KEY,
  gmt_create timestamp NOT NULL ,
  gmt_modified timestamp DEFAULT NULL ,
  smc_def_pid bigint DEFAULT '-1' ,
  smc_top_pid bigint DEFAULT '-1' ,
  smc_task_name varchar(512) NOT NULL ,
  app_service_name varchar(64) NOT NULL ,
  api_service_name varchar(64) NOT NULL ,
  api_method_name varchar(64) NOT NULL ,
  smc_customer_id varchar(64) NOT NULL ,
  smc_conf_flag int DEFAULT '0' ,
  smc_timeout bigint NOT NULL DEFAULT '-1' ,
  smc_status int NOT NULL DEFAULT '1' ,
  smc_has_child int NOT NULL DEFAULT '0' ,
  smc_ext varchar(1024) DEFAULT NULL
);

create unique index on xxx_im.smt_ts_task_def(smc_customer_id,app_service_name,api_service_name,api_method_name);
create index on xxx_im.smt_ts_task_def(smc_top_pid);

CREATE TABLE xxx_im.smt_ts_task_timer (
  id BIGSERIAL NOT NULL PRIMARY KEY,
  gmt_create timestamp NOT NULL ,
  gmt_modified timestamp DEFAULT NULL ,
  smc_def_id bigint NOT NULL ,
  smc_timer_type int DEFAULT '1' ,
  smc_init_delay bigint DEFAULT '0' ,
  smc_once_delay bigint DEFAULT '0' ,
  smc_start_day timestamp DEFAULT NULL ,
  smc_end_day timestamp DEFAULT NULL ,
  smc_period bigint DEFAULT NULL ,
  smc_cron varchar(16) DEFAULT NULL ,
  smc_status int DEFAULT '1'
);

create index on xxx_im.smt_ts_task_timer(smc_def_id,smc_start_day,smc_end_day);

-- 月表 smt_ts_task_record_202110 smt_ts_task_record_202111
CREATE TABLE xxx_im.smt_ts_task_record (
  id BIGSERIAL NOT NULL PRIMARY KEY,
  gmt_create timestamp NOT NULL ,
  gmt_modified timestamp DEFAULT NULL ,
  smc_stime timestamp DEFAULT NULL ,
  smc_etime timestamp DEFAULT NULL ,
  smc_def_id bigint NOT NULL ,
  smc_task_name varchar(512) NOT NULL ,
  smc_timeout bigint NOT NULL DEFAULT '-1' ,
  smc_status int NOT NULL ,
  smc_error varchar(1024) DEFAULT NULL ,
  smc_desc varchar(1024) DEFAULT NULL ,
  smc_ip varchar(16) NOT NULL
);

create index on xxx_im.smt_ts_task_record(smc_def_id);


CREATE TABLE xxx_im.smt_ts_task_msg (
  id BIGSERIAL NOT NULL PRIMARY KEY,
  gmt_create timestamp NOT NULL ,
  gmt_modified timestamp DEFAULT NULL ,
  smc_def_id bigint NOT NULL ,
  smc_action int NOT NULL
) ;

create unique index on xxx_im.smt_ts_task_msg(id,gmt_create);
create index  on xxx_im.smt_ts_task_msg(gmt_create);
create index on xxx_im.smt_ts_task_msg(smc_def_id);


CREATE TABLE xxx_im.smt_ts_consume_progress (
  id BIGSERIAL NOT NULL PRIMARY KEY,
  gmt_create timestamp NOT NULL ,
  gmt_modified timestamp DEFAULT NULL ,
  smc_ip varchar(32) NOT NULL ,
  smc_msg_id bigint NOT NULL
);

create unique index on xxx_im.smt_ts_consume_progress(smc_ip);





