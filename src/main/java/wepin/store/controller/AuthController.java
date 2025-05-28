package wepin.store.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URISyntaxException;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import wepin.store.dto.AppleDto;
import wepin.store.dto.AuthTokenDto;
import wepin.store.dto.CommonResponse;
import wepin.store.dto.KakaoDto;
import wepin.store.enumeration.StatusCode;
import wepin.store.service.AppleService;
import wepin.store.service.AuthService;
import wepin.store.service.KakaoService;
import wepin.store.service.MemberService;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/auth")
@CrossOrigin
public class AuthController {

    private final KakaoService  kakaoService;
    private final AuthService   authService;
    private final AppleService  appleService;
    private final MemberService memberService;

    @PostMapping("normal")
    public ResponseEntity<CommonResponse> normal(@RequestBody KakaoDto.Login params) {

        try {
            String id = params.getId();
            String pw = params.getPassword();

            if (!StringUtils.isEmpty(id)) {
                return ResponseEntity.ok(CommonResponse.builder()
                                                       .statusCode(StatusCode.SUCCESS)
                                                       .message("로그인이 완료되었습니다.")
                                                       .data(kakaoService.normalLogin(id, pw))
                                                       .build());
            } else {
                return ResponseEntity.ok(CommonResponse.builder()
                                                       .statusCode(StatusCode.NOT_FOUND)
                                                       .message("인가코드가 없습니다.")
                                                       .build());
            }
        } catch (Exception ex) {
            return ResponseEntity.ok(CommonResponse.builder()
                                                   .statusCode(StatusCode.NOT_FOUND)
                                                   .message("로그인을 실패했습니다.")
                                                   .build());
        }
    }


    @ApiOperation(value = "카카오 로그인", notes = "인가코드(authorizationCode)만 넘기면 된다. 최초 로그인인 경우 isLogined = true를 넘겨주고" + ", 재로그인인 경우 isLogined = false와 JWT 토큰을 재발급해준다.")
    @PostMapping("/kakao-login")
    public ResponseEntity<CommonResponse> kakaoLogin(@RequestBody KakaoDto.Login params) {

        try {
            String code      = params.getAuthorizationCode();
            String userAgent = params.getUserAgent();

            if (!StringUtils.isEmpty(code)) {
                return ResponseEntity.ok(CommonResponse.builder()
                                                       .statusCode(StatusCode.SUCCESS)
                                                       .message("로그인이 완료되었습니다.")
                                                       .data(kakaoService.kakaoLogin(code, userAgent))
                                                       .build());
            } else {
                return ResponseEntity.ok(CommonResponse.builder()
                                                       .statusCode(StatusCode.NOT_FOUND)
                                                       .message("인가코드가 없습니다.")
                                                       .build());
            }
        } catch (Exception ex) {
            return ResponseEntity.ok(CommonResponse.builder()
                                                   .statusCode(StatusCode.NOT_FOUND)
                                                   .message("로그인을 실패했습니다.")
                                                   .build());
        }
    }

    @ApiOperation(value = "카카오 회원가입", notes = "이메일(email)과 닉네임(nickname), name(이름), kakaoToken을 넘기면 회원가입을 시키고 JWT토큰을 발급해준다.")
    @PostMapping("kakao-sign-up")
    public ResponseEntity<CommonResponse> kakaoSignUp(@RequestBody KakaoDto.Login params) {

        try {
            String email    = params.getEmail();
            String nickname = params.getNickname();
            String token    = params.getKakaoToken();
            String name     = params.getName();

            if (!StringUtils.isEmpty(email) && !StringUtils.isEmpty(email) && !StringUtils.isEmpty(token)) {
                return ResponseEntity.ok(CommonResponse.builder()
                                                       .statusCode(StatusCode.SUCCESS)
                                                       .message("회원가입이 완료되었습니다.")
                                                       .data(kakaoService.kakaoSignUp(email, nickname, token, name))
                                                       .build());
            } else {
                return ResponseEntity.ok(CommonResponse.builder()
                                                       .statusCode(StatusCode.NOT_FOUND)
                                                       .message("이메일 또는 닉네임이 누락되었습니다.")
                                                       .build());
            }
        } catch (Exception ex) {
            return ResponseEntity.ok(CommonResponse.builder()
                                                   .statusCode(StatusCode.NOT_FOUND)
                                                   .message("로그인을 실패했습니다.")
                                                   .build());
        }
    }

