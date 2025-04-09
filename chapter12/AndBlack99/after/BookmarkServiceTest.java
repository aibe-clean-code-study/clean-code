package chapter12.AndBlack99.after;

//BookmarkService 테스트
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BookmarkServiceTest {

    private BookmarkService bookmarkService;

    @BeforeEach
    public void setUp() {
        bookmarkService = new BookmarkService();
    }

    @Test
    public void testAddBookmark() {
        Bookmark bookmark = new Bookmark("https://example.com", "Example");
        bookmarkService.addBookmark(bookmark);
        assertTrue(bookmarkService.getAllBookmarks().contains(bookmark));
    }

    @Test
    public void testRemoveBookmark() {
        Bookmark bookmark = new Bookmark("https://example.com", "Example");
        bookmarkService.addBookmark(bookmark);
        bookmarkService.removeBookmark(bookmark);
        assertFalse(bookmarkService.getAllBookmarks().contains(bookmark));
    }

    @Test
    public void testGetAllBookmarks() {
        Bookmark bookmark1 = new Bookmark("https://example1.com", "Example1");
        Bookmark bookmark2 = new Bookmark("https://example2.com", "Example2");
        bookmarkService.addBookmark(bookmark1);
        bookmarkService.addBookmark(bookmark2);
        assertEquals(2, bookmarkService.getAllBookmarks().size());
    }
}