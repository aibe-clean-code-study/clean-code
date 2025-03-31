package org.juyb99.pickmecupspring.repository;

import io.github.cdimascio.dotenv.Dotenv;
import org.juyb99.pickmecupspring.common.util.httpclient.APIClientParam;
import org.juyb99.pickmecupspring.common.util.httpclient.RestAPIRepository;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class StorageRepository extends RestAPIRepository {
    private final String SUPABASE_URL = "https://%s.supabase.co/storage/v1/object/"
            .formatted(Dotenv.configure().ignoreIfMissing().load().get("SUPABASE_PROJECT_URL"));
    private final String SUPABASE_API_KEY = Dotenv.configure().ignoreIfMissing().load().get("SUPABASE_API_KEY");
    private final String SUPABASE_STORAGE_BUCKET = Dotenv.configure().ignoreIfMissing().load().get("SUPABASE_STORAGE_BUCKET");

    public Optional<String> uploadImage(String fileName, byte[] fileContent) {
        Optional<String> apiResponse = requestAPI(APIClientParam.builder()
                .url(SUPABASE_URL + SUPABASE_STORAGE_BUCKET + "/" + fileName)
                .method(HttpMethod.POST)
                .body(fileContent)
                .headers(new String[]{"apikey", SUPABASE_API_KEY, "Authorization", "Bearer %s".formatted(SUPABASE_API_KEY)})
                .build());

        if (apiResponse.isPresent()) {
            String imageUrl = SUPABASE_URL + "public/" + SUPABASE_STORAGE_BUCKET + "/" + fileName;
            return Optional.of(imageUrl);
        } else {
            return Optional.empty();
        }
    }
}
