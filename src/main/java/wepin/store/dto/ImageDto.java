package wepin.store.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ImageDto {

    String id;
    String imgUrl;
    String pinId;
    Integer uiSeq;
}