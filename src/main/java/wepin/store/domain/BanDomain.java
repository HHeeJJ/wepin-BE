package wepin.store.domain;

import com.querydsl.jpa.impl.JPAQueryFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import wepin.store.entity.Ban;
import wepin.store.repository.BanRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class BanDomain {

    private final JPAQueryFactory factory;
    private final BanRepository   banRepository;

    public void delete(Ban ban) {
        try {
            banRepository.delete(ban);
        } catch (Exception e) {
            log.error(String.valueOf(e));
        }
    }

    public Page<Ban> getBanList(String memberId, Pageable pageable) {
        Pageable pageableWithSort = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Direction.ASC, "createdAt"));
        return banRepository.findByMemberIdAndIsActivatedTrueAndIsDeletedFalse(memberId, pageableWithSort);
    }

    public Optional<Ban> findById(String id) {
        return banRepository.findByIdAndIsActivatedTrueAndIsDeletedFalse(id);
    }

    public void save(Ban ban) {
        try {
            banRepository.save(ban);
        } catch (Exception e) {
            log.error(String.valueOf(e));
        }
    }
}
