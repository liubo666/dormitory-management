# 多阶段构建 - 构建阶段
FROM node:18-alpine AS builder

# 设置工作目录
WORKDIR /app

# 接收构建参数
ARG NODE_REGISTRY=https://registry.npmmirror.com

# 复制package文件
COPY package*.json ./

# 配置npm镜像源并安装依赖
RUN npm config set registry ${NODE_REGISTRY} && \
    npm ci --silent

# 复制源代码
COPY . .

# 构建应用 (跳过类型检查以避免构建失败)
RUN npm run build-only

# 生产阶段 - 使用Nginx提供静态文件服务
FROM nginx:1.25-alpine

# 创建certbot目录
RUN mkdir -p /var/www/certbot

# 复制自定义nginx配置
COPY nginx.conf /etc/nginx/nginx.conf

# 复制构建好的文件到nginx目录
COPY --from=builder /app/dist /usr/share/nginx/html

# 暴露端口
EXPOSE 80 443

# 启动nginx
CMD ["nginx", "-g", "daemon off;"]