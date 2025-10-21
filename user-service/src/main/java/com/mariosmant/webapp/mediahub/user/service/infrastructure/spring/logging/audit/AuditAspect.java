package com.mariosmant.webapp.mediahub.user.service.infrastructure.spring.logging.audit;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

// Simple audit aspect
@Aspect
@Component
@Slf4j
public class AuditAspect {

    @Around("execution(* com.example.api..*(..)) && @annotation(org.springframework.web.bind.annotation.RequestMapping)")
    public Object audit(ProceedingJoinPoint pjp) throws Throwable {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String user = auth != null ? auth.getName() : "anonymous";
        long t0 = System.currentTimeMillis();
        try {
            Object res = pjp.proceed();
            log.info("audit user={} method={} success durationMs={}", user, pjp.getSignature(), System.currentTimeMillis()-t0);
            return res;
        } catch (Exception e) {
            log.warn("audit user={} method={} failure={} durationMs={}",
                    user, pjp.getSignature(), e.getClass().getSimpleName(), System.currentTimeMillis()-t0);
            throw e;
        }
    }

    @Around("execution(* com.example.api..*(..)) && @annotation(org.springframework.web.bind.annotation.RequestMapping)")
    public Object startEndInfo(ProceedingJoinPoint pjp) throws Throwable {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String user = auth != null ? auth.getName() : "anonymous";
        long t0 = System.currentTimeMillis();
        try {
            Object res = pjp.proceed();
            log.info("audit user={} method={} success durationMs={}", user, pjp.getSignature(), System.currentTimeMillis()-t0);
            return res;
        } catch (Exception e) {
            log.warn("audit user={} method={} failure={} durationMs={}",
                    user, pjp.getSignature(), e.getClass().getSimpleName(), System.currentTimeMillis()-t0);
            throw e;
        }
    }

    @Around("execution(* com.example.api..*(..)) && @annotation(org.springframework.web.bind.annotation.RequestMapping)")
    public Object startEndDebug(ProceedingJoinPoint pjp) throws Throwable {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String user = auth != null ? auth.getName() : "anonymous";
        long t0 = System.currentTimeMillis();
        try {
            Object res = pjp.proceed();
            log.info("audit user={} method={} success durationMs={}", user, pjp.getSignature(), System.currentTimeMillis()-t0);
            return res;
        } catch (Exception e) {
            log.warn("audit user={} method={} failure={} durationMs={}",
                    user, pjp.getSignature(), e.getClass().getSimpleName(), System.currentTimeMillis()-t0);
            throw e;
        }
    }

    @Around("execution(* com.example.api..*(..)) && @annotation(org.springframework.web.bind.annotation.RequestMapping)")
    public Object startEndTrace(ProceedingJoinPoint pjp) throws Throwable {
        return startEnd(pjp);
    }

    public Object startEnd(ProceedingJoinPoint pjp) throws Throwable {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String user = auth != null ? auth.getName() : "anonymous";
        long t0 = System.currentTimeMillis();
        try {
            Object res = pjp.proceed();
            log.info("audit user={} method={} success durationMs={}", user, pjp.getSignature(), System.currentTimeMillis()-t0);
            return res;
        } catch (Exception e) {
            log.warn("audit user={} method={} failure={} durationMs={}",
                    user, pjp.getSignature(), e.getClass().getSimpleName(), System.currentTimeMillis()-t0);
            throw e;
        }
    }
}

