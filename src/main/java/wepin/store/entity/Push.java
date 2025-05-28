package wepin.store.entity;


import org.hibernate.annotations.GenericGenerator;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import wepin.store.dto.PushDto;


@Entity
@Table(name = "PUSH")
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Push extends BaseDatetimeExtended implements Serializable {

    @Id
    @ApiModelProperty(notes = "ID")
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "ID", columnDefinition = "VARCHAR(40) COMMENT '관리 ID'", updatable = false, nullable = false, unique = true, length = 40)
    private String id;

    @OneToOne
    private Member member;

    @Column(name = "IS_ALL_ACTIVATED")
    private Boolean isAllActivated;

    @Column(name = "IS_FEED_ACTIVATED")
    private Boolean isFeedActivated;

    @Column(name = "IS_REPLY_ACTIVATED")
    private Boolean isReplyActivated;

    @Column(name = "IS_LIKE_ACTIVATED")
    private Boolean isLikeActivated;

    @Column(name = "IS_FOLLOW_ACTIVATED")
    private Boolean isFollowActivated;

    public void update(PushDto dto) {
        this.setIsAllActivated(dto.isAllActivated());
        this.setIsFeedActivated(dto.isFeedActivated());
        this.setIsReplyActivated(dto.isReplyActivated());
        this.setIsLikeActivated(dto.isLikeActivated());
        this.setIsFollowActivated(dto.isFollowActivated());
    }
}