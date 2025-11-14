package com.dormitory.management.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT工具类
 */
@Slf4j
@Component
public class JwtUtil {

    @Value("${jwt.secret:mySecretKey}")
    private String secret;

    @Value("${jwt.expiration:86400}")
    private Long expiration;

    @Value("${jwt.refresh-expiration:604800}")
    private Long refreshExpiration;


    /**
     * 获取签名密钥（固定 UTF-8 编码，避免中文乱码）
     */
    public SecretKey getSigningKey() {
        // 密钥必须足够长（HS256 要求 ≥256位），此处通过 Keys.hmacShaKeyFor 自动校验
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 生成访问Token（包含用户ID、用户名、角色）
     */
    public String generateAccessToken(Long userId, String username, String role) {
        // 校验参数非空
        if (userId == null || !StringUtils.hasText(username) || !StringUtils.hasText(role)) {
            throw new IllegalArgumentException("生成Token的参数不能为空");
        }

        Date now = new Date();
        // 计算过期时间（避免负数，确保有效期为正）
        long expiryMillis = now.getTime() + Math.max(expiration, 3600) * 1000; // 最小有效期1小时
        Date expiryDate = new Date(expiryMillis);

        try {
            return Jwts.builder()
                    .setSubject(userId.toString()) // 主题：用户ID
                    .claim("username", username)   // 自定义声明：用户名
                    .claim("role", role)           // 自定义声明：角色
                    .setIssuedAt(now)              // 签发时间
                    .setExpiration(expiryDate)     // 过期时间
                    .signWith(getSigningKey(), SignatureAlgorithm.HS256) // 明确签名算法
                    .compact();
        } catch (Exception e) {
            log.error("生成访问Token失败，userId:{}", userId, e);
            throw new RuntimeException("Token生成失败", e);
        }
    }

    /**
     * 生成刷新Token（仅包含用户ID，用于刷新访问Token）
     */
    public String generateRefreshToken(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshExpiration * 1000);

        try {
            return Jwts.builder()
                    .setSubject(userId.toString())
                    .claim("type", "refresh")      // 标记为刷新Token
                    .setIssuedAt(now)
                    .setExpiration(expiryDate)
                    .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                    .compact();
        } catch (Exception e) {
            log.error("生成刷新Token失败，userId:{}", userId, e);
            throw new RuntimeException("刷新Token生成失败", e);
        }
    }

    /**
     * 解析Token，获取Claims（统一封装，避免重复代码）
     */
    private Claims parseTokenClaims(String token) {
        if (!StringUtils.hasText(token)) {
            log.error("Token为空");
            return null;
        }

        try {
            // JJWT 0.11.x 标准语法：verifyWith 验证签名
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey()) // 设置签名密钥
                    .build()
                    .parseClaimsJws(token)          // 解析Token
                    .getBody();                     // 获取声明体
        } catch (MalformedJwtException e) {
            log.error("Token格式错误:{}", token, e);
        } catch (ExpiredJwtException e) {
            log.error("Token已过期:{}", token, e);
        } catch (UnsupportedJwtException e) {
            log.error("Token不支持:{}", token, e);
        } catch (IllegalArgumentException e) {
            log.error("Token参数异常:{}", token, e);
        } catch (JwtException e) {
            log.error("Token解析失败:{}", token, e);
        }
        return null;
    }

    /**
     * 从Token中获取用户ID
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = parseTokenClaims(token);
        if (claims == null) {
            return null;
        }

        try {
            return Long.parseLong(claims.getSubject());
        } catch (NumberFormatException e) {
            log.error("Token中用户ID格式错误", e);
            return null;
        }
    }

    /**
     * 从Token中获取用户名
     */
    public String getUsernameFromToken(String token) {
        Claims claims = parseTokenClaims(token);
        return claims != null ? claims.get("username", String.class) : null;
    }

    /**
     * 从Token中获取用户角色
     */
    public String getRoleFromToken(String token) {
        Claims claims = parseTokenClaims(token);
        return claims != null ? claims.get("role", String.class) : null;
    }

    /**
     * 验证Token是否有效（签名正确 + 未过期）
     */
    public boolean validateToken(String token) {
        Claims claims = parseTokenClaims(token);
        // 若Claims不为null，且未过期，则有效
        return claims != null && claims.getExpiration().after(new Date());
    }

    /**
     * 验证Token是否为刷新Token
     */
    public boolean isRefreshToken(String token) {
        Claims claims = parseTokenClaims(token);
        return claims != null && "refresh".equals(claims.get("type", String.class));
    }

    /**
     * 获取Token过期时间
     */
    public Date getExpirationDateFromToken(String token) {
        Claims claims = parseTokenClaims(token);
        return claims != null ? claims.getExpiration() : null;
    }

    /**
     * 检查Token是否即将过期（30分钟内）
     */
    public boolean isTokenExpiringSoon(String token) {
        Date expiration = getExpirationDateFromToken(token);
        if (expiration == null) {
            return true; // 解析失败视为即将过期
        }

        long thirtyMinutesInMillis = 30 * 60 * 1000;
        // 过期时间 - 当前时间 < 30分钟 → 即将过期
        return (expiration.getTime() - System.currentTimeMillis()) < thirtyMinutesInMillis;
    }
}