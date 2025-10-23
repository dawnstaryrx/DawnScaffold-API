package com.gooodh.common.user.mapper;

import com.gooodh.model.po.SysUserRole;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 用户角色关联表 Mapper
 */
@Mapper
public interface SysUserRoleMapper {

    /**
     * 通过 userId 和 roleId 获取 SysUserRole
     * @param userId
     * @param roleId
     * @return
     */
    @Select("SELECT * FROM sys_user_role WHERE user_id = #{userId} AND role_id = #{roleId}")
    SysUserRole getByUserIdAndRoleId(Integer userId, Integer roleId);

    /**
     * 添加用户角色关联
     * @param userId
     * @param roleId
     */
    @Insert("INSERT INTO sys_user_role (user_id, role_id) VALUES (#{userId}, #{roleId})")
    void add(Integer userId, Integer roleId);

}
