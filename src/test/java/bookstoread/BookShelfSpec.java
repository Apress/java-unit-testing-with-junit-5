package bookstoread;

import org.junit.jupiter.api.*;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import java.time.Duration;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@RunWith(JUnitPlatform.class)
@DisplayName("A bookshelf")
public class BookShelfSpec {

    private BookShelf shelf;
    private Book effectiveJava;
    private Book codeComplete;
    private Book mythicalManMonth;
    private Book cleanCode;

    @BeforeEach
    void init() {
        shelf = new BookShelf();
        effectiveJava = new Book("Effective Java", "Joshua Bloch", LocalDate.of(2008, Month.MAY, 8));
        codeComplete = new Book("Code Complete", "Steve McConnel", LocalDate.of(2004, Month.JUNE, 9));
        mythicalManMonth = new Book("The Mythical Man-Month", "Frederick Phillips Brooks", LocalDate.of(1975, Month.JANUARY, 1));
        cleanCode = new Book("Clean Code", "Robert C. Martin", LocalDate.of(2008, Month.AUGUST, 1));
    }

    //*************** BTR-1 *********************//

    @Nested
    @DisplayName("is empty")
    class IsEmpty {

        @Test
        @DisplayName("when no book is added to it")
        public void emptyBookShelfWhenNoBookAdded() {
            List<Book> books = shelf.books();
            assertTrue(books.isEmpty(), () -> "BookShelf should be empty");
        }

        /*
            Book Notes:
            1. Time for clean up. Adding BeforeEach method
         */
        @Test
        @DisplayName("when add is called without books")
        void emptyBookShelfWhenAddIsCalledWithoutBooks() {
            shelf.add();
            List<Book> books = shelf.books();
            assertTrue(books.isEmpty(), () -> "BookShelf should be empty.");
        }

    }

    @Nested
    @DisplayName("after adding books")
    class BooksAreAdded {

        /*
        Book Notes:
        1. As you write test you will become clear if you want this kind of API or not
        Here rather than just supporting String in add we can use var args as well. That will allow us to
        add multiple books in one go.
        2. You can use non-public methods as test names
         */
        @Test
        @DisplayName("contains two books")
        void bookshelfContainsTwoBooksWhenTwoBooksAdded() {
            shelf.add(effectiveJava, codeComplete);
            List<Book> books = shelf.books();
            assertEquals(2, books.size(), () -> "BookShelf should have two books");
        }

        @Test
        @DisplayName("returns an immutable books collection to client")
        void bookshelfIsImmutableForClient() {
            shelf.add(effectiveJava, codeComplete);
            List<Book> books = shelf.books();
            try {
                books.add(mythicalManMonth);
                fail(() -> "Should not be able to add book to books");
            } catch (Exception e) {
                assertTrue(e instanceof UnsupportedOperationException, () -> "BookShelf should throw UnsupportedOperationException");
            }
        }

    }

    @Test
    void throwsExceptionWhenBooksAreAddedAfterCapacityIsReached() {
        BookShelf bookShelf = new BookShelf(2);
        bookShelf.add(effectiveJava, codeComplete);
        BookShelfCapacityReached throwException = assertThrows(BookShelfCapacityReached.class, () -> bookShelf.add(mythicalManMonth));
        assertEquals("BookShelf capacity of 2 is reached. You can't add more books.", throwException.getMessage());
    }

    @Test
    void test_should_complete_in_one_second() {
//        assertTimeout(Duration.of(1, ChronoUnit.SECONDS), () -> Thread.sleep(2000));
//        String message = assertTimeout(Duration.of(1, ChronoUnit.SECONDS), () -> "Hello, World!");
//        assertEquals("Hello, World!", message);


        assertTimeoutPreemptively(Duration.of(1, ChronoUnit.SECONDS), () -> Thread.sleep(2000));
    }

    @RepeatedTest(value = 10, name = "i_am_a_repeated_test__{currentRepetition}/{totalRepetitions}")
    void i_am_a_repeated_test() {
        assertTrue(true);
    }

    //*************** BTR-2 *********************//

    @Nested
    @DisplayName("is arranged")
    class WhenArranged {

