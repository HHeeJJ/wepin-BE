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
public class PinDto {

    String         id;
    String         addr; // 도로명
    String         addrDetail;
    String         addrStreet; // 지번
    Double         lat; // 위도
    Double         lng; // 경도
    Boolean        isMain;
    String         feedId;
    String         name;
    Integer        uiSeq;
    String         mainUrl;
    String         subUrl;
    List<String>   imgStringList;
    List<ImageDto> imageList;


}