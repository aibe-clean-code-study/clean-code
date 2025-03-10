package exception.handler;

import jakarta.servlet.http.HttpServletResponse;
import util.logger.CustomLogger;

import java.io.IOException;

public class CustomExceptionHandler {
    private static final CustomLogger logger = new CustomLogger(CustomExceptionHandler.class);

    /**
     * 컨트롤러에서 발생한 에러 처리 메서드
     * @param response 응답할 response 객체
     * @param e 에러를 담고 있는 객체
     */
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

    /**
     * API 처리 중 발생한 에러를 로깅합니다.
     */
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
