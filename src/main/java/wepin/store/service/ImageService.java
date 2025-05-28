package wepin.store.service;


import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.transaction.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import wepin.store.entity.Image;
import wepin.store.entity.Pin;
import wepin.store.repository.ImageRepository;
import wepin.store.utils.S3UploadUtils;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ImageService {

    private final S3UploadUtils   s3UploadUtils;
    private final ImageRepository imageRepository;


    public void createPinImage(Pin pin, List<String> fileList) {
        try {
            AtomicInteger idx     = new AtomicInteger(0);
            List<Image>   imgList = new ArrayList<>();

            for (String img : fileList) {
                String imgUrl = s3UploadUtils.upload(img, "pin", pin.getId() + "_" + idx.get());

                imgList.add(Image.builder()
                                 .pin(pin)
                                 .isActivated(true)
                                 .isDeleted(false)
                                 .image(imgUrl)
                                 .uiSeq(idx.get())
                                 .createdAt(LocalDateTime.now())
                                 .updatedAt(LocalDateTime.now())
                                 .build());

                idx.getAndIncrement();
            }

            imageRepository.saveAll(imgList);

        } catch (Exception e) {
            log.error(String.valueOf(e));
        }
    }


}
