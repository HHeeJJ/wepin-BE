package wepin.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import wepin.store.entity.Log;


public interface LogRepository extends JpaRepository<Log, String> {


}
