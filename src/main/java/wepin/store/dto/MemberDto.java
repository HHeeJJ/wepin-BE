package wepin.store.dto;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberDto {

    private String  id;
    private String  memberId;
    private String  username;
    private String  nickname;
    private String  email;
    private Boolean isExist;
    private Boolean isLogined;
    private Boolean isBlacklist;
    private String  kakaoToken;
    private String  appleToken;
    private Integer cnt;
    private String  auth;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserMainDto {
        private String                    memberId;
        private String                    name;
        private String                    nickname;
        private String                    intro;
        private String                    profile;
        private Integer                   feedCnt;
        private Integer                   followerCnt;
        private Integer                   followingCnt;
        private List<FeedDto.FeedMainDto> feedList;
        private Boolean                   isFollow;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SearchDto {
        private String memberId;
        private String nickname;
        private String intro;
        private String profile;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProfileDto {
        private String memberId;
        private String intro;
        private String profile;
        private String nickname;
        private String name;
        private String curMainUrl;
        private String curSubUrl;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UpdateDto {
        private String        memberId;
        private String        intro;
        private MultipartFile profile;
        private String        name;
        private String        nickname;
    }
}

