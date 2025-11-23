package com.dormitory.management.utils;

import com.dormitory.management.config.MinioConfig;
import io.minio.*;
import io.minio.http.Method;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * MinIO工具类
 */
@Slf4j
@Component
public class MinioUtil {

    @Autowired
    private MinioClient minioClient;

    @Autowired
    private MinioConfig minioConfig;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 检查存储桶是否存在，不存在则创建
     */
    private void checkBucketExists() {
        try {
            boolean exists = minioClient.bucketExists(
                    BucketExistsArgs.builder()
                            .bucket(minioConfig.getBucketName())
                            .build()
            );
            if (!exists) {
                minioClient.makeBucket(
                        MakeBucketArgs.builder()
                                .bucket(minioConfig.getBucketName())
                                .build()
                );
                log.info("MinIO存储桶创建成功: {}", minioConfig.getBucketName());
            }
        } catch (Exception e) {
            log.error("检查/创建MinIO存储桶失败", e);
            throw new RuntimeException("MinIO存储桶操作失败");
        }
    }

    /**
     * 上传文件
     */
    public String uploadFile(MultipartFile file, String folder) {
        try {
            // 检查存储桶
            checkBucketExists();

            // 生成文件名
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null ?
                    originalFilename.substring(originalFilename.lastIndexOf(".")) : "";
            String fileName = folder + "/" + UUID.randomUUID().toString() + extension;

            // 上传文件
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioConfig.getBucketName())
                            .object(fileName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            log.info("文件上传成功: {}", fileName);
            return  minioConfig.getBucketName()+"/"+fileName;
        } catch (Exception e) {
            log.error("文件上传失败", e);
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 上传头像
     */
    public String uploadAvatar(MultipartFile file) {
        // 验证文件类型
        String contentType = file.getContentType();
        if (contentType == null || (!contentType.equals("image/jpeg") &&
                !contentType.equals("image/png") && !contentType.equals("image/gif"))) {
            throw new RuntimeException("只支持JPEG、PNG、GIF格式的图片");
        }

        // 验证文件大小（2MB）
        if (file.getSize() > 2 * 1024 * 1024) {
            throw new RuntimeException("图片大小不能超过2MB");
        }

        return uploadFile(file, "avatars");
    }


    /**
     * 获取文件访问URL
     */
    public String getPreviewUrl(String fileName) {
        try {
            String cacheKey = "preview_url:" + fileName;
            String cachedUrl = redisTemplate.opsForValue().get(cacheKey);

            String previewUrl;
            if (StringUtils.isNotBlank(cachedUrl)) {
                // 使用缓存中的URL
                previewUrl = cachedUrl;
                log.info("从Redis缓存获取预览URL: {}", previewUrl);
            } else {
                // 缓存中不存在，生成新的URL
                if (minioConfig.isUseProxy()) {
                    // 使用Nginx代理URL - 不需要预签名，通过Nginx代理访问
                    String objectName = fileName;
                    // 如果fileName包含bucket名称，则提取object部分
                    if (fileName.contains("/")) {
                        String[] parts = fileName.split("/", 2);
                        if (parts.length == 2) {
                            objectName = parts[1];
                        }
                    }
                    previewUrl = minioConfig.getExternalUrl() + "/" + objectName;
                    log.info("通过Nginx代理生成文件访问URL: {}", previewUrl);
                } else {
                    // 使用MinIO预签名URL
                    String objectName = fileName;
                    // 如果fileName包含bucket名称，则提取object部分
                    if (fileName.contains("/")) {
                        String[] parts = fileName.split("/", 2);
                        if (parts.length == 2) {
                            objectName = parts[1];
                        }
                    }

                    previewUrl = minioClient.getPresignedObjectUrl(
                            GetPresignedObjectUrlArgs.builder()
                                    .method(Method.GET)
                                    .bucket(minioConfig.getBucketName())
                                    .object(objectName)
                                    .expiry(7, TimeUnit.DAYS)
                                    .build()
                    );
                    log.info("生成MinIO预签名URL: {}", previewUrl);
                }
                // 存入Redis缓存，有效期7天
                redisTemplate.opsForValue().set(cacheKey, previewUrl, 6, TimeUnit.DAYS);
                log.info("生成新的访问URL并缓存: {}", previewUrl);
            }
            log.info("生成文件访问URL: {}", previewUrl);
            return previewUrl;
        } catch (Exception e) {
            log.error("获取文件访问URL失败", e);
            throw new RuntimeException("获取文件访问URL失败");
        }
    }

    /**
     * 删除文件
     */
    public void deleteFile(String fileName) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(minioConfig.getBucketName())
                            .object(fileName)
                            .build()
            );
            log.info("文件删除成功: {}", fileName);
        } catch (Exception e) {
            log.error("文件删除失败", e);
            throw new RuntimeException("文件删除失败");
        }
    }

    /**
     * 检查文件是否存在
     */
    public boolean fileExists(String fileName) {
        try {
            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(minioConfig.getBucketName())
                            .object(fileName)
                            .build()
            );
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}