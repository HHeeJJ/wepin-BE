package wepin.store.controller;


import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.stream.Collectors;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import wepin.store.dto.CommonPage;
import wepin.store.dto.CommonResponse;
import wepin.store.dto.MemberDto;
import wepin.store.dto.MemberDto.SearchDto;
import wepin.store.entity.Member;
import wepin.store.enumeration.StatusCode;
import wepin.store.service.MemberService;
import wepin.store.service.RegisterMailService;


@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/member")
@CrossOrigin
public class MemberController {
    private final MemberService       memberService;
    private final RegisterMailService registerMailService;

    @ApiOperation(value = "검색", notes = "")
    @GetMapping("/all")
    public ResponseEntity<CommonResponse> getAllMember(@ApiParam(name = "memberId", value = "멤버 ID") @RequestParam(name = "memberId") String memberId,
                                                       Pageable pageable) {

        try {
            if (!StringUtils.isEmpty(memberId)) {
                Pageable     newPageable = PageRequest.of(pageable.getPageNumber(), 10000, pageable.getSort());
                Page<Member> memberPage  = memberService.findAllNotInBlacklistAndBan(memberId, newPageable);

                return ResponseEntity.ok(CommonResponse.builder()
                                                       .statusCode(StatusCode.SUCCESS)
                                                       .message("모든 멤버 조회.")
                                                       .data(CommonPage.builder()
                                                                       .totalCount(memberPage.getTotalElements())
                                                                       .hasNext(memberPage.hasNext())
                                                                       .pageNumber(memberPage.getNumber())
                                                                       .lists(memberPage.getContent()
                                                                                        .stream()
                                                                                        .map(m -> SearchDto.builder()
                                                                                                           .profile(m.getProfile())
                                                                                                           .memberId(m.getId())
                                                                                                           .nickname(m.getNickname())
                                                                                                           .intro(m.getIntroduce())
                                                                                                           .build())
                                                                                        .collect(Collectors.toList()))
                                                                       .build())
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

    @ApiOperation(value = "검색", notes = "")
    @GetMapping("/search")
    public ResponseEntity<CommonResponse> searchMember(@ApiParam(name = "nickname", value = "닉네임") @RequestParam String nickname) {

        try {
            if (!StringUtils.isEmpty(nickname)) {
                return ResponseEntity.ok(CommonResponse.builder()
                                                       .statusCode(StatusCode.SUCCESS)
                                                       .message("nickname 조회되었다.")
                                                       .data(memberService.searchMember(nickname))
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

    @ApiOperation(value = "사용자 정보 (사진, 소개글) 가져오기", notes = "")
    @GetMapping("/user-info")
    public ResponseEntity<CommonResponse> getProfileInfo(@ApiParam(name = "memberId", value = "멤버 ID") @RequestParam String memberId) {

        try {
            if (!StringUtils.isEmpty(memberId)) {
                return ResponseEntity.ok(CommonResponse.builder()
                                                       .statusCode(StatusCode.SUCCESS)
                                                       .message("조회되었습니다.")
                                                       .data(memberService.getProfileInfo(memberId))
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

    @ApiOperation(value = "사용자 정보 업데이트", notes = "사용자 정보 업데이트")
    @PostMapping("/user-update")
    public ResponseEntity<CommonResponse> updateUserInfo(@RequestParam(value = "intro", required = false) String intro,
                                                         @RequestPart(value = "profile", required = false) MultipartFile profile,
                                                         @RequestParam("memberId") String memberId, @RequestParam("name") String name,
                                                         @RequestParam("nickname") String nickname) {

        try {
            if (!StringUtils.isEmpty(memberId) && !StringUtils.isEmpty(name) && !StringUtils.isEmpty(nickname)) {
                memberService.updateUserInfo(MemberDto.UpdateDto.builder()
                                                                .memberId(memberId)
                                                                .intro(intro)
                                                                .profile(profile)
                                                                .nickname(nickname)
                                                                .name(name)
                                                                .build());
                return ResponseEntity.ok(CommonResponse.builder()
                                                       .statusCode(StatusCode.SUCCESS)
                                                       .message("업데이트 되었습니다.")
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

    @ApiOperation(value = "사용자 메인 화면", notes = "사용자 개인 메인 화면 정보 불러오기")
    @GetMapping("/user-main")
    public ResponseEntity<CommonResponse> getUserMain(@ApiParam(name = "memberId", value = "멤버 ID") @RequestParam String memberId,
                                                      @RequestParam String myId) {

        try {
            if (!StringUtils.isEmpty(memberId) && !StringUtils.isEmpty(myId)) {
                return ResponseEntity.ok(CommonResponse.builder()
                                                       .statusCode(StatusCode.SUCCESS)
                                                       .message("조회되었습니다.")
                                                       .data(memberService.getUserMain(memberId, myId))
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


    @ApiOperation(value = "인기 사용자 가져오기", notes = "")
    @GetMapping("/top")
    public ResponseEntity<CommonResponse> getTop10MemberList(@RequestParam String memberId) {

        try {
            return ResponseEntity.ok(CommonResponse.builder()
                                                   .statusCode(StatusCode.SUCCESS)
                                                   .message("인기 사용자 조회~!!!!!!!!")
                                                   //                                                   .data(memberService.getTop10MemberListByRedis())
                                                   .data(memberService.getTop10MemberList(memberId))

                                                   .build());

        } catch (Exception ex) {
            return ResponseEntity.ok(CommonResponse.builder()
                                                   .statusCode(StatusCode.NOT_FOUND)
                                                   .build());
        }
    }


    @ApiOperation(value = "로그인 테스트", notes = "로그인 TEST API")
    @GetMapping({"/login"})
    public ResponseEntity<CommonResponse> login(@ApiParam(name = "memberId", value = "멤버 ID") @RequestParam(name = "memberId") String memberId,
                                                @ApiParam(name = "memberPwd", value = "멤버 pwd") @RequestParam(name = "memberPwd") String memberPwd) {
        if (!StringUtils.isEmpty(memberId) && !StringUtils.isEmpty(memberPwd) && memberService.findByMemberIdAndPassword(memberId,
                                                                                                                         memberPwd) != null) {
            return ResponseEntity.ok(CommonResponse.builder()
                                                   .statusCode(StatusCode.SUCCESS)
                                                   .message("조회되었습니다!!!!!!잇츠 로그인 성공")
                                                   .data(memberService.findByMemberIdAndPassword(memberId, memberPwd))
                                                   .build());
        } else {
            return ResponseEntity.ok(CommonResponse.builder()
                                                   .statusCode(StatusCode.NOT_FOUND)
                                                   .message("로그인실패...")
                                                   .build());
        }
    }

    @ApiOperation(value = "이메일 인증 테스트", notes = "이메일 인증 TEST API")
    @GetMapping({"/emailCertification"})
    public ResponseEntity<CommonResponse> login(
            @ApiParam(name = "memberId", value = "멤버 ID") @RequestParam(name = "memberId") String memberId) throws Exception {
        // 이메일 넣으면 해당 이메일로 인증번호 전송되도록 해놓음
        if (!StringUtils.isEmpty(memberId)) {
            // 등록된 사용자일때 이메일 인증!!!
            // 전송!!!

            String code = registerMailService.sendSimpleMessage(memberId);
            System.out.println("인증코드 : " + code);

            return ResponseEntity.ok(CommonResponse.builder()
                                                   .statusCode(StatusCode.SUCCESS)
                                                   .message("이메일 인증코드는 " + code + " 입니다")
                                                   .build());
        } else {
            return ResponseEntity.ok(CommonResponse.builder()
                                                   .statusCode(StatusCode.NOT_FOUND)
                                                   .message("없는 이메일입니다.")
                                                   .build());
        }
    }
}
