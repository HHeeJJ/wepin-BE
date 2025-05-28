package wepin.store.domain;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import wepin.store.entity.PushHistory;
import wepin.store.repository.PushHistoryRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class PushHistoryDomain {

    private final PushHistoryRepository pushHistoryRepository;

    public PushHistory create(PushHistory pushHistory) {
        try {
            return pushHistoryRepository.save(pushHistory);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw e;
        }
    }


    public Page<PushHistory> findByMemberId(String memberId, Pageable pageable) {
        Pageable pageableWithSort = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Direction.DESC, "createdAt"));
        return pushHistoryRepository.findByMemberId(memberId, pageableWithSort);
    }
}
