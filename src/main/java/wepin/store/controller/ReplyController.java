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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import wepin.store.dto.CommonPage;
import wepin.store.dto.CommonResponse;
import wepin.store.dto.PushDto.PushHistoryDto;
import wepin.store.dto.ReplyDto;
import wepin.store.entity.Reply;
import wepin.store.enumeration.HistoryType;
import wepin.store.enumeration.StatusCode;
import wepin.store.service.PushHistoryService;
import wepin.store.service.ReplyService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/reply")
@CrossOrigin
public class ReplyController {

    private final ReplyService       replyService;
    private final PushHistoryService pushHistoryService;

    @ApiOperation(value = "댓글", notes = "댓글 작성")
    @PostMapping({"/create"})
    public ResponseEntity<CommonResponse> createReply(@RequestBody ReplyDto.CreateDto dto) throws Exception {
        if (!ObjectUtils.isEmpty(dto)) {
            ReplyDto reply = replyService.create(dto);
            if (reply != null) {
                try {
                    if (!reply.getMemberId()
                              .equals(reply.getPushSenderId())) {
                        String message = replyService.sendMessage(dto.getFeedId(), reply.getNickNm(), reply.getDesc());
                        if (!StringUtils.isEmpty(message)) {
                            pushHistoryService.create(PushHistoryDto.builder()
                                                                    .message(message)
                                                                    .senderId(reply.getPushSenderId())
                                                                    .memberId(reply.getMemberId())
                                                                    .refId(dto.getFeedId())
                                                                    .type(HistoryType.REPLY.getName())
                                                                    .build());
                        }
                    }
                } catch (Exception e) {
                    log.error("댓글 등록 알림 발송 실패 replyId : {}", reply.getId());
                }

            }
            return ResponseEntity.ok(CommonResponse.builder()
                                                   .statusCode(StatusCode.SUCCESS)
                                                   .message("댓글 작성")
                                                   .data(reply)
                                                   .build());
        } else {
            return ResponseEntity.ok(CommonResponse.builder()
                                                   .statusCode(StatusCode.NOT_FOUND)
                                                   .build());
        }
    }


    @ApiOperation(value = "댓삭", notes = "댓삭")
    @PostMapping({"/delete"})
    public ResponseEntity<CommonResponse> deleteReply(@RequestBody ReplyDto.RemoveDto dto) throws Exception {

        String id = dto.getReplyId();

        if (!StringUtils.isEmpty(id)) {
            replyService.delete(id);
            return ResponseEntity.ok(CommonResponse.builder()
                                                   .statusCode(StatusCode.SUCCESS)
                                                   .message("댓글 삭제")
                                                   .data(null)
                                                   .build());
        } else {
            return ResponseEntity.ok(CommonResponse.builder()
                                                   .statusCode(StatusCode.NOT_FOUND)
                                                   .build());
        }
    }


    @ApiOperation(value = "댓글 리스트", notes = "댓글 리스트 조회")
    @GetMapping({"/list"})
    public ResponseEntity<CommonResponse> getReplyList(@RequestParam String feedId, Pageable pageable) {

        if (!StringUtils.isEmpty(feedId)) {

            Page<Reply> replyPage = replyService.getReplyListForPage(feedId, pageable);

            return ResponseEntity.ok(CommonResponse.builder()
                                                   .statusCode(StatusCode.SUCCESS)
                                                   .message("댓글이 조회 되었습니다.")
                                                   .data(CommonPage.builder()
                                                                   .totalCount(replyPage.getTotalElements())
                                                                   .hasNext(replyPage.hasNext())
                                                                   .pageNumber(replyPage.getNumber())
                                                                   .lists(replyPage.getContent()
                                                                                   .stream()
                                                                                   .map(m -> ReplyDto.toDto(m))
                                                                                   .collect(Collectors.toList()))
                                                                   .build())
                                                   .build());

        } else {
            return ResponseEntity.ok(CommonResponse.builder()
                                                   .statusCode(StatusCode.NOT_FOUND)
                                                   .build());
        }
    }
}