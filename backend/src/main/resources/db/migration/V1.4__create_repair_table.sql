-- 创建报修表
CREATE TABLE `repair` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `room_id` bigint NOT NULL COMMENT '宿舍ID',
  `room_no` varchar(20) NOT NULL COMMENT '宿舍号',
  `building_name` varchar(100) NOT NULL COMMENT '楼栋名称',
  `student_id` bigint NOT NULL COMMENT '报修学生ID',
  `student_name` varchar(50) NOT NULL COMMENT '报修学生姓名',
  `title` varchar(200) NOT NULL COMMENT '报修标题',
  `description` text COMMENT '报修描述',
  `type` varchar(20) NOT NULL COMMENT '报修类型：water-水电，electric-电路，door-门窗，furniture-家具，other-其他',
  `priority` int NOT NULL DEFAULT '2' COMMENT '优先级：1-低，2-中，3-高',
  `status` int NOT NULL DEFAULT '1' COMMENT '状态：1-待处理，2-处理中，3-已完成，4-已取消',
  `report_time` datetime NOT NULL COMMENT '报修时间',
  `handle_time` datetime COMMENT '处理时间',
  `complete_time` datetime COMMENT '完成时间',
  `handler_id` bigint COMMENT '处理人ID',
  `handler_name` varchar(50) COMMENT '处理人姓名',
  `handle_remark` text COMMENT '处理备注',
  `images` text COMMENT '报修图片，多个图片用逗号分隔',
  `contact_phone` varchar(20) COMMENT '联系电话',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` varchar(50) COMMENT '创建人',
  `update_by` varchar(50) COMMENT '更新人',
  `deleted` int NOT NULL DEFAULT '0' COMMENT '逻辑删除标记：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_room_id` (`room_id`),
  KEY `idx_student_id` (`student_id`),
  KEY `idx_status` (`status`),
  KEY `idx_priority` (`priority`),
  KEY `idx_report_time` (`report_time`),
  KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='报修表';

-- 插入示例数据
INSERT INTO `repair` (`room_id`, `room_no`, `building_name`, `student_id`, `student_name`, `title`, `description`, `type`, `priority`, `status`, `report_time`, `contact_phone`, `create_by`, `update_by`) VALUES
(1, '101', 'A1栋', 1, '张三', '空调漏水', '宿舍空调漏水严重，需要维修', 'water', 3, 2, '2024-12-15 10:30:00', '13800138000', '系统', '系统'),
(2, '102', 'A1栋', 2, '李四', '门锁损坏', '宿舍门锁损坏，无法正常锁门', 'door', 2, 1, '2024-12-15 14:20:00', '13800138001', '系统', '系统'),
(3, '201', 'A2栋', 3, '王五', '灯泡不亮', '宿舍主灯不亮，影响夜间学习', 'electric', 2, 3, '2024-12-14 16:45:00', '13800138002', '系统', '系统');