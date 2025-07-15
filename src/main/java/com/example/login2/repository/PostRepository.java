package com.example.login2.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.login2.model.PostEntity;
import com.example.login2.model.UserEntity;

public interface PostRepository extends JpaRepository<PostEntity, Long> {

    // 제목 키워드 검색
    List<PostEntity> findByTitleContaining(String keyword);

    // 닉네임으로 게시글 검색
    @Query("SELECT p FROM PostEntity p WHERE p.user.nickName LIKE %:nickName% AND p.user.isDeleted = false")
    List<PostEntity> findByUserNickNameContainingAndNotDeleted(@Param("nickName") String nickName);

    // 유저 기반 게시글 검색
    List<PostEntity> findByUser(UserEntity user); 

    // user까지 함께 조회하는 게시글 단건 조회
    @Query("SELECT p FROM PostEntity p JOIN FETCH p.user WHERE p.id = :id")
    Optional<PostEntity> findWithUserById(@Param("id") Long id);
    
   
}
