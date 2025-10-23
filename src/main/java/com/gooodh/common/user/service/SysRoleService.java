package com.gooodh.common.user.service;

import com.gooodh.model.po.SysRole;

import java.util.List;

public interface SysRoleService {
    List<String> getRolesByUserId(Integer id);

    SysRole getRoleById(Integer roleId);

    SysRole getRoleByRoleCode(String roleSuperAdmin);
}
