# ubpm多模块demo(脚手架原型）

## 使用 （根据脚手架生成项目）
mvn archetype:generate -DgroupId=com.shinemo.test -DartifactId=archetype-test -Dversion=1.0.3-SNAPSHOT -DarchetypeGroupId=com.shinemo.demo -DarchetypeArtifactId=task-scheduler-archetype -DarchetypeVersion=1.0.3-SNAPSHOT -X -DarchetypeCatalog=local

## 约定
- 业务异常统一使用com.shinemo.common.tools.exception.ApiException

## 项目结构
- task-scheduler-client: 提供给外部依赖的client
- task-scheduler-core: service等业务逻辑实现层
- task-scheduler-dal:  数据库操作层
- task-scheduler-web:  http接口层

## mapper自动生成
使用mybatis-generator插件自动生成

## 核心配置
- datasource配置: springboot autoconfig
- redis配置: springboot autoconfig
- springboot定时任务线程池配置: com.shinemo.demo.module.core.config.CoreConfig
- http 全局异常处理器: com.shinemo.demo.module.web.exception.handler.GlobalExceptionHandler

## 打包
1. 执行mvn clean package -Dmaven.test.skip=true 打成zip包。
2. unzip后，执行run.sh (start、stop、restart)启动、停止、重启。
