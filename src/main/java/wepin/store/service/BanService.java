package wepin.store.service;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

import javax.transaction.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import wepin.store.domain.BanDomain;
import wepin.store.domain.MemberDomain;
import wepin.store.dto.BanDto;
import wepin.store.entity.Ban;
import wepin.store.entity.Member;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BanService {

    private final BanDomain    banDomain;
    private final MemberDomain memberDomain;

    public void create(BanDto.BanCreateDto dto) {
        try {
            if (!dto.getMemberId()
                    .isEmpty() && !dto.getBanId()
                                      .isEmpty()) {
                Optional<Member> member = memberDomain.findMemberById(dto.getMemberId());
                Optional<Member> ban    = memberDomain.findMemberById(dto.getBanId());

                if (member.isPresent() && ban.isPresent()) {
                    banDomain.save(Ban.builder()
                                      .member(member.get())
                                      .ban(ban.get())
                                      .isActivated(true)
                                      .isDeleted(false)
                                      .build());
                } else {
                    log.error("[memberId] : " + member.get()
                                                      .getId() + " [banId] : " + ban.get()
                                                                                    .getId());
                }

            } else {
                log.error("[memberId] : " + dto.getMemberId() + " [banId] : " + dto.getBanId());
            }

        } catch (Exception e) {
            log.error(String.valueOf(e));
        }
    }

    public void delete(BanDto.BanDeleteDto dto) {
        try {
            if (!dto.getId()
                    .isEmpty()) {
                Optional<Ban> ban = banDomain.findById(dto.getId());

                if (ban.isPresent()) {
                    banDomain.delete(ban.get());
                } else {
                    log.error("차단해제 실패 [id] : " + ban.get()
                                                     .getId());
                }

            } else {
                log.error("차단해제 실패 [id] : " + dto.getId());
            }

        } catch (Exception e) {
            log.error(String.valueOf(e));
        }
    }


    public Page<Ban> getBanList(String memberId, Pageable pageable) {
        return banDomain.getBanList(memberId, pageable);
    }
}
