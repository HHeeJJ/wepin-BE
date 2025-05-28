package wepin.store.service;


import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import wepin.store.domain.CodeDomain;
import wepin.store.domain.FeedDomain;
import wepin.store.domain.HideDomain;
import wepin.store.domain.MemberDomain;
import wepin.store.dto.CodeDto;
import wepin.store.dto.HideDto;
import wepin.store.entity.Code;
import wepin.store.entity.Feed;
import wepin.store.entity.Hide;
import wepin.store.entity.Member;


@Slf4j
@Service
@RequiredArgsConstructor
public class CodeService {

    private final CodeDomain codeDomain;

    public List<CodeDto> getCodeListByCdClsId(String cdClsId) {
        return codeDomain.getCodeListByCdClsId(cdClsId)
                         .stream()
                         .map(m -> CodeDto.builder()
                                          .id(m.getId())
                                          .cdNm(m.getCdNm())
                                          .cdId(m.getCdId())
                                          .cdClsId(m.getCdClsId())
                                          .build())
                         .collect(Collectors.toList());
    }


}
