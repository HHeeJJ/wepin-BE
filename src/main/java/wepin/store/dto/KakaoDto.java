package wepin.store.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import lombok.RequiredArgsConstructor;
import wepin.store.enumeration.MemberChannel;
import wepin.store.service.OAuthLoginParams;

@Builder
@Data
public class KakaoDto {

    private Long   id;
    private String email;
    private String nickname;
    private String name;

    @Getter
    @NoArgsConstructor
    public static class Login implements OAuthLoginParams {

        private String authorizationCode;
        private String email;
        private String nickname;
        private String id;
        private String kakaoToken;
        private String name;
        private String userAgent;
        private String password;

        @Override
        public MemberChannel memberChannel() {
            return MemberChannel.KAKAO;
        }

        @Override
        public MultiValueMap<String, String> makeBody() {
            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("code", authorizationCode);
            return body;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class Logout {
        private String authorizationCode;
        private String memberId;
    }

}