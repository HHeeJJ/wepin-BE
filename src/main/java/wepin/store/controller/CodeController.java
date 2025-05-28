package wepin.store.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import wepin.store.dto.CommonResponse;
import wepin.store.enumeration.StatusCode;
import wepin.store.service.CodeService;


@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/code")
@CrossOrigin
public class CodeController {

    private final CodeService codeService;

    @ApiOperation(value = "리스트", notes = "")
    @GetMapping("/list")
    public ResponseEntity<CommonResponse> list(@ApiParam(name = "cdClsId", value = "cdClsId") @RequestParam String cdClsId) {

        try {
            if (!StringUtils.isEmpty(cdClsId)) {
                return ResponseEntity.ok(CommonResponse.builder()
                                                       .statusCode(StatusCode.SUCCESS)
                                                       .message("code list")
                                                       .data(codeService.getCodeListByCdClsId(cdClsId))
                                                       .build());
            } else {
                return ResponseEntity.ok(CommonResponse.builder()
                                                       .statusCode(StatusCode.NOT_FOUND)
                                                       .build());
            }
        } catch (Exception ex) {
            return ResponseEntity.ok(CommonResponse.builder()
                                                   .statusCode(StatusCode.NOT_FOUND)
                                                   .build());
        }
    }

}
