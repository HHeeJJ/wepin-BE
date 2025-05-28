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
@Table(name = "IMAGE")
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Image extends BaseDatetimeExtended implements Serializable {

    @Id
    @ApiModelProperty(notes = "ID")
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "ID", columnDefinition = "VARCHAR(40) COMMENT '관리 ID'", updatable = false, nullable = false, unique = true, length = 40)
    private String id;

    @ManyToOne
    private Pin pin;

    @Column(name = "IMAGE")
    @Comment("IMAGE")
    private String image;

    @Column(name = "UI_SEQ")
    @Comment("순서")
    private Integer uiSeq;

}