package wepin.store.domain;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import wepin.store.entity.Hide;
import wepin.store.repository.HideRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class HideDomain {

    private final HideRepository hideRepository;

    public void save(Hide hide) {
        try {
            hideRepository.save(hide);
        } catch (Exception e) {
            log.error(String.valueOf(e));
        }
    }
}