    @PostMapping("/kakao-logout")
    public ResponseEntity<CommonResponse> logoutKakao(@RequestBody KakaoDto.Logout params) throws URISyntaxException {
        // 인증 정보로부터 Kakao 액세스 토큰을 가져옵니다.
        String code = params.getAuthorizationCode();

        try {
            if (!StringUtils.isEmpty(code) && kakaoService.kakaoLogout(code)) {
                return ResponseEntity.ok(CommonResponse.builder()
                                                       .statusCode(StatusCode.SUCCESS)
                                                       .message("로그아웃 완료.")
                                                       .build());
            } else {
                return ResponseEntity.ok(CommonResponse.builder()
                                                       .statusCode(StatusCode.NOT_FOUND)
                                                       .message("로그아웃 실패.")
                                                       .build());
            }

        } catch (Exception ex) {
            return ResponseEntity.ok(CommonResponse.builder()
                                                   .statusCode(StatusCode.NOT_FOUND)
                                                   .build());
        }
    }

    @PostMapping("/kakao-unlink")
    public ResponseEntity<CommonResponse> unlinkKakao(@RequestBody KakaoDto.Logout params) throws URISyntaxException {
        // 인증 정보로부터 Kakao 액세스 토큰을 가져옵니다.
        String code     = params.getAuthorizationCode();
        String memberId = params.getMemberId();
        try {
            if (!StringUtils.isEmpty(code) && kakaoService.kakaoUnlink(code, memberId)) {
                return ResponseEntity.ok(CommonResponse.builder()
                                                       .statusCode(StatusCode.SUCCESS)
                                                       .message("탈퇴 완료.")
                                                       .build());
            } else {
                return ResponseEntity.ok(CommonResponse.builder()
                                                       .statusCode(StatusCode.NOT_FOUND)
                                                       .message("탈퇴 실패.")
                                                       .build());
            }

        } catch (Exception ex) {
            return ResponseEntity.ok(CommonResponse.builder()
                                                   .statusCode(StatusCode.NOT_FOUND)
                                                   .build());
        }
    }

    @ApiOperation(value = "JWT 토큰 재발급", notes = "refreshToken을 넘겨주면 유효성 검사를 통과하면 JWT토큰과 refresh토큰을 재발급해준다.")
    @PostMapping("/token-re-issue")
    public ResponseEntity<CommonResponse> tokenReissue(@RequestBody AuthTokenDto params) {
        String refreshToken = params.getRefreshToken();
        try {
            if (!StringUtils.isEmpty(refreshToken)) {
                return ResponseEntity.ok(CommonResponse.builder()
                                                       .statusCode(StatusCode.SUCCESS)
                                                       .data(authService.tokenReIssue(refreshToken))
                                                       .message("JWT토큰 재발급 완료")
                                                       .build());
            } else {
                return ResponseEntity.ok(CommonResponse.builder()
                                                       .statusCode(StatusCode.REFRESH_TOKEN_ERROR)
                                                       .message("JWT토큰 재발급 실패: 서버로 넘어온 리프레시 토큰 NULL")
                                                       .build());
            }
        } catch (Exception ex) {
            // 예외 처리
            return ResponseEntity.ok(CommonResponse.builder()
                                                   .statusCode(StatusCode.AUTHENTICATION_ERROR) // 예외 상황에 따라서 다른 상태 코드를 설정해야 할 수 있습니다.
                                                   .message("JWT토큰 재발급 실패: " + ex.getMessage()) // 예외 메시지를 클라이언트로 전달할 수 있습니다.
                                                   .build());
        }
    }

    @ApiOperation(value = "애플 로그인", notes = "")
    @PostMapping("/apple-login")
    public ResponseEntity<CommonResponse> appleLogin(@RequestBody AppleDto.Login params) {

        try {
            String code = params.getCode();

            if (!StringUtils.isEmpty(code)) {
                return ResponseEntity.ok(CommonResponse.builder()
                                                       .statusCode(StatusCode.SUCCESS)
                                                       .message("로그인이 완료되었습니다.")
                                                       .data(appleService.appleLogin(code))
                                                       .build());
            } else {
                return ResponseEntity.ok(CommonResponse.builder()
                                                       .statusCode(StatusCode.NOT_FOUND)
                                                       .message("인가코드가 없습니다.")
                                                       .build());
            }
        } catch (Exception ex) {
            return ResponseEntity.ok(CommonResponse.builder()
                                                   .statusCode(StatusCode.NOT_FOUND)
                                                   .message("로그인을 실패했습니다.")
                                                   .build());
        }
    }

    @ApiOperation(value = "애플 로그인 창 띄우기", notes = "")
    @PostMapping("/apple-authorize")
    public ResponseEntity<CommonResponse> appleAuthorize(@RequestBody AppleDto dto) {
        try {
            if (true) {
                return ResponseEntity.ok(CommonResponse.builder()
                                                       .statusCode(StatusCode.SUCCESS)
                                                       .message("로그인 창 띄우기 완료.")
                                                       .data(appleService.appleAutorize())
                                                       .build());
            } else {
                return ResponseEntity.ok(CommonResponse.builder()
                                                       .statusCode(StatusCode.NOT_FOUND)
                                                       .message("애플 로그인실패.")
                                                       .build());
            }
        } catch (Exception ex) {
            return ResponseEntity.ok(CommonResponse.builder()
                                                   .statusCode(StatusCode.NOT_FOUND)
                                                   .message("로그인을 실패했습니다.")
                                                   .build());
        }
    }


