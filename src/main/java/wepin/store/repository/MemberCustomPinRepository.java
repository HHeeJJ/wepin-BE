package wepin.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import wepin.store.entity.MemberCustomPin;


public interface MemberCustomPinRepository extends JpaRepository<MemberCustomPin, String> {

}
