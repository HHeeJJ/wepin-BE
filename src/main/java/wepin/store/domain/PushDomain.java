package wepin.store.domain;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import wepin.store.entity.Push;
import wepin.store.repository.PushRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class PushDomain {

    private final PushRepository pushRepository;

    public Push save(Push push) {
        try {
            return pushRepository.save(push);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    public Push findByMemberId(String memberId) {
        return pushRepository.findByMemberId(memberId)
                             .orElse(null);
    }

    public void delete(Push push) {
        try {
            pushRepository.delete(push);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw e;
        }
    }
}
