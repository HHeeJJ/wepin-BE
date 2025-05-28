package wepin.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import wepin.store.entity.Follow;

import java.util.List;
import java.util.Optional;


public interface FollowRepository extends JpaRepository<Follow, String> {

    List<Follow> findByFollowingId(String id);

    List<Follow> findByFollowerId(String id);

    // 팔로잉 수
    Long countByFollowingId(String followingId);

//    팔로워 수
    Long countByFollowerId(String followerId);


    Optional<Follow> findByFollowerIdAndFollowingId(String followerId, String followingId);

}
