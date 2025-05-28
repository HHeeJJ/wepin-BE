package wepin.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import wepin.store.entity.FeedTag;

public interface FeedTagRepository extends JpaRepository<FeedTag, String> {
    List<FeedTag> findByFeedId(String feedId);
}
