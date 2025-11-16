#!/bin/bash

# 宿舍管理系统 EdgeOne 部署脚本
# Linux/macOS 版本

set -e  # 遇到错误时退出

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 变量设置
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
FRONTEND_DIR="$PROJECT_ROOT/frontend"
BACKEND_DIR="$PROJECT_ROOT/backend"
DEPLOYMENT_DIR="$PROJECT_ROOT/deployment"
BUILD_DIR="$PROJECT_ROOT/build"
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}宿舍管理系统 EdgeOne 部署脚本${NC}"
echo -e "${BLUE}========================================${NC}"
echo

echo "开始时间: $(date)"
echo "项目根目录: $PROJECT_ROOT"
echo

# 检查 Node.js
echo -e "${BLUE}检查 Node.js 环境...${NC}"
if ! command -v node &> /dev/null; then
    echo -e "${RED}错误: Node.js 未安装${NC}"
    exit 1
fi
node --version

# 检查 Maven
echo -e "${BLUE}检查 Maven 环境...${NC}"
if ! command -v mvn &> /dev/null; then
    echo -e "${YELLOW}警告: Maven 未安装，将跳过后端构建${NC}"
    MAVEN_AVAILABLE=false
else
    mvn --version
    MAVEN_AVAILABLE=true
fi

# 创建构建目录
echo
echo -e "${BLUE}创建构建目录...${NC}"
mkdir -p "$BUILD_DIR/$TIMESTAMP"

# 构建 Frontend
echo
echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}构建 Frontend${NC}"
echo -e "${GREEN}========================================${NC}"
cd "$FRONTEND_DIR"

echo "安装依赖..."
npm install
if [ $? -ne 0 ]; then
    echo -e "${RED}错误: 前端依赖安装失败${NC}"
    exit 1
fi

echo "构建生产版本..."
npm run build-only
if [ $? -ne 0 ]; then
    echo -e "${RED}错误: 前端构建失败${NC}"
    exit 1
fi

echo "复制前端构建文件到部署目录..."
cp -r dist/* "$BUILD_DIR/$TIMESTAMP/frontend/"
cp "$DEPLOYMENT_DIR/404.html" "$BUILD_DIR/$TIMESTAMP/frontend/"
cp "$DEPLOYMENT_DIR/500.html" "$BUILD_DIR/$TIMESTAMP/frontend/"

# 构建 Backend
echo
echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}构建 Backend${NC}"
echo -e "${GREEN}========================================${NC}"
cd "$BACKEND_DIR"

if [ "$MAVEN_AVAILABLE" = true ]; then
    echo "构建 Spring Boot JAR..."
    mvn clean package -DskipTests -Pprod
    if [ $? -ne 0 ]; then
        echo -e "${YELLOW}警告: 后端构建失败，继续前端部署${NC}"
    else
        echo "复制后端 JAR 文件..."
        mkdir -p "$BUILD_DIR/$TIMESTAMP/backend/"
        cp target/dormitory-management-1.0.0.jar "$BUILD_DIR/$TIMESTAMP/backend/"
    fi
else
    echo -e "${YELLOW}跳过后端构建 (Maven 未安装)${NC}"
fi

# 生成部署配置文件
echo
echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}生成部署配置${NC}"
echo -e "${GREEN}========================================${NC}"
cd "$PROJECT_ROOT"

cat > "$BUILD_DIR/$TIMESTAMP/deploy-manifest.txt" << EOF
部署时间: $(date)
构建版本: $TIMESTAMP

前端文件:
$(ls -la "$BUILD_DIR/$TIMESTAMP/frontend/")

$(if [ -d "$BUILD_DIR/$TIMESTAMP/backend" ]; then
    echo "后端文件:"
    ls -la "$BUILD_DIR/$TIMESTAMP/backend/"
    echo
fi)

EdgeOne 部署步骤:
1. 登录腾讯云控制台
2. 进入 EdgeOne 服务
3. 创建静态网站
4. 上传 frontend 目录下的所有文件
5. 配置域名解析
6. 配置 HTTPS 和缓存策略
7. 配置 API 代理 (如果需要)

环境变量配置:
- DB_URL: 数据库连接地址
- DB_USERNAME: 数据库用户名
- DB_PASSWORD: 数据库密码
- REDIS_HOST: Redis 服务器地址
- REDIS_PASSWORD: Redis 密码
- JWT_SECRET: JWT 密钥
EOF

# 生成部署压缩包
echo
echo -e "${BLUE}打包部署文件...${NC}"
cd "$BUILD_DIR"
tar -czf "$TIMESTAMP.tar.gz" "$TIMESTAMP/"

echo
echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}部署完成${NC}"
echo -e "${GREEN}========================================${NC}"
echo
echo -e "构建文件位置: ${GREEN}$BUILD_DIR/$TIMESTAMP${NC}"
echo -e "压缩包位置: ${GREEN}$BUILD_DIR/$TIMESTAMP.tar.gz${NC}"
echo -e "部署清单: ${GREEN}$BUILD_DIR/$TIMESTAMP/deploy-manifest.txt${NC}"
echo

echo -e "${YELLOW}下一步操作:${NC}"
echo "1. 检查构建文件是否正确生成"
echo "2. 上传到 EdgeOne 静态网站托管"
echo "3. 配置域名和 DNS"
echo "4. 配置生产环境变量"
echo "5. 测试部署结果"
echo

echo -e "${GREEN}部署脚本执行完毕！${NC}"