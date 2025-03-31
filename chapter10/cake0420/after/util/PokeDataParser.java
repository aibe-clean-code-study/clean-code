package util;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PokeDataParser {

    public static List<String> extractTypes(JsonNode pokemonNode) {
        List<String> types = new ArrayList<>();
        if (pokemonNode.has("types")) {
            for (JsonNode typeNode : pokemonNode.get("types")) {
                types.add(typeNode.get("type").get("name").asText());
            }
        }
        return types;
    }

    public static List<String> extractMoves(JsonNode pokemonNode) {
        List<String> moves = new ArrayList<>();
        if (pokemonNode.has("moves")) {
            for (JsonNode moveNode : pokemonNode.get("moves")) {
                moves.add(moveNode.get("move").get("name").asText());
            }
        }
        return moves;
    }

    public static String extractImageUrl(JsonNode pokemonNode) {
        if (pokemonNode.has("sprites") && pokemonNode.get("sprites").has("front_default")) {
            return pokemonNode.get("sprites").get("front_default").asText();
        }
        return ""; // 이미지가 없을 경우 빈 문자열 반환
    }

    public static Optional<String> extractKoreanName(JsonNode speciesNode) {
        if (speciesNode.has("names")) {
            for (JsonNode nameNode : speciesNode.get("names")) {
                if ("ko".equals(nameNode.get("language").get("name").asText())) {
                    return Optional.of(nameNode.get("name").asText());
                }
            }
        }
        return Optional.empty();
    }

    public static Optional<String> extractKoreanFlavorText(JsonNode speciesNode) {
        if (speciesNode.has("flavor_text_entries")) {
            for (JsonNode entryNode : speciesNode.get("flavor_text_entries")) {
                if ("ko".equals(entryNode.get("language").get("name").asText())) {
                    return Optional.of(entryNode.get("flavor_text").asText().replaceAll("\n", " "));
                }
            }
        }
        return Optional.empty();
    }
}
