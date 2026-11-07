package se.yrgo.libraryapp.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;
import java.util.List;

import javax.sql.DataSource;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.radcortez.flyway.test.annotation.H2;

import se.yrgo.libraryapp.entities.Book;
import se.yrgo.libraryapp.entities.BookEdition;
import se.yrgo.libraryapp.entities.BookId;
import se.yrgo.libraryapp.entities.DdsClassification;
import se.yrgo.libraryapp.entities.BookLoan;
import se.yrgo.libraryapp.entities.UserId;

@Tag("integration")
@H2
public class BookDaoIntegrationTest {

    private static DataSource ds;
    private static DataBaseTestHelper helper;

    @BeforeAll
    static void initDataSource() {
        // this way we do not need to create a new datasource every time
        final JdbcDataSource ds = new JdbcDataSource();
        ds.setURL("jdbc:h2:mem:test");
        BookDaoIntegrationTest.ds = ds;
        helper = new DataBaseTestHelper(ds);
    }

    @Test
    void getExistingBook() throws SQLException {
        String isbn = "123456789";
        String title = "Test Book";
        String author = "Test Author";

        helper.createNewBook(title, author, isbn, 10);

        int bookId = helper.getBookId(isbn);

        BookDao bookDao = new BookDao(ds);

        Optional<Book> result = bookDao.get(BookId.of(bookId));

        assertThat(result).isPresent();
        assertThat(result.get().getEdition().getTitle()).isEqualTo(title);
        assertThat(result.get().getEdition().getAuthor()).isEqualTo(author);
    }

    @Test
    void getNonExistingBook() {
        BookDao bookDao = new BookDao(ds);

        Optional<Book> result = bookDao.get(BookId.of(100));

        assertThat(result).isEmpty();
    }

    @Test
    void findBookWithIsbn() throws SQLException {
        String isbn = "123456789";
        String title = "Test Book";
        String author = "Test Author";

        helper.createNewBook(title, author, isbn, 10);

        BookDao bookDao = new BookDao(ds);

        Set<BookEdition> result = bookDao.find(isbn, null, null);

        assertThat(result).hasSize(1);
        assertThat(result.iterator().next().getIsbn()).isEqualTo(isbn);
    }

    @Test
    void findBookWithTitle() throws SQLException {
        String isbn = "123456789";
        String title = "Test Book";
        String author = "Test Author";

        helper.createNewBook(title, author, isbn, 10);

        BookDao bookDao = new BookDao(ds);

        Set<BookEdition> result = bookDao.find(null, title, null);

        assertThat(result).hasSize(1);
        assertThat(result.iterator().next().getTitle()).isEqualTo(title);
    }

    @Test
    void findBookWithAuthor() throws SQLException {
        String isbn = "123456789";
        String title = "Test Book";
        String author = "Test Author";

        helper.createNewBook(title, author, isbn, 10);

        BookDao bookDao = new BookDao(ds);

        Set<BookEdition> result = bookDao.find(null, null, author);

        assertThat(result).hasSize(1);
        assertThat(result.iterator().next().getAuthor()).isEqualTo(author);
    }

    @Test
    void findBookWithEmptySearch() {
        BookDao bookDao = new BookDao(ds);

        Set<BookEdition> result = bookDao.find(null, null, null);

        assertThat(result).isEmpty();
    }

    @Test
    void findNonExistingBook() {
        BookDao bookDao = new BookDao(ds);

        Set<BookEdition> result = bookDao.find("doesnotexist", null, null);

        assertThat(result).isEmpty();
    }

    @Test
    void findBookWithPartialTitle() throws SQLException {
        helper.createNewBook("Programming in Java", "Test Author",
                "123456789", 10);

        BookDao bookDao = new BookDao(ds);

        Set<BookEdition> result = bookDao.find(null, "Java", null);

        assertThat(result).hasSize(1);
    }

    @Test
    void getBooksWithClassification() throws SQLException {
        String isbn = "123456789";
        String title = "Test Book";
        String author = "Test Author";

        helper.createNewBook(title, author, isbn, 10);

        BookDao bookDao = new BookDao(ds);

        DdsClassification classification = new DdsClassification(10, "Test Classification");

        Set<BookEdition> result = bookDao.withClass(classification);

        assertThat(result).hasSize(1);
        assertThat(result.iterator().next().getIsbn()).isEqualTo(isbn);
    }

    @Test
    void getLoansForUserWithLoans() throws SQLException {
        String username = "testuser";
        String isbn = "123456789";

        helper.createNewUser(username, "Test User", "passwordhash");

        int userId = helper.getUserId(username);

        helper.createNewBook("Test Book", "Test Author", isbn, 10);

        int bookId = helper.getBookId(isbn);

        helper.createNewLoan(
                bookId,
                userId,
                LocalDate.of(2026, 11, 7));

        BookDao bookDao = new BookDao(ds);

        List<BookLoan> result = bookDao.loans(UserId.of(userId));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getBook().getId())
                .isEqualTo(BookId.of(bookId));
    }

    @Test
    void getLoansForUserWithoutLoans() throws SQLException {
        String username = "testuser";

        helper.createNewUser(username, "Test User", "passwordhash");

        int userId = helper.getUserId(username);

        BookDao bookDao = new BookDao(ds);

        List<BookLoan> result = bookDao.loans(UserId.of(userId));

        assertThat(result).isEmpty();
    }

    @Test
    void getOverdueLoans() throws SQLException {
        String username = "testuser";
        String isbn = "123456";

        helper.createNewUser(username, "Test User", "passwordhash");

        int userId = helper.getUserId(username);

        helper.createNewBook("Test Book", "Test Author", isbn, 10);

        int bookId = helper.getBookId(isbn);

        helper.createNewLoan(
                bookId,
                userId,
                LocalDate.of(2026, 11, 06));

        BookDao bookDao = new BookDao(ds);

        List<BookLoan> result = bookDao.overdueLoans();

        /*
         * Test database has other books that are overdue, so to make sure the book we
         * add in this test
         * is actually usable I will make to look specifically for it, making it less
         * volatile
         */
        assertThat(result)
                .extracting(loan -> loan.getBook().getId())
                .contains(BookId.of(bookId));
    }

    @Test
    void returnExistingBook() throws SQLException {
        String username = "testuser";
        String isbn = "123456789";

        helper.createNewUser(username, "Test User", "passwordhash");

        int userId = helper.getUserId(username);

        helper.createNewBook("Test Book", "Test Author", isbn, 10);

        int bookId = helper.getBookId(isbn);

        helper.createNewLoan(
                bookId,
                userId,
                LocalDate.of(2026, 12, 15));

        BookDao bookDao = new BookDao(ds);

        boolean result = bookDao.returnBook(BookId.of(bookId));

        assertThat(result).isTrue();
    }

    @Test
    void returnNonExistingLoan() {
        BookDao bookDao = new BookDao(ds);

        boolean result = bookDao.returnBook(BookId.of(9999));

        assertThat(result).isFalse();
    }
}
