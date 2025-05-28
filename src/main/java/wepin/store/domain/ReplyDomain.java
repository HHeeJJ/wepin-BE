package wepin.store.domain;

import static wepin.store.entity.QReply.reply;

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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import wepin.store.entity.Reply;
import wepin.store.repository.ReplyRepository;
import wepin.store.utils.CommonBuilder;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReplyDomain {

    private final ReplyRepository replyRepository;
    private final JPAQueryFactory factory;
    private final CommonBuilder   commonBuilder;

    public Page<Reply> getReplyListByFeedId(String feedId, Pageable pageable) {
        Pageable pageableWithSort = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Direction.ASC, "createdAt"));
        return replyRepository.findByFeedIdAndIsDeletedFalseAndIsActivatedTrue(feedId, pageableWithSort);
    }


    public Page<Reply> getReplyListByFeedIdNotInBlacklistAndBan(String feedId, String memberId, Pageable pageable) {
        JPQLQuery<Reply> pagedQuery = factory.selectFrom(reply)
                                             .where(reply.feed.id.eq(feedId)
                                                                 .and(this.buildDynamicConditions(memberId, true, true)))
                                             .orderBy(getOrderSpecifier("createdAt"))
                                             .offset(pageable.getPageNumber() * pageable.getPageSize())
                                             .limit(pageable.getPageSize());

        long        total   = pagedQuery.fetchCount();
        List<Reply> content = pagedQuery.fetch();

        return new PageImpl<>(content, PageRequest.of(pageable.getPageNumber(), pageable.getPageSize()), total);
    }

    public void deleteAll(String feedId) {
        List<Reply> replyList = replyRepository.findByFeedId(feedId);

        //        List<Reply> deleteList = replyList.stream()
        //                                          .map(m -> {
        //                                              m.delete();
        //                                              return m;
        //                                          })
        //                                          .collect(Collectors.toList());

        if (replyList.size() > 0) {
            try {
                replyRepository.deleteAll(replyList);
            } catch (Exception e) {
                log.error("댓글 삭제 오류가 발생했습니다. " + e);
            }

        }
    }


    private OrderSpecifier<?> getOrderSpecifier(String sortBy) {
        switch (sortBy) {
            case "createdAt":
                return reply.createdAt.asc();
            default:
                return reply.createdAt.desc();
        }

    }


    private BooleanExpression buildDynamicConditions(String memberId, boolean includeBlacklist, boolean includeBans) {
        BooleanExpression conditions = reply.isActivated.eq(true)
                                                        .and(reply.isDeleted.eq(false));

        if (includeBlacklist) {
            conditions = conditions.and(reply.member.id.notIn(commonBuilder.blacklistIds(memberId)));
        }

        if (includeBans) {
            conditions = conditions.and(reply.member.id.notIn(commonBuilder.banIds(memberId)));
        }

        return conditions;
    }

}
