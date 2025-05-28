package wepin.store.dto;


import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthTokenDto {
    private String accessToken;
    private String refreshToken;
    private String grantType;
    private Long expiresIn;
    private MemberDto memberDto;


}