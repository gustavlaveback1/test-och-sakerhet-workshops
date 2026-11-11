package se.yrgo.libraryapp.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.radcortez.flyway.test.annotation.H2;

import se.yrgo.libraryapp.entities.UserId;
import se.yrgo.libraryapp.entities.Role;

@Tag("integration")
@H2
public class RoleDaoIntegrationTest {

    private static DataSource ds;
    private static DataBaseTestHelper helper;

    @BeforeAll
    static void initDataSource() {
        // this way we do not need to create a new datasource every time
        final JdbcDataSource ds = new JdbcDataSource();
        ds.setURL("jdbc:h2:mem:test");
        RoleDaoIntegrationTest.ds = ds;
        helper = new DataBaseTestHelper(ds);
    }

    @Test
    void getRoleWithWrongIdShouldBeEmpty() {
        RoleDao roleDao = new RoleDao(ds);

        List<Role> result = roleDao.get(UserId.of(100));

        assertThat(result).isEmpty();
    }

    @Test
    void getRoleForUserWithoutRoleShouldBeEmpty() throws SQLException {
        RoleDao roleDao = new RoleDao(ds);

        helper.createNewUser("TestUser", "Test User", "passwordHash");
        int id = helper.getUserId("TestUser");

        List<Role> result = roleDao.get(UserId.of(id));

        assertThat(result).isEmpty();
    }

    @Test
    void getRoleForUserWithOneRole() throws SQLException {
        RoleDao roleDao = new RoleDao(ds);

        int userId = helper.getUserId("test");

        List<Role> result = roleDao.get(UserId.of(userId));

        assertThat(result).containsExactly(Role.ADMIN);
    }

    @Test
    void getRoleForUserWithMultipleRoles() throws SQLException {
        RoleDao roleDao = new RoleDao(ds);

        int userId = helper.getUserId("test2");

        List<Role> result = roleDao.get(UserId.of(userId));

        assertThat(result).containsExactly(Role.ADMIN, Role.USER);
    }

}
