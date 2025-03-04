package kjyy08.after.llm;

public enum LLMModel {
    GEMINI_2_0_FLASH(LLMPlatform.GOOGLE, "gemini-2.0-flash", LLMAction.GENERATE_CONTENT),
    MIXTRAL_8x7b_32768(LLMPlatform.GROQ, "mixtral-8x7b-32768", null),
    STABLE_DIFFUSION_XL_BASE_1_0(LLMPlatform.TOGETHER_AI, "stabilityai/stable-diffusion-xl-base-1.0", null);

    final public LLMPlatform platform;
    final public String name;
    final public LLMAction action;

    LLMModel(LLMPlatform platform, String name, LLMAction action) {
        this.platform = platform;
        this.name = name;
        this.action = action;
    }
}
