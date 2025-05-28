package wepin.store.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

import wepin.store.entity.Member;

public interface MemberRepository extends JpaRepository<Member, String> {

    List<Member> findByIsDeletedFalseAndIsActivatedTrue();

    Optional<Member> findByIdAndIsDeletedFalseAndIsActivatedTrue(String id);

    List<Member> findByNicknameContainsAndIsDeletedFalseAndIsActivatedTrue(String nickname);

    Optional<Member> findByMemberIdAndIsDeletedFalseAndIsActivatedTrue(String memberId);

    Optional<Member> findByMemberIdAndTypeAndIsDeletedFalseAndIsActivatedTrue(String memberId, String type);

    List<Member> findByMemberIdAndPasswordAndIsDeletedFalseAndIsActivatedTrue(String memberId, String pw);

    Optional<Member> findByNicknameAndIsDeletedFalseAndIsActivatedTrue(String nickname);

    Optional<Member> findByMemberIdAndPassword(String memberId, String memberPwd);

    List<Member> findByIdInAndIsDeletedFalseAndIsActivatedTrue(List<String> ids);


    Page<Member> findByIdInAndIsDeletedFalseAndIsActivatedTrue(List<String> ids, Pageable pageable);

}
