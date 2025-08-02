package com.github.zavier.spring.agents.web;

import com.github.zavier.spring.agents.agent.Agent;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
public class AgentController {

    @Resource
    private Agent customerServiceAgent;
    @Resource
    private Agent orderServiceAgent;

    @Resource
    private Agent assistant;

    @Resource
    private ChatModel chatModel;

    private final ApplicationContext applicationContext;

    public AgentController(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @GetMapping("/agents")
    public List<String> getBeans() {
        final String[] beanNamesForType = applicationContext.getBeanNamesForType(Agent.class);
        for (String s : beanNamesForType) {
            final Object bean = applicationContext.getBean(s);
        }
        return Arrays.stream(beanNamesForType).toList();
    }

    @GetMapping("/chat")
    public Flux<String> execute(String input) {
        return assistant.asyncExecute(input);
    }
}
