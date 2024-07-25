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

/**'
 * 요청 시 요청 정보 로깅 Aspect
 *
 * @author 김종원
 * @version 1.0
 * @since 24. 7. 25. (목)
 */
@Component
@Aspect
@Slf4j
public class LoggingClientAgent {

    /**
     * 요청 정보 로깅 메소드
     * 
     * @param joinPoint 조인트 포인트
     * @return 결과 오브젝트
     */
    @Around("execution(* com.vj.lets.web..controller.*.*(..))")
    public Object loggingUserAgent(ProceedingJoinPoint joinPoint) {

        String controllerName = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        log.info("요청 컨트롤러 : {}", controllerName);
        log.info("요청 메소드 : {} 요청", methodName);

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
