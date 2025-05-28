package wepin.store.domain;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import wepin.store.entity.Log;
import wepin.store.repository.LogRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class LogDomain {

    private final LogRepository logRepository;

    public void save(Log logEntity) {
        try {
            logRepository.save(logEntity);
        } catch (Exception e) {
            log.error(String.valueOf(e));
        }
    }
}
