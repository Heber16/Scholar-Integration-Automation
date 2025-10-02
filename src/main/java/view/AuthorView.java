package view;

import model.Author;

import javax.swing.*;
import java.awt.*;

public class AuthorView extends JFrame {
    private JTextField authorIdField;
    private JButton searchButton;
    private JTextArea resultArea;

    public AuthorView() {
        setTitle("Google Scholar Author Search");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Top panel with input field and button
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout());

        topPanel.add(new JLabel("Author ID:"));
        authorIdField = new JTextField(20);
        topPanel.add(authorIdField);

        searchButton = new JButton("Search");
        topPanel.add(searchButton);

        add(topPanel, BorderLayout.NORTH);

        // Text area for results
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        add(new JScrollPane(resultArea), BorderLayout.CENTER);
    }

    // Get the author ID typed by the user
    public String getAuthorId() {
        return authorIdField.getText();
    }

    // Display author information in the text area
    public void displayAuthorInfo(Author author) {
        String info = "Name: " + author.getName() + "\n"
                + "Affiliation: " + author.getAffiliation() + "\n"
                + "Citations: " + author.getCitations() + "\n"
                + "Profile URL: " + author.getProfileUrl() + "\n";
        resultArea.setText(info);
    }

    // Display error messages in a popup
    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // Getter for the Search button
    public JButton getSearchButton() {
        return searchButton;
    }
}

