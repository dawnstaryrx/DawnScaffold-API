package com.gooodh.auth.resourceApi;

import com.gooodh.auth.model.UserLoginDTO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * 通用 JWT 鉴权 Token
 * 认证前 principal 和 credential 是 token 字符串
 * 认证后 principal 是 UserLoginDTO，credential 清空
 */
@Setter
@Getter
public class JwtAuthentication extends AbstractAuthenticationToken {

    private String jwtToken;              // 前端传过来的 token
    private UserLoginDTO currentUser;     // 解析后存储的用户信息

    public JwtAuthentication() {
        super(null); // 不使用固定的 GrantedAuthority，权限在解析 JWT 时注入
    }

    @Override
    public Object getCredentials() {
        // 认证通过后，清空敏感信息
        return isAuthenticated() ? null : jwtToken;
    }

    @Override
    public Object getPrincipal() {
        // 未认证时返回 token，本质上就是客户端传来的内容
        // 认证通过后返回用户对象
        return isAuthenticated() ? currentUser : jwtToken;
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        if (currentUser == null || currentUser.getRoles() == null) {
            return super.getAuthorities(); // 空列表
        }
        System.out.println(currentUser.getRoles());
        return currentUser.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role)) // 根据需要加 ROLE_ 前缀
                .collect(Collectors.toList());
    }
}
