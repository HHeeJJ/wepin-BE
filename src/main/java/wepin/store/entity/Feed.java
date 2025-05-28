package wepin.store.entity;


import org.hibernate.annotations.Comment;
import org.hibernate.annotations.GenericGenerator;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@Entity
@Table(name = "FEED")
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Feed extends BaseDatetimeExtended implements Serializable {

//    @Id
//    @ApiModelProperty(notes = "ID")
//    @GeneratedValue(generator = "uuid2")
//    @GenericGenerator(name = "uuid2", strategy = "uuid2")
//    @Column(name = "ID", columnDefinition = "VARCHAR(40) COMMENT '관리 ID'", updatable = false, nullable = false, unique = true, length = 40)

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "ID", updatable = false, nullable = false, length = 40)
    private String id;

    @Column(name = "TITLE")
    @Comment("제목")
    private String title;

    @Column(name = "SHARE_URL")
    @Comment("공유하기 주소")
    private String shareUrl;

    @Column(name = "DESCRIPTION")
    @Comment("내용")
    private String description;

    @Column(name = "IMG_URL")
    @Comment("대표 이미지")
    private String imgUrl;

    @Column(name = "ZOOM_LEVEL")
    @Comment("줌 레벨")
    private Integer zoomLevel;


    //    @Column(name = "MEMBER_ID")
    //    @Comment("멤버 ID")
    //    private String memberId;

    @ManyToOne
    private Member member;

    @ManyToOne
    @JoinColumn(name = "CUSTOM_PIN_ID")
    private CustomPin customPin;

    @OneToMany(mappedBy = "feed", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<FeedPin> feedPinList;

    @OneToMany(mappedBy = "feed", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Reply> replyList;

    @OneToMany(mappedBy = "feed")
    private List<Likes> likeList;

    @OneToMany(mappedBy = "feed", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<FeedTag> feedTagList;

}