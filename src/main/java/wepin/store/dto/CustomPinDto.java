package wepin.store.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
public class CustomPinDto {


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ListDto {
        String  id;
        String  name;
        String  mainPin;
        String  subPin;
        String  memberId;
        String  customPinId;
        String  currentPinId;
        Boolean isCurrent;
        Boolean hasPin;
        String  description;
        String  endAt;
        String  type;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SelectDto {
        String memberId;
        String customPinId;
    }
}