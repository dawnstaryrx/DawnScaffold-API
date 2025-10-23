package com.gooodh.auth.service;

import com.gooodh.util.JSONUtil;

public interface JwtService {
    /**
     * 生成 JWT
     * @param jwtPayload 负载对象
     * @param expiredAt 过期时间戳（毫秒）
     * @return JWT 字符串
     */
    String createJwt(Object jwtPayload, long expiredAt);

    /**
     * 校验 JWT 并返回负载对象
     * @param jwt JWT 字符串
     * @param jwtPayloadClass 负载类型
     * @param <T> 负载类型
     * @return 解析后的对象
     */
    <T> T verifyJwt(String jwt, Class<T> jwtPayloadClass);

    /**
     * 静态方法：不校验签名，直接解析 payload
     * @param jwt JWT 字符串
     * @param jwtPayloadClass 负载类型
     * @param <T> 负载类型
     * @return 解析后的对象
     */
    static <T> T getPayload(String jwt, Class<T> jwtPayloadClass) {
        if (jwt == null || jwt.isEmpty()) return null;
        try {
            byte[] decodedBytes = java.util.Base64.getDecoder().decode(jwt.split("\\.")[1]);
            return JSONUtil.parse(new String(decodedBytes), jwtPayloadClass);
        } catch (Exception e) {
            return null;
        }
    }

}
