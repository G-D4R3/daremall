package dare.daremall;

import dare.daremall.exception.NotEnoughStockException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.nio.file.AccessDeniedException;
import java.util.NoSuchElementException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalStateException.class)
    public String illegalStateException(IllegalStateException e, Model model) {

        model.addAttribute("status", HttpStatus.BAD_REQUEST);
        model.addAttribute("message", e.getMessage());
        return "error/error";
    }

    @ExceptionHandler(AccessDeniedException.class)
    public String accessDeniedException(AccessDeniedException e, Model model) {

        model.addAttribute("status", HttpStatus.FORBIDDEN);
        model.addAttribute("message", HttpStatus.FORBIDDEN.name());
        return "error/404error";
    }

    @ExceptionHandler(NoSuchElementException.class)
    public String noSuchElementException(NoSuchElementException e, Model model) {

        model.addAttribute("status", HttpStatus.BAD_REQUEST);
        model.addAttribute("message", e.getMessage());
        return "error/error";
    }

    @ExceptionHandler(NotEnoughStockException.class)
    public String notEnoughStockException(NotEnoughStockException e, Model model) {
        model.addAttribute("status", HttpStatus.INTERNAL_SERVER_ERROR);
        model.addAttribute("message", e.getMessage());
        return "error/error";
    }
}
