package org.example;

import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import org.example.model.NaverAPIResultItem;
import org.example.service.NaverAPIService;

/**
 * {@code SearchServlet} 클래스는 클라이언트의 검색 요청을 처리하는 서블릿입니다.
 * <p>
 * - 클라이언트가 전달한 검색어를 네이버 API를 통해 조회하고, 결과를 JSON 형식으로 반환합니다.<br>
 * - 향후 LLM(대규모 언어 모델) 추가 기능이 구현될 예정입니다.
 * </p>
 *
 * <p>이 클래스는 네이버 검색 API를 활용하며, API 이용 약관을 준수해야 합니다.</p>
 *
 */
@WebServlet(name = "search", urlPatterns = "/search")
public class SearchServlet extends HttpServlet {
    NaverAPIService naverAPIService = new NaverAPIService();

    public void init() {
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{
        String query = URLDecoder.decode(request.getParameter("query"), StandardCharsets.UTF_8);
        System.out.println(query);
        String json;
        try {
            List<NaverAPIResultItem> result = naverAPIService.searchByQuery(query);
            ObjectMapper objectMapper = new ObjectMapper();
            json = objectMapper.writeValueAsString(result);
        } catch (InterruptedException e) {
            response.sendError(500);
            json = """
                    {
                    "error": "%s"
                    }
                    """.formatted(e.getMessage());
        }
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();
        out.print(json);
        out.flush();
    }

    public void destroy() {
    }
}