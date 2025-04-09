package chapter12.AndBlack99.after;

// MainController 테스트
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MainControllerTest {

    private MainController mainController;
    private BookmarkService mockBookmarkService;
    private Model mockModel;

    @BeforeEach
    public void setUp() {
        mockBookmarkService = mock(BookmarkService.class);
        mockModel = mock(Model.class);
        mainController = new MainController(mockBookmarkService);
    }

    @Test
    public void testShowBookmarks() {
        String viewName = mainController.showBookmarks(mockModel);
        assertEquals("bookmarks", viewName);
        verify(mockBookmarkService).getAllBookmarks();
        verify(mockModel).addAttribute(eq("bookmarks"), any());
    }

    @Test
    public void testAddBookmark() {
        String url = "https://example.com";
        String title = "Example";
        String viewName = mainController.addBookmark(url, title);
        assertEquals("redirect:/bookmarks", viewName);
        verify(mockBookmarkService).addBookmark(any(Bookmark.class));
    }

    @Test
    public void testRemoveBookmark() {
        long id = 1L;
        String viewName = mainController.removeBookmark(id);
        assertEquals("redirect:/bookmarks", viewName);
        verify(mockBookmarkService).removeBookmarkById(id);
    }
}