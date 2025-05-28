package wepin.store.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import javax.transaction.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import wepin.store.domain.MemberDomain;
import wepin.store.domain.PushDomain;
import wepin.store.dto.PushDto;
import wepin.store.entity.Member;
import wepin.store.entity.Push;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PushService {

    private final PushDomain   pushDomain;
    private final MemberDomain memberDomain;

    public PushDto update(PushDto dto) {
        try {
            Push push = pushDomain.findByMemberId(dto.getMemberId());
            push.update(dto);

            return PushDto.toDto(pushDomain.save(push));
        } catch (Exception e) {
            log.error("Push 설정 업데이트 중 오류 발생", e);
            return PushDto.builder()
                          .build();
        }
    }

    public void initPushState(PushDto dto) {
        if (!StringUtils.isEmpty(dto.getMemberId())) {
            Member member = memberDomain.findMemberById(dto.getMemberId())
                                        .orElse(null);
            if (member != null) {
                Push push = pushDomain.findByMemberId(member.getId());
                if (push != null) {
                    log.info("이미 초기화 정보가 존재 : memberId={}, pushId={}", dto.getMemberId(), push.getId());
                    return;
                }
                log.info("[푸시 설정 로직] dto.isAllActivated() : {}", dto.isAllActivated());
                init(member, dto.isAllActivated());
            } else {
                log.error("존재하지 않는 memberId : {}", dto.getMemberId());
            }
        } else {
            log.error("member id is empty");
        }
    }

    public void init(Member member, boolean initFlag) {
        try {
            pushDomain.save(Push.builder()
                                .isAllActivated(initFlag)
                                .isFeedActivated(initFlag)
                                .isFollowActivated(initFlag)
                                .isLikeActivated(initFlag)
                                .isReplyActivated(initFlag)
                                .isActivated(true)
                                .member(member)
                                .createdAt(LocalDateTime.now())
                                .build());
        } catch (Exception e) {
            log.error("Push 설정 저장 중 오류 발생", e);
        }
    }

    public PushDto getPushState(String memberId) throws Exception {
        Push push = pushDomain.findByMemberId(memberId);
        if (push != null) {
            return PushDto.toDto(push);
        } else {
            throw new Exception("알람 설정 상태가 존재하지 않습니다. memberId : " + memberId);
        }
    }

    public PushDto isExistPushState(String memberId) {
        Push push = pushDomain.findByMemberId(memberId);
        return PushDto.builder()
                      .isExist(push != null)
                      .build();
    }

    public void delete(Member member) throws Exception {
        try {
            Push push = pushDomain.findByMemberId(member.getId());
            pushDomain.delete(push);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
