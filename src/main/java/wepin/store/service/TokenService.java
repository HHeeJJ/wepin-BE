package wepin.store.service;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import javax.transaction.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import wepin.store.domain.MemberDomain;
import wepin.store.domain.TokenDomain;
import wepin.store.dto.FcmSendDto;
import wepin.store.entity.Member;
import wepin.store.entity.Token;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TokenService {

    private final TokenDomain  tokenDomain;
    private final MemberDomain memberDomain;

    public void applyToken(FcmSendDto.TokenInfoDto dto) {

        if (!dto.getToken()
                .isEmpty() && !dto.getMemberId()
                                  .isEmpty()) {

            Optional<Member> member = memberDomain.findMemberById(dto.getMemberId());
            Token            token  = tokenDomain.findByMemberId(dto.getMemberId());

            if (token != null) {
                log.info("이미 디바이스 토큰이 존재 memberId {}", dto.getMemberId());
                token.setToken(dto.getToken());
                tokenDomain.save(token);
            } else {
                if (member.isPresent()) {
                    tokenDomain.save(Token.builder()
                                          .token(dto.getToken())
                                          .member(member.get())
                                          .updatedAt(LocalDateTime.now())
                                          .isActivated(true)
                                          .build());
                } else {
                    log.error("Member not found : {}", dto.getMemberId());
                }
            }
        }
    }

    public FcmSendDto.TokenInfoDto getTokenInfo(String memberId) {

        Token tokenInfo = tokenDomain.findByMemberId(memberId);
        if (tokenInfo != null) {
            return FcmSendDto.TokenInfoDto.builder()
                                          .token(tokenInfo.getToken())
                                          .memberId(tokenInfo.getMember()
                                                             .getId())
                                          .updatedAt(tokenInfo.getUpdatedAt()
                                                              .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                                          .build();
        }

        return null;
    }


}
