package bookstoread;

public class BookShelfCapacityReached extends RuntimeException {
    public BookShelfCapacityReached(String message) {
        super(message);
    }
}
