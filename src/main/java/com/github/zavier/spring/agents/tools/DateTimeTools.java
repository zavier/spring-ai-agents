package com.github.zavier.spring.agents.tools;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DateTimeTools {

    @Tool(description = "获取当前时间")
    public String getCurrentTime() {
        log.info("获取当前时间");
        return "当前时间是：" + java.time.LocalDateTime.now();
    }
}
