#!/bin/bash

# 宿舍管理系统完整环境搭建脚本
# 从零开始搭建完整的生产环境架构
# 作者: Dormitory Management Team
# 版本: 1.0.0

set -e

# 颜色输出
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
CYAN='\033[0;36m'
NC='\033[0m'

print_header() {
    echo -e "${CYAN}$1${NC}"
}

print_success() {
    echo -e "${GREEN}[✓ 成功]${NC} $1"
}

print_error() {
    echo -e "${RED}[✗ 失败]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[⚠ 警告]${NC} $1"
}

print_info() {
    echo -e "${BLUE}[ℹ 信息]${NC} $1"
}

print_step() {
    echo -e "${PURPLE}[$(date '+%H:%M:%S')] $1${NC}"
}

# 显示环境搭建进度
show_progress() {
    local current=$1
    local total=$2
    local desc=$3
    local percent=$((current * 100 / total))
    local filled=$((percent / 2))
    local empty=$((50 - filled))

    printf "\r${CYAN}[进度: ${percent}%%]${NC} ["
    printf "${GREEN}%*s${NC}" $filled | tr ' ' '█'
    printf "%*s" $empty | tr ' ' '░'
    printf "] ${desc}"
}

# 检查系统环境
check_system_environment() {
    print_step "检查系统环境..."

    # 检查是否为 root
    if [[ $EUID -ne 0 ]]; then
        print_error "此脚本需要 root 权限运行"
        exit 1
    fi

    # 检查系统版本
    if [[ -f /etc/centos-release ]]; then
        local version=$(cat /etc/centos-release)
        print_success "系统版本: $version"
    elif [[ -f /etc/redhat-release ]]; then
        local version=$(cat /etc/redhat-release)
        print_success "系统版本: $version"
    else
        print_error "不支持的操作系统，仅支持 CentOS/RHEL"
        exit 1
    fi

    # 检查硬件资源
    print_info "硬件资源检查:"
    print_info "  CPU: $(nproc) 核心"
    print_info "  内存: $(free -h | awk '/^Mem:/{print $2}')"
    print_info "  磁盘: $(df -h / | tail -1 | awk '{print $2}') 可用"

    sleep 2
}

# 更新系统
update_system() {
    print_step "更新系统软件包..."

    print_info "更新系统..."
    yum update -y -q

    print_info "安装基础工具..."
    yum groupinstall -y "Development Tools" -q
    yum install -y -q wget curl vim unzip git htop tree net-tools telnet lsof

    print_success "系统更新完成"
}

# 安装 Java 17
install_java() {
    print_step "安装 Java 17..."

    if command -v java &> /dev/null; then
        local java_version=$(java -version 2>&1 | head -n 1 | grep -oP 'version "?(1\.)?\K\d+' || echo "unknown")
        if [[ "$java_version" == "17" ]]; then
            print_success "Java 17 已安装"
            return
        else
            print_warning "检测到 Java $java_version，将安装 Java 17"
        fi
    fi

    print_info "安装 OpenJDK 17..."
    yum install -y -q java-17-openjdk java-17-openjdk-devel

    # 设置环境变量
    cat > /etc/profile.d/java17.sh << 'EOF'
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk
export JRE_HOME=$JAVA_HOME/jre
export CLASSPATH=$JAVA_HOME/lib:$JRE_HOME/lib
export PATH=$JAVA_HOME/bin:$PATH
EOF

    source /etc/profile.d/java17.sh

    # 验证安装
    local version=$(java -version 2>&1 | head -n 1)
    print_success "Java 17 安装完成: $version"
}

# 安装 MySQL 8.0
install_mysql() {
    print_step "安装 MySQL 8.0..."

    if command -v mysql &> /dev/null; then
        print_warning "MySQL 已安装，跳过安装步骤"
        return
    fi

    print_info "添加 MySQL 8.0 仓库..."
    yum install -y -q https://dev.mysql.com/get/mysql80-community-release-el7-3.noarch.rpm

    print_info "安装 MySQL 服务器..."
    yum install -y -q mysql-community-server

    print_info "启动 MySQL 服务..."
    systemctl start mysqld
    systemctl enable mysqld

    # 获取临时密码
    local temp_password=$(grep 'temporary password' /var/log/mysqld.log | tail -n 1 | awk '{print $NF}')

    if [[ -n "$temp_password" ]]; then
        print_success "MySQL 安装完成"
        print_info "临时密码: $temp_password"
        print_warning "请记住此密码，稍后需要使用"
    else
        print_error "无法获取 MySQL 临时密码"
    fi
}

