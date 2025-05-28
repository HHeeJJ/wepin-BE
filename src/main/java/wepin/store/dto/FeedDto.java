package wepin.store.dto;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import wepin.store.entity.Feed;
import wepin.store.entity.FeedTag;
import wepin.store.entity.Image;
import wepin.store.entity.MemberCurrentPin;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FeedDto {

    String id;


    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FeedMainDto {
        String  feedId;
        String  imgUrl;
        String  title;
        Integer pinCnt;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MainDto {
        String        id;
        String        title;
        String        desc;
        String        nickname;
        String        memberId;
        Boolean       isFollow;
        Boolean       isMine;
        String        imgUrl;
        String        profile;
        LocalDateTime createdAt;
        Integer       replyCnt;
        Integer       likeCnt;
        Boolean       isLike;
        List<PinDto>  pinList;
        String        mainUrl;
        String        subUrl;

        public static MainDto toDto(Feed feed, String memberId) {
            List<MemberCurrentPin> memberCurrentPinList = feed.getMember()
                                                              .getMemberCurrentPinList();

            return MainDto.builder()
                          .id(feed.getId())
                          .title(feed.getTitle())
                          .desc(feed.getDescription())
                          .imgUrl(feed.getImgUrl())
                          .profile(feed.getMember()
                                       .getProfile())
                          .memberId(feed.getMember()
                                        .getId())
                          .mainUrl(memberCurrentPinList.isEmpty() ? null : memberCurrentPinList.get(0)
                                                                                               .getCustomPin()
                                                                                               .getMainUrl())
                          .subUrl(memberCurrentPinList.isEmpty() ? null : memberCurrentPinList.get(0)
                                                                                              .getCustomPin()
                                                                                              .getSubUrl())
                          .isMine(feed.getMember()
                                      .getId()
                                      .equals(memberId))
                          .isFollow(feed.getMember()
                                        .getFollowerList()
                                        .stream()
                                        .anyMatch(f -> f.getFollowing()
                                                        .getId()
                                                        .equals(memberId)))
                          .createdAt(feed.getCreatedAt())
                          .pinList(feed.getFeedPinList()
                                       .stream()
                                       .map(p -> PinDto.builder()
                                                       .id(p.getPin()
                                                            .getId())
                                                       .isMain(p.getIsMain())
                                                       .lat(p.getPin()
                                                             .getLatitude())
                                                       .lng(p.getPin()
                                                             .getLongitude())
                                                       .addr(p.getPin()
                                                              .getAddress())
                                                       .addrDetail(p.getPin()
                                                                    .getAddressDetail())
                                                       .addrStreet(p.getPin()
                                                                    .getAddressStreet())
                                                       .imageList(p.getPin()
                                                                   .getImageList()
                                                                   .stream()
                                                                   .sorted(Comparator.comparing(Image::getUiSeq))
                                                                   .map(i -> ImageDto.builder()
                                                                                     .id(i.getId())
                                                                                     .pinId(i.getPin()
                                                                                             .getId())
                                                                                     .imgUrl(i.getImage())
                                                                                     .uiSeq(i.getUiSeq())
                                                                                     .build())
                                                                   .collect(Collectors.toList()))
                                                       .build())
                                       .collect(Collectors.toList()))
                          .nickname(feed.getMember()
                                        .getNickname())
                          .replyCnt(feed.getReplyList()
                                        .size())
                          .likeCnt(feed.getLikeList()
                                       .size())
                          .isLike(feed.getLikeList()
                                      .stream()
                                      .anyMatch(l -> l.getMember()
                                                      .getId()
                                                      .equals(memberId) && l.getFeed()
                                                                            .getId()
                                                                            .equals(feed.getId())))
                          .build();
        }
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DetailDto {
        String                      id;
        String                      title;
        String                      desc;
        String                      nickname;
        String                      memberId;
        Boolean                     isFollow;
        Boolean                     isMine;
        String                      profile;
        LocalDateTime               createdAt;
        ReplyDto.FeedDetailReplyDto replyDto;
        List<PinDto>                pinList;
        Integer                     replyCnt;
        Integer                     likeCnt;
        Integer                     zoomLevel;
        Boolean                     isLike;
        List<String>                tagList;
        String                      mainUrl;
        String                      subUrl;


        public static DetailDto toDto(Feed feed, String memberId) {
            return DetailDto.builder()
                            .id(feed.getId())
                            .title(feed.getTitle())
                            .desc(feed.getDescription())
                            .profile(feed.getMember()
                                         .getProfile())
                            .memberId(feed.getMember()
                                          .getId())
                            .isMine(feed.getMember()
                                        .getId()
                                        .equals(memberId))
                            .isFollow(feed.getMember()
                                          .getFollowerList()
                                          .stream()
                                          .anyMatch(f -> f.getFollowing()
                                                          .getId()
                                                          .equals(memberId)))
                            .createdAt(feed.getCreatedAt())
                            .mainUrl(feed.getCustomPin()
                                         .getMainUrl())
                            .subUrl(feed.getCustomPin()
                                        .getSubUrl())
                            .tagList(feed.getFeedTagList()
                                         .stream()
                                         .sorted(Comparator.comparing(FeedTag::getUiSeq))
                                         .map(t -> t.getTag()
                                                    .getTagName())
                                         .collect(Collectors.toList()))
                            .nickname(feed.getMember()
                                          .getNickname())
                            .replyCnt(feed.getReplyList()
                                          .size())
                            .likeCnt(feed.getLikeList()
                                         .size())
                            .zoomLevel(feed.getZoomLevel())
                            .isLike(feed.getLikeList()
                                        .stream()
                                        .anyMatch(l -> l.getMember()
                                                        .getId()
                                                        .equals(memberId) && l.getFeed()
                                                                              .getId()
                                                                              .equals(feed.getId())))
                            .pinList(feed.getFeedPinList()
                                         .stream()
                                         .map(p -> PinDto.builder()
                                                         .lng(p.getPin()
                                                               .getLongitude())
                                                         .feedId(p.getFeed()
                                                                  .getId())
                                                         .isMain(p.getIsMain())
                                                         .lat(p.getPin()
                                                               .getLatitude())
                                                         .addr(p.getPin()
                                                                .getAddress())
                                                         .addrDetail(p.getPin()
                                                                      .getAddressDetail())
                                                         .addrStreet(p.getPin()
                                                                      .getAddressStreet())
                                                         .id(p.getPin()
                                                              .getId())
                                                         .name(p.getPin()
                                                                .getName())
                                                         .uiSeq(p.getUiSeq())
                                                         .imageList(p.getPin()
                                                                     .getImageList()
                                                                     .stream()
                                                                     .sorted(Comparator.comparing(Image::getUiSeq))
                                                                     .map(i -> ImageDto.builder()
                                                                                       .id(i.getId())
                                                                                       .pinId(i.getPin()
                                                                                               .getId())
                                                                                       .imgUrl(i.getImage())
                                                                                       .uiSeq(i.getUiSeq())
                                                                                       .build())
                                                                     .collect(Collectors.toList()))
                                                         .build())
                                         .collect(Collectors.toList()))
                            .build();
        }
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SaveDto {
        String       title;
        String       desc;
        String       memberId;
        String       img;
        Integer      zoomLevel;
        List<PinDto> pinList;
        List<String> tagList;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DeleteDto {
        String feedId;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MyFeedDto {
        String       feedId;
        String       imgUrl;
        String       title;
        List<PinDto> pinList;
    }


}