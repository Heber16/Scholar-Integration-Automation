package model;

public class Author {
    private String name;
    private String affiliation;
    private int citations;
    private String profileUrl;

    public Author() {} // Empty constructor for JSON mapping

    public Author(String name, String affiliation, int citations, String profileUrl) {
        this.name = name;
        this.affiliation = affiliation;
        this.citations = citations;
        this.profileUrl = profileUrl;
    }

    public String getName() { return name; }
    public String getAffiliation() { return affiliation; }
    public int getCitations() { return citations; }
    public String getProfileUrl() { return profileUrl; }

    public void setName(String name) { this.name = name; }
    public void setAffiliation(String affiliation) { this.affiliation = affiliation; }
    public void setCitations(int citations) { this.citations = citations; }
    public void setProfileUrl(String profileUrl) { this.profileUrl = profileUrl; }
}
