package com.zhouyi.service.impl;

import com.zhouyi.service.CaptchaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class CaptchaServiceImpl implements CaptchaService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final int BG_WIDTH = 320;
    private static final int BG_HEIGHT = 160;
    private static final int SLIDER_WIDTH = 50;
    private static final int SLIDER_HEIGHT = 50;
    
    // Redis key prefixes
    private static final String CAPTCHA_PUZZLE_PREFIX = "captcha:puzzle:";
    private static final String CAPTCHA_TOKEN_PREFIX = "captcha:token:";

    @Override
    public Map<String, Object> generateCaptcha() {
        // 1. 生成背景图和随机缺口位置
        int targetX = new Random().nextInt(BG_WIDTH - SLIDER_WIDTH - 60) + 50;
        int targetY = new Random().nextInt(BG_HEIGHT - SLIDER_HEIGHT - 20) + 10;
        
        BufferedImage backgroundImage = new BufferedImage(BG_WIDTH, BG_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D bgG2d = backgroundImage.createGraphics();
        
        // 绘制随机背景 (模拟复杂背景)
        bgG2d.setColor(new Color(240, 240, 240));
        bgG2d.fillRect(0, 0, BG_WIDTH, BG_HEIGHT);
        
        Random random = new Random();
        for (int i = 0; i < 20; i++) {
            bgG2d.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255), 100));
            bgG2d.fillOval(random.nextInt(BG_WIDTH), random.nextInt(BG_HEIGHT), random.nextInt(100), random.nextInt(100));
        }

        // 2. 生成滑块图 (提取缺口部分的图像)
        BufferedImage sliderImage = new BufferedImage(SLIDER_WIDTH, SLIDER_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics2D sliderG2d = sliderImage.createGraphics();
        
        // 绘制圆角矩形作为形状 (简单实现，AJ-Captcha通常有更复杂的凹凸边缘)
        sliderG2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        sliderG2d.setColor(Color.WHITE);
        sliderG2d.fillRoundRect(0, 0, SLIDER_WIDTH, SLIDER_HEIGHT, 10, 10);
        
        // 使用该形状对原图进行剪切
        sliderG2d.setComposite(AlphaComposite.SrcIn);
        sliderG2d.drawImage(backgroundImage.getSubimage(targetX, targetY, SLIDER_WIDTH, SLIDER_HEIGHT), 0, 0, null);
        
        // 为滑块添加边框
        sliderG2d.setComposite(AlphaComposite.SrcOver);
        sliderG2d.setColor(new Color(255, 255, 255, 150));
        sliderG2d.drawRoundRect(0, 0, SLIDER_WIDTH - 1, SLIDER_HEIGHT - 1, 10, 10);
        
        // 3. 在背景图上把缺口位置涂黑/半透明
        bgG2d.setColor(new Color(0, 0, 0, 120));
        bgG2d.fillRoundRect(targetX, targetY, SLIDER_WIDTH, SLIDER_HEIGHT, 10, 10);
        
        bgG2d.dispose();
        sliderG2d.dispose();

        // 4. 转为Base64并存入Redis
        String captchaKey = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(CAPTCHA_PUZZLE_PREFIX + captchaKey, String.valueOf(targetX), 2, TimeUnit.MINUTES);

        Map<String, Object> result = new HashMap<>();
        result.put("captchaKey", captchaKey);
        result.put("backgroundBase64", imageToBase64(backgroundImage, "jpg"));
        result.put("sliderBase64", imageToBase64(sliderImage, "png"));
        result.put("sliderY", targetY);
        
        return result;
    }

    @Override
    public String verifyCaptcha(String captchaKey, Integer sliderX) {
        String redisKey = CAPTCHA_PUZZLE_PREFIX + captchaKey;
        String expectedXStr = redisTemplate.opsForValue().get(redisKey);
        
        if (expectedXStr == null) {
            return null;
        }
        
        // 顺便删除题目，防止重复验证
        redisTemplate.delete(redisKey);
        
        int expectedX = Integer.parseInt(expectedXStr);
        // 允许8像素内的误差
        if (Math.abs(expectedX - sliderX) <= 8) {
            String captchaToken = UUID.randomUUID().toString();
            redisTemplate.opsForValue().set(CAPTCHA_TOKEN_PREFIX + captchaToken, "valid", 5, TimeUnit.MINUTES);
            return captchaToken;
        }
        
        return null;
    }

    @Override
    public boolean validateCaptchaToken(String captchaToken) {
        if (captchaToken == null || captchaToken.isEmpty()) {
            return false;
        }
        String redisKey = CAPTCHA_TOKEN_PREFIX + captchaToken;
        // 一次性消费
        return Boolean.TRUE.equals(redisTemplate.delete(redisKey));
    }

    private String imageToBase64(BufferedImage image, String format) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, format, baos);
            return "data:image/" + format + ";base64," + Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (Exception e) {
            return "";
        }
    }
}
