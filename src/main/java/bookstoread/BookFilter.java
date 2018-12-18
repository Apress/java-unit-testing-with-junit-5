package bookstoread;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public interface BookFilter {
  boolean apply(Book b);
}

class BookPublishedYearFilter implements BookFilter {
  private Function<LocalDate, Boolean> comparison;

  static BookPublishedYearFilter After(int year) {
    final LocalDate date = LocalDate.of(year, 12, 31);
    BookPublishedYearFilter filter = new BookPublishedYearFilter();
    filter.comparison = date::isBefore;
    return filter;
  }

  static BookPublishedYearFilter Before(int year) {
    final LocalDate date = LocalDate.of(year, 1, 1);
    BookPublishedYearFilter filter = new BookPublishedYearFilter();
    filter.comparison = date::isAfter;
    return filter;
  }

  @Override
  public boolean apply(final Book b) {
    return b!=null && comparison.apply(b.getPublishedOn());
  }
}

class CompositeFilter implements BookFilter {
  private List<BookFilter> filters;

  CompositeFilter() {
    filters = new ArrayList<>();
  }

  @Override
  public boolean apply(final Book b) {
    return filters.stream()
      .map(bookFilter -> bookFilter.apply(b))
      .reduce(true, (b1, b2) -> b1 && b2);
  }

  void addFilter(final BookFilter bookFilter) {
    filters.add(bookFilter);
  }
}

