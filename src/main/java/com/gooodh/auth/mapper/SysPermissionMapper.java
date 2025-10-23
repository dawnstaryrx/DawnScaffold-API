package com.gooodh.auth.mapper;

import com.gooodh.model.po.SysPermission;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SysPermissionMapper {
    /**
     * 查询所有权限及对应的角色
     */
    List<SysPermission> selectAllPermissionsWithRoles();

    List<String> getPermissionsByUserId(Integer id);
}
