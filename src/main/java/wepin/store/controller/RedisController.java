package wepin.store.controller;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import wepin.store.dto.*;
import wepin.store.enumeration.StatusCode;
import wepin.store.service.*;

import java.net.URISyntaxException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/redis")
@CrossOrigin
public class RedisController {

    private final FeedService  feedService;
    private final RedisService redisService;

    //    @ApiOperation(value = "인기 순위 리프레시", notes = "")
    //    @PostMapping("/top-save")
    //    public ResponseEntity<CommonResponse> topSave() {
    //
    //        try {
    //            return ResponseEntity.ok(CommonResponse.builder()
    //                                                   .statusCode(StatusCode.SUCCESS)
    //                                                   .data(redisService.topSave())
    //                                                   .build());
    //
    //        } catch (Exception ex) {
    //            return ResponseEntity.ok(CommonResponse.builder()
    //                                                   .statusCode(StatusCode.NOT_FOUND)
    //                                                   .build());
    //        }
    //    }

    //    @ApiOperation(value = "Redis Test", notes = "Redis Save")
    //    @PostMapping("/save")
    //    public ResponseEntity<CommonResponse> save(@RequestBody RedisDto dto) {
    //
    //        try {
    //            return ResponseEntity.ok(CommonResponse.builder()
    //                                                   .statusCode(StatusCode.SUCCESS)
    //                                                   .data(redisService.save(dto.getKey(), feedService.getMainFeedList(dto.getMemberId())))
    //                                                   .build());
    //
    //        } catch (Exception ex) {
    //            return ResponseEntity.ok(CommonResponse.builder()
    //                                                   .statusCode(StatusCode.NOT_FOUND)
    //                                                   .build());
    //        }
    //    }


    @ApiOperation(value = "Redis Test", notes = "Redis Get")
    @GetMapping("/find")
    public ResponseEntity<CommonResponse> find(@RequestParam String key) {

        try {
            return ResponseEntity.ok(CommonResponse.builder()
                                                   .statusCode(StatusCode.SUCCESS)
                                                   .data(redisService.find(key))
                                                   .build());

        } catch (Exception ex) {
            return ResponseEntity.ok(CommonResponse.builder()
                                                   .statusCode(StatusCode.NOT_FOUND)
                                                   .build());
        }
    }
}