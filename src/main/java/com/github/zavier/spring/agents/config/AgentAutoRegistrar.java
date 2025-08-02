package com.github.zavier.spring.agents.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(AgentBeanDefinitionRegistrar.class)
public class AgentAutoRegistrar {

}