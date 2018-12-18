package bookstoread;


import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.assertj.core.api.BooleanArrayAssert;
import org.junit.jupiter.api.*;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

interface FilterBoundaryTests {
    BookFilter get();

    @Test
    @DisplayName("should not fail for null book.")
    default void nullTest() {
        assertThat(get().apply(null)).isFalse();
    }

}
@RunWith(JUnitPlatform.class)
@Tag("Filter")
@DisplayName("Filter based on")
class BookFilterSpec {
    private Book cleanCode;
    private Book codeComplete;

    @BeforeEach
    void init() {
        cleanCode = new Book("Clean Code", "Robert C. Martin", LocalDate.of(2008, Month.AUGUST, 1));
        codeComplete = new Book("Code Complete", "Steve McConnel", LocalDate.of(2004, Month.JUNE, 9));
    }

    @Nested
    @DisplayName("book published date post specified year")
    class BookPublishedAfterFilterSpec implements FilterBoundaryTests {
        BookFilter filter;

        @BeforeEach
        void init() {
            filter = BookPublishedYearFilter.After(2007);
        }

        @Override
        public BookFilter get() {
            return filter;
        }

        @Test
        @DisplayName("should give matching book")
        void validateBookPublishedDatePostAskedYear() {
            assertTrue(filter.apply(cleanCode));
            assertFalse(filter.apply(codeComplete));
        }
    }

    @Nested
    @DisplayName("book published date pre specified year")
    class BookPublishedBeforeFilterSpec implements FilterBoundaryTests {

        BookFilter filter;

        @BeforeEach
        void init() {
            filter = BookPublishedYearFilter.Before(2007);
        }

        @Override
        public BookFilter get() {
            return filter;
        }

        @Test
        @DisplayName("should give matching book")
        void validateBookPublishedDatePreAskedYear() {
            assertFalse(filter.apply(cleanCode));
            assertTrue(filter.apply(codeComplete));
        }
    }

    /**
     * can we really say that we have called all the filters here ?
     * Enters Mocking now !
     */
    @Test
    @DisplayName("Composite criteria invokes multiple filters")
    void shouldFilterOnMultiplesCriteria() {
        BookFilter mockedFilter = Mockito.mock(BookFilter.class);
        Mockito.when(mockedFilter.apply(cleanCode)).thenReturn(true);
        CompositeFilter compositeFilter = new CompositeFilter();
        compositeFilter.addFilter(mockedFilter);
        compositeFilter.apply(cleanCode);
        Mockito.verify(mockedFilter).apply(cleanCode);
    }

    @Test
    @DisplayName("Composite criteria  invokes all incase of failure")
    void shouldInvokeAllInFailure() {
        CompositeFilter compositeFilter = new CompositeFilter();

        BookFilter invokedMockedFilter = Mockito.mock(BookFilter.class);
        Mockito.when(invokedMockedFilter.apply(cleanCode)).thenReturn(false);
        compositeFilter.addFilter(invokedMockedFilter);

        BookFilter secondInvokedMockedFilter = Mockito.mock(BookFilter.class);
        Mockito.when(secondInvokedMockedFilter.apply(cleanCode)).thenReturn(true);
        compositeFilter.addFilter(secondInvokedMockedFilter);

        assertFalse(compositeFilter.apply(cleanCode));
        Mockito.verify(invokedMockedFilter).apply(cleanCode);
        Mockito.verify(secondInvokedMockedFilter).apply(cleanCode);
    }

    @Test
    @DisplayName("Composite criteria invokes all filters")
    void shouldInvokeAllFilters() {
        CompositeFilter compositeFilter = new CompositeFilter();
        BookFilter firstInvokedMockedFilter = Mockito.mock(BookFilter.class);
        Mockito.when(firstInvokedMockedFilter.apply(cleanCode)).thenReturn(true);
        compositeFilter.addFilter(firstInvokedMockedFilter);

        BookFilter secondInvokedMockedFilter = Mockito.mock(BookFilter.class);
        Mockito.when(secondInvokedMockedFilter.apply(cleanCode)).thenReturn(true);
        compositeFilter.addFilter(secondInvokedMockedFilter);
        assertTrue(compositeFilter.apply(cleanCode));
        Mockito.verify(firstInvokedMockedFilter).apply(cleanCode);
        Mockito.verify(secondInvokedMockedFilter).apply(cleanCode);
    }

    class MockedFilter implements BookFilter {
        boolean returnValue;
        boolean invoked;

        MockedFilter(boolean returnValue) {
            this.returnValue = returnValue;
        }

        @Override
        public boolean apply(Book b) {
            invoked = true;
            return returnValue;
        }
    }

    @TestFactory
    Collection<DynamicTest> dynamicTestsFromCollection() {
        BookFilter filter= null;
        return Arrays.asList(
                dynamicTest("1st dynamic test", () ->{
                    assertTrue(filter.apply(cleanCode));
                    assertFalse(filter.apply(codeComplete));
                }),
                dynamicTest("2nd dynamic test", () ->{
                    assertFalse(filter.apply(cleanCode));
                    assertTrue(filter.apply(codeComplete));
                })
        );

    }

}
