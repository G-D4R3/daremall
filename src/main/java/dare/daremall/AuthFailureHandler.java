package dare.daremall;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
public class AuthFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

        String msg = "";
        if(request.getParameter("loginId").length()==0) {
            msg = "아이디를 입력해주세요";
        }
        else if(request.getParameter("password").length()==0) {
            msg = "비밀번호를 입력해주세요";
        }
        else if (exception instanceof AuthenticationServiceException) {
            msg = "존재하지 않는 사용자입니다.";

        }
        else if(exception instanceof BadCredentialsException) {
            msg = "아이디 또는 비밀번호가 틀립니다.";
        }

        request.setAttribute("errorMessage", msg);
        // 로그인 페이지로 다시 포워딩
        request.getRequestDispatcher("/members/login?error=true").forward(request, response);
    }
}