# 安装 Redis
install_redis() {
    print_step "安装 Redis..."

    if command -v redis-server &> /dev/null; then
        print_warning "Redis 已安装，跳过安装步骤"
        return
    fi

    print_info "安装 Redis..."
    yum install -y -q redis

    # 配置 Redis
    local redis_conf="/etc/redis.conf"
    cp "$redis_conf" "$redis_conf.bak"

    print_info "配置 Redis..."
    sed -i 's/^bind 127.0.0.1$/bind 127.0.0.1/' "$redis_conf"
    sed -i 's/^protected-mode yes$/protected-mode yes/' "$redis_conf"
    sed -i 's/^# requirepass foobared$/requirepass your_redis_password_here/' "$redis_conf"
    sed -i 's/^daemonize no$/daemonize yes/' "$redis_conf"
    sed -i 's/^logfile ""$/logfile \/var\/log\/redis\/redis.log/' "$redis_conf"
    sed -i 's/^dir .\//dir \/var\/lib\/redis\//' "$redis_conf"

    # 创建 Redis 日志目录
    mkdir -p /var/log/redis
    chown redis:redis /var/log/redis

    print_info "启动 Redis 服务..."
    systemctl start redis
    systemctl enable redis

    # 验证安装
    if redis-cli ping &> /dev/null; then
        print_success "Redis 安装完成"
    else
        print_error "Redis 安装失败"
    fi
}

# 安装 Nginx
install_nginx() {
    print_step "安装 Nginx..."

    if command -v nginx &> /dev/null; then
        print_warning "Nginx 已安装，跳过安装步骤"
        return
    fi

    print_info "安装 Nginx..."
    yum install -y -q nginx

    print_info "启动 Nginx 服务..."
    systemctl start nginx
    systemctl enable nginx

    # 配置防火墙
    if systemctl is-active --quiet firewalld; then
        print_info "配置防火墙..."
        firewall-cmd --permanent --add-service=http
        firewall-cmd --permanent --add-service=https
        firewall-cmd --reload
    fi

    # 验证安装
    if curl -s http://localhost | grep -q "nginx"; then
        print_success "Nginx 安装完成"
    else
        print_warning "Nginx 安装完成，但可能需要进一步配置"
    fi
}

# 创建应用目录结构
create_application_structure() {
    print_step "创建应用目录结构..."

    local base_dir="/opt/apps"
    local app_dir="$base_dir/dormitory-management"

    print_info "创建目录结构..."
    mkdir -p "$app_dir"/{logs,config,backup,upload,temp,scripts}

    # 设置权限
    chown -R root:root "$base_dir"
    chmod -R 755 "$base_dir"

    print_success "应用目录结构创建完成: $app_dir"

    # 显示目录结构
    tree "$app_dir" 2>/dev/null || ls -la "$app_dir"
}

# 配置 Nginx 反向代理
configure_nginx_proxy() {
    print_step "配置 Nginx 反向代理..."

    local nginx_conf="/etc/nginx/conf.d/dormitory-backend.conf"

    # 创建 Nginx 配置
    cat > "$nginx_conf" << 'EOF'
# 宿舍管理系统后端 Nginx 配置

upstream dormitory_backend {
    server 127.0.0.1:8080;
    # 如果有多个实例，可以添加更多服务器
    # server 127.0.0.1:8081;
    # server 127.0.0.1:8082;
}

# HTTP 服务器 (重定向到 HTTPS)
server {
    listen 80;
    server_name _;  # 替换为你的实际域名

    # 安全重定向到 HTTPS
    return 301 https://$server_name$request_uri;
}

# HTTPS 服务器
server {
    listen 443 ssl http2;
    server_name _;  # 替换为你的实际域名

    # SSL 证书配置 (需要提供实际的证书路径)
    ssl_certificate /etc/ssl/certs/your-domain.crt;
    ssl_certificate_key /etc/ssl/private/your-domain.key;

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
    add_header Referrer-Policy "strict-origin-when-cross-origin";

    # 主要 API 代理配置
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
        proxy_busy_buffers_size 8k;

        # 客户端上传大小限制
        client_max_body_size 10M;
    }

    # 健康检查端点
    location /health {
        proxy_pass http://dormitory_backend/api/actuator/health;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;

        # 健康检查不需要缓存
        proxy_cache off;
        proxy_no_cache 1;
        proxy_cache_bypass 1;
    }

    # API 文档 (如果需要)
    location /docs {
        proxy_pass http://dormitory_backend/swagger-ui.html;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    # 静态文件缓存 (如果有)
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg|woff|woff2|ttf|eot)$ {
        expires 1y;
        add_header Cache-Control "public, immutable";
    }

    # 访问日志
    access_log /var/log/nginx/dormitory-backend-access.log;
    error_log /var/log/nginx/dormitory-backend-error.log;
}
EOF

    print_info "测试 Nginx 配置..."
    if nginx -t &> /dev/null; then
        print_success "Nginx 配置验证通过"
        print_info "重载 Nginx 配置..."
        nginx -s reload
        print_success "Nginx 重载完成"
    else
        print_error "Nginx 配置验证失败，请检查配置"
        nginx -t
        exit 1
    fi
}

