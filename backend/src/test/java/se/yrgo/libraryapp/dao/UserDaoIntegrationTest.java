package se.yrgo.libraryapp.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.SQLException;
import java.util.Optional;
import javax.sql.DataSource;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import com.radcortez.flyway.test.annotation.H2;

import se.yrgo.libraryapp.entities.LoginInfo;
import se.yrgo.libraryapp.entities.User;
import se.yrgo.libraryapp.entities.UserId;

@Tag("integration")
@H2
public class UserDaoIntegrationTest {
    private static DataSource ds;
    private static DataBaseTestHelper helper;

    @BeforeAll
    static void initDataSource() {
        // this way we do not need to create a new datasource every time
        final JdbcDataSource ds = new JdbcDataSource();
        ds.setURL("jdbc:h2:mem:test");
        UserDaoIntegrationTest.ds = ds;
        helper = new DataBaseTestHelper(ds);
    }

    @Test
    void getUserById() {
        // this data comes from the test migration files
        final String username = "test";
        final UserId userId = UserId.of(1);
        UserDao userDao = new UserDao(ds);
        Optional<User> maybeUser = userDao.get(Integer.toString(userId.getId()));
        assertThat(maybeUser).isPresent();
        assertThat(maybeUser.get().getName()).isEqualTo(username);
        assertThat(maybeUser.get().getId()).isEqualTo(userId);
    }

    @Test
    void getUserShouldBeWrongId() {
        UserDao userDao = new UserDao(ds);

        Optional<User> maybeUser = userDao.get("100");

        assertThat(maybeUser).isEmpty();
    }

    @Test
    void getLoginInfoFromDatabase() {
        UserDao userDao = new UserDao(ds);

        Optional<LoginInfo> result = userDao.getLoginInfo("test");

        assertThat(result).isPresent();
        assertThat(result.get().getUserId()).isEqualTo(UserId.of(1));
        assertThat(result.get().getPasswordHash()).isNotBlank();
    }

    @Test
    void getLoginInfoWithWrongUsername() {
        UserDao userDao = new UserDao(ds);

        Optional<LoginInfo> result = userDao.getLoginInfo("nonexistentuser");

        assertThat(result).isEmpty();
    }

    @Test
    void getUserReturnsCorrectUsernameAndRealname() throws SQLException {
        helper.createNewUser(
                "bossebredsladd12",
                "Bosse Bredsladd",
                "hashedpassword");

        UserDao userDao = new UserDao(ds);

        int id = helper.getUserId("bossebredsladd12");

        Optional<User> result = userDao.get(String.valueOf(id));

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("bossebredsladd12");
        assertThat(result.get().getRealname()).isEqualTo("Bosse Bredsladd");
    }

    @Test
    void registerNewUser() throws SQLException {
        String username = "bossebredsladd";
        String realname = "Bosse Bredsladd";
        String passwordHash = "hashedpassword";

        UserDao userDao = new UserDao(ds);

        boolean result = userDao.register(username, realname, passwordHash);

        assertThat(result).isTrue();

        int id = helper.getUserId(username);

        Optional<User> maybeUser = userDao.get(String.valueOf(id));
        Optional<LoginInfo> loginInfo = userDao.getLoginInfo(username);

        assertThat(maybeUser).isPresent();
        assertThat(maybeUser.get().getName()).isEqualTo(username);
        assertThat(maybeUser.get().getRealname()).isEqualTo(realname);
        assertThat(loginInfo).isPresent();
        assertThat(loginInfo.get().getPasswordHash())
                .isEqualTo(passwordHash);
    }

    @Test
    void registerUserWithExistingName() throws SQLException {
        String username = "existinguser";

        helper.createNewUser(username,
                "Existing User",
                "hashedpassword");

        UserDao userDao = new UserDao(ds);

        boolean result = userDao.register(username,
                "Another user",
                "anotherpassword");

        assertThat(result).isFalse();
    }

    @Test
    void nameIsAvailable() {
        UserDao userDao = new UserDao(ds);

        assertThat(userDao.isNameAvailable("availableuser")).isTrue();
    }

    @Test
    void nameIsNotAvailable() throws SQLException {
        helper.createNewUser("takenuser",
                "Taken User",
                "passwordhash");

        UserDao userDao = new UserDao(ds);

        assertThat(userDao.isNameAvailable("takenuser")).isFalse();
    }
}