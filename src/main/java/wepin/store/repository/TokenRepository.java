package wepin.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

import wepin.store.entity.Token;


public interface TokenRepository extends JpaRepository<Token, String> {

    Optional<Token> findByTokenAndMemberId(String token, String memberId);


    Optional<Token> findByMemberId(String memberId);

    List<Token> findByMemberIdIn(List<String> memberIds);
}
