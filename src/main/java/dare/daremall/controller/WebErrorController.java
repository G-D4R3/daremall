package dare.daremall.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

@Controller
public class WebErrorController implements ErrorController {

    @GetMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {

        model.addAttribute("status", request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE));
        model.addAttribute("message", request.getAttribute(RequestDispatcher.ERROR_MESSAGE));
        return "error/error";
    }
}
