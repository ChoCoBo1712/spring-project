package com.chocobo.esm.validator;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static com.chocobo.esm.validator.ValidationError.INVALID_NAME;

@Component
public class TagValidator {

  private static final String NAME_REGEX = "[a-zA-Z0-9]{1,30}";

    public List<ValidationError> validate(String tagName) {
        List<ValidationError> validationErrors = new ArrayList<>();
        boolean nameIsValid = tagName != null && Pattern.matches(NAME_REGEX, tagName);

        if (!nameIsValid) {
            validationErrors.add(INVALID_NAME);
        }
        return validationErrors;
    }
}
