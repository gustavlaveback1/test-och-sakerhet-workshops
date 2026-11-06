package se.yrgo.libraryapp.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

public class DataBaseTestHelper {

    private final DataSource ds;

    public DataBaseTestHelper(DataSource ds) {
        this.ds = ds;
    }

    public void createNewUser(String username, String realname, String passwordHash) throws SQLException {
        try (Connection conn = ds.getConnection();
                PreparedStatement stmt = conn
                        .prepareStatement("INSERT INTO user(user, realname, password_hash) VALUES (?, ?, ?)");) {

            stmt.setString(1, username);
            stmt.setString(2, realname);
            stmt.setString(3, passwordHash);

            stmt.executeUpdate();
        }
    }

    public int getUserId(String username) throws SQLException {
        try (Connection conn = ds.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(
                        "SELECT id FROM user WHERE user = '" + username + "'")) {

            if (rs.next()) {
                return rs.getInt("id");
            }
        }

        throw new AssertionError("User not found: " + username);
    }
}