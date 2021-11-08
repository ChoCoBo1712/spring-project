package com.epam.esm.repository.impl;

import java.util.ArrayList;
import java.util.List;

public class ClauseBuilder {

  private static final String NEW_LINE = "\n";

  private final List<String> parts = new ArrayList<>();
  private final String start;
  private final String separator;

  ClauseBuilder(String start, String separator) {
    this.start = start;
    this.separator = separator;
  }

  void addPart(String part) {
    parts.add(part);
  }

  String build() {
    String result = String.join(separator, parts);

    if (!result.isEmpty()) {
      result = start + result + NEW_LINE;
    }
    return result;
  }
}
