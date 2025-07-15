package com.example.login2.DTO;

import java.time.LocalDateTime;

import org.springframework.web.multipart.MultipartFile;

import com.example.login2.model.PostEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PostCreateDTO {
    
    private String title;       // 글 제목
    private String content;     // 글 내용
    
    private String email;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 이미지 URL이 아닌, 실제 업로드된 MultipartFile 받기
    private MultipartFile image;

    
    // Entity → DTO 변환자
    public PostCreateDTO(final PostEntity entity) {
        this.title = entity.getTitle();
        this.content = entity.getContent();
        this.email = entity.getUser() != null ? entity.getUser().getEmail() : null; // 👈 추가
        this.createdAt = entity.getCreatedAt();
        this.updatedAt = entity.getUpdatedAt();
    }

    // DTO → Entity 변환 (UserEntity는 서비스에서 주입)
    public static PostEntity toEntity(PostCreateDTO dto) {
        return PostEntity.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .build();
    }
}
