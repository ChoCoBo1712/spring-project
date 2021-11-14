package com.epam.esm.validator;

import org.springframework.stereotype.Component;

@Component
public class PaginationValidator {

    private static final int MIN_PAGE_NUMBER = 1;
    private static final int MIN_PAGE_SIZE = 1;
    private static final int MAX_PAGE_SIZE = 100;

    public boolean validate(int page, int size) {
        boolean valid = page >= MIN_PAGE_NUMBER;

        if (size < MIN_PAGE_SIZE || size > MAX_PAGE_SIZE) {
            valid = false;
        }

        return valid;
    }
}
