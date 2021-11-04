package com.chocobo.esm.handler;

import com.chocobo.esm.exception.EntityAlreadyExistsException;
import com.chocobo.esm.exception.EntityNotFoundException;
import com.chocobo.esm.exception.InvalidEntityException;
import com.chocobo.esm.exception.InvalidSortStringException;
import com.chocobo.esm.validator.ValidationError;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.NonNullApi;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.METHOD_NOT_ALLOWED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNSUPPORTED_MEDIA_TYPE;

@RestControllerAdvice
public class ApplicationExceptionHandler extends ResponseEntityExceptionHandler {

  private static final Logger logger = LogManager.getLogger();

  private static final String RESOURCE_NOT_FOUND_MESSAGE = "resource_not_found";
  private static final String ENTITY_NOT_FOUND_MESSAGE = "entity_not_found";
  private static final String INTERNAL_SERVER_ERROR_MESSAGE = "internal_server_error";
  private static final String ENTITY_ALREADY_EXISTS_MESSAGE = "entity_already_exists";
  private static final String INVALID_ENTITY_MESSAGE = "invalid_entity";
  private static final String INVALID_NAME_MESSAGE = "invalid_entity.name";
  private static final String INVALID_DESCRIPTION_MESSAGE = "invalid_entity.description";
  private static final String INVALID_PRICE_MESSAGE = "invalid_entity.price";
  private static final String INVALID_DURATION_MESSAGE = "invalid_entity.duration";
  private static final String METHOD_NOT_ALLOWED_MESSAGE = "method_not_allowed";
  private static final String INVALID_PARAMS_MESSAGE = "invalid_params";
  private static final String INVALID_MEDIA_TYPE_MESSAGE = "invalid_media_type";
  private static final String INVALID_BODY_FORMAT_MESSAGE = "invalid_body_format";
  private static final String INVALID_SORT_PARAMETER_MESSAGE = "invalid_sort_parameter";

  private static final int RESOURCE_NOT_FOUND_CODE = 40401;
  private static final int ENTITY_NOT_FOUND_CODE = 40402;
  private static final int INTERNAL_SERVER_ERROR_CODE = 50001;
  private static final int ENTITY_ALREADY_EXISTS_CODE = 40901;
  private static final int INVALID_SORT_PARAMETER_CODE = 40001;
  private static final int INVALID_ENTITY_CODE = 40002;
  private static final int METHOD_NOT_ALLOWED_CODE = 40501;
  private static final int INVALID_PARAMS_CODE = 40003;
  private static final int INVALID_MEDIA_TYPE_CODE = 41501;
  private static final int INVALID_BODY_FORMAT_CODE = 40004;

  private static final String ERROR_SEPARATOR = ", ";
  private static final String ERROR_MESSAGE = "errorMessage";
  private static final String ERROR_CODE = "errorCode";

  private final ResourceBundleMessageSource messageSource;
  private final ResourceBundleMessageSource codeSource;

  public ApplicationExceptionHandler(ResourceBundleMessageSource messageSource, ResourceBundleMessageSource codeSource) {
    this.messageSource = messageSource;
    this.codeSource = codeSource;
  }

