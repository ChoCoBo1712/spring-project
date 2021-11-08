package com.epam.esm.validator;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class SortStringValidator {

  private static final String SORT_REGEX =
      "(((?i)name(?-i)|(?i)lastUpdateDate(?-i)).((?i)asc(?-i)|(?i)desc(?-i)),?)+";

  public boolean validate(String sort) {
    return sort == null || Pattern.matches(SORT_REGEX, sort);
  }
}
