package model;

import java.time.LocalDate;

/**
 * Model class representing an academic article
 * Contains all the information about a research paper
 */
public class Article {
    private int id;
    private String title;
    private String authors;
    private LocalDate publicationDate;
    private String abstractText;
    private String link;
    private String keywords;
    private int citedBy;
    private String researcherName;

    // Empty constructor
    public Article() {}

    // Full constructor
    public Article(String title, String authors, LocalDate publicationDate,
                   String abstractText, String link, String keywords,
                   int citedBy, String researcherName) {
        this.title = title;
        this.authors = authors;
        this.publicationDate = publicationDate;
        this.abstractText = abstractText;
        this.link = link;
        this.keywords = keywords;
        this.citedBy = citedBy;
        this.researcherName = researcherName;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthors() { return authors; }
    public void setAuthors(String authors) { this.authors = authors; }

    public LocalDate getPublicationDate() { return publicationDate; }
    public void setPublicationDate(LocalDate publicationDate) {
        this.publicationDate = publicationDate;
    }

    public String getAbstractText() { return abstractText; }
    public void setAbstractText(String abstractText) {
        this.abstractText = abstractText;
    }

    public String getLink() { return link; }
    public void setLink(String link) { this.link = link; }

    public String getKeywords() { return keywords; }
    public void setKeywords(String keywords) { this.keywords = keywords; }

    public int getCitedBy() { return citedBy; }
    public void setCitedBy(int citedBy) { this.citedBy = citedBy; }

    public String getResearcherName() { return researcherName; }
    public void setResearcherName(String researcherName) {
        this.researcherName = researcherName;
    }

    @Override
    public String toString() {
        return "Article{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", authors='" + authors + '\'' +
                ", citedBy=" + citedBy +
                '}';
    }
}