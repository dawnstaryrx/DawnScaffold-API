package com.gooodh.common.user.mapper;

import com.gooodh.model.po.SysUser;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SysUserMapper {

    @Select("SELECT * FROM sys_user WHERE username = #{username}")
    SysUser getByUsername(String username);

    @Select("SELECT r.role_code FROM sys_role r " +
            "JOIN sys_user_role ur ON ur.role_id = r.id " +
            "WHERE ur.user_id = #{userId}")
    List<String> getRolesByUserId(Integer id);

    @Select("SELECT p.permission_code FROM sys_permission p " +
            "JOIN sys_role_permission rp ON rp.permission_id = p.id " +
            "JOIN sys_user_role ur ON ur.role_id = rp.role_id " +
            "WHERE ur.user_id = #{userId}")
    List<String> getPermissionsByUserId(Integer id);

    @Select("SELECT * FROM sys_user WHERE username = #{username} OR email = #{username} OR phone = #{username}")
    SysUser getUserByUsernameOrEmailOrPhone(String username);

    @Insert("INSERT INTO sys_user(username, password, email, create_time, update_time) " +
            "VALUES(#{username}, #{password}, #{email}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void add(SysUser user);

    @Select("SELECT * FROM sys_user WHERE id = #{id}")
    SysUser getById(Integer id);
}
