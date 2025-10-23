package com.gooodh.common.user.service.impl;

import com.gooodh.auth.mapper.SysPermissionMapper;
import com.gooodh.common.user.service.SysPermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SysPermissionServiceImpl implements SysPermissionService {

    private final SysPermissionMapper permissionMapper;

    @Override
    public List<String> getPermissionsByUserId(Integer id) {
        return permissionMapper.getPermissionsByUserId(id);
    }
}
