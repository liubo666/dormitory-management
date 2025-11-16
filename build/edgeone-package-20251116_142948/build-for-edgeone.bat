@echo off
echo ========================================
🚀 宿舍管理系统 EdgeOne 部署打包脚本
echo ========================================
echo.

REM 设置变量
set PROJECT_ROOT=%~dp0
set DEPLOYMENT_DIR=%PROJECT_ROOT%deployment
set BUILD_DIR=%PROJECT_ROOT%build
set TIMESTAMP=%date:~0,4%%date:~5,2%%date:~8,2%_%time:~0,2%%time:~3,2%%time:~6,2%
set BUILD_PACKAGE=%BUILD_DIR%\edgeone-package-%TIMESTAMP%

echo 开始时间: %date% %time%
echo 项目根目录: %PROJECT_ROOT%
echo 部署包输出: %BUILD_PACKAGE%
echo.

REM 检查必要的工具
echo ========================================
echo 🔍 检查部署环境...
echo ========================================

node --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ 错误: Node.js 未安装或不在 PATH 中
    echo 请先安装 Node.js: https://nodejs.org/
    pause
    exit /b 1
)
echo ✅ Node.js: 已安装

npm --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ 错误: npm 未安装或不在 PATH 中
    echo 请先安装 npm
    pause
    exit /b 1
)
echo ✅ npm: 已安装

REM 创建构建目录
echo.
echo 📁 创建构建目录...
if not exist "%BUILD_DIR%" mkdir "%BUILD_DIR%"
if not exist "%BUILD_PACKAGE%" mkdir "%BUILD_PACKAGE%"
if not exist "%BUILD_PACKAGE%\frontend" mkdir "%BUILD_PACKAGE%\frontend"

REM 步骤1: 前置检查
echo.
echo ========================================
echo 🔍 步骤 1/5: 项目前置检查
echo ========================================

echo 检查项目结构...
if not exist "%PROJECT_ROOT%\frontend\package.json" (
    echo ❌ 错误: 前端 package.json 不存在
    exit /b 1
)
echo ✅ 前端 package.json 存在

if not exist "%PROJECT_ROOT%\frontend\src" (
    echo ❌ 错误: 前端 src 目录不存在
    exit /b 1
)
echo ✅ 前端 src 目录存在

echo 检查依赖...
cd /d "%PROJECT_ROOT%\frontend"
if not exist "node_modules" (
    echo 📦 安装前端依赖...
    call npm install
    if %errorlevel% neq 0 (
        echo ❌ 前端依赖安装失败
        pause
        exit /b 1
    )
    echo ✅ 前端依赖安装完成
) else (
    echo ✅ 前端依赖已存在
)

REM 步骤2: 生产构建
echo.
echo ========================================
echo 🔨 步骤 2/5: 构建生产版本
echo ========================================

echo 清理旧构建文件...
if exist "dist" (
    rmdir /s /q "dist"
)

echo 构建前端生产版本...
call npm run build-only
if %errorlevel% neq 0 (
    echo ❌ 前端构建失败
    pause
    exit /b 1
)
echo ✅ 前端构建成功

REM 复制静态资源
echo 复制静态资源到构建目录...
if exist "public\*" (
    xcopy "public\*" "dist\" /E /I /Y
    echo ✅ 静态资源复制完成
) else (
    echo ⚠️  警告: public 目录为空
)

REM 步骤3: 验证构建
echo.
echo ========================================
echo ✅ 步骤 3/5: 验证构建结果
echo ========================================

echo 检查构建文件...
if not exist "dist\index.html" (
    echo ❌ 错误: index.html 未生成
    exit /b    1
)
echo ✅ index.html 存在

if not exist "dist\assets" (
    echo ❌ 错误: assets 目录未生成
    exit /b 1
)
echo ✅ assets 目录存在

echo 检查关键文件...
set /a file_count=0
for %%f in (dist\*.html dist\assets\*.js dist\assets\*.css) do set /a file_count+=1
if %file_count% lss 5 (
    echo ❌ 错误: 构建文件过少 (%file_count% 个)
    exit /b 1
)
echo ✅ 构建文件检查通过 (%file_count% 个文件)

echo 检查文件大小...
for %%f in (dist\assets\*.js) do (
    for %%s in ("%%~sf") do (
        if %%~s lss 100000 (
            echo ⚠️  警告: 文件过大 - %%f (%%~s bytes)
        )
    )
)

REM 步骤4: 创建部署包
echo.
echo ========================================
echo 📦 步骤 4/5: 创建部署包
echo ========================================

echo 复制前端文件...
xcopy "dist\*" "%BUILD_PACKAGE%\frontend\" /E /I /Y

echo 复制配置文件...
copy "%DEPLOYMENT_DIR%\edgeone-final.json" "%BUILD_PACKAGE%\" /Y
copy "%DEPLOYMENT_DIR%\404.html" "%BUILD_PACKAGE%\frontend\" /Y
copy "%DEPLOYMENT_DIR%\500.html" "%BUILD_PACKAGE%\frontend\" /Y

echo 复制构建脚本...
copy "%PROJECT_ROOT%\build-for-edgeone.bat" "%BUILD_PACKAGE%\" /Y

