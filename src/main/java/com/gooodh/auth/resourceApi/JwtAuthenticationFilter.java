package com.gooodh.auth.resourceApi;

import com.gooodh.auth.model.UserLoginDTO;
import com.gooodh.auth.service.JwtService;
import com.gooodh.exception.ExceptionTool;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;

    // ✅ 白名单路径（不用校验 JWT）
    private static final String[] WHITE_LIST = new String[]{
            "/register",
            "/sendCode",
            "/login/password",
            "/captcha/gen",
            "/captcha/check"
    };

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String path = request.getServletPath();
        // ✅ 1. 白名单放行
        for (String white : WHITE_LIST) {
            if (path.startsWith(white)) {
                filterChain.doFilter(request, response);
                return;
            }
        }

        String jwtToken = request.getHeader("Authorization");

        // 1. 判断 token 是否存在
        if (!StringUtils.hasText(jwtToken)) {
            ExceptionTool.throwException("JWT 为空", HttpStatus.UNAUTHORIZED.value());
        }

        if (jwtToken.startsWith("Bearer ")) {
            jwtToken = jwtToken.substring(7);
        }

        UserLoginDTO userLoginInfo;
        try {
            // 2. 解析 JWT
            userLoginInfo = jwtService.verifyJwt(jwtToken, UserLoginDTO.class);

            // 3. 角色/权限列表转成 GrantedAuthority
            List<SimpleGrantedAuthority> authorities = userLoginInfo.getRoles().stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            // 4. 认证对象
            JwtAuthentication authentication = new JwtAuthentication();
            authentication.setJwtToken(jwtToken);
            authentication.setCurrentUser(userLoginInfo);
            authentication.setAuthenticated(true); // 表示已通过认证
            authentication.setDetails(authorities);

            // 5. 注入 SecurityContextHolder
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (ExpiredJwtException e) {
            ExceptionTool.throwException("JWT 已过期", HttpStatus.UNAUTHORIZED, HttpStatus.UNAUTHORIZED.value());
            return;
        } catch (Exception e) {
            ExceptionTool.throwException("JWT 无效", HttpStatus.UNAUTHORIZED.value());
            return;
        }

        // 6. 放行
        System.out.println("用户 " + userLoginInfo.getUsername() + " 通过 JWT 认证");
        filterChain.doFilter(request, response);
    }
}
