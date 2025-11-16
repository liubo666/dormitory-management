#!/bin/bash

# 宿舍管理系统后端快速部署脚本
# 适用于 CentOS 7/8 系统
# 作者: Dormitory Management Team
# 版本: 1.0.0

set -e

# 颜色输出
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# 配置变量
PROJECT_NAME="dormitory-management"
JAR_FILE="$1"
INSTALL_DIR="/opt/apps/${PROJECT_NAME}"

# 显示帮助信息
show_help() {
    echo "宿舍管理系统后端快速部署脚本"
    echo
    echo "用法: $0 <JAR文件路径>"
    echo
    echo "示例: $0 /tmp/dormitory-management-1.0.0.jar"
    echo
    echo "此脚本将执行以下操作："
    echo "1. 检查系统环境"
    echo "2. 安装必要的依赖"
    echo "3. 配置数据库"
    echo "4. 部署应用"
    echo "5. 启动服务"
    echo
    exit 0
}

# 检查参数
check_args() {
    if [[ -z "$JAR_FILE" ]]; then
        log_error "请提供 JAR 文件路径"
        show_help
    fi

    if [[ ! -f "$JAR_FILE" ]]; then
        log_error "JAR 文件不存在: $JAR_FILE"
        exit 1
    fi

    if [[ "$JAR_FILE" != *.jar ]]; then
        log_error "文件扩展名必须是 .jar"
        exit 1
    fi
}

# 检查 root 权限
check_root() {
    if [[ $EUID -ne 0 ]]; then
        log_error "此脚本需要 root 权限运行"
        exit 1
    fi
}

# 检查系统
check_system() {
    if [[ ! -f /etc/centos-release && ! -f /etc/redhat-release ]]; then
        log_error "此脚本仅支持 CentOS/RHEL 系统"
        exit 1
    fi

    local version=$(cat /etc/centos-release 2>/dev/null || cat /etc/redhat-release 2>/dev/null)
    log_info "检测到系统: $version"
}

# 快速安装依赖
quick_install() {
    log_info "快速安装依赖..."

    # 更新系统
    yum update -y -q

    # 安装基础工具
    yum install -y -q wget curl vim

    # 检查 Java
    if ! command -v java &> /dev/null; then
        log_info "安装 Java 17..."
        yum install -y -q java-17-openjdk java-17-openjdk-devel

        # 设置环境变量
        cat > /etc/profile.d/java17.sh << 'EOF'
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk
export PATH=$JAVA_HOME/bin:$PATH
EOF
        source /etc/profile.d/java17.sh
    else
        local java_version=$(java -version 2>&1 | head -n 1)
        log_info "Java 已安装: $java_version"
    fi

    # 检查 MySQL
    if ! command -v mysql &> /dev/null; then
        log_info "安装 MySQL 8.0..."
        yum install -y -q https://dev.mysql.com/get/mysql80-community-release-el7-3.noarch.rpm
        yum install -y -q mysql-community-server

        systemctl start mysqld
        systemctl enable mysqld

        local temp_password=$(grep 'temporary password' /var/log/mysqld.log | tail -n 1 | awk '{print $NF}')
        log_info "MySQL 临时密码: $temp_password"
        log_warning "请记住此密码，稍后需要使用"
    else
        log_info "MySQL 已安装"
    fi

    # 检查 Redis
    if ! command -v redis-server &> /dev/null; then
        log_info "安装 Redis..."
        yum install -y -q redis
        systemctl start redis
        systemctl enable redis
    else
        log_info "Redis 已安装"
    fi

    log_success "依赖安装完成"
}

# 创建目录
create_directories() {
    log_info "创建应用目录..."

    mkdir -p "$INSTALL_DIR"/{logs,config,backup}
    chown -R root:root "$INSTALL_DIR"
    chmod -R 755 "$INSTALL_DIR"

    log_success "目录创建完成: $INSTALL_DIR"
}

# 快速配置数据库
quick_config_database() {
    log_info "配置数据库..."

    local mysql_password="$MYSQL_ROOT_PASSWORD"

    if [[ -z "$mysql_password" ]]; then
        echo -n "请输入 MySQL root 密码 (临时密码): "
        read -s mysql_password
        echo
    fi

    # 创建应用用户和数据库
    mysql --connect-expired-password -u root -p"$mysql_password" << 'EOF'
CREATE DATABASE IF NOT EXISTS dormitory_management CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER IF NOT EXISTS 'dormitory_user'@'localhost' IDENTIFIED BY 'dormitory123';
GRANT ALL PRIVILEGES ON dormitory_management.* TO 'dormitory_user'@'localhost';
FLUSH PRIVILEGES;
EOF

    log_success "数据库配置完成"
}

