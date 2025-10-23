package com.gooodh.common.user.service.impl;

import com.gooodh.auth.mapper.SysRoleMapper;
import com.gooodh.common.user.service.SysRoleService;
import com.gooodh.model.po.SysRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SysRoleServiceImpl implements SysRoleService {

    private final SysRoleMapper roleMapper;

    @Override
    public List<String> getRolesByUserId(Integer id) {
        return roleMapper.getRolesByUserId(id);
    }

    @Override
    public SysRole getRoleById(Integer roleId) {
        return roleMapper.getRoleById(roleId);
    }

    @Override
    public SysRole getRoleByRoleCode(String roleCode) {
        return roleMapper.getRoleByRoleCode(roleCode);
    }
}
