package wepin.store.utils;

import static wepin.store.entity.QBan.ban1;
import static wepin.store.entity.QBlacklist.blacklist;

import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;

import org.springframework.stereotype.Service;

import java.util.List;

import lombok.RequiredArgsConstructor;
import wepin.store.enumeration.MemberStatus;

@RequiredArgsConstructor
@Service
public class CommonBuilder {

    private final JPAQueryFactory factory;

    public List<String> blacklistIds(String memberId) {
        return factory.select(new CaseBuilder().when(blacklist.member.id.eq(memberId))
                                               .then(blacklist.black.id)
                                               .otherwise(blacklist.member.id))
                      .from(blacklist)
                      .where(blacklist.status.in(MemberStatus.BLACK.getName(), MemberStatus.REQUEST.getName())
                                             .and(blacklist.isActivated.eq(true))
                                             .and(blacklist.isDeleted.eq(false))
                                             .and(blacklist.member.id.eq(memberId)
                                                                     .or(blacklist.black.id.eq(memberId))))
                      .groupBy(blacklist.black.id)
                      .fetch();


    }

    public List<String> banIds(String memberId) {
        return factory.select(new CaseBuilder().when(ban1.member.id.eq(memberId))
                                               .then(ban1.ban.id)
                                               .otherwise(ban1.member.id))
                      .from(ban1)
                      .where((ban1.member.id.eq(memberId)
                                            .or(ban1.ban.id.eq(memberId))).and(ban1.isActivated.eq(true))
                                                                          .and(ban1.isDeleted.eq(false)))
                      .fetch();

    }

}
