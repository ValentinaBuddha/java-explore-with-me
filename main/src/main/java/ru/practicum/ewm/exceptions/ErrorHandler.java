package ru.practicum.ewm.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.PrintWriter;
import java.io.StringWriter;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleException(Exception exception) {
        log.error("Error", exception);
        StringWriter out = new StringWriter();
        exception.printStackTrace(new PrintWriter(out));
        String stackTrace = out.toString();
        return new ApiError(exception.getMessage(), stackTrace)
    }
}
