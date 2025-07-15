package com.example.login2.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.login2.model.LikeEntity;
import com.example.login2.model.PostEntity;
import com.example.login2.model.UserEntity;
import com.example.login2.model.CommentEntity;
import com.example.login2.repository.LikeRepository;
import com.example.login2.repository.PostRepository;
import com.example.login2.repository.UserRepository;
import com.example.login2.repository.CommentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    // ------------------ 게시글 좋아요 ------------------

    @Transactional
    public String toggleLike(String email, Long postId) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자 정보를 찾을 수 없습니다."));
        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        Optional<LikeEntity> existing = likeRepository.findByUserAndPost(user, post);

        if (existing.isPresent()) {
            likeRepository.delete(existing.get()); // 좋아요 취소
            return "unliked";
        } else {
            LikeEntity like = LikeEntity.builder()
                    .user(user)
                    .post(post)
                    .build();
            likeRepository.save(like);
            return "liked";
        }
    }

    @Transactional(readOnly = true)
    public int getLikeCount(Long postId) {
        return likeRepository.countByPostId(postId);
    }

    @Transactional(readOnly = true)
    public boolean isLikedByUser(Long postId, String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자 정보를 찾을 수 없습니다."));
        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        return likeRepository.existsByUserAndPost(user, post);
    }

    // ------------------ 댓글 좋아요 ------------------

    @Transactional
    public boolean toggleCommentLike(Long commentId, String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자 정보를 찾을 수 없습니다."));
        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));

        Optional<LikeEntity> existing = likeRepository.findByUserAndComment(user, comment);

        if (existing.isPresent()) {
            likeRepository.delete(existing.get());
            return false;
        } else {
            LikeEntity like = LikeEntity.builder()
                    .user(user)
                    .comment(comment)
                    .build();
            likeRepository.save(like);
            return true;
        }
    }

    @Transactional(readOnly = true)
    public int countCommentLikes(Long commentId) {
        return likeRepository.countByCommentId(commentId);
    }

    @Transactional(readOnly = true)
    public boolean isCommentLikedByUser(Long commentId, String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자 정보를 찾을 수 없습니다."));
        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));

        return likeRepository.existsByUserAndComment(user, comment);
    }
}
