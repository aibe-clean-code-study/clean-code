package org.juyb99.pickmecupjsp.repository;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.Getter;
import org.juyb99.pickmecupjsp.common.repository.Repository;
import org.juyb99.pickmecupjsp.dto.gemini.GeminiModel;
import org.juyb99.pickmecupjsp.dto.gemini.GeminiRequest;
import org.juyb99.pickmecupjsp.dto.gemini.GeminiResponse;
import org.juyb99.pickmecupjsp.dto.httpclient.APIClientParam;
import org.juyb99.pickmecupjsp.dto.httpclient.HttpMethod;
import org.juyb99.pickmecupjsp.util.json.JsonUtil;

import java.util.List;

public class GeminiRepository extends Repository {
    private final String GEMINI_API_BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/";
    private final String GEMINI_API_KEY = Dotenv.configure().ignoreIfMissing().load().get("GEMINI_API_KEY");

    @Getter
    private final static GeminiRepository instance = new GeminiRepository();

    public GeminiResponse requestGeminiAPI(String prompt, GeminiModel model,
                                           GeminiRequest.SystemInstruction systemInstruction,
                                           GeminiRequest.GenerationConfig generationConfig) {
        GeminiRequest geminiRequest = createGeminiRequest(prompt, systemInstruction, generationConfig);

        String response = requestAPI(APIClientParam.builder()
                .url(GEMINI_API_BASE_URL + model.getName() + ":generateContent?key=" + GEMINI_API_KEY)
                .method(HttpMethod.POST)
                .body(JsonUtil.toJson(geminiRequest))
                .headers(new String[]{"Content-Type", "application/json"})
                .build()).orElseThrow(() -> new RuntimeException("Gemini API request failed"));

        return JsonUtil.fromJson(response, GeminiResponse.class);
    }

    private GeminiRequest createGeminiRequest(String prompt,
                                              GeminiRequest.SystemInstruction systemInstruction,
                                              GeminiRequest.GenerationConfig generationConfig) {
        GeminiRequest.Content content = new GeminiRequest.Content(prompt);

        return GeminiRequest.builder()
                .contents(List.of(content))
                .systemInstruction(systemInstruction)
                .generationConfig(generationConfig)
                .build();
    }
}
