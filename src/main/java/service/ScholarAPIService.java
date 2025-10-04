package service;

import model.Article;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Service to interact with Google Scholar API
 * Handles HTTP requests, parsing, and error recovery
 */
public class ScholarAPIService {

    // IMPORTANT: Get free key at https://serpapi.com/
    private static final String SERPAPI_KEY = "API_KEY";
    private static final String BASE_URL = "https://serpapi.com/search.json";
    private static final int MAX_RETRIES = 3;
    private static final int RETRY_DELAY_MS = 2000;

    /**
     * Searches for articles by author name in Google Scholar
     * Implements retry logic for network failures
     *
     * @param authorName Name of the researcher
     * @param maxResults Maximum number of articles to retrieve
     * @return List of found articles
     */
    public List<Article> searchArticlesByAuthor(String authorName, int maxResults)
            throws Exception {

        List<Article> articles = new ArrayList<>();

        if (authorName == null || authorName.trim().isEmpty()) {
            throw new IllegalArgumentException("Researcher name cannot be empty");
        }

        System.out.println("\n🔍 Searching articles for: " + authorName);

        try {
            // Build search URL with encoded query
            String query = URLEncoder.encode(authorName, StandardCharsets.UTF_8);
            String urlString = String.format(
                    "%s?engine=google_scholar&q=%s&api_key=%s&num=%d",
                    BASE_URL, query, SERPAPI_KEY, maxResults
            );

            // Make request with retry logic
            JSONObject response = makeRequestWithRetry(urlString);

            // Parse results
            if (response.has("organic_results")) {
                JSONArray results = response.getJSONArray("organic_results");

                for (int i = 0; i < Math.min(results.length(), maxResults); i++) {
                    JSONObject item = results.getJSONObject(i);
                    Article article = parseArticle(item, authorName);
                    articles.add(article);

                    System.out.println("  ✓ Article found: " + article.getTitle());

                    // Small pause to avoid saturating API
                    Thread.sleep(500);
                }
            }

            System.out.println("✓ Total articles found: " + articles.size());

        } catch (Exception e) {
            System.err.println("✗ Search error: " + e.getMessage());
            throw new Exception("Error querying Google Scholar API: " + e.getMessage(), e);
        }

        return articles;
    }

    /**
     * Makes HTTP request with automatic retry on failure
     * Implements exponential backoff strategy
     *
     * @param urlString URL to request
     * @return JSON response
     */
    private JSONObject makeRequestWithRetry(String urlString) throws Exception {
        Exception lastException = null;

        // Retry loop
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(10000); // 10 seconds
                conn.setReadTimeout(10000);

                int responseCode = conn.getResponseCode();

                if (responseCode == 200) {
                    // Success - read response
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(conn.getInputStream())
                    );
                    StringBuilder content = new StringBuilder();
                    String line;

                    while ((line = in.readLine()) != null) {
                        content.append(line);
                    }

                    in.close();
                    conn.disconnect();

                    return new JSONObject(content.toString());

                } else if (responseCode == 429) {
                    throw new Exception("API rate limit reached. Wait a moment.");
                } else {
                    throw new Exception("HTTP Error: " + responseCode);
                }

            } catch (Exception e) {
                lastException = e;
                System.err.println("  Attempt " + attempt + " failed: " + e.getMessage());

                // Wait before retry (exponential backoff)
                if (attempt < MAX_RETRIES) {
                    Thread.sleep(RETRY_DELAY_MS * attempt);
                }
            }
        }

        throw new Exception("Failed after " + MAX_RETRIES + " attempts", lastException);
    }

    /**
     * Parses a JSON article object into Article model
     * Extracts relevant fields from API response
     *
     * @param item JSON object from API
     * @param researcherName Name of researcher
     * @return Parsed Article object
     */
    private Article parseArticle(JSONObject item, String researcherName) {
        Article article = new Article();

        article.setTitle(item.optString("title", "No title"));
        article.setResearcherName(researcherName);

        // Authors
        if (item.has("publication_info")) {
            JSONObject pubInfo = item.getJSONObject("publication_info");
            article.setAuthors(pubInfo.optString("authors", ""));
        }

        // Abstract/Snippet
        article.setAbstractText(item.optString("snippet", ""));

        // Link
        article.setLink(item.optString("link", ""));

        // Citations
        if (item.has("inline_links") &&
                item.getJSONObject("inline_links").has("cited_by")) {
            JSONObject citedBy = item.getJSONObject("inline_links").getJSONObject("cited_by");
            article.setCitedBy(citedBy.optInt("total", 0));
        }

        // Publication date (approximate)
        article.setPublicationDate(LocalDate.now());

        // Keywords (extract from snippet if possible)
        String snippet = article.getAbstractText();
        if (snippet.length() > 50) {
            article.setKeywords(snippet.substring(0, 50) + "...");
        }

        return article;
    }

    /**
     * Generates mock data for testing without API
     * Useful for development and demonstrations
     *
     * @param researcherName Name of researcher
     * @param count Number of articles to generate
     * @return List of mock articles
     */
    public List<Article> getMockArticles(String researcherName, int count) {
        List<Article> articles = new ArrayList<>();

        for (int i = 1; i <= count; i++) {
            Article article = new Article(
                    researcherName + " - Test Article #" + i,
                    researcherName + ", Coauthor" + i,
                    LocalDate.now().minusYears(i),
                    "This is a test abstract for article number " + i +
                            ". Contains relevant information about academic research.",
                    "https://scholar.google.com/article" + i,
                    "keyword1, keyword2, keyword3",
                    100 + (i * 10),
                    researcherName
            );
            articles.add(article);
        }

        return articles;
    }
}
