package com.dormitory.management.controller;

import com.dormitory.management.common.Result;
import com.dormitory.management.utils.MinioUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 文件上传控制器
 */
@Slf4j
@Tag(name = "文件上传", description = "文件上传相关接口")
@RestController
@RequestMapping("/upload")
public class UploadController {

    @Autowired
    private MinioUtil minioUtil;



    @Operation(summary = "上传头像")
    @PostMapping("/avatar")
    public Result<Map<String,String>> uploadAvatar(@RequestParam("file") MultipartFile file) {
        try {
            if (file == null || file.isEmpty()) {
                return Result.error("请选择要上传的文件");
            }

            // 上传文件到MinIO，获取文件路径
            String filePath = minioUtil.uploadAvatar(file);
            Map<String, String>  urlMaps= new HashMap<>();
            urlMaps.put("filePath",filePath);

            // 同时提供预签名URL用于直接访问
            String previewUrl = minioUtil.getPreviewUrl(filePath);
            urlMaps.put("previewUrl",previewUrl);

            return Result.success(urlMaps);
        } catch (Exception e) {
            log.error("头像上传失败", e);
            return Result.error("头像上传失败：" + e.getMessage());
        }
    }





    @Operation(summary = "删除文件")
    @DeleteMapping("/delete")
    public Result<Void> deleteFile(@RequestParam String fileName) {
        try {
            if (fileName == null || fileName.trim().isEmpty()) {
                return Result.error("文件名不能为空");
            }

            minioUtil.deleteFile(fileName);
            return Result.success();
        } catch (Exception e) {
            log.error("文件删除失败", e);
            return Result.error("文件删除失败：" + e.getMessage());
        }
    }

//    @Operation(summary = "检查文件是否存在")
//    @GetMapping("/exists")
//    public Result<Map<String, Object>> checkFileExists(@RequestParam String fileName) {
//        try {
//            if (fileName == null || fileName.trim().isEmpty()) {
//                return Result.error("文件名不能为空");
//            }
//
//            boolean exists = minioUtil.fileExists(fileName);
//            Map<String, Object> result = new HashMap<>();
//            result.put("exists", exists);
//
//            if (exists) {
//                result.put("url", minioUtil.getFileUrl(fileName));
//            }
//
//            return Result.success(result);
//        } catch (Exception e) {
//            log.error("检查文件是否存在失败", e);
//            return Result.error("检查文件失败：" + e.getMessage());
//        }
//    }
//
//    @Operation(summary = "获取文件预览URL")
//    @GetMapping("/preview")
//    public Result<Map<String, String>> getPreviewUrl(@RequestParam String filePath) {
//        try {
//            String previewUrl = minioUtil.getPreviewUrl(filePath);
//            Map<String, String> result = new HashMap<>();
//            result.put("previewUrl", previewUrl);
//            result.put("filePath", filePath);
//
//            return Result.success(result);
//        } catch (Exception e) {
//            log.error("获取预览URL失败", e);
//            return Result.error("获取预览URL失败：" + e.getMessage());
//        }
//    }
}