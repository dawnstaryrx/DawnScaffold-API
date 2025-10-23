package com.gooodh.model.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SysPermission {
    private Integer id;                 // 权限id
    private String permissionName;      // 权限名称 如 "用户管理"
    private String permissionCode;      // 权限编码 如 "user:manage"
    private String url;                 // 资源路径
    private String method;              // 请求方法 GET/POST/PUT/DELETE
    private Integer type;               // 权限类型 1-菜单 2-按钮 3-其他
    private Integer parentId;           // 父级权限id, 0表示顶级权限
    private String description;         // 权限描述
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    private List<SysRole> roles;        // 当前权限对应的角色集合
}
