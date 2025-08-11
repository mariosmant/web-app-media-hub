package com.mariosmant.webapp.mediahub.common.conf.filters;

import org.apache.logging.log4j.ThreadContext;
import org.springframework.lang.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import com.mariosmant.webapp.mediahub.common.util.IpAddressUtils;

public class MDCLogFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        // Retrieve the username from the request.
        String username = request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : "anonymous";

        // Determine the client IP addressing potential proxies.
        String clientIp = IpAddressUtils.getClientIpAddress(request);

        // Put information into MDC so that log messages include these values
        ThreadContext.put("username", username);
        ThreadContext.put("clientIp", clientIp);

        try {
            filterChain.doFilter(request, response);
        } finally {
            // Clear MDC to avoid leakage for other requests.
            ThreadContext.clearMap();
        }
    }
}
