package org.juyb99.pickmecupspring.service;

import org.juyb99.pickmecupspring.dto.response.GeminiResponseDTO;
import org.juyb99.pickmecupspring.model.gemini.GeminiModel;
import org.juyb99.pickmecupspring.model.gemini.GeminiRequest;
import org.juyb99.pickmecupspring.repository.GeminiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GeminiService {
    private final GeminiRepository geminiRepository;

    @Autowired
    public GeminiService(GeminiRepository geminiRepository) {
        this.geminiRepository = geminiRepository;
    }

    public GeminiResponseDTO runPromptChaining(String... prompts) {
        // 첫 번째 프롬프트에 대해 Gemini API 호출
        List<GeminiResponseDTO.Text> promptList = new ArrayList<>();

        String respoonse = requestGeminiModel(prompts[0]);
        promptList.add(new GeminiResponseDTO.Text(respoonse));

        // 두 번째 프롬프트부터 순차적으로 체이닝
        for (int i = 1; i < prompts.length; i++) {
            String promptText = "%s 를 바탕으로 %s 프롬프트를 수행하세요.".formatted(respoonse, prompts[i]);
            respoonse = requestGeminiModel(promptText);
            promptList.add(new GeminiResponseDTO.Text(respoonse));
        }

        return new GeminiResponseDTO(promptList);
    }

    public String requestGeminiModel(String prompt) {
        return geminiRepository.requestGeminiAPI(prompt,
                GeminiModel.BASE,
                GeminiRequest.SystemInstruction.of("마크다운, 볼드체와 리스트 기호, 개행문자를 사용하지 않고 일반 텍스트를 출력하세요."),
                new GeminiRequest.GenerationConfig()
        ).text();
    }
}
