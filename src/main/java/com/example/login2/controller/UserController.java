package com.example.login2.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.login2.DTO.ChangePasswordDTO;
import com.example.login2.DTO.LoginDTO;
import com.example.login2.DTO.ResponseDTO;
import com.example.login2.DTO.SignupDTO;
import com.example.login2.DTO.UserDTO;
import com.example.login2.model.UserEntity;
import com.example.login2.repository.UserRepository;
import com.example.login2.security.JwtTokenProvider;
import com.example.login2.security.PasswordGenerator;
import com.example.login2.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

//@CrossOrigin(origins = "http://localhost:3000")
@Slf4j // Lombok을 사용하여 로그를 남길 수 있는 log 객체를 자동으로 생성한다.
@RestController // 이 클래스가 RESTful 웹서비스의 컨트롤러 역할을 한다는 것을 나타낸다.
@RequestMapping("/login") // 이 컨트롤러의 기본 URI 경로를 "/login" 으로 설정된다.
public class UserController {

	@Autowired
    private UserService userService;
	
	@Autowired
	private JwtTokenProvider tokenProvider;

	@Autowired
	private UserRepository userRepository;
	
	// 비밀번호 암호화해서 DB로 넘기기
	@Autowired
	private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
  
	
	// 로그인
	@PostMapping("/signin")
	public ResponseEntity<?> login(@RequestBody LoginDTO dto) {
		// 이메일로 사용자 조회
	    UserEntity user = userService.getUserEntityByEmail(dto.getEmail());
	    System.out.println("[DEBUG] 로그인 시도 이메일: '" + dto.getEmail() + "'");
	    // 이메일에 해당하는 유저가 없는 경우
	    if (user == null) {
	        return ResponseEntity
	                .status(HttpStatus.NOT_FOUND)
	                .body(ResponseDTO.builder()
	                        .error("존재하지 않는 계정입니다.")
	                        .build());
	    }

	    // 비밀번호 틀린 경우
	    if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
	        return ResponseEntity
	                .status(HttpStatus.UNAUTHORIZED)
	                .body(ResponseDTO.builder()
	                        .error("비밀번호가 일치하지 않습니다.")
	                        .build());
	    }

	    // 로그인 성공: 토큰 발급 및 유저 정보 반환
	    final String token = tokenProvider.createToken(user.getEmail());

	    UserDTO response = UserDTO.builder()
	            .id(user.getId())
	            .email(user.getEmail())
	            .name(user.getName())
	            .nickName(user.getNickName())
	            .token(token)
	            .build();

