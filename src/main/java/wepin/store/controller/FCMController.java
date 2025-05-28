package wepin.store.controller;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import wepin.store.dto.CommonResponse;
import wepin.store.dto.FcmSendDto;
import wepin.store.enumeration.StatusCode;
import wepin.store.service.TokenService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/fcm")
@CrossOrigin
public class FCMController {

    private final TokenService tokenService;

    @ApiOperation(value = "토큰 정보 가져오기", notes = "")
    @GetMapping({"/token-info"})
    public ResponseEntity<CommonResponse> getTokenInfo(
            @ApiParam(name = "memberId", value = "멤버 ID") @RequestParam(name = "memberId") String memberId) {

        if (!StringUtils.isEmpty(memberId)) {

            return ResponseEntity.ok(CommonResponse.builder()
                                                   .statusCode(StatusCode.SUCCESS)
                                                   .message("FCM 토큰 정보가 조회되었습니다.")
                                                   .data(tokenService.getTokenInfo(memberId))
                                                   .build());
        } else {
            return ResponseEntity.ok(CommonResponse.builder()
                                                   .statusCode(StatusCode.NOT_FOUND)
                                                   .build());
        }
    }


    @ApiOperation(value = "토큰 등록", notes = "")
    @PostMapping(value = "/apply-token")
    public ResponseEntity<CommonResponse> applyToken(@RequestBody FcmSendDto.TokenInfoDto dto) {

        if (!ObjectUtils.isEmpty(dto)) {
            try {
                tokenService.applyToken(dto);
            } catch (Exception e) {
                log.error("토큰 등록 실패 : {}", e.getMessage(), e);
            }
            return ResponseEntity.ok(CommonResponse.builder()
                                                   .statusCode(StatusCode.SUCCESS)
                                                   .message("토큰이 등록되었습니다.")
                                                   .build());
        } else {
            return ResponseEntity.ok(CommonResponse.builder()
                                                   .statusCode(StatusCode.NOT_FOUND)
                                                   .build());
        }
    }
}