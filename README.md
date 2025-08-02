# Spring AI Agents


通过Spring AI 来快速实现一个简单的类似[OpenAI Agents SDK](https://openai.github.io/openai-agents-python/)的功能

具体效果为在application.yml 中配置对应的agent 信息，如

```yml
spring:
  ai:
    agents:
      # 定义一个Agent
      - name: historyTutor
        # 与大模型交互时的Agent的系统提示词
        instructions: You provide assistance with historical queries. Explain important events and context clearly.
        # 其他Agent可以获取到的关于当前agent的描述信息
        handoffDescription: Specialist agent for historical questions

      # 定义另一个Agent
      - name: mathTutor
        instructions: You provide help with math problems. Explain your reasoning at each step and include examples
        handoffDescription: Specialist agent for math questions

      # 定义入口agent
      - name: triageAgent
        instructions: You determine which agent/tools to use based on the user's homework question
        # 这里定义可以分派的其他agent有哪些
        handoffs:
          - historyTutor
          - mathTutor
```

使用时，按需注入即可：

```java
@Slf4j
@RestController
public class AgentController {

    // 通过名称注入
    @Resource
    private Agent triageAgent;

    @GetMapping("/triage")
    public Flux<String> triage(String input) {
        return triageAgent.asyncExecute(input);
    }
}
```
