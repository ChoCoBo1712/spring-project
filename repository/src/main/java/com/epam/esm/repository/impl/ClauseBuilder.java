package com.epam.esm.repository.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ClauseBuilder {

  private static final String EMPTY_STRING = "";
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
    if (parts.isEmpty()) {
      return EMPTY_STRING;
    }

    StringBuilder clause = new StringBuilder(start);
    Iterator<String> iterator = parts.iterator();

    while (true) {
      String condition = iterator.next();
      clause.append(condition);

      if (iterator.hasNext()) {
        clause.append(separator);
      } else {
        clause.append(NEW_LINE);
        break;
      }
    }

    return clause.toString();
  }
}
