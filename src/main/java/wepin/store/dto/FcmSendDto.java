package wepin.store.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
public class FcmSendDto {

    private String token;
    private String title;
    private String body;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TokenInfoDto {
        private String token;
        private String memberId;
        private String updatedAt;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PutDataDto {
        private String key;
        private String value;
    }
}
