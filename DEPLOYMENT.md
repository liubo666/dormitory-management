# 宿舍管理系统 Docker 部署指南

## 系统架构

本系统采用前后端分离架构，使用Docker容器化部署：

- **前端**: Vue 3 + Element Plus + Nginx
- **后端**: Spring Boot 3.1.5 + MySQL 8.0 + Redis 7
- **部署环境**: CentOS 7/8 + Docker + Docker Compose

## 部署环境要求

### 系统要求
- CentOS 7/8 或其他Linux发行版
- CPU: 2核心以上
- 内存: 4GB以上
- 磁盘: 20GB以上可用空间

### 软件要求
- Docker 20.10+
- Docker Compose 2.0+
- Git (用于克隆代码)

## 快速部署

### 1. 准备工作

#### 1.1 安装Docker (CentOS)
```bash
# 卸载旧版本
sudo yum remove docker docker-client docker-client-latest docker-common docker-latest docker-latest-logrotate docker-logrotate docker-engine

# 安装依赖
sudo yum install -y yum-utils device-mapper-persistent-data lvm2

# 添加Docker仓库
sudo yum-config-manager --add-repo https://download.docker.com/linux/centos/docker-ce.repo

# 安装Docker
sudo yum install -y docker-ce docker-ce-cli containerd.io docker-compose-plugin

# 启动Docker
sudo systemctl start docker
sudo systemctl enable docker

# 将当前用户添加到docker组
sudo usermod -aG docker $USER
```

#### 1.2 安装Docker Compose
```bash
# 下载Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose

# 添加执行权限
sudo chmod +x /usr/local/bin/docker-compose

# 验证安装
docker-compose --version
```

### 2. 部署步骤

#### 2.1 克隆代码
```bash
# 克隆项目代码到本地
git clone <your-repository-url>
cd dormitory-management
```

#### 2.2 运行部署脚本
```bash
# 确保脚本有执行权限
chmod +x deploy.sh

# 运行部署脚本
./deploy.sh
```

部署脚本会自动完成以下操作：
- 创建部署目录结构
- 复制项目文件
- 创建必要的配置文件
- 构建Docker镜像
- 启动所有服务容器

#### 2.3 验证部署
部署完成后，访问以下地址验证：
- 前端应用: http://localhost
- 后端API: http://localhost:8080/api/actuator/health
- API文档: http://localhost:8080/api/doc.html

## 目录结构

部署完成后的目录结构：
```
/home/liubo/prod/dormitory-management/
├── backend/                 # 后端应用文件
│   ├── src/                # 源代码
│   ├── Dockerfile          # 后端Docker文件
│   └── logs/               # 后端日志目录
├── frontend/               # 前端应用文件
│   ├── src/                # 源代码
│   ├── Dockerfile          # 前端Docker文件
│   ├── nginx.conf          # Nginx配置
│   └── logs/               # 前端日志目录
├── mysql/                  # MySQL相关文件
│   └── init/               # 数据库初始化脚本
├── scripts/                # 管理脚本
│   ├── start.sh           # 启动服务
│   ├── stop.sh            # 停止服务
│   ├── restart.sh         # 重启服务
│   ├── logs.sh            # 查看日志
│   └── backup.sh          # 数据备份
├── backup/                 # 备份目录
│   ├── mysql/             # MySQL备份
│   └── logs/              # 日志备份
├── docker-compose.yml      # Docker编排文件
└── .env                   # 环境变量配置
```

## 服务管理

### 基本操作

#### 启动服务
```bash
cd /home/liubo/prod/dormitory-management/scripts
./start.sh
```

#### 停止服务
```bash
cd /home/liubo/prod/dormitory-management/scripts
./stop.sh
```

#### 重启服务
```bash
cd /home/liubo/prod/dormitory-management/scripts
./restart.sh
```

#### 查看服务状态
```bash
cd /home/liubo/prod/dormitory-management
docker-compose ps
```

### 日志管理

#### 查看所有服务日志
```bash
cd /home/liubo/prod/dormitory-management
docker-compose logs -f
```

