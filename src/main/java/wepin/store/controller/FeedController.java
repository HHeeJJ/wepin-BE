package wepin.store.controller;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import wepin.store.dto.CommonPage;
import wepin.store.dto.CommonResponse;
import wepin.store.dto.FeedDto;
import wepin.store.dto.PinDto;
import wepin.store.dto.PushDto.PushHistoryDto;
import wepin.store.entity.Feed;
import wepin.store.enumeration.StatusCode;
import wepin.store.service.CustomPinService;
import wepin.store.service.FeedService;
import wepin.store.service.FollowService;
import wepin.store.service.PushHistoryService;


@RequiredArgsConstructor
@RestController
@RequestMapping("/feed")
@CrossOrigin
@Slf4j
public class FeedController {

    private final FeedService        feedService;
    private final FollowService      followService;
    private final PushHistoryService pushHistoryService;
    private final CustomPinService   customPinService;


    @ApiOperation(value = "사용자 메인 피드 불러오기", notes = "MAIN FEED LIST")
    @GetMapping({"/list"})
    public ResponseEntity<CommonResponse> getMainFeedList(
            @ApiParam(name = "memberId", value = "멤버 ID") @RequestParam(name = "memberId") String memberId, Pageable pageable) {

        if (!StringUtils.isEmpty(memberId)) {
            //            List<String> followerList = followService.getFollowerList(memberId)
            //                                                     .stream()
            //                                                     .map(m -> m.getFollowerId())
            //                                                     .collect(Collectors.toList());
            //
            //            List<FeedDto.MainDto> feedList;

            // 팔로우 하고 있는 사람이 있는 경우
            //            if (followerList.size() > 0) {
            //                followerList.add(memberId);
            //                feedList = feedService.getMainFeedListByFollower(followerList, memberId);
            //
            //                if (feedList.size() < 10) {
            //                    feedList.addAll(feedService.getMainFeedListByNonFollower(followerList, memberId));
            //                }
            //            } else { // 팔로우 하고 있는 사람이 없는 경우

            Page<Feed> feedPage = feedService.list(memberId, pageable);

            return ResponseEntity.ok(CommonResponse.builder()
                                                   .statusCode(StatusCode.SUCCESS)
                                                   .message("조회되었습니다.")
                                                   .data(CommonPage.builder()
                                                                   .totalCount(feedPage.getTotalElements())
                                                                   .hasNext(feedPage.hasNext())
                                                                   .pageNumber(feedPage.getNumber())
                                                                   .detail(customPinService.findCurrentPinByMemberId(memberId))
                                                                   .lists(feedPage.getContent()
                                                                                  .stream()
                                                                                  .map(m -> FeedDto.MainDto.toDto(m, memberId))
                                                                                  .collect(Collectors.toList()))
                                                                   .build())
                                                   .build());
            //                        }
            //
            //            List<MainDto> list = feedList.stream()
            //                                         .sorted(Comparator.comparing(MainDto::getCreatedAt)
            //                                                           .reversed())
            //                                         .collect(Collectors.toList());
            //
            //            return ResponseEntity.ok(CommonResponse.builder()
            //                                                   .statusCode(StatusCode.SUCCESS)
            //                                                   .message("조회되었습니다.")
            //                                                   .data(list)
            //                                                   .build());
        } else {
            return ResponseEntity.ok(CommonResponse.builder()
                                                   .statusCode(StatusCode.NOT_FOUND)
                                                   .build());
        }
    }


    @ApiOperation(value = "피드 디테일 불러오기", notes = "FEED DETAIL LIST")
    @GetMapping({"/detail"})
    public ResponseEntity<CommonResponse> getFeedDetail(@ApiParam(name = "feedId", value = "FEED ID") @RequestParam(name = "feedId") String feedId,
                                                        @ApiParam(name = "memberId", value = "멤버 ID") @RequestParam(name = "memberId") String memberId,
                                                        Pageable pageable) {

        if (!StringUtils.isEmpty(memberId) && !StringUtils.isEmpty(feedId)) {

            return ResponseEntity.ok(CommonResponse.builder()
                                                   .statusCode(StatusCode.SUCCESS)
                                                   .message("피드 디테일 조회되었습니다.")
                                                   .data(feedService.getFeedDetail(feedId, memberId, pageable))
                                                   .build());
        } else {
            return ResponseEntity.ok(CommonResponse.builder()
                                                   .statusCode(StatusCode.NOT_FOUND)
                                                   .build());
        }
    }

