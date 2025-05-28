package wepin.store.domain;

import org.springframework.stereotype.Service;

import java.util.List;

import lombok.RequiredArgsConstructor;
import wepin.store.entity.FeedPin;
import wepin.store.repository.FeedPinRepository;

@Service
@RequiredArgsConstructor
public class FeedPinDomain {

    private final FeedPinRepository feedPinRepository;

    public List<FeedPin> saveAll(List<FeedPin> list) {
        try {
            return feedPinRepository.saveAll(list);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public FeedPin save(FeedPin feedPin) {
        try {
            return feedPinRepository.save(feedPin);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void deleteAll(List<FeedPin> list) {
        feedPinRepository.deleteAll(list);
    }

    public void delete(FeedPin feedPin) {
        feedPinRepository.delete(feedPin);
    }
}
