package wepin.store.domain;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import wepin.store.entity.Tag;
import wepin.store.repository.TagRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class TagDomain {

    private final TagRepository tagRepository;

    public Tag update(Tag tag) {
        try {
            return tagRepository.save(tag);
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    public Tag create(String tag) {
        try {
            return tagRepository.save(Tag.builder()
                                         .tagName(tag)
                                         .isActivated(true)
                                         .isDeleted(false)
                                         .usedCnt(1)
                                         .createdAt(LocalDateTime.now())
                                         .build());
        } catch (Exception e) {
            log.error(String.valueOf(e));
            return null;
        }
    }

    public Tag findByTagName(String tagName) {
        return tagRepository.findByTagName(tagName)
                            .orElse(null);
    }
}
