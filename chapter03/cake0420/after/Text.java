import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Text {
    private static final Logger logger = Logger.getLogger(Text.class.getName());

    private static final String CONTENT_TYPE = "application/json";
    private static final String SLACK_WEBHOOK_URL = System.getenv("SLACK_WEBHOOK_URL");
    private static final String WEATHER_API_KEY = System.getenv("WEATHER_API_KEY");
    private static final String URL = System.getenv("URL");
    private static final String GEMINI_API_KEY = System.getenv("GEMINI_API_KEY");
    private static final String MESSAGE = System.getenv("MESSAGE");

    public static void main(String[] args) {
        try {
            validateEnvVariables();

            final WeatherImpl weatherImpl = new WeatherImpl();
            final JsonExtractorImpl extractJson = new JsonExtractorImpl();

            String json = weatherImpl.WeatherAPI(WEATHER_API_KEY);
            String weatherResponse = extractJson.extractTextFromWeatherResponse(json);

            String apiUrl = String.format("%s%s", URL, GEMINI_API_KEY);
            String messageRequest = buildMessageRequest(weatherResponse);

            HttpClient client = HttpClient.newHttpClient();

            HttpResponse<String> geminiResponse = sendGeminiRequest(client, apiUrl, messageRequest);

            if (geminiResponse.statusCode() == 200) {
                String geminiResponseBody = geminiResponse.body();
                String geminiText = extractJson.extractTextFromGeminiResponse(geminiResponseBody);
                logger.info("Gemini Response Text: " + geminiText);

                sendSlackMessage(client, geminiText);
            } else {
                logger.severe("Gemini API 오류: 상태 코드 " + geminiResponse.statusCode());
                logger.severe("Gemini API 응답: " + geminiResponse.body());
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "예외 발생: ", e);
        }
    }

    private static void validateEnvVariables() {
        if (SLACK_WEBHOOK_URL == null || WEATHER_API_KEY == null || URL == null || GEMINI_API_KEY == null || MESSAGE == null) {
            throw new IllegalStateException("환경 변수가 설정되지 않았습니다.");
        }
    }

    private static String buildMessageRequest(String weatherResponse) {
        return String.format("{\n" +
                "  \"contents\": [\n" +
                "    {\n" +
                "      \"parts\": [\n" +
                "        {\"text\": \"%s%s\"}\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}", weatherResponse, MESSAGE);
    }

    private static HttpResponse<String> sendGeminiRequest(HttpClient client, String apiUrl, String messageRequest) throws IOException, InterruptedException {
        HttpRequest geminiRequest = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Content-Type", CONTENT_TYPE)
                .POST(HttpRequest.BodyPublishers.ofString(messageRequest))
                .build();

        return client.send(geminiRequest, HttpResponse.BodyHandlers.ofString());
    }

    private static void sendSlackMessage(HttpClient client, String geminiText) throws IOException, InterruptedException {
        String slackMessageBody = String.format("{\"text\": \"%s\"}", geminiText);

        HttpRequest slackRequest = HttpRequest.newBuilder()
                .uri(URI.create(SLACK_WEBHOOK_URL))
                .header("Content-Type", CONTENT_TYPE)
                .POST(HttpRequest.BodyPublishers.ofString(slackMessageBody))
                .build();

        HttpResponse<String> slackResponse = client.send(slackRequest, HttpResponse.BodyHandlers.ofString());

        if (slackResponse.statusCode() == 200) {
            logger.info("Slack 메시지가 성공적으로 전송되었습니다!");
        } else {
            logger.severe("Slack으로 메시지 전송 오류. 상태 코드: " + slackResponse.statusCode());
        }
    }
}
