package com.gooodh.common.user.controller;

import com.gooodh.annotation.OperationLog;
import com.gooodh.model.dto.RegisterDTO;
import com.gooodh.model.po.Result;
import com.gooodh.common.user.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SysUserController {

    private final SysUserService userService;

    /**
     * 注册
     * @param registerDTO
     * @return
     */
    @Operation(summary = "注册", description = "用户注册", tags = {"公共接口"})
    @OperationLog(value = "注册")
    @PostMapping("/register")
    public Result register(@RequestBody RegisterDTO registerDTO){
        userService.register(registerDTO);
        return Result.success();
    }

    /**
     * 发送验证码
     * @param emailOrPhone 邮箱或手机号
     * @param type 发送验证码类型: email_login, phone_login, email_register, phone_register
     * @return
     */
    @Operation(summary = "发送验证码", description = "发送邮箱或手机号验证码")
    @PostMapping("/sendCode")
    public Result sendCode(@RequestParam String emailOrPhone, @RequestParam String type){
        userService.sendCode(emailOrPhone, type);
        return Result.success();
    }
}
