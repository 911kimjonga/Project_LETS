package com.vj.lets.web.global.aspect;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;

@Component
@Aspect
@Slf4j
public class LoggingClientAgent {

    @Around("execution(* com.vj.lets.web..controller.*.*(..))")
    public Object loggingUserAgent(ProceedingJoinPoint joinPoint) {

        String controllerName = joinPoint.getSignature().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        log.info("{} 컨트롤러 요청", controllerName);
        log.info("{} 메소드 요청", methodName);

        Object result = null;
        try {
            result = joinPoint.proceed();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        } finally {
            HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();

            String requestURL = request.getRequestURL().toString();
            String ip = request.getRemoteAddr();
            String method = request.getMethod();
            String userAgent = request.getHeader("User-Agent");

            log.info("requestURL : {}", requestURL);
            log.info("ip : {}", ip);
            log.info("method : {}", method);
            log.info("userAgent : {}", userAgent);
        }

        return result;
    }

}
