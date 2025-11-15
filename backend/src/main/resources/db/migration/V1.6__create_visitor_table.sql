-- 创建访客登记表
CREATE TABLE `visitor` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `visitor_name` varchar(100) NOT NULL COMMENT '访客姓名',
  `visitor_phone` varchar(20) NOT NULL COMMENT '访客手机号',
  `visitor_id_card` varchar(18) DEFAULT NULL COMMENT '访客身份证号',
  `visit_student_name` varchar(100) NOT NULL COMMENT '被访学生姓名',
  `visit_student_no` varchar(50) DEFAULT NULL COMMENT '被访学生学号',
  `room_id` bigint NOT NULL COMMENT '访问宿舍ID',
  `room_no` varchar(20) DEFAULT NULL COMMENT '访问宿舍号',
  `building_id` bigint DEFAULT NULL COMMENT '楼栋ID',
  `building_name` varchar(100) DEFAULT NULL COMMENT '楼栋名称',
  `visit_purpose` text COMMENT '访问事由',
  `expected_visit_time` datetime NOT NULL COMMENT '预计访问时间',
  `actual_arrival_time` datetime DEFAULT NULL COMMENT '实际到达时间',
  `leave_time` datetime DEFAULT NULL COMMENT '离开时间',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '访问状态：0-待访问，1-访问中，2-已完成，3-已取消',
  `remarks` text COMMENT '备注信息',
  `registrar_id` bigint DEFAULT NULL COMMENT '登记人ID',
  `registrar_name` varchar(100) DEFAULT NULL COMMENT '登记人姓名',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_visitor_name` (`visitor_name`),
  KEY `idx_visitor_phone` (`visitor_phone`),
  KEY `idx_visit_student` (`visit_student_name`, `visit_student_no`),
  KEY `idx_room_id` (`room_id`),
  KEY `idx_expected_visit_time` (`expected_visit_time`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='访客登记表';

-- 添加外键约束
ALTER TABLE `visitor` ADD CONSTRAINT `fk_visitor_room_id`
FOREIGN KEY (`room_id`) REFERENCES `dormitory` (`id`) ON DELETE CASCADE;