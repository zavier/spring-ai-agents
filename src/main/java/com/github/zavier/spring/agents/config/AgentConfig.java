package com.github.zavier.spring.agents.config;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AgentConfig {

    private String name;

    /**
     * Agent指令
     */
    private String instructions;

    private String handoffDescription;

    /**
     * 使用的聊天模型bean名称
     */
    private String chatModel;

    /**
     * 工具bean名称集合
     */
    private List<String> tools = new ArrayList<>();

    /**
     * 待转接的模型bean名称集合（agents）
     */
    private List<String> handoffs = new ArrayList<>();
}