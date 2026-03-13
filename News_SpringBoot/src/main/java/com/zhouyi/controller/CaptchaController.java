package com.zhouyi.controller;

import com.zhouyi.common.result.Result;
import com.zhouyi.service.CaptchaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 验证码控制器
 */
@RestController
@RequestMapping("/api/v1/auth/captcha")
public class CaptchaController {

    @Autowired
    private CaptchaService captchaService;

    /**
     * 生成滑块验证码
     */
    @GetMapping("/generate")
    public Result<Map<String, Object>> generate() {
        Map<String, Object> data = captchaService.generateCaptcha();
        return Result.success("验证码生成成功", data, "/api/v1/auth/captcha/generate");
    }

    /**
     * 校验滑块位置
     * @param request 包含captchaKey和sliderX
     */
    @PostMapping("/verify")
    public Result<String> verify(@RequestBody Map<String, Object> request) {
        String captchaKey = (String) request.get("captchaKey");
        Object sliderXObj = request.get("sliderX");
        
        if (captchaKey == null || sliderXObj == null) {
            return Result.error("参数不能为空", "/api/v1/auth/captcha/verify");
        }
        
        Integer sliderX = Integer.valueOf(sliderXObj.toString());
        String captchaToken = captchaService.verifyCaptcha(captchaKey, sliderX);
        
        if (captchaToken != null) {
            return Result.success("验证通过", captchaToken, "/api/v1/auth/captcha/verify");
        } else {
            return Result.error("验证码校验失败", "/api/v1/auth/captcha/verify");
        }
    }
}