    @ApiOperation(value = "애플 회원가입", notes = "이메일(email)과 닉네임(nickname), kakaoToken을 넘기면 회원가입을 시키고 JWT토큰을 발급해준다.")
    @PostMapping("apple-sign-up")
    public ResponseEntity<CommonResponse> appleSignUp(@RequestBody AppleDto.Login params) {

        try {
            String email    = params.getEmail();
            String nickname = params.getNickname();
            String token    = params.getAppleToken();
            String name     = params.getName();

            if (!StringUtils.isEmpty(email) && !StringUtils.isEmpty(email) && !StringUtils.isEmpty(token)) {
                return ResponseEntity.ok(CommonResponse.builder()
                                                       .statusCode(StatusCode.SUCCESS)
                                                       .message("회원가입이 완료되었습니다.")
                                                       .data(appleService.appleSignUp(email, nickname, token, name))
                                                       .build());
            } else {
                return ResponseEntity.ok(CommonResponse.builder()
                                                       .statusCode(StatusCode.NOT_FOUND)
                                                       .message("이메일 또는 닉네임이 누락되었습니다.")
                                                       .build());
            }
        } catch (Exception ex) {
            return ResponseEntity.ok(CommonResponse.builder()
                                                   .statusCode(StatusCode.NOT_FOUND)
                                                   .message("로그인을 실패했습니다.")
                                                   .build());
        }
    }


    @PostMapping("/apple-logout")
    public ResponseEntity<CommonResponse> logoutApple(@RequestBody AppleDto.Logout params) throws URISyntaxException {

        //        String code = params.getAuthorizationCode();

        try {
            //            if (!StringUtils.isEmpty(code) && kakaoService.kakaoLogout(code)) {
            return ResponseEntity.ok(CommonResponse.builder()
                                                   .statusCode(StatusCode.SUCCESS)
                                                   .message("로그아웃 완료.")
                                                   .build());
            //            } else {
            //                return ResponseEntity.ok(CommonResponse.builder()
            //                        .statusCode(StatusCode.NOT_FOUND)
            //                        .message("로그아웃 실패.")
            //                        .build());
            //            }

        } catch (Exception ex) {
            return ResponseEntity.ok(CommonResponse.builder()
                                                   .statusCode(StatusCode.NOT_FOUND)
                                                   .build());
        }
    }

    @PostMapping("/apple-unlink")
    public ResponseEntity<CommonResponse> unlinkApple(@RequestBody AppleDto.Logout params) throws URISyntaxException {

        String code     = params.getAuthorizationCode();
        String memberId = params.getMemberId();

        try {
            if (!StringUtils.isEmpty(code) && appleService.AppleUnlink(code, memberId)) {
                return ResponseEntity.ok(CommonResponse.builder()
                                                       .statusCode(StatusCode.SUCCESS)
                                                       .message("탈퇴 완료.")
                                                       .build());
            } else {
                return ResponseEntity.ok(CommonResponse.builder()
                                                       .statusCode(StatusCode.NOT_FOUND)
                                                       .message("탈퇴 실패.")
                                                       .build());
            }

        } catch (Exception ex) {
            return ResponseEntity.ok(CommonResponse.builder()
                                                   .statusCode(StatusCode.NOT_FOUND)
                                                   .build());
        }
    }


    @ApiOperation(value = "닉네임 중복 체크", notes = "")
    @GetMapping("/nickname-check")
    public ResponseEntity<CommonResponse> nicknameCheck(
            @ApiParam(name = "nickname", value = "닉네임") @RequestParam(name = "nickname") String nickname) throws URISyntaxException {

        try {
            if (!StringUtils.isEmpty(nickname)) {
                return ResponseEntity.ok(CommonResponse.builder()
                                                       .statusCode(StatusCode.SUCCESS)
                                                       .message("조회되었습니다.")
                                                       .data(memberService.isExistNickname(nickname))
                                                       .build());
            } else {
                return ResponseEntity.ok(CommonResponse.builder()
                                                       .statusCode(StatusCode.NOT_FOUND)
                                                       .build());
            }
        } catch (Exception ex) {
            return ResponseEntity.ok(CommonResponse.builder()
                                                   .statusCode(StatusCode.NOT_FOUND)
                                                   .build());
        }
    }

}