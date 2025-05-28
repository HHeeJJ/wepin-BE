package wepin.store.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.Builder;
import lombok.Getter;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import wepin.store.enumeration.StatusCode;


@Getter
public class CommonResponse<T> {

    // 0000 : 이면 성공
    // 그 외 실패.
    @ApiModelProperty(name = "code", example = "0000")
    @ApiParam(value = "상태코드", required = true)
    private final String code;

    @ApiModelProperty(name = "message", example = "정상처리되었습니다.")
    @ApiParam(value = "메시지", required = true)
    private final String message;

    /* 사용자에게 알림을 해야할 경우 true */
    @ApiModelProperty(name = "alertFlag", example = "false")
    @ApiParam(value = "alert여부")
    private final boolean alertFlag;

    @ApiModelProperty(name = "requestType", example = "false")
    @ApiParam(value = "요청 타입")
    private String requestType;

    @ApiModelProperty(name = "data", example = "Object")
    @ApiParam(value = "dataObject", required = true)
    private final T data;

    @JsonIgnore
    private final StatusCode statusCode;

    @Builder
    public CommonResponse(StatusCode statusCode, String code, String message, boolean alertFlag, String requestType, T data) {
        this.statusCode = ObjectUtils.isNotEmpty(statusCode) ? statusCode : StatusCode.SUCCESS;
        this.code       = StringUtils.isNotEmpty(code) ? code : this.statusCode.getCode();
        this.message    = StringUtils.isNotEmpty(message) ? message : this.statusCode.getMessage();
        this.alertFlag  = alertFlag;
        this.requestType  = requestType;
        this.data       = data;
    }
}
