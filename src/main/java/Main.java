import controller.AuthorController;
import view.AuthorView;

public class Main {
    public static void main(String[] args) {
        AuthorView view = new AuthorView();
        AuthorController controller = new AuthorController(view);

        controller.searchAuthor("AUTHOR_ID");
    }
}
