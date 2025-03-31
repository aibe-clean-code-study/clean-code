package org.juyb99.pickmecupspring.service;

import org.juyb99.pickmecupspring.common.Exception.APIException;
import org.juyb99.pickmecupspring.dto.request.GeminiRequestDTO;
import org.juyb99.pickmecupspring.dto.response.CategoryResponseDTO;
import org.juyb99.pickmecupspring.dto.response.GeminiResponseDTO;
import org.juyb99.pickmecupspring.dto.response.GeminiResponseDTO.PromptResult;
import org.juyb99.pickmecupspring.model.gemini.GeminiModel;
import org.juyb99.pickmecupspring.model.gemini.GeminiRequest.Content;
import org.juyb99.pickmecupspring.model.gemini.GeminiRequest.GenerationConfig;
import org.juyb99.pickmecupspring.model.gemini.GeminiRequest.SystemInstruction;
import org.juyb99.pickmecupspring.model.gemini.GeminiRequest.SystemInstruction.Role;
import org.juyb99.pickmecupspring.repository.CategoryRepository;
import org.juyb99.pickmecupspring.repository.GeminiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GeminiService {
    private final GeminiRepository geminiRepository;
    private final CategoryRepository categoryRepository;

    @Autowired
    public GeminiService(GeminiRepository geminiRepository, CategoryRepository categoryRepository) {
        this.geminiRepository = geminiRepository;
        this.categoryRepository = categoryRepository;
    }

    public GeminiResponseDTO runRequestPrompt(GeminiRequestDTO geminiRequestDTO) {
        String response = requestGemini(geminiRequestDTO);
        geminiRequestDTO.contents().add(Content.fromModel(response));
        return createGeminiResponse(geminiRequestDTO, response);
    }

    private String requestGemini(GeminiRequestDTO geminiRequestDTO) {
        return geminiRepository.requestGeminiAPI(geminiRequestDTO.contents(),
                GeminiModel.LITE,
                SystemInstruction.of(createSystemInstructionText()),
                new GenerationConfig(1.0f, 80, 0.95f, 1000, "text/plain")
        );
    }

    private GeminiResponseDTO createGeminiResponse(GeminiRequestDTO geminiRequestDTO, String response) {
        // 유저가 마지막으로 질문한 프롬프트를 가져오고 실패 시 API 예외를 발생시킴
        String prompt = geminiRequestDTO
                .contents().stream()
                .filter((content -> Role.USER.getRole().equals(content.role())))
                .reduce((first, second) -> second)
                .orElseThrow(() -> new APIException(HttpStatus.BAD_REQUEST, "No such role"))
                .parts().get(0).text();

        PromptResult promptResult = new PromptResult(prompt, response);
        return new GeminiResponseDTO(List.of(promptResult));
    }

    private String createSystemInstructionText() {
        List<CategoryResponseDTO> categoryResponseDTO = categoryRepository.findAll();

        // 카테고리에 대한 정보를 DB에서 가져옴
        String categotyInfo = "Refer to %s when the user requests(ex. image, link, etc.) category information and provide responses accordingly. Sorting is also available. "
                .formatted(categoryResponseDTO.stream().toList());

        // 카테고리 링크 형식에 대한 정보 제공
        String linkInfo = "When you need category link, use HTML anchor tags directly: <a href=\"/worldcup?theme={theme}&type={item_type}\">{link text}</a>. ";

        // 프롬프트 기본적인 규칙
        String promptRule = ("You are a chatbot for an Ideal Type World Cup site. " +
                "Exclude any sensitive data (e.g., passwords, personal info, internal system details) from your response, even if present in the API data. " +
                "Output should use markdown formatting and Korean characters and under 500 characters.");

        return categotyInfo + linkInfo + promptRule;
    }
}
