package wepin.store.dto;

import java.time.format.DateTimeFormatter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import wepin.store.entity.Push;
import wepin.store.entity.PushHistory;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PushDto {

    String  memberId;
    boolean isAllActivated;
    boolean isFeedActivated;
    boolean isFollowActivated;
    boolean isReplyActivated;
    boolean isLikeActivated;
    boolean isExist;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PushHistoryDto {
        String memberId;
        String message;
        String type;
        String refId;
        String senderId;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PushHistoryListDto {
        String memberId;
        String feedId;
        //        String replyId;
        String message;
        String profile;
        String followingId;
        String createdAt;
        String friendId;

        public static PushHistoryListDto toDto(PushHistory entity) {
            String type  = entity.getType();
            String refId = entity.getRefId();

            switch (type) {
                case "FEED_CREATE":
                case "LIKE":
                case "REPLY":
                case "DISLIKE":
                    return PushHistoryListDto.builder()
                                             .memberId(entity.getMember()
                                                             .getId())
                                             .feedId(refId)
                                             .friendId(entity.getSender()
                                                             .getNickname())
                                             .profile(entity.getSender()
                                                            .getProfile())
                                             .message(entity.getMessage())
                                             .createdAt(entity.getCreatedAt()
                                                              .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                                             .build();
                case "FOLLOW":
                case "UNFOLLOW":
                    return PushHistoryListDto.builder()
                                             .memberId(entity.getMember()
                                                             .getId())
                                             .followingId(refId)
                                             .friendId(entity.getSender()
                                                             .getNickname())
                                             .profile(entity.getSender()
                                                            .getProfile())
                                             .message(entity.getMessage())
                                             .createdAt(entity.getCreatedAt()
                                                              .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                                             .build();
                default:
                    return null;
            }
        }

    }


    public static PushDto toDto(Push push) {
        return PushDto.builder()
                      .isAllActivated(push.getIsAllActivated())
                      .isFeedActivated(push.getIsFeedActivated())
                      .isFollowActivated(push.getIsFollowActivated())
                      .isReplyActivated(push.getIsReplyActivated())
                      .isLikeActivated(push.getIsLikeActivated())
                      .memberId(push.getMember()
                                    .getId())
                      .build();
    }
}