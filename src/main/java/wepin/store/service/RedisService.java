package wepin.store.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

import javax.transaction.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import wepin.store.dto.FeedDto;
import wepin.store.dto.MemberDto.SearchDto;
import wepin.store.dto.TopDto;
import wepin.store.mapper.MemberMapper;
import wepin.store.repository.MemberRepository;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RedisService {

    private final MemberMapper     memberMapper;
    private final MemberRepository memberRepository;
    ObjectMapper mapper = new ObjectMapper();

    private final RedisTemplate redisTemplate;

    //    public List<TopDto> topSave() {
    //        try {
    //            ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
    //
    //            String value = mapper.writeValueAsString(memberMapper.getTop10MemberList());
    //            valueOperations.set("top", value);
    //
    //            Gson     gson    = new Gson();
    //            TopDto[] topList = gson.fromJson(valueOperations.get("top"), TopDto[].class);
    //
    //            return Arrays.asList(topList);
    //
    //        } catch (Exception e) {
    //            log.error(String.valueOf(e));
    //            return null;
    //        }
    //    }

    public List<SearchDto> allMemberSave() {
        try {
            ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();

            String value = mapper.writeValueAsString(memberRepository.findByIsDeletedFalseAndIsActivatedTrue());
            valueOperations.set("member", value);

            Gson        gson = new Gson();
            SearchDto[] list = gson.fromJson(valueOperations.get("member"), SearchDto[].class);

            return Arrays.asList(list);

        } catch (Exception e) {
            log.error(String.valueOf(e));
            return null;
        }
    }

    public List<FeedDto.MainDto> save(String key, List<FeedDto.MainDto> dto) {
        try {
            ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();

            String value = mapper.writeValueAsString(dto);
            valueOperations.set(key, value);

            Gson              gson          = new Gson();
            FeedDto.MainDto[] mainFeedLists = gson.fromJson(valueOperations.get(key), FeedDto.MainDto[].class);

            return Arrays.asList(mainFeedLists);

        } catch (Exception e) {
            log.error(String.valueOf(e));
            return null;
        }
    }

    public List<?> find(String key) {
        try {
            ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();

            Gson              gson          = new Gson();
            FeedDto.MainDto[] mainFeedLists = gson.fromJson(valueOperations.get(key), FeedDto.MainDto[].class);

            log.info(valueOperations.get(key));

            return Arrays.asList(mainFeedLists);
        } catch (Exception e) {
            log.error(String.valueOf(e));
            return null;
        }

    }

    public List<TopDto> findTop() {
        try {
            ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();

            Gson     gson    = new Gson();
            TopDto[] topList = gson.fromJson(valueOperations.get("top"), TopDto[].class);

            return Arrays.asList(topList);

        } catch (Exception e) {
            log.error(String.valueOf(e));
            return null;
        }

    }
}
