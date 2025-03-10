package service.search;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import exception.handler.CustomExceptionHandler;
import jakarta.servlet.http.HttpServlet;
import model.dto.APIClientParam;
import model.dto.NaverAPIResult;
import model.dto.NaverAPIResultItem;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;
import util.api.APIClient;
import util.logger.CustomLogger;

public class SearchService extends HttpServlet {
    private static final String NAVER_SEARCH_API_URL = "https://openapi.naver.com/v1/search/news.json";
    private static final String CLIENT_ID_KEY = "NAVER_CLIENT_ID";
    private static final String CLIENT_SECRET_KEY = "NAVER_CLIENT_SECRET";
    private static final String CLIENT_ID_HEADER = "X-Naver-Client-Id";
    private static final String CLIENT_SECRET_HEADER = "X-Naver-Client-Secret";

    private final CustomLogger logger;
    private final String clientId;
    private final String clientSecret;
    private final ObjectMapper objectMapper;
    private final APIClient apiClient;

    public SearchService() {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

        this.logger = new CustomLogger(this.getClass());
        this.clientId = getRequiredVariable(dotenv, CLIENT_ID_KEY);
        this.clientSecret = getRequiredVariable(dotenv, CLIENT_SECRET_KEY);
        this.objectMapper = new ObjectMapper();
        this.apiClient = (APIClient) getServletContext().getAttribute("apiClient");

        logger.info("Naver Search API initialized successfully");
    }

    /**
     * 환경 변수 값을 가져옵니다.
     * @param dotenv dotenv 라이브러리 객체
     * @param key 가져올 환경 변수 키
     * @return 가져온 환경 변수 값
     */
    private String getRequiredVariable(Dotenv dotenv, String key) {
        return Optional.ofNullable(dotenv.get(key))
                .orElseThrow(() -> new IllegalStateException(key + " is missing in environment variables"));
    }

    /**
     * 키워드 기반으로 네이버 뉴스 검색을 수행합니다.
     * @param keyword 검색할 키워드
     * @return {@code List<NaverAPIResultItem>} 네이버 API 검색 결과 리스트
     */
    public List<NaverAPIResultItem> searchByKeyword(String keyword) {
        validateKeyword(keyword);
        logger.info("Searching news by keyword: %s".formatted(keyword));

        String encodedKeyword = encodeKeyword(keyword);
        String url = buildSearchUrl(encodedKeyword);
        APIClientParam requestParams = createRequestParameters(url);

        try {
            String responseBody = sendApiRequest(requestParams);
            return parseResponse(responseBody);
        } catch (Exception e) {
            handleApiError(e);
            return Collections.emptyList();
        }
    }

    /**
     * 입력 받은 키워드에 대해 유효성 검사를 수행합니다.
     * @param keyword 입력 받은 키워드
     */
    private void validateKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new IllegalArgumentException("Search keyword cannot be empty");
        }
    }

    /**
     * 입력 받은 키워드에 대해 URL 인코딩을 수행하고 반환합니다.
     * @param keyword 변환할 키워드
     * @return URL 인코딩을 통해 변환된 문자열
     */
    private String encodeKeyword(String keyword) {
        return URLEncoder.encode(keyword, StandardCharsets.UTF_8);
    }

    /**
     * 네이버 API URL에 인코딩된 키워드 값을 파라미터로 포함시켜 반환합니다.
     */
    private String buildSearchUrl(String encodedKeyword) {
        return NAVER_SEARCH_API_URL + "?query=" + encodedKeyword;
    }

    /**
     * API 요청을 위한 파라미터 객체를 생성합니다.
     * @param url 네이버 검색 API 요청 URL
     * @return {@code APIClientParam} API 요청에 필요한 정보를 포함한 객체
     */
    private APIClientParam createRequestParameters(String url) {
        return new APIClientParam(
                url,
                "GET",
                null,
                CLIENT_ID_HEADER, clientId,
                CLIENT_SECRET_HEADER, clientSecret
        );
    }

    /**
     * API 요청을 수행하고 응답을 받아옵니다.
     * @param params API 요청 파라미터 객체
     * @return API 응답 본문
     */
    private String sendApiRequest(APIClientParam params){
        return apiClient.callAPI(params);
    }

    /**
     * API 응답을 JSON에서 리스트 객체로 변환합니다.
     * @param responseBody API 응답 문자열
     * @return 네이버 API 검색 결과 리스트
     */
    private List<NaverAPIResultItem> parseResponse(String responseBody) throws IOException {
        NaverAPIResult result = objectMapper.readValue(responseBody, NaverAPIResult.class);
        return result.items();
    }

    /**
     * API 호출 중 발생한 오류를 처리합니다.
     */
    private void handleApiError(Exception e) {
        CustomExceptionHandler.handleApiError(e);
    }

}