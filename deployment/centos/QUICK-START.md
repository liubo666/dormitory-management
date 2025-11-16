# CentOS 快速部署指南

## 🎯 目标：从空的 CentOS 服务器到完整的生产环境

你的架构图：
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

## 🚀 方法一：一键搭建（推荐）

在你的 CentOS 服务器上执行以下命令：

```bash
# 方式1：直接下载并执行
curl -sSL https://your-domain.com/quick-setup-server.sh | sudo bash

# 方式2：下载后执行
wget https://your-domain.com/quick-setup-server.sh
chmod +x quick-setup-server.sh
sudo ./quick-setup-server.sh
```

## 📝 方法二：手动逐步搭建

如果上面的方式不行，可以手动执行以下步骤：

### 1. 环境准备和系统更新
```bash
# 更新系统
sudo yum update -y

# 安装基础工具
sudo yum groupinstall -y "Development Tools"
sudo yum install -y wget curl vim unzip htop net-tools
```

### 2. 安装 Java 17
```bash
# 安装 Java 17
sudo yum install -y java-17-openjdk java-17-openjdk-devel

# 设置环境变量
echo "export JAVA_HOME=/usr/lib/jvm/java-17-openjdk" | sudo tee /etc/profile.d/java17.sh
source /etc/profile.d/java17.sh

# 验证安装
java -version
```

### 3. 安装 MySQL 8.0
```bash
# 安装 MySQL
sudo yum install -y https://dev.mysql.com/get/mysql80-community-release-el7-3.noarch.rpm
sudo yum install -y mysql-community-server

# 启动服务
sudo systemctl start mysqld
sudo systemctl enable mysqld

# 获取临时密码
sudo grep 'temporary password' /var/log/mysqld.log | tail -n 1
```

### 4. 安装 Redis
```bash
# 安装 Redis
sudo yum install -y redis

# 启动服务
sudo systemctl start redis
sudo systemctl enable redis
```

### 5. 安装 Nginx
```bash
# 安装 Nginx
sudo yum install -y nginx

# 启动服务
sudo systemctl start nginx
sudo systemctl enable nginx
```

### 6. 配置防火墙
```bash
# 开放端口
sudo firewall-cmd --permanent --add-service=http
sudo firewall-cmd --permanent --add-service=https
sudo firewall-cmd --permanent --add-port=8080/tcp
sudo firewall-cmd --reload
```

## 🔧 数据库配置

安装完成后，需要配置数据库：

```bash
# 获取 MySQL 临时密码
MYSQL_PASSWORD=$(sudo grep 'temporary password' /var/log/mysqld.log | tail -n 1 | awk '{print $NF}')
echo "临时密码: $MYSQL_PASSWORD"

# 登录 MySQL
mysql -u root -p'$MYSQL_PASSWORD'

# 修改 root 密码
ALTER USER 'root'@'localhost' IDENTIFIED BY 'YourNewPassword123!';

# 创建应用数据库和用户
CREATE DATABASE dormitory_management CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'dormitory_user'@'localhost' IDENTIFIED BY 'dormitory123';
GRANT ALL PRIVILEGES ON dormitory_management.* TO 'dormitory_user'@'localhost';
FLUSH PRIVILEGES;

EXIT;
```

## 📁 创建应用目录

```bash
# 创建应用目录
sudo mkdir -p /opt/apps/dormitory-management/{logs,config,backup,upload,scripts}

# 设置权限
sudo chown -R root:root /opt/apps
sudo chmod -R 755 /opt/apps
```

## 🌐 配置 Nginx

创建 Nginx 反向代理配置：

```bash
# 创建配置文件
sudo tee /etc/nginx/conf.d/dormitory-backend.conf << 'EOF'
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

# 重载配置
sudo nginx -s reload
```

## ✅ 验证环境

检查所有服务是否正常运行：

```bash
# 检查服务状态
sudo systemctl status mysqld
sudo systemctl status redis
sudo systemctl status nginx

# 检查端口监听
sudo netstat -tlnp | grep -E ":(80|3306|6379)"

# 测试 Nginx
curl http://localhost
```

