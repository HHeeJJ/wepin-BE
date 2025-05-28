package wepin.store.domain;

import org.springframework.stereotype.Service;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import wepin.store.entity.Token;
import wepin.store.repository.TokenRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenDomain {

    private final TokenRepository tokenRepository;

    public void save(Token token) {
        try {
            tokenRepository.save(token);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public Token findByMemberId(String memberId) {
        return tokenRepository.findByMemberId(memberId)
                              .orElse(null);
    }

    public List<Token> findByMemberIdIn(List<String> memberIds) {
        return tokenRepository.findByMemberIdIn(memberIds);
    }
}
