package controller.search;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import exception.handler.CustomExceptionHandler;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.dto.NaverAPIResultItem;
import service.search.SearchService;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet(urlPatterns = {"/search"})
public class SearchController extends HttpServlet {
    private final SearchService searchService;

    public SearchController() {
        this.searchService = (SearchService) getServletContext().getAttribute("searchService");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            responseRestApi(request, response); // 클라이언트의 요청을 처리하고 JSON 응답을 생성하는 메서드 호출
        } catch (Exception e) {
            handleResponseError(response, e); // 예외 처리를 담당하는 메서드를 호출
        }
    }

    /**
     * 클라이언트 요청을 처리하고 검색 결과를 JSON 형식으로 반환합니다.
     * @param request 클라이언트 요청
     * @param response 서버 응답
     * @throws IOException 입출력 오류 발생 시 예외 처리
     */
    private void responseRestApi(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String searchKeyword = request.getParameter("keyword");
        List<NaverAPIResultItem> naverAPIResultItems = searchService.searchByKeyword(searchKeyword);
        String json = createJson(naverAPIResultItems);
        createResponse(response, json);
    }

    /**
     * 검색 결과를 JSON 형식으로 변환합니다.
     * @param naverAPIResultItems 네이버 API 검색 결과 목록
     * @return JSON 형식의 검색 결과 문자열
     * @throws JsonProcessingException JSON 파싱 중 발생한 예외
     */
    private String createJson(List<NaverAPIResultItem> naverAPIResultItems) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(naverAPIResultItems);
    }

    /**
     * 클라이언트에 JSON 형식의 응답을 전송합니다.
     * @param response 클라이언트 응답 객체
     * @param json 클라이언트 응답에 포함시킬 문자열
     * @throws IOException I/O 처리 중 발생한 예외
     */
    private void createResponse(HttpServletResponse response, String json) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try (PrintWriter out = response.getWriter()) {
            out.print(json);
            out.flush();
        }
    }

    /**
     * 예외 발생 시 오류 응답을 처리합니다.
     * @param response 클라이언트 응답 객체
     * @param e 클라이언트 응답에 포함시킬 예외 객체
     */
    private void handleResponseError(HttpServletResponse response, Exception e) {
        // 예외 처리 로직을 별도의 클래스에 위임
        CustomExceptionHandler.handleResponseError(response, e);
    }
}