	    return ResponseEntity.ok(response);
	}

	
    // 회원가입(create)
	// ResponseEntity는 Spring Framework에서 기본 제공하는 클래스이다. 직접 import만 하면 아무 설정 없이도 바로 사용할 수 있다.
	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@RequestBody SignupDTO dto) {
	    try {
	    	
	    	// 정규식 패턴
	        String namePattern = "^[가-힣a-zA-Z]{2,20}$"; // 한글/영문 2~20자
	        String emailPattern = "^[A-Za-z0-9]([-_.]?[A-Za-z0-9])*@[A-Za-z0-9]([-_.]?[A-Za-z0-9])*\\.[A-Za-z]{2,3}$";
	        String passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*])[A-Za-z\\d!@#$%^&*]{6,20}$";

	        // 이름 유효성 검사
	        if (!dto.getName().matches(namePattern)) {
	            throw new IllegalArgumentException("이름은 한글 또는 영문 2~20자로 입력해주세요.");
	        }

	        // 이메일 유효성 검사
	        if (!dto.getEmail().matches(emailPattern)) {
	            throw new IllegalArgumentException("이메일 형식이 올바르지 않습니다.");
	        }
	        
	        // 비밀번호 유효성 검사
	        if (!dto.getPassword().matches(passwordPattern)) {
	            throw new IllegalArgumentException("비밀번호는 대문자, 숫자, 특수문자를 포함한 6~20자여야 합니다.");
	        }
	    	
	    	
	        // 이메일 중복 확인
	        if (userRepository.existsByEmail(dto.getEmail())) {
	            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
	        }
	        
	        
	        // 닉네임 중복 확인
	        if (userRepository.existsByNickName(dto.getNickName())) {
	            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
	        }

	        // UserEntity 생성
	        UserEntity entity = UserEntity.builder()
	                .password(passwordEncoder.encode(dto.getPassword()))
	                .email(dto.getEmail())
	                .name(dto.getName())
	                .nickName(dto.getNickName())
	                .tempPassword(false)
	                .isDeleted(false)
	                .build();

	        // 저장
	        UserEntity savedUser = userService.create(entity);

	        // 응답용 DTO 생성
	        UserDTO responseUserDTO = UserDTO.builder()
	                .id(savedUser.getId())
	                .name(savedUser.getName())
	                .email(savedUser.getEmail())
	                .nickName(savedUser.getNickName())
	                .tempPassword(false)
	                .isDeleted(false)
	                .build();

	        return ResponseEntity.ok(responseUserDTO);

	    } catch (Exception e) {
	    	e.printStackTrace();  // 스택트레이스 출력
	        ResponseDTO<?> errorResponse = ResponseDTO.builder()
	                .error("회원가입 실패: " + e.getMessage())
	                .build();

	        return ResponseEntity.badRequest().body(errorResponse);
	    }
	}

	
	
	// 아이디(이메일로) 회원 조회
	// 로그인된 사용자 정보 조회
	@GetMapping("/user")
	public ResponseEntity<?> getLoginUser(HttpServletRequest request) {
	    try {
	        String token = request.getHeader("Authorization");
	        String email = tokenProvider.getEmailFromToken(token);

	        UserDTO user = userService.getUserByEmail(email);

	        if (user == null) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                    .body(ResponseDTO.builder().error("회원이 존재하지 않습니다.").build());
	        }

	        return ResponseEntity.ok(user);
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(ResponseDTO.builder().error("서버 오류: " + e.getMessage()).build());
	    }
	}

	

	// 회원 정보 수정
	@PutMapping("/edit-profile")
	public ResponseEntity<?> updateUser(@RequestParam("id") Long id, @RequestBody UserDTO dto) {
	    try {
	        UserDTO updated = userService.updateUser(id, dto);
	        return ResponseEntity.ok(updated);
	    } catch (Exception e) {
	        return ResponseEntity.badRequest()
	                .body(ResponseDTO.builder().error(e.getMessage()).build());
	    }
	}
	

	// 회원 삭제
	@DeleteMapping("/user")
	public ResponseEntity<?> deleteUser(@RequestParam("id") Long id) {
	    try {
	        userService.deactivateUser(id);
	        return ResponseEntity.ok("회원 탈퇴 완료");
	    } catch (Exception e) {
	        return ResponseEntity.badRequest().body("회원 탈퇴 에러: " + e.getMessage());
	    }
	}
    
    
   
	// 이메일 중복 체크
	@GetMapping("/auth/check-email")
	public ResponseEntity<?> checkEmailDuplicate(@RequestParam("email") String email) {
	    System.out.println("이메일 중복 체크 요청 들어옴: " + email);
	    boolean exists = userRepository.existsByEmail(email); 
	    Map<String, Boolean> result = new HashMap<>();
	    result.put("exists", exists);
	    return ResponseEntity.ok(result);
	}
	
	
	// 닉네임 중복 체크
	@GetMapping("/auth/check-nickname")
	public ResponseEntity<?> checkNicknameDuplicate(@RequestParam("nickname") String nickName) {
		   System.out.println("닉네임 중복 체크 요청 들어옴: " + nickName);
		   boolean exists = userRepository.existsByNickName(nickName); 
		   Map<String, Boolean> result = new HashMap<>();
		   result.put("exists", exists);
		   return ResponseEntity.ok(result);
	}
	
	
	// 이름 + 비밀번호로 이메일 찾기
	@PostMapping("/find-email")
	public ResponseEntity<Map<String, String>> findEmail(@RequestBody Map<String, String> request) {
	    String name = request.get("name");
	    String password = request.get("password");

	    List<UserEntity> users = userRepository.findByName(name);

	    if (users.isEmpty()) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                .body(Map.of("error", "일치하는 계정이 없습니다."));
	    }

	    for (UserEntity user : users) {
	        if (passwordEncoder.matches(password, user.getPassword())) {
	            return ResponseEntity.ok(Map.of("email", user.getEmail()));
	        }
	    }

	    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
	            .body(Map.of("error", "비밀번호가 틀렸습니다."));
	}
    
    
	// 임시 비밀번호 생성
	@PostMapping("/temp-password")
	public ResponseEntity<String> issueTempPassword(@RequestBody Map<String, String> request) {
	    String email = request.get("email");

	    Optional<UserEntity> optionalUser = userRepository.findByEmail(email);
	    if (optionalUser.isPresent()) {
	        UserEntity user = optionalUser.get();

	        // 임시 비밀번호 생성
	        String tempPassword = PasswordGenerator.generate(10);

	        // 암호화하여 저장
	        user.setPassword(passwordEncoder.encode(tempPassword));

	        // 임시 비밀번호 발급 플래그 true로 변경
	        user.setTempPassword(true);

	        userRepository.save(user);

	        // 임시 비밀번호(평문) 반환
	        return ResponseEntity.ok(tempPassword);
	    }

	    return ResponseEntity.status(HttpStatus.NOT_FOUND)
	            .body("사용자를 찾을 수 없습니다.");
	}

	
	
	// 비밀번호 변경
	@PutMapping("/change-password")
	public ResponseEntity<?> changePassword(
	        @RequestBody ChangePasswordDTO changePasswordDTO,
	        @RequestHeader("Authorization") String token
	) {
	    try {
	        userService.changePassword(token, changePasswordDTO);
	        return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
	    } catch (IllegalArgumentException e) {
	        return ResponseEntity.badRequest().body(e.getMessage());
	    }
	}
    
   
}
