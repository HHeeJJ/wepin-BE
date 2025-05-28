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


@Entity
@Table(name = "BLACKLIST")
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Blacklist extends BaseDatetimeExtended implements Serializable {

    @Id
    @ApiModelProperty(notes = "ID")
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "ID", columnDefinition = "VARCHAR(40) COMMENT '관리 ID'", updatable = false, nullable = false, unique = true, length = 40)
    private String id;

    //    @Column(name = "REASON")
    //    @Comment("사유")
    //    private String reason;

    @ManyToOne
    @JoinColumn(name = "reason", referencedColumnName = "CD_ID")
    private Code reason;

    @Column(name = "REASON_DETAIL")
    @Comment("사유 상세")
    private String reasonDetail;

    @Column(name = "STATUS")
    @Comment("상태")
    private String status;

    @ManyToOne
    private Member member;

    @ManyToOne
    private Member black;

    @ManyToOne(optional = true)
    private Feed feed;


}