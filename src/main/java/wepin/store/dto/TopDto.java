package wepin.store.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TopDto {


    private String  memberId;
    private String  name;
    private String  nickname;
    private Integer cnt;


}

