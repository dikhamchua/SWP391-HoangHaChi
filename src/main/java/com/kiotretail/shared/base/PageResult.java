package com.kiotretail.shared.base;

import java.util.Collections;
import java.util.List;

/**
 * Generic page result wrapper.
 *
 * @param <T> item type
 */
public class PageResult<T> {

    private List<T> items;
    private int totalItems;
    private int page;
    private int size;

    public PageResult() {
        this.items = Collections.emptyList();
    }

    public PageResult(List<T> items, int totalItems, int page, int size) {
        this.items = items != null ? items : Collections.emptyList();
        this.totalItems = totalItems;
        this.page = page;
        this.size = size;
    }

    public static <T> PageResult<T> of(List<T> items, int totalItems, Pagination pagination) {
        if (pagination == null) {
            throw new IllegalArgumentException("pagination must not be null");
        }
        return new PageResult<>(items, totalItems, pagination.getPage(), pagination.getSize());
    }

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items != null ? items : Collections.emptyList();
    }

    public int getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getTotalPages() {
        if (size <= 0) {
            return 0;
        }
        return (int) Math.ceil((double) totalItems / size);
    }

    public boolean hasNext() {
        return page < getTotalPages();
    }

    public boolean hasPrevious() {
        return page > 1;
    }
}
