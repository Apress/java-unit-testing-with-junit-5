package bookstoread;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ParameterResolverSpec {

    @BeforeEach
    void initialize(TestInfo info,TestReporter reporter) {
        reporter.publishEntry("Associated tags :", info.getTags().toString());
    }

    @RepeatedTest(value = 10)
    @Tag("Numbers")
    void numberTest(RepetitionInfo info) {
       assertTrue(true);
    }

    @Test
    void nonRepeated(RepetitionInfo info) {
        assertTrue(true);
    }

}
