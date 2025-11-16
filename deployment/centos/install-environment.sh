#!/bin/bash

# CentOS 环境依赖安装脚本
# 用于宿舍管理系统后端部署
# 作者: Dormitory Management Team
# 版本: 1.0.0
# 更新时间: 2025-11-16

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

# 检查 root 权限
check_root() {
    if [[ $EUID -ne 0 ]]; then
        log_error "此脚本需要 root 权限运行"
        exit 1
    fi
}

# 检查 CentOS 版本
check_system() {
    if [[ ! -f /etc/centos-release && ! -f /etc/redhat-release ]]; then
        log_error "此脚本仅支持 CentOS/RHEL 系统"
        exit 1
    fi

    local version=$(cat /etc/centos-release 2>/dev/null || cat /etc/redhat-release 2>/dev/null)
    log_info "检测到系统版本: $version"
}

# 更新系统
update_system() {
    log_info "更新系统软件包..."
    yum update -y
    yum install -y epel-release
    yum clean all
    log_success "系统更新完成"
}

# 安装基础工具
install_basic_tools() {
    log_info "安装基础工具..."

    local tools=(
        "wget"
        "curl"
        "vim"
        "unzip"
        "tar"
        "git"
        "lsof"
        "tree"
        "htop"
        "net-tools"
        "telnet"
        "nc"
    )

    for tool in "${tools[@]}"; do
        if ! command -v "$tool" &> /dev/null; then
            log_info "安装 $tool..."
            yum install -y "$tool"
        else
            log_info "$tool 已安装"
        fi
    done

    log_success "基础工具安装完成"
}

# 安装 Java 17
install_java() {
    log_info "安装 Java 17..."

    # 检查 Java 是否已安装
    if command -v java &> /dev/null; then
        local java_version=$(java -version 2>&1 | head -n 1 | grep -oP 'version "?(1\.)?\K\d+' || echo "unknown")
        if [[ "$java_version" == "17" ]]; then
            log_success "Java 17 已安装"
            return
        else
            log_warning "检测到 Java 版本: $java_version，将安装 Java 17"
        fi
    fi

    # 安装 OpenJDK 17
    yum install -y java-17-openjdk java-17-openjdk-devel

    # 设置环境变量
    cat > /etc/profile.d/java17.sh << 'EOF'
#!/bin/bash
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk
export JRE_HOME=$JAVA_HOME/jre
export CLASSPATH=$JAVA_HOME/lib:$JRE_HOME/lib
export PATH=$JAVA_HOME/bin:$PATH
EOF

    # 立即生效
    source /etc/profile.d/java17.sh

    # 验证安装
    if command -v java &> /dev/null; then
        local version=$(java -version 2>&1 | head -n 1)
        log_success "Java 17 安装完成: $version"
    else
        log_error "Java 17 安装失败"
        exit 1
    fi
}

# 安装 MySQL 8.0
install_mysql() {
    log_info "安装 MySQL 8.0..."

    # 检查 MySQL 是否已安装
    if command -v mysql &> /dev/null; then
        log_warning "MySQL 已安装，跳过安装步骤"
        return
    fi

    # 添加 MySQL 8.0 仓库
    yum install -y https://dev.mysql.com/get/mysql80-community-release-el7-3.noarch.rpm

    # 安装 MySQL 服务器
    yum install -y mysql-community-server

    # 启动 MySQL 服务
    systemctl start mysqld
    systemctl enable mysqld

    # 获取临时密码
    local temp_password=$(grep 'temporary password' /var/log/mysqld.log | tail -n 1 | awk '{print $NF}')

    if [[ -z "$temp_password" ]]; then
        log_error "无法获取 MySQL 临时密码"
        exit 1
    fi

    log_info "MySQL 临时密码: $temp_password"

    # 安全配置 MySQL
    log_info "执行 MySQL 安全配置..."

    # 创建临时配置文件
    cat > /tmp/mysql_secure.sql << EOF
ALTER USER 'root'@'localhost' IDENTIFIED BY 'YourStrongPassword123!';
DELETE FROM mysql.user WHERE User='';
DELETE FROM mysql.user WHERE User='root' AND Host NOT IN ('localhost', '127.0.0.1', '::1');
DROP DATABASE IF EXISTS test;
DELETE FROM mysql.db WHERE Db='test' OR Db='test\_%';
FLUSH PRIVILEGES;
EOF

    # 执行安全配置
    mysql --connect-expired-password -u root -p"$temp_password" < /tmp/mysql_secure.sql

    # 清理临时文件
    rm -f /tmp/mysql_secure.sql

    # 重启 MySQL 服务
    systemctl restart mysqld

    log_success "MySQL 8.0 安装完成"
    log_info "默认 root 密码: YourStrongPassword123"
    log_warning "请立即修改 MySQL root 密码"
}

