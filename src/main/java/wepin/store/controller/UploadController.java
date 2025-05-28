package wepin.store.controller;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import wepin.store.dto.CommonResponse;
import wepin.store.dto.UploadDto;
import wepin.store.enumeration.StatusCode;
import wepin.store.utils.S3UploadUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequiredArgsConstructor
@RequestMapping("/upload")
@CrossOrigin
@Slf4j
public class UploadController {

    private final S3UploadUtils s3UploadUtils;

    @ApiOperation(value = "파일 업로드 테스트")
    @PostMapping(value = "/file/list")
    public ResponseEntity<CommonResponse> upload(@RequestBody UploadDto dto) throws Exception {

        List<String> imgList = dto.getImgList();
        String       pinId   = dto.getPinId();
        List<String> urlList = new ArrayList<>();
        if (imgList.size() > 0) {
            AtomicInteger idx = new AtomicInteger(0);

            for (String img : imgList) {
                String mainImgUrl = s3UploadUtils.upload(img, "pin", pinId + "_" + idx.getAndIncrement());

                log.info("IMG_URL : " + mainImgUrl);
                urlList.add(mainImgUrl);
            }

            return ResponseEntity.ok(CommonResponse.builder()
                                                   .statusCode(StatusCode.SUCCESS)
                                                   .message("업로드 테스트 성공")
                                                   .data(urlList)
                                                   .build());

        } else {
            return ResponseEntity.ok(CommonResponse.builder()
                                                   .statusCode(StatusCode.SUCCESS)
                                                   .message("업로드 테스트 실패")
                                                   .data(null)
                                                   .build());
        }

    }

    @ApiOperation(value = "파일 업로드 테스트2")
    @PostMapping(value = "/file")
    public ResponseEntity<CommonResponse> upload2(@RequestParam("preFix") String preFix, @RequestParam("dirNm") String dirNm,
                                                  @RequestPart("img") MultipartFile img) throws Exception {
        if (img != null) {
            String mainImgUrl = s3UploadUtils.upload(img, dirNm, preFix + "_" + LocalDateTime.now());

            log.info("IMG_URL : " + mainImgUrl);

            return ResponseEntity.ok(CommonResponse.builder()
                                                   .statusCode(StatusCode.SUCCESS)
                                                   .message("업로드 테스트 성공")
                                                   .data(mainImgUrl)
                                                   .build());
        } else {
            return ResponseEntity.ok(CommonResponse.builder()
                                                   .statusCode(StatusCode.SUCCESS)
                                                   .message("업로드 테스트 실패")
                                                   .data(null)
                                                   .build());
        }
    }
}
