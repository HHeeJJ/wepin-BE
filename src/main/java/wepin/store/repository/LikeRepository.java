package wepin.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import wepin.store.entity.Likes;

import java.util.Optional;


public interface LikeRepository extends JpaRepository<Likes, String> {

    Optional<Likes> findByMemberIdAndFeedId(String memberId, String feedId);

    Long countByFeedId(String feedId);
}
