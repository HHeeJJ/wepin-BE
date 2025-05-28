package wepin.store.entity;


import org.hibernate.annotations.GenericGenerator;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@Entity
@Table(name = "CODE")
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Code extends BaseDatetimeExtended implements Serializable {

    @Id
    @ApiModelProperty(notes = "ID")
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "ID", columnDefinition = "VARCHAR(40) COMMENT '관리 ID'", updatable = false, nullable = false, unique = true, length = 40)
    private String id;

    @Column(name = "CD_CLS_ID")
    private String cdClsId;

    @Column(name = "CD_ID")
    private String cdId;

    @Column(name = "CD_NM")
    private String cdNm;

    @Column(name = "DESCRIPTION")
    private String desc;

    //    @OneToMany(mappedBy = "reason", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    //    private List<Blacklist> blacklists;

}