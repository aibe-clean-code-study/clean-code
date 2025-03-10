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
        renderHtml(req, resp);
    }

    private void renderHtml(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        request.getRequestDispatcher("/news.html").forward(request, response);
    }
}