#### 查看特定服务日志
```bash
cd /home/liubo/prod/dormitory-management/scripts
./logs.sh backend    # 查看后端日志
./logs.sh frontend   # 查看前端日志
./logs.sh mysql      # 查看MySQL日志
./logs.sh redis      # 查看Redis日志
```

### 数据备份

#### 自动备份
```bash
cd /home/liubo/prod/dormitory-management/scripts
./backup.sh
```

#### 手动备份MySQL数据
```bash
# 备份到文件
docker exec dormitory-mysql mysqldump -u root -pRoot123! dormitory_management > backup.sql

# 恢复数据
docker exec -i dormitory-mysql mysql -u root -pRoot123! dormitory_management < backup.sql
```

## 配置说明

### 环境变量配置

编辑 `.env` 文件修改配置：
```bash
# 数据库配置
MYSQL_ROOT_PASSWORD=Root123!          # MySQL root密码
MYSQL_DATABASE=dormitory_management   # 数据库名
MYSQL_USER=dormitory                  # 应用用户
MYSQL_PASSWORD=Dormitory123!         # 应用用户密码

# 端口配置
BACKEND_PORT=8080                     # 后端端口
FRONTEND_PORT=80                      # 前端端口
MYSQL_PORT=3306                       # MySQL端口
REDIS_PORT=6379                       # Redis端口
```

### 应用配置

#### 后端配置
- 配置文件: `backend/src/main/resources/application-docker.yml`
- 日志目录: `/home/liubo/prod/dormitory-management/backend/logs`
- API地址: `http://localhost:8080/api`

#### 前端配置
- Nginx配置: `frontend/nginx.conf`
- 访问地址: `http://localhost`

## 故障排除

### 常见问题

#### 1. 端口冲突
如果端口被占用，修改 `docker-compose.yml` 中的端口映射：
```yaml
ports:
  - "8081:8080"  # 将主机端口改为8081
```

#### 2. 数据库连接失败
检查MySQL容器状态和网络连接：
```bash
# 查看MySQL容器状态
docker-compose ps mysql

# 测试数据库连接
docker exec -it dormitory-mysql mysql -u dormitory -pDormitory123! -e "SELECT 1"
```

#### 3. 前端无法访问后端
检查网络配置和容器间通信：
```bash
# 查看网络
docker network ls

# 测试后端API
docker exec dormitory-frontend curl http://backend:8080/api/actuator/health
```

#### 4. 内存不足
如果服务器内存不足，可以调整JVM参数：
```yaml
# 在docker-compose.yml中添加
environment:
  JAVA_OPTS: "-Xms256m -Xmx512m"
```

### 日志查看

#### 系统日志
```bash
# Docker服务日志
sudo journalctl -u docker.service

# 应用日志
tail -f /home/liubo/prod/dormitory-management/backend/logs/application.log
```

#### 容器日志
```bash
# 查看所有容器日志
docker-compose logs

# 查看特定容器日志
docker-compose logs backend
docker-compose logs frontend
```

## 安全建议

1. **修改默认密码**: 修改MySQL root密码和应用密码
2. **防火墙配置**: 只开放必要的端口（80, 443, 22）
3. **定期备份**: 设置定时任务自动备份数据
4. **监控告警**: 配置服务监控和告警
5. **更新维护**: 定期更新Docker镜像和系统补丁

## 维护操作

### 清理Docker资源
```bash
# 清理未使用的镜像
docker image prune -f

# 清理未使用的容器
docker container prune -f

# 清理未使用的卷
docker volume prune -f
```

### 更新应用
```bash
# 拉取最新代码
git pull

# 重新构建和部署
cd /home/liubo/prod/dormitory-management
docker-compose down
docker-compose build --no-cache
docker-compose up -d
```

### 性能监控
```bash
# 查看容器资源使用情况
docker stats

# 查看磁盘使用情况
df -h

# 查看内存使用情况
free -h
```

## 联系支持

如果在部署过程中遇到问题，请：
1. 查看相关日志文件
2. 检查系统资源使用情况
3. 参考故障排除章节
4. 联系技术支持团队