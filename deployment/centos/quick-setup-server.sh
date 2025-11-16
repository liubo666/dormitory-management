#!/bin/bash

# 快速服务器环境搭建脚本
# 直接在空 CentOS 服务器上执行
# 使用方法: curl -sSL https://your-domain.com/quick-setup-server.sh | sudo bash

set -e

# 颜色输出
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

log_info() { echo -e "${BLUE}[INFO]${NC} $1"; }
log_success() { echo -e "${GREEN}[SUCCESS]${NC} $1"; }
log_error() { echo -e "${RED}[ERROR]${NC} $1"; }

echo "======================================"
echo "🚀 宿舍管理系统快速环境搭建"
echo "======================================"

# 检查 root 权限
if [[ $EUID -ne 0 ]]; then
    log_error "此脚本需要 root 权限运行"
    exit 1
fi

# 步骤1：更新系统
log_info "步骤 1/7: 更新系统..."
yum update -y -q
yum groupinstall -y "Development Tools" -q
yum install -y -q wget curl vim unzip htop net-tools

# 步骤2：安装 Java 17
log_info "步骤 2/7: 安装 Java 17..."
yum install -y -q java-17-openjdk java-17-openjdk-devel
echo "export JAVA_HOME=/usr/lib/jvm/java-17-openjdk" > /etc/profile.d/java17.sh
source /etc/profile.d/java17.sh

# 步骤3：安装 MySQL 8.0
log_info "步骤 3/7: 安装 MySQL 8.0..."
yum install -y -q https://dev.mysql.com/get/mysql80-community-release-el7-3.noarch.rpm
yum install -y -q mysql-community-server
systemctl start mysqld
systemctl enable mysqld

# 步骤4：安装 Redis
log_info "步骤 4/7: 安装 Redis..."
yum install -y -q redis
systemctl start redis
systemctl enable redis

# 步骤5：安装 Nginx
log_info "步骤 5/7: 安装 Nginx..."
yum install -y -q nginx
systemctl start nginx
systemctl enable nginx

# 步骤6：创建应用目录
log_info "步骤 6/7: 创建应用目录..."
mkdir -p /opt/apps/dormitory-management/{logs,config,backup,upload,scripts}
chown -R root:root /opt/apps
chmod -R 755 /opt/apps

# 步骤7：配置防火墙
log_info "步骤 7/7: 配置防火墙..."
if systemctl is-active --quiet firewalld; then
    firewall-cmd --permanent --add-service=http
    firewall-cmd --permanent --add-service=https
    firewall-cmd --permanent --add-port=8080/tcp
    firewall-cmd --reload
fi

# 创建数据库初始化脚本
cat > /tmp/init-database.sql << 'EOF'
CREATE DATABASE IF NOT EXISTS dormitory_management CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER IF NOT EXISTS 'dormitory_user'@'localhost' IDENTIFIED BY 'dormitory123';
GRANT ALL PRIVILEGES ON dormitory_management.* TO 'dormitory_user'@'localhost';
FLUSH PRIVILEGES;
EOF

# 创建 Nginx 配置
cat > /etc/nginx/conf.d/dormitory-backend.conf << 'EOF'
upstream dormitory_backend {
    server 127.0.0.1:8080;
}

server {
    listen 80;
    server_name _;

    location /api {
        proxy_pass http://dormitory_backend;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_connect_timeout 30s;
        proxy_send_timeout 30s;
        proxy_read_timeout 30s;
    }

    location /health {
        proxy_pass http://dormitory_backend/api/actuator/health;
        proxy_set_header Host $host;
    }
}
EOF

# 重载 Nginx 配置
nginx -s reload

# 获取 MySQL 临时密码
MYSQL_PASSWORD=$(grep 'temporary password' /var/log/mysqld.log | tail -n 1 | awk '{print $NF}')

echo "======================================"
echo "✅ 环境搭建完成！"
echo "======================================"
echo ""
echo "服务状态："
echo "  Java: $(java -version 2>&1 | head -n 1)"
echo "  MySQL: $(systemctl is-active mysqld)"
echo "  Redis: $(systemctl is-active redis)"
echo "  Nginx: $(systemctl is-active nginx)"
echo ""
echo "重要信息："
echo "  MySQL 临时密码: $MYSQL_PASSWORD"
echo "  应用目录: /opt/apps/dormitory-management"
echo "  数据库脚本: /tmp/init-database.sql"
echo ""
echo "下一步操作："
echo "1. 配置 MySQL:"
echo "   mysql -u root -p'$MYSQL_PASSWORD'"
echo "   ALTER USER 'root'@'localhost' IDENTIFIED BY 'YourNewPassword123!';"
echo "   source /tmp/init-database.sql"
echo ""
echo "2. 上传并部署应用 JAR 包到 /opt/apps/dormitory-management/"
echo ""
echo "3. 启动应用:"
echo "   cd /opt/apps/dormitory-management"
echo "   java -jar your-app.jar --spring.profiles.active=prod"
echo ""
echo "======================================"