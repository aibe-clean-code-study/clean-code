package org.juyb99.pickmecupjsp.repository;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.Getter;
import org.juyb99.pickmecupjsp.common.repository.Repository;
import org.juyb99.pickmecupjsp.dto.httpclient.APIClientParam;
import org.juyb99.pickmecupjsp.dto.httpclient.HttpMethod;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

public class StorageRepository extends Repository {
    private final String SUPABASE_URL = "https://%s.supabase.co/storage/v1/object/"
            .formatted(Dotenv.configure().ignoreIfMissing().load().get("SUPABASE_PROJECT_URL"));
    private final String SUPABASE_API_KEY = Dotenv.configure().ignoreIfMissing().load().get("SUPABASE_API_KEY");
    private final String SUPABASE_STORAGE_BUCKET = Dotenv.configure().ignoreIfMissing().load().get("SUPABASE_STORAGE_BUCKET");
    private static final Logger logger = Logger.getLogger(StorageRepository.class.getName());

    private StorageRepository() {
    }

    @Getter
    private final static StorageRepository instance = new StorageRepository();

    public static void main(String[] args) throws IOException {
        StorageRepository storageRepository = StorageRepository.instance;

        Path imagePath = Path.of("C:/Users/kjyy0/Downloads/01.36047379.1 (1).jpg"); // 실제 이미지 경로로 수정하세요.
        byte[] imageBytes = storageRepository.readFileBytes(imagePath);
        String uuidFileName = storageRepository.generateUniqueFileName(imagePath);
        String publicUrl = storageRepository.uploadImage(uuidFileName, imageBytes)
                .orElseThrow(() -> new RuntimeException("Could not upload image"));

        logger.info("Image uploaded: " + publicUrl);
    }

    // 파일 내용을 바이트 배열로 읽어옵니다.
    private byte[] readFileBytes(Path filePath) throws IOException {
        return Files.readAllBytes(filePath);
    }

    // 원본 파일의 확장자를 유지하며 UUID를 이용해 고유 파일명을 생성합니다.
    private String generateUniqueFileName(Path filePath) {
        String originalName = filePath.getFileName().toString();
        String extension = originalName.contains(".")
                ? originalName.substring(originalName.lastIndexOf('.'))
                : "";
        return UUID.randomUUID() + extension;
    }

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
