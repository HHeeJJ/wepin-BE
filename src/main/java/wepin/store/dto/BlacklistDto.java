package wepin.store.dto;

import org.apache.commons.lang3.ObjectUtils;

import java.time.format.DateTimeFormatter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import wepin.store.entity.Blacklist;

@Data
@Builder
public class BlacklistDto {

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ListDto {
        String id;
        String memberNick;
        String blackNick;
        String feedId;
        String feedTitle;
        String memberId;
        String blackId;
        String cdId;
        String reason;
        String detail;
        String status;
        String createdAt;

        public static ListDto toDto(Blacklist entity) {
            return ListDto.builder()
                          .id(entity.getId())
                          .memberNick(entity.getMember()
                                            .getNickname())
                          .blackNick(entity.getBlack()
                                           .getNickname())
                          .feedId(!ObjectUtils.isEmpty(entity.getFeed()) ? entity.getFeed()
                                                                                 .getId() : null)
                          .feedTitle(!ObjectUtils.isEmpty(entity.getFeed()) ? entity.getFeed()
                                                                                    .getTitle() : null)
                          .memberId(entity.getMember()
                                          .getId())
                          .blackId(entity.getBlack()
                                         .getId())
                          .reason(entity.getReason()
                                        .getCdNm())
                          .cdId(entity.getReason()
                                      .getCdId())
                          .detail(entity.getReasonDetail())
                          .createdAt(entity.getCreatedAt()
                                           .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                          .build();
        }
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RequestDto {
        String memberId;
        String blackId;
        String reason;
        String detail;
        String feedId;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UpdateDto {
        String id;
        String status;
    }


}