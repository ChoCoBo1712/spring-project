package com.epam.esm.converter;

import javax.persistence.Converter;
import javax.persistence.AttributeConverter;
import java.time.Period;

@Converter()
public class PeriodConverter implements AttributeConverter<Period, Integer> {

    @Override
    public Integer convertToDatabaseColumn(Period period) {
        return period.getDays();
    }

    @Override
    public Period convertToEntityAttribute(Integer integer) {
        return Period.ofDays(integer);
    }
}
