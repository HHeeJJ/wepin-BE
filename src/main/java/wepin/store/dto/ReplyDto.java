package wepin.store.dto;

import java.time.format.DateTimeFormatter;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import wepin.store.entity.Reply;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReplyDto {

    String  id;
    String  desc;
    Boolean isLike;
    String  feedId;
    Integer uiSeq;
    String  memberId;
    String  nickNm;
    String  profile;
    String  createdAt;
    String  pushSenderId;

    public static ReplyDto toDto(Reply reply) {
        return ReplyDto.builder()
                       .id(reply.getId())
                       .feedId(reply.getFeed()
                                    .getId())
                       .memberId(reply.getMember()
                                      .getId())
                       .nickNm(reply.getMember()
                                    .getNickname())
                       .desc(reply.getDescription())
                       .createdAt(reply.getCreatedAt()
                                       .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                       .profile(reply.getMember()
                                     .getProfile())
                       .build();
    }


    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FeedDetailReplyDto {
        List<ReplyDto> replyList;
        long           totalCount;
        long           pageNumber;
        boolean        hasBack;
        boolean        hasNext;

    }


    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CreateDto {
        String  desc;
        String  feedId;
        String  memberId;
        Boolean isLike;
        Integer uiSeq;

    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RemoveDto {
        String replyId;
        String feedId;
        String memberId;
    }

}