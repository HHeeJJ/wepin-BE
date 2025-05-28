package wepin.store.dto;

import java.time.format.DateTimeFormatter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import wepin.store.entity.Ban;

@Data
@Builder
public class BanDto {

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BanCreateDto {
        String memberId;
        String banId;
    }


    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BanDeleteDto {
        String id;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BanListDto {
        String memberId;
        String banId;
        String nickname;
        String imgUrl;
        String id;
        String createdAt;

        public static BanListDto toDto(Ban entity) {
            return BanListDto.builder()
                             .id(entity.getId())
                             .memberId(entity.getMember()
                                             .getId())
                             .banId(entity.getBan()
                                          .getId())
                             .imgUrl(entity.getBan()
                                           .getProfile())
                             .nickname(entity.getBan()
                                             .getNickname())
                             .createdAt(entity.getCreatedAt()
                                              .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                             .build();
        }
    }
}