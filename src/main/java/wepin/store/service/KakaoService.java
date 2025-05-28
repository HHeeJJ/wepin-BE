package wepin.store.service;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import wepin.store.domain.BlacklistDomain;
import wepin.store.domain.FeedDomain;
import wepin.store.domain.MemberDomain;
import wepin.store.dto.AuthTokenDto;
import wepin.store.dto.KakaoDto;
import wepin.store.dto.MemberDto;
import wepin.store.entity.Feed;
import wepin.store.entity.Member;
import wepin.store.enumeration.Auth;
import wepin.store.enumeration.MemberChannel;
import wepin.store.enumeration.UserAgent;
import wepin.store.jwt.JwtTokenProvider;
import wepin.store.repository.MemberRepository;
import wepin.store.utils.AuthTokensGenerator;
import wepin.store.utils.RandomUtils;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class KakaoService {


    private final MemberRepository    memberRepository;
    private final AuthTokensGenerator authTokensGenerator;
    private final JwtTokenProvider    jwtTokenProvider;
    private final MemberDomain        memberDomain;
    private final FeedDomain          feedDomain;
    private final BlacklistDomain     blacklistDomain;
    private final PushService         pushService;
    private final CustomPinService    customPinService;

    @Value("${oauth.kakao.client-id}")
    private String KAKAO_CLIENT_ID;

    @Value("${oauth.kakao.url.auth}")
    private String KAKAO_AUTH_URI;

    @Value("${oauth.kakao.url.api}")
    private String KAKAO_API_URI;

    @Value("${oauth.kakao.redirect-url}")
    private String KAKAO_REDIRECT_URI;

    public String getKakaoLogin() {
        return KAKAO_AUTH_URI + "/oauth/authorize" + "?client_id=" + KAKAO_CLIENT_ID + "&redirect_uri=" + "" + "&response_type=code";
    }

    private String getAccessToken(String code) throws Exception {
        String accessToken = "";

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-type", "application/x-www-form-urlencoded");

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("grant_type", "authorization_code");
            params.add("client_id", KAKAO_CLIENT_ID);
            params.add("code", code);
            params.add("redirect_uri", KAKAO_REDIRECT_URI);


            RestTemplate                              restTemplate = new RestTemplate();
            HttpEntity<MultiValueMap<String, String>> httpEntity   = new HttpEntity<>(params, headers);

            ResponseEntity<String> response = restTemplate.exchange(KAKAO_AUTH_URI + "/oauth/token", HttpMethod.POST, httpEntity, String.class);

            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObj    = (JSONObject) jsonParser.parse(response.getBody());

            accessToken = (String) jsonObj.get("access_token");

            return accessToken;

        } catch (Exception e) {
            log.error(String.valueOf(e));
            throw new Exception(e);
        }
    }

    private KakaoDto getUserInfoWithToken(String accessToken) throws ParseException {
        try {
            //HttpHeader 생성
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + accessToken);
            headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

            //HttpHeader 담기
            RestTemplate                              rt         = new RestTemplate();
            HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(headers);
            ResponseEntity<String>                    response   = rt.exchange(KAKAO_API_URI + "/v2/user/me", HttpMethod.POST, httpEntity,
                                                                               String.class);

            //Response 데이터 파싱
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObj    = (JSONObject) jsonParser.parse(response.getBody());
            JSONObject account    = (JSONObject) jsonObj.get("kakao_account");
            JSONObject profile    = (JSONObject) account.get("profile");

            String nickname = ObjectUtils.isEmpty(profile) ? RandomUtils.randomName() : String.valueOf(profile.get("nickname"));

            return KakaoDto.builder()
                           .name(nickname)
                           .email(String.valueOf(account.get("email")))
                           .id((long) jsonObj.get("id"))
                           .build();
        } catch (Exception e) {
            log.info(String.valueOf(e));
            throw e;
        }

    }

    public AuthTokenDto normalLogin(String id, String pw) throws Exception {

        List<Member> list = memberRepository.findByMemberIdAndPasswordAndIsDeletedFalseAndIsActivatedTrue(id, pw);
        AuthTokenDto dto  = new AuthTokenDto();
        // 이미 사용자가 존재하는 경우
        if (list.size() > 0) {
            dto = authTokensGenerator.generate(list.get(0)
                                                   .getId(), MemberChannel.EMAIL.getName());
            dto.setMemberDto(MemberDto.builder()
                                      .isLogined(true)
                                      .id(list.get(0)
                                              .getId())
                                      .email(list.get(0)
                                                 .getEmail())
                                      .username(list.get(0)
                                                    .getName())
                                      .build());

            return dto;
        } else {
            dto.setMemberDto(MemberDto.builder()
                                      .isLogined(false)
                                      .build());
        }
        return dto;
    }

    public AuthTokenDto kakaoLogin(String code, String userAgent) throws Exception {
        String   accessToken = code;
        KakaoDto kakaoDto;
        if (userAgent.toUpperCase()
                     .equals(UserAgent.ANDROID.name())) {
            kakaoDto = getUserInfoWithToken(code);
        } else {
            accessToken = getAccessToken(code);

            kakaoDto = getUserInfoWithToken(accessToken);
        }

        String email = kakaoDto.getEmail();
        String name  = kakaoDto.getName();

        if (!StringUtils.isEmpty(email)) {
            Optional<Member> member = memberRepository.findByMemberIdAndTypeAndIsDeletedFalseAndIsActivatedTrue(email,
                                                                                                                MemberChannel.KAKAO.toString());

            AuthTokenDto dto = new AuthTokenDto();

            // 이미 사용자가 존재하는 경우
            if (member.isPresent()) {
                dto = authTokensGenerator.generate(member.get()
                                                         .getId(), accessToken);
                dto.setMemberDto(MemberDto.builder()
                                          .isLogined(true)
                                          .isBlacklist(blacklistDomain.isBlacklist(member.get()
                                                                                         .getId()))
                                          .id(member.get()
                                                    .getId())
                                          .auth(member.get()
                                                      .getAuth())
                                          .email(email)
                                          .username(member.get()
                                                          .getName())
                                          //                        .kakaoToken(accessToken)
                                          .build());
            } else {
                // 사용자가 최초로 로그인 한 경우
                dto.setMemberDto(MemberDto.builder()
                                          .isLogined(false)
                                          .email(email)
                                          .username(name)
                                          .kakaoToken(accessToken)
                                          .build());
            }
            return dto;
        } else {
            throw new IllegalArgumentException("올바르지 않은 사용자입니다.");
        }
    }

    public AuthTokenDto kakaoSignUp(String email, String nickname, String token, String name) {
        log.info("[회원가입] nickname : " + nickname);
        Member member = newMember(KakaoDto.builder()
                                          .email(email)
                                          .nickname(StringUtils.isEmpty(nickname) ? RandomUtils.randomName() : nickname)
                                          .name(name)
                                          .build());

        if (!ObjectUtils.isEmpty(member)) {

            try {
                pushService.init(member, true);
                customPinService.init(member);
            } catch (Exception e) {
                log.error("pushService 초기화 중 오류 발생: ", e);
            }

            AuthTokenDto dto = authTokensGenerator.generate(member.getId(), token);
            dto.setMemberDto(MemberDto.builder()
                                      .isLogined(true)
                                      .id(member.getId())
                                      .auth(Auth.USER.getName())
                                      .email(email)
                                      .build());


            return dto;
        } else {
            throw new IllegalArgumentException("올바르지 않은 사용자입니다.");
        }
    }

    private Member newMember(KakaoDto dto) {
        Member member = Member.builder()
                              .email(dto.getEmail())
                              .memberId(dto.getEmail())
                              .nickname(dto.getNickname())
                              .auth(Auth.USER.getName())
                              .name(dto.getName())
                              .isActivated(true)
                              .isDeleted(false)
                              .type(MemberChannel.KAKAO.toString())
                              .build();

        return memberRepository.save(member);
    }

    public Boolean kakaoLogout(String accessToken) {
        try {
            String myRealToken = jwtTokenProvider.getAccessToken(accessToken);

            // Kakao 로그아웃 요청을 보냅니다.
            RestTemplate restTemplate   = new RestTemplate();
            String       kakaoLogoutUrl = "https://kapi.kakao.com/v1/user/logout";
            HttpHeaders  headers        = new HttpHeaders();
            headers.set("Authorization", "Bearer " + myRealToken);
            HttpEntity<String>     entity   = new HttpEntity<>(null, headers);
            ResponseEntity<String> response = restTemplate.exchange(kakaoLogoutUrl, HttpMethod.POST, entity, String.class);

            if (response.getStatusCode()
                        .is2xxSuccessful()) {
                // 로그아웃 성공
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            System.out.println(e);
        }

        return false;
    }


    @Transactional
    public Boolean kakaoUnlink(String accessToken, String memberId) {
        try {
            String myRealToken = jwtTokenProvider.getAccessToken(accessToken);

            // Kakao 로그아웃 요청을 보냅니다.
            RestTemplate restTemplate   = new RestTemplate();
            String       kakaoLogoutUrl = "https://kapi.kakao.com/v1/user/unlink";
            HttpHeaders  headers        = new HttpHeaders();
            headers.set("Authorization", "Bearer " + myRealToken);
            HttpEntity<String>     entity   = new HttpEntity<>(null, headers);
            ResponseEntity<String> response = restTemplate.exchange(kakaoLogoutUrl, HttpMethod.POST, entity, String.class);

            if (response.getStatusCode()
                        .is2xxSuccessful()) {

                Optional<Member> member = memberDomain.findMemberById(memberId);

                if (member.isPresent()) {
                    Member m = member.get();

                    m.isDeleted(true);
                    m.isActivated(false);
                    memberDomain.save(m);

                    List<Feed> feedList = feedDomain.getFeedListByMemberId(memberId)
                                                    .stream()
                                                    .peek(f -> {
                                                        f.isDeleted(true);
                                                        f.isActivated(false);
                                                    })
                                                    .collect(Collectors.toList());

                    feedDomain.saveAll(feedList);
                    pushService.delete(m);
                }

                // 탈퇴 성공
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            System.out.println(e);
        }

        return false;
    }
}