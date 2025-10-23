package com.gooodh.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gooodh.annotation.OperationLog;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.*;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import java.util.*;

@Slf4j
@Aspect
@Component
public class OperationLogAspect {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Pointcut("@annotation(com.gooodh.annotation.OperationLog)")
    public void operationLog() {}

    @Around("operationLog()")
    public Object recordLog(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        // 获取方法信息
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        OperationLog annotation = signature.getMethod().getAnnotation(OperationLog.class);
        String methodName = signature.getDeclaringTypeName() + "." + signature.getName();
        Object[] args = joinPoint.getArgs();

        // 获取请求
        HttpServletRequest request = Arrays.stream(args)
                .filter(a -> a instanceof HttpServletRequest)
                .map(a -> (HttpServletRequest) a)
                .findFirst()
                .orElse(null);

        String ip = (request != null) ? request.getRemoteAddr() : "N/A";

        // 执行目标方法
        Object result = null;
        try {
            result = joinPoint.proceed();
            long cost = System.currentTimeMillis() - startTime;

            log.info("""
                    操作日志:
                      模块: {}
                      方法: {}
                      参数: {}
                      返回: {}
                      耗时: {} ms
                      IP: {}
                    """,
                    annotation.value(),
                    methodName,
                    objectMapper.writeValueAsString(joinPoint.getArgs()),
                    objectMapper.writeValueAsString(result),
                    cost,
                    ip
            );
        } catch (Exception e) {
            log.error("方法执行异常：{}，错误信息：{}", methodName, e.getMessage(), e);
            throw e;
        }

        return result;
    }
}
