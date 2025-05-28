package wepin.store.entity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@Table(name = "LIKES")
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Likes extends BaseDatetimeExtended implements Serializable {

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