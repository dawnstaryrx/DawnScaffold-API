package com.gooodh.common.user.service;

import com.gooodh.model.dto.RegisterDTO;
import com.gooodh.model.po.SysUser;

public interface SysUserService {
    SysUser getUserByUsernameOrEmailOrPhone(String username);

    void register(RegisterDTO registerDTO);

    SysUser getUserById(Integer userId);

    void sendCode(String emailOrPhone, String type, String token);

    SysUser getUserByOpenId(String openId, String platform);

    void createUserWithOpenId(SysUser user, String openId, String platform);
}
