package com.gooodh.auth.service.impl;

import com.gooodh.auth.model.UserLoginDTO;
import com.gooodh.auth.service.UserDetailsService;
import com.gooodh.model.po.SysUser;
import com.gooodh.common.user.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {
    private final SysUserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) {
        SysUser user = userMapper.getByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }
        List<String> roles = userMapper.getRolesByUserId(user.getId());
        List<String> permissions = userMapper.getPermissionsByUserId(user.getId());

        UserLoginDTO dto = new UserLoginDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setPassword(user.getPassword());
        dto.setNickname(user.getNickname());
        dto.setEmail(user.getEmail());
        dto.setAvatar(user.getAvatar());
        dto.setRoles(roles);
        dto.setPermissions(permissions);
        return dto;
    }
}
