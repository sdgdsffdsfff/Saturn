# Saturn Executor部署

Executor是作业的执行器。这一节将介绍如何将调试好的作业部署到Executor上面运行。关于如何开发调试作业，见“入门”一章。

Executor启动时，需要指定所属的域。启动后，Executor会自动拉取域下的作业配置信息，然后根据Saturn Console scheudler的调度安排，以及作业的Cron表达式去执行作业调度。

## 1 部署前准备 ##

### 1.1 硬件准备

Linux服务器1台

> 这里只介绍Linux OS的方式。其他OS差别不大。

### 1.2 软件准备

JDK  >= 1.7

### 1.3 检查

- [ ] 检查是否能访问Saturn Console (参见Saturn Console部署指南)
- [ ] 检查Saturn Console上是否有指定的namespace（可以在Console的注册中心搜索一下namespace名字）
- [ ] 检查是否能访问ZooKeeper (参见Saturn Console部署指南)。可以通过telnet 对应zk的端口，默认是（2181）
- [ ] 如果你要部署的是Java作业，检查是否已经打包了一个*-app.zip的包。详情参见[Saturn作业开发指引之Java作业](zh-cn/2.x/saturn-dev-java.md)

## 2 开始部署 ##

### 2.1 设置环境变量

设置saturn console uri:

```Shell
export VIP_SATURN_CONSOLE_URI=http://localhost:9088
```

### 2.2 获取executor ###

从https://github.com/vipshop/Saturn/releases 中点击链接获取最新版本的'Executor Zip File'，
将得到一个saturn-executor-{version}-zip.zip的文件。

将zip文件放到你期望的安装路径下（称作$TARGET_DIR），并解压，得到saturn-executor-{version}的目录。目录结构如下：

    saturn-executor-{version}
        -/bin
        -/demo_script
        -/lib
        -/logs
        -saturn-executor.jar

/bin： 存放executor的启动脚本(saturn-executor.sh)
/bin： 存放executor的启动脚本(saturn-executor.sh)

/demo_script： 一些演示用的脚本(4个php脚本)

/lib:  存放executor的依赖及第三方jar包

/logs: 已经不作使用，将被废弃

saturn-executor.jar：executor启动的主jar

### 2.3 作业部署

#### 2.3.1 部署Shell作业

把开发好的shell脚本放在某一个目录，比如/apps/saturn/shell目录，然后通过 console 添加作业即可。

请确保这些脚本有足够的权限被执行。

#### 2.3.2 部署Java作业

将开发并打包好的**-app.zip在/saturn-executor-{version}同一级目录进行解压。目录结构见下图。

Executor启动时会扫描这个app目录，并加载这个目录下（含子目录)所有的jar包。

```
saturn-executor-{version}
    -/bin
    -/demo_script
    -/lib
    -/logs
    -saturn-executor.jar
app
    -/lib
      - abc.jar
      - xyz.jar
```

### 2.3 网卡名字的修改

确保你的网卡名字包含bond0或者eth0上。否则在启动时报一下错误：

```java
getCachedAddressException:java.lang.Exception: wrong with get ip cause by could not read any info from local host, bond0 and eth0
```

如果没有bond0或者eth0，你需要修改一下你的网卡名字。

### 2.4 启动executor ###

```shell
cd saturn-executor-{version}/bin
#修改权限
chmod a+x saturn-executor.sh
#启动
./saturn-executor.sh start -n www.abc.com -e executor_001
```

参数描述：

| 参数                                           | 必填 | 描述                                                         | 默认值                                             |
| ---------------------------------------------- | ---- | ------------------------------------------------------------ | -------------------------------------------------- |
| -n                                             | Y    | 本executor所属的namespace                                    |                                                    |
| -e                                             | N    | 本executor的唯一ID，如果不指定则使用hostname                 | hostname                                           |
| -env                                           | N    | 运行模式，可取值为dev/product。 dev模式下-Xmx为512m，product模式下-Xmx为2G | product                                            |
| -d                                             | N    | 业务library所在目录                                          | $TARGET_DIR/app                                    |
| -r                                             | N    | 运行模式，前台(foreground)或者后台(background)，空代表background模式。 | 空                                                 |
| -t                                             | N    | 本executor启动的超时时间，单位为秒                           | 120                                                |
| -jmx                                           | N    | jmx端口                                                      | 24501                                              |
| -sld                                           | N    | saturn日志目录                                               | /apps/logs/saturn/{namespace}/{executorname}-{ip}/ |
| jvmArgs                                        | N    | 需要添加的JVM参数                                            | 空                                                 |
| VIP_SATURN_INIT_JOB_BY_GROUPS （-D或环境变量） | N    | 如果Executor机器设置了这个变量，则该Executor只能启动该域下属于该groups的作业； 否则，可以启动该域下全部作业。<br />多值用逗号分隔groups1,groups2 | 无                                                 |
| VIP_SATURN_START_TIMEOUT                       | N    | 本executor的启动超时时间，单位为秒                           | 120                                                |
| VIP_SATURN_RUNNING_IP<br />（-D或环境变量）    | N    | 本executor的运行ip(在某些mac上没有eth0和bond0时启动会报错，通过配置hosts或者配置该环境变量可以解决此报错问题) |                                                    |
| VIP_SATURN_RUNNING_HOSTNAME                    | N    | 本executor的运行主机名                                       |                                                    |

下面展示一个成功启动的console 输出：

```shell
$ ./saturn-executor.sh start -n www.abc.com -e executor-0134

The java version is 1.8.0_121
Log redirects to /apps/logs/saturn/www.abc.com/executor-0134-xxx.xxx.xxx.xxx
The jmx port is 24501.

Saturn executor start successfully, running as process:18332.
```

如果启动失败，根据console提示的路径查看saturn-executor.log。

### 2.5 单台物理机启动多个executor ###

建议在一台物理机启动一个executor。 如果想在单台物理机启动多个executor，建议采用以下步骤：

1. 将saturn-executor-{version} copy到多个

2. 在启动这些executor时，分别指定不同executorname和JMX端口，命令如下：

   ```Shell
   $ ./saturn-executor.sh start -n www.abc.com -e executor-001 -jmx 24502
   ```

   注意：executor name，默认是hostname；jmx端口默认是24501

### 2.6 FAQ

Q：新作业部署时，流程是怎么样？

A：参见“灰度发布”一章。

