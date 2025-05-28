package wepin.store.domain;

import static wepin.store.entity.QCustomPin.customPin;
import static wepin.store.entity.QMember.member;
import static wepin.store.entity.QMemberCurrentPin.memberCurrentPin;
import static wepin.store.entity.QMemberCustomPin.memberCustomPin;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import wepin.store.dto.CustomPinDto;
import wepin.store.entity.CustomPin;
import wepin.store.repository.CustomPinRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomPinDomain {

    private final JPAQueryFactory     factory;
    private final CustomPinRepository customPinRepository;

    public Page<CustomPinDto.ListDto> fetchCustomPinDetails(String memberId, Pageable pageable) {
        List<CustomPinDto.ListDto> results = factory.select(
                                                            Projections.constructor(CustomPinDto.ListDto.class, customPin.id, customPin.name, customPin.mainUrl, customPin.subUrl,
                                                                                    memberCustomPin.member.id, memberCustomPin.customPin.id, memberCurrentPin.id,
                                                                                    memberCustomPin.member.id.isNotNull(), memberCurrentPin.id.isNotNull(), customPin.description,
                                                                                    Expressions.stringTemplate("DATE_FORMAT({0}, '%Y-%m-%d %H:%i:%s')", memberCustomPin.endAt), customPin.type))
                                                    .from(customPin)
                                                    .leftJoin(memberCustomPin)
                                                    .on(customPin.id.eq(memberCustomPin.customPin.id)
                                                                    .and(memberCustomPin.member.id.eq(memberId)))
                                                    .leftJoin(member)
                                                    .on(member.id.eq(memberCustomPin.member.id))
                                                    .leftJoin(memberCurrentPin)
                                                    .on(memberCustomPin.customPin.id.eq(memberCurrentPin.customPin.id)
                                                                                    .and(customPin.isActivated.isTrue())
                                                                                    .and(customPin.isDeleted.isFalse())
                                                                                    .and(memberCurrentPin.member.id.eq(memberId)))
                                                    .orderBy(customPin.id.asc())
                                                    .offset(pageable.getOffset())
                                                    .limit(pageable.getPageSize())
                                                    .fetch();

        long total = results.size();

        return new PageImpl<>(results, pageable, total);
    }

    public List<CustomPin> findByType(String type) {
        return customPinRepository.findByType(type);
    }

    public Optional<CustomPin> findById(String id) {
        return customPinRepository.findById(id);
    }
}
