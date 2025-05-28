package wepin.store.domain;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import wepin.store.entity.Code;
import wepin.store.repository.CodeRepository;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CodeDomain {

    private final CodeRepository codeRepository;

    public Optional<Code> getCode(String cdId, String cdClsId) {
        return codeRepository.findByCdIdAndCdClsIdAndIsActivatedTrueAndIsDeletedFalse(cdId, cdClsId);
    }

    public List<Code> getCodeListByCdClsId(String cdClsId) {
        return codeRepository.findByCdClsIdAndIsActivatedTrueAndIsDeletedFalse(cdClsId);
    }
}
