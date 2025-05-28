package wepin.store.entity;


import lombok.*;
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
@Table(name = "MEMBER_CURRENT_PIN")
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class MemberCurrentPin extends BaseDatetimeExtended implements Serializable {

    @Id
    @ApiModelProperty(notes = "ID")
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "ID", columnDefinition = "VARCHAR(40) COMMENT '관리 ID'", updatable = false, nullable = false, unique = true, length = 40)
    private String id;

    @ManyToOne
    @JoinColumn(name = "CUSTOM_PIN_ID")
    private CustomPin customPin;

    @ManyToOne
    @JoinColumn(name = "MEMBER_ID")
    private Member member;


}