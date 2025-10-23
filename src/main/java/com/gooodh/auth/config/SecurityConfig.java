package com.gooodh.auth.config;

import com.gooodh.auth.handler.exception.CustomAuthenticationExceptionHandler;
import com.gooodh.auth.handler.exception.CustomSecurityExceptionHandler;
import com.gooodh.auth.handler.login.LoginFailHandler;
import com.gooodh.auth.handler.login.LoginSuccessHandler;
import com.gooodh.auth.handler.login.username.UsernameAuthenticationFilter;
import com.gooodh.auth.handler.login.username.UsernameAuthenticationProvider;
import com.gooodh.auth.resourceApi.JwtAuthenticationFilter;
import com.gooodh.auth.service.JwtService;
import com.gooodh.auth.service.SecurityPermissionService;
import com.gooodh.model.po.SysPermission;
import com.gooodh.model.po.SysRole;
import jakarta.servlet.Filter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.SecurityContextHolderFilter;
import org.springframework.security.web.savedrequest.NullRequestCache;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final ApplicationContext applicationContext;
    private final SecurityPermissionService permissionService;
    private final AuthenticationEntryPoint authenticationExceptionHandler = new CustomAuthenticationExceptionHandler();
    private final Filter globalSpringSecurityExceptionHandler = new CustomSecurityExceptionHandler();

    public SecurityConfig(ApplicationContext applicationContext, SecurityPermissionService permissionService) {
        this.applicationContext = applicationContext;
        this.permissionService = permissionService;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    private void commonHttpSetting(HttpSecurity http) throws Exception {
        // 禁用SpringSecurity默认filter。这些filter都是非前后端分离项目的产物，用不上.
        // yml配置文件将日志设置DEBUG模式，就能看到加载了哪些filter
        // logging:
        //    level:
        //       org.springframework.security: DEBUG
        // 表单登录/登出、session管理、csrf防护等默认配置，如果不disable。会默认创建默认filter
        http.formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .sessionManagement(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                // requestCache用于重定向，前后端分析项目无需重定向，requestCache也用不上
                .requestCache(cache -> cache
                        .requestCache(new NullRequestCache())
                )
                // 无需给用户一个匿名身份
                .anonymous(AbstractHttpConfigurer::disable);
        // 处理 SpringSecurity 异常响应结果。响应数据的结构，改成业务统一的JSON结构。不要框架默认的响应结构
        http.exceptionHandling(exceptionHandling ->
                        exceptionHandling
                                // 认证失败异常
                                .authenticationEntryPoint(authenticationExceptionHandler)
                // 鉴权失败异常
//                        .accessDeniedHandler(authorizationExceptionHandler)
        );
        // 其他未知异常. 尽量提前加载。
        http.addFilterBefore(globalSpringSecurityExceptionHandler, SecurityContextHolderFilter.class);
    }

    @Bean
    public SecurityFilterChain loginFilterChain(HttpSecurity http) throws Exception {
        commonHttpSetting(http);

        // 使用securityMatcher限定当前配置作用的路径
        http.securityMatcher("/login/*")
                .authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated());

        LoginSuccessHandler loginSuccessHandler = applicationContext.getBean(LoginSuccessHandler.class);
        LoginFailHandler loginFailHandler = applicationContext.getBean(LoginFailHandler.class);

        // 登录方式：用户名/邮箱、密码登录
        String usernameLoginPath = "/login/password"; // 登录路径
        // 用 lambda 实现 RequestMatcher
        RequestMatcher usernameLoginMatcher = request ->
                usernameLoginPath.equals(request.getServletPath()) &&
                        HttpMethod.POST.matches(request.getMethod());
        // 创建 UsernameAuthenticationFilter
        UsernameAuthenticationFilter usernameLoginFilter = new UsernameAuthenticationFilter(
                usernameLoginMatcher,
                // 校验
                new ProviderManager(List.of(applicationContext.getBean(UsernameAuthenticationProvider.class))),
                // 成功失败处理
                loginSuccessHandler,
                loginFailHandler
        );
        // 加入过滤链
        http.addFilterBefore(usernameLoginFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        commonHttpSetting(http);
        // 动态加载 RBAC 权限
        http.authorizeHttpRequests(authorize -> {
            List<SysPermission> permissions = permissionService.loadUrlRoleMappings();
            for (SysPermission p : permissions) {
                String url = p.getUrl();
                String method = p.getMethod();

                if (url == null || p.getRoles() == null || p.getRoles().isEmpty()) {
                    continue; // 没有URL或没有角色，跳过
                }

                // 提取角色编码
                String[] roleCodes = p.getRoles().stream()
                        .map(SysRole::getRoleCode)
                        .toArray(String[]::new);

                // method为空或ALL时，匹配所有方法
                if (method == null || "ALL".equalsIgnoreCase(method)) {
                    authorize.requestMatchers(url).hasAnyAuthority(roleCodes);
                } else {
                    authorize.requestMatchers(HttpMethod.valueOf(method), url).hasAnyAuthority(roleCodes);
                }
            }
            // TODO 白名单放行
            authorize.requestMatchers(
                    "/register",
                    "/sendCode",
                    "/login/password"
            ).permitAll();

            authorize.anyRequest().denyAll(); // 默认拒绝所有未匹配的请求
        });

        // 加入 JWT 过滤器
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(applicationContext.getBean(JwtService.class));
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
