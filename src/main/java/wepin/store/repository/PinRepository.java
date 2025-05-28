package wepin.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

import wepin.store.entity.Pin;


public interface PinRepository extends JpaRepository<Pin, String> {

    Optional<Pin> findByIdAndIsDeletedFalseAndIsActivatedTrue(String id);

}
