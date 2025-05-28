package wepin.store.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LikeDto {
    Long   count;
    String feedId;
    String memberId;
    String nickNm;
    String pushSenderId;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SaveDeleteDto {
        String feedId;
        String memberId;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FeedInLikeDto {
        Integer count;
        Boolean isLike;
    }
}