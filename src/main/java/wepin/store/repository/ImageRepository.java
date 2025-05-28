package wepin.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import wepin.store.entity.Image;


public interface ImageRepository extends JpaRepository<Image, String> {

}
