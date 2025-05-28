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
@Table(name = "LOG")
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Log extends BaseDatetimeExtended implements Serializable {

    @Id
    @ApiModelProperty(notes = "ID")
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "ID", columnDefinition = "VARCHAR(40) COMMENT '관리 ID'", updatable = false, nullable = false, unique = true, length = 40)
    private String id;

    @Column(name = "MEMBER_ID")
    private String memberId;

    @Column(name = "NAME")
    private String name;

    @Column(name = "TYPE")
    private String type;

    @Column(name = "MESSAGE")
    private String message;

    @Column(name = "LOC")
    private String loc;
}