package wepin.store.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

import wepin.store.dto.FollowCountDto;

@Mapper
public interface FollowMapper {

    Long countByFollowerId(FollowCountDto dto);

    Long countByFollowingId(FollowCountDto dto);


    List<String> getFollowerIdList(FollowCountDto dto);

    List<String> getFollowingIdList(FollowCountDto dto);
}
