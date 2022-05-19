package dare.daremall.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class ErrorResult {

    private int status;
    private String message;

    public ErrorResult() {}

    public ErrorResult(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
