#!/bin/bash

# 宿舍管理系统快速部署脚本
# 适用于在CentOS服务器上快速部署

set -e

echo "=========================================="
echo "宿舍管理系统 Docker 快速部署"
echo "=========================================="

# 检查Docker是否安装
if ! command -v docker &> /dev/null; then
    echo "正在安装Docker..."
    # 卸载旧版本
    sudo yum remove -y docker docker-client docker-client-latest docker-common docker-latest docker-latest-logrotate docker-logrotate docker-engine 2>/dev/null || true
    # 安装依赖
    sudo yum install -y yum-utils device-mapper-persistent-data lvm2
    # 添加Docker仓库
    sudo yum-config-manager --add-repo https://download.docker.com/linux/centos/docker-ce.repo
    # 安装Docker
    sudo yum install -y docker-ce docker-ce-cli containerd.io docker-compose-plugin
    # 启动Docker
    sudo systemctl start docker
    sudo systemctl enable docker
    # 添加用户到docker组
    sudo usermod -aG docker $USER
    echo "Docker安装完成，请重新登录后再次运行此脚本"
    exit 0
fi

if ! command -v docker-compose &> /dev/null; then
    echo "正在安装Docker Compose..."
    sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
    sudo chmod +x /usr/local/bin/docker-compose
fi

# 创建部署目录
echo "创建部署目录..."
sudo mkdir -p /home/liubo/prod/dormitory-management
sudo chown -R $USER:$USER /home/liubo/prod/dormitory-management

# 复制文件到部署目录
echo "复制项目文件..."
CURRENT_DIR=$(pwd)
DEPLOY_DIR="/home/liubo/prod/dormitory-management"

# 创建子目录
mkdir -p $DEPLOY_DIR/{backend,frontend,scripts,mysql/init,backup/{mysql,logs}}

# 复制项目文件
cp -r $CURRENT_DIR/backend/* $DEPLOY_DIR/backend/
cp -r $CURRENT_DIR/frontend/* $DEPLOY_DIR/frontend/
cp $CURRENT_DIR/docker-compose.yml $DEPLOY_DIR/

# 创建环境变量文件
echo "创建配置文件..."
cat > $DEPLOY_DIR/.env << 'EOF'
MYSQL_ROOT_PASSWORD=Root123!
MYSQL_DATABASE=dormitory_management
MYSQL_USER=dormitory
MYSQL_PASSWORD=Dormitory123!
SPRING_PROFILES_ACTIVE=docker
JWT_SECRET=a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6q7r8s9t0u1v2w3x4y5z6a7b8c9d0e1f2g3h4
JWT_EXPIRATION=28800
EOF

# 创建MySQL初始化脚本
cat > $DEPLOY_DIR/mysql/init/01-database.sql << 'EOF'
CREATE DATABASE IF NOT EXISTS dormitory_management CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER IF NOT EXISTS 'dormitory'@'%' IDENTIFIED BY 'Dormitory123!';
GRANT ALL PRIVILEGES ON dormitory_management.* TO 'dormitory'@'%';
FLUSH PRIVILEGES;
EOF

# 创建管理脚本
echo "创建管理脚本..."
cat > $DEPLOY_DIR/scripts/start.sh << 'EOF'
#!/bin/bash
cd /home/liubo/prod/dormitory-management
docker-compose up -d
echo "服务已启动"
docker-compose ps
EOF

cat > $DEPLOY_DIR/scripts/stop.sh << 'EOF'
#!/bin/bash
cd /home/liubo/prod/dormitory-management
docker-compose down
echo "服务已停止"
EOF

cat > $DEPLOY_DIR/scripts/restart.sh << 'EOF'
#!/bin/bash
cd /home/liubo/prod/dormitory-management
docker-compose restart
echo "服务已重启"
docker-compose ps
EOF

cat > $DEPLOY_DIR/scripts/logs.sh << 'EOF'
#!/bin/bash
cd /home/liubo/prod/dormitory-management
if [ -z "$1" ]; then
    echo "用法: $0 [backend|frontend|mysql|redis]"
    exit 1
fi
docker-compose logs -f $1
EOF

chmod +x $DEPLOY_DIR/scripts/*.sh

# 构建和启动服务
echo "开始部署应用..."
cd $DEPLOY_DIR

# 停止现有容器
docker-compose down 2>/dev/null || true

# 构建镜像
echo "构建Docker镜像..."
docker-compose build --no-cache

# 启动服务
echo "启动服务..."
docker-compose up -d

# 等待服务启动
echo "等待服务启动..."
sleep 30

# 检查服务状态
echo "检查服务状态..."
docker-compose ps

# 创建日志目录
mkdir -p $DEPLOY_DIR/backend/logs $DEPLOY_DIR/frontend/logs

echo ""
echo "=========================================="
echo "部署完成！"
echo "=========================================="
echo "访问地址:"
echo "  前端应用: http://localhost"
echo "  后端API:  http://localhost:8080/api/actuator/health"
echo "  API文档:  http://localhost:8080/api/doc.html"
echo ""
echo "管理命令:"
echo "  cd /home/liubo/prod/dormitory-management/scripts"
echo "  ./start.sh    # 启动服务"
echo "  ./stop.sh     # 停止服务"
echo "  ./restart.sh  # 重启服务"
echo "  ./logs.sh backend # 查看后端日志"
echo ""
echo "如需修改配置，请编辑: $DEPLOY_DIR/.env"
echo "=========================================="