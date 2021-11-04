package com.chocobo.esm.dto.serialization;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.Period;

public class PeriodDeserializer extends JsonDeserializer<Period> {

  @Override
  public Period deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
    return Period.ofDays(p.getIntValue());
  }
}
