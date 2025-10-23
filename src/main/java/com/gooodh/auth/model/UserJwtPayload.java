package com.gooodh.auth.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserJwtPayload {
    private Integer id;
    private String username;
    private String nickname;
    private List<String> roles;
    private List<String> permissions;
}

