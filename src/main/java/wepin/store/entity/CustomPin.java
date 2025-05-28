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
import javax.persistence.OneToMany;
import javax.persistence.Table;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "CUSTOM_PIN")
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class CustomPin extends BaseDatetimeExtended implements Serializable {

    @Id
    @ApiModelProperty(notes = "ID")
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "ID", columnDefinition = "VARCHAR(40) COMMENT '관리 ID'", updatable = false, nullable = false, unique = true, length = 40)
    private String id;

    @Column(name = "NAME")
    @Comment("이름")
    private String name;

    @Column(name = "MAIN_URL")
    @Comment("메인 핀")
    private String mainUrl;

    @Column(name = "SUB_URL")
    @Comment("서브 핀")
    private String subUrl;

    @Column(name = "DESCRIPTION")
    @Comment("설명")
    private String description;

    @Column(name = "TYPE")
    @Comment("TYPE")
    private String type;

    @OneToMany(mappedBy = "customPin", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Feed> feedList;
}