package wepin.store.service;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import wepin.store.domain.CustomPinDomain;
import wepin.store.domain.MemberCurrentPinDomain;
import wepin.store.domain.MemberCustomPinDomain;
import wepin.store.domain.MemberDomain;
import wepin.store.dto.CustomPinDto;
import wepin.store.dto.CustomPinDto.SelectDto;
import wepin.store.dto.MemberDto.ProfileDto;
import wepin.store.entity.CustomPin;
import wepin.store.entity.Member;
import wepin.store.entity.MemberCurrentPin;
import wepin.store.entity.MemberCustomPin;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CustomPinService {

    private final CustomPinDomain        customPinDomain;
    private final MemberCurrentPinDomain memberCurrentPinDomain;
    private final MemberDomain           memberDomain;
    private final MemberCustomPinDomain  memberCustomPinDomain;

    public void init() {
        List<Member> memberList = memberDomain.findAll();

        for (Member member : memberList) {
            init(member);
        }
    }


    public Page<CustomPinDto.ListDto> getCustomPins(String memberId, Pageable pageable) {
        return customPinDomain.fetchCustomPinDetails(memberId, pageable);
    }

    public void init(Member member) {
        try {
            List<CustomPin>       customPinList = customPinDomain.findByType("BASIC");
            List<MemberCustomPin> list          = new ArrayList<>();

            for (CustomPin customPin : customPinList) {
                if (customPin.getId()
                             .equals("1")) {
                    memberCurrentPinDomain.save(MemberCurrentPin.builder()
                                                                .customPin(customPin)
                                                                .member(member)
                                                                .isActivated(true)
                                                                .build());
                }


                list.add(MemberCustomPin.builder()
                                        .customPin(customPin)
                                        .member(member)
                                        .isActivated(true)
                                        .build());
            }

            memberCustomPinDomain.saveAll(list);
        } catch (Exception e) {
            log.error("커스텀 핀 초기화 중 오류 발생", e);
        }
    }

    public ProfileDto findCurrentPinByMemberId(String memberId) {

        MemberCurrentPin memberCurrentPin = memberCurrentPinDomain.findByMemberId(memberId)
                                                                  .orElse(null);

        return ProfileDto.builder()
                         .memberId(memberId)
                         .curMainUrl(memberCurrentPin.getCustomPin()
                                                     .getMainUrl())
                         .curSubUrl(memberCurrentPin.getCustomPin()
                                                    .getSubUrl())
                         .build();


    }


    public Optional<MemberCurrentPin> saveCustomPin(SelectDto dto) {
        try {
            String memberId    = dto.getMemberId();
            String customPinId = dto.getCustomPinId();

            Optional<Member>           member           = memberDomain.findMemberById(memberId);
            Optional<CustomPin>        customPin        = customPinDomain.findById(customPinId);
            Optional<MemberCurrentPin> memberCurrentPin = memberCurrentPinDomain.findByMemberId(memberId);

            if (member.isPresent() && customPin.isPresent()) {

                memberCurrentPin.ifPresent(memberCurrentPinDomain::delete);

                return memberCurrentPinDomain.save(MemberCurrentPin.builder()
                                                                   .customPin(customPin.get())
                                                                   .member(member.get())
                                                                   .isActivated(true)
                                                                   .isDeleted(false)
                                                                   .build());
            } else {
                return Optional.empty();
            }
        } catch (Exception e) {
            log.error("Custom pin save error : {}", e.getMessage());
            return Optional.empty();
        }
    }
}
