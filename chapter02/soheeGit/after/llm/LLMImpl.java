package soheeGit.after.llm;

import after.webClient.WebClientImpl;

public class LLMImpl extends WebClientImpl implements LLM {

    @Override
    public String callAPI(LLMModel model, String body) {
        String path = "";
        HttpMethod method = HttpMethod.GET;
        String[] headers = new String[0];
        String apiKey = "";
        switch (model.platform) {
            case GOOGLE -> {
                path = "https://generativelanguage.googleapis.com/v1beta/models/%s:%s?key=%s".formatted(
                        model.name, model.action.name, model.platform.apiKey);
                method = HttpMethod.POST;
                headers = new String[]{"Content-Type", "application/json"};
                logger.info(path);
            }
            case GROQ -> {
            }
            case TOGETHER_AI -> {
            }
        }
        String result = null;
        logger.info(body);
        try {
            result = sendRequest(makeRequest(path, method, body, headers));
        } catch (Exception e) {
            logger.severe(e.toString());
            throw new RuntimeException(e);
        }
        return result;
    }
}

interface LLM {
    enum LLMPlatform {
        GOOGLE(System.getenv("GEMINI_API_KEY")),
        GROQ(System.getenv("GROQ_API_KEY")),
        TOGETHER_AI(System.getenv("TOGETHER_API_KEY"));
        final String apiKey;
        LLMPlatform(String apiKey) {
            this.apiKey = apiKey;
        }
    }

    enum LLMModel {
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

    enum LLMAction {
        GENERATE_CONTENT("generateContent");

        final String name;

        LLMAction(String name) {
            this.name = name;
        }
    }

    String callAPI(LLMModel model, String body);
}