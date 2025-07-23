package com.example.login2.DTO;

import java.time.LocalDateTime;

import com.example.login2.model.CommentEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// 댓글 응답 DTO
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CommentResponseDTO {
	
	private Long id;
    private String content;
    private String author;
    private String email;
    private LocalDateTime createdAt;
    
  
    public CommentResponseDTO(CommentEntity comment) {
        this.id = comment.getId();
        this.content = comment.getContent();
        
        if (comment.getUser() == null || comment.getUser().getIsDeleted()) {
            this.author = "알 수 없음";
            this.email = null;  // 이메일도 숨기고 싶으면 null 처리
        } else {
            this.author = comment.getUser().getNickName();
            this.email = comment.getUser().getEmail();
        }

        this.createdAt = comment.getCreatedAt();
    }

}
