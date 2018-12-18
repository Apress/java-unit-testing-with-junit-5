package bookstoread;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.*;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class BookFilterTemplateSpec {

    @BeforeAll
    static void print(){


    }

    @BeforeEach
    void print1(){
    System.out.println("aaaaaa");
    }

    @TestTemplate
    @ExtendWith(BookFilterTestInvocationContextProvider.class)
    void validateFilters(BookFilter filter, Book[] books) {
        assertNotNull(filter);
        assertFalse(filter.apply(books[0]));
        assertTrue(filter.apply(books[1]));
    }
}

class BookFilterTestInvocationContextProvider implements TestTemplateInvocationContextProvider {
    @Override
    public boolean supportsTestTemplate(ExtensionContext context) {
        return true;
    }

    @Override
    public Stream<TestTemplateInvocationContext> provideTestTemplateInvocationContexts(ExtensionContext context) {
        Book cleanCode = new Book("Clean Code", "Robert C. Martin", LocalDate.of(2008, Month.AUGUST, 1));
        Book codeComplete = new Book("Code Complete", "Steve McConnel", LocalDate.of(2004, Month.JUNE, 9));
        return Stream.of(bookFilterTestContext("Before Filter", BookPublishedYearFilter.Before(2007), cleanCode, codeComplete),
                bookFilterTestContext("After Filter", BookPublishedYearFilter.After(2007), codeComplete, cleanCode));
    }

    private TestTemplateInvocationContext bookFilterTestContext(String testName, BookFilter bookFilter, Book... array) {
        return new TestTemplateInvocationContext() {
            @Override
            public String getDisplayName(int invocationIndex) {
                return testName;
            }

            @Override
            public List<Extension> getAdditionalExtensions() {
                return Lists.newArrayList(new TypedParameterResolver(bookFilter), new TypedParameterResolver(array));
            }
        };
    }
}

class TypedParameterResolver<T> implements ParameterResolver {
    T data;

    TypedParameterResolver(T data) {
        this.data = data;
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        Class parameterClass = parameterContext.getParameter().getType();
        return parameterClass.isInstance(data);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return data;
    }
}


