package wepin.store.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LogDto {

    String id;
    String memberId;
    String name;
    String message;
    String type;
    String loc;
    String createdAt;

}