package com.example.login2.DTO;

import java.time.LocalDateTime;

import com.example.login2.model.PostEntity;
import com.example.login2.model.UserEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PostResponseDTO {

    private Long id;
    private String title;
    private String content;
    private String imageUrl; // 🔽 이미지 URL 포함

    // 작성자 정보 (UserEntity에서 가져옴)
    private String email;
    private String author;
    private String nickName;

    private int views;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    
    
    // Entity → DTO
    public PostResponseDTO(final PostEntity entity) {
        this.id = entity.getId();
        this.title = entity.getTitle();
        this.content = entity.getContent();
        this.imageUrl = entity.getImageUrl();
        // 작성자 정보가 존재하면 매핑
        if (entity.getUser() != null) {
            this.email = entity.getUser().getEmail();
            this.author = entity.getUser().getName();
            this.nickName = !entity.getUser().getIsDeleted() 
                    ? entity.getUser().getNickName() 
                    : "알 수 없음";
        }

        this.views = entity.getViews();
        this.createdAt = entity.getCreatedAt();
        this.updatedAt = entity.getUpdatedAt();
    }

    // 정적 팩토리 메서드
    public static PostResponseDTO fromEntity(PostEntity entity) {
        return new PostResponseDTO(entity);
    }
    
    public static PostResponseDTO from(PostEntity post) {
        return PostResponseDTO.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .imageUrl(post.getImageUrl())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }

    // DTO → Entity (user는 null 처리 or 외부에서 직접 주입)
    public static PostEntity toEntity(PostResponseDTO dto, UserEntity user) {
        return PostEntity.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .content(dto.getContent())
                .imageUrl(dto.getImageUrl())
                .views(dto.getViews())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .user(user) // 주의: user는 별도로 설정 필요
                .build();
    }
}
