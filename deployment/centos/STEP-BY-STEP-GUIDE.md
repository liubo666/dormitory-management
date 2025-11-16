# CentOS 环境搭建逐步指南

## 🎯 目标

按照架构图在空的 CentOS 服务器上搭建完整的生产环境：

```
┌─────────────────────────────────────────────────────────────┐
│                        CentOS 服务器                         │
├─────────────────────────────────────────────────────────────┤
│  Nginx (反向代理)                                            │
│  ├── 80/443 端口 (HTTP/HTTPS)                              │
│  └── SSL 证书                                               │
├─────────────────────────────────────────────────────────────┤
│  Spring Boot 应用 (8080 端口)                               │
│  ├── JAR 包运行                                            │
│  ├── 内嵌 Tomcat                                           │
│  └── JVM 管理                                              │
├─────────────────────────────────────────────────────────────┤
│  数据库层                                                   │
│  ├── MySQL 8.0 (3306 端口)                                │
│  └── Redis (6379 端口)                                    │
├─────────────────────────────────────────────────────────────┤
│  监控和日志                                                 │
│  ├── systemd 服务管理                                     │
│  ├── 应用日志                                             │
│  ├── 系统监控                                             │
│  └── 备份机制                                             │
└─────────────────────────────────────────────────────────────┘
```

## 📋 前置要求

- CentOS 7.x 或 8.x 系统
- root 权限
- 稳定的网络连接
- 至少 4GB 内存，50GB 磁盘空间

## 🚀 一键搭建（推荐）

如果你想要最快的搭建方式，直接运行完整环境搭建脚本：

```bash
# 1. 下载脚本到服务器
wget https://your-domain.com/deployment/centos/build-complete-environment.sh
chmod +x build-complete-environment.sh

# 2. 执行一键搭建
sudo ./build-complete-environment.sh

# 3. 等待完成（大约需要 10-20 分钟）
```

## 📝 逐步操作指南

如果你想要逐步了解每一步的细节，请按照以下步骤操作：

### 第一步：系统准备

#### 1.1 检查系统信息
```bash
# 查看系统版本
cat /etc/centos-release

# 查看硬件资源
free -h          # 内存
df -h            # 磁盘
nproc            # CPU 核心
```

#### 1.2 更新系统
```bash
# 更新系统软件包
sudo yum update -y

# 安装基础工具
sudo yum groupinstall -y "Development Tools"
sudo yum install -y wget curl vim unzip git htop tree
```

### 第二步：安装 Java 17

#### 2.1 安装 OpenJDK 17
```bash
# 安装 Java 17
sudo yum install -y java-17-openjdk java-17-openjdk-devel

# 设置环境变量
echo "export JAVA_HOME=/usr/lib/jvm/java-17-openjdk" | sudo tee /etc/profile.d/java17.sh
echo "export PATH=\$JAVA_HOME/bin:\$PATH" | sudo tee -a /etc/profile.d/java17.sh
source /etc/profile.d/java17.sh

# 验证安装
java -version
```

### 第三步：安装 MySQL 8.0

#### 3.1 安装 MySQL
```bash
# 添加 MySQL 仓库
sudo yum install -y https://dev.mysql.com/get/mysql80-community-release-el7-3.noarch.rpm

# 安装 MySQL 服务器
sudo yum install -y mysql-community-server

# 启动并设置开机自启
sudo systemctl start mysqld
sudo systemctl enable mysqld
```

#### 3.2 安全配置 MySQL
```bash
# 获取临时密码
sudo grep 'temporary password' /var/log/mysqld.log | tail -n 1

# 登录 MySQL（使用临时密码）
mysql -u root -p

# 修改 root 密码
ALTER USER 'root'@'localhost' IDENTIFIED BY 'YourNewPassword123!';

# 创建应用数据库和用户
CREATE DATABASE dormitory_management CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'dormitory_user'@'localhost' IDENTIFIED BY 'dormitory123';
GRANT ALL PRIVILEGES ON dormitory_management.* TO 'dormitory_user'@'localhost';
FLUSH PRIVILEGES;

EXIT;
```

### 第四步：安装 Redis

#### 4.1 安装 Redis
```bash
# 安装 Redis
sudo yum install -y redis

# 启动并设置开机自启
sudo systemctl start redis
sudo systemctl enable redis
```

