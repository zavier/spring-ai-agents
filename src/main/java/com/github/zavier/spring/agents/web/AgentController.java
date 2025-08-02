package com.github.zavier.spring.agents.web;

import com.github.zavier.spring.agents.agent.Agent;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@Slf4j
@RestController
public class AgentController {

    @Resource
    private Agent triageAgent;

    @GetMapping("/triage")
    public Flux<String> triage(String input) {
        return triageAgent.asyncExecute(input);
    }
}
