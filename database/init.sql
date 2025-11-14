-- 宿舍管理系统数据库初始化脚本
-- 创建数据库
CREATE DATABASE IF NOT EXISTS dormitory_management DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE dormitory_management;

-- 用户表
CREATE TABLE IF NOT EXISTS `sys_user` (
    `id` BIGINT NOT NULL COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '密码',
    `name` VARCHAR(50) NOT NULL COMMENT '姓名',
    `gender` TINYINT DEFAULT 1 COMMENT '性别(1:男,2:女)',
    `phone` VARCHAR(20) COMMENT '手机号',
    `email` VARCHAR(100) COMMENT '邮箱',
    `role` VARCHAR(20) DEFAULT 'user' COMMENT '角色(admin,user)',
    `status` TINYINT DEFAULT 1 COMMENT '状态(0:禁用,1:启用)',
    `avatar` VARCHAR(255) COMMENT '头像',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` VARCHAR(50) COMMENT '创建人',
    `update_by` VARCHAR(50) COMMENT '更新人',
    `deleted` TINYINT DEFAULT 0 COMMENT '删除标识(0:未删除,1:已删除)',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 宿舍楼表
CREATE TABLE IF NOT EXISTS `dorm_building` (
    `id` BIGINT NOT NULL COMMENT '楼栋ID',
    `building_no` VARCHAR(20) NOT NULL COMMENT '楼栋号',
    `building_name` VARCHAR(100) NOT NULL COMMENT '楼栋名称',
    `floor_count` INT DEFAULT 4 COMMENT '楼层数',
    `description` VARCHAR(255) COMMENT '描述',
    `status` TINYINT DEFAULT 1 COMMENT '状态(0:停用,1:启用)',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` VARCHAR(50) COMMENT '创建人',
    `update_by` VARCHAR(50) COMMENT '更新人',
    `deleted` TINYINT DEFAULT 0 COMMENT '删除标识(0:未删除,1:已删除)',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_building_no` (`building_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='宿舍楼表';

-- 宿舍表
CREATE TABLE IF NOT EXISTS `dorm_room` (
    `id` BIGINT NOT NULL COMMENT '宿舍ID',
    `building_id` BIGINT NOT NULL COMMENT '楼栋ID',
    `room_no` VARCHAR(20) NOT NULL COMMENT '宿舍号',
    `floor` INT NOT NULL COMMENT '楼层',
    `bed_count` INT DEFAULT 4 COMMENT '床位数',
    `gender` TINYINT DEFAULT 1 COMMENT '性别限制(1:男,2:女,3:不限)',
    `type` VARCHAR(20) DEFAULT 'standard' COMMENT '宿舍类型(standard:标准,luxury:豪华)',
    `status` TINYINT DEFAULT 1 COMMENT '状态(0:停用,1:启用)',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` VARCHAR(50) COMMENT '创建人',
    `update_by` VARCHAR(50) COMMENT '更新人',
    `deleted` TINYINT DEFAULT 0 COMMENT '删除标识(0:未删除,1:已删除)',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_building_room` (`building_id`, `room_no`),
    KEY `idx_building_id` (`building_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='宿舍表';

-- 学生表
CREATE TABLE IF NOT EXISTS `student` (
    `id` BIGINT NOT NULL COMMENT '学生ID',
    `student_no` VARCHAR(20) NOT NULL COMMENT '学号',
    `name` VARCHAR(50) NOT NULL COMMENT '姓名',
    `gender` TINYINT DEFAULT 1 COMMENT '性别(1:男,2:女)',
    `birth_date` DATE COMMENT '出生日期',
    `phone` VARCHAR(20) COMMENT '手机号',
    `email` VARCHAR(100) COMMENT '邮箱',
    `id_card` VARCHAR(18) COMMENT '身份证号',
    `college` VARCHAR(100) COMMENT '学院',
    `major` VARCHAR(100) COMMENT '专业',
    `class_name` VARCHAR(50) COMMENT '班级',
    `grade` VARCHAR(20) COMMENT '年级',
    `address` VARCHAR(255) COMMENT '家庭地址',
    `emergency_contact` VARCHAR(50) COMMENT '紧急联系人',
    `emergency_phone` VARCHAR(20) COMMENT '紧急联系人电话',
    `status` TINYINT DEFAULT 1 COMMENT '状态(0:离校,1:在校)',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` VARCHAR(50) COMMENT '创建人',
    `update_by` VARCHAR(50) COMMENT '更新人',
    `deleted` TINYINT DEFAULT 0 COMMENT '删除标识(0:未删除,1:已删除)',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_student_no` (`student_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生表';

-- 入住记录表
CREATE TABLE IF NOT EXISTS `checkin_record` (
    `id` BIGINT NOT NULL COMMENT '记录ID',
    `student_id` BIGINT NOT NULL COMMENT '学生ID',
    `room_id` BIGINT NOT NULL COMMENT '宿舍ID',
    `bed_no` INT NOT NULL COMMENT '床位号',
    `checkin_date` DATE NOT NULL COMMENT '入住日期',
    `checkout_date` DATE COMMENT '退宿日期',
    `status` TINYINT DEFAULT 1 COMMENT '状态(1:入住中,2:已退宿)',
    `remark` VARCHAR(255) COMMENT '备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` VARCHAR(50) COMMENT '创建人',
    `update_by` VARCHAR(50) COMMENT '更新人',
    `deleted` TINYINT DEFAULT 0 COMMENT '删除标识(0:未删除,1:已删除)',
    PRIMARY KEY (`id`),
    KEY `idx_student_id` (`student_id`),
    KEY `idx_room_id` (`room_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='入住记录表';

-- 报修表
CREATE TABLE IF NOT EXISTS `repair` (
    `id` BIGINT NOT NULL COMMENT '报修ID',
    `room_id` BIGINT NOT NULL COMMENT '宿舍ID',
    `student_id` BIGINT COMMENT '学生ID',
    `title` VARCHAR(100) NOT NULL COMMENT '报修标题',
    `description` TEXT COMMENT '问题描述',
    `type` VARCHAR(50) COMMENT '报修类型(water:水电,furniture:家具,other:其他)',
    `priority` TINYINT DEFAULT 1 COMMENT '优先级(1:低,2:中,3:高)',
    `status` TINYINT DEFAULT 1 COMMENT '状态(1:待处理,2:处理中,3:已完成,4:已关闭)',
    `report_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '报修时间',
    `handle_time` DATETIME COMMENT '处理时间',
    `complete_time` DATETIME COMMENT '完成时间',
    `handler_id` BIGINT COMMENT '处理人ID',
    `handle_remark` VARCHAR(255) COMMENT '处理备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` VARCHAR(50) COMMENT '创建人',
    `update_by` VARCHAR(50) COMMENT '更新人',
    `deleted` TINYINT DEFAULT 0 COMMENT '删除标识(0:未删除,1:已删除)',
    PRIMARY KEY (`id`),
    KEY `idx_room_id` (`room_id`),
    KEY `idx_student_id` (`student_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报修表';

-- 卫生检查表
CREATE TABLE IF NOT EXISTS `inspection` (
    `id` BIGINT NOT NULL COMMENT '检查ID',
    `room_id` BIGINT NOT NULL COMMENT '宿舍ID',
    `inspector_id` BIGINT NOT NULL COMMENT '检查人ID',
    `inspection_date` DATE NOT NULL COMMENT '检查日期',
    `score` INT DEFAULT 100 COMMENT '卫生分数(0-100)',
    `level` TINYINT DEFAULT 1 COMMENT '等级(1:优秀,2:良好,3:一般,4:差)',
    `problems` TEXT COMMENT '问题描述',
    `photos` VARCHAR(500) COMMENT '照片地址(多个用逗号分隔)',
    `remark` VARCHAR(255) COMMENT '备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` VARCHAR(50) COMMENT '创建人',
    `update_by` VARCHAR(50) COMMENT '更新人',
    `deleted` TINYINT DEFAULT 0 COMMENT '删除标识(0:未删除,1:已删除)',
    PRIMARY KEY (`id`),
    KEY `idx_room_id` (`room_id`),
    KEY `idx_inspector_id` (`inspector_id`),
    KEY `idx_inspection_date` (`inspection_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='卫生检查表';

-- 访客表
CREATE TABLE IF NOT EXISTS `visitor` (
    `id` BIGINT NOT NULL COMMENT '访客ID',
    `visitor_name` VARCHAR(50) NOT NULL COMMENT '访客姓名',
    `visitor_phone` VARCHAR(20) COMMENT '访客手机号',
    `visitor_id_card` VARCHAR(18) COMMENT '访客身份证号',
    `visit_student_id` BIGINT NOT NULL COMMENT '受访学生ID',
    `visit_room_id` BIGINT NOT NULL COMMENT '受访宿舍ID',
    `visit_reason` VARCHAR(255) COMMENT '来访事由',
    `visit_date` DATE NOT NULL COMMENT '来访日期',
    `entry_time` DATETIME COMMENT '进入时间',
    `exit_time` DATETIME COMMENT '离开时间',
    `status` TINYINT DEFAULT 1 COMMENT '状态(1:访问中,2:已离开)',
    `remark` VARCHAR(255) COMMENT '备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` VARCHAR(50) COMMENT '创建人',
    `update_by` VARCHAR(50) COMMENT '更新人',
    `deleted` TINYINT DEFAULT 0 COMMENT '删除标识(0:未删除,1:已删除)',
    PRIMARY KEY (`id`),
    KEY `idx_visit_student_id` (`visit_student_id`),
    KEY `idx_visit_room_id` (`visit_room_id`),
    KEY `idx_visit_date` (`visit_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='访客表';

-- 费用表
CREATE TABLE IF NOT EXISTS `fee` (
    `id` BIGINT NOT NULL COMMENT '费用ID',
    `room_id` BIGINT NOT NULL COMMENT '宿舍ID',
    `student_id` BIGINT COMMENT '学生ID(个人费用时填写)',
    `type` VARCHAR(50) NOT NULL COMMENT '费用类型(water:水费,electric:电费,rent:住宿费,other:其他)',
    `amount` DECIMAL(10,2) NOT NULL COMMENT '金额',
    `period` VARCHAR(20) COMMENT '费用周期(如:202401)',
    `status` TINYINT DEFAULT 1 COMMENT '状态(1:未缴费,2:已缴费)',
    `due_date` DATE COMMENT '缴费截止日期',
    `pay_date` DATE COMMENT '缴费日期',
    `remark` VARCHAR(255) COMMENT '备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` VARCHAR(50) COMMENT '创建人',
    `update_by` VARCHAR(50) COMMENT '更新人',
    `deleted` TINYINT DEFAULT 0 COMMENT '删除标识(0:未删除,1:已删除)',
    PRIMARY KEY (`id`),
    KEY `idx_room_id` (`room_id`),
    KEY `idx_student_id` (`student_id`),
    KEY `idx_type_period` (`type`, `period`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='费用表';

-- 插入初始数据
-- 插入管理员用户 (密码: 123456)
INSERT INTO `sys_user` (`id`, `username`, `password`, `name`, `gender`, `role`, `status`)
VALUES (1, 'admin', '$2a$10$7JB720yubVSOfvVWbfXCOOxjTOQcQjmrJF1ZM4nAVccp/.rkMlDWy', '系统管理员', 1, 'admin', 1);

-- 插入示例宿舍楼
INSERT INTO `dorm_building` (`id`, `building_no`, `building_name`, `floor_count`) VALUES
(1, 'A1', 'A1栋', 6),
(2, 'A2', 'A2栋', 6),
(3, 'B1', 'B1栋', 4),
(4, 'B2', 'B2栋', 4);

-- 插入示例宿舍
INSERT INTO `dorm_room` (`id`, `building_id`, `room_no`, `floor`, `bed_count`, `gender`) VALUES
(1, 1, '101', 1, 4, 1),
(2, 1, '102', 1, 4, 1),
(3, 1, '201', 2, 4, 1),
(4, 2, '101', 1, 4, 2),
(5, 2, '102', 1, 4, 2),
(6, 3, '101', 1, 6, 1),
(7, 3, '102', 1, 6, 1);

-- 插入示例学生
INSERT INTO `student` (`id`, `student_no`, `name`, `gender`, `college`, `major`, `class_name`, `grade`) VALUES
(1, '2024001', '张三', 1, '计算机学院', '软件工程', '软工1班', '2024级'),
(2, '2024002', '李四', 1, '计算机学院', '软件工程', '软工1班', '2024级'),
(3, '2024003', '王五', 2, '计算机学院', '软件工程', '软工2班', '2024级'),
(4, '2024004', '赵六', 2, '计算机学院', '软件工程', '软工2班', '2024级');

-- 插入示例入住记录
INSERT INTO `checkin_record` (`id`, `student_id`, `room_id`, `bed_no`, `checkin_date`, `status`) VALUES
(1, 1, 1, 1, '2024-09-01', 1),
(2, 2, 1, 2, '2024-09-01', 1),
(3, 3, 4, 1, '2024-09-01', 1),
(4, 4, 4, 2, '2024-09-01', 1);

COMMIT;