  @Override
  protected ResponseEntity<Object> handleNoHandlerFoundException(
      NoHandlerFoundException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
    logger.error("No handler found for current request: " + request);
    String errorMessage = getErrorMessageFromSource(RESOURCE_NOT_FOUND_MESSAGE);
    return buildErrorResponseEntity(RESOURCE_NOT_FOUND_CODE, errorMessage, status);
  }

  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<Object> handleEntityNotFound(EntityNotFoundException e) {
    logger.error("Requested entity not found: ", e);
    String errorMessagePattern = getErrorMessageFromSource(ENTITY_NOT_FOUND_MESSAGE);
    String errorMessage = String.format(errorMessagePattern, e.getEntityId());
    return buildErrorResponseEntity(ENTITY_NOT_FOUND_CODE, errorMessage, NOT_FOUND);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Object> handleDefault(Exception e) {
    logger.error("Unexpected exception", e);
    String errorMessage = getErrorMessageFromSource(INTERNAL_SERVER_ERROR_MESSAGE);
    return buildErrorResponseEntity(INTERNAL_SERVER_ERROR_CODE, errorMessage, INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(EntityAlreadyExistsException.class)
  public ResponseEntity<Object> handleEntityAlreadyExists() {
    logger.error("Current entity already exists");
    String errorMessage = getErrorMessageFromSource(ENTITY_ALREADY_EXISTS_MESSAGE);
    return buildErrorResponseEntity(ENTITY_ALREADY_EXISTS_CODE, errorMessage, CONFLICT);
  }

  @ExceptionHandler(InvalidSortStringException.class)
  public ResponseEntity<Object> handleInvalidSortString() {
    logger.error("Current sort string is invalid");
    String errorMessage = getErrorMessageFromSource(INVALID_SORT_PARAMETER_MESSAGE);
    return buildErrorResponseEntity(INVALID_SORT_PARAMETER_CODE, errorMessage, BAD_REQUEST);
  }

  @ExceptionHandler(InvalidEntityException.class)
  public ResponseEntity<Object> handleInvalidEntity(InvalidEntityException e) {
    logger.error("Current entity is invalid " + e.getCauseEntity().getSimpleName());
    Iterator<ValidationError> iterator = e.getValidationErrors().iterator();
    StringBuilder errorDetails = new StringBuilder();

    while (iterator.hasNext()) {
      ValidationError error = iterator.next();
      errorDetails.append(switch (error) {
        case INVALID_NAME -> getErrorMessageFromSource(INVALID_NAME_MESSAGE);
        case INVALID_DESCRIPTION -> getErrorMessageFromSource(INVALID_DESCRIPTION_MESSAGE);
        case INVALID_PRICE -> getErrorMessageFromSource(INVALID_PRICE_MESSAGE);
        case INVALID_DURATION -> getErrorMessageFromSource(INVALID_DURATION_MESSAGE);
      });

      if (iterator.hasNext()) {
        errorDetails.append(ERROR_SEPARATOR);
      }
    }

    Class<?> causeEntity = e.getCauseEntity();
    String errorMessage =
            String.format(getErrorMessageFromSource(INVALID_ENTITY_MESSAGE), causeEntity.getSimpleName(), errorDetails);
    return buildErrorResponseEntity(INVALID_ENTITY_CODE, errorMessage, BAD_REQUEST);
  }

  @Override
  public ResponseEntity<Object> handleHttpRequestMethodNotSupported(
          HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
    logger.error("Current request method is not supported: ", ex);
    String errorMessage = getErrorMessageFromSource(METHOD_NOT_ALLOWED_MESSAGE);
    return buildErrorResponseEntity(METHOD_NOT_ALLOWED_CODE, errorMessage, METHOD_NOT_ALLOWED);
  }

  @Override
  protected ResponseEntity<Object> handleBindException(
          BindException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
    logger.error("Current request body binding exception: ", ex);
    String errorMessage = getErrorMessageFromSource(INVALID_PARAMS_MESSAGE);
    return buildErrorResponseEntity(INVALID_PARAMS_CODE, errorMessage, BAD_REQUEST);
  }

  @Override
  protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
          HttpMediaTypeNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
    logger.error("Current media type is not supported: ", ex);
    String errorMessage = getErrorMessageFromSource(INVALID_MEDIA_TYPE_MESSAGE);
    return buildErrorResponseEntity(INVALID_MEDIA_TYPE_CODE, errorMessage, UNSUPPORTED_MEDIA_TYPE);
  }

  @Override
  public ResponseEntity<Object> handleHttpMessageNotReadable(
          HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
    logger.error("Current request message is not readable: ", ex);
    String errorMessage = getErrorMessageFromSource(INVALID_BODY_FORMAT_MESSAGE);
    return buildErrorResponseEntity(INVALID_BODY_FORMAT_CODE, errorMessage, BAD_REQUEST);
  }

  private String getErrorMessageFromSource(String errorMessageName) {
    Locale locale = LocaleContextHolder.getLocale();
    return messageSource.getMessage(errorMessageName, null, locale);
  }

  private ResponseEntity<Object> buildErrorResponseEntity(int errorCode, String errorMessage, HttpStatus status) {
    Map<String, Object> body = new HashMap<>();
    body.put(ERROR_MESSAGE, errorMessage);
    body.put(ERROR_CODE, errorCode);
    return new ResponseEntity<>(body, status);
  }
}
