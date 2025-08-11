package com.mariosmant.webapp.mediahub.core.conf.scheduler;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
@EnableScheduling
public class SchedulingConfig implements org.springframework.scheduling.annotation.SchedulingConfigurer {

    private final CustomCronTrigger customCronTrigger = new CustomCronTrigger("0/10 * * * * ?"); // Runs every 10 seconds

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(taskScheduler());
        taskRegistrar.addTriggerTask(new CustomTriggerTask(customCronTrigger), customCronTrigger);
    }

    public Executor taskScheduler() {
        return Executors.newScheduledThreadPool(1);
    }


}

