package wepin.store.domain;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import wepin.store.entity.Pin;
import wepin.store.repository.PinRepository;

@Service
@RequiredArgsConstructor
public class PinDomain {

    private final PinRepository pinRepository;

    public List<Pin> saveAll(List<Pin> list) {
        try {
            return pinRepository.saveAll(list);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Pin save(Pin pin) {
        try {
            return pinRepository.save(pin);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //    public List<Pin> deletePin(String feedId) {
    //
    //        List<Pin> pinList = pinRepository.findByFeedIdAndIsDeletedFalseAndIsActivatedTrue(feedId);
    //
    //        List<Pin> deleteList = pinList.stream()
    //                                      .map(m -> {
    //                                          m.delete();
    //                                          return m;
    //                                      })
    //                                      .collect(Collectors.toList());
    //
    //        if (deleteList.size() > 0) {
    //            pinRepository.saveAll(deleteList);
    //        }
    //
    //        return deleteList;
    //    }

    public Optional<Pin> findById(String id) {
        return pinRepository.findByIdAndIsDeletedFalseAndIsActivatedTrue(id);
    }

}
