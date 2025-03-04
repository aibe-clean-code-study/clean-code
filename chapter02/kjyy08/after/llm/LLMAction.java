package kjyy08.after.llm;

public enum LLMAction {
    GENERATE_CONTENT("generateContent");

    final String name;

    LLMAction(String name) {
        this.name = name;
    }
}