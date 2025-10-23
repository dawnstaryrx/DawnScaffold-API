package com.gooodh.model.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SysUserRole {
    private Integer id;
    private Integer userId;
    private Integer roleId;
}
