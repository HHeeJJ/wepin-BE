package wepin.store.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import wepin.store.enumeration.MemberChannel;
import wepin.store.service.OAuthLoginParams;

import java.awt.*;

@Builder
@Data
public class AppleDto {

    private String id;
    private String email;
    private String nickname;
    private String appleToken;
    private String name;

    @Getter
    @NoArgsConstructor
    public static class Login implements OAuthLoginParams {

        private String code;
        private String state;
        private String id_token;
        private String email;
        private String nickname;
        private String id;
        private String appleToken;
        private String name;


        @Override
        public MemberChannel memberChannel() {
            return MemberChannel.APPLE;
        }

        @Override
        public MultiValueMap<String, String> makeBody() {
            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("code", code);
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