package exception.handler;

import jakarta.servlet.http.HttpServletResponse;
import util.logger.CustomLogger;

import java.io.IOException;

public class CustomExceptionHandler {
    private static final CustomLogger logger = new CustomLogger(CustomExceptionHandler.class);

    public static void handleResponseError(HttpServletResponse response, Exception e) {
        logger.severe("Exception occurred: %s".formatted(e.getMessage()));

        String errorJson = """
                {
                "error": "%s"
                }
                """.formatted(e.getMessage());

        try {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(errorJson);
            response.getWriter().flush();
        } catch (IOException ioException) {
            logger.severe("Error while writing response: %s".formatted(ioException.getMessage()));
        }
    }

    public static void handleApiError(Exception e) {
        if (e instanceof IOException) {
            logger.severe("API response parsing failed: %s".formatted(e.getMessage()));
        } else if (e instanceof InterruptedException) {
            logger.severe("API request interrupted: %s".formatted(e.getMessage()));
            Thread.currentThread().interrupt();
        } else {
            logger.severe("Unexpected error occurred: %s".formatted(e.getMessage()));
        }
    }

}
