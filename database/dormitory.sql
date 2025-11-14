-- 创建宿舍表
CREATE TABLE IF NOT EXISTS `dormitory` (
    `id` VARCHAR(32) NOT NULL COMMENT '宿舍ID',
    `building_id` VARCHAR(32) NOT NULL COMMENT '楼栋ID',
    `room_no` VARCHAR(10) NOT NULL COMMENT '房间号',
    `floor_number` INT NOT NULL COMMENT '楼层',
    `bed_count` INT NOT NULL DEFAULT 4 COMMENT '床位数',
    `occupied_beds` INT NOT NULL DEFAULT 0 COMMENT '已入住人数',
    `description` TEXT COMMENT '描述',
    `status` INT NOT NULL DEFAULT 1 COMMENT '状态(0:已满,1:可用,2:维护中)',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` VARCHAR(50) COMMENT '创建人',
    `update_by` VARCHAR(50) COMMENT '更新人',
    `deleted` INT NOT NULL DEFAULT 0 COMMENT '删除标识(0:未删除,1:已删除)',
    PRIMARY KEY (`id`),
    KEY `idx_building_id` (`building_id`),
    KEY `idx_room_no` (`room_no`),
    KEY `idx_floor_number` (`floor_number`),
    KEY `idx_status` (`status`),
    KEY `idx_deleted` (`deleted`),
    UNIQUE KEY `uk_building_room` (`building_id`, `room_no`, `deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='宿舍表';

-- 插入示例数据
INSERT INTO `dormitory` (`id`, `building_id`, `room_no`, `floor_number`, `bed_count`, `occupied_beds`, `description`, `status`, `create_by`) VALUES
('1', '1', '101', 1, 4, 2, '朝南，采光良好', 1, 'admin'),
('2', '1', '102', 1, 4, 4, '朝南，四人间', 0, 'admin'),
('3', '1', '103', 1, 4, 0, '朝南，空房', 1, 'admin'),
('4', '1', '201', 2, 4, 1, '朝北，三人空床位', 1, 'admin'),
('5', '1', '202', 2, 4, 0, '朝北，空房', 2, 'admin'),
('6', '2', '101', 1, 6, 3, '六人间，位置较好', 1, 'admin'),
('7', '2', '102', 1, 6, 6, '六人间，已住满', 0, 'admin'),
('8', '2', '201', 2, 6, 2, '六人间，有空位', 1, 'admin');