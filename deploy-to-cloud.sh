#!/bin/bash

# 云端部署脚本
# 使用方法: ./deploy-to-cloud.sh YOUR_SERVER_IP

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 服务器IP
SERVER_IP=$1
if [ -z "$SERVER_IP" ]; then
    echo -e "${RED}错误: 请提供服务器IP地址${NC}"
    echo "使用方法: ./deploy-to-cloud.sh YOUR_SERVER_IP"
    exit 1
fi

echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}开始部署到云端服务器: ${SERVER_IP}${NC}"
echo -e "${GREEN}========================================${NC}"

# 步骤1: 打包项目
echo -e "${YELLOW}步骤1: 打包项目...${NC}"
mvn clean package -DskipTests
if [ $? -ne 0 ]; then
    echo -e "${RED}Maven打包失败！${NC}"
    exit 1
fi
echo -e "${GREEN}✓ 打包完成${NC}"

# 步骤2: 创建部署包
echo -e "${YELLOW}步骤2: 创建部署包...${NC}"
DEPLOY_DIR="box-im-deploy"
rm -rf $DEPLOY_DIR
mkdir -p $DEPLOY_DIR

# 复制必要文件
cp docker-compose-cloud.yml $DEPLOY_DIR/
cp -r db $DEPLOY_DIR/

# 复制im-platform
mkdir -p $DEPLOY_DIR/im-platform/target
cp im-platform/Dockerfile $DEPLOY_DIR/im-platform/
cp im-platform/target/im-platform.jar $DEPLOY_DIR/im-platform/target/

# 复制im-server
mkdir -p $DEPLOY_DIR/im-server/target
cp im-server/Dockerfile $DEPLOY_DIR/im-server/
cp im-server/target/im-server.jar $DEPLOY_DIR/im-server/target/

# 打包
tar -czf box-im-deploy.tar.gz -C $DEPLOY_DIR .
echo -e "${GREEN}✓ 部署包创建完成${NC}"

# 步骤3: 上传到服务器
echo -e "${YELLOW}步骤3: 上传到服务器 ${SERVER_IP}...${NC}"
ssh root@${SERVER_IP} "mkdir -p /opt/box-im"
scp box-im-deploy.tar.gz root@${SERVER_IP}:/opt/box-im/
echo -e "${GREEN}✓ 上传完成${NC}"

# 步骤4: 在服务器上解压并部署
echo -e "${YELLOW}步骤4: 在服务器上部署...${NC}"
ssh root@${SERVER_IP} << 'ENDSSH'
cd /opt/box-im
tar -xzf box-im-deploy.tar.gz

# 创建必要的目录
mkdir -p mysql-data redis-data minio-data logs/platform logs/server

# 停止旧容器
docker-compose -f docker-compose-cloud.yml down 2>/dev/null || true

# 启动新容器
docker-compose -f docker-compose-cloud.yml up -d --build

echo "等待服务启动..."
sleep 30

# 检查容器状态
docker-compose -f docker-compose-cloud.yml ps

echo "部署完成！"
ENDSSH

if [ $? -eq 0 ]; then
    echo -e "${GREEN}========================================${NC}"
    echo -e "${GREEN}部署成功！${NC}"
    echo -e "${GREEN}========================================${NC}"
    echo ""
    echo -e "访问地址:"
    echo -e "  - API文档: ${GREEN}http://${SERVER_IP}:8888/doc.html${NC}"
    echo -e "  - API服务: ${GREEN}http://${SERVER_IP}:8888${NC}"
    echo -e "  - WebSocket: ${GREEN}ws://${SERVER_IP}:8878${NC}"
    echo ""
    echo -e "数据库初始化:"
    echo -e "  ssh root@${SERVER_IP}"
    echo -e "  cd /opt/box-im"
    echo -e "  docker exec -i im-mysql mysql -uroot -pBoxIM@2024!Strong im_platform < db/im_platform.sql"
    echo -e "  docker exec -i im-mysql mysql -uroot -pBoxIM@2024!Strong im_platform < db/wallet_system.sql"
    echo -e "  docker exec -i im-mysql mysql -uroot -pBoxIM@2024!Strong im_platform < db/group_privacy_system.sql"
else
    echo -e "${RED}部署失败！${NC}"
    exit 1
fi

# 清理临时文件
rm -rf $DEPLOY_DIR box-im-deploy.tar.gz
