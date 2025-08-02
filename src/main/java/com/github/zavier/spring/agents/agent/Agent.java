package com.github.zavier.spring.agents.agent;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.ai.util.json.schema.JsonSchemaGenerator;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class Agent {
    @Setter
    private String name;
    @Setter
    private String instructions;
    @Setter
    private String handoffDescription;
    @Setter
    private ChatModel chatModel;
    @Setter
    private List<Object> tools = new ArrayList<>();
    @Setter
    private List<Agent> handoffs = new ArrayList<>();

    private ChatClient chatClient;


    public void init() {
        Assert.notNull(chatModel, "ChatModel must not be null");
        log.info("Initializing agent: {}", name);

        final List<ToolCallback> list = handoffs.stream()
                .map(Agent::getToolCallback)
                .toList();


        final ChatClient.Builder builder = ChatClient.builder(chatModel);
        chatClient = builder.defaultSystem(instructions)
                .defaultTools(tools.toArray())
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .defaultToolCallbacks(list)
                .build();
    }


    public String execute(String input) {
        log.info("Executing agent: {}", name);
        return chatClient.prompt()
                .user( input)
                .call()
                .content();
    }

    public Flux<String> asyncExecute(String input) {
        log.info("async Executing agent: {}", name);
        return chatClient.prompt()
                .user(input)
                .stream()
                .content();
    }


    public ToolCallback getToolCallback() {
        return new AgentToolCallback(this);
    }

    public static class AgentToolCallback implements ToolCallback {

        private final Agent agent;

        public AgentToolCallback(Agent agent) {
            this.agent = agent;
        }

        @Override
        public ToolDefinition getToolDefinition() {
            final Method callMethod;
            try {
                callMethod = AgentToolCallback.class.getMethod("call", String.class);
            } catch (NoSuchMethodException e) {
                log.error("Error generating JSON schema for method: {}", e.getMessage(), e);
                throw new RuntimeException(e);
            }
            final String methodInput = JsonSchemaGenerator.generateForMethodInput(callMethod);
            return ToolDefinition.builder()
                    .name(agent.name)
                    .description(agent.handoffDescription)
                    .inputSchema(methodInput)
                    .build();
        }

        @Override
        public String call(String toolInput) {
            return agent.execute(toolInput);
        }
    }

}
