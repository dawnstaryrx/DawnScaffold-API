package com.gooodh.common.user.controller;

import com.gooodh.common.user.service.SysUserRoleService;
import com.gooodh.model.dto.SysUserRoleDTO;
import com.gooodh.model.po.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户角色控制器
 */
@RestController
@RequiredArgsConstructor
public class SysUserRoleController {

    private final SysUserRoleService sysUserRoleService;

    /**
     * 给用户分配角色
     * @return
     */
    @PostMapping("/super-admin/user/role")
    public Result addUserRole(@RequestBody SysUserRoleDTO sysUserRoleDTO) {
        sysUserRoleService.add(sysUserRoleDTO.getUserId(), sysUserRoleDTO.getRoleId());
        return Result.success();
    }

    /**
     * 删除用户角色
     * @param sysUserRoleDTO
     * @return
     */
    @DeleteMapping("/super-admin/user/role")
    public Result deleteUserRole(@RequestBody SysUserRoleDTO sysUserRoleDTO) {
        sysUserRoleService.delete(sysUserRoleDTO.getUserId(), sysUserRoleDTO.getRoleId());
        return Result.success();
    }

}
