# 宿舍管理系统 API 文档

## 接口说明

- 接口基础路径: `/api`
- 认证方式: JWT Token (Header: `Authorization: Bearer {token}`)
- 数据格式: JSON
- 字符编码: UTF-8

## 通用响应格式

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {}
}
```

### 响应状态码说明

- 200: 操作成功
- 401: 未授权
- 403: 权限不足
- 404: 资源不存在
- 500: 服务器错误

## 认证接口

### 用户登录
- 接口地址: `/user/login`
- 请求方式: POST
- 是否需要认证: 否

**请求参数:**
```json
{
  "username": "admin",
  "password": "123456"
}
```

**响应示例:**
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }
}
```

### 刷新Token
- 接口地址: `/user/refresh`
- 请求方式: POST
- 是否需要认证: 否

**请求参数:**
```
refreshToken: 刷新令牌
```

**响应示例:**
```json
{
  "code": 200,
  "message": "刷新成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }
}
```

### 获取当前用户信息
- 接口地址: `/user/info`
- 请求方式: GET
- 是否需要认证: 是

**响应示例:**
```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "id": 1,
    "username": "admin",
    "name": "管理员",
    "gender": 1,
    "phone": "13800138000",
    "email": "admin@dormitory.com",
    "role": "admin",
    "avatar": "",
    "enabled": true
  }
}
```

### 用户登出
- 接口地址: `/user/logout`
- 请求方式: POST
- 是否需要认证: 是

**响应示例:**
```json
{
  "code": 200,
  "message": "退出成功",
  "data": null
}
```

## 宿舍楼管理

### 获取宿舍楼列表
- 接口地址: `/building/list`
- 请求方式: GET
- 是否需要认证: 是

**请求参数:**
```
page: 页码 (默认1)
size: 每页数量 (默认10)
buildingNo: 楼栋号 (可选)
buildingName: 楼栋名称 (可选)
status: 状态 (可选)
```

**响应示例:**
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "total": 10,
    "records": [
      {
        "id": 1,
        "buildingNo": "A1",
        "buildingName": "A1栋",
        "floorCount": 6,
        "description": "男生宿舍",
        "status": 1,
        "createTime": "2024-01-01 10:00:00"
      }
    ]
  }
}
```

### 新增宿舍楼
- 接口地址: `/building/add`
- 请求方式: POST
- 是否需要认证: 是

**请求参数:**
```json
{
  "buildingNo": "A3",
  "buildingName": "A3栋",
  "floorCount": 6,
  "description": "新建宿舍楼"
}
```

### 修改宿舍楼
- 接口地址: `/building/update`
- 请求方式: PUT
- 是否需要认证: 是

**请求参数:**
```json
{
  "id": 1,
  "buildingNo": "A1",
  "buildingName": "A1栋",
  "floorCount": 6,
  "description": "男生宿舍楼",
  "status": 1
}
```

### 删除宿舍楼
- 接口地址: `/building/delete/{id}`
- 请求方式: DELETE
- 是否需要认证: 是

## 宿舍管理

### 获取宿舍列表
- 接口地址: `/room/list`
- 请求方式: GET
- 是否需要认证: 是

**请求参数:**
```
page: 页码
size: 每页数量
buildingId: 楼栋ID (可选)
roomNo: 宿舍号 (可选)
floor: 楼层 (可选)
gender: 性别限制 (可选)
status: 状态 (可选)
```

**响应示例:**
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "total": 50,
    "records": [
      {
        "id": 1,
        "buildingId": 1,
        "buildingName": "A1栋",
        "roomNo": "101",
        "floor": 1,
        "bedCount": 4,
        "gender": 1,
        "type": "standard",
        "status": 1,
        "occupiedBeds": 2,
        "createTime": "2024-01-01 10:00:00"
      }
    ]
  }
}
```

### 新增宿舍
- 接口地址: `/room/add`
- 请求方式: POST
- 是否需要认证: 是

**请求参数:**
```json
{
  "buildingId": 1,
  "roomNo": "103",
  "floor": 1,
  "bedCount": 4,
  "gender": 1,
  "type": "standard"
}
```

## 学生管理

### 获取学生列表
- 接口地址: `/student/list`
- 请求方式: GET
- 是否需要认证: 是

**请求参数:**
```
page: 页码
size: 每页数量
studentNo: 学号 (可选)
name: 姓名 (可选)
gender: 性别 (可选)
college: 学院 (可选)
major: 专业 (可选)
status: 状态 (可选)
```

