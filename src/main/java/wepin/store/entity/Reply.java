package wepin.store.entity;


import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@Table(name = "REPLY")
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Reply extends BaseDatetimeExtended implements Serializable {

    @Id
    @ApiModelProperty(notes = "ID")
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "ID", columnDefinition = "VARCHAR(40) COMMENT '관리 ID'", updatable = false, nullable = false, unique = true, length = 40)
    private String id;

    @Column(name = "PARENT_ID")
    @Comment("부모 ID")
    private String parentId;

    @ManyToOne
    private Feed feed;

    @ManyToOne
    private Member member;

    @Column(name = "DESCRIPTION")
    @Comment("내용")
    private String description;

    @Column(name = "IS_LIKE")
    @Comment("좋아요 여부")
    private Boolean isLike;

    @Column(name = "UI_SEQ")
    @Comment("순서")
    private Integer uiSeq;

}