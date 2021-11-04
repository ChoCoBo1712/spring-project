package com.chocobo.esm.mapper;

import java.beans.PropertyEditorSupport;
import java.time.Period;

public class PeriodEditor extends PropertyEditorSupport {

  @Override
  public Period getValue() {
    return (Period) super.getValue();
  }

  @Override
  public void setValue(final Object value) {
    super.setValue(Period.ofDays((Integer) value));
  }

  @Override
  public String getAsText() {
    return getValue().toString();
  }

  @Override
  public void setAsText(final String text) throws IllegalArgumentException {
    setValue(Period.ofDays(Integer.parseInt(text)));
  }
}
