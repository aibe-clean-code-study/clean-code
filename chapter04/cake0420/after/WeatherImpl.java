import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class WeatherImpl implements Weather {

    /**
     * 주어진 날씨 API 키를 사용하여 날씨 정보를 가져옵니다.
     *
     * @param weatherApiKey 날씨 API를 호출할 때 사용되는 API 키
     * @return 날씨 API에서 반환한 응답 본문
     * @throws IOException 네트워크 문제나 I/O 오류가 발생한 경우
     */
    public String WeatherAPI(String weatherApiKey) throws IOException {

        // 오늘 날짜를 구한 뒤 한국 날짜 형식으로 변환
        LocalDate today = LocalDate.now();
        String koreaDate = getKoreaDate(today);

        // 날씨 API의 URL을 생성
        URL url = new URL(buildWeatherApiUrl(weatherApiKey, koreaDate).toString());
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestMethod("GET");
        httpURLConnection.setRequestProperty("Content-type", "application/json");

        // 서버 응답 코드에 따라 입력 스트림을 다르게 처리
        BufferedReader bufferedReader;
        if (httpURLConnection.getResponseCode() >= 200 && httpURLConnection.getResponseCode() <= 300) {
            // 정상 응답일 경우 InputStreamReader로 데이터 읽기
            bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
        } else {
            // 에러 응답일 경우 ErrorStreamReader로 에러 데이터 읽기
            bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getErrorStream()));
        }

        // 응답 내용을 StringBuilder에 저장
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            sb.append(line);
        }
        bufferedReader.close();
        httpURLConnection.disconnect();

        // 날씨 API의 응답 본문을 반환
        return sb.toString();
    }

    /**
     * 날씨 API의 URL을 생성하는 메소드
     *
     * @param weatherApiKey 날씨 API 키
     * @param koreaDate 한국 날짜
     * @return 날씨 API를 호출할 수 있는 URL
     * @throws UnsupportedEncodingException URL 인코딩에 실패한 경우
     */
    public StringBuilder buildWeatherApiUrl(String weatherApiKey, String koreaDate) throws UnsupportedEncodingException {
        StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtNcst"); /*URL*/
        urlBuilder.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + weatherApiKey); /*서비스 키*/
        urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지 번호*/
        urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode("1000", "UTF-8")); /*한 페이지 결과 수*/
        urlBuilder.append("&" + URLEncoder.encode("dataType", "UTF-8") + "=" + URLEncoder.encode("JSON", "UTF-8")); /*요청 자료 형식 (XML/JSON)*/
        urlBuilder.append("&" + URLEncoder.encode("base_date", "UTF-8") + "=" + URLEncoder.encode(koreaDate, "UTF-8")); /*기준 날짜*/
        urlBuilder.append("&" + URLEncoder.encode("base_time", "UTF-8") + "=" + URLEncoder.encode("0600", "UTF-8")); /*기준 시간 (06시 발표)*/
        urlBuilder.append("&" + URLEncoder.encode("nx", "UTF-8") + "=" + URLEncoder.encode("62", "UTF-8")); /*예보 지점 X 좌표*/
        urlBuilder.append("&" + URLEncoder.encode("ny", "UTF-8") + "=" + URLEncoder.encode("125", "UTF-8")); /*예보 지점 Y 좌표*/

        return urlBuilder;
    }

    /**
     * 오늘 날짜를 기반으로 내일 날짜를 한국 날짜 형식(yyyyMMdd)으로 반환합니다.
     *
     * @param today 오늘 날짜
     * @return 내일 날짜를 "yyyyMMdd" 형식으로 반환
     */
    public String getKoreaDate(LocalDate today) {

        // 날짜 형식 지정
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

        // 내일 날짜 계산
        LocalDate tomorrow = today.plusDays(1);

        // 내일 날짜를 형식에 맞게 반환
        return tomorrow.format(formatter);
    }

}