**响应示例:**
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "total": 100,
    "records": [
      {
        "id": 1,
        "studentNo": "2024001",
        "name": "张三",
        "gender": 1,
        "phone": "13800138000",
        "college": "计算机学院",
        "major": "软件工程",
        "className": "软工1班",
        "grade": "2024级",
        "status": 1,
        "roomInfo": {
          "buildingName": "A1栋",
          "roomNo": "101",
          "bedNo": 1
        }
      }
    ]
  }
}
```

### 新增学生
- 接口地址: `/student/add`
- 请求方式: POST
- 是否需要认证: 是

**请求参数:**
```json
{
  "studentNo": "2024005",
  "name": "李四",
  "gender": 1,
  "phone": "13800138001",
  "college": "计算机学院",
  "major": "软件工程",
  "className": "软工1班",
  "grade": "2024级",
  "idCard": "123456789012345678",
  "address": "浙江省杭州市"
}
```

### 分配宿舍
- 接口地址: `/student/assign-room`
- 请求方式: POST
- 是否需要认证: 是

**请求参数:**
```json
{
  "studentId": 1,
  "roomId": 1,
  "bedNo": 3
}
```

## 入住管理

### 获取入住记录
- 接口地址: `/checkin/list`
- 请求方式: GET
- 是否需要认证: 是

**请求参数:**
```
page: 页码
size: 每页数量
studentId: 学生ID (可选)
roomId: 宿舍ID (可选)
status: 状态 (可选)
startDate: 开始日期 (可选)
endDate: 结束日期 (可选)
```

**响应示例:**
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "total": 80,
    "records": [
      {
        "id": 1,
        "studentId": 1,
        "studentName": "张三",
        "studentNo": "2024001",
        "roomId": 1,
        "roomNo": "101",
        "buildingName": "A1栋",
        "bedNo": 1,
        "checkinDate": "2024-09-01",
        "checkoutDate": null,
        "status": 1
      }
    ]
  }
}
```

### 退宿
- 接口地址: `/checkin/checkout`
- 请求方式: POST
- 是否需要认证: 是

**请求参数:**
```json
{
  "id": 1,
  "checkoutDate": "2024-12-01",
  "remark": "毕业退宿"
}
```

## 报修管理

### 获取报修列表
- 接口地址: `/repair/list`
- 请求方式: GET
- 是否需要认证: 是

**请求参数:**
```
page: 页码
size: 每页数量
roomId: 宿舍ID (可选)
studentId: 学生ID (可选)
status: 状态 (可选)
type: 报修类型 (可选)
priority: 优先级 (可选)
```

**响应示例:**
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "total": 20,
    "records": [
      {
        "id": 1,
        "roomId": 1,
        "roomNo": "101",
        "studentId": 1,
        "studentName": "张三",
        "title": "水龙头漏水",
        "description": "宿舍水龙头漏水严重",
        "type": "water",
        "priority": 2,
        "status": 2,
        "reportTime": "2024-12-01 10:00:00",
        "handleTime": "2024-12-01 14:00:00",
        "completeTime": null
      }
    ]
  }
}
```

### 新增报修
- 接口地址: `/repair/add`
- 请求方式: POST
- 是否需要认证: 是

**请求参数:**
```json
{
  "roomId": 1,
  "studentId": 1,
  "title": "电灯不亮",
  "description": "宿舍主电灯不亮",
  "type": "electric",
  "priority": 2
}
```

### 处理报修
- 接口地址: `/repair/handle`
- 请求方式: POST
- 是否需要认证: 是

**请求参数:**
```json
{
  "id": 1,
  "status": 3,
  "handleRemark": "已更换新的电灯泡"
}
```

## 卫生检查

### 获取检查记录
- 接口地址: `/inspection/list`
- 请求方式: GET
- 是否需要认证: 是

**请求参数:**
```
page: 页码
size: 每页数量
roomId: 宿舍ID (可选)
level: 等级 (可选)
startDate: 开始日期 (可选)
endDate: 结束日期 (可选)
```

**响应示例:**
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "total": 100,
    "records": [
      {
        "id": 1,
        "roomId": 1,
        "roomNo": "101",
        "buildingName": "A1栋",
        "inspectorId": 1,
        "inspectorName": "管理员",
        "inspectionDate": "2024-12-01",
        "score": 85,
        "level": 2,
        "problems": "地面有垃圾",
        "photos": ["/uploads/inspection/1.jpg"]
      }
    ]
  }
}
```

