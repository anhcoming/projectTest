package com.viettel.hstd.exception;


import com.viettel.hstd.core.config.Message;
import com.viettel.hstd.core.dto.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionsHandle extends ResponseEntityExceptionHandler {

    @Autowired
    Message message;

    @ExceptionHandler(Exception.class)
    public BaseResponse<?> handleAllExceptions(Exception ex, WebRequest request) {
        List<String> details = new ArrayList<>();
        String stacktrace = ExceptionUtils.getStackTrace(ex);
        log.error(stacktrace);
        details.add(ex.getLocalizedMessage());
        return new BaseResponse.ResponseBuilder<>().failed(details, message.getMessage("message.error"));
    }

    @ExceptionHandler(com.viettel.hstd.exception.BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResponse<?> handleBadRequest(Exception ex, WebRequest request) {

        List<String> details = new ArrayList<>();
        String stacktrace = ExceptionUtils.getStackTrace(ex);
        log.error(stacktrace);
        details.add(ex.getLocalizedMessage());
        return new BaseResponse.ResponseBuilder<>().failed(details, details.size() > 0 ? details.get(0) : message.getMessage("message.bad_request"));
    }

    @ExceptionHandler(com.viettel.hstd.exception.NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public BaseResponse<?> handleNotFound(Exception ex, WebRequest request) {

        List<String> details = new ArrayList<>();
        String stacktrace = ExceptionUtils.getStackTrace(ex);
        log.error(stacktrace);
        details.add(ex.getLocalizedMessage());
        return new BaseResponse.ResponseBuilder<>().failed(details, details.size() > 0 ? details.get(0) : message.getMessage("message.not_found"));
    }

    @ExceptionHandler(com.viettel.hstd.exception.AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public BaseResponse<?> handleAccessDenied(Exception ex, WebRequest request) {

        List<String> details = new ArrayList<>();
        String stacktrace = ExceptionUtils.getStackTrace(ex);
        log.error(stacktrace);
        details.add(ex.getLocalizedMessage());
        return new BaseResponse.ResponseBuilder<>().failed(details, message.getMessage("message.access_denied"));
    }

    @ExceptionHandler(com.viettel.hstd.exception.UnauthorizedAccessException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public BaseResponse<?> handleUnauthorized(com.viettel.hstd.exception.UnauthorizedAccessException ex, WebRequest request) {

        List<String> details = new ArrayList<>();
        String stacktrace = ExceptionUtils.getStackTrace(ex);
        log.error(stacktrace);
        details.add(ex.getLocalizedMessage());
        return new BaseResponse.ResponseBuilder<>().failed(details, message.getMessage("message.unauthorized"));
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public BaseResponse<?> handleUnauthorized(BadCredentialsException ex, WebRequest request) {

        List<String> details = new ArrayList<>();
        String stacktrace = ExceptionUtils.getStackTrace(ex);
        log.error(stacktrace);
        details.add(ex.getLocalizedMessage());
        return new BaseResponse.ResponseBuilder<>().failed(details, message.getMessage("message.unauthorized"));
    }

    @Override
    protected ResponseEntity handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        List<String> details = new ArrayList<>();
        String stacktrace = ExceptionUtils.getStackTrace(ex);
        log.error(stacktrace);
        for (ObjectError error : ex.getBindingResult().getAllErrors()) {
            details.add(error.getDefaultMessage());
        }
        return ResponseEntity.of(Optional.ofNullable(new BaseResponse.ResponseBuilder<>().failed(details, message.getMessage("message.invalid"))));
    }
}

