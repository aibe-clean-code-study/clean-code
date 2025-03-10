import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Text {
    // 로거 객체를 초기화하여 로그 메시지를 기록
    private static final Logger logger = Logger.getLogger(Text.class.getName());

    // 환경 변수에서 API 키와 URL을 읽어옴
    private static final String CONTENT_TYPE = "application/json";
    private static final String SLACK_WEBHOOK_URL = System.getenv("SLACK_WEBHOOK_URL");
    private static final String WEATHER_API_KEY = System.getenv("WEATHER_API_KEY");
    private static final String URL = System.getenv("URL");
    private static final String GEMINI_API_KEY = System.getenv("GEMINI_API_KEY");
    private static final String MESSAGE = System.getenv("MESSAGE");

    public static void main(String[] args) {
        try {
            // 환경 변수 유효성 검사
            validateEnvVariables();

            // 날씨 API 호출 및 응답 추출
            final WeatherImpl weatherImpl = new WeatherImpl();
            final JsonExtractorImpl extractJson = new JsonExtractorImpl();

            // 날씨 API 응답을 JSON 형식으로 받아오기
            String json = weatherImpl.WeatherAPI(WEATHER_API_KEY);
            String weatherResponse = extractJson.extractTextFromWeatherResponse(json);

            // 메시지 API 요청 URL 생성
            String apiUrl = String.format("%s%s", URL, GEMINI_API_KEY);
            String messageRequest = buildMessageRequest(weatherResponse);

            // HTTP 클라이언트 생성
            HttpClient client = HttpClient.newHttpClient();

            // Gemini API 요청 전송
            HttpResponse<String> geminiResponse = sendGeminiRequest(client, apiUrl, messageRequest);

            // Gemini API 응답 처리
            if (geminiResponse.statusCode() == 200) {
                // 응답에서 텍스트 추출
                String geminiResponseBody = geminiResponse.body();
                String geminiText = extractJson.extractTextFromGeminiResponse(geminiResponseBody);
                logger.info("Gemini Response Text: " + geminiText);

                // Slack 메시지 전송
                sendSlackMessage(client, geminiText);
            } else {
                // Gemini API 호출 실패 시 로그 기록
                logger.severe("Gemini API 오류: 상태 코드 " + geminiResponse.statusCode());
                logger.severe("Gemini API 응답: " + geminiResponse.body());
            }
        } catch (Exception e) {
            // 예외 발생 시 로그 기록
            logger.log(Level.SEVERE, "예외 발생: ", e);
        }
    }

    /**
     * 환경 변수들이 올바르게 설정되었는지 확인하는 메소드
     * @throws IllegalStateException 환경 변수 중 하나라도 설정되지 않은 경우 예외 발생
     */
    private static void validateEnvVariables() {
        if (SLACK_WEBHOOK_URL == null || WEATHER_API_KEY == null || URL == null || GEMINI_API_KEY == null || MESSAGE == null) {
            throw new IllegalStateException("환경 변수가 설정되지 않았습니다.");
        }
    }

    /**
     * Gemini API로 보낼 메시지 요청을 생성하는 메소드
     * @param weatherResponse 날씨 API 응답에서 추출한 텍스트
     * @return Gemini API로 보낼 메시지 요청
     */
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

    /**
     * Gemini API로 요청을 보내고 응답을 받는 메소드
     * @param client HTTP 클라이언트
     * @param apiUrl Gemini API URL
     * @param messageRequest Gemini API에 보낼 메시지
     * @return Gemini API의 응답
     * @throws IOException 네트워크 문제나 I/O 오류가 발생한 경우
     * @throws InterruptedException 요청 처리 중 인터럽트가 발생한 경우
     */
    private static HttpResponse<String> sendGeminiRequest(HttpClient client, String apiUrl, String messageRequest) throws IOException, InterruptedException {
        HttpRequest geminiRequest = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Content-Type", CONTENT_TYPE)
                .POST(HttpRequest.BodyPublishers.ofString(messageRequest))
                .build();

        return client.send(geminiRequest, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Slack 메시지를 전송하는 메소드
     * @param client HTTP 클라이언트
     * @param geminiText Gemini API로부터 받은 텍스트
     * @throws IOException 네트워크 문제나 I/O 오류가 발생한 경우
     * @throws InterruptedException 요청 처리 중 인터럽트가 발생한 경우
     */
    private static void sendSlackMessage(HttpClient client, String geminiText) throws IOException, InterruptedException {
        // Slack 메시지 본문 생성
        String slackMessageBody = String.format("{\"text\": \"%s\"}", geminiText);

        HttpRequest slackRequest = HttpRequest.newBuilder()
                .uri(URI.create(SLACK_WEBHOOK_URL))
                .header("Content-Type", CONTENT_TYPE)
                .POST(HttpRequest.BodyPublishers.ofString(slackMessageBody))
                .build();

        // Slack으로 메시지 전송
        HttpResponse<String> slackResponse = client.send(slackRequest, HttpResponse.BodyHandlers.ofString());

        // Slack 응답 처리
        if (slackResponse.statusCode() == 200) {
            logger.info("Slack 메시지가 성공적으로 전송되었습니다!");
        } else {
            logger.severe("Slack으로 메시지 전송 오류. 상태 코드: " + slackResponse.statusCode());
        }
    }
}
