package se.yrgo.libraryapp.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

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

    public void createNewBook(String title, String author, String isbn, int dds) throws SQLException {
        try (Connection conn = ds.getConnection();
                PreparedStatement stmt = conn.prepareStatement(
                        "INSERT INTO book_edition(isbn, title, author, dds_code) VALUES (?, ?, ?, ?)")) {

            stmt.setString(1, isbn);
            stmt.setString(2, title);
            stmt.setString(3, author);
            stmt.setInt(4, dds);

            stmt.executeUpdate();
        }

        try (Connection conn = ds.getConnection();
                PreparedStatement stmt = conn.prepareStatement(
                        "INSERT INTO book(isbn) VALUES (?)")) {

            stmt.setString(1, isbn);

            stmt.executeUpdate();
        }
    }

    public int getBookId(String isbn) throws SQLException {
        try (Connection conn = ds.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(
                        "SELECT book_id FROM book WHERE isbn = '" + isbn + "'")) {

            if (rs.next()) {
                return rs.getInt("book_id");
            }
        }

        throw new AssertionError("Book not found: " + isbn);
    }

    public void createNewLoan(int bookId, int userId, LocalDate returnDate) throws SQLException {
        try (Connection conn = ds.getConnection();
                PreparedStatement stmt = conn.prepareStatement(
                        "INSERT INTO book_loan(book_id, borrower_id, return_date) VALUES (?, ?, ?)")) {

            stmt.setInt(1, bookId);
            stmt.setInt(2, userId);
            stmt.setDate(3, java.sql.Date.valueOf(returnDate));

            stmt.executeUpdate();
        }
    }

    public void clearBooks() throws SQLException {
        try (Connection conn = ds.getConnection();
                Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM book_loan");
            stmt.executeUpdate("DELETE FROM book");
            stmt.executeUpdate("DELETE FROM book_edition");
        }
    }
}