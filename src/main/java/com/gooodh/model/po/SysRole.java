package com.gooodh.model.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SysRole {
    private Integer id;                 // 角色id
    private String roleName;            // 角色名称 如 "管理员"
    private String roleCode;            // 角色编码 如 "admin"
    private String description;         // 角色描述
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
