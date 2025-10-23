package com.gooodh.test.controller;

import com.gooodh.annotation.OperationLog;
import com.gooodh.model.po.Result;
import com.gooodh.test.model.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "测试接口", description = "测试用接口")
public class TestController {

    @Operation(summary = "测试接口", description = "Hello, World!")
    @OperationLog(value = "测试接口")
    @RequestMapping(value = "/public/test")
    public String test() {
        return "Hello, World!";
    }

    @Operation(summary = "测试返回对象", description = "返回一个用户对象")
    @RequestMapping("/user")
    public Result<User> test2() {
        User user = new User(1, "zhangsan");
        return Result.success(user);
    }

    @GetMapping("/admin/test")
    public String adminTest() {
        return "Admin Access OK";
    }

    @GetMapping("/user/test")
    public String userTest() {
        return "User Access OK";
    }
}
