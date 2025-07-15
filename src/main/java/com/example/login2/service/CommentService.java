package com.example.login2.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.login2.DTO.CommentRequestDTO;
import com.example.login2.DTO.CommentResponseDTO;
import com.example.login2.model.CommentEntity;
import com.example.login2.model.PostEntity;
import com.example.login2.model.UserEntity;
import com.example.login2.repository.CommentRepository;
import com.example.login2.repository.PostRepository;
import com.example.login2.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    
    // 댓글 생성
    public CommentResponseDTO createComment(Long postId, String userEmail, CommentRequestDTO request) {
        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글을 찾을 수 없습니다."));

        UserEntity user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다."));

        CommentEntity comment = CommentEntity.builder()
                .post(post)
                .user(user)
                .content(request.getContent())
                .build();

        CommentEntity saved = commentRepository.save(comment);
        return new CommentResponseDTO(saved);
    }

    
    // 댓글 조회 (게시글 ID 기준)
    public List<CommentResponseDTO> getCommentsByPostId(Long postId) {
        return commentRepository.findByPostIdOrderByCreatedAtAsc(postId)
                .stream()
                .map(CommentResponseDTO::new)
                .collect(Collectors.toList());
    }
    
    
    // 댓글 수정
    public CommentResponseDTO updateComment(Long commentId, String userEmail, CommentRequestDTO request) {
        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        if (!comment.getUser().getEmail().equals(userEmail)) {
            throw new SecurityException("해당 댓글을 수정할 권한이 없습니다.");
        }

        comment.setContent(request.getContent());
        comment.setUpdatedAt(java.time.LocalDateTime.now()); // 명시적 업데이트
        CommentEntity updated = commentRepository.save(comment);
        return new CommentResponseDTO(updated);
    }

    
    // 댓글 삭제
    public void deleteComment(Long commentId, String userEmail) {
        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        if (!comment.getUser().getEmail().equals(userEmail)) {
            throw new SecurityException("해당 댓글을 삭제할 권한이 없습니다.");
        }

        commentRepository.delete(comment);
    }
}