# 安装 Redis
install_redis() {
    log_info "安装 Redis..."

    # 检查 Redis 是否已安装
    if command -v redis-server &> /dev/null; then
        log_warning "Redis 已安装，跳过安装步骤"
        return
    fi

    # 启用 EPEL 仓库并安装 Redis
    yum install -y redis

    # 配置 Redis
    local redis_conf="/etc/redis.conf"

    # 备份原始配置
    cp "$redis_conf" "$redis_conf.bak"

    # 修改 Redis 配置
    sed -i 's/^bind 127.0.0.1$/bind 127.0.0.1/' "$redis_conf"
    sed -i 's/^protected-mode yes$/protected-mode yes/' "$redis_conf"
    sed -i 's/^# requirepass foobared$/requirepass your_redis_password/' "$redis_conf"
    sed -i 's/^daemonize no$/daemonize yes/' "$redis_conf"
    sed -i 's/^logfile ""$/logfile \/var\/log\/redis\/redis.log/' "$redis_conf"
    sed -i 's/^dir .\//dir \/var\/lib\/redis\//' "$redis_conf"

    # 创建 Redis 日志目录
    mkdir -p /var/log/redis
    chown redis:redis /var/log/redis

    # 启动并启用 Redis 服务
    systemctl start redis
    systemctl enable redis

    # 验证 Redis 安装
    if redis-cli ping &> /dev/null; then
        log_success "Redis 安装完成"
    else
        log_error "Redis 安装失败"
        exit 1
    fi
}

# 安装 Nginx
install_nginx() {
    log_info "安装 Nginx..."

    # 检查 Nginx 是否已安装
    if command -v nginx &> /dev/null; then
        log_warning "Nginx 已安装，跳过安装步骤"
        return
    fi

    # 安装 Nginx
    yum install -y nginx

    # 启动并启用 Nginx 服务
    systemctl start nginx
    systemctl enable nginx

    # 配置防火墙
    if systemctl is-active --quiet firewalld; then
        firewall-cmd --permanent --add-service=http
        firewall-cmd --permanent --add-service=https
        firewall-cmd --reload
    fi

    # 验证 Nginx 安装
    if curl -s http://localhost | grep -q "nginx"; then
        log_success "Nginx 安装完成"
    else
        log_warning "Nginx 安装完成，但可能需要进一步配置"
    fi
}

# 安装 Maven
install_maven() {
    log_info "安装 Maven..."

    # 检查 Maven 是否已安装
    if command -v mvn &> /dev/null; then
        log_warning "Maven 已安装，跳过安装步骤"
        return
    fi

    # 安装 Maven
    yum install -y maven

    # 验证 Maven 安装
    if command -v mvn &> /dev/null; then
        local maven_version=$(mvn -version | head -n 1)
        log_success "Maven 安装完成: $maven_version"
    else
        log_error "Maven 安装失败"
        exit 1
    fi
}

# 配置防火墙
setup_firewall() {
    log_info "配置防火墙..."

    # 检查防火墙状态
    if ! systemctl is-active --quiet firewalld; then
        log_warning "防火墙服务未运行，启动防火墙..."
        systemctl start firewalld
        systemctl enable firewalld
    fi

    # 开放必要端口
    local ports=(
        "80/tcp"      # HTTP
        "443/tcp"     # HTTPS
        "8080/tcp"    # 应用端口
        "3306/tcp"    # MySQL (可选，生产环境建议关闭公网访问)
        "6379/tcp"    # Redis (可选，生产环境建议关闭公网访问)
    )

    for port in "${ports[@]}"; do
        log_info "开放端口 $port..."
        firewall-cmd --permanent --add-port="$port"
    done

    firewall-cmd --reload
    log_success "防火墙配置完成"
}

# 配置系统限制
configure_system_limits() {
    log_info "配置系统限制..."

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

    log_success "系统限制配置完成"
}

# 创建应用用户
create_app_user() {
    log_info "创建应用用户..."

    local app_user="dormitory"

    # 检查用户是否已存在
    if id "$app_user" &>/dev/null; then
        log_warning "用户 $app_user 已存在"
    else
        useradd -r -s /bin/false "$app_user"
        log_success "用户 $app_user 创建成功"
    fi
}

