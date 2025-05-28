package wepin.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

import wepin.store.entity.Tag;

public interface TagRepository extends JpaRepository<Tag, String> {
    Optional<Tag> findByTagName(String tagName);
}
