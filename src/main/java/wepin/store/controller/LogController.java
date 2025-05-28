package wepin.store.controller;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import wepin.store.dto.CommonResponse;
import wepin.store.dto.LogDto;
import wepin.store.enumeration.LogType;
import wepin.store.enumeration.StatusCode;
import wepin.store.service.LogService;


@RequiredArgsConstructor
@RestController
@RequestMapping("/log")
@CrossOrigin
@Slf4j
public class LogController {

    private final LogService logService;

    @ApiOperation(value = "로그 저장", notes = "LOG CREATE")
    @PostMapping(value = "/create")
    public ResponseEntity<CommonResponse> createLog(@RequestBody LogDto dto) {

        if (!ObjectUtils.isEmpty(dto)) {
            try {
                logService.create(dto);
            } catch (Exception e) {
                logService.create(LogDto.builder()
                                        .name("ADMIN")
                                        .memberId(null)
                                        .message(e.getMessage())
                                        .loc("createLog")
                                        .type(LogType.ERROR.toString())
                                        .build());
            }
        } else {
            return ResponseEntity.badRequest()
                                 .body(CommonResponse.builder()
                                                     .statusCode(StatusCode.BAD_REQUEST)
                                                     .message("요청 데이터가 비어있습니다.")
                                                     .build());
        }
        return ResponseEntity.ok(CommonResponse.builder()
                                               .statusCode(StatusCode.SUCCESS)
                                               .message("로그 생성")
                                               .build());
    }
}
