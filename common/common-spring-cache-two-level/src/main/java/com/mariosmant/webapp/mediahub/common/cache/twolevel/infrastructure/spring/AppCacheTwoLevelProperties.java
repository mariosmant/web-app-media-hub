package com.mariosmant.webapp.mediahub.common.cache.twolevel.infrastructure.spring;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ConfigurationProperties(prefix = "app.cache.two.level")
public class AppCacheTwoLevelProperties {
    private List<String> caches = new ArrayList<>();
}