#### 4.2 配置 Redis（可选）
```bash
# 编辑 Redis 配置
sudo vim /etc/redis.conf

# 修改以下配置：
# requirepass your_redis_password
# bind 127.0.0.1
# daemonize yes

# 重启 Redis
sudo systemctl restart redis

# 测试连接
redis-cli ping
```

### 第五步：安装 Nginx

#### 5.1 安装 Nginx
```bash
# 安装 Nginx
sudo yum install -y nginx

# 启动并设置开机自启
sudo systemctl start nginx
sudo systemctl enable nginx

# 验证安装
curl http://localhost
```

#### 5.2 配置防火墙
```bash
# 检查防火墙状态
sudo systemctl status firewalld

# 开放必要端口
sudo firewall-cmd --permanent --add-service=http
sudo firewall-cmd --permanent --add-service=https
sudo firewall-cmd --permanent --add-port=8080/tcp
sudo firewall-cmd --reload
```

### 第六步：创建应用目录结构

#### 6.1 创建目录
```bash
# 创建应用根目录
sudo mkdir -p /opt/apps/dormitory-management

# 创建子目录
sudo mkdir -p /opt/apps/dormitory-management/{logs,config,backup,upload,temp,scripts}

# 设置权限
sudo chown -R root:root /opt/apps/dormitory-management
sudo chmod -R 755 /opt/apps/dormitory-management
```

### 第七步：配置 Nginx 反向代理

#### 7.1 创建 Nginx 配置文件
```bash
sudo vim /etc/nginx/conf.d/dormitory-backend.conf
```

配置内容：
```nginx
upstream dormitory_backend {
    server 127.0.0.1:8080;
}

server {
    listen 80;
    server_name _;  # 替换为你的域名

    location /api {
        proxy_pass http://dormitory_backend;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;

        # 超时配置
        proxy_connect_timeout 30s;
        proxy_send_timeout 30s;
        proxy_read_timeout 30s;

        # 缓冲配置
        proxy_buffering on;
        proxy_buffer_size 4k;
        proxy_buffers 8 4k;

        # 上传大小限制
        client_max_body_size 10M;
    }

    location /health {
        proxy_pass http://dormitory_backend/api/actuator/health;
        proxy_set_header Host $host;
    }
}
```

#### 7.2 测试并重载 Nginx
```bash
# 测试配置
sudo nginx -t

# 重载配置
sudo nginx -s reload

# 验证代理
curl http://localhost/api/actuator/health
```

### 第八步：创建监控脚本

#### 8.1 创建监控脚本目录
```bash
cd /opt/apps/dormitory-management/scripts
```

#### 8.2 创建系统监控脚本
```bash
sudo tee /opt/apps/dormitory-management/scripts/monitor-system.sh << 'EOF'
#!/bin/bash

echo "系统监控报告 - $(date)"
echo "==========================="

echo "CPU 使用情况:"
top -bn1 | grep "Cpu(s)" | awk '{print "CPU 使用率: " $2}'

echo ""
echo "内存使用情况:"
free -h | awk '/Mem:/ {printf "总内存: %s\n已使用: %s\n可用: %s\n", $2, $3, $7}'

echo ""
echo "磁盘使用情况:"
df -h / | awk '{print "根分区: " $3 "/" $2 " (" $5 ")"}'

echo ""
echo "网络连接:"
echo "活跃连接数: $(netstat -an | grep ESTABLISHED | wc -l)"

echo ""
echo "进程信息:"
echo "Java 进程数: $(ps aux | grep java | grep -v grep | wc -l)"
echo "MySQL 进程: $(pgrep mysqld | wc -l)"
echo "Redis 进程: $(pgrep redis-server | wc -l)"
echo "Nginx 进程: $(pgrep nginx | wc -l)"
EOF

sudo chmod +x /opt/apps/dormitory-management/scripts/monitor-system.sh
```

