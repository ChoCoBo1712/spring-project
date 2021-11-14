package com.epam.esm.validator;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.regex.Pattern;

@Component
public class SortArrayValidator {

  private static final String SORT_REGEX =
      "(((?i)name(?-i)|(?i)lastUpdateDate(?-i)).((?i)asc(?-i)|(?i)desc(?-i)))";

  public boolean validate(String[] sort) {
    if (sort == null) {
      return true;
    }

    boolean valid = true;
    for (String param : sort) {
      valid &= Pattern.matches(SORT_REGEX, param);
    }
    return valid;
  }
}
