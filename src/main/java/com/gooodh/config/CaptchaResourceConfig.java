package com.gooodh.config;

import cloud.tianai.captcha.common.constant.CaptchaTypeConstant;
import cloud.tianai.captcha.resource.CrudResourceStore;
import cloud.tianai.captcha.resource.common.model.dto.Resource;
import cloud.tianai.captcha.resource.common.model.dto.ResourceMap;
import cloud.tianai.captcha.resource.impl.provider.ClassPathResourceProvider;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static cloud.tianai.captcha.common.constant.CommonConstant.DEFAULT_SLIDER_IMAGE_TEMPLATE_PATH;

@Component
@RequiredArgsConstructor
public class CaptchaResourceConfig {
    private final CrudResourceStore resourceStore;

    @PostConstruct
    public void init() {
        // 旋转验证码 模板 (系统内置)
        ResourceMap template3 = new ResourceMap("default", 4);
        template3.put("active.png", new Resource(ClassPathResourceProvider.NAME, DEFAULT_SLIDER_IMAGE_TEMPLATE_PATH.concat("/3/active.png")));
        template3.put("fixed.png", new Resource(ClassPathResourceProvider.NAME, DEFAULT_SLIDER_IMAGE_TEMPLATE_PATH.concat("/3/fixed.png")));

        // 1. 添加一些模板
//        resourceStore.addTemplate(CaptchaTypeConstant.SLIDER, template1);
//        resourceStore.addTemplate(CaptchaTypeConstant.SLIDER, template2);
        resourceStore.addTemplate(CaptchaTypeConstant.ROTATE, template3);

        // 2. 添加自定义背景图片, resource 的参数1为资源类型(默认支持 classpath/file/url ), resource 的参数2为资源路径, resource 的参数3为标签
        resourceStore.addResource(CaptchaTypeConstant.SLIDER, new Resource("classpath", "captcha/bgimages/a.png", "default"));
        resourceStore.addResource(CaptchaTypeConstant.SLIDER, new Resource("classpath", "captcha/bgimages/a.png", "default"));
        resourceStore.addResource(CaptchaTypeConstant.SLIDER, new Resource("classpath", "captcha/bgimages/a.png", "default"));
        resourceStore.addResource(CaptchaTypeConstant.ROTATE, new Resource("classpath", "captcha/bgimages/a.png", "default"));
        resourceStore.addResource(CaptchaTypeConstant.CONCAT, new Resource("classpath", "captcha/bgimages/a.png", "default"));
        resourceStore.addResource(CaptchaTypeConstant.WORD_IMAGE_CLICK, new Resource("classpath", "captcha/bgimages/a.pngg", "default"));
    }
}
