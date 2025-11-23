package com.dormitory.management.config;

import io.minio.MinioClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MinIO配置类
 */
@Configuration
@ConfigurationProperties(prefix = "minio")
@Data
public class MinioConfig {

    /**
     * MinIO服务地址
     */
    private String endpoint;

    /**
     * MinIO访问密钥
     */
    private String accessKey;

    /**
     * MinIO秘密密钥
     */
    private String secretKey;

    /**
     * 存储桶名称
     */
    private String bucketName;

    /**
     * 是否使用HTTPS
     */
    private boolean secure;

    /**
     * 是否通过Nginx代理访问
     */
    private boolean useProxy;

    /**
     * 外部访问的基础URL (通过nginx代理)
     */
    private String externalUrl;

    /**
     * 创建MinIO客户端
     */
    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }
}