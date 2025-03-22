package codegen;
import java.util.List;
class CompletionRequest {
    public String model;
    public String prompt;
    public String suffix;
    public int max_tokens;
    public double temperature;
}

class ChatRequest {
    public String model;
    public List<Message> messages;
    public int max_tokens;
}

class Message {
    public String role;
    public String content;

    public Message(String role, String content) {
        this.role = role;
        this.content = content;
    }
}

class ApiResponse {
    public String text;
    public List<Choice> choices;
}

class Choice {
    public Message message;
}