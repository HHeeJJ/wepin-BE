package wepin.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import wepin.store.entity.CustomPin;


public interface CustomPinRepository extends JpaRepository<CustomPin, String> {

    public List<CustomPin> findByType(String type);
}
