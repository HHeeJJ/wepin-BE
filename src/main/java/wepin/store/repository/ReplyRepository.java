package wepin.store.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

import wepin.store.entity.Reply;

public interface ReplyRepository extends JpaRepository<Reply, String> {
    Optional<Reply> findByIdAndIsDeletedFalseAndIsActivatedTrue(String id);


    Page<Reply> findByFeedIdAndIsDeletedFalseAndIsActivatedTrue(String feedId, Pageable pageable);


    List<Reply> findByFeedId(String feedId);
}