    @ApiOperation(value = "내 피드 불러오기", notes = "My FEED LIST")
    @GetMapping({"/mine"})
    public ResponseEntity<CommonResponse> getMyFeedList(
            @ApiParam(name = "memberId", value = "멤버 ID") @RequestParam(name = "memberId") String memberId, Pageable pageable) {

        if (!StringUtils.isEmpty(memberId)) {

            Page<Feed> feedPage = feedService.getMyFeedList(memberId, pageable);

            return ResponseEntity.ok(CommonResponse.builder()
                                                   .statusCode(StatusCode.SUCCESS)
                                                   .message("내 피드 조회되었습니다.")
                                                   .data(CommonPage.builder()
                                                                   .totalCount(feedPage.getTotalElements())
                                                                   .hasNext(feedPage.hasNext())
                                                                   .pageNumber(feedPage.getNumber())
                                                                   .lists(feedPage.getContent()
                                                                                  .stream()
                                                                                  .map(m -> FeedDto.MyFeedDto.builder()
                                                                                                             .feedId(m.getId())
                                                                                                             .title(m.getTitle())
                                                                                                             .imgUrl(m.getImgUrl())
                                                                                                             .pinList(m.getFeedPinList()
                                                                                                                       .stream()
                                                                                                                       .map(p -> PinDto.builder()
                                                                                                                                       .lng(p.getPin()
                                                                                                                                             .getLongitude())
                                                                                                                                       .feedId(p.getFeed()
                                                                                                                                                .getId())
                                                                                                                                       .isMain(p.getIsMain())
                                                                                                                                       .lat(p.getPin()
                                                                                                                                             .getLatitude())
                                                                                                                                       .addr(p.getPin()
                                                                                                                                              .getAddress())
                                                                                                                                       .addrDetail(
                                                                                                                                               p.getPin()
                                                                                                                                                .getAddressDetail())
                                                                                                                                       .addrStreet(
                                                                                                                                               p.getPin()
                                                                                                                                                .getAddressStreet())
                                                                                                                                       .id(p.getPin()
                                                                                                                                            .getId())
                                                                                                                                       .name(p.getPin()
                                                                                                                                              .getName())
                                                                                                                                       .uiSeq(p.getUiSeq())
                                                                                                                                       .build())
                                                                                                                       .collect(Collectors.toList()))
                                                                                                             .build())
                                                                                  .collect(Collectors.toList()))
                                                                   .build())
                                                   .build());
        } else {
            return ResponseEntity.ok(CommonResponse.builder()
                                                   .statusCode(StatusCode.NOT_FOUND)
                                                   .build());
        }
    }


