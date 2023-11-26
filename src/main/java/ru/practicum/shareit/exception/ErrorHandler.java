package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handle(MethodArgumentNotValidException e) {
        log.warn("Получен статус 400 Bad request {}", e.getMessage(), e);
        return Map.of("Ошибка валидации", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handle(ConstraintViolationException e) {
        log.warn("Получен статус 400 Bad request {}", e.getMessage(), e);
        return Map.of("Ошибка валидации", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handle(EntityNotFoundException e) {
        log.warn("Получен статус 404 Not found {}", e.getMessage(), e);
        return Map.of("Объект не найден", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handle(InvalidEmailException e) {
        log.warn("Получен статус 409 Conflict {}", e.getMessage(), e);
        return Map.of("Ошибка валидации", e.getMessage());
    }
}
