package com.example.login2.DTO;

import org.springframework.web.multipart.MultipartFile;

import com.example.login2.model.PostEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// 게시글 수정용
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PostUpdateDTO {
    private String title;   // 글 제목  
    private String content; // 글 내용
    
    // 이미지 교체할 경우
    private MultipartFile image;
    
    
    // PostEntity -> PostUpdateDTO 변환 생성자
    public PostUpdateDTO(final PostEntity entity) {
        this.title = entity.getTitle();
        this.content = entity.getContent();
    }
    
    // 보통 수정 시에는 toEntity 대신 서비스에서 기존 엔티티를 찾아 수정
    // 그래도 필요하다면 이렇게도 가능 (id, user 정보는 서비스에서 관리)
    public static PostEntity toEntity(PostUpdateDTO dto) {
        return PostEntity.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .build();
    }
}
