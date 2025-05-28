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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import wepin.store.dto.CommonResponse;
import wepin.store.dto.PushDto;
import wepin.store.enumeration.StatusCode;
import wepin.store.service.PushService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/push")
@CrossOrigin
public class PushController {

    private final PushService pushService;

    @ApiOperation(value = "알람 상태 변경", notes = "알람 상태 변경")
    @PostMapping({"/update"})
    public ResponseEntity<CommonResponse> updatePushState(@RequestBody PushDto dto) throws Exception {
        if (!ObjectUtils.isEmpty(dto)) {
            try {
                return ResponseEntity.ok(CommonResponse.builder()
                                                       .statusCode(StatusCode.SUCCESS)
                                                       .message("상태 변경")
                                                       .data(pushService.update(dto))
                                                       .build());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return ResponseEntity.ok(CommonResponse.builder()
                                                       .message(e.getMessage())
                                                       .statusCode(StatusCode.NOT_FOUND)
                                                       .build());
            }
        } else {
            return ResponseEntity.ok(CommonResponse.builder()
                                                   .statusCode(StatusCode.NOT_FOUND)
                                                   .build());
        }
    }


    @ApiOperation(value = "알람 상태 리스트", notes = "알람 상태 리스트 조회")
    @GetMapping({"/list"})
    public ResponseEntity<CommonResponse> getPushState(@RequestParam String memberId) {
        if (!StringUtils.isEmpty(memberId)) {
            try {
                return ResponseEntity.ok(CommonResponse.builder()
                                                       .statusCode(StatusCode.SUCCESS)
                                                       .message("알람 상태 조회")
                                                       .data(pushService.getPushState(memberId))
                                                       .build());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return ResponseEntity.ok(CommonResponse.builder()
                                                       .message(e.getMessage())
                                                       .statusCode(StatusCode.NOT_FOUND)
                                                       .build());
            }
        } else {
            log.error("존재하지 않는 memberId : {}", memberId);
            return ResponseEntity.ok(CommonResponse.builder()
                                                   .statusCode(StatusCode.NOT_FOUND)
                                                   .build());
        }
    }

    @ApiOperation(value = "알람 상태 존재여부", notes = "알람 상태 존재여부")
    @GetMapping({"/exist"})
    public ResponseEntity<CommonResponse> isExist(@RequestParam String memberId) {
        if (!StringUtils.isEmpty(memberId)) {
            try {
                return ResponseEntity.ok(CommonResponse.builder()
                                                       .statusCode(StatusCode.SUCCESS)
                                                       .message("알람 상태 존재여부 조회")
                                                       .data(pushService.isExistPushState(memberId))
                                                       .build());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return ResponseEntity.ok(CommonResponse.builder()
                                                       .message(e.getMessage())
                                                       .statusCode(StatusCode.NOT_FOUND)
                                                       .build());
            }
        } else {
            log.error("존재하지 않는 memberId : {}", memberId);
            return ResponseEntity.ok(CommonResponse.builder()
                                                   .statusCode(StatusCode.NOT_FOUND)
                                                   .build());
        }
    }


    @PostMapping({"/init"})
    public ResponseEntity<CommonResponse> initPushState(@RequestBody PushDto dto) throws Exception {
        if (!ObjectUtils.isEmpty(dto)) {
            try {
                pushService.initPushState(dto);
                return ResponseEntity.ok(CommonResponse.builder()
                                                       .statusCode(StatusCode.SUCCESS)
                                                       .message("알림 초기화 성공")
                                                       .build());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return ResponseEntity.ok(CommonResponse.builder()
                                                       .message(e.getMessage())
                                                       .statusCode(StatusCode.NOT_FOUND)
                                                       .build());
            }
        } else {
            return ResponseEntity.ok(CommonResponse.builder()
                                                   .statusCode(StatusCode.NOT_FOUND)
                                                   .build());
        }
    }
}