        @Test
        @DisplayName("lexicographically by book title")
        void bookshelfArrangedByBookTitle() {
            shelf.add(effectiveJava, codeComplete, mythicalManMonth);
            List<Book> books = shelf.arrange();
            assertEquals(asList(codeComplete, effectiveJava, mythicalManMonth), books, () -> "Books in a bookshelf should be arranged lexicographically by book title");
        }


        /*
        Book note:
        We started with book as just a String primitive. This makes it easy to start off.
        Now, we realized book will have other properties so we will need to think about Book as a model class.
        Before we move ahead, let's create Book model with only single property
        After refactoring you will get java.lang.ClassCastException: bookstoread.Book cannot be cast to java.lang.Comparable.
        First we will have to implement Comparable interface and then we will have to add equals and hashcode method
         */
        @Test
        @DisplayName("by user provided criteria (by book title lexicographically descending)")
        void bookshelfArrangedByUserProvidedCriteria() {
            shelf.add(effectiveJava, codeComplete, mythicalManMonth);
            List<Book> books = shelf.arrange(Comparator.<Book>naturalOrder().reversed());
            assertEquals(
                    asList(mythicalManMonth, effectiveJava, codeComplete),
                    books,
                    () -> "Books in a bookshelf are arranged in descending order of book title");
        }

        /*
        Book note:
        One thing that reader should note here is that business logic `Comparator.<Book>naturalOrder().reversed()` in the above example is in the test.
         Our API supports client to provide their own criteria. If we in future we discovered that this should be in bookshelf then we can just move it there.
         You discover production code in tests.
         */

        /*
        Book note:
        We will extend the Book model to include few more fields.

         */
        @Test
        @DisplayName("by book publication date in ascending order")
        void bookshelfArrangedByAnotherUserProvidedCriteria() {
            shelf.add(effectiveJava, codeComplete, mythicalManMonth);
            List<Book> books = shelf.arrange((b1, b2) -> b1.getPublishedOn().compareTo(b2.getPublishedOn()));
            assertEquals(
                    asList(mythicalManMonth, codeComplete, effectiveJava),
                    books,
                    () -> "Books in a bookshelf are arranged by book publication date in ascending order");
        }

    }


    @Nested
    @DisplayName("books are grouped by")
    class GroupBy {

        /*
    Book note:
    Exercise: Ask readers to arrange book by author name
     */

    /*
    Book note:
    One common requirement that comes is to group books within bookshelf by criteria.
    For example, group books by their author or publication year.
    Here we will introduce AssertJ assertions
     */

        @Test
        @DisplayName("publication year")
        void groupBooksInBookShelfByPublicationYear() {
            shelf.add(effectiveJava, codeComplete, mythicalManMonth, cleanCode);

            Map<Year, List<Book>> booksByPublicationYear = shelf.groupByPublicationYear();
            assertThat(booksByPublicationYear)
                    .containsKey(Year.of(2008))
                    .containsValues(Arrays.asList(effectiveJava, cleanCode));

            assertThat(booksByPublicationYear)
                    .containsKey(Year.of(2004))
                    .containsValues(singletonList(codeComplete));

            assertThat(booksByPublicationYear)
                    .containsKey(Year.of(1975))
                    .containsValues(singletonList(mythicalManMonth));
        }

        /*
        Book note:
        If you think more we can make our function generic by extracting out grouping function
         */
        @Test
        @DisplayName("user provided criteria(group by author name)")
        void groupBooksInBookShelfByUserProvidedCriteria() {
            shelf.add(effectiveJava, codeComplete, mythicalManMonth, cleanCode);
            Map<String, List<Book>> booksByAuthor = shelf.groupBy(Book::getAuthor);

            assertThat(booksByAuthor)
                    .containsKey("Joshua Bloch")
                    .containsValues(singletonList(effectiveJava));

            assertThat(booksByAuthor)
                    .containsKey("Steve McConnel")
                    .containsValues(singletonList(codeComplete));

            assertThat(booksByAuthor)
                    .containsKey("Frederick Phillips Brooks")
                    .containsValues(singletonList(mythicalManMonth));

            assertThat(booksByAuthor)
                    .containsKey("Robert C. Martin")
                    .containsValues(singletonList(cleanCode));
        }

    }
}