# 创建系统监控脚本
create_monitoring_scripts() {
    print_step "创建系统监控脚本..."

    local scripts_dir="/opt/apps/dormitory-management/scripts"

    # 系统资源监控脚本
    cat > "$scripts_dir/monitor-system.sh" << 'EOF'
#!/bin/bash

# 系统资源监控脚本

echo "================================="
echo "系统资源监控报告"
echo "时间: $(date)"
echo "================================="

echo ""
echo "=== CPU 使用情况 ==="
top -bn1 | grep "Cpu(s)" | awk '{print "CPU 使用率: " $2}'
top -bn1 | grep "Cpu(s)" | awk '{print "用户: " $2 " 系统: " $4 " 空闲: " $8}'

echo ""
echo "=== 内存使用情况 ==="
free -h | awk '/Mem:/ {printf "总内存: %s\n已使用: %s\n可用: %s\n使用率: %.2f%%\n", $2, $3, $7, $3/$2*100}'
free -h | awk '/Swap:/ {printf "Swap: %s (已使用: %s)\n", $2, $3}'

echo ""
echo "=== 磁盘使用情况 ==="
df -h | awk '$NF=="/"{printf "根分区: %s/%s (%s)\n", $3, $2, $5}'

echo ""
echo "=== 网络连接情况 ==="
echo "活跃连接数: $(netstat -an | grep ESTABLISHED | wc -l)"
echo "监听端口:"
netstat -tlnp | grep LISTEN | awk '{print $4}' | sort -n

echo ""
echo "=== 系统负载 ==="
uptime

echo ""
echo "=== 进程信息 ==="
echo "Java 进程数: $(ps aux | grep java | grep -v grep | wc -l)"
echo "MySQL 进程: $(pgrep mysqld | wc -l)"
echo "Redis 进程: $(pgrep redis-server | wc -l)"
echo "Nginx 进程: $(pgrep nginx | wc -l)"

echo ""
echo "================================="
EOF

    chmod +x "$scripts_dir/monitor-system.sh"

    # 应用服务监控脚本
    cat > "$scripts_dir/monitor-services.sh" << 'EOF'
#!/bin/bash

# 应用服务监控脚本

echo "================================="
echo "应用服务监控报告"
echo "时间: $(date)"
echo "================================="

services=("mysqld" "redis" "nginx" "dormitory-backend")

for service in "${services[@]}"; do
    if systemctl is-active --quiet "$service"; then
        echo "$service: ✓ 运行中"
        echo "  状态: $(systemctl is-active "$service")"
        echo "  启用: $(systemctl is-enabled "$service")"
    else
        echo "$service: ✗ 未运行"
    fi
    echo ""
done

echo "=== 端口监听检查 ==="
ports=("3306:MySQL" "6379:Redis" "80:Nginx-HTTP" "443:Nginx-HTTPS" "8080:Spring-Boot")

for port_info in "${ports[@]}"; do
    port=$(echo "$port_info" | cut -d: -f1)
    service_name=$(echo "$port_info" | cut -d: -f2)

    if netstat -tlnp | grep -q ":$port "; then
        echo "$port ($service_name): ✓ 监听中"
    else
        echo "$port ($service_name): ✗ 未监听"
    fi
done

echo ""
echo "================================="
EOF

    chmod +x "$scripts_dir/monitor-services.sh"

    # 数据库备份脚本
    cat > "$scripts_dir/backup-database.sh" << 'EOF'
#!/bin/bash

# 数据库备份脚本

BACKUP_DIR="/opt/apps/dormitory-management/backup"
DATE=$(date +%Y%m%d_%H%M%S)
DB_NAME="dormitory_management"
DB_USER="dormitory_user"

# 创建备份目录
mkdir -p "$BACKUP_DIR"

# 备份数据库
echo "开始备份数据库: $DB_NAME"
mysqldump -u "$DB_USER" -p "$DB_NAME" > "$BACKUP_DIR/db_backup_$DATE.sql"

if [ $? -eq 0 ]; then
    # 压缩备份文件
    gzip "$BACKUP_DIR/db_backup_$DATE.sql"

    # 删除7天前的备份
    find "$BACKUP_DIR" -name "db_backup_*.sql.gz" -mtime +7 -delete

    echo "数据库备份完成: $BACKUP_DIR/db_backup_$DATE.sql.gz"
else
    echo "数据库备份失败"
    exit 1
fi
EOF

    chmod +x "$scripts_dir/backup-database.sh"

    print_success "监控脚本创建完成"
}