echo 生成部署清单...
(
echo EdgeOne 部署包信息
echo ==========================
echo 项目名称: 宿舍管理系统
echo 版本号: 1.0.0
echo 构建时间: %date% %time%
echo 包路径: %BUILD_PACKAGE%
echo
echo 构建信息:
echo - 前端框架: Vue 3 + Vite
echo - UI组件: Element Plus
echo - 构建工具: Webpack/Vite
echo - 压缩: 启用
echo
echo 文件列表:
dir /b "%BUILD_PACKAGE%\frontend\"
echo
echo EdgeOne 配置:
echo - 缓存策略: 已配置
echo - HTTPS: 已启用
echo - 压缩: 已启用
echo - 安全头: 已配置
echo
echo 部署步骤:
echo 1. 登录腾讯云 EdgeOne 控制台
echo 2. 创建静态网站服务
echo 3. 上传 frontend 目录下的所有文件
echo 4. 配置域名解析
echo 5. 配置缓存和安全策略
echo 6. 测试网站访问
echo 7. 配置后端API代理（如需要）
echo
echo 环境变量说明:
echo - 更新 edgeone-final.json 中的域名配置
echo - 配置后端API地址
echo - 设置合适的缓存策略
echo
echo 注意事项:
echo - 确保域名已备案并指向腾讯云
echo - 检查SSL证书状态
echo - 监控网站访问速度
echo - 定期更新部署包
) > "%BUILD_PACKAGE%\deployment-info.txt"

REM 创建部署检查清单
(
echo EdgeOne 部署检查清单
echo =====================
echo
echo ☐ 准备工作
echo - [ ] 域名已备案并指向腾讯云
echo - [ ] EdgeOne 服务已开通
echo - [ ] 腾讯云账户余额充足
echo - [ ] 代码已提交到版本控制
echo
echo ☐ 部署前检查
echo - [ ] 构建文件完整性检查
echo - [ ] 配置文件正确性验证
echo - [ ] SSL证书状态确认
echo - [ ] 缓存策略配置合理
echo - [ ] 安全头设置正确
echo
echo ☐ 文件上传
echo - [ ] 所有静态文件已上传
echo - [ ] HTML文件路径正确
echo - [ ] CSS/JS文件路径正确
echo - [ ] 图片/图标文件已上传
echo - [ ] 错误页面已配置
echo
echo ☐ 域名配置
echo - [ ] DNS解析已设置
echo - [ ] CNAME记录已配置
echo - [] 域名已验证所有权
echo - [ ] SSL证书已生效
echo
echo ☐ 功能测试
echo - [ ] 网站首页正常访问
echo - [ ] 所有页面链接正常
echo - [ ] 静态资源加载正常
echo - [ ] 页面交互功能正常
echo - [ ] 移动端显示正常
echo
echo ☐ 性能优化
echo - [ ] 静态资源压缩生效
echo - [ ] 缓存策略工作正常
echo - [ ] 页面加载速度满意
echo - [ ] SEO配置正确
echo - [ ] 错误页面显示正常
echo
echo ☐ 安全配置
echo - [ ] HTTPS强制跳转
echo - [ ] 安全头配置正确
echo - [ ] XSS防护启用
echo - [ ] 速率限制合理
echo - [ ] 内容安全策略配置
echo
echo ☐ 监控设置
echo - [ ] 访问日志启用
echo - [ ] 错误监控配置
echo - [ ] 性能监控设置
echo - [ ] 备份策略制定
echo
echo 部署状态: _________
echo 部署人员: _________
echo 完成时间: _________
) > "%BUILD_PACKAGE%\deployment-checklist.txt"

REM 步骤5: 最终验证
echo.
echo ========================================
echo ✅ 步骤 5/5: 最终验证
echo ========================================

echo 计算包大小...
for /f "tokens=3" %%a in ('dir /s "%BUILD_PACKAGE%" ^| find /c /v "" /c "$"') do set total_size=%%a
echo 部署包总大小: %total_size% bytes

echo 生成部署包摘要...
(
echo 部署包摘要
echo =============
echo
echo 包名: edgeone-package-%TIMESTAMP%
echo 位置: %BUILD_PACKAGE%
echo 大小: %total_size% bytes
echo 创建时间: %date% %time%
echo
echo 包含内容:
echo - frontend/: 前端静态文件
echo - edgeone-final.json: EdgeOne配置
echo - 404.html/500.html: 错误页面
echo - build-for-edgeone.bat: 部署脚本
echo - deployment-info.txt: 部署信息
echo - deployment-checklist.txt: 检查清单
echo
echo 下一步操作:
echo 1. 检查部署包完整性
echo 2. 准备 EdgeOne 控制台
echo 3. 上传 frontend 目录到 EdgeOne
echo 4. 按照 deployment-checklist.txt 进行部署
echo 5. 测试网站功能
) > "%BUILD_PACKAGE%\package-summary.txt"

echo.
echo ========================================
echo ✅ EdgeOne 部署包创建完成！
echo ========================================
echo.
echo 📦 部署包位置: %BUILD_PACKAGE%
echo 📏 包大小: %total_size% bytes
echo ⏱ 创建时间: %date% %time%
echo.
echo 📋 检查清单:
echo - [ ] 查看 deployment-info.txt 了解部署详情
echo - [ ] 按照 deployment-checklist.txt 进行部署
echo - [ ] 上传到 EdgeOne 后验证功能
echo.
echo 🌐 EdgeOne 控制台: https://console.cloud.tencent.com/
echo.
echo 🚀 部署准备就绪！
echo.
pause