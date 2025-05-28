package wepin.store.entity;


import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.GenericGenerator;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import io.swagger.annotations.ApiModelProperty;
import lombok.experimental.SuperBuilder;


@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "FEED_PIN")
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class FeedPin extends BaseDatetimeExtended implements Serializable, Cloneable {

    @Id
    @ApiModelProperty(notes = "ID")
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "ID", columnDefinition = "VARCHAR(40) COMMENT '관리 ID'", updatable = false, nullable = false, unique = true, length = 40)
    private String id;

    @Column(name = "IS_MAIN")
    @Comment("대표 여부")
    private Boolean isMain;

    @Column(name = "UI_SEQ")
    @Comment("순서")
    private Integer uiSeq;

    @ManyToOne
    @JoinColumn(name = "FEED_ID")
    private Feed feed;

    @ManyToOne
    @JoinColumn(name = "PIN_ID")
    private Pin pin;
}