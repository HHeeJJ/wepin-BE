package wepin.store.entity;


import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.GenericGenerator;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import io.swagger.annotations.ApiModelProperty;
import lombok.experimental.SuperBuilder;
import wepin.store.dto.PinDto;


@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "PIN")
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Pin extends BaseDatetimeExtended implements Serializable, Cloneable {

    @Id
    @ApiModelProperty(notes = "ID")
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "ID", columnDefinition = "VARCHAR(40) COMMENT '관리 ID'", updatable = false, nullable = false, unique = true, length = 40)
    private String id;

    @Column(name = "ADDRESS")
    @Comment("주소")
    private String address;

    @Column(name = "LATITUDE")
    @Comment("위도")
    private Double latitude;

    @Column(name = "LONGITUDE")
    @Comment("경도")
    private Double longitude;

    @Column(name = "IS_MAIN")
    @Comment("대표 여부")
    private Boolean isMain;

    @Column(name = "UI_SEQ")
    @Comment("순서")
    private Integer uiSeq;

    @Column(name = "NAME")
    @Comment("장소 이름")
    private String name;

    @Column(name = "ADDRESS_DETAIL")
    @Comment("상세 주소")
    private String addressDetail;

    @Column(name = "ADDRESS_STREET")
    @Comment("상세 주소")
    private String addressStreet;

    @Column(name = "FEED_ID")
    @Comment("FEED ID")
    private String feedId;

    //    @ManyToOne
    //    private Feed feed;

    @OneToMany(mappedBy = "pin")
    private List<FeedPin> feedPinList;

    @OneToMany(mappedBy = "pin")
    private List<Image> imageList;

    @Override
    public Pin clone() throws CloneNotSupportedException {
        Pin cloned = (Pin) super.clone();
        cloned.id = null; // id는 복사하지 않음
        return cloned;
    }

    public PinDto toDto() {
        return PinDto.builder()
                     .lng(this.getLongitude())
                     .lat(this.getLatitude())
                     .id(this.getId())
                     .name(this.getName())
                     .addr(this.getAddress())
                     .addrStreet(this.getAddressStreet())
                     .addrDetail(this.getAddressDetail())
                     .build();
    }
}