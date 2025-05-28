package wepin.store.service;


import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Optional;

import javax.transaction.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import wepin.store.domain.ReplyDomain;
import wepin.store.domain.TokenDomain;
import wepin.store.dto.FcmSendDto.PutDataDto;
import wepin.store.dto.PushDto;
import wepin.store.dto.ReplyDto;
import wepin.store.entity.Feed;
import wepin.store.entity.Member;
import wepin.store.entity.Reply;
import wepin.store.entity.Token;
import wepin.store.repository.ReplyRepository;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ReplyService {

    private final MemberService   memberService;
    private final FeedService     feedService;
    private final ReplyRepository replyRepository;
    private final ReplyDomain     replyDomain;
    private final TokenDomain     tokenDomain;
    private final FCMService      fcmService;
    private final PushService     pushService;

    public ReplyDto create(ReplyDto.CreateDto dto) throws Exception {

        try {
            Optional<Member> member = memberService.findByIdAndIsDeletedFalseAndIsActivatedTrue(dto.getMemberId());
            Optional<Feed>   feed   = feedService.findByIdAndIsDeletedFalseAndIsActivatedTrue(dto.getFeedId());

            if (feed.isPresent() && member.isPresent()) {
                Reply reply = replyRepository.save(Reply.builder()
                                                        .feed(feed.get())
                                                        .member(member.get())
                                                        .description(dto.getDesc())
                                                        .isLike(dto.getIsLike())
                                                        .uiSeq(dto.getUiSeq())
                                                        .isActivated(true)
                                                        .isDeleted(false)
                                                        .createdAt(LocalDateTime.now())
                                                        .updatedAt(LocalDateTime.now())
                                                        .build());

                return ReplyDto.builder()
                               .profile(reply.getMember()
                                             .getProfile())
                               .id(reply.getId())
                               .desc(reply.getDescription())
                               .nickNm(reply.getMember()
                                            .getNickname())
                               .memberId(reply.getFeed()
                                              .getMember()
                                              .getId())
                               .pushSenderId(reply.getMember()
                                                  .getId())
                               .createdAt(reply.getCreatedAt()
                                               .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                               .feedId(reply.getFeed()
                                            .getId())
                               .build();
            } else {
                log.error("Feed 또는 Member가 존재하지 않습니다.");
            }
        } catch (Exception e) {
            log.error(String.valueOf(e));
            throw e;
        }
        return null;
    }

    public void delete(String id) {
        try {
            Optional<Reply> reply = replyRepository.findByIdAndIsDeletedFalseAndIsActivatedTrue(id);

            if (reply.isPresent()) {
                //                reply.get()
                //                     .delete();
                //                replyRepository.save(reply.get());
                replyRepository.delete(reply.get());
            } else {
                log.error("존재하지 않는 댓글입니다.");
            }
        } catch (Exception e) {
            log.error("댓글 삭제 중 에러가 발생했습니다. " + e);
        }

    }


    public void deleteAll(String feedId) {
        replyDomain.deleteAll(feedId);
    }

    public Page<Reply> getReplyListForPage(String feedId, Pageable pageable) {
        return replyDomain.getReplyListByFeedId(feedId, pageable);
    }


    public Page<Reply> getReplyListByFeedId(String feedId, Pageable pageable) {
        return replyDomain.getReplyListByFeedId(feedId, pageable);
    }

    public String sendMessage(String feedId, String nickNm, String reply) throws Exception {
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
                isSend = push.isAllActivated() ? true : push.isReplyActivated();
            }

            if (token != null && isSend) {
                message = String.format("%s님이 회원님의 게시물에 댓글을 달았습니다.", nickNm);
                //                fcmService.sendMessage(message, reply, token.getToken(), Collections.singletonList(PutDataDto.builder()
                //                                                                                                             .key("feedId")
                //                                                                                                             .value(feedId)
                //                                                                                                             .build()));

                fcmService.sendMessage(message, token.getToken(), Collections.singletonList(PutDataDto.builder()
                                                                                                      .key("feedId")
                                                                                                      .value(feedId)
                                                                                                      .build()));
            } else {
                log.error("토큰이 존재하지 않아 알림 미발송 memberId : {}", memberId);
            }
        }
        return message;

    }

}
