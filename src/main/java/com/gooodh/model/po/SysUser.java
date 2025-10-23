package com.gooodh.model.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SysUser {
    private Integer id;                     // 用户id
    private String username;                // 用户名
    private String nickname;                // 昵称
    private String password;                // 密码
    private String avatar;                  // 头像url
    private String email;                   // 邮箱
    private String phone;                   // 电话
    private String githubOpenid;            // github的唯一标识
    private String wechatOpenid;            // 微信的唯一标识
    private String linuxdoOpenid;           // linuxdo的唯一标识
    private Integer point;                  // 积分
    private Integer loginFailTime;          // 连续登录失败次数
    private LocalDateTime createTime;       // 创建时间
    private LocalDateTime updateTime;       // 更新时间
    private LocalDateTime lastLoginTime;    // 最后登录时间
    private String lastLoginIp;             // 最后登录ip
}
