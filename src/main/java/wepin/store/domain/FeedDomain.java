package wepin.store.domain;

import static wepin.store.entity.QBlacklist.blacklist;
import static wepin.store.entity.QFeed.feed;
import static wepin.store.entity.QFeedTag.feedTag;
import static wepin.store.entity.QHide.hide;
import static wepin.store.entity.QTag.tag;

import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import wepin.store.entity.Feed;
import wepin.store.enumeration.MemberStatus;
import wepin.store.repository.FeedRepository;
import wepin.store.utils.CommonBuilder;

@Service
@RequiredArgsConstructor
public class FeedDomain {

    private final JPAQueryFactory factory;
    private final FeedRepository  feedRepository;
    private final CommonBuilder   commonBuilder;


    public List<Feed> findAll() {
        return feedRepository.findAll();
    }

    public Page<Feed> list(String memberId, Pageable pageable) {

        JPQLQuery<Feed> pagedQuery = this.mainFeedList(memberId)
                                         .offset(pageable.getPageNumber() * pageable.getPageSize())
                                         .limit(pageable.getPageSize());

        long       total   = pagedQuery.fetchCount();
        List<Feed> content = pagedQuery.fetch();

        return new PageImpl<>(content, PageRequest.of(pageable.getPageNumber(), pageable.getPageSize()), total);
    }

    public Page<Feed> getFeedListByTag(String tagName, String memberId, Pageable pageable) {
        JPQLQuery<Feed> pagedQuery = this.mainFeedList(memberId)
                                         .innerJoin(feedTag)
                                         .on(feed.id.eq(feedTag.feed.id))
                                         .innerJoin(tag)
                                         .on(feedTag.tag.id.eq(tag.id))
                                         .where(tag.tagName.like("%" + tagName + "%"))
                                         .groupBy(feedTag.feed.id)
                                         .offset((long) pageable.getPageNumber() * pageable.getPageSize())
                                         .limit(pageable.getPageSize());

        long       total   = pagedQuery.fetchCount();
        List<Feed> content = pagedQuery.fetch();

        return new PageImpl<>(content, PageRequest.of(pageable.getPageNumber(), pageable.getPageSize()), total);
    }

    public Page<Feed> getMyFeedList(String memberId, Pageable pageable) {
        Pageable pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());

        return feedRepository.findByMemberIdAndIsDeletedFalseAndIsActivatedTrueOrderByCreatedAtDesc(memberId, pageRequest);

    }

    public Page<Feed> getMainFeedListForPageByBeginner(Pageable pageable) {
        return feedRepository.findByIsDeletedFalseAndIsActivatedTrueOrderByCreatedAtDesc(pageable);
    }

    //    public Feed getFeedDetail(String feedId) {
    //        return feedRepository.findByIdAndIsDeletedFalseAndIsActivatedTrue(feedId)
    //                             .orElse(null);
    //    }

    public List<Feed> getUserMainFeedList(String memberId, String myId) {
        return this.mainFeedList(myId)
                   .where(feed.member.id.eq(memberId))
                   .fetch();
    }

    public Optional<Feed> findById(String feedId) {
        return feedRepository.findByIdAndIsDeletedFalseAndIsActivatedTrue(feedId);
    }


    public List<Feed> getFeedListByMemberId(String memberId) {
        return feedRepository.findByMemberIdAndIsDeletedFalseAndIsActivatedTrue(memberId);
    }

    public void saveAll(List<Feed> list) {
        feedRepository.saveAll(list);
    }

    public JPQLQuery<Feed> mainFeedList(String myId) {
        return factory.selectFrom(feed)
                      .leftJoin(blacklist)
                      .on(feed.member.id.eq(blacklist.black.id)
                                        .and(blacklist.member.id.eq(myId))
                                        .and(blacklist.status.eq(MemberStatus.REQUEST.getName()))
                                        .and(feed.isActivated.eq(blacklist.isActivated))
                                        .and(feed.isDeleted.eq(blacklist.isDeleted)))
                      .leftJoin(hide)
                      .on(feed.id.eq(hide.feed.id)
                                 .and(hide.member.id.eq(myId))
                                 .and(feed.isActivated.eq(hide.isActivated))
                                 .and(feed.isDeleted.eq(hide.isDeleted)))
                      .where(feed.isActivated.eq(true)
                                             .and(feed.isDeleted.eq(false))
                                             .and(blacklist.black.id.isNull())
                                             .and(hide.feed.id.isNull())
                                             .and(feed.member.id.notIn(commonBuilder.blacklistIds(myId)))
                                             .and(feed.member.id.notIn(commonBuilder.banIds(myId))))
                      .orderBy(feed.createdAt.desc());
    }

    public Feed save(Feed feed) {
        return feedRepository.save(feed);
    }
}
