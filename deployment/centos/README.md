# 宿舍管理系统后端 CentOS 部署指南

## 📋 目录

- [系统要求](#系统要求)
- [部署架构](#部署架构)
- [快速开始](#快速开始)
- [详细步骤](#详细步骤)
- [配置说明](#配置说明)
- [监控和维护](#监控和维护)
- [故障排除](#故障排除)
- [安全建议](#安全建议)

## 🎯 系统要求

### 硬件要求
- **CPU**: 2核心以上 (推荐 4核心)
- **内存**: 4GB 以上 (推荐 8GB)
- **存储**: 50GB 以上可用空间
- **网络**: 稳定的互联网连接

### 软件要求
- **操作系统**: CentOS 7.x / 8.x 或 RHEL 7.x / 8.x
- **权限**: root 权限或 sudo 权限
- **Java**: OpenJDK 17 (脚本会自动安装)
- **数据库**: MySQL 8.0 (脚本会自动安装)
- **缓存**: Redis 6.0+ (脚本会自动安装)
- **Web服务器**: Nginx (脚本会自动安装)

## 🏗️ 部署架构

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

## 🚀 快速开始

### 1. 准备工作

```bash
# 下载部署文件
wget https://your-domain.com/deployment/centos/install-environment.sh
wget https://your-domain.com/deployment/centos/deploy-centos.sh
wget https://your-domain.com/deployment/centos/init-database.sql

# 设置执行权限
chmod +x install-environment.sh
chmod +x deploy-centos.sh
```

### 2. 一键部署

```bash
# 步骤1: 安装环境依赖
sudo ./install-environment.sh

# 步骤2: 构建应用 JAR (在本地开发环境)
cd backend
mvn clean package -DskipTests
cp target/dormitory-management-1.0.0.jar /tmp/

# 步骤3: 上传 JAR 文件到服务器
scp target/dormitory-management-1.0.0.jar root@your-server:/tmp/

# 步骤4: 部署应用 (在服务器上)
sudo ./deploy-centos.sh /tmp/dormitory-management-1.0.0.jar
```

### 3. 数据库初始化

```bash
# 登录 MySQL
mysql -u root -p

# 执行初始化脚本
source /path/to/init-database.sql
```

## 📝 详细步骤

### 第一步：环境准备

#### 1.1 系统更新

```bash
# 更新系统软件包
sudo yum update -y
sudo yum install -y epel-release
```

#### 1.2 运行环境安装脚本

```bash
# 运行自动安装脚本
sudo ./install-environment.sh
```

脚本将自动安装：
- Java 17 OpenJDK
- MySQL 8.0
- Redis 6.0+
- Nginx
- Maven
- 系统监控工具

### 第二步：数据库配置

#### 2.1 MySQL 安全配置

```bash
# 登录 MySQL (使用临时密码)
mysql -u root -p

# 修改 root 密码
ALTER USER 'root'@'localhost' IDENTIFIED BY 'YourSecurePassword123!';

# 创建应用用户
CREATE USER 'dormitory_user'@'localhost' IDENTIFIED BY 'your_secure_db_password';
CREATE USER 'dormitory_user'@'%' IDENTIFIED BY 'your_secure_db_password';

# 授权
GRANT ALL PRIVILEGES ON *.* TO 'dormitory_user'@'localhost';
GRANT ALL PRIVILEGES ON *.* TO 'dormitory_user'@'%';
FLUSH PRIVILEGES;
```

#### 2.2 数据库初始化

```bash
# 执行数据库初始化脚本
mysql -u root -p < init-database.sql
```

#### 2.3 Redis 配置

```bash
# 编辑 Redis 配置
sudo vim /etc/redis.conf

# 修改以下配置：
# requirepass your_redis_password
# bind 127.0.0.1
# daemonize yes

# 重启 Redis
sudo systemctl restart redis
```

### 第三步：应用部署

#### 3.1 构建 JAR 包

在本地开发环境执行：

```bash
# 进入后端项目目录
cd backend

# 清理并打包
mvn clean package -DskipTests

# JAR 文件位置
ls -la target/dormitory-management-1.0.0.jar
```

#### 3.2 上传文件到服务器

```bash
# 使用 SCP 上传 JAR 文件
scp target/dormitory-management-1.0.0.jar root@your-server:/tmp/
```

#### 3.3 运行部署脚本

```bash
# 在服务器上执行部署
sudo ./deploy-centos.sh /tmp/dormitory-management-1.0.0.jar
```

### 第四步：配置文件调整

#### 4.1 环境变量配置

编辑 `/opt/apps/dormitory-management/config/.env`：

```bash
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
```

#### 4.2 应用配置

检查 `/opt/apps/dormitory-management/config/application-prod.yml` 配置是否正确。

### 第五步：Nginx 配置

#### 5.1 创建 Nginx 配置文件

```bash
sudo vim /etc/nginx/conf.d/dormitory-backend.conf
```

配置内容见 `deployment/centos/deploy-centos.sh` 脚本中的 Nginx 配置部分。

#### 5.2 测试并重载 Nginx

```bash
# 测试配置
sudo nginx -t

# 重载配置
sudo nginx -s reload
```

### 第六步：服务启动和验证

#### 6.1 启动应用服务

```bash
# 启动服务
sudo systemctl start dormitory-backend

# 设置开机自启
sudo systemctl enable dormitory-backend

# 查看状态
sudo systemctl status dormitory-backend
```

#### 6.2 健康检查

```bash
# 检查端口监听
sudo netstat -tlnp | grep 8080

# 测试 HTTP 响应
curl -f http://localhost:8080/api/actuator/health

# 查看日志
sudo journalctl -u dormitory-backend -f
```

## ⚙️ 配置说明

### JVM 参数

在 systemd 服务文件中配置的 JVM 参数：

```bash
JAVA_OPTS=-Xms512m -Xmx1024m -XX:+UseG1GC -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0
```

### 数据库连接池配置

```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      idle-timeout: 300000
      connection-timeout: 20000
      max-lifetime: 1200000
```

### Redis 连接配置

```yaml
spring:
  data:
    redis:
      lettuce:
        pool:
          max-active: 20
          max-wait: -1ms
          max-idle: 10
          min-idle: 5
```

## 📊 监控和维护

### 服务管理

```bash
# 启动服务
sudo systemctl start dormitory-backend

# 停止服务
sudo systemctl stop dormitory-backend

# 重启服务
sudo systemctl restart dormitory-backend

# 查看状态
sudo systemctl status dormitory-backend

# 查看日志
sudo journalctl -u dormitory-backend -f
```

### 日志管理

```bash
# 应用日志位置
tail -f /opt/apps/dormitory-management/logs/application.log

# Nginx 访问日志
tail -f /var/log/nginx/dormitory-backend-access.log

# Nginx 错误日志
tail -f /var/log/nginx/dormitory-backend-error.log
```

### 数据备份

#### 数据库备份脚本

```bash
#!/bin/bash
# backup-database.sh

BACKUP_DIR="/opt/apps/dormitory-management/backup"
DATE=$(date +%Y%m%d_%H%M%S)
DB_NAME="dormitory_management"

# 创建备份目录
mkdir -p $BACKUP_DIR

# 备份数据库
mysqldump -u dormitory_user -p $DB_NAME > $BACKUP_DIR/db_backup_$DATE.sql

# 压缩备份文件
gzip $BACKUP_DIR/db_backup_$DATE.sql

# 删除 7 天前的备份
find $BACKUP_DIR -name "db_backup_*.sql.gz" -mtime +7 -delete

echo "数据库备份完成: $BACKUP_DIR/db_backup_$DATE.sql.gz"
```

#### 应用备份脚本

```bash
#!/bin/bash
# backup-application.sh

BACKUP_DIR="/opt/apps/dormitory-management/backup"
DATE=$(date +%Y%m%d_%H%M%S)
APP_DIR="/opt/apps/dormitory-management"

# 创建备份目录
mkdir -p $BACKUP_DIR

# 备份配置文件
tar -czf $BACKUP_DIR/config_backup_$DATE.tar.gz -C $APP_DIR config/

# 删除 30 天前的备份
find $BACKUP_DIR -name "*_backup_*.tar.gz" -mtime +30 -delete

echo "应用备份完成: $BACKUP_DIR/config_backup_$DATE.tar.gz"
```

### 性能监控

#### 系统资源监控

```bash
# CPU 和内存使用
htop

# 磁盘使用
df -h

# 网络连接
netstat -tlnp

# 系统负载
uptime
```

#### 应用性能监控

应用内置了 Spring Boot Actuator，可通过以下端点监控：

- `/api/actuator/health` - 健康检查
- `/api/actuator/info` - 应用信息
- `/api/actuator/metrics` - 性能指标

## 🔧 故障排除

### 常见问题

#### 1. 服务启动失败

```bash
# 查看详细错误日志
sudo journalctl -u dormitory-backend -n 50

# 检查端口占用
sudo netstat -tlnp | grep 8080

# 检查 Java 进程
ps aux | grep java
```

#### 2. 数据库连接失败

```bash
# 检查 MySQL 服务状态
sudo systemctl status mysqld

# 测试数据库连接
mysql -u dormitory_user -p -h localhost dormitory_management

# 检查防火墙
sudo firewall-cmd --list-ports
```

#### 3. Redis 连接失败

```bash
# 检查 Redis 服务状态
sudo systemctl status redis

# 测试 Redis 连接
redis-cli -a your_redis_password ping

# 检查 Redis 配置
sudo grep -v "^#" /etc/redis.conf | grep -v "^$"
```

#### 4. Nginx 配置错误

```bash
# 测试 Nginx 配置
sudo nginx -t

# 查看 Nginx 错误日志
sudo tail -f /var/log/nginx/error.log

# 重载 Nginx 配置
sudo nginx -s reload
```

### 日志分析

#### 应用日志分析

```bash
# 查看最近的错误
grep -i error /opt/apps/dormitory-management/logs/application.log | tail -20

# 统计 HTTP 状态码
grep "HTTP" /opt/apps/dormitory-management/logs/application.log | awk '{print $NF}' | sort | uniq -c

# 查看慢查询
grep -i "slow" /opt/apps/dormitory-management/logs/application.log
```

#### 数据库慢查询

```bash
# 查看 MySQL 慢查询日志
sudo tail -f /var/log/mysql/mysql-slow.log

# 分析慢查询
mysqldumpslow /var/log/mysql/mysql-slow.log
```

## 🔒 安全建议

### 1. 数据库安全

- 定期更改数据库密码
- 限制数据库访问 IP
- 启用数据库审计日志
- 定期备份数据库

### 2. 应用安全

- 使用强 JWT 密钥
- 定期更新依赖库
- 启用 HTTPS
- 配置安全头

### 3. 系统安全

- 定期更新系统补丁
- 配置防火墙规则
- 禁用不必要的服务
- 使用非 root 用户运行应用

### 4. 网络安全

```bash
# 限制数据库端口访问
sudo firewall-cmd --permanent --remove-port=3306/tcp
sudo firewall-cmd --reload

# 只允许特定 IP 访问管理端口
sudo firewall-cmd --permanent --add-rich-rule="rule family='ipv4' source address='192.168.1.0/24' port protocol='tcp' port='8080' accept"
```

## 📱 移动端和 API 访问

### API 基础 URL

```
生产环境: https://your-api-domain.com/api
测试环境: http://your-server-ip:8080/api
```

### 主要 API 端点

```
POST /api/user/login          # 用户登录
GET  /api/statistics/overall  # 统计信息
GET  /api/students/page       # 学生列表
POST /api/checkin/page       # 入住记录
GET  /api/fees/page          # 费用列表
```

### 前端配置

更新前端 `.env.production` 文件：

```env
VITE_API_BASE_URL=https://your-api-domain.com/api
VITE_APP_TITLE=宿舍管理系统
```

## 🔄 更新和维护

### 应用更新流程

1. **备份当前版本**
   ```bash
   sudo systemctl stop dormitory-backend
   sudo cp /opt/apps/dormitory-management/dormitory-management-1.0.0.jar \
          /opt/apps/dormitory-management/backup/
   ```

2. **部署新版本**
   ```bash
   sudo ./deploy-centos.sh /tmp/new-version.jar
   ```

3. **验证更新**
   ```bash
   curl -f http://localhost:8080/api/actuator/health
   ```

### 定期维护任务

```bash
# 每日任务 (cron)
0 2 * * * /opt/apps/dormitory-management/scripts/backup-database.sh
0 3 * * 0 /opt/apps/dormitory-management/scripts/cleanup-logs.sh

# 每周任务 (cron)
0 4 * * 1 /opt/apps/dormitory-management/scripts/backup-application.sh
```

## 📞 技术支持

### 联系方式

- **技术支持**: support@dormitory.com
- **问题反馈**: issues@dormitory.com
- **文档更新**: docs@dormitory.com

### 在线资源

- **项目文档**: https://docs.dormitory.com
- **API 文档**: https://api.dormitory.com/docs
- **问题追踪**: https://github.com/dormitory/issues

---

**版本**: 1.0.0
**更新时间**: 2025-11-16
**维护团队**: Dormitory Management Team