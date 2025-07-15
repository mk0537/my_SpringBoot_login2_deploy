package com.example.login2.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.login2.DTO.ChangePasswordDTO;
import com.example.login2.DTO.UserDTO;
import com.example.login2.model.UserEntity;
import com.example.login2.repository.UserRepository;
import com.example.login2.security.JwtTokenProvider;
import com.example.login2.security.PasswordGenerator;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserService {
	
	@Autowired
	private UserRepository repository;
	private UserEntity entity;
	
	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	
	private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	
	// 로그인
	public UserEntity getUserEntityByEmail(String email) {
	    System.out.println("Attempting to find user by email: " + email);
	    UserEntity user = repository.findByEmailAndIsDeletedFalse(email)
	        .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자이거나 탈퇴된 계정입니다."));
	    System.out.println("Found user: " + user.getEmail());
	    return user;
	}
	
	
	// 회원가입 (회원 추가 (create))
	 public UserEntity create(UserEntity entity) {
	        if (repository.existsByEmail(entity.getEmail())) {
	            throw new RuntimeException("Email already exists");
	        }
	        return repository.save(entity);
	    }
	
    
	
	// 아이디(이메일)로 회원 단건 조회 (read)
	public UserDTO getUserByEmail(String email) {
	    UserEntity entity = repository.findByEmail(email)
	            .orElseThrow(() -> new RuntimeException("해당 사용자가 존재하지 않습니다."));
	    
	    // User 객체 생성
	    return UserDTO.builder()
	            .id(entity.getId())
	            .name(entity.getName())
	            .email(entity.getEmail())
	            .nickName(entity.getNickName())
	            .build();
	}
	

    // 회원 정보 수정 (update)
    public UserDTO updateUser(Long id, UserDTO dto) {
        UserEntity entity = repository.findById(id) // id로 해당 유저 찾기
                .orElseThrow(() -> new RuntimeException("해당 사용자가 존재하지 않습니다."));

        entity.setName(dto.getName()); // 이름 수정
        
        entity.setNickName(dto.getNickName()); // 닉네임 수정
        
        UserEntity updated = repository.save(entity); // DB에 저장

        // 변경된 사용자 정보를 DTO로 만들어 리턴
        return UserDTO.builder()
                .id(updated.getId())
                .name(updated.getName())
                .email(updated.getEmail())
                .nickName(entity.getNickName())
                .build();
    }
    

    // 회원 삭제 (delete)
    public void deactivateUser(Long userId) {
        UserEntity user = repository.findById(userId)
            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        user.setIsDeleted(true); // 실제 삭제 대신
        repository.save(user);
    }
    
    
    // 이름 + 아이디로 아이디 찾기
    public String findEmailByNameAndPassword(String name, String password) {
        List<UserEntity> users = repository.findByName(name); // 여러 명 나올 수 있음

        for (UserEntity user : users) {
            if (passwordEncoder.matches(password, user.getPassword())) {
                return user.getEmail(); // 비밀번호까지 맞으면 이 사람
            }
        }

        return null; // 없으면 null
    }

    
    // 임시 비밀번호 발급
    public String resetPassword(String email) {
        Optional<UserEntity> optionalUser = repository.findByEmail(email);

        if (optionalUser.isPresent()) {
            UserEntity user = optionalUser.get();

            // 유효한 임시 비밀번호 생성
            String tempPassword = PasswordGenerator.generate(10);

            // 비밀번호 암호화 후 저장
            user.setPassword(passwordEncoder.encode(tempPassword));
            
            // 임시 비밀번호 발급 여부
            user.setTempPassword(true); 
            
            repository.save(user);

            return tempPassword;
        }

        return null;
    }
    
    
    // 비밀번호 변경
    public void changePassword(String token, ChangePasswordDTO dto) {
        String email = jwtTokenProvider.getEmailFromToken(token);
        UserEntity user = repository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 현재 비밀번호 확인
        if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }

        // 새 비밀번호로 변경
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        repository.save(user);
    }

   
}