# 宿舍管理API文档

## 接口概览

宿舍管理模块提供完整的宿舍CRUD操作，支持新增、编辑、查看详情等功能。

---

## 1. 新增宿舍

**接口地址：** `POST /dormitory`

**功能说明：** 创建新的宿舍记录，自动创建床位

**请求参数：**
```json
{
  "buildingId": 1,
  "roomNo": "101",
  "floorNumber": 1,
  "bedCount": 4,
  "description": "101宿舍"
}
```

**参数说明：**
- `buildingId`: 楼栋ID（必填）
- `roomNo`: 房间号（必填）
- `floorNumber`: 楼层（必填，1-30）
- `bedCount`: 床位数（必填，1-10）
- `description`: 描述（可选）

**响应示例：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": null
}
```

**业务逻辑：**
- 自动校验同一楼栋内房间号唯一性
- 自动设置宿舍状态为可用
- 根据bedCount设置总床位数和可用床位数
- 自动创建对应数量的床位记录（床位1、床位2...）
- 新宿舍初始占用数为0
- 使用UserContext获取当前操作用户

---

## 2. 编辑宿舍

**接口地址：** `PUT /dormitory/{id}`

**功能说明：** 编辑宿舍信息

**请求参数：**
```json
{
  "buildingId": 1,
  "roomNo": "102",
  "floorNumber": 1,
  "bedCount": 2,
  "description": "102宿舍（已编辑）"
}
```

**响应示例：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": null
}
```

**业务限制：**
- **重要：** 如果宿舍的 `occupiedBeds > 0`（有学生入住），则不允许编辑
- 编辑时会重新创建床位信息（根据新的bedCount）
- 自动更新修改人和修改时间

**错误示例：**
```json
{
  "code": 500,
  "message": "有学生入住不可编辑",
  "data": null
}
```

---

## 3. 查看宿舍详情

**接口地址：** `GET /dormitory/{id}`

**功能说明：** 获取宿舍详细信息，包括床位列表

**响应示例：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "buildingId": 1,
    "roomNo": "101",
    "floorNumber": 1,
    "totalBeds": 4,
    "occupiedBeds": 2,
    "availableBeds": 2,
    "description": "101宿舍",
    "status": 1,
    "statusText": "可用",
    "createTime": "2024-01-01 10:00:00",
    "updateTime": "2024-01-01 10:00:00",
    "createBy": "管理员",
    "updateBy": "管理员",
    "bedList": [
      {
        "id": 1,
        "bedNo": "床位1",
        "status": 1,
        "statusText": "可用",
        "studentId": null,
        "studentName": null,
        "studentNo": null
      },
      {
        "id": 2,
        "bedNo": "床位2",
        "status": 2,
        "statusText": "已占用",
        "studentId": 1,
        "studentName": "张三",
        "studentNo": "2024001"
      }
    ]
  }
}
```

---

## 4. 分页查询宿舍列表

**接口地址：** `POST /dormitory/page`

**功能说明：** 分页查询宿舍列表

**请求参数：**
```json
{
  "current": 1,
  "size": 10,
  "buildingId": 1,
  "roomNo": "101",
  "status": 1
}
```

**响应示例：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [...],
    "total": 100,
    "size": 10,
    "current": 1,
    "pages": 10
  }
}
```

---

## 5. 删除宿舍

**接口地址：** `DELETE /dormitory/{id}`

**功能说明：** 删除宿舍（逻辑删除）

**响应示例：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": null
}
```

---

## 6. 获取可用宿舍列表

**接口地址：** `GET /dormitory/available`

**功能说明：** 获取有空床位的宿舍列表

**请求参数：**
- `buildingId`（可选）：楼栋ID

**响应示例：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "roomNo": "101",
      "availableBeds": 2,
      "statusText": "可用"
    }
  ]
}
```

---

## 状态说明

| 状态值 | 状态文本 | 说明 |
|--------|----------|------|
| 0 | 已满 | 无可用床位 |
| 1 | 可用 | 有可用床位 |
| 2 | 维护中 | 宿舍维护中，不可分配 |

## 床位状态说明

| 状态值 | 状态文本 | 说明 |
|--------|----------|------|
| 1 | 可用 | 床位可用 |
| 2 | 已占用 | 已有学生入住 |
| 3 | 维护中 | 床位维护中 |

---

## 注意事项

1. **编辑限制：** 宿舍有学生入住时（`occupiedBeds > 0`）不允许编辑基本信息
2. **用户上下文：** 所有操作自动记录操作人，使用 `UserContext` 获取当前用户
3. **事务处理：** 新增和编辑操作使用事务确保数据一致性
4. **数据验证：** 自动验证楼栋内房间号唯一性

---

## 前端对接建议

1. **新增宿舍：** 表单提交时调用 `POST /dormitory`
2. **编辑宿舍：**
   - 先调用 `GET /dormitory/{id}` 获取详情
   - 检查 `occupiedBeds` 字段，如果 >0 则禁用编辑功能
   - 提交时调用 `PUT /dormitory/{id}`
3. **查看详情：** 直接调用 `GET /dormitory/{id}`
4. **错误处理：** 捕获 "有学生入住不可编辑" 等业务异常信息

---

*更新时间：2024年*