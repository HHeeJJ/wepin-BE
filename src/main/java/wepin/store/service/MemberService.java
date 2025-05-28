package wepin.store.service;


import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import wepin.store.domain.BlacklistDomain;
import wepin.store.domain.MemberDomain;
import wepin.store.dto.MemberDto;
import wepin.store.dto.MemberDto.SearchDto;
import wepin.store.dto.TopDto;
import wepin.store.entity.Blacklist;
import wepin.store.entity.Follow;
import wepin.store.entity.Member;
import wepin.store.mapper.MemberMapper;
import wepin.store.repository.MemberRepository;
import wepin.store.utils.S3UploadUtils;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {
    private final MemberRepository memberRepository;
    private final FollowService    followService;
    private final FeedService      feedService;
    private final S3UploadUtils    s3UploadUtils;
    private final RedisService     redisService;
    private final BlacklistDomain  blacklistDomain;
    private final MemberDomain     memberDomain;

    private final MemberMapper memberMapper;

    public void updateUserInfo(MemberDto.UpdateDto dto) throws IOException {
        try {

            if (!StringUtils.isEmpty(dto.getMemberId())) {
                Optional<Member> member  = memberRepository.findByIdAndIsDeletedFalseAndIsActivatedTrue(dto.getMemberId());
                String           profile = null;
                if (dto.getProfile() != null) {
                    profile = s3UploadUtils.upload(dto.getProfile(), "user", dto.getMemberId() + "_" + LocalDateTime.now());
                }

                if (member.isPresent()) {
                    member.get()
                          .setName(dto.getName());
                    member.get()
                          .setNickname(dto.getNickname());
                    member.get()
                          .setIntroduce(dto.getIntro());
                    member.get()
                          .setProfile(profile == null ? member.get()
                                                              .getProfile() : profile);

                    memberRepository.save(member.get());
                }
            }

        } catch (Exception e) {
            throw e;
        }
    }


    public List<SearchDto> findAll() {
        return memberRepository.findByIsDeletedFalseAndIsActivatedTrue()
                               .stream()
                               .map(m -> SearchDto.builder()
                                                  .profile(m.getProfile())
                                                  .memberId(m.getId())
                                                  .nickname(m.getNickname())
                                                  .intro(m.getIntroduce())
                                                  .build())
                               .collect(Collectors.toList());
    }

    public Page<Member> findAllNotInBlacklistAndBan(String memberId, Pageable pageable) {
        return memberDomain.findAllNotInBlacklistAndBan(memberId, pageable);
    }

    public List<TopDto> getTop10MemberListByRedis() {
        log.info("Redis");
        return redisService.findTop();
        //        return null;
    }

    public List<TopDto> getTop10MemberList(String memberId) {
        return memberMapper.getTop10MemberList(memberId);
        //        return null;
    }

    public List<MemberDto.SearchDto> searchMember(String nickname) throws Exception {
        try {
            List<Member>    memberList = memberRepository.findByNicknameContainsAndIsDeletedFalseAndIsActivatedTrue(nickname);
            List<Blacklist> blacklist  = blacklistDomain.findAll();

            return memberList.stream()
                             .filter(f -> blacklist.stream()
                                                   .noneMatch(b -> f.getId()
                                                                    .equals(b.getBlack()
                                                                             .getId())))
                             .map(m -> MemberDto.SearchDto.builder()
                                                          .memberId(m.getId())
                                                          .nickname(m.getNickname())
                                                          .intro(m.getIntroduce())
                                                          .profile(m.getProfile())
                                                          .build())
                             .collect(Collectors.toList());

        } catch (Exception e) {
            log.error(String.valueOf(e));
            throw new Exception(e);
        }
    }

    public MemberDto.ProfileDto getProfileInfo(String memberId) {
        try {
            Optional<Member> member = memberRepository.findByIdAndIsDeletedFalseAndIsActivatedTrue(memberId);

            if (member.isPresent()) {
                return MemberDto.ProfileDto.builder()
                                           .memberId(memberId)
                                           .intro(member.get()
                                                        .getIntroduce())
                                           .nickname(member.get()
                                                           .getNickname())
                                           .name(member.get()
                                                       .getName())
                                           .profile(member.get()
                                                          .getProfile())
                                           .build();
            } else {
                return null;
            }
        } catch (Exception e) {
            log.error(String.valueOf(e));
            return null;
        }
    }

    public MemberDto.UserMainDto getUserMain(String memberId, String myId) {
        Optional<Member> member = memberRepository.findByIdAndIsDeletedFalseAndIsActivatedTrue(memberId);

        if (member.isPresent()) {
            List<Follow> followerList  = followService.getFollowingList(memberId);
            Page<Follow> followerPage  = followService.getFollowerInfoList(memberId, myId, PageRequest.of(0, 100));
            Page<Follow> followingPage = followService.getFollowingInfoList(memberId, myId, PageRequest.of(0, 100));
            Long         feedCnt       = feedService.countByMemberIdAndIsDeletedFalseAndIsActivatedTrue(memberId);

            return MemberDto.UserMainDto.builder()
                                        .memberId(memberId)
                                        .name(member.get()
                                                    .getName())
                                        .nickname(member.get()
                                                        .getNickname())
                                        .intro(member.get()
                                                     .getIntroduce())
                                        .profile(member.get()
                                                       .getProfile())
                                        .feedCnt(feedCnt.intValue())
                                        .followerCnt((int) followerPage.getTotalElements())
                                        .followingCnt((int) followingPage.getTotalElements())
                                        .feedList(feedService.getFeedMain(memberId, myId))
                                        .isFollow(followerList.stream()
                                                              .anyMatch(m -> m.getFollower()
                                                                              .getId()
                                                                              .equals(memberId) && m.getFollowing()
                                                                                                    .getId()
                                                                                                    .equals(myId)))
                                        .build();
        } else {
            log.error("존재하지 않는 사용자 memberId : " + memberId);
            return null;
        }
    }

    public Optional<Member> findByIdAndIsDeletedFalseAndIsActivatedTrue(String id) {
        return memberRepository.findByIdAndIsDeletedFalseAndIsActivatedTrue(id);
    }


    public MemberDto isExistNickname(String nickname) {
        Optional<Member> member = memberRepository.findByNicknameAndIsDeletedFalseAndIsActivatedTrue(nickname);

        return MemberDto.builder()
                        .isExist(member.isPresent())
                        .build();
    }

    public Boolean isLogined(String id) {
        Optional<Member> member = memberRepository.findByIdAndIsDeletedFalseAndIsActivatedTrue(id);

        return member.isPresent();
    }

    public MemberDto findByMemberIdAndIsDeletedFalseAndIsActivatedTrue(String memberId) {
        Optional<Member> member = memberRepository.findByMemberIdAndIsDeletedFalseAndIsActivatedTrue(memberId);

        if (member.isPresent()) {
            return MemberDto.builder()
                            .nickname(member.get()
                                            .getNickname())
                            .username(member.get()
                                            .getName())
                            .build();
        } else {
            return null;
        }
    }

    public MemberDto findByMemberIdAndPassword(String memberId, String memberPwd) {
        Optional<Member> member = memberRepository.findByMemberIdAndPassword(memberId, memberPwd);

        if (member.isPresent()) {
            return MemberDto.builder()
                            .nickname(member.get()
                                            .getNickname())
                            .username(member.get()
                                            .getName())
                            .build();
        } else {
            return null;
        }
    }
}
