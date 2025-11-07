package com.mariosmant.webapp.mediahub.common.web.rate.limiter.infrastructure.spring.redis;

import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;

public class RedisScriptLoader {
    public static <T> DefaultRedisScript<T> load(String path, Class<T> resultType) {
        DefaultRedisScript<T> script = new DefaultRedisScript<>();
        script.setLocation(new ClassPathResource(path));
        script.setResultType(resultType);
        return script;
    }
}
