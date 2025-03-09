import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class Text {
    public static void main(String[] args) throws IOException, ParseException { // 진입부분
        // 이게 있어야 이 클래스를 실행했을 때 작동을 함
        // 웹훅을 만들 거임 -> URL 필요함
        // 환경변수로 받아올 것임 -> yml 파일에서 전달하게
        String webhookUrl = System.getenv("SLACK_WEBHOOK_URL");
//        String slackMessage = System.getenv("SLACK_WEBHOOK_MSG");
        String weatherApiKey = System.getenv("WEATHER_API_KEY");
        String apiKey = System.getenv("GEMINI_API_KEY");  // Gemini API 키
        String json = WeatherAPI(weatherApiKey);
        String weatherResponse = extractTextFromWeatherResponse(json);
        System.out.println(apiKey);
        System.out.println(json);
//         Gemini API URL을 수정하여 API 키를 포함합니다.
        String apiUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + apiKey;
//         요청 본문 형식 (curl 요청에 맞게 수정)
        String messageRequest = "{\n" +
            "  \"contents\": [\n" +
            "    {\n" +
            "      \"parts\": [\n" +
            "        {\"text\": \"" + weatherResponse + "이 값은 기상청 단기예보 날씨 정보야 이 정보를 바탕으로 고양이 기상캐스터처럼 설명해줘 알겠다는 말은 하지말고 바로 시작해 " +
                "말투 끝에는 냥이라는 표현을 섞어줘 그리고 현재 gemini 자바 sdk는 이미지 업로드하여 전달만 하지 url 을 주는 가능이 없다냥 왜냐면 Java SDK에는 현재 이 기능이 없습다냥. 2025년 1분기까지 Java SDK에 파일 API를 도입할 계획이다냥 이라는 말을 해 " + "\"}\n" +
            "      ]\n" +
            "    }\n" +
            "  ]\n" +
            "}";
        // Java 11 -> fetch
        HttpClient client = HttpClient.newHttpClient();
        // 요청을 얹힐 거다
//         HttpRequest request = HttpRequest.newBuilder()
//                     .uri(URI.create(webhookUrl))
//             .header("Content-Type", "application/json")
//             .POST(HttpRequest.BodyPublishers.ofString("{\"text\":\"" + slackMessage + "\"}"))
//             .build();

        HttpRequest geminiRequest = HttpRequest.newBuilder()
            .uri(URI.create(apiUrl))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(messageRequest)) // 요청 본문
            .build();

//         try {
//             HttpResponse<String> response = client.send(
//                     request, HttpResponse.BodyHandlers.ofString()
//             );
//             System.out.println("요청 코드: " + response.statusCode());
//             System.out.println("응답 결과: " + response.body());
//         } catch (Exception e) {
//             e.printStackTrace();
//         }

        try {
            HttpResponse<String> geminiResponse = client.send(geminiRequest, HttpResponse.BodyHandlers.ofString());

            if (geminiResponse.statusCode() == 200) {
                String geminiResponseBody = geminiResponse.body();
                String geminiText = extractTextFromGeminiResponse(geminiResponseBody);
                System.out.println("Gemini Response Text: " + geminiText);

                String slackMessageBody = "{\"text\": \""  + geminiText + "\"}";

                HttpRequest slackRequest = HttpRequest.newBuilder()
                        .uri(URI.create(webhookUrl))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(slackMessageBody))
                        .build();

                HttpResponse<String> slackResponse = client.send(slackRequest, HttpResponse.BodyHandlers.ofString());

                if (slackResponse.statusCode() == 200) {
                    System.out.println("Slack 메시지가 성공적으로 전송되었습니다!");
                } else {
                    System.err.println("Slack으로 메시지 전송 오류. 상태 코드: " + slackResponse.statusCode());
                }
            } else {
                System.err.println("Gemini API 오류: 상태 코드 " + geminiResponse.statusCode());
                System.err.println("Gemini API 응답: " + geminiResponse.body()); // 오류 메시지 출력 추가
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String extractTextFromGeminiResponse(String geminiResponseBody) {
        String text = "";
        try {
            JSONObject responseJson = new JSONObject(geminiResponseBody);
            text = responseJson.getJSONArray("candidates")
                    .getJSONObject(0)
                    .getJSONObject("content")
                    .getJSONArray("parts")
                    .getJSONObject(0)
                    .getString("text");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return text;
    }




    private static String extractTextFromWeatherResponse(String WeatherResponseBody) {
        StringBuilder weatherInfo = new StringBuilder();

        try {
            JSONObject responseJson = new JSONObject(WeatherResponseBody);
            JSONObject body = responseJson.getJSONObject("response").getJSONObject("body");
            JSONArray items = body.getJSONObject("items").getJSONArray("item");

            weatherInfo.append("날씨 정보:\n"); // Or any header you prefer

            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);
                String category = item.getString("category");
                String obsrValue = item.getString("obsrValue");

                String categoryName = getCategoryName(category); // Helper function (see below)

                weatherInfo.append(categoryName).append(": ").append(obsrValue);

                if(category.equals("VEC")){
                    weatherInfo.append("도");
                } else if (!category.equals("PTY") && !category.equals("RN1") && !category.equals("VEC")){
                    weatherInfo.append(getUnit(category));
                }

                weatherInfo.append("\n");
            }
            return weatherInfo.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "날씨 정보를 파싱하는 중 오류가 발생했습니다."; // Or handle the error as needed
        }
    }

    // Helper function to convert category codes to human-readable names
    private static String getCategoryName(String category) {
        switch (category) {
            case "PTY": return "강수 형태";
            case "REH": return "습도";
            case "RN1": return "1시간 강수량";
            case "T1H": return "기온";
            case "UUU": return "동서바람성분";
            case "VEC": return "풍향";
            case "VVV": return "남북바람성분";
            case "WSD": return "풍속";
            default: return category; // Or handle unknown categories
        }
    }

    private static String getUnit(String category) {
        switch (category) {
            case "REH": return "%";
            case "RN1": return "mm";
            case "T1H": return "℃";
            case "UUU": return "m/s";
            case "VVV": return "m/s";
            case "WSD": return "m/s";
            default: return ""; // Or handle unknown categories
        }
    }
    public static String WeatherAPI(String weatherApiKey) throws IOException, ParseException {
        // Get the current date
        LocalDate today = LocalDate.now();
        System.out.println("Today's date: " + today);

        // Format the date in "yyyyMMdd" format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String date = today.format(formatter);

        // Parse the date back into a Date object using SimpleDateFormat
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        Date todayDate = simpleDateFormat.parse(date);

        // Output the parsed date
        System.out.println("Parsed date: " + todayDate);

        // Add 1 day to the current date using LocalDate's plusDays method
        LocalDate tomorrow = today.plusDays(1);
        String koreaDate = tomorrow.format(formatter);

        // Output the date of tomorrow
        System.out.println("Tomorrow's date: " + koreaDate);
        StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtNcst"); /*URL*/
        urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + weatherApiKey); /*Service Key*/
        urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지번호*/
        urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("1000", "UTF-8")); /*한 페이지 결과 수*/
        urlBuilder.append("&" + URLEncoder.encode("dataType","UTF-8") + "=" + URLEncoder.encode("JSON", "UTF-8")); /*요청자료형식(XML/JSON) Default: XML*/
        urlBuilder.append("&" + URLEncoder.encode("base_date","UTF-8") + "=" + URLEncoder.encode(koreaDate, "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("base_time","UTF-8") + "=" + URLEncoder.encode("0600", "UTF-8")); /*06시 발표(정시단위) */
        urlBuilder.append("&" + URLEncoder.encode("nx","UTF-8") + "=" + URLEncoder.encode("62", "UTF-8")); /*예보지점의 X 좌표값*/
        urlBuilder.append("&" + URLEncoder.encode("ny","UTF-8") + "=" + URLEncoder.encode("125", "UTF-8")); /*예보지점의 Y 좌표값*/
        URL url = new URL(urlBuilder.toString());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");
        System.out.println("Response code: " + conn.getResponseCode());
        BufferedReader rd;
        if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        rd.close();
        conn.disconnect();
        return sb.toString();
    }


}
