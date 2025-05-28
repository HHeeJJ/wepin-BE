package wepin.store.service;


import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import javax.transaction.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import wepin.store.domain.FeedPinDomain;
import wepin.store.domain.PinDomain;
import wepin.store.dto.PinDto;
import wepin.store.entity.Feed;
import wepin.store.entity.FeedPin;
import wepin.store.entity.Pin;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PinService {

    private final ImageService  imageService;
    private final PinDomain     pinDomain;
    private final FeedPinDomain feedPinDomain;

    public List<PinDto> createPin(List<PinDto> list, Feed feed) {
        List<PinDto>  pinList     = new ArrayList<>();
        List<FeedPin> feedPinList = new ArrayList<>();

        try {
            list.forEach(m -> {
                try {
                    Pin pin = pinDomain.save(Pin.builder()
                            .address(m.getAddr())
                            .addressDetail(m.getAddrDetail())
                            .addressStreet(m.getAddrStreet())
                            .feedId(feed.getId())
                            .name(m.getName())
                            .latitude(m.getLat())
                            .longitude(m.getLng())
                            .isActivated(true)
                            .isDeleted(false)
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            //                                                .isMain(m.getIsMain())
                            //                                                .uiSeq(m.getUiSeq())
                            .build());

                    if (!m.getImgStringList()
                          .isEmpty()) {
                        imageService.createPinImage(pin, m.getImgStringList());
                    }

                    feedPinList.add(FeedPin.builder()
                                            .id(UUID.randomUUID().toString())
                                            .feed(feed)
                                           .pin(pin)
                                           .isMain(m.getIsMain())
                                           .uiSeq(m.getUiSeq())
                                           .isActivated(true)
                                           .isDeleted(false)
                                           .build());

                    pinList.add(m);
                } catch (Exception e) {
                    log.error("핀 생성 중 오류 발생: {}", e.getMessage());
                }
            });
            feedPinDomain.saveAll(feedPinList);
        } catch (Exception e) {
            log.error("전체 핀 생성 중 오류 발생: {}", e.getMessage());
        }

        return pinList;
    }

    public List<PinDto> merge(List<PinDto> list, Feed feed) {
        List<PinDto>  pinList     = new ArrayList<>();
        List<FeedPin> feedPinList = new ArrayList<>();

        try {
            AtomicInteger idx = new AtomicInteger(0);
            for (PinDto pinDto : list) {
                Optional<Pin> pinOptional = pinDomain.findById(pinDto.getId());

                if (pinOptional.isPresent()) {
                    Pin pin = pinOptional.get();
                    
                    feedPinList.add(FeedPin.builder()
                                           .feed(feed)
                                           .pin(pin)
                                           .isMain(pinDto.getIsMain())
                                           .uiSeq(idx.get())
                                           .isActivated(true)
                                           .isDeleted(false)
                                           .build());

                    pinList.add(pin.toDto());

                    idx.getAndIncrement();
                }
            }

            feedPinDomain.saveAll(feedPinList);
            return pinList;
        } catch (Exception e) {
            log.error("핀 합치기 중 오류 발생: {}", e.getMessage());
        }

        return pinList;
    }


}
