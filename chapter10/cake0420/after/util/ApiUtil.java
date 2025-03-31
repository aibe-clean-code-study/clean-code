package util;

import java.util.Optional;

public interface ApiUtil{
    Optional<String> getApiResponse(String apiUrl, String method, String body) ;
}
