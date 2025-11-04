package com.gooodh.common.user.controller;

import cloud.tianai.captcha.application.ImageCaptchaApplication;
import cloud.tianai.captcha.application.vo.ImageCaptchaVO;
import cloud.tianai.captcha.common.constant.CaptchaTypeConstant;
import cloud.tianai.captcha.common.response.ApiResponse;
import cloud.tianai.captcha.validator.common.model.dto.ImageCaptchaTrack;
import com.gooodh.model.constant.RedisConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequestMapping("/captcha")
@RequiredArgsConstructor
public class CaptchaController {
    @Autowired
    private ImageCaptchaApplication application;
    private final StringRedisTemplate stringRedisTemplate;

    @PostMapping("/gen")
    public ApiResponse<ImageCaptchaVO> genCaptcha() {
        // 1.生成验证码(该数据返回给前端用于展示验证码数据)
        // 参数1为具体的验证码类型， 默认支持 SLIDER、ROTATE、WORD_IMAGE_CLICK、CONCAT 等验证码类型，详见： `CaptchaTypeConstant`类
        return  application.generateCaptcha(CaptchaTypeConstant.SLIDER);
    }

    @PostMapping("/check")
    public ApiResponse<?> checkCaptcha(@RequestBody Data data) {
        ApiResponse<?> response = application.matching(data.getId(), data.getData());
        if (response.isSuccess()) {
            // 验证码验证成功，此处应该进行自定义业务处理， 或者返回验证token进行二次验证等。
            stringRedisTemplate.opsForValue().set(RedisConstant.CAPTCHA_VALID_TOKEN_PREFIX + data.getId(), "true", 5 * 60, java.util.concurrent.TimeUnit.SECONDS);
            return ApiResponse.ofSuccess(Collections.singletonMap("validToken", data.getId()));
        }
        return response;
    }

    @lombok.Data
    public static class Data {
        // 验证码id
        private String  id;
        // 验证码数据
        private ImageCaptchaTrack data;
        // 可以加用户自定义业务参数...
    }
}
