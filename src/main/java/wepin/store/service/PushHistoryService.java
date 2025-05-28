package wepin.store.service;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import javax.transaction.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import wepin.store.domain.MemberDomain;
import wepin.store.domain.PushHistoryDomain;
import wepin.store.dto.PushDto.PushHistoryDto;
import wepin.store.entity.Member;
import wepin.store.entity.PushHistory;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PushHistoryService {

    private final PushHistoryDomain pushHistoryDomain;
    private final MemberDomain      memberDomain;

    public void create(PushHistoryDto dto) throws Exception {
        try {
            Member member = memberDomain.findMemberById(dto.getMemberId())
                                        .orElse(null);
            Member sender = memberDomain.findMemberById(dto.getSenderId())
                                        .orElse(null);
            if (member != null) {
                pushHistoryDomain.create(PushHistory.builder()
                                                    .type(dto.getType())
                                                    .message(dto.getMessage())
                                                    .refId(dto.getRefId())
                                                    .sender(sender)
                                                    .member(member)
                                                    .isActivated(true)
                                                    .isDeleted(false)
                                                    .createdAt(LocalDateTime.now())
                                                    .build());
            } else {
                log.error("존재하지 않는 MEMBER ID: {}", dto.getMemberId());
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public void create(List<PushHistoryDto> list) throws Exception {
        try {
            for (PushHistoryDto dto : list) {
                create(dto);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public Page<PushHistory> findByMemberId(String memberId, Pageable pageable) {
        return pushHistoryDomain.findByMemberId(memberId, pageable);
    }
}
