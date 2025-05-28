package wepin.store.controller;

import org.apache.commons.lang3.StringUtils;
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
import wepin.store.dto.LikeDto;
import wepin.store.dto.PushDto.PushHistoryDto;
import wepin.store.enumeration.HistoryType;
import wepin.store.enumeration.StatusCode;
import wepin.store.service.LikeService;
import wepin.store.service.PushHistoryService;


@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/like")
@CrossOrigin
public class LikeController {

    private final LikeService        likeService;
    private final PushHistoryService pushHistoryService;

    @ApiOperation(value = "좋아요", notes = "CREATE LIKE")
    @PostMapping({"/create"})
    public ResponseEntity<CommonResponse> createLike(@RequestBody LikeDto.SaveDeleteDto dto) throws Exception {

        String feedId   = dto.getFeedId();
        String memberId = dto.getMemberId();

        if (!StringUtils.isEmpty(feedId) && !StringUtils.isEmpty(memberId)) {
            LikeDto likeDto = likeService.createLike(memberId, feedId);
            if (likeDto != null) {
                try {
                    if (!likeDto.getMemberId()
                                .equals(likeDto.getPushSenderId())) {
                        String message = likeService.sendMessage(likeDto.getFeedId(), likeDto.getNickNm(), HistoryType.LIKE.getName());
                        if (!StringUtils.isEmpty(message)) {
                            pushHistoryService.create(PushHistoryDto.builder()
                                                                    .message(message)
                                                                    .senderId(likeDto.getPushSenderId())
                                                                    .memberId(likeDto.getMemberId())
                                                                    .refId(feedId)
                                                                    .type(HistoryType.LIKE.getName())
                                                                    .build());
                        }

                    }
                } catch (Exception e) {
                    log.error("좋아요 알림 발송 실패 feedId : {}", likeDto.getFeedId(), e);
                }
            }

            return ResponseEntity.ok(CommonResponse.builder()
                                                   .statusCode(StatusCode.SUCCESS)
                                                   .message("좋아요")
                                                   .data(likeDto)
                                                   .build());
        } else {
            return ResponseEntity.ok(CommonResponse.builder()
                                                   .statusCode(StatusCode.NOT_FOUND)
                                                   .build());
        }
    }


    @ApiOperation(value = "좋아요 취소", notes = "LIKE DELETE")
    @PostMapping({"/delete"})
    public ResponseEntity<CommonResponse> deleteLike(@RequestBody LikeDto.SaveDeleteDto dto) throws Exception {

        String feedId   = dto.getFeedId();
        String memberId = dto.getMemberId();

        if (!StringUtils.isEmpty(feedId) && !StringUtils.isEmpty(memberId)) {
            LikeDto likeDto = likeService.deleteLike(memberId, feedId);
            if (likeDto != null) {
                try {
                    if (!likeDto.getMemberId()
                                .equals(likeDto.getPushSenderId())) {
                        String message = likeService.sendMessage(likeDto.getFeedId(), likeDto.getNickNm(), HistoryType.DISLIKE.getName());
                        if (!StringUtils.isEmpty(message)) {
                            pushHistoryService.create(PushHistoryDto.builder()
                                                                    .message(message)
                                                                    .senderId(likeDto.getPushSenderId())
                                                                    .memberId(likeDto.getMemberId())
                                                                    .refId(feedId)
                                                                    .type(HistoryType.DISLIKE.getName())
                                                                    .build());
                        }
                    }
                } catch (Exception e) {
                    log.error("좋아요 알림 발송 실패 feedId : {}", likeDto.getFeedId(), e);
                }
            }
            return ResponseEntity.ok(CommonResponse.builder()
                                                   .statusCode(StatusCode.SUCCESS)
                                                   .message("좋아요 취소")
                                                   .data(likeDto)
                                                   .build());
        } else {
            return ResponseEntity.ok(CommonResponse.builder()
                                                   .statusCode(StatusCode.NOT_FOUND)
                                                   .build());
        }
    }
}