# 创建应用目录结构
create_app_directories() {
    log_info "创建应用目录结构..."

    local base_dir="/opt/apps/dormitory-management"
    local dirs=(
        "$base_dir"
        "$base_dir/logs"
        "$base_dir/config"
        "$base_dir/backup"
        "$base_dir/upload"
        "$base_dir/temp"
    )

    for dir in "${dirs[@]}"; do
        mkdir -p "$dir"
        chown -R dormitory:dormitory "$dir"
        chmod -R 755 "$dir"
    done

    log_success "应用目录结构创建完成: $base_dir"
}

# 安装监控工具
install_monitoring() {
    log_info "安装监控工具..."

    # 安装 htop (如果未安装)
    if ! command -v htop &> /dev/null; then
        yum install -y htop
    fi

    # 安装 iotop (如果未安装)
    if ! command -v iotop &> /dev/null; then
        yum install -y iotop
    fi

    log_success "监控工具安装完成"
}

# 生成环境信息报告
generate_environment_report() {
    log_info "生成环境信息报告..."

    local report_file="/opt/apps/dormitory-management/environment-report.txt"

    cat > "$report_file" << EOF
宿舍管理系统部署环境报告
========================================
生成时间: $(date)
系统版本: $(cat /etc/centos-release 2>/dev/null || cat /etc/redhat-release 2>/dev/null)
内核版本: $(uname -r)
CPU信息: $(lscpu | grep 'Model name' | cut -d':' -f2 | xargs)
内存信息: $(free -h | grep '^Mem:' | awk '{print $2}')
磁盘信息: $(df -h / | tail -n 1 | awk '{print $2}')

软件版本
--------
Java: $(java -version 2>&1 | head -n 1)
MySQL: $(mysql --version 2>/dev/null || echo "未安装")
Redis: $(redis-server --version 2>/dev/null || echo "未安装")
Nginx: $(nginx -v 2>&1 || echo "未安装")
Maven: $(mvn -version 2>&1 | head -n 1 2>/dev/null || echo "未安装")

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

重要信息
--------
应用目录: /opt/apps/dormitory-management
配置目录: /opt/apps/dormitory-management/config
日志目录: /opt/apps/dormitory-management/logs
备份目录: /opt/apps/dormitory-management/backup

MySQL 默认密码: YourStrongPassword123 (请立即修改)
Redis 默认密码: your_redis_password (请立即修改)
========================================
EOF

    chown dormitory:dormitory "$report_file"
    log_success "环境报告已生成: $report_file"
}

# 显示安装完成信息
show_completion_info() {
    log_success "========== 环境安装完成 =========="
    echo
    echo "安装的软件版本:"
    echo "  Java: $(java -version 2>&1 | head -n 1)"
    echo "  MySQL: $(mysql --version 2>&1 | head -n 1)"
    echo "  Redis: $(redis-server --version 2>&1 | head -n 1)"
    echo "  Nginx: $(nginx -v 2>&1)"
    echo "  Maven: $(mvn -version 2>&1 | head -n 1)"
    echo
    echo "重要提醒:"
    echo "  1. 请立即修改 MySQL root 密码: mysql -u root -p"
    echo "  2. 请配置 Redis 密码: /etc/redis.conf"
    echo "  3. 请上传应用 JAR 文件到: /opt/apps/dormitory-management"
    echo "  4. 请运行数据库初始化脚本"
    echo "  5. 请配置防火墙规则"
    echo "  6. 请配置 SSL 证书"
    echo
    echo "下一步操作:"
    echo "  1. 运行数据库初始化: mysql -u root -p < init-database.sql"
    echo "  2. 运行应用部署: ./deploy-centos.sh <jar文件>"
    echo "  3. 配置 Nginx 反向代理"
    echo "  4. 设置监控和日志"
    echo "=========================================="
}

# 主函数
main() {
    echo "========================================"
    echo "🔧 宿舍管理系统 CentOS 环境安装脚本"
    echo "========================================"

    check_root
    check_system
    update_system
    install_basic_tools
    install_java
    install_mysql
    install_redis
    install_nginx
    install_maven
    setup_firewall
    configure_system_limits
    create_app_user
    create_app_directories
    install_monitoring
    generate_environment_report
    show_completion_info

    log_success "环境安装完成！"
}

# 脚本入口
if [[ "${BASH_SOURCE[0]}" == "${0}" ]]; then
    main "$@"
fi