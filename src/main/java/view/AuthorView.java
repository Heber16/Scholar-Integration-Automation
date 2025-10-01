package view;

import model.Author;

public class AuthorView {
    public void printAuthorDetails(Author author) {
        System.out.println("==== Author Information ====");
        System.out.println("Name: " + author.getName());
        System.out.println("Affiliation: " + author.getAffiliation());
        System.out.println("Citations: " + author.getCitations());
        System.out.println("Profile URL: " + author.getProfileUrl());
        System.out.println("============================");
    }
}
