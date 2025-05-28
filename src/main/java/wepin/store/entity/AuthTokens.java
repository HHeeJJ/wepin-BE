package wepin.store.entity;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthTokens {
    private String accessToken;
    private String refreshToken;
    private String grantType;
    private Long expiresIn;
    private String memberId;


    public static AuthTokens of(String accessToken, String refreshToken, String grantType, Long expiresIn, String memberId) {
        return new AuthTokens(accessToken, refreshToken, grantType, expiresIn, memberId);
    }
}