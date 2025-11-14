package com.dormitory.management.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 宿舍实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("dormitory")
public class Dormitory implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;



    /**
     * 楼栋号
     */
    private String buildingNo;

    /**
     * 楼栋名称
     */
    private String buildingName;



    /**
     * 房间号
     */
    private String roomNo;

    /**
     * 楼层
     */
    private Integer floorNumber;

    /**
     * 可用床位数（从床位表计算得出）
     */
    private Integer availableBeds;

    /**
     * 已入住人数（从床位表计算得出）
     */
    private Integer occupiedBeds;

    /**
     * 总床位数（从床位表计算得出）
     */
    private Integer totalBeds;

    /**
     * 描述
     */
    private String description;

    /**
     * 状态(0:已满,1:可用,2:维护中)
     */
    private Integer status;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 更新人
     */
    private String updateBy;

    /**
     * 删除标识(0:未删除,1:已删除)
     */
    @TableLogic
    private Integer deleted;
}