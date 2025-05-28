package wepin.store.enumeration;

public enum StatusCode {
    SUCCESS("0000", null),
    BAD_REQUEST("4001", "요청 데이터가 비어있습니다."),
    ERROR("4002", "서버에러 발생"),
    NOT_FOUND("4000", "요청한 내역을 찾을수 없습니다."),
    IS_BALCKLIST("4001", "서비스 이용제한 사용자 입니다."),
    REFRESH_TOKEN_ERROR("E001", "리프레시 토큰 에러"),
    AUTHENTICATION_ERROR("I999", "인증오류");

    String code;

    String message;

    StatusCode(String code, String message) {
        this.code    = code;
        this.message = message;
    }

    public String getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }

    public static boolean isSuccess(StatusCode statusCode) {
        return statusCode.getCode()
                         .equals(StatusCode.SUCCESS.getCode());
    }
}