# 部署应用
deploy_app() {
    log_info "部署应用..."

    # 复制 JAR 文件
    cp "$JAR_FILE" "$INSTALL_DIR/dormitory-management.jar"
    chmod +x "$INSTALL_DIR/dormitory-management.jar"

    # 创建配置文件
    cat > "$INSTALL_DIR/config/application-prod.yml" << 'EOF'
server:
  port: 8080
  servlet:
    context-path: /api

spring:
  application:
    name: dormitory-management
  profiles:
    active: prod

  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/dormitory_management?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: dormitory_user
    password: dormitory123
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5

  data:
    redis:
      host: localhost
      port: 6379
      database: 0
      lettuce:
        pool:
          max-active: 20
          max-idle: 10

jwt:
  secret: your_production_jwt_secret_key_make_it_long_and_secure_at_least_256_bits_please_change_this_in_production
  expiration: 28800

logging:
  level:
    com.dormitory: INFO
  file:
    name: /opt/apps/dormitory-management/logs/application.log
    max-size: 100MB
    max-history: 30

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
EOF

    log_success "应用部署完成"
}

# 创建服务
create_service() {
    log_info "创建系统服务..."

    cat > "/etc/systemd/system/dormitory-backend.service" << EOF
[Unit]
Description=Dormitory Management Backend Service
After=network.target mysql.service redis.service

[Service]
Type=simple
User=root
WorkingDirectory=${INSTALL_DIR}
ExecStart=/usr/bin/java -jar ${INSTALL_DIR}/dormitory-management.jar --spring.profiles.active=prod
Restart=always
RestartSec=10
StandardOutput=journal
StandardError=journal

[Install]
WantedBy=multi-user.target
EOF

    systemctl daemon-reload
    systemctl enable dormitory-backend

    log_success "服务创建完成"
}

# 配置防火墙
setup_firewall() {
    log_info "配置防火墙..."

    if systemctl is-active --quiet firewalld; then
        firewall-cmd --permanent --add-port=8080/tcp
        firewall-cmd --reload
        log_success "防火墙配置完成，已开放 8080 端口"
    else
        log_warning "防火墙未运行，请手动配置"
    fi
}

# 启动服务
start_service() {
    log_info "启动应用服务..."

    systemctl start dormitory-backend

    # 等待服务启动
    sleep 10

    if systemctl is-active --quiet dormitory-backend; then
        log_success "服务启动成功"

        # 健康检查
        if curl -sf http://localhost:8080/api/actuator/health > /dev/null 2>&1; then
            log_success "健康检查通过"
        else
            log_warning "健康检查失败，请查看日志"
        fi
    else
        log_error "服务启动失败"
        systemctl status dormitory-backend --no-pager
        journalctl -u dormitory-backend -n 20 --no-pager
        exit 1
    fi
}

# 显示完成信息
show_completion_info() {
    log_success "========== 部署完成 =========="
    echo
    echo "应用信息:"
    echo "  应用名称: dormitory-management"
    echo "  部署目录: $INSTALL_DIR"
    echo "  配置文件: $INSTALL_DIR/config/application-prod.yml"
    echo "  日志目录: $INSTALL_DIR/logs"
    echo "  数据库: dormitory_management"
    echo "  数据库用户: dormitory_user / dormitory123"
    echo
    echo "服务管理:"
    echo "  启动: systemctl start dormitory-backend"
    echo "  停止: systemctl stop dormitory-backend"
    echo "  重启: systemctl restart dormitory-backend"
    echo "  状态: systemctl status dormitory-backend"
    echo "  日志: journalctl -u dormitory-backend -f"
    echo
    echo "访问地址:"
    echo "  本地: http://localhost:8080/api"
    echo "  健康检查: http://localhost:8080/api/actuator/health"
    echo
    echo "重要提醒:"
    echo "  1. 请修改数据库密码"
    echo "  2. 请修改 JWT 密钥"
    echo "  3. 请配置防火墙规则"
    echo "  4. 请配置 SSL 证书"
    echo "  5. 请初始化数据库表结构"
    echo "=========================================="
}

# 主函数
main() {
    echo "========================================"
    echo "🚀 宿舍管理系统后端快速部署"
    echo "========================================"

    check_args
    check_root
    check_system
    quick_install
    create_directories
    quick_config_database
    deploy_app
    create_service
    setup_firewall
    start_service
    show_completion_info

    log_success "快速部署完成！"
}

# 脚本入口
if [[ "${BASH_SOURCE[0]}" == "${0}" ]]; then
    main "$@"
fi