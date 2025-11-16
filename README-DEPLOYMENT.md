# 宿舍管理系统 EdgeOne 部署指南

## 📋 部署概述

本指南详细说明如何将宿舍管理系统部署到腾讯云 EdgeOne 静态网站托管服务。

### 系统架构

```
前端 (Vue 3) → EdgeOne 静态托管 → 用户访问
                      ↓
                API 代理 → 后端服务 (Spring Boot)
```

## 🚀 快速部署

### 1. 环境准备

确保你的开发环境已安装：

```bash
# Node.js (推荐 v18+)
node --version

# npm (或 yarn)
npm --version

# Maven (用于后端构建，可选)
mvn --version
```

### 2. 一键部署

#### Windows 用户
```cmd
deploy.bat
```

#### Linux/macOS 用户
```bash
chmod +x deploy.sh
./deploy.sh
```

部署脚本将自动：
- 构建前端生产版本
- 构建 Spring Boot 后端 (如果 Maven 可用)
- 生成部署配置文件
- 创建部署压缩包

## 📦 手动部署步骤

### 1. 构建前端

```bash
cd frontend
npm install
npm run build-only
```

构建完成后，`frontend/dist` 目录包含所有需要部署的文件。

### 2. 构建后端 (可选)

```bash
cd backend
mvn clean package -DskipTests -Pprod
```

构建完成后，JAR 文件位于 `backend/target/dormitory-management-1.0.0.jar`。

### 3. EdgeOne 配置

#### 3.1 创建静态网站

1. 登录腾讯云控制台
2. 进入 EdgeOne 服务
3. 创建静态网站
4. 上传 `frontend/dist` 目录下的所有文件

#### 3.2 域名配置

1. 在 EdgeOne 控制台添加自定义域名
2. 配置 DNS 解析指向 EdgeOne 提供的 CNAME
3. 启用 HTTPS (EdgeOne 自动提供免费 SSL 证书)

#### 3.3 缓存策略配置

根据 `deployment/edgeone-config.json` 中的缓存配置：

- JS/CSS 文件：缓存 1 年
- 图片文件：缓存 30 天
- HTML 文件：缓存 1 小时

#### 3.4 错误页面

上传自定义错误页面：
- `404.html` - 页面未找到
- `500.html` - 服务器错误

## 🔧 生产环境配置

### 1. 环境变量

在 EdgeOne 或后端服务器中配置以下环境变量：

```bash
# 数据库配置
DB_URL=jdbc:mysql://your-db-host:3306/dormitory_management
DB_USERNAME=your-db-username
DB_PASSWORD=your-db-password

# Redis 配置
REDIS_HOST=your-redis-host
REDIS_PORT=6379
REDIS_PASSWORD=your-redis-password

# JWT 配置
JWT_SECRET=your-production-jwt-secret-key

# 其他配置
KNIFE4J_USERNAME=admin
KNIFE4J_PASSWORD=your-admin-password
```

### 2. 数据库准备

1. 创建数据库：
```sql
CREATE DATABASE dormitory_management CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 执行数据库迁移脚本（如果有）

### 3. 后端服务部署

如果需要独立部署后端服务：

```bash
# 启动后端服务
java -jar dormitory-management-1.0.0.jar --spring.profiles.active=prod
```

## 🎨 Logo 和品牌配置

### 1. Logo 文件位置

将你的 Logo 文件放置在 `frontend/public/` 目录：

- `favicon.ico` - 网站图标 (16x16, 32x32, 48x48 px)
- `logo-placeholder.svg` - 主要 Logo (SVG 格式)
- `apple-touch-icon.png` - Apple 设备图标 (180x180 px)
- `logo-192.png` - PWA 图标 (192x192 px)

### 2. 更新 Logo 引用

Logo 已在以下文件中引用：
- `frontend/src/views/login/index.vue` - 登录页面
- `frontend/src/views/layout/index.vue` - 导航栏

## 🔒 安全配置

### 1. HTTP 安全头

EdgeOne 已配置以下安全头：

```http
X-Frame-Options: DENY
X-Content-Type-Options: nosniff
X-XSS-Protection: 1; mode=block
Strict-Transport-Security: max-age=31536000
Content-Security-Policy: default-src 'self'...
```

### 2. HTTPS 强制重定向

所有 HTTP 请求将自动重定向到 HTTPS。

### 3. 访问限制

配置 IP 访问限制和速率限制以防止恶意访问。

## 📊 监控和日志

### 1. EdgeOne 监控

- 访问日志：保留 30 天
- 错误日志：保留 7 天
- 实时监控：流量、延迟、错误率

### 2. 后端日志

配置生产环境日志级别：
- 应用日志：INFO 级别
- 安全日志：WARN 级别
- 框架日志：WARN 级别

## 🚀 部署验证

部署完成后，进行以下验证：

1. **基本功能**
   - [ ] 首页能正常访问
   - [ ] 登录页面显示正常
   - [ ] Logo 显示正确

2. **功能测试**
   - [ ] 用户登录功能
   - [ ] 主要页面加载
   - [ ] API 接口响应

3. **性能测试**
   - [ ] 页面加载速度
   - [ ] 静态资源缓存
   - [ ] CDN 加速效果

4. **安全测试**
   - [ ] HTTPS 正常工作
   - [ ] 安全头配置
   - [ ] 敏感信息泄露检查

## 🔄 部署更新

### 1. 前端更新

```bash
cd frontend
npm run build-only
# 上传 dist 目录到 EdgeOne
```

### 2. 后端更新

```bash
cd backend
mvn clean package -DskipTests -Pprod
# 停止旧服务，启动新服务
```

### 3. 数据库更新

执行数据库迁移脚本（如果有 schema 变更）。

## 🆘 故障排除

### 常见问题

1. **页面 404**
   - 检查文件是否正确上传
   - 确认 EdgeOne 配置正确

2. **API 请求失败**
   - 检查 API 代理配置
   - 验证后端服务状态

3. **静态资源加载慢**
   - 检查 CDN 缓存配置
   - 优化图片和资源大小

4. **HTTPS 证书问题**
   - 等待证书自动签发
   - 检查域名 DNS 配置

### 技术支持

如遇到部署问题，请检查：
1. EdgeOne 控制台错误日志
2. 浏览器开发者工具
3. 后端应用日志

## 📞 联系方式

- 技术支持：tech-support@example.com
- 项目文档：查看项目 README.md
- 问题反馈：提交 GitHub Issues