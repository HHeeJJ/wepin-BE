package wepin.store.domain;

import static wepin.store.entity.QMember.member;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import wepin.store.entity.Member;
import wepin.store.repository.MemberCurrentPinRepository;
import wepin.store.repository.MemberRepository;
import wepin.store.utils.CommonBuilder;

@Service
@RequiredArgsConstructor
public class MemberDomain {

    private final JPAQueryFactory            factory;
    private final MemberRepository           memberRepository;
    private final CommonBuilder              commonBuilder;
    private final MemberCurrentPinRepository memberCurrentPinRepository;

    public List<Member> findAll() {
        return memberRepository.findByIsDeletedFalseAndIsActivatedTrue();
    }


    public Page<Member> findByIdIn(List<String> ids, Pageable pageable) {
        Pageable pageableWithSort = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Direction.ASC, "nickname"));
        return memberRepository.findByIdInAndIsDeletedFalseAndIsActivatedTrue(ids, pageableWithSort);
    }

    public Optional<Member> findMemberById(String id) {
        return memberRepository.findByIdAndIsDeletedFalseAndIsActivatedTrue(id);
    }

    public void save(Member member) {
        memberRepository.save(member);
    }

    public Page<Member> findAllNotInBlacklistAndBan(String memberId, Pageable pageable) {
        JPQLQuery<Member> pagedQuery = factory.selectFrom(member)
                                              .where(buildDynamicConditions(memberId, true, true))
                                              .orderBy(getOrderSpecifier("nickname"))
                                              .offset((long) pageable.getPageNumber() * pageable.getPageSize())
                                              .limit(pageable.getPageSize());

        long         total   = pagedQuery.fetchCount();
        List<Member> content = pagedQuery.fetch();

        return new PageImpl<>(content, PageRequest.of(pageable.getPageNumber(), pageable.getPageSize()), total);
    }


    private BooleanExpression buildDynamicConditions(String memberId, boolean includeBlacklist, boolean includeBans) {
        BooleanExpression conditions = member.isActivated.eq(true)
                                                         .and(member.isDeleted.eq(false));

        if (includeBlacklist) {
            conditions = conditions.and(member.id.notIn(commonBuilder.blacklistIds(memberId)));
        }

        if (includeBans) {
            conditions = conditions.and(member.id.notIn(commonBuilder.banIds(memberId)));
        }

        return conditions;
    }

    private OrderSpecifier<?> getOrderSpecifier(String sortBy) {
        switch (sortBy) {
            case "createdAt":
                return member.createdAt.asc();
            default:
                return member.nickname.asc();
        }
    }

}
