package wepin.store.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FollowDto {

    String followingId; // 팔로우 누른 사람
    String followerId; // 팔로우 당한 사람

    String memberId;
    String name;
    String profile;
    String intro;
    String nickname;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ListDto {
        List<FollowDto> dto;
        Integer         cnt;
    }


}