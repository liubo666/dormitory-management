# API代理配置指南

## 🔗 代理架构

```
浏览器 → 前端服务器 (3001) → 后端服务器 (8080)
        静态资源        API请求代理
```

## 🚀 当前配置

### 前端代理服务器
- **地址**: http://localhost:3001
- **后端目标**: http://localhost:8080
- **代理规则**: `/api/*` → `http://localhost:8080/*`

### 请求映射

| 前端请求 | 代理到后端 | 说明 |
|----------|------------|------|
| `POST /api/user/login` | `POST http://localhost:8080/user/login` | 用户登录 |
| `GET /api/user/info` | `GET http://localhost:8080/user/info` | 用户信息 |
| `GET /api/statistics/*` | `GET http://localhost:8080/statistics/*` | 统计数据 |
| `POST /api/user/logout` | `POST http://localhost:8080/user/logout` | 用户登出 |

## 🛠️ 启动完整测试环境

### 1. 启动后端服务

```bash
cd backend
# 开发模式
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# 或生产模式
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

后端将在 http://localhost:8080 启动

### 2. 启动前端代理服务器

```bash
cd frontend
node test-production-with-proxy.js
```

前端将在 http://localhost:3001 启动

### 3. 测试API连接

```bash
cd frontend
node test-api.js
```

## 🔍 测试验证

### 基础测试
1. **前端页面**: http://localhost:3001
   - ✅ 页面正常加载
   - ✅ 静态资源正常
   - ✅ Logo和样式正确

2. **API代理**: 浏览器开发者工具Network标签
   - ✅ `/api/*` 请求被代理到后端
   - ✅ 响应状态码正常
   - ✅ CORS头正确设置

### API测试用例

```bash
# 登录接口
curl -X POST http://localhost:3001/api/user/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# 获取统计数据
curl http://localhost:3001/api/statistics/overall

# 健康检查
curl http://localhost:3001/api/health
```

## 🔧 生产环境配置

### EdgeOne API代理配置

```json
{
  "edgeone": {
    "apiProxy": {
      "enabled": true,
      "backendUrl": "https://api.yourdomain.com",
      "pathMapping": {
        "/api/*": "https://api.yourdomain.com/api/*"
      },
      "headers": {
        "X-Forwarded-For": "$client_ip",
        "X-Forwarded-Proto": "$scheme",
        "X-Real-IP": "$client_ip"
      }
    }
  }
}
```

### Nginx API代理配置

```nginx
server {
    listen 80;
    server_name yourdomain.com;

    # 前端静态文件
    location / {
        root /var/www/dormitory-management/dist;
        try_files $uri $uri/ /index.html;
    }

    # API代理
    location /api/ {
        proxy_pass http://localhost:8080/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;

        # CORS设置
        add_header Access-Control-Allow-Origin *;
        add_header Access-Control-Allow-Methods "GET, POST, PUT, DELETE, OPTIONS";
        add_header Access-Control-Allow-Headers "Content-Type, Authorization";

        # 处理预检请求
        if ($request_method = 'OPTIONS') {
            return 204;
        }
    }
}
```

## 🐛 故障排除

### 常见问题

1. **502 Bad Gateway**
   - **原因**: 后端服务未启动
   - **解决**: 启动后端服务 `cd backend && mvn spring-boot:run`

2. **CORS错误**
   - **原因**: 后端未配置CORS
   - **解决**: 在后端SecurityConfig中添加CORS配置

3. **请求路径错误**
   - **原因**: 代理路径配置错误
   - **检查**: 确认后端的 `server.servlet.context-path` 配置

4. **超时错误**
   - **原因**: 后端响应慢或连接超时
   - **解决**: 增加代理超时时间

### 调试技巧

1. **查看代理日志**:
   ```bash
   # 代理服务器会显示所有请求日志
   🔄 API代理: POST /api/user/login -> http://localhost:8080/user/login [200]
   ```

2. **使用浏览器开发者工具**:
   - Network标签查看请求详情
   - Console标签查看JavaScript错误

3. **直接测试后端**:
   ```bash
   curl http://localhost:8080/api/user/login
   ```

## 📋 完整部署检查清单

- [ ] 后端服务正常启动 (端口8080)
- [ ] 前端代理服务器正常启动 (端口3001)
- [ ] API代理配置正确
- [ ] 静态资源正常加载
- [ ] 登录功能正常工作
- [ ] 统计数据显示正常
- [ ] 无CORS错误
- [ ] 控制台无JavaScript错误

## 🔄 开发环境切换

### 开发模式 (使用Vite代理)
```bash
cd frontend
npm run dev  # 使用vite.config.ts中的proxy配置
```

### 生产模式测试 (使用Node.js代理)
```bash
cd frontend
npm run build-only
node test-production-with-proxy.js
```

## 📞 技术支持

如果遇到问题：
1. 检查后端服务是否启动
2. 查看代理服务器日志
3. 验证API请求格式
4. 确认端口是否被占用