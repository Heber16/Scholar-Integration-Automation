import database.DatabaseManager;
import model.Article;
import service.ScholarAPIService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Main graphical user interface
 * Provides interactive controls for searching and viewing articles
 */
public class Main extends JFrame {

    private DatabaseManager dbManager;
    private ScholarAPIService apiService;

    // GUI Components
    private JTextField txtResearcher1;
    private JTextField txtResearcher2;
    private JButton btnSearch;
    private JButton btnViewDB;
    private JButton btnClear;
    private JTable tableArticles;
    private DefaultTableModel tableModel;
    private JTextArea txtLog;
    private JProgressBar progressBar;

    /**
     * Constructor - Initializes GUI and services
     */
    public Main() {
        initializeComponents();
        initializeServices();
    }

    /**
     * Sets up all GUI components and layout
     */
    private void initializeComponents() {
        setTitle("Google Scholar Integration System");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top panel - Search
        JPanel searchPanel = createSearchPanel();
        mainPanel.add(searchPanel, BorderLayout.NORTH);

        // Center panel - Table
        JPanel tablePanel = createTablePanel();
        mainPanel.add(tablePanel, BorderLayout.CENTER);

        // Bottom panel - Log
        JPanel logPanel = createLogPanel();
        mainPanel.add(logPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    /**
     * Creates search input panel with text fields and buttons
     */
    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Researcher Search"));

        panel.add(new JLabel("Researcher 1:"));
        txtResearcher1 = new JTextField("Albert Einstein");
        panel.add(txtResearcher1);

        panel.add(new JLabel("Researcher 2:"));
        txtResearcher2 = new JTextField("Marie Curie");
        panel.add(txtResearcher2);

        btnSearch = new JButton("🔍 Search and Save Articles");
        btnSearch.addActionListener(e -> searchAndSaveArticles());
        panel.add(btnSearch);

        btnViewDB = new JButton("📊 View Database");
        btnViewDB.addActionListener(e -> loadArticlesFromDB());
        panel.add(btnViewDB);

        btnClear = new JButton("🗑️ Clear Table");
        btnClear.addActionListener(e -> clearTable());
        panel.add(btnClear);

        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        panel.add(progressBar);

        return panel;
    }

    /**
     * Creates table panel to display articles
     */
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Articles"));

        String[] columns = {"ID", "Title", "Authors", "Date", "Citations", "Researcher"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };

        tableArticles = new JTable(tableModel);
        tableArticles.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        tableArticles.getColumnModel().getColumn(1).setPreferredWidth(300);

        JScrollPane scrollPane = new JScrollPane(tableArticles);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Creates log panel to show activity messages
     */
    private JPanel createLogPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Activity Log"));

        txtLog = new JTextArea(8, 50);
        txtLog.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(txtLog);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Initializes database and API services
     * Called once at startup
     */
    private void initializeServices() {
        try {
            log("Initializing services...");

            dbManager = new DatabaseManager();
            dbManager.connect();
            dbManager.createSchema();

            apiService = new ScholarAPIService();

            log("✓ Services initialized successfully");

        } catch (SQLException e) {
            logError("Database initialization error: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                    "Error connecting to database.\nCheck configuration.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Main method - Searches and saves articles for both researchers
     * Runs in background thread to avoid freezing GUI
     */
    private void searchAndSaveArticles() {
        String researcher1 = txtResearcher1.getText().trim();
        String researcher2 = txtResearcher2.getText().trim();

        if (researcher1.isEmpty() || researcher2.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter both researcher names",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Execute in separate thread to avoid blocking GUI
        SwingWorker<Void, String> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                btnSearch.setEnabled(false);
                progressBar.setValue(0);

                try {
                    // Researcher 1
                    publish("🔍 Searching articles for " + researcher1 + "...");
                    progressBar.setValue(10);

                    List<Article> articles1 = apiService.searchArticlesByAuthor(researcher1, 3);
                    // For testing without API: apiService.getMockArticles(researcher1, 3);

                    progressBar.setValue(30);

                    for (Article article : articles1) {
                        dbManager.insertArticle(article);
                        Thread.sleep(200);
                    }

                    publish("✓ Saved " + articles1.size() + " articles for " + researcher1);
                    progressBar.setValue(50);

                    // Researcher 2
                    publish("🔍 Searching articles for " + researcher2 + "...");
                    progressBar.setValue(60);

                    List<Article> articles2 = apiService.searchArticlesByAuthor(researcher2, 3);
                    // For testing without API: apiService.getMockArticles(researcher2, 3);

                    progressBar.setValue(80);

                    for (Article article : articles2) {
                        dbManager.insertArticle(article);
                        Thread.sleep(200);
                    }

                    publish("✓ Saved " + articles2.size() + " articles for " + researcher2);
                    progressBar.setValue(100);

                    publish("✅ Process completed successfully");

                } catch (Exception e) {
                    publish("❌ Error: " + e.getMessage());
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void process(List<String> chunks) {
                // Update log in real-time
                for (String message : chunks) {
                    log(message);
                }
            }

            @Override
            protected void done() {
                // Re-enable button and refresh table
                btnSearch.setEnabled(true);
                loadArticlesFromDB();
                JOptionPane.showMessageDialog(Main.this,
                        "Process completed. Check log for details.",
                        "Information", JOptionPane.INFORMATION_MESSAGE);
            }
        };

        worker.execute();
    }

    /**
     * Loads all articles from database and displays in table
     */
    private void loadArticlesFromDB() {
        try {
            log("Loading articles from database...");
            clearTable();

            List<Article> articles = dbManager.getAllArticles();

            for (Article article : articles) {
                Object[] row = {
                        article.getId(),
                        article.getTitle(),
                        article.getAuthors(),
                        article.getPublicationDate(),
                        article.getCitedBy(),
                        article.getResearcherName()
                };
                tableModel.addRow(row);
            }

            log("✓ Loaded " + articles.size() + " articles");

        } catch (SQLException e) {
            logError("Load error: " + e.getMessage());
        }
    }

    /**
     * Clears table view (doesn't delete from database)
     */
    private void clearTable() {
        tableModel.setRowCount(0);
    }

    /**
     * Adds message to log area
     */
    private void log(String message) {
        txtLog.append(message + "\n");
        txtLog.setCaretPosition(txtLog.getDocument().getLength());
    }

    /**
     * Adds error message to log
     */
    private void logError(String message) {
        log("❌ " + message);
    }

    /**
     * Cleanup when closing application
     */
    @Override
    public void dispose() {
        if (dbManager != null) {
            dbManager.close();
        }
        super.dispose();
    }

    /**
     * Main entry point - Launches the application
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Use system look and feel for native appearance
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            Main gui = new Main();
            gui.setVisible(true);
        });
    }
}