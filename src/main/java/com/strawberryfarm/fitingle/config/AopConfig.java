package com.strawberryfarm.fitingle.config;

import com.strawberryfarm.fitingle.aop.logtrace.LogTrace;
import com.strawberryfarm.fitingle.aop.logtrace.LogTraceAspect;
import com.strawberryfarm.fitingle.aop.logtrace.ThreadLocalLogTrace;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AopConfig {

    @Bean
    public LogTraceAspect logTraceAspect(LogTrace logTrace) {
        return new LogTraceAspect(logTrace);
    }

    @Bean
    public LogTrace logTrace() {
        return new ThreadLocalLogTrace();
    }
}
