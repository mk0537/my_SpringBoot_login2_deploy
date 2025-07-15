package com.example.login2.DTO;

import java.time.LocalDateTime;

import com.example.login2.model.PostEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PostDTO {
	
	    private Long id;
	    private String title;
	    private String content;
	    private int views;
	    
	    private LocalDateTime createdAt;
	    private LocalDateTime updatedAt;
	    
	    private String nickName;
	    private String email;
	    
	    // 이미지 URL 필드
	    private String imageUrl;

	    
	    public PostDTO(PostEntity entity) {
	        this.id = entity.getId();
	        this.title = entity.getTitle();
	        this.content = entity.getContent();
	        this.views = entity.getViews();
	        this.createdAt = entity.getCreatedAt();
	        this.updatedAt = entity.getUpdatedAt();
	        this.nickName = !entity.getUser().getIsDeleted() 
                    ? entity.getUser().getNickName() 
                    : "알 수 없음"; 
	        this.email = entity.getUser().getEmail();
	        this.imageUrl = entity.getImageUrl();
	    }
	   
	}



