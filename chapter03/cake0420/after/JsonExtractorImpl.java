import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.logging.Level;
import java.util.logging.Logger;

public class JsonExtractorImpl implements JsonExtractor {
    private static final Logger logger = Logger.getLogger(JsonExtractorImpl.class.getName());

    public String extractTextFromGeminiResponse(String geminiResponseBody) {
        String text = "";
        try {
            JSONObject responseJson = new JSONObject(geminiResponseBody);
            text = responseJson.getJSONArray("candidates")
                    .getJSONObject(0)
                    .getJSONObject("content")
                    .getJSONArray("parts")
                    .getJSONObject(0)
                    .getString("text");
        } catch (JSONException e) {
            logger.log(Level.SEVERE, "Error extracting text from Gemini response. Response body: " + geminiResponseBody, e);
        }
        return text;
    }

    public String extractTextFromWeatherResponse(String WeatherResponseBody) {
        StringBuilder weatherInfo = new StringBuilder();

        try {
            JSONObject responseJson = new JSONObject(WeatherResponseBody);
            JSONObject body = responseJson.getJSONObject("response").getJSONObject("body");
            JSONArray items = body.getJSONObject("items").getJSONArray("item");

            weatherInfo.append("날씨 정보:\n");

            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);
                String category = item.getString("category");
                String obsrValue = item.getString("obsrValue");

                String categoryName = getCategoryName(category);

                weatherInfo.append(categoryName).append(": ").append(obsrValue);

                if(category.equals("VEC")){
                    weatherInfo.append("도");
                } else if (!category.equals("PTY") && !category.equals("RN1") && !category.equals("VEC")){
                    weatherInfo.append(getUnit(category));
                }

                weatherInfo.append("\n");
            }


        } catch (JSONException e) {
            logger.log(Level.SEVERE, "Error extracting weather from weather api response. Response body: " + weatherInfo, e);
        }
        return weatherInfo.toString();
    }

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

    private static String getUnit(String category) {
        return switch (category) {
            case "REH" -> "%";
            case "RN1" -> "mm";
            case "T1H" -> "℃";
            case "UUU" -> "m/s";
            case "VVV" -> "m/s";
            case "WSD" -> "m/s";
            default -> "";
        };
    }
}
