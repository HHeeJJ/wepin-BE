package wepin.store.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import wepin.store.entity.PushHistory;


public interface PushHistoryRepository extends JpaRepository<PushHistory, String> {

    Page<PushHistory> findByMemberId(String memberId, Pageable pageable);
}