    @ApiOperation(value = "피드 저장", notes = "CREATE FEED")
    @PostMapping(value = "/create"
            //            , consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    public ResponseEntity<CommonResponse> createFeed(@RequestBody FeedDto.SaveDto dto) {

        if (!ObjectUtils.isEmpty(dto)) {
            try {
                FeedDto.MainDto feed = feedService.createFeed(dto);
                if (feed != null) {
                    List<PushHistoryDto> list = new ArrayList<>();
                    try {
                        list = feedService.sendMessage(feed.getMemberId(), feed.getNickname(), feed);
                    } catch (Exception e) {
                        log.error("피드 등록 알림 발송 실패 feedId : {} ", feed.getId(), e);
                    }

                    if (!list.isEmpty()) {
                        pushHistoryService.create(list);
                    }
                }

                return ResponseEntity.ok(CommonResponse.builder()
                                                       .statusCode(StatusCode.SUCCESS)
                                                       .message("피드가 저장되었습니다.")
                                                       .build());
            } catch (Exception e) {
                log.error("피드 저장 실패 memberId : {}", dto.getMemberId(), e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                     .body(CommonResponse.builder()
                                                         .statusCode(StatusCode.ERROR)
                                                         .message("내부 서버 오류로 인해 피드 저장에 실패했습니다.")
                                                         .build());
            }
        } else {
            return ResponseEntity.badRequest()
                                 .body(CommonResponse.builder()
                                                     .statusCode(StatusCode.BAD_REQUEST)
                                                     .message("요청 데이터가 비어있습니다.")
                                                     .build());
        }
    }

    @ApiOperation(value = "피드 합치기", notes = "CREATE MERGE")
    @PostMapping(value = "/merge"
            //            , consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    public ResponseEntity<CommonResponse> merge(@RequestBody FeedDto.SaveDto dto) {

        if (!ObjectUtils.isEmpty(dto)) {
            try {
                FeedDto.MainDto feed = feedService.merge(dto);
                if (feed != null) {
                    List<PushHistoryDto> list = new ArrayList<>();
                    try {
                        list = feedService.sendMessage(feed.getMemberId(), feed.getNickname(), feed);
                    } catch (Exception e) {
                        log.error("피드[합치기] 등록 알림 발송 실패 feedId : {} ", feed.getId(), e);
                    }

                    if (!list.isEmpty()) {
                        pushHistoryService.create(list);
                    }
                }

                return ResponseEntity.ok(CommonResponse.builder()
                                                       .statusCode(StatusCode.SUCCESS)
                                                       .message("피드[합치기]가 저장되었습니다.")
                                                       .build());
            } catch (Exception e) {
                log.error("피드[합치기] 저장 실패 memberId : {}", dto.getMemberId(), e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                     .body(CommonResponse.builder()
                                                         .statusCode(StatusCode.ERROR)
                                                         .message("내부 서버 오류로 인해 피드[합치기] 저장에 실패했습니다.")
                                                         .build());
            }
        } else {
            return ResponseEntity.badRequest()
                                 .body(CommonResponse.builder()
                                                     .statusCode(StatusCode.BAD_REQUEST)
                                                     .message("요청 데이터가 비어있습니다.")
                                                     .build());
        }
    }


    @ApiOperation(value = "피드 삭제", notes = "FEED DELETE")
    @PostMapping("/delete")
    public ResponseEntity<CommonResponse> deleteFeed(@RequestBody FeedDto.DeleteDto dto) {

        String feedId = dto.getFeedId();

        if (!StringUtils.isEmpty(feedId)) {
            try {
                feedService.deleteFeed(feedId);
            } catch (Exception e) {
                log.info("피드 삭제 실패 feedId : " + dto.getFeedId());
                throw e;
            }
            return ResponseEntity.ok(CommonResponse.builder()
                                                   .statusCode(StatusCode.SUCCESS)
                                                   .message("피드가 삭제되었습니다.")
                                                   .build());
        } else {
            log.info("[피드 삭제] FEED_ID 누락");
            return ResponseEntity.ok(CommonResponse.builder()
                                                   .statusCode(StatusCode.NOT_FOUND)
                                                   .build());
        }
    }

    @ApiOperation(value = "피드 비활성화", notes = "FEED 비활성화")
    @PostMapping("/disabled")
    public ResponseEntity<CommonResponse> disabled(@RequestBody FeedDto.DeleteDto dto) {

        String feedId = dto.getFeedId();

        if (!StringUtils.isEmpty(feedId)) {
            try {
                feedService.disabled(feedId);
            } catch (Exception e) {
                log.info("피드 비활성화 실패 feedId : " + dto.getFeedId());
                throw e;
            }
            return ResponseEntity.ok(CommonResponse.builder()
                                                   .statusCode(StatusCode.SUCCESS)
                                                   .message("피드가 삭제되었습니다.")
                                                   .build());
        } else {
            log.info("[피드 비활성화] FEED_ID 누락");
            return ResponseEntity.ok(CommonResponse.builder()
                                                   .statusCode(StatusCode.NOT_FOUND)
                                                   .build());
        }
    }


    @ApiOperation(value = "피드 검색 (태그)", notes = "")
    @GetMapping({"/search"})
    public ResponseEntity<CommonResponse> getFeedListByTag(@ApiParam(name = "Tag", value = "Tag") @RequestParam(name = "tag") String tag,
                                                           @ApiParam(name = "memberId", value = "memberId") @RequestParam(name = "memberId") String memberId,
                                                           Pageable pageable) {

        if (!StringUtils.isEmpty(tag)) {
            Page<Feed> feedPage = feedService.getFeedListByTag(tag, memberId, pageable);
            return ResponseEntity.ok(CommonResponse.builder()
                                                   .statusCode(StatusCode.SUCCESS)
                                                   .message("피드가 조회되었습니다.")
                                                   .data(CommonPage.builder()
                                                                   .totalCount(feedPage.getTotalElements())
                                                                   .hasNext(feedPage.hasNext())
                                                                   .pageNumber(feedPage.getNumber())
                                                                   .lists(feedPage.getContent()
                                                                                  .stream()
                                                                                  .map(m -> FeedDto.FeedMainDto.builder()
                                                                                                               .feedId(m.getId())
                                                                                                               .pinCnt(m.getFeedPinList()
                                                                                                                        .size())
                                                                                                               .title(m.getTitle())
                                                                                                               .imgUrl(m.getImgUrl())
                                                                                                               .build())
                                                                                  .collect(Collectors.toList()))
                                                                   .build())
                                                   .build());
        } else {
            return ResponseEntity.ok(CommonResponse.builder()
                                                   .statusCode(StatusCode.NOT_FOUND)
                                                   .build());
        }
    }

    @ApiOperation(value = "피드 마이그레이션", notes = "피드 마이그레이션")
    @PostMapping("/migrate")
    public ResponseEntity<CommonResponse> migrate() {

        try {
            feedService.migrate();
        } catch (Exception e) {
            log.info("피드 마이그레이션 실패");
            throw e;
        }
        return ResponseEntity.ok(CommonResponse.builder()
                                               .statusCode(StatusCode.SUCCESS)
                                               .message("피드가 마이그레이션 성공.")
                                               .build());

    }


}
