package com.example.login2.controller;

import com.example.login2.security.JwtTokenProvider;
import com.example.login2.service.LikeService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(origins = "http://my-login-frontend-bucket.s3-website.ap-northeast-2.amazonaws.com", allowCredentials = "true")
@RestController
@RequestMapping("/likes")
@RequiredArgsConstructor  // @Autowired 어노테이션 대신 생성자 주입
public class LikeController {

    private final LikeService likeService;
    private final JwtTokenProvider tokenProvider;

    
    // --------------------- 게시글 좋아요 ---------------------
    
    // 좋아요 토글 API
    @PostMapping("/{postId}")
    public ResponseEntity<?> toggleLike(@PathVariable("postId") Long postId, HttpServletRequest request) {
        String token = request.getHeader("Authorization").replace("Bearer ", "");
        String email = tokenProvider.getEmailFromToken(token);
        String result = likeService.toggleLike(email, postId);

        // 프론트에서는 최신 좋아요 수도 필요하므로 추가 제공
        int likeCount = likeService.getLikeCount(postId);
        return ResponseEntity.ok(Map.of("result", result, "likeCount", likeCount));
    }

    // 좋아요 개수 조회 API
    @GetMapping("/{postId}")
    public ResponseEntity<?> getLikeCount(@PathVariable("postId") Long postId) {
        int count = likeService.getLikeCount(postId);
        return ResponseEntity.ok(Map.of("likeCount", count));
    }

    // 사용자의 좋아요 여부 확인 API
    @GetMapping("/{postId}/status")
    public ResponseEntity<?> checkLikeStatus(@PathVariable("postId") Long postId, HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization").replace("Bearer ", "");
            String email = tokenProvider.getEmailFromToken(token);
            boolean liked = likeService.isLikedByUser(postId, email);
            return ResponseEntity.ok(Map.of("liked", liked));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증 실패");
        }
    }
    
    
    // --------------------- 댓글 좋아요 ---------------------

    // 댓글 좋아요 토글
    @PostMapping("/comments/{commentId}")
    public ResponseEntity<?> toggleCommentLike(@PathVariable("commentId") Long commentId, HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization").replace("Bearer ", "");
            String email = tokenProvider.getEmailFromToken(token);
            boolean liked = likeService.toggleCommentLike(commentId, email);
            int likeCount = likeService.countCommentLikes(commentId);

            return ResponseEntity.ok(Map.of(
                "result", liked ? "liked" : "unliked",
                "likeCount", likeCount
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증 실패");
        }
    }

    // 댓글 좋아요 수 조회
    @GetMapping("/comments/{commentId}")
    public ResponseEntity<?> getCommentLikeCount(@PathVariable("commentId") Long commentId) {
        int count = likeService.countCommentLikes(commentId);
        return ResponseEntity.ok(Map.of("likeCount", count));
    }

    // 댓글 좋아요 여부 조회
    @GetMapping("/comments/{commentId}/status")
    public ResponseEntity<?> getCommentLikeStatus(@PathVariable("commentId") Long commentId, HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization").replace("Bearer ", "");
            String email = tokenProvider.getEmailFromToken(token);
            boolean liked = likeService.isCommentLikedByUser(commentId, email);
            return ResponseEntity.ok(Map.of("liked", liked));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("liked", false));
        }
    }
    
}
