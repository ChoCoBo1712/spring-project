package com.epam.esm.validator;

import com.epam.esm.entity.GiftCertificate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static com.epam.esm.validator.ValidationError.INVALID_DESCRIPTION;
import static com.epam.esm.validator.ValidationError.INVALID_DURATION;
import static com.epam.esm.validator.ValidationError.INVALID_NAME;
import static com.epam.esm.validator.ValidationError.INVALID_PRICE;

@Component
public class GiftCertificateValidator {

  private static final String NAME_REGEX = "[a-zA-Z0-9\\s]{1,30}";
  private static final String DESCRIPTION_REGEX = "[a-zA-Z0-9\\s,.]{1,100}";
  private static final int PRICE_MIN_VALUE = 0;
  private static final int DURATION_MIN_VALUE = 0;

  public List<ValidationError> validate(GiftCertificate certificate) {
    List<ValidationError> validationErrors = new ArrayList<>();
    String name = certificate.getName();
    String description = certificate.getDescription();
    BigDecimal price = certificate.getPrice();
    Period duration = certificate.getDuration();

    boolean nameIsValid = (name != null && validateName(name));
    if (!nameIsValid) {
      validationErrors.add(INVALID_NAME);
    }

    boolean descriptionIsValid = (description != null && validateDescription(description));
    if (!descriptionIsValid) {
      validationErrors.add(INVALID_DESCRIPTION);
    }

    boolean priceIsValid = (price != null && validatePrice(price));
    if (!priceIsValid) {
      validationErrors.add(INVALID_PRICE);
    }

    boolean durationIsValid = (duration != null && validateDuration(duration));
    if (!durationIsValid) {
      validationErrors.add(INVALID_DURATION);
    }

    return validationErrors;
  }

  private boolean validateName(String name) {
    return Pattern.matches(NAME_REGEX, name);
  }

  private boolean validateDescription(String description) {
    return Pattern.matches(DESCRIPTION_REGEX, description);
  }

  private boolean validatePrice(BigDecimal price) {
    return price.compareTo(BigDecimal.ZERO) >= PRICE_MIN_VALUE;
  }

  private boolean validateDuration(Period duration) {
    return duration.getDays() > DURATION_MIN_VALUE;
  }
}
