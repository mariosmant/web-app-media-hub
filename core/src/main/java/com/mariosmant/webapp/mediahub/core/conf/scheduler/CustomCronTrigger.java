package com.mariosmant.webapp.mediahub.core.conf.scheduler;

import lombok.Getter;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.support.CronTrigger;

import java.time.Instant;
import java.time.LocalDateTime;

class CustomCronTrigger implements Trigger {
    @Getter
    private LocalDateTime scheduledTime;

    private final CronTrigger cronTrigger;

    public CustomCronTrigger(String cronExpression) {
        this.cronTrigger = new CronTrigger(cronExpression);
    }

    @Override
    public Instant nextExecution(@NonNull TriggerContext triggerContext) {
        Instant nextExecutionInstant = cronTrigger.nextExecution(triggerContext);
        if (nextExecutionInstant != null) {
            scheduledTime = nextExecutionInstant.atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
        }
        return nextExecutionInstant;
    }

}