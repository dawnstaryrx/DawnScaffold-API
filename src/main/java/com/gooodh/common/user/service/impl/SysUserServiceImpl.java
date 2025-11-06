package com.gooodh.common.user.service.impl;

import com.gooodh.common.user.service.*;
import com.gooodh.exception.ExceptionTool;
import com.gooodh.model.constant.RedisConstant;
import com.gooodh.model.dto.EmailDTO;
import com.gooodh.model.dto.RegisterDTO;
import com.gooodh.model.enums.CodeTypeEnum;
import com.gooodh.model.po.SysRole;
import com.gooodh.model.po.SysUser;
import com.gooodh.common.user.mapper.SysUserMapper;
import com.gooodh.util.RandomUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.gooodh.model.constant.RedisConstant.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class SysUserServiceImpl implements SysUserService {

    private final SysUserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final SmsService smsService;
    private final StringRedisTemplate stringRedisTemplate;
    private final SysRoleService sysRoleService;
    private final SysUserRoleService sysUserRoleService;

    @Value("${email.register.title}")
    private String registerEmailTitle;
    @Value("${email.register.content}")
    private String registerEmailContent;
    @Value("${email.login.title}")
    private String loginEmailTitle;
    @Value("${email.login.content}")
    private String loginEmailContent;

    @Override
    public SysUser getUserByUsernameOrEmailOrPhone(String username) {
        return userMapper.getUserByUsernameOrEmailOrPhone(username);
    }

    @Override
    public SysUser getUserById(Integer userId) {
        return userMapper.getById(userId);
    }

    @Override
    public void register(RegisterDTO registerDTO) {
        // 查询用户是否存在
        SysUser userByUsernameOrEmail = userMapper.getUserByUsernameOrEmailOrPhone(registerDTO.getEmail());
        if (userByUsernameOrEmail != null){
            ExceptionTool.throwException("用户已存在！");
        }
        // 检查验证码是否正确
        ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
        String key = "";
        if (Objects.equals(registerDTO.getType(), CodeTypeEnum.REGISTER_EMAIL_CODE.getType())){
            key = EMAIL_REGISTER_CODE_PREFIX + registerDTO.getEmail();
        } else if (Objects.equals(registerDTO.getType(), CodeTypeEnum.REGISTER_PHONE_CODE.getType())){
            key = PHONE_REGISTER_CODE_PREFIX + registerDTO.getPhone();
        }
        String redisCode = operations.get(key);
        if (!registerDTO.getCode().equals(redisCode)){
            ExceptionTool.throwException("验证码错误！");
        }
        // 检查两次密码是否一致
        if (!registerDTO.getPassword().equals(registerDTO.getRePassword())){
            ExceptionTool.throwException("两次密码不一致！");
        }
        switch (registerDTO.getType()){
            case "email":
                break;
            case "phone":
                break;
            default:
                ExceptionTool.throwException("不支持的注册类型！");
        }
        SysUser user = new SysUser();
        user.setUsername(registerDTO.getEmail());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        // TODO 给超级管理员用户分配角色 ROLE_SUPER_ADMIN 其他用户 ROLE_USER
        SysRole roleSuperAdmin = sysRoleService.getRoleByRoleCode("ROLE_SUPER_ADMIN");
        SysRole roleUser = sysRoleService.getRoleByRoleCode("ROLE_USER");
        userMapper.add(user);
        //TODO  SuperAdmin
        sysUserRoleService.add(user.getId(), roleSuperAdmin.getId());
    }

    /**
     * 发送验证码
     * @param emailOrPhone
     * @param type
     */
    @Override
    public void sendCode(String emailOrPhone, String type, String token) {
        // 判断是否为空
        if (emailOrPhone == null || emailOrPhone.isEmpty()){
            ExceptionTool.throwException("邮箱或手机号不能为空！");
        }
        // 判断token是否有效
        if (token == null || token.isEmpty()) {
            ExceptionTool.throwException("请先通过行为验证！");
        }
        // Redis 中存储行为验证码通过的标识：CAPTCHA_VALID_TOKEN_PREFIX + token
        String redisKey = RedisConstant.CAPTCHA_VALID_TOKEN_PREFIX + token;
        String valid = stringRedisTemplate.opsForValue().get(redisKey);
        if (valid == null) {
            ExceptionTool.throwException("行为验证已失效，请重新验证！");
        }
        // 一旦使用成功，可选择立即删除 token，避免重复使用
        stringRedisTemplate.delete(redisKey);
        SysUser user = userMapper.getUserByUsernameOrEmailOrPhone(emailOrPhone);
        // 生成验证码
        String code = RandomUtil.getCode();
        String key = "";
        if (CodeTypeEnum.REGISTER_EMAIL_CODE.getType().equals(type)){
            if (user != null){
                ExceptionTool.throwException("用户已存在！");
            }
            // 发送验证码
            EmailDTO emailDTO = new EmailDTO();
            emailDTO.setEmail(emailOrPhone);
            emailDTO.setTitle(registerEmailTitle);
            emailDTO.setContent(registerEmailContent.replace("{code}", code));
            emailService.sendMsg(emailDTO);
            key = EMAIL_REGISTER_CODE_PREFIX + emailOrPhone;
        } else if (CodeTypeEnum.LOGIN_EMAIL_CODE.getType().equals(type)){
            if (user == null){
                ExceptionTool.throwException("用户不存在！");
            }
            // 发送验证码
            EmailDTO emailDTO = new EmailDTO();
            emailDTO.setEmail(emailOrPhone);
            emailDTO.setTitle(loginEmailTitle);
            emailDTO.setContent(loginEmailContent.replace("{code}", code));
            emailService.sendMsg(emailDTO);
            key = EMAIL_LOGIN_CODE_PREFIX + emailOrPhone;
        } else if (CodeTypeEnum.REGISTER_PHONE_CODE.getType().equals(type)){
            if (user != null){
                ExceptionTool.throwException("用户已存在！");
            }
            // 发送验证码
            smsService.sendMsg(emailOrPhone, code, type);
        } else if (CodeTypeEnum.LOGIN_PHONE_CODE.getType().equals(type)){
            if (user == null){
                ExceptionTool.throwException("用户不存在！");
            }
            // 发送验证码
            smsService.sendMsg(emailOrPhone, code, type);
        } else {
            ExceptionTool.throwException("不支持的验证码类型！");
        }
        // 往Redis中存储一个键值对
        ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
        operations.set(key,  code, CODE_TIME_SECOND, TimeUnit.SECONDS);
    }
}
