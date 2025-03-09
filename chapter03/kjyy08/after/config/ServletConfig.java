package config;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import service.search.SearchPageService;
import service.search.SearchService;
import util.api.APIClient;

@WebListener
public class ServletConfig implements ServletContextListener {

    private final SearchService searchService;
    private final SearchPageService searchPageService;
    private final APIClient apiClient;

    public ServletConfig() {
        searchService = new SearchService();
        searchPageService = new SearchPageService();
        apiClient = new APIClient();
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("Servlet context initialized.");
        registerServicesInContext(sce);
    }

    private void registerServicesInContext(ServletContextEvent sce){
        sce.getServletContext().setAttribute("searchService", searchService);
        sce.getServletContext().setAttribute("searchPageService", searchPageService);
        sce.getServletContext().setAttribute("apiClient", apiClient);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("Servlet context destroyed.");
    }
}
