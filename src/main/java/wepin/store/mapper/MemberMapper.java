package wepin.store.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

import wepin.store.dto.TopDto;

@Mapper
public interface MemberMapper {

    public List<TopDto> getTop10MemberList(String memberId);
}
