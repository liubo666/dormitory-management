# 宿舍管理系统

**技术栈:**
- 后端: Spring Boot 3 + Spring Security + JWT + MyBatis-Plus + Redis
- 前端: Vue3 + Vite + Element Plus + TypeScript
- 架构: 前后端分离
- 设计风格: 学校风格 - 学术蓝为主色调

## 主要功能模块
- 宿舍楼管理: 楼栋、楼层、宿舍信息
- 学生管理: 学生档案、分配宿舍、调宿退宿
- 宿舍入住管理: 入住登记、退宿记录
- 报修管理: 宿舍报修、维修处理、进度跟踪
- 卫生检查: 宿舍卫生评分、记录、统计
- 访客登记: 访客信息、到访记录
- 宿舍费用: 水电费记录、缴费情况
- 数据统计: 入住率、性别分布、宿舍使用情况可视化

## 项目结构
```
dormitory-management/
├── backend/           # 后端项目
├── frontend/          # 前端项目
├── database/          # 数据库脚本
├── docs/              # 项目文档
└── README.md          # 项目说明
```

## 快速开始

### 后端启动
```bash
cd backend
mvn clean install
mvn spring-boot:run
```

### 前端启动
```bash
cd frontend
npm install
npm run dev
```

### 数据库初始化
```bash
# 执行 database/init.sql 脚本
```

## 开发规范
- 后端API遵循RESTful规范
- 前端组件命名使用PascalCase
- 代码提交前需要格式化和lint检查