@echo off
echo ========================================
echo 宿舍管理系统 EdgeOne 部署脚本
echo ========================================
echo.

REM 设置变量
set PROJECT_ROOT=%~dp0
set FRONTEND_DIR=%PROJECT_ROOT%frontend
set BACKEND_DIR=%PROJECT_ROOT%backend
set DEPLOYMENT_DIR=%PROJECT_ROOT%deployment
set BUILD_DIR=%PROJECT_ROOT%build
set TIMESTAMP=%date:~0,4%%date:~5,2%%date:~8,2%_%time:~0,2%%time:~3,2%%time:~6,2%

echo 开始时间: %date% %time%
echo 项目根目录: %PROJECT_ROOT%
echo.

REM 检查 Node.js
echo 检查 Node.js 环境...
node --version
if %errorlevel% neq 0 (
    echo 错误: Node.js 未安装或不在 PATH 中
    pause
    exit /b 1
)

REM 检查 Maven
echo 检查 Maven 环境...
call mvn --version
if %errorlevel% neq 0 (
    echo 警告: Maven 未安装或不在 PATH 中，将跳过后端构建
)

REM 创建构建目录
echo.
echo 创建构建目录...
if not exist "%BUILD_DIR%" mkdir "%BUILD_DIR%"
if not exist "%BUILD_DIR%\%TIMESTAMP%" mkdir "%BUILD_DIR%\%TIMESTAMP%"

REM 构建 Frontend
echo.
echo ========================================
echo 构建 Frontend
echo ========================================
cd /d "%FRONTEND_DIR%"

echo 安装依赖...
call npm install
if %errorlevel% neq 0 (
    echo 错误: 前端依赖安装失败
    pause
    exit /b 1
)

echo 构建生产版本...
call npm run build-only
if %errorlevel% neq 0 (
    echo 错误: 前端构建失败
    pause
    exit /b 1
)

echo 复制前端构建文件到部署目录...
xcopy "%FRONTEND_DIR%\dist" "%BUILD_DIR%\%TIMESTAMP%\frontend\" /E /I /Y
xcopy "%DEPLOYMENT_DIR%\404.html" "%BUILD_DIR%\%TIMESTAMP%\frontend\" /Y
xcopy "%DEPLOYMENT_DIR%\500.html" "%BUILD_DIR%\%TIMESTAMP%\frontend\" /Y

REM 构建 Backend (如果 Maven 可用)
echo.
echo ========================================
echo 构建 Backend
echo ========================================
cd /d "%BACKEND_DIR%"

call mvn --version >nul 2>&1
if %errorlevel% equ 0 (
    echo 构建 Spring Boot JAR...
    call mvn clean package -DskipTests -Pprod
    if %errorlevel% neq 0 (
        echo 警告: 后端构建失败，继续前端部署
    ) else (
        echo 复制后端 JAR 文件...
        copy "%BACKEND_DIR%\target\dormitory-management-1.0.0.jar" "%BUILD_DIR%\%TIMESTAMP%\backend\"
    )
) else (
    echo 跳过后端构建 (Maven 未安装)
)

REM 生成部署配置文件
echo.
echo ========================================
echo 生成部署配置
echo ========================================
cd /d "%PROJECT_ROOT%"

echo 生成 EdgeOne 部署清单...
(
echo 部署时间: %date% %time%
echo 构建版本: %TIMESTAMP%
echo.
echo 前端文件:
dir "%BUILD_DIR%\%TIMESTAMP%\frontend" /B
echo.
if exist "%BUILD_DIR%\%TIMESTAMP%\backend" (
    echo 后端文件:
    dir "%BUILD_DIR%\%TIMESTAMP%\backend" /B
)
echo.
echo EdgeOne 部署步骤:
echo 1. 登录腾讯云控制台
echo 2. 进入 EdgeOne 服务
echo 3. 创建静态网站
echo 4. 上传 frontend 目录下的所有文件
echo 5. 配置域名解析
echo 6. 配置 HTTPS 和缓存策略
echo 7. 配置 API 代理 ^(如果需要^)
echo.
echo 环境变量配置:
echo - DB_URL: 数据库连接地址
echo - DB_USERNAME: 数据库用户名
echo - DB_PASSWORD: 数据库密码
echo - REDIS_HOST: Redis 服务器地址
echo - REDIS_PASSWORD: Redis 密码
echo - JWT_SECRET: JWT 密钥
) > "%BUILD_DIR%\%TIMESTAMP%\deploy-manifest.txt"

REM 生成部署压缩包
echo.
echo 打包部署文件...
cd /d "%BUILD_DIR%"
call powershell -Command "Compress-Archive -Path '%TIMESTAMP%' -DestinationPath '%TIMESTAMP%.zip' -Force"

echo.
echo ========================================
echo 部署完成
echo ========================================
echo.
echo 构建文件位置: %BUILD_DIR%\%TIMESTAMP%
echo 压缩包位置: %BUILD_DIR%\%TIMESTAMP%.zip
echo 部署清单: %BUILD_DIR%\%TIMESTAMP%\deploy-manifest.txt
echo.

echo 下一步操作:
echo 1. 检查构建文件是否正确生成
echo 2. 上传到 EdgeOne 静态网站托管
echo 3. 配置域名和 DNS
echo 4. 配置生产环境变量
echo 5. 测试部署结果
echo.

echo 部署脚本执行完毕！
pause