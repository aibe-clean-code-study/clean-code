import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.logging.Level;
import java.util.logging.Logger;

public class JsonExtractorImpl implements JsonExtractor {
    private static final Logger logger = Logger.getLogger(JsonExtractorImpl.class.getName());

    /**
     * Gemini 응답에서 텍스트를 추출합니다.
     *
     * @param geminiResponseBody Gemini 응답 본문
     * @return 추출된 텍스트 정보
     */
    public String extractTextFromGeminiResponse(String geminiResponseBody) {
        String text = "";
        try {
            // Gemini 응답 JSON 파싱
            JSONObject responseJson = new JSONObject(geminiResponseBody);
            // 텍스트 정보 추출
            text = responseJson.getJSONArray("candidates")
                    .getJSONObject(0)
                    .getJSONObject("content")
                    .getJSONArray("parts")
                    .getJSONObject(0)
                    .getString("text");
        } catch (JSONException e) {
            // 예외 발생 시 로깅
            logger.log(Level.SEVERE, "Error extracting text from Gemini response. Response body: " + geminiResponseBody, e);
        }
        return text;
    }

    /**
     * 날씨 응답에서 데이터를 추출하고, 포맷하여 반환합니다.
     *
     * @param weatherResponseBody 날씨 응답 본문
     * @return 포맷된 날씨 정보
     */
    public String extractTextFromWeatherResponse(String weatherResponseBody) {
        try {
            // 날씨 데이터 추출
            JSONArray items = extractWeatherItems(weatherResponseBody);
            // 날씨 데이터를 포맷하여 반환
            return formatWeatherData(items);
        } catch (JSONException e) {
            // 예외 발생 시 로깅 및 기본 오류 메시지 반환
            logger.log(Level.SEVERE, "Error extracting weather data from response: " + weatherResponseBody, e);
            return "날씨 정보를 불러오는 중 오류가 발생했습니다.";
        }
    }

    /**
     * 날씨 응답 본문에서 날씨 항목들을 추출합니다.
     *
     * @param weatherResponseBody 날씨 응답 본문
     * @return 날씨 항목들의 배열
     * @throws JSONException JSON 파싱 예외
     */
    private JSONArray extractWeatherItems(String weatherResponseBody) throws JSONException {
        JSONObject responseJson = new JSONObject(weatherResponseBody);
        JSONObject body = responseJson.getJSONObject("response").getJSONObject("body");
        return body.getJSONObject("items").getJSONArray("item");
    }

    /**
     * 날씨 항목을 읽기 쉬운 형식으로 포맷합니다.
     *
     * @param items 날씨 항목들의 배열
     * @return 포맷된 날씨 정보
     */
    private String formatWeatherData(JSONArray items) {
        StringBuilder weatherInfo = new StringBuilder("날씨 정보:\n");

        for (int i = 0; i < items.length(); i++) {
            JSONObject item = items.getJSONObject(i);
            String category = item.getString("category");
            String obsrValue = item.getString("obsrValue");

            // 카테고리 이름 변환
            String categoryName = getCategoryName(category);
            weatherInfo.append(categoryName).append(": ").append(obsrValue);

            // 카테고리에 따라 단위 추가
            if (category.equals("VEC")) {
                weatherInfo.append("도");
            } else if (!category.equals("PTY") && !category.equals("RN1") && !category.equals("VEC")) {
                weatherInfo.append(getUnit(category));
            }
            weatherInfo.append("\n");
        }
        return weatherInfo.toString();
    }

    /**
     * 카테고리 코드에 해당하는 이름을 반환합니다.
     *
     * @param category 카테고리 코드
     * @return 카테고리 이름
     */
    private String getCategoryName(String category) {
        return switch (category) {
            case "PTY" -> "강수 형태";
            case "REH" -> "습도";
            case "RN1" -> "1시간 강수량";
            case "T1H" -> "기온";
            case "UUU" -> "동서바람성분";
            case "VEC" -> "풍향";
            case "VVV" -> "남북바람성분";
            case "WSD" -> "풍속";
            default -> category;
        };
    }

    /**
     * 카테고리 코드에 해당하는 단위를 반환합니다.
     *
     * @param category 카테고리 코드
     * @return 카테고리의 단위
     */
    private static String getUnit(String category) {
        return switch (category) {
            case "REH" -> "%";
            case "RN1" -> "mm";
            case "T1H" -> "℃";
            case "UUU", "VVV", "WSD" -> "m/s";
            default -> "";
        };
    }
}
