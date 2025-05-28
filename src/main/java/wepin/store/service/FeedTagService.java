package wepin.store.service;


import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.transaction.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import wepin.store.domain.FeedTagDomain;
import wepin.store.domain.TagDomain;
import wepin.store.entity.Feed;
import wepin.store.entity.FeedTag;
import wepin.store.entity.Tag;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class FeedTagService {

    private final TagDomain     tagDomain;
    private final FeedTagDomain feedTagDomain;


    public void create(List<String> tagList, Feed feed) {
        try {
            List<FeedTag> feedTagList = new ArrayList<>();
            AtomicInteger idx         = new AtomicInteger(0);

            tagList.forEach(m -> {
                Tag tag = tagDomain.findByTagName(m);
                if (tag == null) {
                    Tag createdTag = tagDomain.create(m);

                    feedTagList.add(FeedTag.builder()
                                           .tag(createdTag)
                                           .feed(feed)
                                           .uiSeq(idx.getAndIncrement())
                                           .isActivated(true)
                                           .isDeleted(false)
                                           .createdAt(LocalDateTime.now())
                                           .build());
                } else {
                    tag.setUsedCnt(tag.getUsedCnt() + 1);
                    feedTagList.add(FeedTag.builder()
                                           .tag(tagDomain.update(tag))
                                           .feed(feed)
                                           .isActivated(true)
                                           .isDeleted(false)
                                           .uiSeq(idx.getAndIncrement())
                                           .createdAt(LocalDateTime.now())
                                           .build());
                }
            });


            feedTagDomain.create(feedTagList);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public void deleteAll(Feed feed) {
        try {
            List<FeedTag> list = feedTagDomain.findByFeedId(feed.getId());

            if (!list.isEmpty()) {
                feedTagDomain.deleteAll(list);
            }

        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
