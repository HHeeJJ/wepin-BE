package wepin.store.service;


import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

import javax.transaction.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import wepin.store.dto.LikeDto;
import wepin.store.entity.Feed;
import wepin.store.entity.Likes;
import wepin.store.entity.Member;
import wepin.store.repository.LikeRepository;


@Slf4j
@Component
@EnableAsync
@RequiredArgsConstructor
public class ScheduleService {

    private final RedisService redisService;

    //    @Scheduled(cron = "0 0 */1 * * *")
    //    @Scheduled(cron = "0 0 9 * * *")
    //    public void setTop10() {
    //        redisService.topSave();
    //    }

    //    @Scheduled(cron = "0 0 */1 * * *")
    //    public void setAllMember() {
    //        redisService.allMemberSave();
    //    }
}
