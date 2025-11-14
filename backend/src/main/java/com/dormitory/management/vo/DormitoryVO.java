package com.dormitory.management.vo;

import com.dormitory.management.dto.BedInfoDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 宿舍视图对象
 */
@Data
public class DormitoryVO {

    /**
     * 宿舍ID
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;


    /**
     * 楼栋名称
     */
    private String buildingName;

    /**
     * 楼栋号
     */
    private String buildingNo;



    /**
     * 房间号
     */
    private String roomNo;

    /**
     * 楼层
     */
    private Integer floorNumber;


    /**
     * 已入住人数
     */
    private Integer occupiedBeds;

    /**
     * 可用床位数
     */
    private Integer availableBeds;

    /**
     * 总床位数
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
     * 状态文本
     */
    private String statusText;

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
     * 床位列表（详细信息）
     */

    private List<BedInfoDTO> bedInfos;
}