package dare.daremall.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

public enum ErrorCode {

    DUPLICATED_MEMBER(400, "사용할 수 없는 아이디입니다."),
    NO_MORE_JOIN(400, "같은 이름, 휴대폰 번호 조합으로 회원가입할 수 있는 횟수는 3번입니다.");

    private int errorCode;
    private String errorMessage;

    public String getErrorMessage() {
        return errorMessage;
    }

    public int getErrorCode() {
        return errorCode;
    }

    ErrorCode(int errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}