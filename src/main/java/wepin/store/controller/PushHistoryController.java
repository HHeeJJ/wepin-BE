package wepin.store.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import wepin.store.dto.CommonPage;
import wepin.store.dto.CommonResponse;
import wepin.store.dto.PushDto.PushHistoryListDto;
import wepin.store.entity.PushHistory;
import wepin.store.enumeration.StatusCode;
import wepin.store.service.PushHistoryService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/push-history")
@CrossOrigin
public class PushHistoryController {

    private final PushHistoryService pushHistoryService;


    @ApiOperation(value = "알람 리스트", notes = "알람 리스트 조회")
    @GetMapping({"/list"})
    public ResponseEntity<CommonResponse> getPushHistoryList(@RequestParam String memberId, Pageable pageable) {
        if (!StringUtils.isEmpty(memberId)) {
            try {
                Page<PushHistory> page = pushHistoryService.findByMemberId(memberId, pageable);
                return ResponseEntity.ok(CommonResponse.builder()
                                                       .statusCode(StatusCode.SUCCESS)
                                                       .message("알람 리스트 조회")
                                                       .data(CommonPage.builder()
                                                                       .totalCount(page.getTotalElements())
                                                                       .hasNext(page.hasNext())
                                                                       .pageNumber(page.getNumber())
                                                                       .lists(page.getContent()
                                                                                  .stream()
                                                                                  .map(PushHistoryListDto::toDto)
                                                                                  .collect(Collectors.toList()))
                                                                       .build())
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

}