#### 8.3 创建服务监控脚本
```bash
sudo tee /opt/apps/dormitory-management/scripts/monitor-services.sh << 'EOF'
#!/bin/bash

echo "服务状态监控 - $(date)"
echo "========================="

services=("mysqld" "redis" "nginx")

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

echo "端口监听检查:"
ports=("3306:MySQL" "6379:Redis" "80:Nginx" "8080:Spring-Boot")

for port_info in "${ports[@]}"; do
    port=$(echo "$port_info" | cut -d: -f1)
    service_name=$(echo "$port_info" | cut -d: -f2)

    if netstat -tlnp | grep -q ":$port "; then
        echo "$port ($service_name): ✓ 监听中"
    else
        echo "$port ($service_name): ✗ 未监听"
    fi
done
EOF

sudo chmod +x /opt/apps/dormitory-management/scripts/monitor-services.sh
```

#### 8.4 创建数据库备份脚本
```bash
sudo tee /opt/apps/dormitory-management/scripts/backup-database.sh << 'EOF'
#!/bin/bash

BACKUP_DIR="/opt/apps/dormitory-management/backup"
DATE=$(date +%Y%m%d_%H%M%S)
DB_NAME="dormitory_management"
DB_USER="dormitory_user"

mkdir -p "$BACKUP_DIR"

echo "开始备份数据库: $DB_NAME"
mysqldump -u "$DB_USER" -p "$DB_NAME" > "$BACKUP_DIR/db_backup_$DATE.sql"

if [ $? -eq 0 ]; then
    gzip "$BACKUP_DIR/db_backup_$DATE.sql"
    find "$BACKUP_DIR" -name "db_backup_*.sql.gz" -mtime +7 -delete
    echo "备份完成: $BACKUP_DIR/db_backup_$DATE.sql.gz"
else
    echo "备份失败"
    exit 1
fi
EOF

sudo chmod +x /opt/apps/dormitory-management/scripts/backup-database.sh
```

### 第九步：验证环境搭建

#### 9.1 检查所有服务状态
```bash
# 检查系统监控
/opt/apps/dormitory-management/scripts/monitor-system.sh

# 检查服务状态
/opt/apps/dormitory-management/scripts/monitor-services.sh
```

#### 9.2 验证端口监听
```bash
# 检查所有端口
sudo netstat -tlnp | grep -E ":(80|443|3306|6379|8080)"

# 应该看到类似输出：
# tcp        0      0 0.0.0.0:80              0.0.0.0:*               LISTEN
# tcp        0      0 0.0.0.0:443             0.0.0.0:*               LISTEN
# tcp        0      0 127.0.0.1:3306          0.0.0.0:*               LISTEN
# tcp        0      0 127.0.0.1:6379          0.0.0.0:*               LISTEN
# tcp        0      0 0.0.0.0:8080            0.0.0.0:*               LISTEN
```

#### 9.3 测试 Nginx 代理
```bash
# 测试健康检查端点
curl http://localhost/api/actuator/health

# 应该返回类似：
# {"status":"UP"}
```

### 第十步：设置定时任务

#### 10.1 创建定时任务脚本
```bash
sudo tee /opt/apps/dormitory-management/scripts/setup-cron.sh << 'EOF'
#!/bin/bash

echo "设置定时任务..."

# 添加到 crontab
(crontab -l 2>/dev/null; echo "
# 每分钟检查系统状态
* * * * * /opt/apps/dormitory-management/scripts/monitor-system.sh >> /var/log/system-monitor.log 2>&1

# 每5分钟检查服务状态
*/5 * * * * /opt/apps/dormitory-management/scripts/monitor-services.sh >> /var/log/service-monitor.log 2>&1

# 每天凌晨2点备份数据库
0 2 * * * /opt/apps/dormitory-management/scripts/backup-database.sh >> /var/log/db-backup.log 2>&1

# 每周日凌晨3点清理日志
0 3 * * 0 find /opt/apps/dormitory-management/logs -name "*.log" -mtime +30 -delete
") | crontab -

echo "定时任务设置完成"
EOF

sudo chmod +x /opt/apps/dormitory-management/scripts/setup-cron.sh

# 执行定时任务设置
sudo /opt/apps/dormitory-management/scripts/setup-cron.sh
```

#### 10.2 验证定时任务
```bash
# 查看定时任务列表
crontab -l

# 查看定时任务日志
tail -f /var/log/system-monitor.log
```

## 🎯 环境搭建验证清单

### ✅ 系统环境
- [ ] CentOS 系统版本确认
- [ ] 系统更新完成
- [ ] 基础工具安装完成
- [ ] 硬件资源满足要求

