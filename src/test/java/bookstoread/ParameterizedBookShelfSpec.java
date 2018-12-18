package bookstoread;

import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ParameterizedBookShelfSpec {

    @ParameterizedTest
    @ValueSource(strings = {"Effective Java", "Code Complete", "Clean Code"})
    void shouldGiveBackBooksForTitle(String title) {
        BookShelf shelf = new BookShelf();
        Book effectiveJava = new Book("Effective Java", "Joshua Bloch", LocalDate.of(2008, Month.MAY, 8));
        Book codeComplete = new Book("Code Complete", "Steve McConnel", LocalDate.of(2004, Month.JUNE, 9));
        Book mythicalManMonth = new Book("The Mythical Man-Month", "Frederick Phillips Brooks", LocalDate.of(1975, Month.JANUARY, 1));
        Book cleanCode = new Book("Clean Code", "Robert C. Martin", LocalDate.of(2008, Month.AUGUST, 1));
        shelf.add(effectiveJava, codeComplete, mythicalManMonth, cleanCode);
        List<Book> foundBooks = shelf.findBooksByTitle(title.toLowerCase());
        assertNotNull(foundBooks);
        assertEquals(1,foundBooks.size());
        foundBooks = shelf.findBooksByTitle(title.toUpperCase());
        assertNotNull(foundBooks);
        assertEquals(0,foundBooks.size());
    }

    @ParameterizedTest
    @MethodSource("bookFilterProvider")
    void validateFilterWithNullData(BookFilter filter) {
        assertThat(filter.apply(null)).isFalse();
    }

    @ParameterizedTest
    @ArgumentsSource(BookFilterCompositeArgsProvider.class)
    void validateBookFilterWithBooks(BookFilter filter, Book[] books) {
        assertNotNull(filter);
        assertFalse(filter.apply(books[0]));
        assertTrue(filter.apply(books[1]));
    }

    @DisplayName("Filter validates a passing book")
    @ParameterizedTest(name = "{index} : Validating {1}")
    @ArgumentsSource(BeforeYearArgsProvider.class)
    @ArgumentsSource(AfterYearArgsProvider.class)
    void validateBookFilterWithBook(BookFilter filter, Book book) {
        assertNotNull(filter);
        assertTrue(filter.apply(book));
    }

    static Stream<BookFilter> bookFilterProvider() {
        return Stream.of(BookPublishedYearFilter.Before(2007), BookPublishedYearFilter.After(2007));
    }
}

class BookFilterCompositeArgsProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        Book cleanCode = new Book("Clean Code", "Robert C. Martin", LocalDate.of(2008, Month.AUGUST, 1));
        Book codeComplete = new Book("Code Complete", "Steve McConnel", LocalDate.of(2004, Month.JUNE, 9));
        return Stream.of(Arguments.of(BookPublishedYearFilter.Before(2007), Arrays.array(cleanCode, codeComplete)),
                Arguments.of(BookPublishedYearFilter.After(2007), Arrays.array(codeComplete, cleanCode)));

    }
}
class BeforeYearArgsProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        Book cleanCode = new Book("Clean Code", "Robert C. Martin", LocalDate.of(2006, Month.AUGUST, 1));
        Book codeComplete = new Book("Code Complete", "Steve McConnel", LocalDate.of(2004, Month.JUNE, 9));
        return Stream.of(Arguments.of(BookPublishedYearFilter.Before(2007), cleanCode),
                Arguments.of(BookPublishedYearFilter.Before(2007), codeComplete));

    }
}
class AfterYearArgsProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        Book cleanCode = new Book("Clean Code", "Robert C. Martin", LocalDate.of(2009, Month.AUGUST, 1));
        Book codeComplete = new Book("Code Complete", "Steve McConnel", LocalDate.of(2008, Month.JUNE, 9));
        return Stream.of(Arguments.of(BookPublishedYearFilter.After(2007), cleanCode),
                Arguments.of(BookPublishedYearFilter.After(2007), codeComplete));

    }
}