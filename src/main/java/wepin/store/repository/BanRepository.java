package wepin.store.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

import wepin.store.entity.Ban;


public interface BanRepository extends JpaRepository<Ban, String> {

    Page<Ban> findByMemberIdAndIsActivatedTrueAndIsDeletedFalse(String memberId, Pageable pageable);

    Optional<Ban> findByIdAndIsActivatedTrueAndIsDeletedFalse(String id);
}
