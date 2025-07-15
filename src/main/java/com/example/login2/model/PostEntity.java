package com.example.login2.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "posts")
public class PostEntity {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                    // 게시글 ID (PK)
	
	// 작성자 UserEntity와 ManyToOne 관계 설정 (UserEntity로 회원 가입을 받으면 게시판의 회원이 될 수 있는 관계)
    @ManyToOne(fetch = FetchType.LAZY) // 지연로딩 추천
    @JoinColumn(name = "user_id")      // FK 컬럼명 user_id
    private UserEntity user;

    
    @Column(nullable = false)
    private String title;              // 게시글 제목

    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;            // 본문 내용 (긴 글 허용)

   
    @Column(nullable = false)
    private int views;             // 게시글 조회수


    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;   // 작성일

    @UpdateTimestamp
    private LocalDateTime updatedAt;   // 수정일
    
    // 이미지 URL 필드 추가
    private String imageUrl;

}
