package wepin.store.service;

import org.springframework.util.MultiValueMap;
import wepin.store.enumeration.MemberChannel;

public interface OAuthLoginParams {
    MemberChannel memberChannel();
    MultiValueMap<String, String> makeBody();
}
