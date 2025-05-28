package wepin.store.entity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.hibernate.annotations.GenericGenerator;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@Entity
@Table(name = "HIDE")
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Hide extends BaseDatetimeExtended implements Serializable {

    @Id
    @ApiModelProperty(notes = "ID")
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "ID", columnDefinition = "VARCHAR(40) COMMENT '관리 ID'", updatable = false, nullable = false, unique = true, length = 40)
    private String id;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @JoinColumn(name = "MEMBER_ID", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @JoinColumn(name = "FEED_ID", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Feed feed;


}