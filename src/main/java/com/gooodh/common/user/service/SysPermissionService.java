package com.gooodh.common.user.service;

import java.util.List;

public interface SysPermissionService {
    List<String> getPermissionsByUserId(Integer id);
}
