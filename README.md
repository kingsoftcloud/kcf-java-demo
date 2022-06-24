## 更新
**2022.06.21**

增加了推送消息到飞书Bot的功能，以增强KCF整体演示效果，即：有文件上传到KS3后会向飞书Bot发送消息。

引入[OkHttp](https://square.github.io/okhttp/)作为HTTP Client使用。

KCF的函数中目前还不能直接访问公网：绕道方案为：
1. 启用KCF的"访问VPC资源"功能，并选择需要访问的VPC和subnet。
2. 在上一步骤选择的VPC/subnet内的虚拟机上架设一个forward proxy服务。
3. 在OkHttp中设置proxy相关的参数。

## 简介
一个编写KCF函数的demo，也可以作为编写KCF的脚手架使用。
## 说明
只包含基本的、轻量级的RESTful服务框架，编译打包文件小，基础功能打包后的文件（Uber Jar）体积不到9MB，便于在函数中添加更多第三方jar包而不超过KCF函数的体积限制（50MB）。

CloudEvents目前还在快速演进中，使用本项目时，若提示找不到指定版本的与CloudEvents相关的Jar包，则需要自行下载CloudEvents JAVA SDK并编译。本项目目前使用的是`2.3.0`版本：

```
git clone https://github.com/cloudevents/sdk-java.git
git checkout 2.3.0
```

`deploy.sh`用于将打包后的Uber Jar上传到KS3，方便创建部署KCF。目前KCF还没有提供部署工具，用KS3上传比客户端上传更方便一些（ks3util工具请自行准备并配置好）。

本项目使用[apache maven](https://maven.apache.org/)进行构建，通过[Maven Assembly Plugin](https://maven.apache.org/plugins/maven-assembly-plugin/)打包Uber Jar（可以独立执行的jar包）。在使用本项目前，请确保已经正确安装并配置好Maven。

## 引用的项目
[CloudEvents JAVA SDK](https://github.com/cloudevents/sdk-java)

CloudEvents的主要作用是定义了一套消息标准，对于编写KCF函数来说，并不需要依赖完整的CloudEvents JAVA SDK功能。KCF函数中主要通过CloudEvents JAVA SDK来解析触发KCF函数时传递过来的消息，由于消息本身就是符合CloudEvents规范的JSON结构，因此完全可以自己写消息解析逻辑而不依赖CloudEvents JAVA SDK。

[javalin](https://javalin.io/)

轻量级的Java Web Framework，同时支持JAVA和Kotlin，简单，小巧，社区活跃，且有大厂支持。KCF函数仅仅是接受约定的POST请求，然后解析随请求传递过来的消息进行处理，因此并不需要使用Spring Boot之类的重型框架。

[Project Lombok](https://projectlombok.org/)

“摸鱼必备”，如果用IDE（VSCode，Jetbrain，Eclipse等），记得安装Lombok相关的插件，否则会因各种报错而导致无法调试。

[https://www.slf4j.org/](https://www.slf4j.org/)

配合`Lombok`输出日志到控制台，供KCF进行日志捕获。

[CloudEvents KS3消息格式](https://docs.ksyun.com/documents/41718)

如果打算自己写消息解析逻辑而完全脱离CloudEvents SDK的话，请参考此文档。
