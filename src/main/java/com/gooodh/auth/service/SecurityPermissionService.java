package com.gooodh.auth.service;

import com.gooodh.model.po.SysPermission;

import java.util.List;

public interface SecurityPermissionService {
    List<SysPermission> loadUrlRoleMappings();
}
