package com.gooodh.auth.handler.login;

import com.gooodh.auth.model.UserJwtPayload;
import com.gooodh.auth.model.UserLoginDTO;
import com.gooodh.auth.service.JwtService;
import com.gooodh.exception.ExceptionTool;
import com.gooodh.model.po.Result;
import com.gooodh.util.JSONUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AbstractAuthenticationTargetUrlRequestHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 认证成功/登录成功 事件处理器
 */
@Component
public class LoginSuccessHandler extends
        AbstractAuthenticationTargetUrlRequestHandler implements AuthenticationSuccessHandler {

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private static final String USER_REFRESH_TOKEN_PREFIX = "USER_REFRESH_TOKEN:";

    public LoginSuccessHandler() {
        this.setRedirectStrategy(new RedirectStrategy() {
            // 更改重定向策略，前后端分离项目，后端使用RestFul风格，无需做重定向
            @Override
            public void sendRedirect(HttpServletRequest request, HttpServletResponse response, String url)
                    throws IOException {
                // Do nothing, no redirects in REST
            }
        });
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {
        Object principal = authentication.getPrincipal();
        if (principal == null || !(principal instanceof UserLoginDTO)) {
            ExceptionTool.throwException("登陆认证成功后，authentication.getPrincipal()返回的Object对象必须是：UserLoginDTO！");
        }
        UserLoginDTO loginUser = (UserLoginDTO) principal;
        // 给 sessionId（前端可以用作唯一标识）
        String sessionId = UUID.randomUUID().toString();
        loginUser.setSessionId(sessionId);
        // 生成 token 和 refreshToken
        Map<String, Object> responseData = new LinkedHashMap<>();
        responseData.put("token", generateToken(loginUser));
        responseData.put("refreshToken", generateRefreshToken(loginUser));
        responseData.put("username", loginUser.getUsername());
        responseData.put("roles", loginUser.getRoles());
        responseData.put("permissions", loginUser.getPermissions());

        // 将refreshToken存放到Redis中，并设置过期时间
        String key = USER_REFRESH_TOKEN_PREFIX + loginUser.getUsername();
        stringRedisTemplate.opsForValue().set(key, responseData.get("refreshToken").toString(), 30, TimeUnit.DAYS);

        // 返回 JSON 给前端
        response.setContentType("application/json;charset=UTF-8");
        System.out.println(JSONUtil.stringify(Result.success(responseData)));
        PrintWriter writer = response.getWriter();
        writer.print(JSONUtil.stringify(Result.success(responseData))); // 你的 Result 工具类
        writer.flush();
        writer.close();
    }

    /**
     * 生成 JWT token
     */
    private String generateToken(UserLoginDTO currentUserDTO) {
        UserJwtPayload payload = new UserJwtPayload(
                currentUserDTO.getId(),
                currentUserDTO.getUsername(),
                currentUserDTO.getNickname(),
                currentUserDTO.getRoles(),
                currentUserDTO.getPermissions()
        );
        long expiredTime = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(10);
        return jwtService.createJwt(payload, expiredTime);
    }


    /**
     * 生成刷新 token
     */
    private String generateRefreshToken(UserLoginDTO currentUserDTO) {
        long expiredTime = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(30); // 30天过期
        return jwtService.createJwt(currentUserDTO, expiredTime);
    }

}
