package wepin.store.domain;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import wepin.store.entity.Blacklist;
import wepin.store.entity.QBlacklist;
import wepin.store.entity.QCode;
import wepin.store.enumeration.CodeCls;
import wepin.store.enumeration.MemberStatus;
import wepin.store.repository.BlacklistRepository;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BlacklistDomain {

    private final JPAQueryFactory     factory;
    private final BlacklistRepository blacklistRepository;

    public List<Blacklist> findAll() {
        return blacklistRepository.findAll();
    }

    public Boolean isBlacklist(String blackId) {
        return blacklistRepository.countByBlackIdAndStatusAndIsDeletedFalseAndIsActivatedTrue(blackId, MemberStatus.BLACK.getName()) > 0;
    }

    public Page<Blacklist> getBlacklist(String status, Pageable pageable) {
        QBlacklist b = QBlacklist.blacklist;
        QCode      c = QCode.code;

        JPAQuery<Blacklist> query = factory.selectFrom(b)
                                           .innerJoin(c)
                                           .on(b.reason.cdId.eq(c.cdId)
                                                            .and(b.isActivated.eq(c.isActivated))
                                                            .and(b.isDeleted.eq(c.isDeleted))
                                                            .and(c.cdClsId.eq(CodeCls.BLOCK.getName())))
                                           .where(b.isActivated.eq(true)
                                                               .and(b.isDeleted.eq(false))
                                                               .and(b.status.eq(status)));

        JPAQuery<Blacklist> pagedQuery = query.offset(pageable.getPageNumber() * pageable.getPageSize())
                                              .limit(pageable.getPageSize());

        long            total   = pagedQuery.fetchCount();
        List<Blacklist> content = pagedQuery.fetch();

        return new PageImpl<>(content, PageRequest.of(pageable.getPageNumber(), pageable.getPageSize()), total);
    }

    public void save(Blacklist blacklist) {
        try {
            blacklistRepository.save(blacklist);
        } catch (Exception e) {
            log.error(String.valueOf(e));
        }
    }


    public Optional<Blacklist> findById(String id) {
        return blacklistRepository.findById(id);
    }

}
