package wepin.store.controller;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.stream.Collectors;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import wepin.store.dto.CommonPage;
import wepin.store.dto.CommonResponse;
import wepin.store.dto.CustomPinDto;
import wepin.store.dto.CustomPinDto.SelectDto;
import wepin.store.entity.MemberCurrentPin;
import wepin.store.enumeration.StatusCode;
import wepin.store.service.CustomPinService;


@RequiredArgsConstructor
@RestController
@RequestMapping("/custom-pin")
@CrossOrigin
@Slf4j
public class CustomPinController {

    private final CustomPinService customPinService;

    @PostMapping({"/init"})
    public ResponseEntity<CommonResponse> init() {
        try {
            customPinService.init();
            return ResponseEntity.ok(CommonResponse.builder()
                                                   .statusCode(StatusCode.SUCCESS)
                                                   .build());
        } catch (Exception e) {
            log.info("커스텀 핀 초기화");
            throw e;
        }
    }

    @ApiOperation(value = "커스텀 핀 리스트", notes = "")
    @GetMapping({"/list"})
    public ResponseEntity<CommonResponse> get(@RequestParam(name = "memberId") String memberId, Pageable pageable) {

        if (!StringUtils.isEmpty(memberId)) {

            Page<CustomPinDto.ListDto> page = customPinService.getCustomPins(memberId, pageable);

            return ResponseEntity.ok(CommonResponse.builder()
                                                   .data(CommonPage.builder()
                                                                   .totalCount(page.getTotalElements())
                                                                   .hasNext(page.hasNext())
                                                                   .pageNumber(page.getNumber())
                                                                   .lists(page.getContent()
                                                                              .stream()
                                                                              .map(pin -> CustomPinDto.ListDto.builder()
                                                                                                              .id(pin.getId())
                                                                                                              .name(pin.getName())
                                                                                                              .mainPin(pin.getMainPin())
                                                                                                              .subPin(pin.getSubPin())
                                                                                                              .memberId(pin.getMemberId())
                                                                                                              .description(pin.getDescription())
                                                                                                              .customPinId(pin.getCustomPinId())
                                                                                                              .currentPinId(pin.getCurrentPinId())
                                                                                                              .endAt(pin.getEndAt())
                                                                                                              .isCurrent(
                                                                                                                      pin.getCurrentPinId() != null)
                                                                                                              .hasPin(pin.getMemberId() != null)
                                                                                                              .build())
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

    @ApiOperation(value = "", notes = "")
    @PostMapping(value = "/use")
    public ResponseEntity<CommonResponse> select(@RequestBody SelectDto dto) {

        if (!ObjectUtils.isEmpty(dto)) {
            try {
                Optional<MemberCurrentPin> memberCurrentPin = customPinService.saveCustomPin(dto);

                if (memberCurrentPin.isPresent()) {
                    Pageable pageable = PageRequest.of(0, 20);
                    Page<CustomPinDto.ListDto> page = customPinService.getCustomPins(memberCurrentPin.get()
                                                                                                     .getMember()
                                                                                                     .getId(), pageable);

                    return ResponseEntity.ok(CommonResponse.builder()
                                                           .message("핀 선택이 완료되었습니다.")
                                                           .data(CommonPage.builder()
                                                                           .totalCount(page.getTotalElements())
                                                                           .hasNext(page.hasNext())
                                                                           .pageNumber(page.getNumber())
                                                                           .lists(page.getContent()
                                                                                      .stream()
                                                                                      .map(pin -> CustomPinDto.ListDto.builder()
                                                                                                                      .id(pin.getId())
                                                                                                                      .name(pin.getName())
                                                                                                                      .mainPin(pin.getMainPin())
                                                                                                                      .subPin(pin.getSubPin())
                                                                                                                      .memberId(pin.getMemberId())
                                                                                                                      .description(
                                                                                                                              pin.getDescription())
                                                                                                                      .customPinId(
                                                                                                                              pin.getCustomPinId())
                                                                                                                      .currentPinId(
                                                                                                                              pin.getCurrentPinId())
                                                                                                                      .isCurrent(
                                                                                                                              pin.getCurrentPinId() != null)
                                                                                                                      .hasPin(pin.getMemberId() != null)
                                                                                                                      .build())
                                                                                      .collect(Collectors.toList()))
                                                                           .build())
                                                           .statusCode(StatusCode.SUCCESS)
                                                           .build());
                } else {
                    return ResponseEntity.ok(CommonResponse.builder()
                                                           .message("요청 데이터가 비어있습니다.")
                                                           .statusCode(StatusCode.BAD_REQUEST)
                                                           .build());
                }
            } catch (Exception e) {
                log.info("memberId : {} customPinId : {}", dto.getMemberId(), dto.getCustomPinId());
                throw e;
            }
        } else {
            return ResponseEntity.ok(CommonResponse.builder()
                                                   .statusCode(StatusCode.BAD_REQUEST)
                                                   .build());
        }
    }
}
