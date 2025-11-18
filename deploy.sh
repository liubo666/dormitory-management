#!/bin/bash

# 宿舍管理系统 Docker 部署脚本
# 部署路径: /home/liubo/prod/dormitory-management

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 日志函数
log_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

log_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# 检查是否为root用户
check_root() {
    if [[ $EUID -eq 0 ]]; then
        log_error "请不要使用root用户运行此脚本！"
        exit 1
    fi
}

# 检查Docker是否安装
check_docker() {
    if ! command -v docker &> /dev/null; then
        log_error "Docker未安装，请先安装Docker"
        exit 1
    fi

    if ! command -v docker-compose &> /dev/null; then
        log_error "Docker Compose未安装，请先安装Docker Compose"
        exit 1
    fi

    log_info "Docker环境检查通过"
}

# 创建部署目录结构
create_directories() {
    log_info "创建部署目录结构..."

    BASE_DIR="/home/liubo/prod/dormitory-management"

    # 创建主要目录
    mkdir -p $BASE_DIR/{backend,frontend,mysql,logs,scripts}

    # 创建后端日志目录
    mkdir -p $BASE_DIR/backend/logs

    # 创建前端日志目录
    mkdir -p $BASE_DIR/frontend/logs

    # 创建MySQL初始化脚本目录
    mkdir -p $BASE_DIR/mysql/init

    # 创建备份目录
    mkdir -p $BASE_DIR/backup/{mysql,logs}

    log_info "目录结构创建完成"
}

# 复制文件到部署目录
copy_files() {
    log_info "复制项目文件到部署目录..."

    BASE_DIR="/home/liubo/prod/dormitory-management"
    CURRENT_DIR=$(pwd)

    # 复制后端文件
    cp -r $CURRENT_DIR/backend/* $BASE_DIR/backend/

    # 复制前端文件
    cp -r $CURRENT_DIR/frontend/* $BASE_DIR/frontend/

    # 复制docker-compose文件
    cp $CURRENT_DIR/docker-compose.yml $BASE_DIR/

    # 复制部署脚本
    cp $CURRENT_DIR/deploy.sh $BASE_DIR/scripts/

    # 设置脚本执行权限
    chmod +x $BASE_DIR/scripts/*.sh

    log_info "文件复制完成"
}

# 创建MySQL初始化脚本
create_mysql_init() {
    log_info "创建MySQL初始化脚本..."

    BASE_DIR="/home/liubo/prod/dormitory-management"

    cat > $BASE_DIR/mysql/init/01-database.sql << 'EOF'
-- 创建数据库和用户
CREATE DATABASE IF NOT EXISTS dormitory_management CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 创建用户并授权
CREATE USER IF NOT EXISTS 'dormitory'@'%' IDENTIFIED BY 'Dormitory123!';
GRANT ALL PRIVILEGES ON dormitory_management.* TO 'dormitory'@'%';
FLUSH PRIVILEGES;
EOF

    log_info "MySQL初始化脚本创建完成"
}

# 创建环境变量文件
create_env_file() {
    log_info "创建环境变量文件..."

    BASE_DIR="/home/liubo/prod/dormitory-management"

    cat > $BASE_DIR/.env << 'EOF'
# 数据库配置
MYSQL_ROOT_PASSWORD=Root123!
MYSQL_DATABASE=dormitory_management
MYSQL_USER=dormitory
MYSQL_PASSWORD=Dormitory123!

# Spring Boot配置
SPRING_PROFILES_ACTIVE=docker

# JWT配置
JWT_SECRET=a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6q7r8s9t0u1v2w3x4y5z6a7b8c9d0e1f2g3h4
JWT_EXPIRATION=28800

# 端口配置
BACKEND_PORT=8080
FRONTEND_PORT=80
MYSQL_PORT=3306
REDIS_PORT=6379
EOF

    log_info "环境变量文件创建完成"
}

# 构建和启动容器
deploy_services() {
    log_info "开始构建和启动服务..."

    BASE_DIR="/home/liubo/prod/dormitory-management"
    cd $BASE_DIR

    # 停止现有容器
    docker-compose down

    # 构建镜像
    log_info "构建Docker镜像..."
    docker-compose build --no-cache

    # 启动服务
    log_info "启动服务..."
    docker-compose up -d

    log_info "服务部署完成"
}

# 检查服务状态
check_services() {
    log_info "检查服务状态..."

    BASE_DIR="/home/liubo/prod/dormitory-management"
    cd $BASE_DIR

    # 等待服务启动
    sleep 30

    # 检查容器状态
    docker-compose ps

    # 检查健康状态
    log_info "检查服务健康状态..."

    # 检查后端API
    if curl -f -s http://localhost:8080/api/actuator/health > /dev/null; then
        log_info "后端服务运行正常"
    else
        log_warn "后端服务可能未完全启动，请稍后检查"
    fi

    # 检查前端
    if curl -f -s http://localhost > /dev/null; then
        log_info "前端服务运行正常"
    else
        log_warn "前端服务可能未完全启动，请稍后检查"
    fi
}

# 创建管理脚本
create_management_scripts() {
    log_info "创建管理脚本..."

    BASE_DIR="/home/liubo/prod/dormitory-management"

    # 启动脚本
    cat > $BASE_DIR/scripts/start.sh << 'EOF'
#!/bin/bash
cd /home/liubo/prod/dormitory-management
docker-compose up -d
echo "服务已启动"
docker-compose ps
EOF

    # 停止脚本
    cat > $BASE_DIR/scripts/stop.sh << 'EOF'
#!/bin/bash
cd /home/liubo/prod/dormitory-management
docker-compose down
echo "服务已停止"
EOF

    # 重启脚本
    cat > $BASE_DIR/scripts/restart.sh << 'EOF'
#!/bin/bash
cd /home/liubo/prod/dormitory-management
docker-compose restart
echo "服务已重启"
docker-compose ps
EOF

    # 查看日志脚本
    cat > $BASE_DIR/scripts/logs.sh << 'EOF'
#!/bin/bash
cd /home/liubo/prod/dormitory-management
if [ -z "$1" ]; then
    echo "用法: $0 [backend|frontend|mysql|redis]"
    exit 1
fi
docker-compose logs -f $1
EOF

    # 备份脚本
    cat > $BASE_DIR/scripts/backup.sh << 'EOF'
#!/bin/bash
BACKUP_DIR="/home/liubo/prod/dormitory-management/backup"
DATE=$(date +%Y%m%d_%H%M%S)

# 备份MySQL数据
echo "备份MySQL数据..."
docker exec dormitory-mysql mysqldump -u root -pRoot123! dormitory_management > $BACKUP_DIR/mysql/backup_$DATE.sql

# 备份日志文件
echo "备份日志文件..."
tar -czf $BACKUP_DIR/logs/logs_$DATE.tar.gz /home/liubo/prod/dormitory-management/backend/logs /home/liubo/prod/dormitory-management/frontend/logs

echo "备份完成: $DATE"
EOF

    # 设置执行权限
    chmod +x $BASE_DIR/scripts/*.sh

    log_info "管理脚本创建完成"
}

# 主函数
main() {
    log_info "开始部署宿舍管理系统..."

    check_root
    check_docker
    create_directories
    copy_files
    create_mysql_init
    create_env_file
    create_management_scripts
    deploy_services
    check_services

    log_info "部署完成！"
    log_info "访问地址: http://localhost"
    log_info "API文档: http://localhost:8080/api/doc.html"
    log_info "管理命令: cd /home/liubo/prod/dormitory-management/scripts && ./start.sh|stop.sh|restart.sh|logs.sh"
}

# 执行主函数
main "$@"