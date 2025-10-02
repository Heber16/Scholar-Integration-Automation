package controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.Author;
import view.AuthorView;

import javax.swing.*;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class AuthorController {
    private final AuthorView view;
    private final String API_KEY = "API_KEY"; // <-- replace with your SerpApi key

    public AuthorController(AuthorView view) {
        this.view = view;

        // Add event listener to the Search button
        this.view.getSearchButton().addActionListener(e -> {
            String authorId = view.getAuthorId().trim();
            if (!authorId.isEmpty()) {
                searchAuthor(authorId);
            } else {
                view.showError("Please enter an author ID.");
            }
        });
    }

    // Perform GET request to SerpApi and fetch author data
    public void searchAuthor(String authorId) {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            private Author author;

            @Override
            protected Void doInBackground() {
                try {
                    // Build the request URL
                    String urlString = "https://serpapi.com/search.json?engine=google_scholar_author&author_id="
                            + authorId + "&api_key=" + API_KEY;

                    URL url = new URL(urlString);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");

                    // Read response
                    Scanner sc = new Scanner(conn.getInputStream());
                    StringBuilder response = new StringBuilder();
                    while (sc.hasNext()) {
                        response.append(sc.nextLine());
                    }
                    sc.close();

                    // Parse JSON response
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode root = mapper.readTree(response.toString());

                    JsonNode authorNode = root.path("author");
                    if (!authorNode.isMissingNode()) {
                        String name = authorNode.path("name").asText();
                        String affiliation = authorNode.path("affiliations").asText();
                        int citations = root.path("cited_by").path("table").get(0).path("citations").asInt(0);
                        String profileUrl = authorNode.path("link").asText();

                        author = new Author(name, affiliation, citations, profileUrl);
                    }
                } catch (IOException ex) {
                    view.showError("Failed to fetch author data: " + ex.getMessage());
                }
                return null;
            }

            @Override
            protected void done() {
                if (author != null) {
                    view.displayAuthorInfo(author);
                } else {
                    view.showError("No author data found.");
                }
            }
        };
        worker.execute();
    }
}


