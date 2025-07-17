package com.example.login2.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.login2.DTO.CommentRequestDTO;
import com.example.login2.DTO.CommentResponseDTO;
import com.example.login2.service.CommentService;

import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "http://my-login-frontend-bucket.s3-website.ap-northeast-2.amazonaws.com", allowCredentials = "true")
@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor // @Autowired 어노테이션 대신 생성자 주입
public class CommentController {

    private final CommentService commentService;

    
    // 댓글 생성
    @PostMapping("/{postId}")
    public ResponseEntity<CommentResponseDTO> createComment(
            @PathVariable("postId") Long postId,
            @RequestBody CommentRequestDTO requestDTO,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        if (userDetails == null) {
            throw new RuntimeException("로그인된 사용자만 댓글을 작성할 수 있습니다.");
        }

        String userEmail = userDetails.getUsername();
        return ResponseEntity.ok(commentService.createComment(postId, userEmail, requestDTO));
    }


    
    // 게시글별 댓글 목록 조회
    @GetMapping("/{postId}")
    public ResponseEntity<List<CommentResponseDTO>> getComments(@PathVariable("postId") Long postId) {
        List<CommentResponseDTO> comments = commentService.getCommentsByPostId(postId);
        return ResponseEntity.ok(comments);
    }
    

    // 댓글 수정
    @PutMapping("/{commentId}")
    public ResponseEntity<CommentResponseDTO> updateComment(
            @PathVariable("commentId") Long commentId,
            @RequestBody CommentRequestDTO requestDTO,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String userEmail = userDetails.getUsername();
        CommentResponseDTO updated = commentService.updateComment(commentId, userEmail, requestDTO);
        return ResponseEntity.ok(updated);
    }

    
    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable("commentId") Long commentId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String userEmail = userDetails.getUsername();
        commentService.deleteComment(commentId, userEmail);
        return ResponseEntity.noContent().build();
    }
    
    
}
