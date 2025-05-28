package wepin.store.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

import wepin.store.entity.Feed;


public interface FeedRepository extends JpaRepository<Feed, String> {

    Optional<Feed> findByIdAndIsDeletedFalseAndIsActivatedTrue(String id);

    Long countByMemberIdAndIsDeletedFalseAndIsActivatedTrue(String memberId);

    List<Feed> findByMemberIdAndIsDeletedFalseAndIsActivatedTrue(String memberId);

    List<Feed> findByMemberIdAndIsDeletedFalseAndIsActivatedTrueOrderByCreatedAtDesc(String memberId);

    Page<Feed> findByMemberIdInAndIsDeletedFalseAndIsActivatedTrueOrderByCreatedAtDesc(List<String> members, Pageable pageable);

    Page<Feed> findByMemberIdAndIsDeletedFalseAndIsActivatedTrueOrderByCreatedAtDesc(String memberId, Pageable pageable);

    List<Feed> findByMemberIdNotInAndIsDeletedFalseAndIsActivatedTrueOrderByCreatedAtDesc(List<String> members);

    List<Feed> findTop10ByIsDeletedFalseAndIsActivatedTrueOrderByCreatedAtDesc();

    Page<Feed> findByIsDeletedFalseAndIsActivatedTrueOrderByCreatedAtDesc(Pageable pageable);
}
