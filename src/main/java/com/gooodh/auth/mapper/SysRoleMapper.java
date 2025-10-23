package com.gooodh.auth.mapper;

import com.gooodh.model.po.SysRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SysRoleMapper {
    List<String> getRolesByUserId(Integer id);

    @Select("SELECT * FROM sys_role WHERE id = #{id}")
    SysRole getRoleById(Integer id);

    @Select("SELECT * FROM sys_role WHERE role_code = #{roleCode}")
    SysRole getRoleByRoleCode(String roleCode);
}
