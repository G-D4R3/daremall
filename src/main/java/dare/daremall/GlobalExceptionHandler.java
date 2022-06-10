package dare.daremall;

import dare.daremall.exception.ErrorResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.nio.file.AccessDeniedException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResult> notFoundException(IllegalStateException e) {

        ErrorResult er = new ErrorResult();
        er.setStatus(HttpStatus.NOT_FOUND.value());
        er.setMessage(e.getMessage());

        return new ResponseEntity<>(er, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResult> accessDeny(AccessDeniedException e) {
        ErrorResult er = new ErrorResult();
        er.setStatus(HttpStatus.FORBIDDEN.value());
        er.setMessage(e.getMessage());
        return new ResponseEntity<>(er, HttpStatus.FORBIDDEN);
    }
}
