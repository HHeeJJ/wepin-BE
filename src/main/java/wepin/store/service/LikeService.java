package wepin.store.service;


import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

import javax.transaction.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import wepin.store.domain.TokenDomain;
import wepin.store.dto.LikeDto;
import wepin.store.dto.PushDto;
import wepin.store.entity.Feed;
import wepin.store.entity.Likes;
import wepin.store.entity.Member;
import wepin.store.entity.Token;
import wepin.store.repository.LikeRepository;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class LikeService {

    private final LikeRepository likeRepository;

    private final MemberService memberService;
    private final FeedService   feedService;
    private final FCMService    fcmService;
    private final PushService   pushService;
    private final TokenDomain   tokenDomain;

    public LikeDto createLike(String memberId, String feedId) throws Exception {

        try {
            Optional<Member> member = memberService.findByIdAndIsDeletedFalseAndIsActivatedTrue(memberId);
            Optional<Feed>   feed   = feedService.findByIdAndIsDeletedFalseAndIsActivatedTrue(feedId);

            if (feed.isPresent() && member.isPresent()) {
                likeRepository.save(Likes.builder()
                                         .feed(feed.get())
                                         .member(member.get())
                                         .isActivated(true)
                                         .isDeleted(false)
                                         .createdAt(LocalDateTime.now())
                                         .updatedAt(LocalDateTime.now())
                                         .build());

                return LikeDto.builder()
                              .count(likeRepository.countByFeedId(feedId))
                              .feedId(feedId)
                              .memberId(feed.get()
                                            .getMember()
                                            .getId())
                              .pushSenderId(memberId)
                              .nickNm(member.get()
                                            .getNickname())
                              .build();
            } else {
                throw new Exception("Feed 또는 Member가 존재하지 않습니다.");
            }
        } catch (Exception e) {
            throw e;
        }
    }

    public LikeDto deleteLike(String memberId, String feedId) {
        try {
            Optional<Likes>  likes  = likeRepository.findByMemberIdAndFeedId(memberId, feedId);
            Optional<Feed>   feed   = feedService.findByIdAndIsDeletedFalseAndIsActivatedTrue(feedId);
            Optional<Member> member = memberService.findByIdAndIsDeletedFalseAndIsActivatedTrue(memberId);

            if (likes.isPresent()) {
                likeRepository.delete(likes.get());

                return LikeDto.builder()
                              .count(likeRepository.countByFeedId(feedId))
                              .feedId(feedId)
                              .memberId(feed.map(value -> value.getMember()
                                                               .getId())
                                            .orElse(null))
                              .pushSenderId(memberId)
                              .nickNm(member.map(Member::getNickname)
                                            .orElse(null))
                              .build();
            } else {
                log.error("좋아요 취소 중 에러가 발생했습니다.");
                return null;
            }
        } catch (Exception e) {
            log.error(String.valueOf(e));
            return null;
        }
    }

    public String sendMessage(String feedId, String nickNm, String type) throws Exception {
        String message = StringUtils.EMPTY;
        Feed feed = feedService.findByIdAndIsDeletedFalseAndIsActivatedTrue(feedId)
                               .orElse(null);

        if (feed != null) {
            String memberId = feed.getMember()
                                  .getId();
            boolean isSend = false;
            Token   token  = tokenDomain.findByMemberId(memberId);
            PushDto push   = pushService.getPushState(memberId);

            if (push != null) {
                log.info("[푸시 설정 로직] isAllActivated : {} , isLikeActivated : {}", push.isAllActivated(), push.isLikeActivated());
                isSend = push.isAllActivated() || push.isLikeActivated();
            }

            if (token != null && isSend) {
                switch (type) {
                    case "LIKE":
                        message = String.format("%s님이 회원님의 게시물을 좋아합니다.", nickNm);
                        break;
                    case "DISLIKE":
                        message = String.format("%s님이 회원님의 게시물을 좋아했다가 안좋아합니다~^^", nickNm);
                        break;
                }

                log.info("[푸시 설정 로직] token : {}", token.getToken());
                fcmService.sendMessage(message, token.getToken());
            } else {
                log.error("토큰이 존재하지 않아 알림 미발송 memberId : {}, Token : {}, isSend : {}", memberId, token, isSend);
            }
        }
        return message;
    }
}
