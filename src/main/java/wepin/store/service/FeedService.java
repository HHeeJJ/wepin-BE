package wepin.store.service;


import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import wepin.store.domain.FeedDomain;
import wepin.store.domain.FeedPinDomain;
import wepin.store.domain.MemberCurrentPinDomain;
import wepin.store.domain.MemberDomain;
import wepin.store.domain.PinDomain;
import wepin.store.domain.ReplyDomain;
import wepin.store.domain.TokenDomain;
import wepin.store.dto.FcmSendDto.PutDataDto;
import wepin.store.dto.FeedDto;
import wepin.store.dto.FeedDto.DetailDto;
import wepin.store.dto.FeedDto.FeedMainDto;
import wepin.store.dto.FeedDto.MainDto;
import wepin.store.dto.PinDto;
import wepin.store.dto.PushDto;
import wepin.store.dto.PushDto.PushHistoryDto;
import wepin.store.dto.ReplyDto;
import wepin.store.dto.ReplyDto.FeedDetailReplyDto;
import wepin.store.entity.Feed;
import wepin.store.entity.Image;
import wepin.store.entity.Member;
import wepin.store.entity.MemberCurrentPin;
import wepin.store.entity.Reply;
import wepin.store.entity.Token;
import wepin.store.enumeration.HistoryType;
import wepin.store.repository.FeedRepository;
import wepin.store.repository.MemberRepository;
import wepin.store.utils.DetailedException;
import wepin.store.utils.DetailedException.ErrorCode;
import wepin.store.utils.S3UploadUtils;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class FeedService {

    private final FeedRepository         feedRepository;
    private final MemberRepository       memberRepository;
    private final PinService             pinService;
    private final S3UploadUtils          s3UploadUtils;
    private final FeedDomain             feedDomain;
    private final ReplyDomain            replyDomain;
    private final FeedTagService         feedTagService;
    private final FollowService          followService;
    private final PushService            pushService;
    private final FCMService             fcmService;
    private final TokenDomain            tokenDomain;
    private final MemberCurrentPinDomain memberCurrentPinDomain;
    private final MemberDomain           memberDomain;
    private final FeedPinDomain          feedPinDomain;

    public MainDto createFeed(FeedDto.SaveDto dto) throws DetailedException {
        try {
            Optional<Member> memberOptional = memberDomain.findMemberById(dto.getMemberId());

            if (memberOptional.isEmpty()) {
                throw new DetailedException(DetailedException.ErrorCode.MEMBER_NOT_FOUND, "멤버를 찾을 수 없음: " + dto.getMemberId());
            }

            Member member = memberOptional.get();

            MemberCurrentPin memberCurrentPin = memberCurrentPinDomain.findByMemberId(member.getId())
                                                                      .orElse(null);

            String mainImgUrl;

            if (!StringUtils.isEmpty(dto.getImg())) {
                try {
                    mainImgUrl = s3UploadUtils.upload(dto.getImg(), "feed", dto.getMemberId() + "_" + LocalDateTime.now());
                } catch (Exception e) {
                    throw new DetailedException(DetailedException.ErrorCode.IMAGE_UPLOAD_FAILED, "이미지 업로드 실패", e);
                }

                Feed feed = feedDomain.save(Feed.builder()
                                                .id(UUID.randomUUID().toString())
                                                .member(member)
                                                .description(dto.getDesc())
                                                .title(dto.getTitle())
                                                .zoomLevel(dto.getZoomLevel())
                                                .customPin(memberCurrentPin.getCustomPin())
                                                .imgUrl(mainImgUrl)
                                                .isActivated(true)
                                                .isDeleted(false)
                                                .createdAt(LocalDateTime.now())
                                                .updatedAt(LocalDateTime.now())
                                                .build());


                List<PinDto> pinList = pinService.createPin(dto.getPinList(), feed);

                feedTagService.create(dto.getTagList(), feed);

                MainDto mainDto = MainDto.builder()
                    .memberId(member.getId())
                    .nickname(member.getNickname())
                    .id(feed.getId())
                    .pinList(pinList)
                    .build();

                return mainDto;
            } else {
                throw new DetailedException(DetailedException.ErrorCode.MISSING_IMAGE_INFO, "이미지 정보가 없음" + "img URL [" + dto.getImg() + "]");
            }
        } catch (Exception e) {
            throw new DetailedException(ErrorCode.UNKNOWN_ERROR, "알 수 없는 에러 발생", e);
        }
    }

    public MainDto merge(FeedDto.SaveDto dto) throws DetailedException {
        try {
            Optional<Member> memberOptional = memberDomain.findMemberById(dto.getMemberId());

            if (memberOptional.isEmpty()) {
                throw new DetailedException(DetailedException.ErrorCode.MEMBER_NOT_FOUND, "멤버를 찾을 수 없음: " + dto.getMemberId());
            }

            Member member = memberOptional.get();
            String mainImgUrl;

            MemberCurrentPin memberCurrentPin = memberCurrentPinDomain.findByMemberId(member.getId())
                                                                      .orElse(null);

            if (!StringUtils.isEmpty(dto.getImg())) {
                try {
                    mainImgUrl = s3UploadUtils.upload(dto.getImg(), "feed", dto.getMemberId() + "_" + LocalDateTime.now());
                } catch (Exception e) {
                    throw new DetailedException(DetailedException.ErrorCode.IMAGE_UPLOAD_FAILED, "이미지 업로드 실패", e);
                }

                Feed feed = feedDomain.save(Feed.builder()
                                                .member(member)
                                                .description(dto.getDesc())
                                                .title(dto.getTitle())
                                                .zoomLevel(dto.getZoomLevel())
                                                .customPin(memberCurrentPin.getCustomPin())
                                                .imgUrl(mainImgUrl)
                                                .isActivated(true)
                                                .isDeleted(false)
                                                .createdAt(LocalDateTime.now())
                                                .updatedAt(LocalDateTime.now())
                                                .build());

                List<PinDto> pinList = pinService.merge(dto.getPinList(), feed);
                feedTagService.create(dto.getTagList(), feed);

                return MainDto.builder()
                              .memberId(member.getId())
                              .nickname(member.getNickname())
                              .id(feed.getId())
                              .pinList(pinList)
                              .build();
            } else {
                throw new DetailedException(DetailedException.ErrorCode.MISSING_IMAGE_INFO, "이미지 정보가 없음" + "img URL [" + dto.getImg() + "]");
            }
        } catch (Exception e) {
            throw new DetailedException(ErrorCode.UNKNOWN_ERROR, "알 수 없는 에러 발생", e);
        }
    }

    public Page<Feed> getMyFeedList(String memberId, Pageable pageable) {
        try {
            return feedDomain.getMyFeedList(memberId, pageable);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw e;
        }
    }


    public void deleteFeed(String feedId) {
        try {
            Optional<Feed> feed = feedDomain.findById(feedId);

            if (feed.isPresent()) {
                feed.get()
                    .delete();
                feedDomain.save(feed.get());
                feedPinDomain.deleteAll(feed.get()
                                            .getFeedPinList());

                replyDomain.deleteAll(feedId);
                feedTagService.deleteAll(feed.get());
            } else {
                log.error("존재하지 않는 피드입니다.");
            }
        } catch (Exception e) {
            log.error(String.valueOf(e));
            throw e;
        }
    }


    public void disabled(String feedId) {
        //        try {
        //            Optional<Feed> feed = feedDomain.findById(feedId);
        //
        //            if (feed.isPresent()) {
        //                feed.get()
        //                    .deactivate();
        //                feedDomain.save(feed.get());
        //                feedPinDomain.deleteAll();
        //
        //                pinService.deletePin(feed.get());
        //
        //                replyDomain.deleteAll(feedId);
        //
        //            } else {
        //                log.error("존재하지 않는 피드입니다.");
        //            }
        //        } catch (Exception e) {
        //            log.error(String.valueOf(e));
        //            throw e;
        //        }

        
    }

    public DetailDto getFeedDetail(String feedId, String memberId, Pageable pageable) {
        Feed feed = feedDomain.findById(feedId)
                              .orElse(null);

        if (feed != null) {
            String      id        = feed.getId();
            Page<Reply> replyPage = replyDomain.getReplyListByFeedIdNotInBlacklistAndBan(id, memberId, pageable);

            DetailDto dto = DetailDto.toDto(feed, memberId);
            dto.setReplyDto(FeedDetailReplyDto.builder()
                                              .replyList(replyPage.getContent()
                                                                  .stream()
                                                                  .map(m -> ReplyDto.builder()
                                                                                    .id(m.getId())
                                                                                    .feedId(feedId)
                                                                                    .memberId(m.getMember()
                                                                                               .getId())
                                                                                    .nickNm(m.getMember()
                                                                                             .getNickname())
                                                                                    .desc(m.getDescription())
                                                                                    .createdAt(m.getCreatedAt()
                                                                                                .format(DateTimeFormatter.ofPattern(
                                                                                                        "yyyy-MM-dd HH:mm:ss")))
                                                                                    .profile(m.getMember()
                                                                                              .getProfile())
                                                                                    .build())
                                                                  .collect(Collectors.toList()))
                                              .totalCount(replyPage.getTotalElements())
                                              .hasNext(replyPage.hasNext())
                                              .pageNumber(replyPage.getNumber())
                                              .build());
            return dto;
        }

        return null;
    }

    public Page<Feed> getFeedListByTag(String tag, String memberId, Pageable pageable) {
        return feedDomain.getFeedListByTag(tag, memberId, pageable);


        //        return feedDomain.getFeedListByTag(tag, memberId, pageable)
        //                         .stream()
        //                         .map(m -> FeedDto.FeedMainDto.builder()
        //                                                      .feedId(m.getId())
        //                                                      .pinCnt(m.getPinList()
        //                                                               .size())
        //                                                      .title(m.getTitle())
        //                                                      .imgUrl(m.getImgUrl())
        //                                                      .build())
        //                         .collect(Collectors.toList());
    }


    public List<MainDto> getMainFeedListByFollower(List<String> followerList, String memberId) {
        Pageable   pageRequest = PageRequest.of(0, 5); // Pageable을 사용
        Page<Feed> feedPage    = feedRepository.findByMemberIdInAndIsDeletedFalseAndIsActivatedTrueOrderByCreatedAtDesc(followerList, pageRequest);
        List<Feed> feedList    = feedPage.getContent(); // 페이지에서 데이터 추출
        return toDto(feedList, true, memberId);
    }

    public List<MainDto> getMainFeedListByNonFollower(List<String> followerList, String memberId) {
        return toDto(feedRepository.findByMemberIdNotInAndIsDeletedFalseAndIsActivatedTrueOrderByCreatedAtDesc(followerList), false, memberId);
    }

    public Page<Feed> getMainFeedList(String memberId, Pageable pageable) {
        return feedDomain.getMainFeedListForPageByBeginner(pageable);
    }

    public Page<Feed> list(String memberId, Pageable pageable) {
        return feedDomain.list(memberId, pageable);
    }

    //    public List<MainDto> getMyFeedList(String memberId) {
    //        return toDto(feedRepository.findByMemberIdAndIsDeletedFalseAndIsActivatedTrue(memberId), false, memberId);
    //    }

    public List<MainDto> toDto(List<Feed> list, Boolean isFollow, String memberId) {
        return list.stream()
                   .map(m -> MainDto.builder()
                                    .id(m.getId())
                                    .title(m.getTitle())
                                    .desc(m.getDescription())
                                    .imgUrl(m.getImgUrl())
                                    .profile(m.getMember()
                                              .getProfile())
                                    .memberId(m.getMember()
                                               .getId())
                                    .isMine(m.getMember()
                                             .getId()
                                             .equals(memberId))
                                    .isFollow(isFollow)
                                    .createdAt(m.getCreatedAt())
                                    .nickname(m.getMember()
                                               .getNickname())
                                    //                                    .pinList(m.getPinList()
                                    //                                              .stream()
                                    //                                              .sorted(Comparator.comparing(Pin::getUiSeq))
                                    //                                              .map(p -> PinDto.builder()
                                    //                                                              .lng(p.getLongitude())
                                    //                                                              .feedId(p.getFeed()
                                    //                                                                       .getId())
                                    //                                                              .isMain(p.getIsMain())
                                    //                                                              .lat(p.getLatitude())
                                    //                                                              .addr(p.getAddress())
                                    //                                                              .addrDetail(p.getAddressDetail())
                                    //                                                              .id(p.getId())
                                    //                                                              .name(p.getName())
                                    //                                                              .uiSeq(p.getUiSeq())
                                    //                                                              .imageList(p.getImageList()
                                    //                                                                          .stream()
                                    //                                                                          .sorted(Comparator.comparing(Image::getUiSeq))
                                    //                                                                          .map(i -> ImageDto.builder()
                                    //                                                                                            .id(i.getId())
                                    //                                                                                            .pinId(i.getPin()
                                    //                                                                                                    .getId())
                                    //                                                                                            .imgUrl(i.getImage())
                                    //                                                                                            .uiSeq(i.getUiSeq())
                                    //                                                                                            .build())
                                    //                                                                          .collect(Collectors.toList()))
                                    //                                                              .build())
                                    //                                              .collect(Collectors.toList()))
                                    //                                    .likeDTO(LikeDto.FeedInLikeDto.builder()
                                    //                                                                  .count(m.getLikeList()
                                    //                                                                          .size())
                                    //                                                                  .isLike(m.getLikeList()
                                    //                                                                           .stream()
                                    //                                                                           .anyMatch(l -> l.getMember()
                                    //                                                                                           .getId()
                                    //                                                                                           .equals(memberId) && l.getFeed()
                                    //                                                                                                                 .getId()
                                    //                                                                                                                 .equals(m.getId())))
                                    //                                                                  .build())
                                    //                                    .replyList(m.getReplyList()
                                    //                                                .stream()
                                    //                                                .sorted(Comparator.comparing(Reply::getCreatedAt))
                                    //                                                .map(r -> ReplyDto.builder()
                                    //                                                                  .feedId(r.getFeed()
                                    //                                                                           .getId())
                                    //                                                                  .desc(r.getDescription())
                                    //                                                                  .isLike(r.getIsLike())
                                    //                                                                  .uiSeq(r.getUiSeq())
                                    //                                                                  .id(r.getId())
                                    //                                                                  .memberId(r.getMember()
                                    //                                                                             .getId())
                                    //                                                                  .nickNm(r.getMember()
                                    //                                                                           .getNickname())
                                    //                                                                  .profile(r.getMember()
                                    //                                                                            .getProfile())
                                    //                                                                  .build())
                                    //                                                .collect(Collectors.toList()))
                                    .build())
                   .collect(Collectors.toList());
    }

    public List<FeedMainDto> getFeedMain(String memberId, String myId) {
        try {
            return feedDomain.getUserMainFeedList(memberId, myId)
                             .stream()
                             .map(m -> FeedDto.FeedMainDto.builder()
                                                          .feedId(m.getId())
                                                          .imgUrl(m.getFeedPinList()
                                                                   .size() == 1 && m.getFeedPinList()
                                                                                    .get(0)
                                                                                    .getPin()
                                                                                    .getImageList() != null && !m.getFeedPinList()
                                                                                                                 .get(0)
                                                                                                                 .getPin()
                                                                                                                 .getImageList()
                                                                                                                 .isEmpty() ? m.getFeedPinList()
                                                                                                                               .get(0)
                                                                                                                               .getPin()
                                                                                                                               .getImageList()
                                                                                                                               .stream()
                                                                                                                               .min(Comparator.comparing(
                                                                                                                                       Image::getUiSeq))
                                                                                                                               .map(Image::getImage)
                                                                                                                               .orElse(m.getImgUrl()) : m.getImgUrl())
                                                          .title(m.getTitle())
                                                          .pinCnt(m.getFeedPinList()
                                                                   .size())
                                                          .build())
                             .collect(Collectors.toList());
        } catch (Exception e) {
            log.error(String.valueOf(e));
            return null;
        }
    }

    public Optional<Feed> findByIdAndIsDeletedFalseAndIsActivatedTrue(String id) {
        return feedDomain.findById(id);
    }


    public Long countByMemberIdAndIsDeletedFalseAndIsActivatedTrue(String memberId) {
        return feedRepository.countByMemberIdAndIsDeletedFalseAndIsActivatedTrue(memberId);
    }


    public List<PushHistoryDto> sendMessage(String memberId, String nickNm, MainDto feed) {
        String               message;
        boolean              isSend  = false;
        List<PushHistoryDto> dtoList = new ArrayList<>();
        List<PinDto>         pinList = feed.getPinList();

        List<String> followers = followService.getFollowingList(memberId)
                                              .stream()
                                              .map(m -> m.getFollowing()
                                                         .getId())
                                              .collect(Collectors.toList());

        for (String follower : followers) {
            try {
                PushDto push = pushService.getPushState(follower);
                if (push != null) {
                    isSend = push.isAllActivated() ? true : push.isFeedActivated();
                }
                Token token = tokenDomain.findByMemberId(follower);

                if (token != null && isSend) {
                    if (pinList.size() > 1) {
                        String pinName = pinList.stream()
                                                .filter(PinDto::getIsMain)
                                                .map(PinDto::getName)
                                                .findFirst()
                                                .orElse("메인");

                        message = String.format("%s님이 %s핀 외 %d개의 핀을 공유했습니다.", nickNm, pinName, pinList.size() - 1);

                        fcmService.sendMessage(message, token.getToken(), Collections.singletonList(PutDataDto.builder()
                                                                                                              .key("feedId")
                                                                                                              .value(feed.getId())
                                                                                                              .build()));
                    } else {
                        message = String.format("%s님이 새로운 게시물을 공유했습니다.", nickNm);

                        fcmService.sendMessage(message, token.getToken(), Collections.singletonList(PutDataDto.builder()
                                                                                                              .key("feedId")
                                                                                                              .value(feed.getId())
                                                                                                              .build()));
                    }
                    log.info("message: {}", message);
                    dtoList.add(PushHistoryDto.builder()
                                              .message(message)
                                              .memberId(follower)
                                              .senderId(feed.getMemberId())
                                              .refId(feed.getId())
                                              .type(HistoryType.FEED_CREATE.getName())
                                              .build());
                } else {
                    log.info("토큰이 존재하지 않아 알림 미발송 memberId : {}", follower);
                }
            } catch (Exception e) {
                log.info("알림 발송 중 오류 발생 memberId: {} 오류: {}", follower, e.getMessage());
            }
        }

        return dtoList;
    }

    public void migrate() {
        //        List<Feed>    feedList    = feedDomain.findAll();
        //        List<FeedPin> feedPinList = new ArrayList<>();
        //
        //        feedList.forEach(m -> {
        //            pinDomain.findByFeedId(m.getId())
        //                     .forEach(p -> {
        //                         feedPinList.add(FeedPin.builder()
        //                                                .pin(p)
        //                                                .feed(m)
        //                                                .isMain(p.getIsMain())
        //                                                .uiSeq(p.getUiSeq())
        //                                                .isActivated(true)
        //                                                .isDeleted(false)
        //                                                .build());
        //                     });
        //        });
        //
        //        feedPinDomain.saveAll(feedPinList);
    }

}
