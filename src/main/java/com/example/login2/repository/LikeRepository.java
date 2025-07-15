package com.example.login2.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.login2.model.CommentEntity;
import com.example.login2.model.LikeEntity;
import com.example.login2.model.PostEntity;
import com.example.login2.model.UserEntity;

public interface LikeRepository extends JpaRepository<LikeEntity, Long> {
	// ------------------ 게시글용 ------------------
	Optional<LikeEntity> findByUserAndPost(UserEntity user, PostEntity post);
    boolean existsByUserAndPost(UserEntity user, PostEntity post);
    int countByPostId(Long postId);
    
    
    // ------------------ 댓글용 ------------------
    Optional<LikeEntity> findByUserAndComment(UserEntity user, CommentEntity comment);
    int countByCommentId(Long commentId);
    boolean existsByUserAndComment(UserEntity user, CommentEntity comment);
}