# 创建系统初始化脚本
create_init_scripts() {
    print_step "创建系统初始化脚本..."

    local scripts_dir="/opt/apps/dormitory-management/scripts"

    # 添加到 crontab 的脚本
    cat > "$scripts_dir/setup-cron.sh" << 'EOF'
#!/bin/bash

# 设置定时任务

echo "设置系统监控定时任务..."

# 添加 crontab 任务
(crontab -l 2>/dev/null; echo "
# 系统监控任务 (每分钟)
* * * * * /opt/apps/dormitory-management/scripts/monitor-system.sh >> /var/log/system-monitor.log 2>&1

# 服务监控任务 (每5分钟)
*/5 * * * * /opt/apps/dormitory-management/scripts/monitor-services.sh >> /var/log/service-monitor.log 2>&1

# 数据库备份任务 (每天凌晨2点)
0 2 * * * /opt/apps/dormitory-management/scripts/backup-database.sh >> /var/log/db-backup.log 2>&1

# 日志清理任务 (每周日凌晨3点)
0 3 * * 0 find /opt/apps/dormitory-management/logs -name "*.log" -mtime +30 -delete
") | crontab -

echo "定时任务设置完成"

# 创建日志目录
mkdir -p /var/log/{system-monitor,service-monitor,db-backup}

echo "日志目录创建完成"
EOF

    chmod +x "$scripts_dir/setup-cron.sh"

    print_success "初始化脚本创建完成"
}

# 配置系统安全
configure_security() {
    print_step "配置系统安全..."

    print_info "配置防火墙..."

    # 开放必要端口
    local ports=("80/tcp" "443/tcp" "8080/tcp")

    for port in "${ports[@]}"; do
        if systemctl is-active --quiet firewalld; then
            print_info "开放端口 $port..."
            firewall-cmd --permanent --add-port="$port"
        fi
    done

    if systemctl is-active --quiet firewalld; then
        firewall-cmd --reload
        print_success "防火墙配置完成"
    else
        print_warning "防火墙未运行，请手动配置"
    fi

    print_info "配置系统限制..."

    # 修改文件描述符限制
    cat >> /etc/security/limits.conf << 'EOF'

# Application limits
* soft nofile 65536
* hard nofile 65536
* soft nproc 65536
* hard nproc 65536
* soft memlock unlimited
* hard memlock unlimited
EOF

    # 修改内核参数
    cat > /etc/sysctl.d/99-app-limits.conf << 'EOF'
# Application kernel parameters
net.core.somaxconn = 32768
net.ipv4.tcp_max_syn_backlog = 32768
net.core.netdev_max_backlog = 32768
vm.swappiness = 1
vm.overcommit_memory = 1
fs.file-max = 2097152
EOF

    # 应用内核参数
    sysctl -p /etc/sysctl.d/99-app-limits.conf

    print_success "系统安全配置完成"
}

# 生成环境报告
generate_environment_report() {
    print_step "生成环境搭建报告..."

    local report_file="/opt/apps/dormitory-management/environment-setup-report.txt"

    cat > "$report_file" << EOF
宿舍管理系统环境搭建报告
========================================
搭建时间: $(date)
系统版本: $(cat /etc/centos-release 2>/dev/null || cat /etc/redhat-release 2>/dev/null)
内核版本: $(uname -r)
CPU信息: $(lscpu | grep 'Model name' | cut -d':' -f2 | xargs)
内存信息: $(free -h | grep '^Mem:' | awk '{print $2}')
磁盘信息: $(df -h / | tail -n 1 | awk '{print $2}')

软件版本信息
--------
Java: $(java -version 2>&1 | head -n 1)
MySQL: $(mysql --version 2>/dev/null | head -n 1 || echo "未安装")
Redis: $(redis-server --version 2>/dev/null || echo "未安装")
Nginx: $(nginx -v 2>&1 || echo "未安装")

网络配置
--------
主机名: $(hostname)
IP地址: $(hostname -I | xargs)
开放端口: $(firewall-cmd --list-ports 2>/dev/null || echo "未配置防火墙")

服务状态
--------
MySQL: $(systemctl is-active mysqld 2>/dev/null || echo "未安装")
Redis: $(systemctl is-active redis 2>/dev/null || echo "未安装")
Nginx: $(systemctl is-active nginx 2>/dev/null || echo "未安装")

目录结构
--------
应用根目录: /opt/apps/dormitory-management
配置目录: /opt/apps/dormitory-management/config
日志目录: /opt/apps/dormitory-management/logs
备份目录: /opt/apps/dormitory-management/backup
脚本目录: /opt/apps/dormitory-management/scripts

监控工具
--------
系统监控: /opt/apps/dormitory-management/scripts/monitor-system.sh
服务监控: /opt/apps/dormitory-management/scripts/monitor-services.sh
数据库备份: /opt/apps/dormitory-management/scripts/backup-database.sh
定时任务: /opt/apps/dormitory-management/scripts/setup-cron.sh

下一步操作
--------
1. 运行数据库初始化脚本
2. 上传应用 JAR 文件
3. 部署 Spring Boot 应用
4. 配置 SSL 证书
5. 设置定时任务
6. 测试完整架构

========================================
搭建完成时间: $(date)
搭建脚本版本: 1.0.0
EOF

    print_success "环境报告已生成: $report_file"
}

# 显示完成信息
show_completion_info() {
    print_header "========== 环境搭建完成 =========="
    echo
    echo "${GREEN}✅ 已完成的组件:${NC}"
    echo "  ${GREEN}•${NC} 系统更新和基础工具"
    echo "  ${GREEN}•${NC} Java 17 运行环境"
    echo "  ${GREEN}•${NC} MySQL 8.0 数据库服务"
    echo "  ${GREEN}•${NC} Redis 缓存服务"
    echo "  ${GREEN}•${NC} Nginx 反向代理"
    echo "  ${GREEN}•${NC} 应用目录结构"
    echo "  ${GREEN}•${NC} 系统监控脚本"
    echo "  ${GREEN}•${NC} 安全配置"
    echo
    echo "${BLUE}📁 创建的目录:${NC}"
    echo "  /opt/apps/dormitory-management/"
    echo "  ├── logs/          # 应用日志"
    echo "  ├── config/        # 配置文件"
    echo "  ├── backup/        # 备份文件"
    echo "  ├── upload/        # 上传文件"
    echo "  └── scripts/       # 监控脚本"
    echo
    echo "${CYAN}🔧 下一步操作:${NC}"
    echo "  1. 初始化数据库:"
    echo "     mysql -u root -p < /path/to/init-database.sql"
    echo
    echo "  2. 构建应用 JAR:"
    echo "     cd backend && mvn clean package -DskipTests"
    echo
    echo "  3. 部署应用:"
    echo "     sudo ./deploy-centos.sh /path/to/jar-file"
    echo
    echo "  4. 设置定时任务:"
    echo "     /opt/apps/dormitory-management/scripts/setup-cron.sh"
    echo
    echo "${YELLOW}⚠ 重要提醒:${NC}"
    echo "  • 请记得修改 MySQL root 密码"
    echo "  • 请配置 Redis 密码"
    echo "  • 请更新 Nginx 配置中的域名"
    echo "  • 请配置 SSL 证书"
    echo
    echo "${GREEN}🎉 环境架构已准备就绪！${NC}"
    echo "=========================================="
}

# 主函数
main() {
    echo "========================================"
    echo "🚀 宿舍管理系统完整环境搭建"
    echo "        (从零开始)"
    echo "========================================"

    # 执行所有步骤
    check_system_environment
    update_system
    install_java
    install_mysql
    install_redis
    install_nginx
    create_application_structure
    configure_nginx_proxy
    create_monitoring_scripts
    create_init_scripts
    configure_security
    generate_environment_report
    show_completion_info

    print_success "完整环境搭建完成！"
}

# 脚本入口
if [[ "${BASH_SOURCE[0]}" == "${0}" ]]; then
    main "$@"
fi