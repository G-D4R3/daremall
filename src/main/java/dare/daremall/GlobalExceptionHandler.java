package dare.daremall;

import dare.daremall.exception.ErrorResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.nio.file.AccessDeniedException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalStateException.class)
    public String notFoundException(IllegalStateException e, Model model) {

        /*ErrorResult er = new ErrorResult();
        er.setStatus(HttpStatus.NOT_FOUND.value());
        er.setMessage(e.getMessage());*/

        model.addAttribute("status", HttpStatus.BAD_REQUEST);
        model.addAttribute("message", HttpStatus.BAD_REQUEST.name());
        return "error/404error";
    }

    @ExceptionHandler(AccessDeniedException.class)
    public String accessDeny(AccessDeniedException e, Model model) {
        /*ErrorResult er = new ErrorResult();
        er.setStatus(HttpStatus.FORBIDDEN.value());
        er.setMessage(e.getMessage());*/

        model.addAttribute("status", HttpStatus.FORBIDDEN);
        model.addAttribute("message", HttpStatus.FORBIDDEN.name());
        return "error/404error";
    }
}
