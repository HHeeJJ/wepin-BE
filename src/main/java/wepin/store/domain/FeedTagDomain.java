package wepin.store.domain;

import org.springframework.stereotype.Service;

import java.util.List;

import javax.transaction.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import wepin.store.entity.FeedTag;
import wepin.store.repository.FeedTagRepository;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class FeedTagDomain {

    private final FeedTagRepository feedTagRepository;

    //    public void create(List<String> tagList, Feed feed) {
    //        try {
    //            //            List<FeedTag> feedTagList = new ArrayList<>();
    //            AtomicInteger idx = new AtomicInteger(0);
    //            for (String tagStr : tagList) {
    //                Tag tag = tagDomain.findByTagName(tagStr);
    //
    //                if (tag == null) {
    //                    Tag createdTag = tagDomain.create(tagStr);
    //                    feedTagRepository.save(FeedTag.builder()
    //                                                  .tag(createdTag)
    //                                                  .feed(feed)
    //                                                  .uiSeq(idx.getAndIncrement())
    //                                                  .build());
    //                    //                    feedTagList.add(FeedTag.builder()
    //                    //                                           .tag(createdTag)
    //                    //                                           .feed(feed)
    //                    //                                           .uiSeq(idx.getAndIncrement())
    //                    //                                           .build());
    //                } else {
    //                    feedTagRepository.save(FeedTag.builder()
    //                                                  .tag(tag)
    //                                                  .feed(feed)
    //                                                  .uiSeq(idx.getAndIncrement())
    //                                                  .build());
    //                    //                    feedTagList.add(FeedTag.builder()
    //                    //                                           .tag(tag)
    //                    //                                           .feed(feed)
    //                    //                                           .uiSeq(idx.getAndIncrement())
    //                    //                                           .build());
    //                }
    //            }
    //            //            feedTagRepository.saveAll(feedTagList);
    //        } catch (Exception e) {
    //            log.error(e.getMessage());
    //        }
    //    }

    public FeedTag create(FeedTag feedTag) {
        try {
            return feedTagRepository.save(feedTag);
        } catch (Exception e) {
            log.error(String.valueOf(e));
            return null;
        }
    }

    public void create(List<FeedTag> list) {
        try {
            feedTagRepository.saveAll(list);
        } catch (Exception e) {
            log.error(String.valueOf(e));
        }
    }

    public void deleteAll(List<FeedTag> list) {
        try {
            feedTagRepository.deleteAll(list);
        } catch (Exception e) {
            log.error(String.valueOf(e));
        }
    }

    public List<FeedTag> findByFeedId(String feedId) {
        return feedTagRepository.findByFeedId(feedId);
    }
}
