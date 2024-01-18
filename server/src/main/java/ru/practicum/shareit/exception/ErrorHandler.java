package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {
    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handle(RuntimeException e) {
        log.warn("Получен статус 400 Bad request {}", e.getMessage(), e);
        return new ErrorResponse("Ошибка валидации", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handle(UnavailableItemException e) {
        log.warn("Получен статус 400 Bad request {}", e.getMessage(), e);
        return new ErrorResponse("Ошибка бронирования", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handle(EntityNotFoundException e) {
        log.warn("Получен статус 404 Not found {}", e.getMessage(), e);
        return new ErrorResponse("Объект не найден", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handle(StatusException e) {
        log.warn("Получен статус 400 Bad request {}", e.getMessage(), e);
        return new ErrorResponse("Unknown state: UNSUPPORTED_STATUS", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handle(Throwable e) {
        log.warn("Получен статус 500 Internal ServerError {}", e.getMessage(), e);
        return new ErrorResponse("Внутренняя ошибка сервера", e.getMessage());
    }
}
