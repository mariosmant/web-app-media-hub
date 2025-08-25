package com.mariosmant.webapp.mediahub.common.rate.limiter.token.bucket.infrastructure.spring;

//import io.github.bucket4j.Bandwidth;
//import io.github.bucket4j.Bucket;
//import io.github.bucket4j.BucketConfiguration;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Distributed-ready rate limiter using Bucket4j. This implementation
 * uses an in-memory fallback; integrate Redis proxy manager as needed.
 */
public class TokenBucketRateLimitFilter extends OncePerRequestFilter {

//    private final Map<String, Bucket> localBuckets = new ConcurrentHashMap<>();
//
//    private final BucketConfiguration defaultConfig = BucketConfiguration.builder()
//            .addLimit(
//                    Bandwidth.builder()
//                            .capacity(100L)
//                            .refillIntervally(100L, Duration.ofMinutes(1L))
//                            .build()
//            )
//            .build();

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest req,
                                    @NonNull HttpServletResponse res,
                                    @NonNull FilterChain chain)
            throws ServletException, IOException {
//        String key = resolveKey(req);
//        Bucket bucket = localBuckets.computeIfAbsent(key, k -> Bucket.builder().addLimit(defaultConfig.getBandwidths()[0]).build());
//
//        if (bucket.tryConsume(1)) {
//            chain.doFilter(req, res);
//        } else {
//            res.setStatus(429);
//            res.setContentType("application/json");
//            res.getWriter().write("{\"error\":\"rate_limited\",\"message\":\"Too many requests\"}");
//        }
        // TODO Remove below line.
        chain.doFilter(req, res);
    }

    // TODO Use client key resolver and combine also IP address utils. Make configurable if fall back to X-Forwarded-For should be used or not. Default make it disabled as strongly preferred the new standard.
    private String resolveKey(HttpServletRequest req) {
        String user = req.getUserPrincipal() != null ? req.getUserPrincipal().getName() : null;
        if (StringUtils.hasText(user)) return "user:" + user;
        String xfwd = req.getHeader("X-Forwarded-For");
        if (StringUtils.hasText(xfwd)) return "ip:" + xfwd.split(",")[0].trim();
        return "ip:" + req.getRemoteAddr();
    }
}
