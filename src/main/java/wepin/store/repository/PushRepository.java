package wepin.store.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

import wepin.store.entity.Push;


public interface PushRepository extends JpaRepository<Push, String> {

    Optional<Push> findByMemberId(String memberId);
}
