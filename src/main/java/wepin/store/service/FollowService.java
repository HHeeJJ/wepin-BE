package wepin.store.service;


import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import wepin.store.domain.FollowDomain;
import wepin.store.domain.MemberDomain;
import wepin.store.domain.TokenDomain;
import wepin.store.dto.FollowCountDto;
import wepin.store.dto.FollowDto;
import wepin.store.dto.PushDto;
import wepin.store.entity.Follow;
import wepin.store.entity.Member;
import wepin.store.entity.Token;
import wepin.store.enumeration.HistoryType;
import wepin.store.mapper.FollowMapper;
import wepin.store.repository.FollowRepository;
import wepin.store.repository.MemberRepository;


@Slf4j
@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;
    private final MemberRepository memberRepository;
    private final FollowDomain     followDomain;
    private final MemberDomain     memberDomain;
    private final FollowMapper     followMapper;
    private final TokenDomain      tokenDomain;
    private final FCMService       fcmService;
    private final PushService      pushService;

    public List<Follow> getFollowerList(String followingId) {
        return followRepository.findByFollowingId(followingId);
    }

    public List<Follow> getFollowingList(String followerId) {
        return followRepository.findByFollowerId(followerId);
    }

    public FollowDto create(FollowDto dto) throws Exception {
        Optional<Member> follower  = memberRepository.findByIdAndIsDeletedFalseAndIsActivatedTrue(dto.getFollowerId());
        Optional<Member> following = memberRepository.findByIdAndIsDeletedFalseAndIsActivatedTrue(dto.getFollowingId());

        if (follower.isPresent() && following.isPresent()) {
            followRepository.save(Follow.builder()
                                        .follower(follower.get())
                                        .following(following.get())
                                        .isActivated(true)
                                        .isDeleted(false)
                                        .createdAt(LocalDateTime.now())
                                        .updatedAt(LocalDateTime.now())
                                        .build());

            return FollowDto.builder()
                            .followerId(follower.get()
                                                .getId())
                            .followingId(following.get()
                                                  .getId())
                            .nickname(following.get()
                                               .getNickname())
                            .build();
        } else {
            throw new Exception("팔로워 팔로잉 둘 중 하나가 존재하지 않습니다.");
        }
    }

    public FollowDto delete(String followerId, String followingId) throws Exception {
        Optional<Follow> follow    = followRepository.findByFollowerIdAndFollowingId(followerId, followingId);
        Optional<Member> following = memberRepository.findByIdAndIsDeletedFalseAndIsActivatedTrue(followingId);

        if (follow.isPresent()) {
            followRepository.delete(follow.get());

            return FollowDto.builder()
                            .followerId(followerId)
                            .followingId(followingId)
                            .nickname(following.get()
                                               .getNickname())
                            .build();
        } else {
            throw new Exception("언팔로우 중 에러가 발생했습니다.");
        }
    }

    public Long countByFollowingId(String memberId, String myId) {
        return followMapper.countByFollowingId(FollowCountDto.builder()
                                                             .memberId(memberId)
                                                             .myId(myId)
                                                             .build());
    }

    public Long countByFollowerId(String memberId, String myId) {
        return followMapper.countByFollowerId(FollowCountDto.builder()
                                                            .memberId(memberId)
                                                            .myId(myId)
                                                            .build());
    }

    public Page<Follow> getFollowerInfoList(String followerId, String myId, Pageable pageable) {
        return followDomain.getFollowingList(followerId, myId, pageable);
    }

    public Page<Follow> getFollowingInfoList(String followingId, String myId, Pageable pageable) {
        return followDomain.getFollowerList(followingId, myId, pageable);
    }


    public String sendMessage(String followerId, String nickNm, String type) throws Exception {
        String  message = StringUtils.EMPTY;
        boolean isSend  = false;
        Token   token   = tokenDomain.findByMemberId(followerId);
        PushDto push    = pushService.getPushState(followerId);

        if (push != null) {
            isSend = push.isAllActivated() ? true : push.isFollowActivated();
        }

        if (token != null && isSend) {
            if (type.equals(HistoryType.FOLLOW.getName())) {
                message = String.format("%s님이 회원님을 팔로우하기 시작했습니다.", nickNm);
            } else {
                message = String.format("%s님이 회원님의 팔로우를 취소했습니다.", nickNm);
            }

            try {
                fcmService.sendMessage(message, token.getToken());
            } catch (Exception e) {
                throw new Exception(e.getMessage());
            }
        } else {
            log.error("토큰이 존재하지 않아 알림 미발송 memberId : {}", followerId);
        }
        return message;
    }

}
