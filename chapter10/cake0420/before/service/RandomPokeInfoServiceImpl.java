package service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.dto.PokeRecordDTO;
import util.ApiUtils;
import util.PokeDataParser;

import java.io.IOException;
import java.util.Optional;
import java.util.logging.Level;

public class RandomPokeInfoServiceImpl extends ApiUtils implements PokeInfoService {
    private RandomPokeInfoServiceImpl() {}
    private static final RandomPokeInfoServiceImpl instance = new RandomPokeInfoServiceImpl();
    public static RandomPokeInfoServiceImpl getInstance() {
        return instance;
    }
    public PokeRecordDTO getPokeInfo() {
        try {
            // 랜덤 포켓몬 이름 가져오기
            String pokeName = getRandomPokeName();

            // 포켓몬 기본 정보 API 호출
            Optional<String> pokemonData = getApiResponse(dotenv.get("POKE_API_URL") + pokeName, "GET", null);
            if (pokemonData.isEmpty()) {
                throw new IOException("포켓몬 데이터를 가져오는 데 실패했습니다.");
            }

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode pokemonNode = objectMapper.readTree(pokemonData.get());

            // 포켓몬 종 정보 API 호출
            JsonNode speciesNode = null;
            if (pokemonNode.has("species")) {
                String speciesUrl = pokemonNode.get("species").get("url").asText();
                Optional<String> speciesData = getApiResponse(speciesUrl, "GET", null);
                if (speciesData.isPresent()) {
                    speciesNode = objectMapper.readTree(speciesData.get());
                }
            }

            // PokeRecordDTO 생성
            PokeRecordDTO pokeRecordDTO = new PokeRecordDTO(
                    pokeName,
                    PokeDataParser.extractTypes(pokemonNode),
                    PokeDataParser.extractMoves(pokemonNode),
                    PokeDataParser.extractImageUrl(pokemonNode),
                    speciesNode != null ? PokeDataParser.extractKoreanName(speciesNode).orElse(null) : null,
                    speciesNode != null ? PokeDataParser.extractKoreanFlavorText(speciesNode).orElse("") : ""
            );

            logger.log(Level.INFO, pokeRecordDTO.toString());
            return pokeRecordDTO;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "포켓몬 Api에 문제가 발생했습니다.: ", e);
            return null;
        }
    }

    private String getRandomPokeName() throws IOException {
        Optional<String> allPokemonData = getApiResponse(dotenv.get("POKE_API_URL"), "GET", null);
        if (allPokemonData.isEmpty()) {
            throw new IOException("포켓몬 목록을 가져오는 데 실패했습니다.");
        }

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode results = objectMapper.readTree(allPokemonData.get()).get("results");
        if (results.isEmpty()) {
            throw new IOException("포켓몬 목록이 비어 있습니다.");
        }

        int randomIndex = (int) (Math.random() * results.size());
        return results.get(randomIndex).get("name").asText();
    }
}
