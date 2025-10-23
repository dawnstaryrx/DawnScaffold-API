package com.gooodh.common.user.service.impl;

import com.gooodh.common.user.service.PhoneService;
import com.gooodh.common.user.service.SysUserService;
import com.gooodh.exception.ExceptionTool;
import com.gooodh.model.constant.RedisConstant;
import com.gooodh.model.enums.CodeTypeEnum;
import com.gooodh.model.po.SysUser;
import com.gooodh.util.IpUtil;
import com.gooodh.util.RandomUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class PhoneServiceImpl implements PhoneService {

//    private final SysUserService sysUserService;
    private final StringRedisTemplate stringRedisTemplate;
    private final RedisTemplate<String, String> redisTemplate;
    private final HttpServletRequest request;

    private static final String RATE_LIMITER_KEY = "rate_limiter:";
    private static final int MAX_REQUESTS = 5; // 最大请求次数
    private static final int TIME_WINDOW = 60; // 时间窗口（秒）

    @Value("${sms.username}")
    private String smsUsername;
    @Value("${sms.password}")
    private String smsPassword;
    @Value("${sms.content}")
    private String smsContent;

    @Override
    public void sendMsg(String phone,String code, String type) {
        // 获取客户端 IP
        String clientIp = IpUtil.getClientIp(request);
        // 检查是否超过请求限制
        if (!allowRequest(clientIp)) {
            ExceptionTool.throwException("请求频率过高，请稍后再试");
        }
        if (phone == null){
            ExceptionTool.throwException("手机号不能为空");
        }
//        SysUser userByPhone = sysUserService.getUserByUsernameOrEmailOrPhone(phone);
        String content = "";
        String key = "";
        if (CodeTypeEnum.REGISTER_PHONE_CODE.getType().equals(type)){
            // 注册
            // 判断是否已经被注册
//            if (userByPhone != null){
//                ExceptionTool.throwException("该手机号已注册");
//            }
            // 进行注册
            content = "注册验证码为 " + code + " ，五分钟有效，请妥善保管！";
            key = RedisConstant.PHONE_REGISTER_CODE_PREFIX + phone;
        } else if(CodeTypeEnum.LOGIN_PHONE_CODE.getType().equals(type)){
            // 登录
            // 判断是否已经被注册
//            if (userByPhone == null){
//                ExceptionTool.throwException("该手机号未注册");
//            }
            content = "登录验证码为 " + code + " ，五分钟有效，请妥善保管！";
            key = RedisConstant.PHONE_LOGIN_CODE_PREFIX + phone;
        } else {
            ExceptionTool.throwException("未知的验证码类型");
        }
        // 往Redis中存储一个键值对
        ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
        operations.set(key,  code, RedisConstant.CODE_TIME_SECOND, TimeUnit.SECONDS);
        // 模拟发送短信
        log.info("发送短信成功------------" + content);
        sendSmsByBao(code, phone, clientIp);
    }

    private boolean allowRequest(String ip) {
        String key = RATE_LIMITER_KEY + ip;
        Long currentCount = redisTemplate.opsForValue().increment(key);

        if (currentCount == 1) {
            redisTemplate.expire(key, TIME_WINDOW, TimeUnit.SECONDS);
        }

        return currentCount <= MAX_REQUESTS;
    }

    private void sendSmsByBao(String code, String phone, String ip){
        if (!allowRequest(ip)) {
            System.out.println(ip + "请求频率过高，请稍后再试");
            throw new RuntimeException("请求频率过高，请稍后再试");
        }
        String myUsername = smsUsername; //在短信宝注册的用户名
        String myPassword = smsPassword; //在短信宝注册的密码
        String myContent = smsContent;
        String content = myContent.replace("{code}", code); //要发送的短信内容

        String httpUrl = "http://api.smsbao.com/sms";

        StringBuffer httpArg = new StringBuffer();
        httpArg.append("u=").append(myUsername).append("&");
        httpArg.append("p=").append(md5(myPassword)).append("&");
        httpArg.append("m=").append(phone).append("&");
        httpArg.append("c=").append(encodeUrlString(content, "UTF-8"));

        String result = request(httpUrl, httpArg.toString());
        System.out.println(result);
    }

    public static String request(String httpUrl, String httpArg) {
        BufferedReader reader = null;
        String result = null;
        StringBuilder sbf = new StringBuilder();
        httpUrl = httpUrl + "?" + httpArg;

        try {
            URL url = new URL(httpUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            InputStream is = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String strRead = reader.readLine();
            if (strRead != null) {
                sbf.append(strRead);
                while ((strRead = reader.readLine()) != null) {
                    sbf.append("\n");
                    sbf.append(strRead);
                }
            }
            reader.close();
            result = sbf.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String md5(String plainText) {
        StringBuilder buf = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes());
            byte[] b = md.digest();
            int i;
            buf = new StringBuilder();
            for (byte value : b) {
                i = value;
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return buf.toString();
    }

    public static String encodeUrlString(String str, String charset) {
        String strret = null;
        if (str == null)
            return str;
        try {
            strret = java.net.URLEncoder.encode(str, charset);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return strret;
    }
}
