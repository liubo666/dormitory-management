#!/bin/bash

# 宿舍管理系统后端 CentOS 部署脚本
# 作者: Dormitory Management Team
# 版本: 1.0.0
# 更新时间: 2025-11-16

set -e  # 遇到错误立即退出

# 配置变量
PROJECT_NAME="dormitory-management"
APP_NAME="dormitory-backend"
VERSION="1.0.0"
BASE_DIR="/opt/apps"
DEPLOY_DIR="${BASE_DIR}/${PROJECT_NAME}"
JAR_NAME="${PROJECT_NAME}-${VERSION}.jar"
SERVICE_NAME="${APP_NAME}"
LOG_DIR="${DEPLOY_DIR}/logs"
CONFIG_DIR="${DEPLOY_DIR}/config"
BACKUP_DIR="${DEPLOY_DIR}/backup"

# 颜色输出
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 日志函数
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

# 检查是否为 root 用户
check_root() {
    if [[ $EUID -ne 0 ]]; then
        log_error "此脚本需要 root 权限运行"
        exit 1
    fi
}

# 检查 CentOS 版本
check_centos_version() {
    if [[ ! -f /etc/centos-release && ! -f /etc/redhat-release ]]; then
        log_error "此脚本仅支持 CentOS/RHEL 系统"
        exit 1
    fi

    local version=$(cat /etc/centos-release 2>/dev/null || cat /etc/redhat-release 2>/dev/null)
    log_info "检测到系统版本: $version"
}

# 安装系统依赖
install_dependencies() {
    log_info "开始安装系统依赖..."

    # 更新系统
    yum update -y

    # 安装基础工具
    yum install -y wget curl vim unzip

    # 安装 Java 17
    if ! command -v java &> /dev/null; then
        log_info "安装 Java 17..."
        yum install -y java-17-openjdk java-17-openjdk-devel

        # 设置 JAVA_HOME
        echo "export JAVA_HOME=/usr/lib/jvm/java-17-openjdk" >> /etc/profile
        echo "export PATH=\$JAVA_HOME/bin:\$PATH" >> /etc/profile
        source /etc/profile

        log_success "Java 17 安装完成"
    else
        log_info "Java 已安装: $(java -version 2>&1 | head -n 1)"
    fi

    # 安装 Maven (如果需要本地编译)
    if ! command -v mvn &> /dev/null; then
        log_info "安装 Maven..."
        yum install -y maven
        log_success "Maven 安装完成"
    fi
}

# 创建应用目录
create_directories() {
    log_info "创建应用目录结构..."

    mkdir -p "${DEPLOY_DIR}"
    mkdir -p "${LOG_DIR}"
    mkdir -p "${CONFIG_DIR}"
    mkdir -p "${BACKUP_DIR}"

    # 设置权限
    chown -R root:root "${DEPLOY_DIR}"
    chmod -R 755 "${DEPLOY_DIR}"

    log_success "目录创建完成: ${DEPLOY_DIR}"
}

# 部署 JAR 文件
deploy_jar() {
    local jar_source="$1"

    if [[ ! -f "$jar_source" ]]; then
        log_error "JAR 文件不存在: $jar_source"
        exit 1
    fi

    log_info "部署 JAR 文件..."

    # 备份现有 JAR (如果存在)
    if [[ -f "${DEPLOY_DIR}/${JAR_NAME}" ]]; then
        log_info "备份现有 JAR 文件..."
        cp "${DEPLOY_DIR}/${JAR_NAME}" "${BACKUP_DIR}/${JAR_NAME}.$(date +%Y%m%d_%H%M%S).bak"
    fi

    # 复制新的 JAR 文件
    cp "$jar_source" "${DEPLOY_DIR}/${JAR_NAME}"
    chmod +x "${DEPLOY_DIR}/${JAR_NAME}"

    log_success "JAR 文件部署完成"
}

