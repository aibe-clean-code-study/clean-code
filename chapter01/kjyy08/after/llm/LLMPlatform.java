package kjyy08.after.llm;

public enum LLMPlatform {
    GOOGLE(System.getenv("GEMINI_API_KEY")),
    GROQ(System.getenv("GROQ_API_KEY")),
    TOGETHER_AI(System.getenv("TOGETHER_API_KEY"));

    final String apiKey;

    LLMPlatform(String apiKey) {
        this.apiKey = apiKey;
    }
}