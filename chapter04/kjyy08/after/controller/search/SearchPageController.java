package controller.search;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.search.SearchPageService;

import java.io.IOException;

@WebServlet(urlPatterns = {"/pages/search/naver"})
public class SearchPageController extends HttpServlet {
    private final SearchPageService searchPageService;

    public SearchPageController() {
        this.searchPageService = (SearchPageService) getServletContext().getAttribute("searchPageService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        renderHtml(req, resp); // GET 요청 시 HTML 페이지를 응답으로 전송
    }

    /**
     * news.html 페이지를 클라이언트에게 전송합니다.
     * @param request 서블릿 요청 객체
     * @param response 서블릿 응답 객체
     */
    private void renderHtml(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        request.getRequestDispatcher("/news.html").forward(request, response);
    }
}
