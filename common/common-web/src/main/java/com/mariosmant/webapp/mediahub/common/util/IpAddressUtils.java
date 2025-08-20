package com.mariosmant.webapp.mediahub.common.util;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public class IpAddressUtils {

    /**
     * Checks if the given IP string is invalid.
     *
     * @param ip the IP address string to check
     * @return true if ip is null, empty, or equals "unknown" (case-insensitive), false otherwise.
     */
    private static boolean isInvalidIp(String ip) {
        // If the header (IP) is missing, empty, or marked as "unknown".
        return ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip);
    }

    /**
     * Retrieve the client's IP address taking into account common proxy headers.
     * <p>
     * This static method is thread-safe because it relies solely on the passed
     * HttpServletRequest and uses only local variables.
     * </p>
     *
     * @param request the HTTP request from which to retrieve the IP address
     * @return the client IP address or the first IP in the list if multiple are provided
     */
    public static String getClientIpAddress(HttpServletRequest request) {
        // Create an immutable list of header names in order of precedence.
        List<String> headerNames = List.of(
                "X-Forwarded-For",   // This is the standard header used by proxies/load balancers to specify the original IP address. It may contain a comma-separated list of IPs if the request passed through multiple proxies.
                "Proxy-Client-IP",   // Used by some proxy servers.
                "WL-Proxy-Client-IP",// Typically used by WebLogic Server proxies.
                "HTTP_CLIENT_IP",    // Some proxies use this header instead.
                "HTTP_X_FORWARDED_FOR" // Similar to X-Forwarded-For in some setups.
        );

        String ip = null;
        for (String header : headerNames) {
            ip = request.getHeader(header);
            if (!isInvalidIp(ip)) {
                break;
            }
        }

        if (isInvalidIp(ip)) {
            // If none of the proxy headers yield an IP, fall back to getRemoteAddr().
            // This returns the IP address of the direct connection, which might be that of the proxy
            // rather than the originating client.
            ip = request.getRemoteAddr();
        }

        // In case the "X-Forwarded-For" header contained multiple IP addresses (comma-separated),
        // take the first one, which typically represents the original client's IP.
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }

        return ip;
    }
}
