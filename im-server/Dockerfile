FROM openjdk:17-slim

# 设置工作目录
WORKDIR /app

# 复制jar文件
COPY target/im-server.jar /app/app.jar

# 设置时区
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 暴露端口
EXPOSE 8878 8879

# 启动应用
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=docker", "/app/app.jar"]

