package com.mariosmant.webapp.mediahub.upload.service.infrastructure.spring.logging;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.boot.logging.LogLevel;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

// Simple audit aspect
@Aspect
@Component
@Slf4j
public class LogAspect {

    @Around("execution(* com.example.api..*(..)) && @annotation(org.springframework.web.bind.annotation.RequestMapping)")
    public Object startEndInfo(ProceedingJoinPoint pjp) throws Throwable {
        return startEnd(pjp, LogLevel.INFO);
    }

    @Around("execution(* com.example.api..*(..)) && @annotation(org.springframework.web.bind.annotation.RequestMapping)")
    public Object startEndDebug(ProceedingJoinPoint pjp) throws Throwable {
        return startEnd(pjp, LogLevel.DEBUG);
    }

    @Around("execution(* com.example.api..*(..)) && @annotation(org.springframework.web.bind.annotation.RequestMapping)")
    public Object startEndTrace(ProceedingJoinPoint pjp) throws Throwable {
        return startEnd(pjp, LogLevel.TRACE);
    }

    private Object startEnd(ProceedingJoinPoint pjp, LogLevel logLevel) throws Throwable {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String user = auth != null ? auth.getName() : "anonymous";
        long t0 = System.currentTimeMillis();
        try {
            log.info("Start method={}", pjp.getSignature());
            Object res = pjp.proceed();
            log.info("End method={} durationMs={}", pjp.getSignature(), System.currentTimeMillis()-t0);
            return res;
        } catch (Exception e) {
            log.error("Exception method={} failure={} exception={} durationMs={}",
                    pjp.getSignature(), e.getClass().getSimpleName(), ExceptionUtils.getStackTrace(e), System.currentTimeMillis()-t0);
            throw e;
        }
    }

    private void logBasedOnLogLevel(LogLevel logLevel, String logMessage, Object... logParams) throws IllegalArgumentException {
        switch (logLevel) {
            case TRACE -> log.trace(logMessage, logParams);
            case INFO -> log.info(logMessage, logParams);
            case WARN -> log.warn(logMessage, logParams);
            case ERROR -> log.error(logMessage, logParams);
            default -> throw new IllegalArgumentException("Unsupported log level: " + logLevel);
        }
    }
}

