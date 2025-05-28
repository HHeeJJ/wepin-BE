package wepin.store.dto;

import lombok.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import wepin.store.enumeration.MemberChannel;
import wepin.store.service.OAuthLoginParams;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RedisDto {

   private String memberId;
   private String key;
}