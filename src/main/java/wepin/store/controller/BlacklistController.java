package wepin.store.controller;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import wepin.store.dto.BlacklistDto;
import wepin.store.dto.CommonPage;
import wepin.store.dto.CommonResponse;
import wepin.store.entity.Blacklist;
import wepin.store.enumeration.StatusCode;
import wepin.store.service.BlacklistService;


@RequiredArgsConstructor
@RestController
@RequestMapping("/black")
@CrossOrigin
@Slf4j
public class BlacklistController {

    private final BlacklistService blacklistService;

    @ApiOperation(value = "신고 불러오기", notes = "BLACK LIST")
    @GetMapping({"/list"})
    public ResponseEntity<CommonResponse> getBlackList(@ApiParam(name = "status", value = "상태") @RequestParam(name = "status") String status,
                                                       Pageable pageable) {

        if (!StringUtils.isEmpty(status)) {

            Page<Blacklist> page = blacklistService.getBlackList(status, pageable);


            return ResponseEntity.ok(CommonResponse.builder()
                                                   .data(CommonPage.builder()
                                                                   .totalCount(page.getTotalElements())
                                                                   .hasNext(page.hasNext())
                                                                   .pageNumber(page.getNumber())
                                                                   .lists(page.getContent()
                                                                              .stream()
                                                                              .map(m -> BlacklistDto.ListDto.toDto(m))
                                                                              .collect(Collectors.toList()))
                                                                   .build())
                                                   .statusCode(StatusCode.SUCCESS)
                                                   .build());
        } else {
            return ResponseEntity.ok(CommonResponse.builder()
                                                   .statusCode(StatusCode.NOT_FOUND)
                                                   .build());
        }
    }


    @ApiOperation(value = "신고 접수", notes = "")
    @PostMapping(value = "/request")
    public ResponseEntity<CommonResponse> requestBlacklist(@RequestBody BlacklistDto.RequestDto dto) {

        if (!ObjectUtils.isEmpty(dto)) {
            try {
                blacklistService.request(dto);
            } catch (Exception e) {
                log.info("신고 memberId : " + dto.getMemberId() + "blackId : " + dto.getBlackId());
                throw e;
            }
            return ResponseEntity.ok(CommonResponse.builder()
                                                   .statusCode(StatusCode.SUCCESS)
                                                   .message("신고가 완료되었습니다.")
                                                   .build());
        } else {
            return ResponseEntity.ok(CommonResponse.builder()
                                                   .statusCode(StatusCode.NOT_FOUND)
                                                   .build());
        }
    }


    @ApiOperation(value = "블랙리스트 상태 변경", notes = "BLACK UPDATE")
    @PostMapping("/update")
    public ResponseEntity<CommonResponse> update(@RequestBody BlacklistDto.UpdateDto dto) {


        if (!ObjectUtils.isEmpty(dto)) {
            try {
                blacklistService.update(dto);
            } catch (Exception e) {
                log.info("상태 변경 실패 : [id] " + dto.getId() + " [status] " + dto.getStatus());
                throw e;
            }
            return ResponseEntity.ok(CommonResponse.builder()
                                                   .statusCode(StatusCode.SUCCESS)
                                                   .message("상태가 변경되었습니다.")
                                                   .build());
        } else {
            return ResponseEntity.ok(CommonResponse.builder()
                                                   .statusCode(StatusCode.NOT_FOUND)
                                                   .build());
        }
    }
}
