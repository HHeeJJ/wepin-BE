package wepin.store.service;


import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

import javax.transaction.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import wepin.store.domain.BlacklistDomain;
import wepin.store.domain.CodeDomain;
import wepin.store.domain.FeedDomain;
import wepin.store.domain.MemberDomain;
import wepin.store.dto.BlacklistDto;
import wepin.store.entity.Blacklist;
import wepin.store.entity.Code;
import wepin.store.entity.Feed;
import wepin.store.entity.Member;
import wepin.store.enumeration.CodeCls;
import wepin.store.enumeration.MemberStatus;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BlacklistService {

    private final BlacklistDomain blacklistDomain;
    private final MemberDomain    memberDomain;
    private final FeedDomain      feedDomain;
    private final CodeDomain      codeDomain;

    public Boolean isBlacklist(String blackId) {
        return blacklistDomain.isBlacklist(blackId);
    }

    public Page<Blacklist> getBlackList(String status, Pageable pageable) {
        return blacklistDomain.getBlacklist(status, pageable);
    }

    public void request(BlacklistDto.RequestDto dto) {
        try {
            Optional<Member> member = memberDomain.findMemberById(dto.getMemberId());
            Optional<Member> black  = memberDomain.findMemberById(dto.getBlackId());
            Optional<Code>   code   = codeDomain.getCode(dto.getReason(), CodeCls.BLOCK.getName());
            Optional<Feed>   feed   = Optional.empty();
            if (!StringUtils.isEmpty(dto.getFeedId())) {
                feed = feedDomain.findById(dto.getFeedId());
            }

            if (member.isPresent() && black.isPresent() && code.isPresent()) {
                blacklistDomain.save(Blacklist.builder()
                                              .black(black.get())
                                              .member(member.get())
                                              .feed(feed.orElse(null))
                                              .reason(code.get())
                                              .reasonDetail(dto.getDetail())
                                              .status(MemberStatus.REQUEST.getName())
                                              .isActivated(true)
                                              .isDeleted(false)
                                              .build());
            } else {
                log.error("신고 에러 발생: [memberId] " + dto.getMemberId() + " [blackId] " + dto.getBlackId() + " [feedId] " + dto.getFeedId());
            }
        } catch (Exception e) {
            log.error(String.valueOf(e));
        }
    }

    public void update(BlacklistDto.UpdateDto dto) {
        try {
            Optional<Blacklist> black = blacklistDomain.findById(dto.getId());

            if (black.isPresent()) {
                Blacklist blacklist = black.get();

                blacklist.setStatus(dto.getStatus()
                                       .toUpperCase());

                blacklistDomain.save(blacklist);
            } else {
                log.error("존재하지 않는 신고 건 : [id] " + dto.getId());
            }
        } catch (Exception e) {
            log.error(String.valueOf(e));
        }
    }
}
