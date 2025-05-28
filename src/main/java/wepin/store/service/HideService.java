package wepin.store.service;


import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import wepin.store.domain.FeedDomain;
import wepin.store.domain.HideDomain;
import wepin.store.domain.MemberDomain;
import wepin.store.dto.HideDto;
import wepin.store.entity.Feed;
import wepin.store.entity.Hide;
import wepin.store.entity.Member;


@Slf4j
@Service
@RequiredArgsConstructor
public class HideService {

    private final MemberDomain memberDomain;
    private final FeedDomain   feedDomain;
    private final HideDomain   hideDomain;

    public void create(HideDto dto) throws Exception {
        try {
            Optional<Member> member = memberDomain.findMemberById(dto.getMemberId());
            Optional<Feed>   feed   = feedDomain.findById(dto.getFeedId());

            if (member.isPresent() && feed.isPresent()) {
                hideDomain.save(Hide.builder()
                                    .member(member.get())
                                    .feed(feed.get())
                                    .isActivated(true)
                                    .isDeleted(false)
                                    .createdAt(LocalDateTime.now())
                                    .updatedAt(LocalDateTime.now())
                                    .build());

            } else {
                throw new Exception("member feed 둘 중 하나가 존재하지 않습니다.");
            }
        } catch (Exception e) {
            throw e;
        }
    }


}
