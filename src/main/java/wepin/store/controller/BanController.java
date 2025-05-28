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
import wepin.store.dto.BanDto;
import wepin.store.dto.BanDto.BanListDto;
import wepin.store.dto.CommonPage;
import wepin.store.dto.CommonResponse;
import wepin.store.entity.Ban;
import wepin.store.enumeration.StatusCode;
import wepin.store.service.BanService;


@RequiredArgsConstructor
@RestController
@RequestMapping("/ban")
@CrossOrigin
@Slf4j
public class BanController {

    private final BanService banService;

    @ApiOperation(value = "차단", notes = "")
    @PostMapping(value = "/create")
    public ResponseEntity<CommonResponse> ban(@RequestBody BanDto.BanCreateDto dto) {

        if (!ObjectUtils.isEmpty(dto)) {
            try {
                banService.create(dto);
            } catch (Exception e) {
                log.info("차단 memberId : " + dto.getMemberId() + "banId : " + dto.getBanId());
                throw e;
            }
            return ResponseEntity.ok(CommonResponse.builder()
                                                   .statusCode(StatusCode.SUCCESS)
                                                   .message("차단 완료되었습니다.")
                                                   .build());
        } else {
            return ResponseEntity.ok(CommonResponse.builder()
                                                   .statusCode(StatusCode.NOT_FOUND)
                                                   .build());
        }
    }


    @ApiOperation(value = "차단 해제", notes = "")
    @PostMapping(value = "/delete")
    public ResponseEntity<CommonResponse> delete(@RequestBody BanDto.BanDeleteDto dto) {

        if (!ObjectUtils.isEmpty(dto)) {
            try {
                banService.delete(dto);
            } catch (Exception e) {
                log.info("차단해제 id : " + dto.getId());
                throw e;
            }
            return ResponseEntity.ok(CommonResponse.builder()
                                                   .statusCode(StatusCode.SUCCESS)
                                                   .message("차단해제 완료되었습니다.")
                                                   .build());
        } else {
            return ResponseEntity.ok(CommonResponse.builder()
                                                   .statusCode(StatusCode.NOT_FOUND)
                                                   .build());
        }
    }

    @ApiOperation(value = "차단 목록", notes = "")
    @GetMapping(value = "/list")
    public ResponseEntity<CommonResponse> list(@ApiParam(name = "memberId", value = "memberId") @RequestParam(name = "memberId") String memberId,
                                               Pageable pageable) {

        if (!StringUtils.isEmpty(memberId)) {

            Page<Ban> page = banService.getBanList(memberId, pageable);

            return ResponseEntity.ok(CommonResponse.builder()
                                                   .data(CommonPage.builder()
                                                                   .totalCount(page.getTotalElements())
                                                                   .hasNext(page.hasNext())
                                                                   .pageNumber(page.getNumber())
                                                                   .lists(page.getContent()
                                                                              .stream()
                                                                              .map(m -> BanListDto.toDto(m))
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

}
