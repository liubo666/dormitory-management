# 本地生产环境测试清单

## 🚀 测试服务器信息

- **访问地址**: http://localhost:3001
- **服务状态**: ✅ 运行中
- **根目录**: `frontend/dist`
- **服务器类型**: Node.js 静态文件服务器

## 📋 测试项目

### ✅ 基础功能测试

| 测试项 | 状态 | 说明 |
|--------|------|------|
| 首页访问 (/) | ✅ PASS | 返回 200 OK |
| Logo文件 (/logo-placeholder.svg) | ✅ PASS | 返回 200 OK |
| Favicon (/favicon.ico) | ✅ PASS | 返回 200 OK |
| SPA路由 (/dashboard) | ✅ PASS | 返回 index.html |
| CORS支持 | ✅ PASS | 已设置 Access-Control 头 |

### 🧪 需要手动测试的页面

请在浏览器中访问以下地址进行测试：

#### 1. 主要页面
- [ ] http://localhost:3001/ - 首页
- [ ] http://localhost:3001/login - 登录页面
- [ ] http://localhost:3001/dashboard - 仪表板
- [ ] http://localhost:3001/dormitory - 宿舍管理
- [ ] http://localhost:3001/student - 学生管理
- [ ] http://localhost:3001/fee - 费用管理

#### 2. 静态资源
- [ ] http://localhost:3001/logo-placeholder.svg - Logo文件
- [ ] http://localhost:3001/favicon.ico - 网站图标
- [ ] http://localhost:3001/apple-touch-icon.png - Apple图标
- [ ] http://localhost:3001/site.webmanifest - PWA配置

#### 3. JavaScript和CSS文件
- [ ] http://localhost:3001/assets/index-*.js - 主要JS文件
- [ ] http://localhost:3001/assets/index-*.css - 主要CSS文件
- [ ] http://localhost:3001/assets/element-*.js - Element Plus组件
- [ ] http://localhost:3001/assets/vendor-*.js - Vue生态

### 🔍 检查要点

#### 1. 页面加载
- [ ] 页面能够正常加载，没有404错误
- [ ] 静态资源（CSS、JS、图片）正常显示
- [ ] 浏览器控制台没有错误信息

#### 2. Logo显示
- [ ] 登录页面Logo显示正常
- [ ] 导航栏Logo显示正常
- [ ] 浏览器标签页图标显示正常

#### 3. 响应式设计
- [ ] 桌面端显示正常
- [ ] 移动端适配良好
- [ ] 不同屏幕尺寸下布局正常

#### 4. 功能验证
- [ ] 登录表单可以正常显示
- [ ] 导航菜单可以正常点击
- [ ] Element Plus组件正常渲染

### 🚨 常见问题排查

#### 问题1: 页面白屏
**可能原因**: JavaScript加载失败
**解决方案**:
1. 检查浏览器控制台错误
2. 确认JS文件路径正确
3. 验证压缩文件完整性

#### 问题2: 样式丢失
**可能原因**: CSS文件加载失败
**解决方案**:
1. 检查网络请求状态
2. 确认CSS文件存在
3. 验证文件路径正确

#### 问题3: Logo不显示
**可能原因**: 图片文件路径错误
**解决方案**:
1. 检查 `/logo-placeholder.svg` 是否存在
2. 验证图片文件完整性
3. 确认HTML中的路径引用正确

#### 问题4: API请求失败
**可能原因**: 后端服务未启动或代理配置错误
**解决方案**:
1. 启动后端服务（端口8080）
2. 检查API代理配置
3. 验证跨域设置

### 🛠️ 测试工具使用

#### 1. 浏览器开发者工具
- **Network标签**: 检查资源加载状态
- **Console标签**: 查看JavaScript错误
- **Elements标签**: 检查DOM结构和样式

#### 2. 命令行测试
```bash
# 测试首页
curl http://localhost:3001

# 测试特定文件
curl -I http://localhost:3001/logo-placeholder.svg

# 测试API代理（如果后端运行）
curl http://localhost:3001/api/health
```

### 📊 性能检查

#### 1. 页面加载时间
- 首屏加载时间 < 3秒
- 静态资源加载时间 < 1秒

#### 2. 文件大小
- 主要JS文件应已压缩
- CSS文件应已优化
- 图片文件应已优化

### 🔒 安全检查

#### 1. 敏感信息
- [ ] 硬编码的API密钥已移除
- [ ] 开发环境调试信息已关闭
- [ ] 错误信息不泄露敏感数据

#### 2. CORS配置
- [ ] 生产环境CORS策略正确
- [ ] 不允许不必要的跨域请求

### ✅ 测试完成后的步骤

1. **如果测试通过**:
   - 记录测试结果
   - 准备EdgeOne部署
   - 配置生产环境变量

2. **如果发现问题**:
   - 修复相关问题
   - 重新构建 (`npm run build-only`)
   - 重新测试

### 📞 联系支持

如果在测试过程中遇到问题，请：
1. 检查浏览器控制台错误信息
2. 查看服务器日志输出
3. 验证构建文件完整性
4. 提交详细的错误报告

---

**测试完成时间**: ___________
**测试人员**: ___________
**测试结果**: ✅ 通过 / ❌ 失败