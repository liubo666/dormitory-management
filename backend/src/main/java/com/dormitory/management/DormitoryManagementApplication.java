package com.dormitory.management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DormitoryManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(DormitoryManagementApplication.class, args);
        System.out.println("宿舍管理系统启动成功！");
    }

}