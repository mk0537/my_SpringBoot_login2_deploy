package com.example.login2.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// 댓글 요청 DTO
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CommentRequestDTO {
	 private String content;
	 
	 
}
