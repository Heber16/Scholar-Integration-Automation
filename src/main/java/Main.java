import view.AuthorView;
import controller.AuthorController;

public class Main {
    public static void main(String[] args) {
        // Start the GUI in the Event Dispatch Thread (recommended for Swing apps)
        javax.swing.SwingUtilities.invokeLater(() -> {
            AuthorView view = new AuthorView();
            new AuthorController(view);
            view.setVisible(true);
        });
    }
}


