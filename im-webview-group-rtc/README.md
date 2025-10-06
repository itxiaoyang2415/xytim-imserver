### 项目说明
1.本组件本质上是一个基于vue2开发的H5页面,实现盒子IM的实时音视频功能

2.本组件最终将被打包成webview组件，并嵌入到im-uniapp中,不可单独运行

3.本组件应该与im-uniapp保持同级目录。否则请修改vue.config.js的outputDir变量

### 项目初始化
安装node18,然后执行以下指令进行初始化:
```
npm install
```
如果node版本不一致，可能会导致初始化失败，此时可尝试加上 --force 参数

### 打包组件
执行以下指令打包，组件将编译至im-uniapp的hybird/html目录
```
npm run build
```

### 启动
打包成功后，正常启动im-uniapp即可
