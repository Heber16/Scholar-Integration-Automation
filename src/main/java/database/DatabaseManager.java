package database;

import model.Article;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages all database operations (CRUD)
 * Handles connection, table creation, and data storage
 */
public class DatabaseManager {
    // Database configuration - MODIFY ACCORDING TO YOUR SETUP
    private static final String DB_URL = "jdbc:mysql://localhost:3306/scholar_db";
    private static final String DB_USER = "root";
        private static final String DB_PASSWORD = "pass";

    // For SQLite (simpler alternative):
    // private static final String DB_URL = "jdbc:sqlite:scholar_articles.db";

    private Connection connection;

    /**
     * Constructor - Loads database driver
     */
    public DatabaseManager() throws SQLException {
        try {
            // Load driver (optional in newer Java versions)
            Class.forName("com.mysql.cj.jdbc.Driver");
            // For SQLite: Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Database driver not found", e);
        }
    }

    /**
     * Establishes connection to the database
     */
    public void connect() throws SQLException {
        try {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            // For SQLite: connection = DriverManager.getConnection(DB_URL);
            System.out.println("✓ Database connection established");
        } catch (SQLException e) {
            System.err.println("✗ Connection error: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Creates the articles table if it doesn't exist
     * Uses CREATE TABLE IF NOT EXISTS to avoid errors
     */
    public void createSchema() throws SQLException {
        String createTableSQL = """
            CREATE TABLE IF NOT EXISTS articles (
                id INT AUTO_INCREMENT PRIMARY KEY,
                title VARCHAR(500) NOT NULL,
                authors TEXT,
                publication_date DATE,
                abstract TEXT,
                link VARCHAR(500),
                keywords TEXT,
                cited_by INT DEFAULT 0,
                researcher_name VARCHAR(255),
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """;

        // For SQLite change AUTO_INCREMENT to AUTOINCREMENT

        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(createTableSQL);
            System.out.println("✓ Database schema verified/created");
        } catch (SQLException e) {
            System.err.println("✗ Schema creation error: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Inserts an article into the database
     * Uses PreparedStatement to prevent SQL injection
     *
     * @param article Article object to insert
     * @return Generated article ID or -1 if failed
     */
    public int insertArticle(Article article) throws SQLException {
        String insertSQL = """
            INSERT INTO articles 
            (title, authors, publication_date, abstract, link, keywords, cited_by, researcher_name)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement pstmt = connection.prepareStatement(
                insertSQL, Statement.RETURN_GENERATED_KEYS)) {

            // Set parameters using ? placeholders
            pstmt.setString(1, article.getTitle());
            pstmt.setString(2, article.getAuthors());
            pstmt.setDate(3, article.getPublicationDate() != null ?
                    Date.valueOf(article.getPublicationDate()) : null);
            pstmt.setString(4, article.getAbstractText());
            pstmt.setString(5, article.getLink());
            pstmt.setString(6, article.getKeywords());
            pstmt.setInt(7, article.getCitedBy());
            pstmt.setString(8, article.getResearcherName());

            int affectedRows = pstmt.executeUpdate();

            // Retrieve auto-generated ID
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int id = generatedKeys.getInt(1);
                        article.setId(id);
                        System.out.println("  ✓ Article inserted with ID: " + id);
                        return id;
                    }
                }
            }
            return -1;
        } catch (SQLException e) {
            System.err.println("  ✗ Insert error: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Retrieves all articles from the database
     * Converts ResultSet rows into Article objects
     *
     * @return List of articles
     */
    public List<Article> getAllArticles() throws SQLException {
        List<Article> articles = new ArrayList<>();
        String selectSQL = "SELECT * FROM articles ORDER BY created_at DESC";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(selectSQL)) {

            // Iterate through results
            while (rs.next()) {
                Article article = new Article();
                article.setId(rs.getInt("id"));
                article.setTitle(rs.getString("title"));
                article.setAuthors(rs.getString("authors"));

                // Handle null dates
                Date date = rs.getDate("publication_date");
                if (date != null) {
                    article.setPublicationDate(date.toLocalDate());
                }

                article.setAbstractText(rs.getString("abstract"));
                article.setLink(rs.getString("link"));
                article.setKeywords(rs.getString("keywords"));
                article.setCitedBy(rs.getInt("cited_by"));
                article.setResearcherName(rs.getString("researcher_name"));

                articles.add(article);
            }

            System.out.println("✓ Retrieved " + articles.size() + " articles");
            return articles;

        } catch (SQLException e) {
            System.err.println("✗ Retrieval error: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Closes the database connection
     * Always call this when finished
     */
    public void close() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("✓ Connection closed");
            } catch (SQLException e) {
                System.err.println("✗ Close error: " + e.getMessage());
            }
        }
    }

    /**
     * Checks if connection is active
     */
    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}
