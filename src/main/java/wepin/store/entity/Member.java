package wepin.store.entity;


import lombok.*;
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
import lombok.experimental.SuperBuilder;


@Entity
@Table(name = "MEMBER")
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Member extends BaseDatetimeExtended implements Serializable {

    @Id
    @ApiModelProperty(notes = "ID")
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "ID", columnDefinition = "VARCHAR(40) COMMENT '관리 ID'", updatable = false, nullable = false, unique = true, length = 40)
    private String id;

    @Column(name = "MEMBER_ID")
    @Comment("멤버 아이디")
    private String memberId;

    @Column(name = "PASSWORD")
    @Comment("비밀번호")
    private String password;

    @Column(name = "NAME")
    @Comment("회원 성명")
    private String name;

    @Column(name = "EMAIL")
    @Comment("이메일")
    private String email;

    @Column(name = "NICKNAME")
    @Comment("별칭")
    private String nickname;

    @Column(name = "PROFILE")
    @Comment("프로필 이미지")
    private String profile;

    @Column(name = "TYPE")
    @Comment("가입유형")
    private String type;

    @Column(name = "INTRODUCE")
    @Comment("자기소개")
    private String introduce;

    @Column(name = "AUTH")
    @Comment("권한")
    private String auth;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MemberCurrentPin> memberCurrentPinList;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MemberCustomPin> memberCustomPinList;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Feed> feedList;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Likes> likeList;

    @OneToMany(mappedBy = "follower", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Follow> followerList;

    @OneToMany(mappedBy = "following", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Follow> followingList;
}