package bookstoread;

import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.migrationsupport.rules.EnableRuleMigrationSupport;
import org.junit.rules.ExpectedException;

import java.time.LocalDate;
import java.time.Month;

@EnableRuleMigrationSupport
@ExtendWith(LoggingTestExecutionExceptionHandler.class)
public class BookShelfSpecWithRules {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    Book effectiveJava = new Book("Effective Java", "Joshua Bloch", LocalDate.of(2008, Month.MAY, 8));
    Book codeComplete = new Book("Code Complete", "Steve McConnel", LocalDate.of(2004, Month.JUNE, 9));

    @Test
    void throwsExceptionWhenBooksAreAddedAfterCapacityIsReached() {
        BookShelf bookShelf = new BookShelf(1);
        expectedException.expect(BookShelfCapacityReached.class);
        expectedException.expectMessage("BookShelf capacity of 1 is reached. You can't add more books.");

        bookShelf.add(effectiveJava, codeComplete);
    }
}
