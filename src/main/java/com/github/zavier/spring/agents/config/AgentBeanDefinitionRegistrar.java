package com.github.zavier.spring.agents.config;

import com.github.zavier.spring.agents.agent.Agent;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class AgentBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar,
        EnvironmentAware, BeanFactoryAware, PriorityOrdered, ResourceLoaderAware {
    private BeanFactory beanFactory;
    private Environment environment;
    private ResourceLoader resourceLoader;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        // 使用Binder绑定配置到List
        List<AgentConfig> agents = Binder.get(environment)
                .bind("spring.ai.agents", Bindable.listOf(AgentConfig.class))
                .orElse(List.of());

        for (AgentConfig agentConfig : agents) {
            String beanName = agentConfig.getName();

            // 检查是否已注册同名Bean
            if (registry.containsBeanDefinition(beanName)) {
                throw new IllegalStateException("Bean already exists: " + beanName);
            }

            BeanDefinition beanDefinition = createAgentBeanDefinition(agentConfig);
            registry.registerBeanDefinition(beanName, beanDefinition);
        }
    }

    private BeanDefinition createAgentBeanDefinition(AgentConfig agentConfig) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder
                .genericBeanDefinition(Agent.class);

        // 设置基本属性
        builder.addPropertyValue("name", agentConfig.getName());
        builder.addPropertyValue("instructions", agentConfig.getInstructions());

        // 如果配置了文件，则从文件中读取
        if (agentConfig.getInstructions() != null && agentConfig.getInstructions().startsWith("classpath:")) {
            try {
                final Resource resource = resourceLoader.getResource(agentConfig.getInstructions());
                String instructions = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
                builder.addPropertyValue("instructions", instructions);
            } catch (IOException e) {
                throw new IllegalStateException("Failed to read instructions file: " + agentConfig.getInstructions(), e);
            }
        }

        // handoffs-description
        builder.addPropertyValue("handoffDescription", agentConfig.getHandoffDescription());


        // chatModel
        if (StringUtils.isNotBlank(agentConfig.getChatModel())) {
            builder.addPropertyReference("chatModel", agentConfig.getChatModel());
        } else {
            builder.addPropertyReference("chatModel", "openAiChatModel");
        }

        // tools
        if (agentConfig.getTools() != null && !agentConfig.getTools().isEmpty()) {
            ManagedList<RuntimeBeanReference> toolsList = new ManagedList<>();
            for (String toolName : agentConfig.getTools()) {
                if (beanFactory.containsBean(toolName)) {
                    toolsList.add(new RuntimeBeanReference(toolName));
                } else {
                    throw new IllegalStateException("Tool bean not found: " + toolName);
                }
            }
            builder.addPropertyValue("tools", toolsList);
        }

        // handoffs
        if (agentConfig.getHandoffs() != null && !agentConfig.getHandoffs().isEmpty()) {
            ManagedList<RuntimeBeanReference> handoffsList = new ManagedList<>();
            for (String handoffName : agentConfig.getHandoffs()) {
                if (beanFactory.containsBean(handoffName)) {
                    handoffsList.add(new RuntimeBeanReference(handoffName));
                } else {
                    throw new IllegalStateException("Handoff bean not found: " + handoffName);
                }
            }
            builder.addPropertyValue("handoffs", handoffsList);
        }

        builder.setInitMethodName("init");

        return builder.getBeanDefinition();
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
