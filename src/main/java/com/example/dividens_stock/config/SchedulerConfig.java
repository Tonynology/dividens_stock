package com.example.dividens_stock.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Configuration
public class SchedulerConfig implements SchedulingConfigurer {

    // Thread Poll
    // Thread는 생성시키고 소멸할 때 많은 리소스를 소모하기 때문에
    // 매번 Thread를 생성하고 없애는 대신에 Thread Poll에는 설정된 크기의
    // Thread를 만들어 놓고 해당 Thread들을 계속해서 재사용 할 수 있게 관리를 해준다.

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        ThreadPoolTaskScheduler threadPoll = new ThreadPoolTaskScheduler();

        int n = Runtime.getRuntime().availableProcessors(); // 코어의 갯수를 가져오기.
        threadPoll.setPoolSize(n + 1);
        threadPoll.initialize();

        taskRegistrar.setTaskScheduler(threadPoll);
    }
}
