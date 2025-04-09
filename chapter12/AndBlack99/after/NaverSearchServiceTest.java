package chapter12.AndBlack99.after;

//NaverSearchService 테스트

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class NaverSearchServiceTest {

    private NaverSearchService naverSearchService;
    private NaverSearchAPI mockNaverSearchAPI;

    @BeforeEach
    public void setUp() {
        mockNaverSearchAPI = mock(NaverSearchAPI.class);
        naverSearchService = new NaverSearchService(mockNaverSearchAPI);
    }

    @Test
    public void testSearch() {
        String keyword = "example";
        NaverSearchResult mockResult = new NaverSearchResult();
        when(mockNaverSearchAPI.search(keyword)).thenReturn(mockResult);

        NaverSearchResult result = naverSearchService.search(keyword);
        assertEquals(mockResult, result);
        verify(mockNaverSearchAPI).search(keyword);
    }
}
