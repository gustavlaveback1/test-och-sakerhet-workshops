package se.yrgo.libraryapp.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import se.yrgo.libraryapp.dao.BookDao;
import se.yrgo.libraryapp.entities.BookEdition;
import se.yrgo.libraryapp.entities.DdsClassification;

@ExtendWith(MockitoExtension.class)
public class BookControllerTest {

    @Mock
    BookDao bookDao;

    BookController bookController;

    @BeforeEach
    void setUp() {
        bookController = new BookController(bookDao);
    }

    @Test
    void searchBooksByIsbn() {
        String isbn = "123456789";

        Set<BookEdition> expected = Set.of(
                new BookEdition("Test Book", "Test Author", isbn));

        when(bookDao.find(isbn, null, null)).thenReturn(expected);

        Set<BookEdition> result = bookController.search(isbn, null, null, null);

        assertThat(result).isEqualTo(expected);

        verify(bookDao).find(isbn, null, null);
    }

    @Test
    void searchBooksByTitle() {
        String title = "Test Book";

        Set<BookEdition> expected = Set.of(
                new BookEdition("Test Book", "Test Author", "123456789"));

        when(bookDao.find(null, title, null)).thenReturn(expected);

        Set<BookEdition> result = bookController.search(null, title, null, null);

        assertThat(result).isEqualTo(expected);

        verify(bookDao).find(null, title, null);
    }

    @Test
    void searchBooksByAuthor() {
        String author = "Test Author";

        Set<BookEdition> expected = Set.of(
                new BookEdition("Test Book", author, "123456789"));

        when(bookDao.find(null, null, author)).thenReturn(expected);

        Set<BookEdition> result = bookController.search(null, null, author, null);

        assertThat(result).isEqualTo(expected);

        verify(bookDao).find(null, null, author);
    }

    @Test
    void searchBooksByClassification() {
        String classification = "10";

        DdsClassification expectedClassification = new DdsClassification(10, null);

        Set<BookEdition> expected = Set.of(
                new BookEdition("Test Book", "Test Author", "123456789"));

        when(bookDao.withClass(expectedClassification)).thenReturn(expected);

        Set<BookEdition> result = bookController.search(
                null, null, null, classification);

        assertThat(result).isEqualTo(expected);

        verify(bookDao).withClass(expectedClassification);
    }

    @Test
    void searchWithNoParameters() {
        when(bookDao.find(null, null, null)).thenReturn(Set.of());

        Set<BookEdition> result = bookController.search(
                null, null, null, null);

        assertThat(result).isEmpty();

        verify(bookDao).find(null, null, null);
    }
}
