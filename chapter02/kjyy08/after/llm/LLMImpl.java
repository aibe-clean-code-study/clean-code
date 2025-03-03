package kjyy08.after.llm;

import kjyy08.after.webclient.HttpMethod;
import kjyy08.after.webclient.WebClientImpl;

import java.net.http.HttpRequest;
import java.util.logging.Logger;

public class LLMImpl implements LLM {
    private final String GOOGLE_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/";
    private final WebClientImpl webClient;
    private final Logger logger;

    public LLMImpl() {
        this.webClient = new WebClientImpl();
        this.logger = Logger.getLogger(WebClientImpl.class.getName());
    }

    @Override
    public String requestLLMPromptAPI(LLMModel model, String body) {
        String platformRequestPath = "";
        HttpMethod httpMethod = HttpMethod.POST;
        String[] headers = new String[]{"Content-Type", "application/json"};

        switch (model.platform) {
            case GOOGLE -> platformRequestPath = buildRequestPathForGoogle(model);
            case GROQ -> platformRequestPath = buildRequestPathForGroq(model);
            case TOGETHER_AI -> platformRequestPath = buildRequestPathForTogetherAI(model);
        }

        String responseBody;

        try {
            HttpRequest request = webClient.makeRequest(platformRequestPath, httpMethod, body, headers);
            responseBody = webClient.sendRequest(request);
        } catch (Exception e) {
            logger.severe(e.toString());
            throw new RuntimeException(e);
        }
        return responseBody;
    }

    private String buildRequestPathForGoogle(LLMModel model) {
        return String.format(GOOGLE_API_URL + "%s:%s?key=%s", model.name, model.action.name, model.platform.apiKey);
    }

    private String buildRequestPathForGroq(LLMModel model) {
        return "";
    }

    private String buildRequestPathForTogetherAI(LLMModel model) {
        return "";
    }
}