### ✅ Java 环境
- [ ] Java 17 安装完成
- [ ] JAVA_HOME 环境变量设置
- [ ] Java 版本验证通过

### ✅ MySQL 数据库
- [ ] MySQL 8.0 安装完成
- [ ] 服务启动并设为开机自启
- [ ] 数据库创建完成
- [ ] 应用用户创建完成
- [ ] 权限设置完成
- [ ] 数据库连接测试通过

### ✅ Redis 缓存
- [ ] Redis 安装完成
- [ ] 服务启动并设为开机自启
- [ ] Redis 配置完成
- [ ] Redis 连接测试通过

### ✅ Nginx 反向代理
- [ ] Nginx 安装完成
- [ ] 服务启动并设为开机自启
- [ ] 反向代理配置完成
- [ ] 防火墙端口开放
- [ ] Nginx 配置重载
- [ ] 代理功能测试通过

### ✅ 应用目录结构
- [ ] 根目录创建完成
- [ ] 子目录结构创建完成
- [ ] 目录权限设置正确

### ✅ 监控和日志
- [ ] 系统监控脚本创建
- [ ] 服务监控脚本创建
- [ ] 数据库备份脚本创建
- [ ] 定时任务配置完成
- [ ] 日志目录创建完成
- [ ] 监控功能测试通过

### ✅ 安全配置
- [ ] 防火墙规则配置
- [ ] 系统限制优化
- [ ] 内核参数优化

## 🎉 环境搭建完成！

如果你按照以上步骤操作，现在你应该拥有一个完整的生产环境架构：

```
✅ CentOS 服务器基础环境
✅ Java 17 运行环境
✅ MySQL 8.0 数据库服务 (3306)
✅ Redis 缓存服务 (6379)
✅ Nginx 反向代理 (80/443)
✅ 应用目录结构 (/opt/apps/dormitory-management)
✅ 系统监控和日志
✅ 自动备份机制
```

## 🚀 下一步操作

### 1. 部署 Spring Boot 应用
```bash
# 构建应用 JAR 包
cd backend
mvn clean package -DskipTests

# 上传到服务器
scp target/dormitory-management-1.0.0.jar root@your-server:/tmp/

# 部署应用
sudo ./deploy-centos.sh /tmp/dormitory-management-1.0.0.jar
```

### 2. 初始化数据库
```bash
# 执行数据库初始化脚本
mysql -u root -p < init-database.sql
```

### 3. 配置 SSL 证书
```bash
# 获取 SSL 证书
# 更新 Nginx 配置中的证书路径
# 测试 HTTPS 访问
```

### 4. 前端部署
```bash
# 部署前端到 EdgeOne
# 配置前端 API 地址指向新的后端
```

## 🔧 故障排除

### 常见问题解决

#### 1. MySQL 连接失败
```bash
# 检查 MySQL 服务状态
sudo systemctl status mysqld

# 重启 MySQL 服务
sudo systemctl restart mysqld

# 检查端口监听
sudo netstat -tlnp | grep 3306
```

#### 2. Redis 连接失败
```bash
# 检查 Redis 服务状态
sudo systemctl status redis

# 重启 Redis 服务
sudo systemctl restart redis

# 测试连接
redis-cli ping
```

#### 3. Nginx 配置错误
```bash
# 测试 Nginx 配置
sudo nginx -t

# 查看错误日志
sudo tail -f /var/log/nginx/error.log

# 重载配置
sudo nginx -s reload
```

#### 4. 端口占用问题
```bash
# 查看端口占用
sudo netstat -tlnp | grep :8080

# 杀死占用进程
sudo kill -9 <PID>
```

## 📞 技术支持

如果在搭建过程中遇到问题，可以：

1. 查看详细日志：`journalctl -u <service_name> -f`
2. 检查服务状态：`systemctl status <service_name>`
3. 运行系统监控脚本：`/opt/apps/dormitory-management/scripts/monitor-system.sh`
4. 查看环境报告：`/opt/apps/dormitory-management/environment-setup-report.txt`

---

**搭建完成时间**: 2025-11-16
**环境版本**: CentOS 完整生产环境
**下一步**: 部署 Spring Boot 应用