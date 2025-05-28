package wepin.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

import wepin.store.entity.Code;


public interface CodeRepository extends JpaRepository<Code, String> {

    Optional<Code> findByCdIdAndCdClsIdAndIsActivatedTrueAndIsDeletedFalse(String cdId, String cdClsId);

    List<Code> findByCdClsIdAndIsActivatedTrueAndIsDeletedFalse(String cdClsId);
}
