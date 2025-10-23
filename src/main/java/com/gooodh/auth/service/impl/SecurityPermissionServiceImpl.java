package com.gooodh.auth.service.impl;

import com.gooodh.auth.mapper.SysPermissionMapper;
import com.gooodh.auth.service.SecurityPermissionService;
import com.gooodh.model.po.SysPermission;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SecurityPermissionServiceImpl implements SecurityPermissionService {
    private final SysPermissionMapper permissionMapper;

    @Override
    public List<SysPermission> loadUrlRoleMappings() {
        return permissionMapper.selectAllPermissionsWithRoles();
    }
}
