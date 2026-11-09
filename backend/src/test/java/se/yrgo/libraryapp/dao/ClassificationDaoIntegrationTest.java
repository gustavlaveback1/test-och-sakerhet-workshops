package se.yrgo.libraryapp.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.radcortez.flyway.test.annotation.H2;

import se.yrgo.libraryapp.entities.DdsClassification;

@Tag("integration")
@H2
public class ClassificationDaoIntegrationTest {

    private static DataSource ds;
    private static DataBaseTestHelper helper;

    @BeforeAll
    static void initDataSource() {
        // this way we do not need to create a new datasource every time
        final JdbcDataSource ds = new JdbcDataSource();
        ds.setURL("jdbc:h2:mem:test");
        ClassificationDaoIntegrationTest.ds = ds;
        helper = new DataBaseTestHelper(ds);
    }

    @Test
    void getAvailableReturnsEmptyListWhenNoBooksExist() {
        ClassificationDao classificationDao = new ClassificationDao(ds);
        List<DdsClassification> result = classificationDao.getAvailable();

        assertTrue(result.isEmpty());
    }

    @Test
    void getAvailableReturnsListWithMultipleBooks() throws SQLException {
        ClassificationDao classificationDao = new ClassificationDao(ds);
        helper.createNewBook("Test Book", "Test Author", "123456789", 10);
        helper.createNewBook("Test Book 2", "Test Author 2", "123456788", 10);
        helper.createNewBook("Test Book 3", "Test Author 3", "123456787", 0);

        List<DdsClassification> result = classificationDao.getAvailable();

        List<DdsClassification> expected = List.of(
                new DdsClassification(0, "Computer science, information and general works"),
                new DdsClassification(10, "Bibliography"));

        assertThat(result).containsAll(expected);
    }

    @Test
    void getAvailableDoesNotReturnClassificationsWithoutBooks() throws SQLException {
        // Removes all testdata to isolate the test and make it less volatile.
        helper.clearBooks();

        ClassificationDao classificationDao = new ClassificationDao(ds);

        helper.createNewBook("Book 1", "Author 1", "11111111", 10);

        List<DdsClassification> result = classificationDao.getAvailable();

        assertThat(result)
                .extracting(DdsClassification::getCode)
                .containsExactly(10);
    }

    @Test
    void getAvailableDoesNotReturnDuplicateBooks() throws SQLException {
        helper.clearBooks();

        ClassificationDao classificationDao = new ClassificationDao(ds);

        helper.createNewBook("Test Book 1", "Test Author 1", "123456789", 10);
        helper.createNewBook("Test Book 2", "Test Author 2", "222345567", 10);
        helper.createNewBook("Test Book 3", "Test Author 3", "333125686", 10);

        List<DdsClassification> result = classificationDao.getAvailable();

        assertThat(result)
                .extracting(DdsClassification::getCode)
                .containsExactly(10);
    }
}
