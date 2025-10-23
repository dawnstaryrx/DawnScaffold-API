package com.gooodh.common.user.service;

/**
 * 用户角色服务接口
 * @author gooodh
 */
public interface SysUserRoleService {
    /**
     * 为用户添加角色
     * @param userId 用户ID
     * @param roleId 角色ID
     */
    void add(Integer userId, Integer roleId);

    /**
     * 删除用户的角色
     * @param userId 用户ID
     * @param roleId 角色ID
     */
    void delete(Integer userId, Integer roleId);
}
