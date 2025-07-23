package com.example.login2.controller;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.login2.DTO.PostCreateDTO;
import com.example.login2.DTO.PostDTO;
import com.example.login2.DTO.PostResponseDTO;
import com.example.login2.DTO.PostUpdateDTO;
import com.example.login2.DTO.ResponseDTO;
import com.example.login2.model.PostEntity;
import com.example.login2.security.JwtTokenProvider;
import com.example.login2.service.PostService;

import io.jsonwebtoken.ExpiredJwtException;
import java.nio.file.Path;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

//@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequiredArgsConstructor // @Autowired 어노테이션 대신 생성자 주입
@RequestMapping("/posts")
public class PostController {
    
    private final PostService p_service;
    private final JwtTokenProvider tokenProvider;

    // 전체 게시글 조회
    @GetMapping
    public ResponseEntity<?> getAllPosts() {
        try {
            List<PostEntity> entities = p_service.findAll();

            List<PostResponseDTO> dtos = entities.stream()
                    .map(PostResponseDTO::fromEntity)
                    .collect(Collectors.toList());

            ResponseDTO<PostResponseDTO> response = ResponseDTO.<PostResponseDTO>builder()
                    .data(dtos)
                    .build();

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            ResponseDTO<PostResponseDTO> response = ResponseDTO.<PostResponseDTO>builder()
                    .error("게시글 목록 조회 실패: " + e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // 선택한 게시글 조회
    @GetMapping("/post/{id}")
    public ResponseEntity<?> getPostById(@PathVariable("id") Long id) {
        try {
            PostEntity postEntity = p_service.findWithUserById(id)
                    .orElseThrow(() -> new RuntimeException("게시글이 없습니다."));

            PostResponseDTO dto = PostResponseDTO.fromEntity(postEntity);
            return ResponseEntity.ok(dto);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("게시글 조회 실패: " + e.getMessage());
        }
    }

    
    // 조회수 증가
    @PutMapping("/post/{id}/views")
    public ResponseEntity<?> increaseViews(@PathVariable("id") Long id) {
        try {
            PostDTO updated = p_service.increaseViews(id);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("조회수 증가 실패: " + e.getMessage());
        }
    }
    

    // 게시글 추가
    @PostMapping("/write")
    public ResponseEntity<?> createPost(
            @RequestBody PostCreateDTO dto,
            HttpServletRequest request
    ) {
        try {
            String token = request.getHeader("Authorization"); // → "Bearer eyJ..." 형식일 것
            String email = tokenProvider.getEmailFromToken(token.replace("Bearer ", ""));
            p_service.createPost(dto, email);

            Map<String, String> response = new HashMap<>();
            response.put("message", "게시글 작성 완료");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            System.out.println("게시글 작성 실패: " + e.getMessage());  // ← **이 로그 반드시 확인**
            errorResponse.put("error", "게시글 작성 실패: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    
    // 게시글 수정
    @PutMapping("/edit/{id}")
    public ResponseEntity<?> updatePost(
            @PathVariable("id") Long id,
            @RequestBody PostUpdateDTO dto,
            HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new RuntimeException("잘못된 인증 헤더입니다.");
            }

            String token = authHeader.substring(7);  // "Bearer " 제거
            String email = tokenProvider.getEmailFromToken(token);

            p_service.updatePost(id, dto, email);

            System.out.println("수정 요청 id=" + id);
            System.out.println("dto.title=" + dto.getTitle());
            System.out.println("dto.content=" + dto.getContent());

            Map<String, String> response = new HashMap<>();
            response.put("message", "게시글 수정 완료");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            System.out.println("수정 실패 이유: " + e.getMessage());
            errorResponse.put("error", "게시글 수정 실패: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    // 게시글 삭제
    @DeleteMapping("{id}")
    public ResponseEntity<?> deletePost(@PathVariable("id") Long id, HttpServletRequest request) {
        try {
        	
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7); // "Bearer " 부분 제거
            }
            String email = tokenProvider.getEmailFromToken(token);
            p_service.deletePost(id, email);
            return ResponseEntity.ok("게시글 삭제 완료");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("게시글 삭제 실패: " + e.getMessage());
        }
    }

    
    // 검색어로 게시글 찾기
    @GetMapping("/search")
    public ResponseEntity<?> searchPosts(@RequestParam("keyword") String keyword) {
        try {
            List<PostEntity> posts = p_service.searchByTitle(keyword);

            List<PostDTO> postDTOs = posts.stream()
                .map(post -> PostDTO.builder()
                    .id(post.getId())
                    .title(post.getTitle())
                    .content(post.getContent())
                    .views(post.getViews())
                    .createdAt(post.getCreatedAt())
                    .updatedAt(post.getUpdatedAt())
                    .nickName(
                            (post.getUser() != null && !post.getUser().getIsDeleted())
                                ? post.getUser().getNickName()
                                : "알 수 없음"
                        )
                    .build())
                .collect(Collectors.toList());

            return ResponseEntity.ok(postDTOs);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("게시글 검색 실패: " + e.getMessage());
        }
    }
    
    
    // 닉네임으로 게시글 찾기
    @GetMapping("/search/nick")
    public ResponseEntity<?> searchPostsByNickName(@RequestParam("keyword") String keyword) {
        try {
            List<PostEntity> posts = p_service.searchByNickName(keyword);

            if (posts.isEmpty()) {
                // 검색 결과 없을 때
                Map<String, String> response = new HashMap<>();
                response.put("message", "회원 정보를 찾을 수 없습니다.");
                return ResponseEntity.ok(response);
            }

            List<PostDTO> postDTOs = posts.stream()
                .map(post -> PostDTO.builder()
                    .id(post.getId())
                    .title(post.getTitle())
                    .content(post.getContent())
                    .views(post.getViews())
                    .createdAt(post.getCreatedAt())
                    .updatedAt(post.getUpdatedAt())
                    .nickName(post.getUser() != null ? post.getUser().getNickName() : null)
                    .build())
                .collect(Collectors.toList());

            return ResponseEntity.ok(postDTOs);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("닉네임으로 게시글 검색 실패: " + e.getMessage());
        }
    }
    
    
    // 이미지 파일 업로드
    @PostMapping("/upload/image")
    public ResponseEntity<?> uploadImage(@RequestParam("image") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("파일이 비어 있습니다.");
            }

            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

            // uploads 폴더 절대경로 생성
            Path uploadDir = Paths.get("uploads");
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);  // 폴더가 없으면 생성
                System.out.println("uploads 폴더 생성 완료: " + uploadDir.toAbsolutePath());
            }

            Path savePath = uploadDir.resolve(fileName);
            Files.copy(file.getInputStream(), savePath, StandardCopyOption.REPLACE_EXISTING);

            Map<String, String> result = new HashMap<>();
            result.put("imageUrl", "/uploads/" + fileName); // 프론트에서 이 URL로 접근 가능해야 함
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("이미지 업로드 실패: " + e.getMessage());
        }
    }
    
    
    
    // 만료된 토큰 처리
    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<String> handleExpiredJwtException(ExpiredJwtException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("토큰이 만료되었습니다. 다시 로그인하세요.");
    }
    
    
}
