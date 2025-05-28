package wepin.store.domain;

import static wepin.store.entity.QFollow.follow;
import static wepin.store.entity.QMember.member;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

import lombok.RequiredArgsConstructor;
import wepin.store.entity.Follow;
import wepin.store.repository.FollowRepository;
import wepin.store.utils.CommonBuilder;

@Service
@RequiredArgsConstructor
public class FollowDomain {

    private final FollowRepository followRepository;
    private final JPAQueryFactory  factory;
    private final CommonBuilder    commonBuilder;


    /**
     * 팔로우 하는 유저의  List
     *
     * @Param followerId
     */
    public Page<Follow> getFollowingList(String followerId, String myId, Pageable pageable) {
        JPQLQuery<Follow> pagedQuery = factory.selectFrom(follow)
                                              .innerJoin(member)
                                              .on(follow.following.id.eq(member.id)
                                                                     .and(follow.isActivated.eq(member.isActivated)
                                                                                            .and(follow.isDeleted.eq(member.isDeleted))))
                                              .where(follow.follower.id.eq(followerId)
                                                                       .and(this.buildDynamicConditions())
                                                                       .and(follow.following.id.notIn(commonBuilder.banIds(myId)))
                                                                       .and(follow.following.id.notIn(commonBuilder.blacklistIds(myId))))
                                              .orderBy(getOrderSpecifier("nickname"))
                                              .offset(pageable.getPageNumber() * pageable.getPageSize())
                                              .limit(pageable.getPageSize());

        long         total   = pagedQuery.fetchCount();
        List<Follow> content = pagedQuery.fetch();

        return new PageImpl<>(content, PageRequest.of(pageable.getPageNumber(), pageable.getPageSize()), total);

    }

    /**
     * 대상을 팔로우 하는 유저의  List
     *
     * @Param followingId
     */
    public Page<Follow> getFollowerList(String followingId, String myId, Pageable pageable) {
        JPQLQuery<Follow> pagedQuery = factory.selectFrom(follow)
                                              .innerJoin(member)
                                              .on(follow.follower.id.eq(member.id)
                                                                    .and(follow.isActivated.eq(member.isActivated)
                                                                                           .and(follow.isDeleted.eq(member.isDeleted))))
                                              .where(follow.following.id.eq(followingId)
                                                                        .and(this.buildDynamicConditions())
                                                                        .and(follow.follower.id.notIn(commonBuilder.banIds(myId)))
                                                                        .and(follow.follower.id.notIn(commonBuilder.blacklistIds(myId))))
                                              .orderBy(getOrderSpecifier("nickname"))
                                              .offset(pageable.getPageNumber() * pageable.getPageSize())
                                              .limit(pageable.getPageSize());

        long         total   = pagedQuery.fetchCount();
        List<Follow> content = pagedQuery.fetch();

        return new PageImpl<>(content, PageRequest.of(pageable.getPageNumber(), pageable.getPageSize()), total);

    }

    private BooleanExpression buildDynamicConditions() {
        BooleanExpression conditions = follow.isActivated.eq(true)
                                                         .and(follow.isDeleted.eq(false));

        //        if (includeBlacklist) {
        //            conditions = conditions.and(follow..id.notIn(commonBuilder.blacklistIds(memberId)));
        //        }
        //
        //        if (includeBans) {
        //            conditions = conditions.and(follow.id.notIn(commonBuilder.banIds(memberId)));
        //        }

        return conditions;
    }

    private OrderSpecifier<?> getOrderSpecifier(String sortBy) {
        switch (sortBy) {
            case "createdAt":
                return follow.createdAt.asc();
            default:
                return follow.createdAt.desc();
        }
    }
}
