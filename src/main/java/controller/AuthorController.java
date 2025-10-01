package controller;

import model.Author;
import view.AuthorView;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AuthorController {
    private AuthorView view;
    private final String API_KEY = "API_KEY";

    public AuthorController(AuthorView view) {
        this.view = view;
    }

    public void searchAuthor(String authorId) {
        String url = "https://serpapi.com/search.json?engine=google_scholar_author&author_id=" + authorId + "&api_key=" + API_KEY;

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(response.body());

                JsonNode authorNode = root.path("author");

                String name = authorNode.path("name").asText();
                String affiliation = authorNode.path("affiliations").asText();
                String profileUrl = authorNode.path("website").asText();

                int citations = 0;

                Author author = new Author(name, affiliation, citations, profileUrl);
                view.printAuthorDetails(author);


            } else {
                System.out.println("Error: API returned status " + response.statusCode());
            }

        } catch (Exception e) {
            System.out.println("Error fetching author: " + e.getMessage());
        }
    }
}
