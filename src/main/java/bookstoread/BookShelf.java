package bookstoread;

import java.time.Year;
import java.util.*;
import java.util.function.Function;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

public class BookShelf {

    private final List<Book> books = new ArrayList<>();
    private final int capacity;

    public BookShelf() {
        this.capacity = Integer.MAX_VALUE;
    }

    public BookShelf(int capacity) {
        this.capacity = capacity;
    }

    public List<Book> books() {
        return Collections.unmodifiableList(books);
    }

    public void add(Book... booksToAdd) throws BookShelfCapacityReached {
        Arrays.stream(booksToAdd).forEach(book -> {
            if (books.size() == capacity) {
                throw new BookShelfCapacityReached(String.format("BookShelf capacity of %d is reached. You can't add more books.", this.capacity));
            }
            books.add(book);
        });
    }

    public List<Book> arrange() {
        return arrange(Comparator.naturalOrder());
    }

    public List<Book> arrange(Comparator<Book> comparator) {
        return books
                .stream()
                .sorted(comparator)
                .collect(toList());
    }

    public Map<Year, List<Book>> groupByPublicationYear() {
        return groupBy(book -> Year.of(book.getPublishedOn().getYear()));
    }

    public <K> Map<K, List<Book>> groupBy(Function<Book, K> fx) {
        return books
                .stream()
                .collect(groupingBy(fx));
    }

    public Progress progress() {
        if (books.isEmpty()) {
            return Progress.notStarted();
        }
        int booksRead = Long.valueOf(books.stream().filter(Book::isRead).count()).intValue();
        int booksInProgress = Long.valueOf(books.stream().filter(Book::isProgress).count()).intValue();
        int booksToRead = books.size() - booksRead - booksInProgress;

        int percentageCompleted = booksRead * 100 / books.size();
        int percentageToRead = booksToRead * 100 / books.size();
        int percentageInProgress = booksInProgress * 100 / books.size();

        return new Progress(percentageCompleted, percentageToRead, percentageInProgress);
    }

    public List<Book> findBooksByTitle(String title) {
        return findBooksByTitle(title, b -> true);
    }

    public List<Book> findBooksByTitle(String title, BookFilter filter) {
        return books.stream()
                .filter(b -> b.getTitle().toLowerCase().contains(title))
                .filter(b -> filter.apply(b))
                .collect(toList());
    }
}
