package com.gooodh.auth.handler.login.github;

import com.gooodh.util.JSONUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

public class GitHubAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private static final Logger logger = LoggerFactory.getLogger(GitHubAuthenticationFilter.class);

    public GitHubAuthenticationFilter(RequestMatcher requestMatcher,
                                      AuthenticationManager authenticationManager,
                                      AuthenticationSuccessHandler authenticationSuccessHandler,
                                      AuthenticationFailureHandler authenticationFailureHandler) {
        super(requestMatcher);
        setAuthenticationManager(authenticationManager);
        setAuthenticationSuccessHandler(authenticationSuccessHandler);
        setAuthenticationFailureHandler(authenticationFailureHandler);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        logger.debug("use GitHubAuthenticationFilter");

        // 提取请求数据
        String requestJsonData = request.getReader().lines()
                .collect(Collectors.joining(System.lineSeparator()));
        Map<String, Object> requestMapData = JSONUtil.parseToMap(requestJsonData);
        String code = requestMapData.get("code").toString();

        // 封装成Spring Security需要的对象
        GitHubAuthentication authentication = new GitHubAuthentication();
        authentication.setCode(code);
        authentication.setAuthenticated(false);

        // 开始登录认证。SpringSecurity会利用 Authentication对象去寻找 AuthenticationProvider进行登录认证
        return getAuthenticationManager().authenticate(authentication);
    }

}
