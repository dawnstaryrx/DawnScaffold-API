package com.gooodh.auth.handler.login.username;

import com.gooodh.auth.model.UserLoginDTO;
import com.gooodh.common.user.service.SysPermissionService;
import com.gooodh.common.user.service.SysRoleService;
import com.gooodh.model.po.SysUser;
import com.gooodh.common.user.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 帐号密码登录认证
 */
@Component
public class UsernameAuthenticationProvider implements AuthenticationProvider {
  @Autowired
  private SysUserService userService;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private SysRoleService roleService;

  @Autowired
  private SysPermissionService permissionService;

  public UsernameAuthenticationProvider() {
    super();
  }

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    // 用户提交的用户名 + 密码：
    String username = (String)authentication.getPrincipal();
    String password = (String) authentication.getCredentials();

    // 查数据库，匹配用户信息
    SysUser user = userService.getUserByUsernameOrEmailOrPhone(username);
    if (user == null
        || !passwordEncoder.matches(password, user.getPassword())) {
      // 密码错误，直接抛异常。
      // 根据SpringSecurity框架的代码逻辑，认证失败时，应该抛这个异常：org.springframework.security.core.AuthenticationException
      // BadCredentialsException就是这个异常的子类
      // 抛出异常后后，AuthenticationFailureHandler的实现类会处理这个异常。
      throw new BadCredentialsException("用户名或密码不正确");
    }

      // 查询用户角色和权限
      // TODO 加入缓存
      List<String> roles = roleService.getRolesByUserId(user.getId());
      List<String> permissions = permissionService.getPermissionsByUserId(user.getId());

      // 构造 DTO，存储到 Authentication 中
      UserLoginDTO userLoginDTO = UserLoginDTO.fromSysUser(user, roles, permissions);

      // 创建认证对象
      UsernameAuthentication token = new UsernameAuthentication();
      token.setCurrentUser(userLoginDTO);
      token.setAuthenticated(true); // 认证通过
      return token;
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return authentication.isAssignableFrom(UsernameAuthentication.class);
  }
}

