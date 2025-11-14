package com.dormitory.management.dto;

import lombok.Data;

import java.io.Serializable;
@Data
public class CheckInResDTO implements Serializable {


    private  Integer pageIndex=1;

    private  Integer pageSize=10;

    private  String  keyword;

}
