package com.github.zavier.spring.agents.config;

import java.util.ArrayList;
import java.util.List;

public class AgentConfig {
    /**
     * Agent的bean名称
     */
    private String name;

    /**
     * Agent指令
     */
    private String instructions;

    /**
     * 使用的聊天模型bean名称
     */
    private String chatModel;

    /**
     * 工具bean名称集合
     */
    private List<String> tools = new ArrayList<>();

    // getters and setters
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

    public String getChatModel() {
        return chatModel;
    }

    public void setChatModel(String chatModel) {
        this.chatModel = chatModel;
    }

    public List<String> getTools() {
        return tools;
    }

    public void setTools(List<String> tools) {
        this.tools = tools;
    }
}