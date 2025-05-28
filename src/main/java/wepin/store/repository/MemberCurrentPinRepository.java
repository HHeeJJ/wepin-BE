package wepin.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

import wepin.store.entity.MemberCurrentPin;


public interface MemberCurrentPinRepository extends JpaRepository<MemberCurrentPin, String> {

    Optional<MemberCurrentPin> findByMemberId(String memberId);
}
