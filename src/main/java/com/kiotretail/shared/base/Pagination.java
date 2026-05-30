package com.kiotretail.shared.base;

/**
 * Simple pagination parameters POJO.
 * page is 1-based; size constrained to [1, 100].
 */
public class Pagination {

    private static final int DEFAULT_PAGE = 1;
    private static final int DEFAULT_SIZE = 15;
    private static final int MIN_SIZE = 1;
    private static final int MAX_SIZE = 100;

    private int page;
    private int size;

    public Pagination() {
        this.page = DEFAULT_PAGE;
        this.size = DEFAULT_SIZE;
    }

    public Pagination(int page, int size) {
        setPage(page);
        setSize(size);
    }

    public static Pagination of(int page, int size) {
        return new Pagination(page, size);
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        if (page < 1) {
            throw new IllegalArgumentException("page must be >= 1, got " + page);
        }
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        if (size < MIN_SIZE || size > MAX_SIZE) {
            throw new IllegalArgumentException(
                    "size must be in [" + MIN_SIZE + ", " + MAX_SIZE + "], got " + size);
        }
        this.size = size;
    }

    public int getOffset() {
        return (page - 1) * size;
    }
}
