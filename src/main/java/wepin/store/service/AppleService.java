package wepin.store.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import wepin.store.domain.BlacklistDomain;
import wepin.store.domain.FeedDomain;
import wepin.store.domain.MemberDomain;
import wepin.store.dto.AppleDto;
import wepin.store.dto.AuthTokenDto;
import wepin.store.dto.MemberDto;
import wepin.store.entity.Feed;
import wepin.store.entity.Member;
import wepin.store.enumeration.Auth;
import wepin.store.enumeration.MemberChannel;
import wepin.store.jwt.JwtTokenProvider;
import wepin.store.repository.MemberRepository;
import wepin.store.utils.AuthTokensGenerator;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class AppleService {

    private final MemberRepository    memberRepository;
    private final AuthTokensGenerator authTokensGenerator;
    private final JwtTokenProvider    jwtTokenProvider;
    private final MemberDomain        memberDomain;
    private final FeedDomain          feedDomain;
    private final BlacklistDomain     blacklistDomain;
    private final PushService         pushService;
    private final CustomPinService    customPinService;

    @Value("${oauth.apple.team-id}")
    private String APPLE_TEAM_ID;

    @Value("${oauth.apple.login-key}")
    private String APPLE_LOGIN_KEY;

    @Value("${oauth.apple.client-id}")
    private String APPLE_CLIENT_ID;

    @Value("${oauth.apple.redirect-url}")
    private String APPLE_REDIRECT_URL;

    @Value("${oauth.apple.key-path}")
    private String APPLE_KEY_PATH;

    private final static String APPLE_AUTH_URL = "https://appleid.apple.com";


    public AuthTokenDto appleLogin(String code) throws Exception {

        AppleDto appleDto = getAppleInfo(code);

        String memberId    = appleDto.getId();
        String email       = appleDto.getEmail();
        String accessToken = appleDto.getAppleToken();

        if (!StringUtils.isEmpty(email)) {
            Optional<Member> member = memberRepository.findByMemberIdAndTypeAndIsDeletedFalseAndIsActivatedTrue(memberId,
                                                                                                                MemberChannel.APPLE.toString());

            AuthTokenDto dto = new AuthTokenDto();

            // 이미 사용자가 존재하는 경우
            if (member.isPresent()) {
                dto = authTokensGenerator.generate(member.get()
                                                         .getId(), accessToken);
                dto.setMemberDto(MemberDto.builder()
                                          .isLogined(true)
                                          .isBlacklist(blacklistDomain.isBlacklist(member.get()
                                                                                         .getId()))
                                          .auth(member.get()
                                                      .getAuth())
                                          .id(member.get()
                                                    .getId())
                                          .email(memberId)
                                          .build());
            } else {
                // 사용자가 최초로 로그인 한 경우
                dto.setMemberDto(MemberDto.builder()
                                          .isLogined(false)
                                          .email(memberId)
                                          .appleToken(accessToken)
                                          .build());
            }
            return dto;
        } else {
            throw new IllegalArgumentException("올바르지 않은 사용자입니다.");
        }
    }

    public AppleDto appleAutorize() throws Exception {
        String code     = "";
        String state    = "";
        String userName = "";
        String email    = "";
        log.info("here");

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-type", "application/x-www-form-urlencoded");

            // GET 요청을 보내기 위해 URL에 쿼리 매개변수를 추가
            String url = APPLE_AUTH_URL + "/auth/authorize?" + "client_id=" + APPLE_CLIENT_ID + "&redirect_uri=" + APPLE_REDIRECT_URL + "&response_type=code+id_token";

            RestTemplate           restTemplate = new RestTemplate();
            ResponseEntity<String> response     = restTemplate.getForEntity(url, String.class);

            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObj    = (JSONObject) jsonParser.parse(response.getBody());

            log.info("코드 받아오나: " + jsonObj.toJSONString());

        } catch (Exception e) {
            log.info("Error: " + String.valueOf(e));
            throw new Exception("API call failed");
        }

        return AppleDto.builder()
                       .id("test")
                       .appleToken("testAccessToken...")
                       .email(email)
                       .build();
    }

    public AuthTokenDto appleSignUp(String email, String nickname, String token, String name) {

        Member member = newMember(AppleDto.builder()
                                          .email(email)
                                          .nickname(nickname)
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
                                      .auth(Auth.USER.getName())
                                      .id(member.getId())
                                      .email(email)
                                      .build());

            return dto;
        } else {
            throw new IllegalArgumentException("올바르지 않은 사용자입니다.");
        }
    }

    private Member newMember(AppleDto dto) {
        return memberRepository.save(Member.builder()
                                           .email(dto.getEmail())
                                           .memberId(dto.getEmail())
                                           .nickname(dto.getNickname())
                                           .auth(Auth.USER.getName())
                                           .name(dto.getName())
                                           .isActivated(true)
                                           .isDeleted(false)
                                           .type(MemberChannel.APPLE.toString())
                                           .build());
    }

    public AppleDto getAppleInfo(String code) throws Exception {
        if (code == null) throw new Exception("Failed get authorization code");

        String clientSecret = createClientSecret();
        String userId       = "";
        String email        = "";
        String accessToken  = "";

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-type", "application/x-www-form-urlencoded");

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("grant_type", "authorization_code");
            params.add("client_id", APPLE_CLIENT_ID);
            params.add("client_secret", clientSecret);
            params.add("code", code);
            params.add("scope", "name email"); // 원하는 정보의 범위를 설정
            params.add("redirect_uri", APPLE_REDIRECT_URL);

            RestTemplate                              restTemplate = new RestTemplate();
            HttpEntity<MultiValueMap<String, String>> httpEntity   = new HttpEntity<>(params, headers);

            ResponseEntity<String> response = restTemplate.exchange(APPLE_AUTH_URL + "/auth/token", HttpMethod.POST, httpEntity, String.class);

            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObj    = (JSONObject) jsonParser.parse(response.getBody());

            accessToken = String.valueOf(jsonObj.get("access_token"));

            //ID TOKEN을 통해 회원 고유 식별자 받기
            SignedJWT    signedJWT  = SignedJWT.parse(String.valueOf(jsonObj.get("id_token")));
            JWTClaimsSet getPayload = signedJWT.getJWTClaimsSet();

            ObjectMapper objectMapper = new ObjectMapper();
            JSONObject payload = objectMapper.readValue(getPayload.toJSONObject()
                                                                  .toJSONString(), JSONObject.class);

            log.info("getAppleInfo{}", payload);

            userId = String.valueOf(payload.get("sub"));
            email  = String.valueOf(payload.get("email"));

            log.info("apple userId: {}", userId);
            log.info("apple email: {}", email);

        } catch (Exception e) {
            log.info(String.valueOf(e));
            throw new Exception("API call failed");
        }

        return AppleDto.builder()
                       .id(userId)
                       .appleToken(accessToken)
                       .email(email)
                       .build();
    }

    @Transactional
    public Boolean AppleUnlink(String code, String memberId) throws Exception {
        String clientSecret = createClientSecret();
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-type", "application/x-www-form-urlencoded");

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            //            params.add("grant_type", "authorization_code");
            params.add("client_id", APPLE_CLIENT_ID);
            params.add("client_secret", clientSecret);
            params.add("token", code);

            RestTemplate                              restTemplate = new RestTemplate();
            HttpEntity<MultiValueMap<String, String>> httpEntity   = new HttpEntity<>(params, headers);

            ResponseEntity<String> response = restTemplate.exchange(APPLE_AUTH_URL + "/auth/revoke", HttpMethod.POST, httpEntity, String.class);
            log.info("response.getStatusCode().is2xxSuccessful() : {}", response.getStatusCode()
                                                                                .is2xxSuccessful());
            if (response.getStatusCode()
                        .is2xxSuccessful()) {

                Optional<Member> member = memberDomain.findMemberById(memberId);

                if (member.isPresent()) {
                    Member m = member.get();

                    m.isDeleted(true);
                    m.isActivated(false);
                    log.info("member : {}", m.getId());
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
            log.info(String.valueOf(e));
            throw new Exception("API call failed");
        }
    }

    private String createClientSecret() throws Exception {
        Date now = new Date();
        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.ES256).keyID(APPLE_LOGIN_KEY)
                                                                    .build();
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder().issuer(APPLE_TEAM_ID)
                                                           .issueTime(now)
                                                           .expirationTime(new Date(now.getTime() + 3600000))
                                                           .audience(APPLE_AUTH_URL)
                                                           .subject(APPLE_CLIENT_ID)
                                                           .build();

        SignedJWT jwt = new SignedJWT(header, claimsSet);

        try {
            ECPrivateKey ecPrivateKey = getECPrivateKey(getPrivateKey());
            JWSSigner    jwsSigner    = new ECDSASigner(ecPrivateKey);

            jwt.sign(jwsSigner);
        } catch (InvalidKeyException | JOSEException e) {
            throw new Exception("Failed create client secret");
        }

        return jwt.serialize();
    }

    private byte[] getPrivateKey() throws Exception {
        byte[] content = null;
        File   file    = null;

        URL res = getClass().getResource(APPLE_KEY_PATH);

        if ("jar".equals(res.getProtocol())) {
            try {
                InputStream input = getClass().getResourceAsStream(APPLE_KEY_PATH);
                file = File.createTempFile("tempfile", ".tmp");
                OutputStream out = new FileOutputStream(file);

                int    read;
                byte[] bytes = new byte[1024];

                while ((read = input.read(bytes)) != -1) {
                    out.write(bytes, 0, read);
                }

                out.close();
                file.deleteOnExit();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else {
            file = new File(res.getFile());
        }

        if (file.exists()) {
            try (FileReader keyReader = new FileReader(file); PemReader pemReader = new PemReader(keyReader)) {
                PemObject pemObject = pemReader.readPemObject();
                content = pemObject.getContent();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            throw new Exception("File " + file + " not found");
        }

        return content;
    }

    private ECPrivateKey getECPrivateKey(byte[] privateKeyBytes) throws Exception {
        KeyFactory          keyFactory = KeyFactory.getInstance("EC");
        PKCS8EncodedKeySpec keySpec    = new PKCS8EncodedKeySpec(privateKeyBytes);
        return (ECPrivateKey) keyFactory.generatePrivate(keySpec);
    }
}