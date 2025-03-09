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

    private String getRequiredVariable(Dotenv dotenv, String key) {
        return Optional.ofNullable(dotenv.get(key))
                .orElseThrow(() -> new IllegalStateException(key + " is missing in environment variables"));
    }

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

    private void validateKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new IllegalArgumentException("Search keyword cannot be empty");
        }
    }

    private String encodeKeyword(String keyword) {
        return URLEncoder.encode(keyword, StandardCharsets.UTF_8);
    }

    private String buildSearchUrl(String encodedKeyword) {
        return NAVER_SEARCH_API_URL + "?query=" + encodedKeyword;
    }

    private APIClientParam createRequestParameters(String url) {
        return new APIClientParam(
                url,
                "GET",
                null,
                CLIENT_ID_HEADER, clientId,
                CLIENT_SECRET_HEADER, clientSecret
        );
    }

    private String sendApiRequest(APIClientParam params){
        return apiClient.callAPI(params);
    }

    private List<NaverAPIResultItem> parseResponse(String responseBody) throws IOException {
        NaverAPIResult result = objectMapper.readValue(responseBody, NaverAPIResult.class);
        return result.items();
    }

    private void handleApiError(Exception e) {
        CustomExceptionHandler.handleApiError(e);
    }

}