-- 宿舍管理系统数据库初始化脚本
-- 适用于 CentOS 部署环境
-- 创建时间: 2025-11-16

-- 创建数据库
CREATE DATABASE IF NOT EXISTS dormitory_management
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE dormitory_management;

-- 创建应用专用用户 (生产环境推荐)
CREATE USER IF NOT EXISTS 'dormitory_user'@'localhost' IDENTIFIED BY 'your_secure_db_password';
CREATE USER IF NOT EXISTS 'dormitory_user'@'%' IDENTIFIED BY 'your_secure_db_password';

-- 授权
GRANT ALL PRIVILEGES ON dormitory_management.* TO 'dormitory_user'@'localhost';
GRANT ALL PRIVILEGES ON dormitory_management.* TO 'dormitory_user'@'%';

-- 刷新权限
FLUSH PRIVILEGES;

-- 创建用户表
CREATE TABLE IF NOT EXISTS `users` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(255) NOT NULL COMMENT '密码（加密）',
  `real_name` varchar(50) NOT NULL COMMENT '真实姓名',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `role` varchar(20) NOT NULL DEFAULT 'USER' COMMENT '角色：ADMIN-管理员，USER-普通用户',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像URL',
  `gender` tinyint DEFAULT NULL COMMENT '性别：0-未知，1-男，2-女',
  `birth_date` date DEFAULT NULL COMMENT '出生日期',
  `address` varchar(255) DEFAULT NULL COMMENT '地址',
  `id_card` varchar(18) DEFAULT NULL COMMENT '身份证号',
  `emergency_contact` varchar(50) DEFAULT NULL COMMENT '紧急联系人',
  `emergency_phone` varchar(20) DEFAULT NULL COMMENT '紧急联系电话',
  `remark` text COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-未删除，1-已删除',
  `create_by` varchar(50) DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(50) DEFAULT NULL COMMENT '更新人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  KEY `idx_email` (`email`),
  KEY `idx_phone` (`phone`),
  KEY `idx_role` (`role`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 创建学生表
CREATE TABLE IF NOT EXISTS `students` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '学生ID',
  `student_no` varchar(20) NOT NULL COMMENT '学号',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `name` varchar(50) NOT NULL COMMENT '姓名',
  `gender` tinyint DEFAULT NULL COMMENT '性别：0-未知，1-男，2-女',
  `grade` varchar(20) DEFAULT NULL COMMENT '年级',
  `class_name` varchar(50) DEFAULT NULL COMMENT '班级',
  `major` varchar(100) DEFAULT NULL COMMENT '专业',
  `department` varchar(100) DEFAULT NULL COMMENT '院系',
  `phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `address` varchar(255) DEFAULT NULL COMMENT '家庭地址',
  `id_card` varchar(18) DEFAULT NULL COMMENT '身份证号',
  `enrollment_date` date DEFAULT NULL COMMENT '入学日期',
  `graduation_date` date DEFAULT NULL COMMENT '毕业日期',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-离校，1-在校，2-休学',
  `remark` text COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-未删除，1-已删除',
  `create_by` varchar(50) DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(50) DEFAULT NULL COMMENT '更新人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_student_no` (`student_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_name` (`name`),
  KEY `idx_grade` (`grade`),
  KEY `idx_class_name` (`class_name`),
  KEY `idx_major` (`major`),
  KEY `idx_status` (`status`),
  KEY `idx_enrollment_date` (`enrollment_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学生表';

-- 创建宿舍楼表
CREATE TABLE IF NOT EXISTS `buildings` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '宿舍楼ID',
  `name` varchar(100) NOT NULL COMMENT '宿舍楼名称',
  `code` varchar(50) NOT NULL COMMENT '宿舍楼编码',
  `type` varchar(20) DEFAULT NULL COMMENT '类型：MALE-男寝，FEMALE-女寝，MIXED-混合',
  `floors` int NOT NULL DEFAULT '1' COMMENT '楼层数',
  `total_rooms` int NOT NULL DEFAULT '0' COMMENT '总房间数',
  `total_capacity` int NOT NULL DEFAULT '0' COMMENT '总容量',
  `current_occupancy` int NOT NULL DEFAULT '0' COMMENT '当前入住人数',
  `address` varchar(255) DEFAULT NULL COMMENT '地址',
  `manager_id` bigint DEFAULT NULL COMMENT '管理员ID',
  `manager_name` varchar(50) DEFAULT NULL COMMENT '管理员姓名',
  `manager_phone` varchar(20) DEFAULT NULL COMMENT '管理员电话',
  `description` text COMMENT '描述',
  `facilities` text COMMENT '设施信息（JSON格式）',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-停用，1-启用，2-维修中',
  `remark` text COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-未删除，1-已删除',
  `create_by` varchar(50) DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(50) DEFAULT NULL COMMENT '更新人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`),
  KEY `idx_name` (`name`),
  KEY `idx_type` (`type`),
  KEY `idx_manager_id` (`manager_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='宿舍楼表';

-- 创建宿舍表
CREATE TABLE IF NOT EXISTS `dormitories` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '宿舍ID',
  `building_id` bigint NOT NULL COMMENT '宿舍楼ID',
  `room_no` varchar(20) NOT NULL COMMENT '房间号',
  `floor` int NOT NULL DEFAULT '1' COMMENT '楼层',
  `type` varchar(20) DEFAULT NULL COMMENT '类型：SINGLE-单人间，DOUBLE-双人间，QUAD-四人间，SIX-六人间',
  `capacity` int NOT NULL DEFAULT '4' COMMENT '容量',
  `current_occupancy` int NOT NULL DEFAULT '0' COMMENT '当前入住人数',
  `area` decimal(8,2) DEFAULT NULL COMMENT '面积（平方米）',
  `beds` int NOT NULL DEFAULT '4' COMMENT '床位数',
  `desks` int NOT NULL DEFAULT '4' COMMENT '桌子数',
  `chairs` int NOT NULL DEFAULT '4' COMMENT '椅子数',
  `wardrobes` int NOT NULL DEFAULT '4' COMMENT '衣柜数',
  `balcony` tinyint NOT NULL DEFAULT '0' COMMENT '是否有阳台：0-无，1-有',
  `toilet` tinyint NOT NULL DEFAULT '0' COMMENT '是否有独立卫生间：0-无，1-有',
  `air_conditioning` tinyint NOT NULL DEFAULT '0' COMMENT '是否有空调：0-无，1-有',
  `water_heater` tinyint NOT NULL DEFAULT '0' COMMENT '是否有热水器：0-无，1-有',
  `network` tinyint NOT NULL DEFAULT '1' COMMENT '是否有网络：0-无，1-有',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-停用，1-正常，2-维修中，3-已满',
  `description` text COMMENT '描述',
  `facilities` text COMMENT '设施详情（JSON格式）',
  `remark` text COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-未删除，1-已删除',
  `create_by` varchar(50) DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(50) DEFAULT NULL COMMENT '更新人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_building_room` (`building_id`,`room_no`),
  KEY `idx_building_id` (`building_id`),
  KEY `idx_room_no` (`room_no`),
  KEY `idx_floor` (`floor`),
  KEY `idx_type` (`type`),
  KEY `idx_status` (`status`),
  KEY `idx_capacity` (`capacity`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='宿舍表';

-- 创建入住记录表
CREATE TABLE IF NOT EXISTS `check_in_records` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `student_id` bigint NOT NULL COMMENT '学生ID',
  `dormitory_id` bigint NOT NULL COMMENT '宿舍ID',
  `bed_number` varchar(10) DEFAULT NULL COMMENT '床位号',
  `check_in_date` date NOT NULL COMMENT '入住日期',
  `expected_checkout_date` date DEFAULT NULL COMMENT '预计退宿日期',
  `actual_checkout_date` date DEFAULT NULL COMMENT '实际退宿日期',
  `check_in_type` varchar(20) DEFAULT 'FORMAL' COMMENT '入住类型：FORMAL-正式，TEMPORARY-临时',
  `status` varchar(20) DEFAULT 'CHECKED_IN' COMMENT '状态：PENDING-待审批，APPROVED-已批准，REJECTED-已拒绝，CHECKED_IN-已入住，CHECKED_OUT-已退宿',
  `application_reason` text COMMENT '申请原因',
  `approval_time` datetime DEFAULT NULL COMMENT '审批时间',
  `approver_id` bigint DEFAULT NULL COMMENT '审批人ID',
  `approver_name` varchar(50) DEFAULT NULL COMMENT '审批人姓名',
  `approval_remark` text COMMENT '审批备注',
  `checkout_reason` text COMMENT '退宿原因',
  `checkout_processor_id` bigint DEFAULT NULL COMMENT '退宿处理人ID',
  `checkout_processor_name` varchar(50) DEFAULT NULL COMMENT '退宿处理人姓名',
  `deposit_amount` decimal(10,2) DEFAULT '0.00' COMMENT '押金金额',
  `deposit_refund_amount` decimal(10,2) DEFAULT '0.00' COMMENT '押金退还金额',
  `deposit_refund_date` datetime DEFAULT NULL COMMENT '押金退还时间',
  `keys_returned` tinyint NOT NULL DEFAULT '0' COMMENT '钥匙是否归还：0-未归还，1-已归还',
  `equipment_status` text COMMENT '设备状态（JSON格式）',
  `remark` text COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-未删除，1-已删除',
  `create_by` varchar(50) DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(50) DEFAULT NULL COMMENT '更新人',
  PRIMARY KEY (`id`),
  KEY `idx_student_id` (`student_id`),
  KEY `idx_dormitory_id` (`dormitory_id`),
  KEY `idx_check_in_date` (`check_in_date`),
  KEY `idx_status` (`status`),
  KEY `idx_check_in_type` (`check_in_type`),
  KEY `idx_approver_id` (`approver_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='入住记录表';

-- 创建费用表
CREATE TABLE IF NOT EXISTS `fees` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '费用ID',
  `fee_type` int NOT NULL COMMENT '费用类型：1-住宿费，2-水电费，3-网费，4-其他费用',
  `fee_name` varchar(100) NOT NULL COMMENT '费用名称',
  `description` text COMMENT '费用描述',
  `amount` decimal(10,2) NOT NULL COMMENT '费用金额',
  `billing_cycle` int NOT NULL DEFAULT '1' COMMENT '计费周期：1-按月，2-按学期，3-按年，4-一次性',
  `student_id` bigint NOT NULL COMMENT '学生ID',
  `student_name` varchar(50) NOT NULL COMMENT '学生姓名',
  `student_no` varchar(20) NOT NULL COMMENT '学号',
  `room_id` bigint NOT NULL COMMENT '宿舍ID',
  `room_no` varchar(20) NOT NULL COMMENT '房间号',
  `building_id` bigint NOT NULL COMMENT '宿舍楼ID',
  `building_name` varchar(100) NOT NULL COMMENT '宿舍楼名称',
  `start_time` date NOT NULL COMMENT '计费开始时间',
  `end_time` date NOT NULL COMMENT '计费结束时间',
  `due_date` date NOT NULL COMMENT '缴费截止日期',
  `paid_amount` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '已付金额',
  `unpaid_amount` decimal(10,2) GENERATED ALWAYS AS (amount - paid_amount) STORED COMMENT '未付金额',
  `payment_status` int NOT NULL DEFAULT '0' COMMENT '支付状态：0-未支付，1-部分支付，2-已支付',
  `payment_method` varchar(20) DEFAULT NULL COMMENT '支付方式：1-现金，2-银行转账，3-微信，4-支付宝',
  `payment_time` datetime DEFAULT NULL COMMENT '支付时间',
  `payment_transaction_id` varchar(100) DEFAULT NULL COMMENT '支付交易号',
  `invoice_number` varchar(50) DEFAULT NULL COMMENT '发票号码',
  `receipt_number` varchar(50) DEFAULT NULL COMMENT '收据号码',
  `late_fee` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '滞纳金',
  `discount_amount` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '优惠金额',
  `remark` text COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-未删除，1-已删除',
  `create_by` varchar(50) DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(50) DEFAULT NULL COMMENT '更新人',
  PRIMARY KEY (`id`),
  KEY `idx_fee_type` (`fee_type`),
  KEY `idx_student_id` (`student_id`),
  KEY `idx_room_id` (`room_id`),
  KEY `idx_building_id` (`building_id`),
  KEY `idx_start_time` (`start_time`),
  KEY `idx_end_time` (`end_time`),
  KEY `idx_due_date` (`due_date`),
  KEY `idx_payment_status` (`payment_status`),
  KEY `idx_payment_time` (`payment_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='费用表';

-- 创建访客记录表
CREATE TABLE IF NOT EXISTS `visitor_records` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `visitor_name` varchar(50) NOT NULL COMMENT '访客姓名',
  `visitor_phone` varchar(20) DEFAULT NULL COMMENT '访客电话',
  `visitor_id_card` varchar(18) DEFAULT NULL COMMENT '访客身份证号',
  `visitor_relationship` varchar(50) DEFAULT NULL COMMENT '与学生关系',
  `visit_student_id` bigint NOT NULL COMMENT '被访学生ID',
  `visit_student_name` varchar(50) NOT NULL COMMENT '被访学生姓名',
  `visit_student_no` varchar(20) NOT NULL COMMENT '被访学生学号',
  `dormitory_id` bigint NOT NULL COMMENT '宿舍ID',
  `dormitory_no` varchar(20) NOT NULL COMMENT '宿舍号',
  `building_id` bigint NOT NULL COMMENT '宿舍楼ID',
  `building_name` varchar(100) NOT NULL COMMENT '宿舍楼名称',
  `expected_visit_time` datetime NOT NULL COMMENT '预计访问时间',
  `actual_arrival_time` datetime DEFAULT NULL COMMENT '实际到达时间',
  `leave_time` datetime DEFAULT NULL COMMENT '离开时间',
  `visit_purpose` text COMMENT '访问事由',
  `visit_status` varchar(20) DEFAULT 'REGISTERED' COMMENT '访问状态：REGISTERED-已登记，ARRIVED-已到达，LEFT-已离开，CANCELLED-已取消',
  `registrar_id` bigint NOT NULL COMMENT '登记人ID',
  `registrar_name` varchar(50) NOT NULL COMMENT '登记人姓名',
  `approved_by_id` bigint DEFAULT NULL COMMENT '审批人ID',
  `approved_by_name` varchar(50) DEFAULT NULL COMMENT '审批人姓名',
  `approval_time` datetime DEFAULT NULL COMMENT '审批时间',
  `items_brought` text COMMENT '携带物品',
  `remarks` text COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-未删除，1-已删除',
  `create_by` varchar(50) DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(50) DEFAULT NULL COMMENT '更新人',
  PRIMARY KEY (`id`),
  KEY `idx_visitor_name` (`visitor_name`),
  KEY `idx_visit_student_id` (`visit_student_id`),
  KEY `idx_dormitory_id` (`dormitory_id`),
  KEY `idx_building_id` (`building_id`),
  KEY `idx_expected_visit_time` (`expected_visit_time`),
  KEY `idx_visit_status` (`visit_status`),
  KEY `idx_registrar_id` (`registrar_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='访客记录表';

-- 创建维修记录表
CREATE TABLE IF NOT EXISTS `repair_records` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `room_id` bigint NOT NULL COMMENT '宿舍ID',
  `room_no` varchar(20) NOT NULL COMMENT '房间号',
  `building_name` varchar(100) NOT NULL COMMENT '宿舍楼名称',
  `student_id` bigint NOT NULL COMMENT '报修学生ID',
  `student_name` varchar(50) NOT NULL COMMENT '报修学生姓名',
  `student_phone` varchar(20) DEFAULT NULL COMMENT '学生联系电话',
  `title` varchar(200) NOT NULL COMMENT '维修标题',
  `description` text NOT NULL COMMENT '问题描述',
  `type` varchar(50) NOT NULL COMMENT '维修类型：ELECTRICAL-电器，PLUMBING-水管，FURNITURE-家具，DOOR-门窗，OTHER-其他',
  `priority` int NOT NULL DEFAULT '1' COMMENT '优先级：1-低，2-中，3-高，4-紧急',
  `status` varchar(20) DEFAULT 'PENDING' COMMENT '状态：PENDING-待处理，ASSIGNED-已分配，IN_PROGRESS-处理中，COMPLETED-已完成，CANCELLED-已取消',
  `report_time` datetime NOT NULL COMMENT '报修时间',
  `assign_time` datetime DEFAULT NULL COMMENT '分配时间',
  `start_time` datetime DEFAULT NULL COMMENT '开始处理时间',
  `complete_time` datetime DEFAULT NULL COMMENT '完成时间',
  `assigned_to_id` bigint DEFAULT NULL COMMENT '分配给的维修人员ID',
  `assigned_to_name` varchar(50) DEFAULT NULL COMMENT '分配给的维修人员姓名',
  `assigned_to_phone` varchar(20) DEFAULT NULL COMMENT '维修人员电话',
  `estimated_cost` decimal(10,2) DEFAULT NULL COMMENT '预估费用',
  `actual_cost` decimal(10,2) DEFAULT NULL COMMENT '实际费用',
  `materials_used` text COMMENT '使用的材料',
  `work_description` text COMMENT '维修工作描述',
  `images` text COMMENT '相关图片（JSON格式）',
  `student_rating` int DEFAULT NULL COMMENT '学生评分（1-5）',
  `student_feedback` text COMMENT '学生反馈',
  `completion_notes` text COMMENT '完成备注',
  `remark` text COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-未删除，1-已删除',
  `create_by` varchar(50) DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(50) DEFAULT NULL COMMENT '更新人',
  PRIMARY KEY (`id`),
  KEY `idx_room_id` (`room_id`),
  KEY `idx_student_id` (`student_id`),
  KEY `idx_type` (`type`),
  KEY `idx_priority` (`priority`),
  KEY `idx_status` (`status`),
  KEY `idx_report_time` (`report_time`),
  KEY `idx_assigned_to_id` (`assigned_to_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='维修记录表';

-- 创建卫生检查记录表
CREATE TABLE IF NOT EXISTS `inspection_records` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `room_id` bigint NOT NULL COMMENT '宿舍ID',
  `room_no` varchar(20) NOT NULL COMMENT '房间号',
  `building_id` bigint NOT NULL COMMENT '宿舍楼ID',
  `building_name` varchar(100) NOT NULL COMMENT '宿舍楼名称',
  `inspection_date` date NOT NULL COMMENT '检查日期',
  `inspector_id` bigint NOT NULL COMMENT '检查员ID',
  `inspector_name` varchar(50) NOT NULL COMMENT '检查员姓名',
  `score` int NOT NULL COMMENT '卫生评分（0-100）',
  `level` varchar(20) NOT NULL COMMENT '卫生等级：EXCELLENT-优秀，GOOD-良好，PASS-合格，FAIL-不合格',
  `level_text` varchar(50) NOT NULL COMMENT '等级描述',
  `ground_cleanliness` int DEFAULT NULL COMMENT '地面清洁度（0-25）',
  `bed_tidy` int DEFAULT NULL COMMENT '床铺整理（0-25）',
  `desk_organization` int DEFAULT NULL COMMENT '桌面整洁（0-25）',
  `garbage_disposal` int DEFAULT NULL COMMENT '垃圾处理（0-25）',
  `issues` text COMMENT '发现问题',
  `improvement_suggestions` text COMMENT '改进建议',
  `praise_points` text COMMENT '表扬点',
  `images` text COMMENT '检查图片（JSON格式）',
  `follow_up_required` tinyint NOT NULL DEFAULT '0' COMMENT '是否需要跟进：0-否，1-是',
  `follow_up_date` date DEFAULT NULL COMMENT '跟进检查日期',
  `student_responsible` text COMMENT '负责学生名单',
  `remarks` text COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-未删除，1-已删除',
  `create_by` varchar(50) DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(50) DEFAULT NULL COMMENT '更新人',
  PRIMARY KEY (`id`),
  KEY `idx_room_id` (`room_id`),
  KEY `idx_building_id` (`building_id`),
  KEY `idx_inspection_date` (`inspection_date`),
  KEY `idx_inspector_id` (`inspector_id`),
  KEY `idx_score` (`score`),
  KEY `idx_level` (`level`),
  KEY `idx_follow_up_required` (`follow_up_required`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='卫生检查记录表';

-- 创建系统日志表
CREATE TABLE IF NOT EXISTS `system_logs` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `user_id` bigint DEFAULT NULL COMMENT '用户ID',
  `username` varchar(50) DEFAULT NULL COMMENT '用户名',
  `operation` varchar(100) NOT NULL COMMENT '操作类型',
  `method` varchar(200) DEFAULT NULL COMMENT '请求方法',
  `params` text COMMENT '请求参数',
  `time` bigint NOT NULL COMMENT '执行时长(毫秒)',
  `ip` varchar(64) DEFAULT NULL COMMENT 'IP地址',
  `user_agent` varchar(500) DEFAULT NULL COMMENT '用户代理',
  `result` text COMMENT '操作结果',
  `status` int NOT NULL DEFAULT '1' COMMENT '状态：0-失败，1-成功',
  `error_msg` text COMMENT '错误信息',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_username` (`username`),
  KEY `idx_operation` (`operation`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_ip` (`ip`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统日志表';

-- 创建操作权限表 (用于未来的权限控制)
CREATE TABLE IF NOT EXISTS `permissions` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '权限ID',
  `name` varchar(100) NOT NULL COMMENT '权限名称',
  `code` varchar(100) NOT NULL COMMENT '权限编码',
  `description` varchar(255) DEFAULT NULL COMMENT '权限描述',
  `type` varchar(20) NOT NULL DEFAULT 'MENU' COMMENT '权限类型：MENU-菜单，BUTTON-按钮，API-接口',
  `parent_id` bigint DEFAULT '0' COMMENT '父权限ID',
  `path` varchar(200) DEFAULT NULL COMMENT '权限路径',
  `component` varchar(200) DEFAULT NULL COMMENT '组件路径',
  `icon` varchar(100) DEFAULT NULL COMMENT '图标',
  `sort_order` int NOT NULL DEFAULT '0' COMMENT '排序',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_type` (`type`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='权限表';

-- 插入默认管理员用户
-- 密码: admin123 (BCrypt加密)
INSERT IGNORE INTO `users` (`id`, `username`, `password`, `real_name`, `email`, `phone`, `role`, `status`, `gender`, `remark`, `create_by`, `update_by`)
VALUES (1, 'admin', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '系统管理员', 'admin@dormitory.com', '13800138000', 'ADMIN', 1, 1, '系统默认管理员账户', 'system', 'system');

-- 插入默认权限数据
INSERT IGNORE INTO `permissions` (`id`, `name`, `code`, `description`, `type`, `parent_id`, `path`, `icon`, `sort_order`, `status`) VALUES
(1, '系统管理', 'system', '系统管理模块', 'MENU', 0, '/system', 'el-icon-setting', 1, 1),
(2, '用户管理', 'system:user', '用户管理', 'MENU', 1, '/system/users', 'el-icon-user', 1, 1),
(3, '角色管理', 'system:role', '角色管理', 'MENU', 1, '/system/roles', 'el-icon-user-solid', 2, 1),
(4, '菜单管理', 'system:menu', '菜单管理', 'MENU', 1, '/system/menus', 'el-icon-menu', 3, 1),
(5, '日志管理', 'system:log', '日志管理', 'MENU', 1, '/system/logs', 'el-icon-document', 4, 1),
(10, '宿舍管理', 'dormitory', '宿舍管理模块', 'MENU', 0, '/dormitory', 'el-icon-house', 2, 1),
(11, '宿舍楼管理', 'dormitory:building', '宿舍楼管理', 'MENU', 10, '/dormitory/buildings', 'el-icon-office-building', 1, 1),
(12, '宿舍管理', 'dormitory:room', '宿舍管理', 'MENU', 10, '/dormitory/rooms', 'el-icon-school', 2, 1),
(13, '入住管理', 'checkin', '入住管理模块', 'MENU', 0, '/checkin', 'el-icon-check', 3, 1),
(14, '入住申请', 'checkin:application', '入住申请', 'MENU', 13, '/checkin/applications', 'el-icon-document-add', 1, 1),
(15, '入住记录', 'checkin:record', '入住记录', 'MENU', 13, '/checkin/records', 'el-icon-document-checked', 2, 1),
(20, '学生管理', 'student', '学生管理模块', 'MENU', 0, '/student', 'el-icon-user', 4, 1),
(21, '学生信息', 'student:info', '学生信息', 'MENU', 20, '/student/info', 'el-icon-info', 1, 1),
(30, '费用管理', 'fee', '费用管理模块', 'MENU', 0, '/fee', 'el-icon-money', 5, 1),
(31, '费用设置', 'fee:setting', '费用设置', 'MENU', 30, '/fee/settings', 'el-icon-setting', 1, 1),
(32, '缴费管理', 'fee:payment', '缴费管理', 'MENU', 30, '/fee/payments', 'el-icon-s-finance', 2, 1),
(40, '访客管理', 'visitor', '访客管理模块', 'MENU', 0, '/visitor', 'el-icon-user', 6, 1),
(41, '访客登记', 'visitor:register', '访客登记', 'MENU', 40, '/visitor/register', 'el-icon-edit', 1, 1),
(42, '访客记录', 'visitor:record', '访客记录', 'MENU', 40, '/visitor/records', 'el-icon-document', 2, 1),
(50, '维修管理', 'repair', '维修管理模块', 'MENU', 0, '/repair', 'el-icon-tools', 7, 1),
(51, '维修申请', 'repair:application', '维修申请', 'MENU', 50, '/repair/applications', 'el-icon-plus', 1, 1),
(52, '维修记录', 'repair:record', '维修记录', 'MENU', 50, '/repair/records', 'el-icon-document', 2, 1),
(60, '卫生检查', 'inspection', '卫生检查模块', 'MENU', 0, '/inspection', 'el-icon-brush', 8, 1),
(61, '检查记录', 'inspection:record', '检查记录', 'MENU', 60, '/inspection/records', 'el-icon-document', 1, 1),
(70, '统计分析', 'statistics', '统计分析模块', 'MENU', 0, '/statistics', 'el-icon-data-line', 9, 1),
(71, '入住统计', 'statistics:checkin', '入住统计', 'MENU', 70, '/statistics/checkin', 'el-icon-data-board', 1, 1),
(72, '费用统计', 'statistics:fee', '费用统计', 'MENU', 70, '/statistics/fee', 'el-icon-s-marketing', 2, 1),
(73, '维修统计', 'statistics:repair', '维修统计', 'MENU', 70, '/statistics/repair', 'el-icon-s-tools', 3, 1),
(80, '个人中心', 'profile', '个人中心', 'MENU', 0, '/profile', 'el-icon-user-solid', 10, 1);

-- 创建索引以提高查询性能
CREATE INDEX IF NOT EXISTS idx_users_role_status ON users(role, status);
CREATE INDEX IF NOT EXISTS idx_students_grade_status ON students(grade, status);
CREATE INDEX IF NOT EXISTS idx_dormitories_building_floor ON dormitories(building_id, floor);
CREATE INDEX IF NOT EXISTS idx_fees_student_payment ON fees(student_id, payment_status);
CREATE INDEX IF NOT EXISTS idx_checkin_student_dormitory ON check_in_records(student_id, dormitory_id);
CREATE INDEX IF NOT EXISTS idx_visitor_student_time ON visitor_records(visit_student_id, expected_visit_time);
CREATE INDEX IF NOT EXISTS idx_repair_room_status ON repair_records(room_id, status);
CREATE INDEX IF NOT EXISTS idx_inspection_room_score ON inspection_records(room_id, score);

-- 创建视图以便于查询
CREATE OR REPLACE VIEW v_student_dormitory AS
SELECT
    s.id as student_id,
    s.student_no,
    s.name as student_name,
    s.gender,
    s.grade,
    s.class_name,
    s.major,
    s.phone as student_phone,
    s.email as student_email,
    d.id as dormitory_id,
    d.room_no,
    d.building_id,
    b.name as building_name,
    b.type as building_type,
    d.capacity,
    d.current_occupancy,
    cir.check_in_date,
    cir.expected_checkout_date,
    cir.status as check_in_status,
    cir.bed_number
FROM students s
LEFT JOIN check_in_records cir ON s.id = cir.student_id AND cir.status = 'CHECKED_IN'
LEFT JOIN dormitories d ON cir.dormitory_id = d.id
LEFT JOIN buildings b ON d.building_id = b.id
WHERE s.deleted = 0 AND s.status = 1;

-- 创建存储过程用于统计
DELIMITER //

CREATE PROCEDURE IF NOT EXISTS sp_get_dormitory_statistics(IN p_building_id BIGINT, IN p_date DATE)
BEGIN
    SELECT
        b.id as building_id,
        b.name as building_name,
        COUNT(d.id) as total_rooms,
        SUM(d.capacity) as total_capacity,
        SUM(COALESCE(d.current_occupancy, 0)) as current_occupancy,
        SUM(CASE WHEN d.current_occupancy = 0 THEN 1 ELSE 0 END) as empty_rooms,
        SUM(CASE WHEN d.current_occupancy = d.capacity THEN 1 ELSE 0 END) as full_rooms,
        ROUND(SUM(COALESCE(d.current_occupancy, 0)) * 100.0 / SUM(d.capacity), 2) as occupancy_rate
    FROM buildings b
    LEFT JOIN dormitories d ON b.id = d.building_id AND d.deleted = 0
    WHERE b.deleted = 0 AND b.status = 1
    AND (p_building_id IS NULL OR b.id = p_building_id)
    GROUP BY b.id, b.name;
END //

DELIMITER ;

-- 显示初始化完成信息
SELECT 'Database initialization completed successfully!' as message;
SELECT COUNT(*) as total_tables FROM information_schema.tables
WHERE table_schema = 'dormitory_management' AND table_type = 'BASE TABLE';