### 新增检查记录
- 接口地址: `/inspection/add`
- 请求方式: POST
- 是否需要认证: 是

**请求参数:**
```json
{
  "roomId": 1,
  "inspectionDate": "2024-12-01",
  "score": 90,
  "level": 1,
  "problems": "整体干净整洁",
  "photos": ["/uploads/inspection/2.jpg"]
}
```

## 访客管理

### 获取访客记录
- 接口地址: `/visitor/list`
- 请求方式: GET
- 是否需要认证: 是

**请求参数:**
```
page: 页码
size: 每页数量
visitorName: 访客姓名 (可选)
visitStudentId: 受访学生ID (可选)
visitDate: 访问日期 (可选)
status: 状态 (可选)
```

**响应示例:**
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "total": 30,
    "records": [
      {
        "id": 1,
        "visitorName": "张明",
        "visitorPhone": "13800138000",
        "visitStudentId": 1,
        "visitStudentName": "张三",
        "visitRoomId": 1,
        "visitRoomNo": "101",
        "visitReason": "探望同学",
        "visitDate": "2024-12-01",
        "entryTime": "2024-12-01 09:00:00",
        "exitTime": "2024-12-01 11:00:00",
        "status": 2
      }
    ]
  }
}
```

### 访客登记
- 接口地址: `/visitor/register`
- 请求方式: POST
- 是否需要认证: 是

**请求参数:**
```json
{
  "visitorName": "李明",
  "visitorPhone": "13800138001",
  "visitorIdCard": "123456789012345678",
  "visitStudentId": 1,
  "visitRoomId": 1,
  "visitReason": "探望同学",
  "visitDate": "2024-12-01"
}
```

### 访客离场
- 接口地址: `/visitor/exit`
- 请求方式: POST
- 是否需要认证: 是

**请求参数:**
```json
{
  "id": 1,
  "exitTime": "2024-12-01 12:00:00"
}
```

## 费用管理

### 获取费用列表
- 接口地址: `/fee/list`
- 请求方式: GET
- 是否需要认证: 是

**请求参数:**
```
page: 页码
size: 每页数量
roomId: 宿舍ID (可选)
studentId: 学生ID (可选)
type: 费用类型 (可选)
status: 状态 (可选)
period: 费用周期 (可选)
```

**响应示例:**
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "total": 200,
    "records": [
      {
        "id": 1,
        "roomId": 1,
        "roomNo": "101",
        "studentId": null,
        "type": "water",
        "amount": 50.00,
        "period": "202412",
        "status": 1,
        "dueDate": "2024-12-31",
        "payDate": null,
        "remark": "12月水费"
      }
    ]
  }
}
```

### 新增费用
- 接口地址: `/fee/add`
- 请求方式: POST
- 是否需要认证: 是

**请求参数:**
```json
{
  "roomId": 1,
  "studentId": null,
  "type": "electric",
  "amount": 80.00,
  "period": "202412",
  "dueDate": "2024-12-31",
  "remark": "12月电费"
}
```

### 缴费
- 接口地址: `/fee/pay`
- 请求方式: POST
- 是否需要认证: 是

**请求参数:**
```json
{
  "id": 1,
  "payDate": "2024-12-15"
}
```

## 数据统计

### 获取统计数据
- 接口地址: `/statistics/overview`
- 请求方式: GET
- 是否需要认证: 是

**响应示例:**
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "buildingCount": 12,
    "roomCount": 480,
    "studentCount": 2456,
    "occupancyRate": 78.5,
    "maleCount": 1234,
    "femaleCount": 1222,
    "todayRepairs": 8,
    "pendingRepairs": 15,
    "todayVisitors": 25,
    "unpaidFees": 3500.00
  }
}
```

### 获取入住率统计
- 接口地址: `/statistics/occupancy`
- 请求方式: GET
- 是否需要认证: 是

**响应示例:**
```json
{
  "code": 200,
  "message": "查询成功",
  "data": [
    {
      "buildingName": "A1栋",
      "totalRooms": 80,
      "occupiedRooms": 62,
      "occupancyRate": 77.5
    }
  ]
}
```

---

更多详细信息请参考在线API文档: http://localhost:8080/api/doc.html