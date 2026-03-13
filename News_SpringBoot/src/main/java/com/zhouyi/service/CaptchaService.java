package com.zhouyi.service;

import java.util.Map;

/**
 * 验证码服务接口
 */
public interface CaptchaService {
    
    /**
     * 生成滑块验证码
     * @return 包含captchaKey, backgroundBase64, sliderBase64, sliderY 的Map
     */
    Map<String, Object> generateCaptcha();
    
    /**
     * 校验滑块位置
     * @param captchaKey 验证码标识
     * @param sliderX 用户滑动的X轴坐标
     * @return 校验通过返回captchaToken，否则返回null
     */
    String verifyCaptcha(String captchaKey, Integer sliderX);
    
    /**
     * 校验Captcha Token的有效性（一次性消费）
     * @param captchaToken 校验凭块
     * @return 是否有效
     */
    boolean validateCaptchaToken(String captchaToken);
}
