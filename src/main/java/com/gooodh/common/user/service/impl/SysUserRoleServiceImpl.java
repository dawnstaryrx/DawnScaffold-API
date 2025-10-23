package com.gooodh.common.user.service.impl;

import com.gooodh.common.user.service.SysRoleService;
import com.gooodh.common.user.mapper.SysUserRoleMapper;
import com.gooodh.common.user.service.SysUserRoleService;
import com.gooodh.common.user.service.SysUserService;
import com.gooodh.exception.ExceptionTool;
import com.gooodh.model.po.SysRole;
import com.gooodh.model.po.SysUser;
import com.gooodh.model.po.SysUserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SysUserRoleServiceImpl implements SysUserRoleService {

    private final SysUserRoleMapper sysUserRoleMapper;
//    private final SysUserService sysUserService;
    private final SysRoleService sysRoleService;

    @Override
    public void add(Integer userId, Integer roleId) {
        // 判断用户是否存在
//        SysUser user = sysUserService.getUserById(userId);
//        if (user == null) {
//            ExceptionTool.throwException("用户不存在");
//        }
        // 判断角色是否存在
        SysRole role = sysRoleService.getRoleById(roleId);
        if (role == null) {
            ExceptionTool.throwException("角色不存在");
        }
        // 判断用户是否已经拥有该角色
        SysUserRole sysUserRole = sysUserRoleMapper.getByUserIdAndRoleId(userId, roleId);
        if (sysUserRole != null) {
            ExceptionTool.throwException("用户已经拥有该角色");
        }
        // 添加用户角色
        sysUserRoleMapper.add(userId, roleId);
    }

    @Override
    public void delete(Integer userId, Integer roleId) {

    }
}
