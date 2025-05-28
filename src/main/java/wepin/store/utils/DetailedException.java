package wepin.store.utils;


public class DetailedException extends Exception {
    private final ErrorCode errorCode;
    private final String    detailMessage;

    // 에러 코드 enum 정의
    public enum ErrorCode {
        MEMBER_NOT_FOUND,
        FEED_SAVE_FAILED,
        IMAGE_UPLOAD_FAILED,
        MISSING_IMAGE_INFO,
        UNKNOWN_ERROR
    }

    // 생성자
    public DetailedException(ErrorCode errorCode, String detailMessage) {
        super(detailMessage);
        this.errorCode     = errorCode;
        this.detailMessage = detailMessage;
    }

    public DetailedException(ErrorCode errorCode, String detailMessage, Throwable cause) {
        super(detailMessage, cause);
        this.errorCode     = errorCode;
        this.detailMessage = detailMessage;
    }

    // Getter 메서드
    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public String getDetailMessage() {
        return detailMessage;
    }

    // 에러 메시지를 반환하는 유틸리티 메서드
    @Override
    public String getMessage() {
        return String.format("[%s] %s", errorCode, detailMessage);
    }
}
