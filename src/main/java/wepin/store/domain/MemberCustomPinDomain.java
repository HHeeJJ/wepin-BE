package wepin.store.domain;

import org.springframework.stereotype.Service;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import wepin.store.entity.MemberCustomPin;
import wepin.store.repository.MemberCustomPinRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberCustomPinDomain {

    private final MemberCustomPinRepository memberCustomPinRepository;

    public List<MemberCustomPin> saveAll(List<MemberCustomPin> memberCustomPins) {
        try {
            return memberCustomPinRepository.saveAll(memberCustomPins);
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

}
