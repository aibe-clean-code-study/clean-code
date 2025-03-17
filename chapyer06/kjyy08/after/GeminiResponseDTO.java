package org.juyb99.pickmecupspring.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record GeminiResponseDTO(@JsonProperty("text_list") List<Text> textList) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Text(String text) {

    }
}
