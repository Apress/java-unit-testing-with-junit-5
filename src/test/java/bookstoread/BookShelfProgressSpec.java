package bookstoread;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;

import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.Collection;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

/**
 * Inject books in tests using a new provider.
 */
@RunWith(JUnitPlatform.class)
@DisplayName("progress")
@ExtendWith(BooksProvider.class)
class BookShelfProgressSpec {

    private BookShelf shelf;

    /**
     * We can inject test metadata using TestInfo object. this is a replacement for TestName rule in Junit4
     * Similar to this there is a TestReporter which can be used to write back junit report.
     * Both these objects do no require any extension and are inject by the Junit engine. for any other type use Extension.
     *
     */
    @BeforeEach
    void setup(Book[] books, TestInfo info) {
        System.out.println(" Test info with display Name as  "+ info.getDisplayName());
        shelf = new BookShelf();
        shelf.add(books);
    }

    /**
     * Using assumptions to make sure  that we have the same instance of book
     * Now that the instances are same so the tests will start working
     */
    @Test
    @DisplayName("is 40% completed and 60% to-read when two books read and 3 books not read yet")
    void progressWithCompletedAndToReadPercentages(Book[] books) {
        Assumptions.assumeTrue(shelf.books().stream().filter(b -> b == books[0]).count() == 1);
        Assumptions.assumeTrue(shelf.books().stream().filter(b -> b == books[1]).count() == 1);

        books[0].startedReadingOn(LocalDate.of(2016, Month.JULY, 1));
        books[0].finishedReadingOn(LocalDate.of(2016, Month.JULY, 31));

        books[1].startedReadingOn(LocalDate.of(2016, Month.AUGUST, 1));
        books[1].finishedReadingOn(LocalDate.of(2016, Month.AUGUST, 31));

        Progress progress = shelf.progress();

        assertThat(progress.completed()).isEqualTo(40);
        assertThat(progress.toRead()).isEqualTo(60);
    }

    @Test
    @DisplayName("is 0% completed and 100% to-read when no book is read yet")
    void progress100PercentUnread() {
        Progress progress = shelf.progress();
        assertThat(progress.completed()).isEqualTo(0);
        assertThat(progress.toRead()).isEqualTo(100);
    }

    /**
     * Using assumptions to make sure  that we have the same instance of book
     * Now that the instances are same so the tests will start working
     */
    @Test
    @DisplayName("is 40% completed, 20% in-progress, and 40% to-read when 2 books read, 1 book in progress, and 2 books unread")
    void reportProgressOfCurrentlyReadingBooks(Book[] books) {
        Assumptions.assumeTrue(shelf.books().stream().filter(b -> b == books[0]).count() == 1);
        Assumptions.assumeTrue(shelf.books().stream().filter(b -> b == books[1]).count() == 1);
        Assumptions.assumeTrue(shelf.books().stream().filter(b -> b == books[2]).count() == 1);

        books[0].startedReadingOn(LocalDate.of(2016, Month.JULY, 1));
        books[0].finishedReadingOn(LocalDate.of(2016, Month.JULY, 31));

        books[1].startedReadingOn(LocalDate.of(2016, Month.AUGUST, 1));
        books[1].finishedReadingOn(LocalDate.of(2016, Month.AUGUST, 31));

        books[2].startedReadingOn(LocalDate.of(2016, Month.SEPTEMBER, 1));

        Progress progress = shelf.progress();

        assertThat(progress.completed()).isEqualTo(40);
        assertThat(progress.inProgress()).isEqualTo(20);
        assertThat(progress.toRead()).isEqualTo(40);
    }

}

class BooksProvider implements ParameterResolver {

    @Override
    public boolean supportsParameter(final ParameterContext parameterContext, final ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().equals(Book[].class);
    }

    /**
     * The method will create a new array every time. This is not a desired behaviour
     * in a test case  we have asked books multiple times  i.e dbefore each and  the test method  and both arrays will be different.
     * This cause test case failures
     *
     * FIX : the execution context can be used to create a store which can hold values to be used multiple times in the test case.
     * The change injects same books array when asked multiple times in a test execution
     */
    @Override
    public Object resolveParameter(final ParameterContext parameterContext, final ExtensionContext extensionContext) throws ParameterResolutionException {
        ExtensionContext.Store books = extensionContext.getStore(ExtensionContext.Namespace.create(Book.class));
        return books.getOrComputeIfAbsent("booksList", key -> getBooks());
    }

    Book[] getBooks(){
        Book effectiveJava = new Book("Effective Java", "Joshua Bloch", LocalDate.of(2008, Month.MAY, 8));
        Book codeComplete = new Book("Code Complete", "Steve McConnel", LocalDate.of(2004, Month.JUNE, 9));
        Book mythicalManMonth = new Book("The Mythical Man-Month", "Frederick Phillips Brooks", LocalDate.of(1975, Month.JANUARY, 1));
        Book cleanCode = new Book("Clean Code", "Robert C. Martin", LocalDate.of(2008, Month.AUGUST, 1));
        Book refactoring = new Book("Refactoring: Improving the Design of Existing Code", "Martin Fowler", LocalDate.of(2002, Month.MARCH, 9));
        return  new Book[]{effectiveJava, codeComplete, mythicalManMonth, cleanCode, refactoring};
    }
}
