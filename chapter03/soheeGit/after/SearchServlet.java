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

@WebServlet(name = "search", urlPatterns = "/search")
public class SearchServlet extends HttpServlet {
    NaverAPIService naverAPIService = new NaverAPIService();
    // 서블릿이 처음 로드될 때 한 번만 실행되는 초기화 메서드
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

        // JSON 응답 전송
        PrintWriter out = response.getWriter();
        out.print(json);
        out.flush();
    }

    public void destroy() {
    }
}