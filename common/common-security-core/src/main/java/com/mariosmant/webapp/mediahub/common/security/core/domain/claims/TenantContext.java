package com.mariosmant.webapp.mediahub.common.security.core.domain.claims;

public final class TenantContext {
    private static final ThreadLocal<String> TENANT = new ThreadLocal<>();

    private TenantContext() {}

    public static void set(String tenantId) { TENANT.set(tenantId); }
    public static String get() { return TENANT.get(); }
    public static void clear() { TENANT.remove(); }
}
