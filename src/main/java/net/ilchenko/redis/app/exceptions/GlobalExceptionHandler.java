package net.ilchenko.redis.app.exceptions;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.springframework.core.annotation.AnnotatedElementUtils.findMergedAnnotation;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private final MessageSource messageSource;

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(final MethodArgumentNotValidException ex,
                                                                  final HttpHeaders headers,
                                                                  final HttpStatus status,
                                                                  final WebRequest request) {
        final StringBuilder message = new StringBuilder();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            message.append(fieldErrorToString(error, request.getLocale()));
        }
        final ApiError error = new ApiError(message.substring(0, message.length() - 1));
        return super.handleExceptionInternal(ex, error, headers, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(ConstraintViolationException ex) {
        final StringBuilder message = new StringBuilder();
        for (ConstraintViolation<?> constraintViolation : ex.getConstraintViolations()) {
            message.append(constraintViolationToString(constraintViolation));
        }
        final ApiError error = new ApiError(message.substring(0, message.length() - 1));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiError> handleAppException(AppException ex, Locale locale) {
        log.error(messageSource.getMessage(ex.getMessageKey(), ex.getArgs(), Locale.getDefault()), ex);
        final String localizedMessage = messageSource.getMessage(ex.getMessageKey(), ex.getArgs(), locale);
        final ApiError error = new ApiError(localizedMessage);
        return ResponseEntity.status(resolveResponseStatus(ex)).body(error);
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ApiError> handleEverything(Throwable th) {
        log.error(th.getMessage(), th);
        final ApiError error = new ApiError(th);
        return ResponseEntity.status(resolveResponseStatus(th)).body(error);
    }

    private HttpStatus resolveResponseStatus(Throwable throwable) {
        final ResponseStatus annotation = findMergedAnnotation(throwable.getClass(), ResponseStatus.class);
        return (annotation != null) ? annotation.value() : HttpStatus.INTERNAL_SERVER_ERROR;
    }

    private String constraintViolationToString(ConstraintViolation<?> cv) {
        return getPropertyName(cv.getPropertyPath()) + " " + cv.getMessage() + "\n";
    }

    private String fieldErrorToString(FieldError error, Locale locale) {
        return error.getField() + " " + messageSource.getMessage(error, locale) + "\n";
    }

    private String getPropertyName(Path propertyPath) {
        String name = "";
        final List<Path.Node> nodes = collect(propertyPath);
        final Path.Node leaf = nodes.get(nodes.size() - 1);
        if (leaf.isInIterable()) {
            final int idx = leaf.getIndex() + 1;
            name += "Line " + idx + ": ";
        }
        return name + leaf.getName();
    }

    private <T> List<T> collect(Iterable<T> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false)
                .collect(Collectors.toList());
    }
}
