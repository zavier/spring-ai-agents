package com.github.zavier.spring.agents.agent;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;

import java.util.List;

@Slf4j
public class Agent {
    private String name;
    private String instructions;
    private ChatModel chatModel;
    private List<Object> tools;


    private ChatClient chatClient;


    public void init() {
        Assert.notNull(chatModel, "ChatModel must not be null");
        log.info("Initializing agent: {}", name);

        final ChatClient.Builder builder = ChatClient.builder(chatModel);
        chatClient = builder.defaultSystem(instructions)
                .defaultTools(tools.toArray())
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();
    }


    public String execute(String input) {
        return chatClient.prompt()
                .user( input)
                .call()
                .content();
    }

    public Flux<String> asyncExecute(String input) {
        return chatClient.prompt()
                .user(input)
                .stream()
                .content();
    }








    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public ChatModel getChatModel() {
        return chatModel;
    }

    public void setChatModel(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    public List<Object> getTools() {
        return tools;
    }

    public void setTools(List<Object> tools) {
        this.tools = tools;
    }
}
