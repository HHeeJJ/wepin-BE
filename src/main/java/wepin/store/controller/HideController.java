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
import wepin.store.dto.CommonResponse;
import wepin.store.dto.HideDto;
import wepin.store.enumeration.StatusCode;
import wepin.store.service.HideService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/hide")
@CrossOrigin
public class HideController {

    private final HideService hideService;


    @ApiOperation(value = "숨김처리", notes = "숨김처리")
    @PostMapping({"/create"})
    public ResponseEntity<CommonResponse> hide(@RequestBody HideDto dto) throws Exception {

        if (!ObjectUtils.isEmpty(dto)) {
            hideService.create(dto);
            return ResponseEntity.ok(CommonResponse.builder()
                                                   .statusCode(StatusCode.SUCCESS)
                                                   .message("피드 숨김")
                                                   .build());
        } else {
            return ResponseEntity.ok(CommonResponse.builder()
                                                   .statusCode(StatusCode.NOT_FOUND)
                                                   .build());
        }
    }


}