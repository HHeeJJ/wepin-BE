package wepin.store.domain;

import org.springframework.stereotype.Service;

import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import wepin.store.entity.MemberCurrentPin;
import wepin.store.repository.MemberCurrentPinRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberCurrentPinDomain {

    private final MemberCurrentPinRepository memberCurrentPinRepository;

    public Optional<MemberCurrentPin> findByMemberId(String memberId) {
        return memberCurrentPinRepository.findByMemberId(memberId);
    }

    public void delete(String memberId) {
        Optional<MemberCurrentPin> memberCurrentPin = memberCurrentPinRepository.findByMemberId(memberId);

        memberCurrentPin.ifPresent(memberCurrentPinRepository::delete);
    }

    public MemberCurrentPin findCurrentPinByMemberId(String memberId) {
        return memberCurrentPinRepository.findByMemberId(memberId)
                                         .orElse(null);
    }

    public void delete(MemberCurrentPin memberCurrentPin) {
        memberCurrentPinRepository.delete(memberCurrentPin);
    }

    public Optional<MemberCurrentPin> save(MemberCurrentPin memberCurrentPin) {
        return Optional.of(memberCurrentPinRepository.save(memberCurrentPin));
    }

}
