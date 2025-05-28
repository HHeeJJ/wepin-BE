package wepin.store.service;


import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import javax.transaction.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import wepin.store.domain.LogDomain;
import wepin.store.dto.LogDto;
import wepin.store.entity.Log;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class LogService {

    private final LogDomain logDomain;

    // 주석
    public void create(LogDto dto) {
        try {
            logDomain.save(Log.builder()
                              .memberId(dto.getMemberId())
                              .name(dto.getName())
                              .loc(dto.getLoc())
                              .message(dto.getMessage())
                              .type(dto.getType())
                              .isActivated(true)
                              .createdAt(LocalDateTime.now())
                              .build());

        } catch (Exception e) {
            log.error(String.valueOf(e));
        }
    }


}