# 配置应用配置文件
setup_config() {
    log_info "配置应用配置文件..."

    # 生产环境配置
    cat > "${CONFIG_DIR}/application-prod.yml" << 'EOF'
server:
  port: 8080
  servlet:
    context-path: /api

spring:
  application:
    name: dormitory-management
  profiles:
    active: prod

  # 数据源配置
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://${DB_HOST:localhost}:${DB_PORT:3306}/${DB_NAME:dormitory_management}?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: ${DB_USERNAME:root}
    password: ${DB_PASSWORD:your_secure_password}
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      idle-timeout: 300000
      connection-timeout: 20000
      max-lifetime: 1200000

  # Redis配置
  data:
    redis:
      host: ${REDIS_HOST:localhost}
      port: ${REDIS_PORT:6379}
      password: ${REDIS_PASSWORD:}
      database: 0
      timeout: 10000ms
      lettuce:
        pool:
          max-active: 20
          max-wait: -1ms
          max-idle: 10
          min-idle: 5

  # Jackson配置
  jackson:
    time-zone: GMT+8
    date-format: yyyy-MM-dd HH:mm:ss

# MyBatis-Plus配置
mybatis-plus:
  configuration:
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
    map-underscore-to-camel-case: true
  global-config:
    db-config:
      logic-delete-field: deleted
      logic-delete-value: 1
      logic-not-delete-value: 0

# JWT配置
jwt:
  secret: ${JWT_SECRET:your_production_jwt_secret_key_here_make_it_long_and_secure}
  expiration: ${JWT_EXPIRATION:28800}

# 日志配置
logging:
  level:
    com.dormitory: INFO
    org.springframework.security: DEBUG
  pattern:
    file: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
    console: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
  file:
    name: ${LOG_FILE:/opt/apps/dormitory-management/logs/application.log}
    max-size: 100MB
    max-history: 30

# 管理端点配置
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
  endpoint:
    health:
      show-details: when-authorized
EOF

    # 环境变量配置文件
    cat > "${CONFIG_DIR}/.env" << 'EOF'
# 数据库配置
DB_HOST=localhost
DB_PORT=3306
DB_NAME=dormitory_management
DB_USERNAME=dormitory_user
DB_PASSWORD=your_secure_db_password

# Redis配置
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=your_redis_password

# JWT配置
JWT_SECRET=your_production_jwt_secret_key_here_make_it_long_and_secure_at_least_256_bits
JWT_EXPIRATION=28800

# 应用配置
SERVER_PORT=8080
LOG_FILE=/opt/apps/dormitory-management/logs/application.log

# 外部访问配置
CORS_ALLOWED_ORIGINS=https://yourdomain.com,https://www.yourdomain.com
EOF

    # 设置配置文件权限
    chmod 600 "${CONFIG_DIR}/.env"
    chown root:root "${CONFIG_DIR}"/*

    log_success "配置文件设置完成"
}

# 创建 systemd 服务
create_service() {
    log_info "创建 systemd 服务..."

    cat > "/etc/systemd/system/${SERVICE_NAME}.service" << EOF
[Unit]
Description=Dormitory Management Backend Service
After=network.target mysql.service redis.service

[Service]
Type=simple
User=root
Group=root
WorkingDirectory=${DEPLOY_DIR}
EnvironmentFile=${CONFIG_DIR}/.env
ExecStart=/usr/bin/java -jar ${DEPLOY_DIR}/${JAR_NAME} --spring.profiles.active=prod
ExecStop=/bin/kill -15 \$MAINPID
ExecReload=/bin/kill -HUP \$MAINPID
Restart=always
RestartSec=10
StandardOutput=journal
StandardError=journal
SyslogIdentifier=${APP_NAME}

# JVM 参数
Environment=JAVA_OPTS=-Xms512m -Xmx1024m -XX:+UseG1GC -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0

[Install]
WantedBy=multi-user.target
EOF

    # 重新加载 systemd
    systemctl daemon-reload

    log_success "systemd 服务创建完成"
}

# 配置防火墙
setup_firewall() {
    log_info "配置防火墙..."

    # 检查防火墙状态
    if systemctl is-active --quiet firewalld; then
        # 开放 8080 端口
        firewall-cmd --permanent --add-port=8080/tcp
        firewall-cmd --reload
        log_success "防火墙配置完成，已开放 8080 端口"
    else
        log_warning "防火墙服务未运行，请手动配置端口访问"
    fi
}

# 配置 Nginx (可选)
setup_nginx() {
    log_info "配置 Nginx 反向代理..."

    cat > "/etc/nginx/conf.d/dormitory-backend.conf" << 'EOF'
upstream dormitory_backend {
    server 127.0.0.1:8080;
}

server {
    listen 80;
    server_name your-api-domain.com;  # 替换为实际的API域名

    # 重定向到 HTTPS
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name your-api-domain.com;  # 替换为实际的API域名

    # SSL 证书配置 (需要提供实际的证书路径)
    ssl_certificate /etc/ssl/certs/your-api-domain.com.crt;
    ssl_certificate_key /etc/ssl/private/your-api-domain.com.key;

    # SSL 安全配置
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers ECDHE-RSA-AES128-GCM-SHA256:ECDHE-RSA-AES256-GCM-SHA384:ECDHE-RSA-AES128-SHA256:ECDHE-RSA-AES256-SHA384;
    ssl_prefer_server_ciphers off;
    ssl_session_cache shared:SSL:10m;
    ssl_session_timeout 10m;

    # 安全头
    add_header X-Frame-Options DENY;
    add_header X-Content-Type-Options nosniff;
    add_header X-XSS-Protection "1; mode=block";
    add_header Strict-Transport-Security "max-age=31536000; includeSubDomains" always;

    # 反向代理配置
    location /api {
        proxy_pass http://dormitory_backend;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_set_header X-Forwarded-Host $server_name;

        # 超时配置
        proxy_connect_timeout 30s;
        proxy_send_timeout 30s;
        proxy_read_timeout 30s;

        # 缓冲配置
        proxy_buffering on;
        proxy_buffer_size 4k;
        proxy_buffers 8 4k;
    }

    # 健康检查端点
    location /health {
        proxy_pass http://dormitory_backend/actuator/health;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    # 访问日志
    access_log /var/log/nginx/dormitory-backend-access.log;
    error_log /var/log/nginx/dormitory-backend-error.log;
}
EOF

    # 测试 Nginx 配置
    nginx -t

    if [[ $? -eq 0 ]]; then
        log_success "Nginx 配置文件创建成功"
        log_warning "请根据实际情况修改域名和 SSL 证书路径"
    else
        log_error "Nginx 配置文件有误，请检查"
    fi
}

# 启动服务
start_service() {
    log_info "启动应用服务..."

    # 启用并启动服务
    systemctl enable ${SERVICE_NAME}
    systemctl start ${SERVICE_NAME}

    # 检查服务状态
    sleep 3
    if systemctl is-active --quiet ${SERVICE_NAME}; then
        log_success "服务启动成功"
        systemctl status ${SERVICE_NAME} --no-pager
    else
        log_error "服务启动失败"
        systemctl status ${SERVICE_NAME} --no-pager
        journalctl -u ${SERVICE_NAME} -n 20 --no-pager
        exit 1
    fi
}

# 健康检查
health_check() {
    log_info "执行健康检查..."

    # 等待服务完全启动
    sleep 10

    # 检查本地端口
    if netstat -tlnp | grep -q ":8080 "; then
        log_success "端口 8080 监听正常"
    else
        log_error "端口 8080 未监听"
        return 1
    fi

    # 检查 HTTP 响应
    local response=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/api/actuator/health || echo "000")

    if [[ "$response" == "200" ]]; then
        log_success "健康检查通过 (HTTP $response)"
    else
        log_warning "健康检查失败 (HTTP $response)"
        log_info "请检查应用日志: journalctl -u ${SERVICE_NAME} -f"
    fi
}

# 显示部署信息
show_deployment_info() {
    log_success "========== 部署完成 =========="
    echo
    echo "应用信息:"
    echo "  应用名称: ${APP_NAME}"
    echo "  版本: ${VERSION}"
    echo "  部署目录: ${DEPLOY_DIR}"
    echo "  配置目录: ${CONFIG_DIR}"
    echo "  日志目录: ${LOG_DIR}"
    echo "  JAR 文件: ${DEPLOY_DIR}/${JAR_NAME}"
    echo
    echo "服务管理:"
    echo "  启动服务: systemctl start ${SERVICE_NAME}"
    echo "  停止服务: systemctl stop ${SERVICE_NAME}"
    echo "  重启服务: systemctl restart ${SERVICE_NAME}"
    echo "  查看状态: systemctl status ${SERVICE_NAME}"
    echo "  查看日志: journalctl -u ${SERVICE_NAME} -f"
    echo
    echo "应用访问:"
    echo "  本地访问: http://localhost:8080/api"
    echo "  健康检查: http://localhost:8080/api/actuator/health"
    echo
    echo "配置文件:"
    echo "  主配置: ${CONFIG_DIR}/application-prod.yml"
    echo "  环境变量: ${CONFIG_DIR}/.env"
    echo
    echo "重要提醒:"
    echo "  1. 请修改 ${CONFIG_DIR}/.env 中的数据库和 Redis 连接信息"
    echo "  2. 请确保数据库和 Redis 服务已启动"
    echo "  3. 请根据需要配置防火墙和 Nginx"
    echo "  4. 建议配置 SSL 证书启用 HTTPS"
    echo "=========================================="
}

# 主函数
main() {
    local jar_source="$1"

    echo "========================================"
    echo "🚀 宿舍管理系统后端 CentOS 部署脚本"
    echo "========================================"

    # 检查参数
    if [[ -z "$jar_source" ]]; then
        log_error "请提供 JAR 文件路径"
        echo "用法: $0 <jar文件路径>"
        exit 1
    fi

    # 执行部署步骤
    log_info "开始部署..."

    check_root
    check_centos_version
    install_dependencies
    create_directories
    deploy_jar "$jar_source"
    setup_config
    create_service
    setup_firewall
    setup_nginx
    start_service
    health_check
    show_deployment_info

    log_success "部署完成！"
}

# 脚本入口
if [[ "${BASH_SOURCE[0]}" == "${0}" ]]; then
    main "$@"
fi