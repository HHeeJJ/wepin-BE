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


@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "TAG")
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Tag extends BaseDatetimeExtended implements Serializable {

    @Id
    @ApiModelProperty(notes = "ID")
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "ID", columnDefinition = "VARCHAR(40) COMMENT '관리 ID'", updatable = false, nullable = false, unique = true, length = 40)
    private String id;

    @Column(name = "TAG_NAME")
    @Comment("태그 이름")
    private String tagName;

    @Column(name = "USED_CNT")
    @Comment("사용 횟수")
    private Integer usedCnt;

    @OneToMany(mappedBy = "tag")
    private List<FeedTag> feedTagList;


}