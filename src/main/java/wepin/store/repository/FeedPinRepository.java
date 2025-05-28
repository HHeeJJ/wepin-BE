package wepin.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import wepin.store.entity.FeedPin;


public interface FeedPinRepository extends JpaRepository<FeedPin, String> {


}
