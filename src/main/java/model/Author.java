package model;

public class Author {
    private String name;
    private String affiliation;
    private int citations;
    private String profileUrl;

    public Author() {}

    public Author(String name, String affiliation, int citations, String profileUrl) {
        this.name = name;
        this.affiliation = affiliation;
        this.citations = citations;
        this.profileUrl = profileUrl;
    }

    // Getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAffiliation() { return affiliation; }
    public void setAffiliation(String affiliation) { this.affiliation = affiliation; }

    public int getCitations() { return citations; }
    public void setCitations(int citations) { this.citations = citations; }

    public String getProfileUrl() { return profileUrl; }
    public void setProfileUrl(String profileUrl) { this.profileUrl = profileUrl; }
}

