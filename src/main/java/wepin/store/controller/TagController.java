package wepin.store.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@RequiredArgsConstructor
@RestController
@RequestMapping("/tag")
@CrossOrigin
@Slf4j
public class TagController {

    //    private final TagService tagService;
    //
    //
    //    @ApiOperation(value = " 저장", notes = "")
    //    @PostMapping(value = "/create")
    //    public ResponseEntity<CommonResponse> create(@RequestBody TagDto dto) {
    //
    //        if (!ObjectUtils.isEmpty(dto)) {
    //            tagService.create(dto.getTagList(), dto.getFeedId());
    //            return ResponseEntity.ok(CommonResponse.builder()
    //                                                   .statusCode(StatusCode.SUCCESS)
    //                                                   .message("저장되었습니다.")
    //                                                   .build());
    //        } else {
    //            return ResponseEntity.ok(CommonResponse.builder()
    //                                                   .statusCode(StatusCode.NOT_FOUND)
    //                                                   .build());
    //        }
    //    }

}
