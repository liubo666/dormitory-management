//package com.dormitory.management.controller;
//
//import com.dormitory.management.common.Result;
//import com.dormitory.management.service.RegistrationApplicationService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.servlet.view.RedirectView;
//
//import java.nio.charset.StandardCharsets;
//import java.util.Base64;
//
///**
// * 审批控制器 - 模仿星图工作台的智能跳转机制
// */
//@Slf4j
//@Controller
//@RequestMapping("/admin/approval")
//@RequiredArgsConstructor
//public class ApprovalController {
//
//    private final RegistrationApplicationService registrationApplicationService;
////    private final JwtUtil jwtUtil;
//
//    /**
//     * 处理审批请求 - 模仿星图工作台的统一入口
//     */
//    @GetMapping
//    public RedirectView handleApproval(@RequestParam String action, @RequestParam String token) {
//        try {
//            // 解码token
//            String decodedToken = decodeToken(token);
//
//            // 验证token有效性
////            RegistrationApplication application = registrationApplicationService.getApplicationByToken(decodedToken);
////            if (application == null) {
////                return new RedirectView("/error?msg=Invalid+token");
////            }
//
//            // 根据action跳转到相应页面
//            if ("approve".equals(action)) {
//                return new RedirectView("/admin/approve/" + decodedToken);
//            } else if ("reject".equals(action)) {
//                return new RedirectView("/admin/reject/" + decodedToken);
//            } else {
//                return new RedirectView("/error?msg=Invalid+action");
//            }
//
//        } catch (Exception e) {
//            log.error("审批链接处理失败", e);
//            return new RedirectView("/error?msg=Processing+failed");
//        }
//    }
//
//    /**
//     * 简化版本的审批页面
//     */
//    @GetMapping("/approve/{token}")
//    @ResponseBody
//    public Result<String> approveApplication(@PathVariable String token) {
//        try {
//            // 如果token是Base64编码的，先解码
//            String actualToken = token;
//            if (token.length() > 50) { // Base64编码后的token会比较长
//                actualToken = decodeToken(token);
//            }
//
//            registrationApplicationService.approveApplication(actualToken);
//            return Result.success("注册申请已通过");
//
//        } catch (Exception e) {
//            log.error("审批通过失败", e);
//            return Result.error("审批失败：" + e.getMessage());
//        }
//    }
//
//    /**
//     * 简化版本的驳回页面
//     */
//    @GetMapping("/reject/{token}")
//    @ResponseBody
//    public Result<String> rejectApplication(@PathVariable String token, @RequestParam(required = false) String reason) {
//        try {
//            // 如果token是Base64编码的，先解码
//            String actualToken = token;
//            if (token.length() > 50) { // Base64编码后的token会比较长
//                actualToken = decodeToken(token);
//            }
//
//            // 这里需要从JWT中获取管理员ID，暂时使用固定ID用于测试
//            Long adminId = 1L; // 实际使用时应该从JWT获取
//
//            String rejectionReason = reason != null ? reason : "管理员驳回";
//            registrationApplicationService.rejectApplication(actualToken, adminId, rejectionReason);
//            return Result.success("注册申请已驳回");
//
//        } catch (Exception e) {
//            log.error("审批驳回失败", e);
//            return Result.error("审批失败：" + e.getMessage());
//        }
//    }
//
//    /**
//     * 解码Token
//     */
//    private String decodeToken(String encodedToken) {
//        try {
//            // 尝试Base64解码
//            byte[] decodedBytes = Base64.getDecoder().decode(encodedToken);
//            return new String(decodedBytes, StandardCharsets.UTF_8);
//        } catch (Exception e) {
//            // 如果解码失败，说明token是原始格式
//            return encodedToken;
//        }
//    }
//}