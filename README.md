
###  **盒子IM**
1. 盒子IM是一个仿微信实现的网页版聊天软件，不依赖任何第三方收费组件。
1. 支持私聊、群聊、离线消息、发送语音、图片、文件、已读未读、群@等功能
1. 支持单人、多人音视频通话(基于原生webrtc实现,需要ssl证书)
1. uniapp端兼容app、h5、微信小程序,可与web端同时在线，并保持消息同步
1. 后端采用springboot+netty实现，网页端使用vue，移动端使用uniapp
1. 服务器支持集群化部署，每个im-server仅处理自身连接用户的消息

详细文档：https://www.yuque.com/u1475064/mufu2a


#### 在线体验

账号：张三/Aa123123 李四/Aa123123,也可以在网页端自行注册账号

网页端：https://www.boxim.online

桌面端：https://www.boxim.online/download/boxim.exe

移动安卓端：https://www.boxim.online/download/boxim.apk

移动H5端: https://www.boxim.online/h5/ ,或扫码：

![输入图片说明](%E6%88%AA%E5%9B%BE/h5%E4%BA%8C%E7%BB%B4%E7%A0%81.png)

由于微信小程序每次发布审核过于严苛和繁琐，暂时不再提供体验环境，但uniapp端依然会继续兼容小程序



#### 项目结构
| 模块                     | 功能                               |
|------------------------|----------------------------------|
| im-platform            | 业务平台服务，负责处理来自用户的业务请求(http)       |
| im-server              | 消息推送服务，不依赖业务,负责将消息推送给用户(ws)      |
| im-client              | 消息推送sdk, 其他服务可集成此sdk与im-server通信 |
| im-common              | 公共包,后端服务均依赖此包                    |
| im-web                 | web页面和桌面端                        |
| im-uniapp              | uniapp页面,可打包成app、h5、微信小程序        |
| im-webview-private-rtc | uniapp单人音视频webview组件源码           |
| im-webview-group-rtc   | uniapp多人音视频webview组件源码           |

更多细节可以参考文档和每个目录里的readme.md(如有)

#### 本地启动
1.安装运行环境
- 安装node:v18.19.0
- 安装jdk:17
- 安装maven:3.9.6
- 安装mysql:8.0,账号密码分别为root/root,创建名为im_platform的数据库，运行db/im_platfrom.sql脚本
- 安装redis:6.2
- 安装minio:RELEASE.2024-xx,使用默认账号、密码、端口

2.启动后端服务
```
mvn clean package
java -jar ./im-platform/target/im-platform.jar
java -jar ./im-server/target/im-server.jar
```

3.启动前端web
```
cd im-web
npm install
npm run serve
```
访问 http://localhost:8080

注:
为了支持打包桌面端，在3.7版本开始引入了electron, 但是实践中发现，下载electron依赖经常会失败,可以尝试以下方法:
1. 多尝试npm install几次
2. 使用淘宝仓库镜像源
3. vpn翻墙
4. 如果不需要桌面端，可以把package.json里面的electron相关的依赖移除


4.启动electron客户端
```
cd im-web
npm install
npm run electron:serve
```

5.启动uniapp-h5
```
cd im-uniapp
npm install
```
然后将im-uniapp目录导入HBuilderX,点击菜单"运行"->"开发环境-h5"
访问 http://localhost:5173


