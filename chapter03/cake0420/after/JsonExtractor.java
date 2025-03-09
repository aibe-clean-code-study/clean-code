public interface JsonExtractor {
    String extractTextFromWeatherResponse(String WeatherResponseBody);

    String extractTextFromGeminiResponse(String geminiResponseBody);
}
