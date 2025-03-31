package org.juyb99.pickmecupspring.repository;

import io.github.cdimascio.dotenv.Dotenv;
import org.juyb99.pickmecupspring.common.util.httpclient.APIClientParam;
import org.juyb99.pickmecupspring.common.util.httpclient.RestAPIRepository;
import org.juyb99.pickmecupspring.common.util.json.JsonUtil;
import org.juyb99.pickmecupspring.model.gemini.GeminiModel;
import org.juyb99.pickmecupspring.model.gemini.GeminiRequest;
import org.juyb99.pickmecupspring.model.gemini.GeminiRequest.Content;
import org.juyb99.pickmecupspring.model.gemini.GeminiRequest.GenerationConfig;
import org.juyb99.pickmecupspring.model.gemini.GeminiRequest.SystemInstruction;
import org.juyb99.pickmecupspring.model.gemini.GeminiResponse;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class GeminiRepository extends RestAPIRepository {
    private final String GEMINI_API_BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/";
    private final String GEMINI_API_KEY = Dotenv.configure().ignoreIfMissing().load().get("GEMINI_API_KEY");

    public String requestGeminiAPI(List<Content> contents, GeminiModel model,
                                   SystemInstruction systemInstruction,
                                   GenerationConfig generationConfig) {
        GeminiRequest geminiRequest = createGeminiRequest(contents, systemInstruction, generationConfig);

        String response = requestAPI(APIClientParam.builder()
                .url(GEMINI_API_BASE_URL + model.getName() + ":generateContent?key=" + GEMINI_API_KEY)
                .method(HttpMethod.POST)
                .body(JsonUtil.toJson(geminiRequest))
                .headers(new String[]{"Content-Type", "application/json"})
                .build()).orElseThrow(() -> new RuntimeException("Gemini API request failed"));

        GeminiResponse geminiResponse = JsonUtil.fromJson(response, GeminiResponse.class);

        return geminiResponse.candidates().get(0).content().parts().get(0).text();
    }

    private GeminiRequest createGeminiRequest(List<Content> contents,
                                              SystemInstruction systemInstruction,
                                              GenerationConfig generationConfig) {
        return GeminiRequest.builder()
                .contents(contents)
                .systemInstruction(systemInstruction)
                .generationConfig(generationConfig)
                .build();
    }
}
