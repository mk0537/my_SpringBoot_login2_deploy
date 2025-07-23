package com.example.login2.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.login2.DTO.PostCreateDTO;
import com.example.login2.DTO.PostDTO;
import com.example.login2.DTO.PostResponseDTO;
import com.example.login2.DTO.PostUpdateDTO;
import com.example.login2.model.PostEntity;
import com.example.login2.model.UserEntity;
import com.example.login2.repository.PostRepository;
import com.example.login2.repository.UserRepository;

import java.nio.file.Path;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository p_repository;
    private final UserRepository u_repository;
    
    private final Path uploadPath = Paths.get("uploads"); // 상대 경로: 프로젝트 내부

    
    
    // 모든 게시글 조회하기
    public List<PostEntity> findAll() {
        return p_repository.findAll();
    }

    // 아이디로 게시글 조회 (user 함께 fetch)
    public Optional<PostEntity> findWithUserById(Long id) {
        return p_repository.findWithUserById(id);
    }

    // 조회수 증가 (user 함께 fetch)
    public PostDTO increaseViews(Long id) {
        PostEntity post = p_repository.findWithUserById(id)
                .orElseThrow(() -> new RuntimeException("게시글이 존재하지 않습니다."));

        post.setViews(post.getViews() + 1);

        PostEntity saved = p_repository.save(post);

        return new PostDTO(saved);
    }

    
    // 사용자 이메일로 UserEntity 조회
    public UserEntity getUserByEmail(String email) {
        return u_repository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자 없음"));
    }

    
    // 게시글 생성
    public void createPost(PostCreateDTO dto, String email) {
        UserEntity user = u_repository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        PostEntity post = PostEntity.builder()
            .title(dto.getTitle())
            .content(dto.getContent())
            .user(user)
            .build();

        p_repository.save(post);
    }
    
    
    // 게시글 수정 (user 함께 fetch)
    public void updatePost(Long id, PostUpdateDTO dto, String userEmail) {
        PostEntity post = p_repository.findWithUserById(id)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        // 작성자 확인
        if (!post.getUser().getEmail().equals(userEmail)) {
            throw new RuntimeException("작성자 본인만 수정할 수 있습니다.");
        }

        // 수정
        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());

        p_repository.save(post);
    }

    
    // 게시글 삭제 (user 함께 fetch)
    public void deletePost(Long id, String userEmail) {
        PostEntity post = p_repository.findWithUserById(id)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        // 이메일 로그 확인 (디버깅용)
        System.out.println("DB 이메일: " + post.getUser().getEmail());
        System.out.println("토큰 이메일: " + userEmail);
        
        // 작성자 확인
        if (!post.getUser().getEmail().equalsIgnoreCase(userEmail)) {
            throw new RuntimeException("작성자 본인만 삭제할 수 있습니다.");
        }
       

        p_repository.delete(post);
    }

    
    // 검색어로 게시글 찾기
    public List<PostEntity> searchByTitle(String keyword) {
        return p_repository.findByTitleContaining(keyword);
    }
    
    
    // 닉네임으로 게시글 찾기
    public List<PostEntity> searchByNickName(String nickName) {
    	return p_repository.findByUserNickNameContainingAndNotDeleted(nickName);
    }
    
    
    // 이미지 파일 업로드
    public String uploadImage(MultipartFile file) {
        try {
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 파일 확장자 포함한 원본 이름
            String originalFilename = file.getOriginalFilename();

            // 확장자 추출
            String extension = "";
            int dotIndex = originalFilename.lastIndexOf(".");
            if (dotIndex > 0) {
                extension = originalFilename.substring(dotIndex);
            }

            // 중복 방지를 위한 UUID 파일 이름 생성
            String newFileName = UUID.randomUUID().toString() + extension;

            // 저장 경로
            Path targetPath = uploadPath.resolve(newFileName);

            // 실제 파일 저장
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            // 저장된 이미지 URL 경로 반환 (React에서 접근 가능해야 함)
            return "/uploads/" + newFileName;

        } catch (IOException e) {
            throw new RuntimeException("이미지 업로드 실패: " + e.getMessage());
        }
    }
   
    
}

	
	
	
	
	
	
	

