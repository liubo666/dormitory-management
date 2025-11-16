#!/usr/bin/env python3
"""
本地生产环境测试服务器
使用 Python 的 http.server 模块模拟静态网站托管
"""

import http.server
import socketserver
import os
import sys
from pathlib import Path

# 设置端口和目录
PORT = 3001
DIST_DIR = Path(__file__).parent / "dist"

class ProductionHTTPRequestHandler(http.server.SimpleHTTPRequestHandler):
    def __init__(self, *args, **kwargs):
        super().__init__(*args, directory=DIST_DIR, **kwargs)

    def end_headers(self):
        # 添加安全头和缓存控制
        self.send_header('Cache-Control', 'no-cache, no-store, must-revalidate')
        self.send_header('Pragma', 'no-cache')
        self.send_header('Expires', '0')
        self.send_header('Access-Control-Allow-Origin', '*')
        self.send_header('Access-Control-Allow-Methods', 'GET, POST, PUT, DELETE, OPTIONS')
        self.send_header('Access-Control-Allow-Headers', 'Content-Type, Authorization')

        # 如果是HTML文件，添加字符集
        if self.path.endswith('.html') or self.path == '/':
            self.send_header('Content-Type', 'text/html; charset=utf-8')

        super().end_headers()

    def do_GET(self):
        # 处理 SPA 路由
        if self.path == '/':
            self.path = '/index.html'
        elif not self.path.startswith('/assets/') and not any(
            self.path.endswith(ext) for ext in ['.html', '.css', '.js', '.json', '.png', '.jpg', '.jpeg', '.gif', '.svg', '.ico', '.woff', '.woff2', '.ttf']
        ):
            # 对于非静态资源的路径，返回 index.html（SPA 路由支持）
            self.path = '/index.html'

        try:
            super().do_GET()
        except FileNotFoundError:
            # 文件未找到，返回 404 页面
            self.send_error(404, f"文件未找到: {self.path}")

    def log_message(self, format, *args):
        """自定义日志格式"""
        print(f"📝 {self.address_string()} - {self.command} {self.path} - {format%args}")

def main():
    # 检查 dist 目录是否存在
    if not DIST_DIR.exists():
        print("❌ 错误: dist 目录不存在！")
        print("请先运行: npm run build")
        sys.exit(1)

    # 检查 index.html 是否存在
    if not (DIST_DIR / "index.html").exists():
        print("❌ 错误: dist/index.html 不存在！")
        print("请先运行: npm run build")
        sys.exit(1)

    print("""
========================================
🚀 生产环境测试服务器已启动
========================================
📱 访问地址: http://localhost:{}
📁 服务目录: {}
⚡ 状态: 运行中
🛠️  测试功能:
   - ✅ 静态资源服务
   - ✅ SPA 路由支持
   - ✅ CORS 支持
   - ✅ 缓存禁用
========================================
按 Ctrl+C 停止服务器
    """.format(PORT, DIST_DIR.absolute()))

    try:
        with socketserver.TCPServer(("", PORT), ProductionHTTPRequestHandler) as httpd:
            httpd.serve_forever()
    except KeyboardInterrupt:
        print("\n🛑 正在关闭服务器...")
        print("✅ 服务器已关闭")
    except OSError as e:
        if e.errno == 48:  # Address already in use
            print(f"❌ 错误: 端口 {PORT} 已被占用！")
            print("请关闭使用该端口的程序或更改端口号")
        else:
            print(f"❌ 服务器启动失败: {e}")
        sys.exit(1)

if __name__ == "__main__":
    main()