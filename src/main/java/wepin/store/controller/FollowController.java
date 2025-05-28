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
import wepin.store.dto.CommonPage;
import wepin.store.dto.CommonResponse;
import wepin.store.dto.FollowDto;
import wepin.store.dto.PushDto.PushHistoryDto;
import wepin.store.entity.Follow;
import wepin.store.enumeration.HistoryType;
import wepin.store.enumeration.StatusCode;
import wepin.store.service.FollowService;
import wepin.store.service.MemberService;
import wepin.store.service.PushHistoryService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/follow")
@Slf4j
@CrossOrigin
public class FollowController {

    private final FollowService      followService;
    private final MemberService      memberService;
    private final PushHistoryService pushHistoryService;

    @ApiOperation(value = "팔로우", notes = "팔로우")
    @PostMapping({"/create"})
    public ResponseEntity<CommonResponse> follow(@RequestBody FollowDto dto) throws Exception {

        if (!ObjectUtils.isEmpty(dto)) {
            FollowDto follow = followService.create(dto);

            try {
                String message = followService.sendMessage(follow.getFollowerId(), follow.getNickname(), HistoryType.FOLLOW.getName());
                if (!StringUtils.isEmpty(message)) {
                    pushHistoryService.create(PushHistoryDto.builder()
                                                            .message(message)
                                                            .memberId(follow.getFollowerId())
                                                            .senderId(follow.getFollowingId())
                                                            .refId(follow.getFollowingId())
                                                            .type(HistoryType.FOLLOW.getName())
                                                            .build());
                }

            } catch (Exception e) {
                log.error("팔로우 알림 발송 실패 followerId : {}", follow.getFollowerId(), e);
            }
            return ResponseEntity.ok(CommonResponse.builder()
                                                   .statusCode(StatusCode.SUCCESS)
                                                   .message("팔로우")
                                                   .data(memberService.getUserMain(dto.getFollowerId(), dto.getFollowingId()))
                                                   .build());
        } else {
            return ResponseEntity.ok(CommonResponse.builder()
                                                   .statusCode(StatusCode.NOT_FOUND)
                                                   .build());
        }
    }


    @ApiOperation(value = "언팔로우", notes = "언팔로우")
    @PostMapping({"/delete"})
    public ResponseEntity<CommonResponse> unFollow(@RequestBody FollowDto dto) throws Exception {

        String followerId  = dto.getFollowerId();
        String followingId = dto.getFollowingId();

        if (!StringUtils.isEmpty(followerId) && !StringUtils.isEmpty(followingId)) {
            FollowDto follow = followService.delete(followerId, followingId);
            try {
                String message = followService.sendMessage(follow.getFollowerId(), follow.getNickname(), HistoryType.UNFOLLOW.getName());
                if (!StringUtils.isEmpty(message)) {
                    pushHistoryService.create(PushHistoryDto.builder()
                                                            .message(message)
                                                            .memberId(follow.getFollowerId())
                                                            .senderId(follow.getFollowingId())
                                                            .refId(follow.getFollowingId())
                                                            .type(HistoryType.UNFOLLOW.getName())
                                                            .build());
                }

            } catch (Exception e) {
                log.error("팔로우 알림 발송 실패 followerId : {}", follow.getFollowerId(), e);
            }
            return ResponseEntity.ok(CommonResponse.builder()
                                                   .statusCode(StatusCode.SUCCESS)
                                                   .message("언팔로우")
                                                   .data(memberService.getUserMain(dto.getFollowerId(), dto.getFollowingId()))
                                                   .build());
        } else {
            return ResponseEntity.ok(CommonResponse.builder()
                                                   .statusCode(StatusCode.NOT_FOUND)
                                                   .build());
        }
    }


    @ApiOperation(value = "팔로잉 리스트", notes = "")
    @GetMapping({"/following-list"})
    public ResponseEntity<CommonResponse> getFollowingList(@ApiParam(name = "followingId", value = "팔로잉 ID") @RequestParam String followingId,
                                                           @RequestParam String myId, Pageable pageable) throws Exception {

        if (!StringUtils.isEmpty(followingId)) {
            Page<Follow> memberPage = followService.getFollowingInfoList(followingId, myId, pageable);

            return ResponseEntity.ok(CommonResponse.builder()
                                                   .statusCode(StatusCode.SUCCESS)
                                                   .message("팔로잉 리스트")
                                                   .data(CommonPage.builder()
                                                                   .totalCount(memberPage.getTotalElements())
                                                                   .hasNext(memberPage.hasNext())
                                                                   .pageNumber(memberPage.getNumber())
                                                                   .lists(memberPage.getContent()
                                                                                    .stream()
                                                                                    .map(m -> FollowDto.builder()
                                                                                                       .memberId(m.getFollower()
                                                                                                                  .getId())
                                                                                                       .name(m.getFollower()
                                                                                                              .getName())
                                                                                                       .profile(m.getFollower()
                                                                                                                 .getProfile())
                                                                                                       .intro(m.getFollower()
                                                                                                               .getIntroduce())
                                                                                                       .nickname(m.getFollower()
                                                                                                                  .getNickname())
                                                                                                       .build())
                                                                                    .collect(Collectors.toList()))
                                                                   .build())
                                                   .build());
        } else {
            return ResponseEntity.ok(CommonResponse.builder()
                                                   .statusCode(StatusCode.NOT_FOUND)
                                                   .message("팔로잉 ID가 없습니다.")
                                                   .build());
        }
    }

    @ApiOperation(value = "팔로워 리스트", notes = "")
    @GetMapping({"/follower-list"})
    public ResponseEntity<CommonResponse> getFollowerList(@ApiParam(name = "followerId", value = "팔로워 ID") @RequestParam String followerId,
                                                          @RequestParam String myId, Pageable pageable) {

        if (!StringUtils.isEmpty(followerId)) {
            Page<Follow> memberPage = followService.getFollowerInfoList(followerId, myId, pageable);

            return ResponseEntity.ok(CommonResponse.builder()
                                                   .statusCode(StatusCode.SUCCESS)
                                                   .message("팔로워 리스트")
                                                   .data(CommonPage.builder()
                                                                   .totalCount(memberPage.getTotalElements())
                                                                   .hasNext(memberPage.hasNext())
                                                                   .pageNumber(memberPage.getNumber())
                                                                   .lists(memberPage.getContent()
                                                                                    .stream()
                                                                                    .map(m -> FollowDto.builder()
                                                                                                       .memberId(m.getFollowing()
                                                                                                                  .getId())
                                                                                                       .name(m.getFollowing()
                                                                                                              .getName())
                                                                                                       .profile(m.getFollowing()
                                                                                                                 .getProfile())
                                                                                                       .intro(m.getFollowing()
                                                                                                               .getIntroduce())
                                                                                                       .nickname(m.getFollowing()
                                                                                                                  .getNickname())
                                                                                                       .build())
                                                                                    .collect(Collectors.toList()))
                                                                   .build())
                                                   .build());
        } else {
            return ResponseEntity.ok(CommonResponse.builder()
                                                   .statusCode(StatusCode.NOT_FOUND)
                                                   .message("팔로워 ID가 없습니다.")
                                                   .build());
        }
    }
}