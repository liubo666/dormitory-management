-- 创建卫生检查表
CREATE TABLE `t_inspection` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `room_id` bigint NOT NULL COMMENT '宿舍ID',
  `inspection_date` datetime NOT NULL COMMENT '检查日期',
  `score` int NOT NULL COMMENT '卫生分数(0-100)',
  `level` varchar(20) NOT NULL COMMENT '等级：excellent-优秀，good-良好，pass-合格，fail-不合格',
  `inspector_id` bigint DEFAULT NULL COMMENT '检查人ID',
  `inspector_name` varchar(50) DEFAULT NULL COMMENT '检查人姓名',
  `remarks` text COMMENT '备注',
  `issues` text COMMENT '详细问题描述',
  `images` text COMMENT '检查图片列表（用逗号分隔）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` varchar(50) DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(50) DEFAULT NULL COMMENT '更新人',
  PRIMARY KEY (`id`),
  KEY `idx_room_id` (`room_id`),
  KEY `idx_inspection_date` (`inspection_date`),
  KEY `idx_level` (`level`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='卫生检查表';