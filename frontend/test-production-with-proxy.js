const http = require('http');
const https = require('https');
const fs = require('fs');
const path = require('path');
const url = require('url');

const PORT = 3001;
const DIST_DIR = path.join(__dirname, 'dist');
const BACKEND_URL = 'http://localhost:8080'; // 后端服务地址

// MIME 类型映射
const mimeTypes = {
  '.html': 'text/html',
  '.js': 'application/javascript',
  '.css': 'text/css',
  '.json': 'application/json',
  '.png': 'image/png',
  '.jpg': 'image/jpg',
  '.jpeg': 'image/jpeg',
  '.gif': 'image/gif',
  '.svg': 'image/svg+xml',
  '.ico': 'image/x-icon',
  '.woff': 'font/woff',
  '.woff2': 'font/woff2',
  '.ttf': 'font/ttf',
  '.eot': 'application/vnd.ms-fontobject'
};

// 创建HTTP代理请求
function proxyRequest(req, res, targetUrl) {
  const parsedUrl = url.parse(targetUrl);
  const options = {
    hostname: parsedUrl.hostname,
    port: parsedUrl.port || (parsedUrl.protocol === 'https:' ? 443 : 80),
    path: parsedUrl.path,
    method: req.method,
    headers: {
      ...req.headers,
      // 修改host头指向目标服务器
      'host': parsedUrl.hostname,
      // 添加代理标识头
      'x-forwarded-for': req.socket.remoteAddress,
      'x-forwarded-proto': 'http',
      'x-forwarded-host': req.headers.host || `localhost:${PORT}`,
    }
  };

  // 删除一些不应该转发的头
  delete options.headers['content-length'];

  const proxyReq = (parsedUrl.protocol === 'https:' ? https : http).request(options, (proxyRes) => {
    // 转发响应头
    res.writeHead(proxyRes.statusCode, proxyRes.headers);

    // 转发响应体
    proxyRes.pipe(res);

    console.log(`🔄 API代理: ${req.method} ${req.url} -> ${targetUrl} [${proxyRes.statusCode}]`);
  });

  proxyReq.on('error', (err) => {
    console.error(`❌ 代理请求失败: ${req.url} -> ${targetUrl}`, err.message);
    res.writeHead(502, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify({
      error: 'Bad Gateway',
      message: '后端服务不可用',
      details: err.message
    }));
  });

  // 转发请求体（如果有）
  req.pipe(proxyReq);
}

const server = http.createServer((req, res) => {
  const parsedUrl = url.parse(req.url);
  let pathname = parsedUrl.pathname;

  console.log(`请求: ${req.method} ${pathname}`);

  // API代理处理
  if (pathname.startsWith('/api/')) {
    // 保持 /api 前缀，因为后端配置了 context-path: /api
    const queryString = parsedUrl.search ? parsedUrl.search : '';
    const targetUrl = `${BACKEND_URL}${pathname}${queryString}`;

    proxyRequest(req, res, targetUrl);
    return;
  }

  // 默认路由到 index.html
  if (pathname === '/') {
    pathname = '/index.html';
  }

  // 构建文件路径
  const filePath = path.join(DIST_DIR, pathname);

  // 检查文件是否存在
  fs.access(filePath, fs.constants.F_OK, (err) => {
    if (err) {
      // 文件不存在，返回 404 或 index.html (用于 SPA 路由)
      if (pathname.startsWith('/assets/')) {
        // 静态资源不存在，返回 404
        res.writeHead(404, { 'Content-Type': 'text/html; charset=utf-8' });
        res.end(`
          <!DOCTYPE html>
          <html>
          <head>
            <meta charset="UTF-8">
            <title>404 - 资源未找到</title>
          </head>
          <body>
            <h1>404 - 资源未找到</h1>
            <p>请求的文件不存在: ${pathname}</p>
            <a href="/">返回首页</a>
          </body>
          </html>
        `);
      } else {
        // SPA 路由，返回 index.html
        const indexPath = path.join(DIST_DIR, 'index.html');
        fs.readFile(indexPath, 'utf8', (err, content) => {
          if (err) {
            res.writeHead(500, { 'Content-Type': 'text/html; charset=utf-8' });
            res.end('服务器内部错误');
            return;
          }
          res.writeHead(200, { 'Content-Type': 'text/html; charset=utf-8' });
          res.end(content);
        });
      }
      return;
    }

    // 读取文件
    fs.readFile(filePath, (err, content) => {
      if (err) {
        res.writeHead(500, { 'Content-Type': 'text/html; charset=utf-8' });
        res.end('服务器内部错误');
        return;
      }

      // 获取文件扩展名
      const ext = path.extname(filePath);
      const contentType = mimeTypes[ext] || 'application/octet-stream';

      // 设置响应头
      res.writeHead(200, {
        'Content-Type': `${contentType}${ext === '.html' ? '; charset=utf-8' : ''}`,
        'Cache-Control': 'no-cache', // 测试时不缓存
        'Access-Control-Allow-Origin': '*',
        'Access-Control-Allow-Methods': 'GET, POST, PUT, DELETE, OPTIONS',
        'Access-Control-Allow-Headers': 'Content-Type, Authorization'
      });

      res.end(content);
    });
  });
});

// 处理 OPTIONS 预检请求
server.on('request', (req, res) => {
  if (req.method === 'OPTIONS') {
    res.writeHead(200, {
      'Access-Control-Allow-Origin': '*',
      'Access-Control-Allow-Methods': 'GET, POST, PUT, DELETE, OPTIONS',
      'Access-Control-Allow-Headers': 'Content-Type, Authorization',
      'Access-Control-Max-Age': '86400'
    });
    res.end();
  }
});

server.listen(PORT, () => {
  console.log(`
========================================
🚀 生产环境测试服务器已启动 (含API代理)
========================================
📱 前端地址: http://localhost:${PORT}
🔗 后端地址: ${BACKEND_URL}
📁 服务目录: ${DIST_DIR}
⚡ 状态: 运行中
🛠️  功能:
   - ✅ 静态资源服务
   - ✅ SPA 路由支持
   - ✅ CORS 支持
   - ✅ 缓存禁用
   - ✅ API 代理 (/api/* -> ${BACKEND_URL})
========================================
🧪 API测试:
   POST http://localhost:${PORT}/api/user/login
   GET  http://localhost:${PORT}/api/statistics/overall

按 Ctrl+C 停止服务器
  `);
});

// 优雅关闭
process.on('SIGINT', () => {
  console.log('\n🛑 正在关闭服务器...');
  server.close(() => {
    console.log('✅ 服务器已关闭');
    process.exit(0);
  });
});