package com.strawberryfarm.fitingle.utils;

import com.strawberryfarm.fitingle.aop.logtrace.TraceId;
import com.strawberryfarm.fitingle.aop.logtrace.TraceIdHolder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LogUtil {
    public static void customInfo(String message) {
        TraceId traceId = TraceIdHolder.getId();
        if (traceId != null) {
            String prefix = generatePrefix(traceId.getLevel());
            log.info("[{}] {}{}", traceId.getId(), prefix, message);
        } else {
            log.info("{}", message);
        }
    }

    private static String generatePrefix(int level) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < level; i++) {
            sb.append("|   ");
        }
        if (level > 0) {
            sb.append("|-- ");
        }
        return sb.toString();
    }

}