## 🚀 部署 Spring Boot 应用

### 1. 构建 JAR 包
在你的本地开发环境：
```bash
cd backend
mvn clean package -DskipTests
```

### 2. 上传 JAR 文件
```bash
# 上传到服务器
scp target/dormitory-management-1.0.0.jar root@your-server:/tmp/
```

### 3. 在服务器上创建配置文件
```bash
# 创建应用配置
sudo tee /opt/apps/dormitory-management/config/application-prod.yml << 'EOF'
server:
  port: 8080
  servlet:
    context-path: /api

spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/dormitory_management?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: dormitory_user
    password: dormitory123

  data:
    redis:
      host: localhost
      port: 6379

jwt:
  secret: your_production_jwt_secret_key_here
  expiration: 28800

logging:
  file:
    name: /opt/apps/dormitory-management/logs/application.log
EOF
```

### 4. 运行应用
```bash
# 复制 JAR 文件
sudo cp /tmp/dormitory-management-1.0.0.jar /opt/apps/dormitory-management/dormitory-management.jar
cd /opt/apps/dormitory-management

# 运行应用
nohup java -jar -Dspring.profiles.active=prod dormitory-management.jar > logs/startup.log 2>&1 &

# 查看日志
tail -f logs/startup.log
```

### 5. 创建系统服务（可选但推荐）
```bash
# 创建 systemd 服务
sudo tee /etc/systemd/system/dormitory-backend.service << 'EOF'
[Unit]
Description=Dormitory Management Backend Service
After=network.target mysql.service redis.service

[Service]
Type=simple
WorkingDirectory=/opt/apps/dormitory-management
ExecStart=/usr/bin/java -jar -Dspring.profiles.active=prod dormitory-management.jar
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
EOF

# 启用并启动服务
sudo systemctl daemon-reload
sudo systemctl enable dormitory-backend
sudo systemctl start dormitory-backend
```

## 🔍 最终验证

### 1. 检查应用健康状态
```bash
# 健康检查
curl http://localhost:8080/api/actuator/health

# 通过 Nginx 访问
curl http://localhost/api/actuator/health
```

### 2. 查看完整的服务状态
```bash
# 检查所有服务
sudo systemctl status mysqld redis nginx dormitory-backend

# 检查端口监听
sudo netstat -tlnp | grep -E ":(80|443|3306|6379|8080)"
```

### 3. 环境应该看起来像这样：
```bash
✅ Nginx (80) → 反向代理 → Spring Boot (8080)
✅ MySQL (3306) → 数据库服务
✅ Redis (6379) → 缓存服务
✅ 应用日志 → /opt/apps/dormitory-management/logs/
```

## 🎉 完成！

现在你的 CentOS 服务器应该有完整的架构：

```
✅ CentOS 服务器基础环境
✅ Java 17 运行环境
✅ MySQL 8.0 数据库 (3306)
✅ Redis 缓存 (6379)
✅ Nginx 反向代理 (80/443)
✅ Spring Boot 应用 (8080)
✅ 监控和日志系统
```

## 🔧 故障排除

### 如果 MySQL 连接失败
```bash
sudo systemctl status mysqld
sudo journalctl -u mysqld -f
```

### 如果 Redis 连接失败
```bash
sudo systemctl status redis
redis-cli ping
```

### 如果 Nginx 配置错误
```bash
sudo nginx -t
sudo tail -f /var/log/nginx/error.log
```

### 如果应用启动失败
```bash
# 查看启动日志
tail -f /opt/apps/dormitory-management/logs/application.log

# 查看服务状态
sudo systemctl status dormitory-backend
sudo journalctl -u dormitory-backend -f
```

## 📞 下一步

1. **数据库初始化**：执行完整的数据库初始化脚本
2. **SSL 证书**：配置 HTTPS
3. **前端部署**：部署前端到 EdgeOne
4. **监控配置**：设置监控和告警

你的环境现在已经准备好了！🎉