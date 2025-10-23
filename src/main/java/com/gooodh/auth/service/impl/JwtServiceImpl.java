package com.gooodh.auth.service.impl;

import com.gooodh.auth.service.JwtService;
import com.gooodh.exception.ExceptionTool;
import com.gooodh.util.JSONUtil;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtServiceImpl implements JwtService, InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(JwtServiceImpl.class);

    private PrivateKey privateKey;
    private JwtParser jwtParser;

    @Value("${login.jwt.private-key}")
    private String privateKeyBase64;

    @Value("${login.jwt.public-key}")
    private String publicKeyBase64;

    @Override
    public void afterPropertiesSet() {
        this.privateKey = loadPrivateKey();
        this.jwtParser = loadJwtParser();
    }

    private PrivateKey loadPrivateKey() {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Decoders.BASE64.decode(privateKeyBase64));
            return keyFactory.generatePrivate(keySpec);
        } catch (Exception e) {
            logger.error("加载JWT私钥失败", e);
            ExceptionTool.throwException("获取JWT私钥失败");
            return null;
        }
    }

    private JwtParser loadJwtParser() {
        try {
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Decoders.BASE64.decode(publicKeyBase64));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keyFactory.generatePublic(keySpec);
            return Jwts.parserBuilder().setSigningKey(publicKey).build();
        } catch (Exception e) {
            logger.error("加载JWT公钥失败", e);
            ExceptionTool.throwException("获取JWT公钥失败");
            return null;
        }
    }

    @Override
    public String createJwt(Object jwtPayload, long expiredAt) {
        Map<String, Object> headMap = new HashMap<>();
        headMap.put("alg", SignatureAlgorithm.RS256.getValue());
        headMap.put("typ", "JWT");

        Map<String, Object> body = JSONUtil.parse(JSONUtil.stringify(jwtPayload), HashMap.class);

        return Jwts.builder()
                .setHeader(headMap)
                .setClaims(body)
                .setExpiration(new Date(expiredAt))
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }

    @Override
    public <T> T verifyJwt(String jwt, Class<T> jwtPayloadClass) {
        if (jwt == null || jwt.isEmpty()) return null;
        Jws<Claims> jws = jwtParser.parseClaimsJws(jwt);
        Claims claims = jws.getBody();
        if (claims == null) return null;
        return JSONUtil.convert(claims, jwtPayloadClass);
    }
}
