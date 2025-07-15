package com.example.login2.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// 로그인 요청용
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class LoginDTO {
	private String email; // 아이디
	private String password;
	private boolean isTempPassword; // 임시 비밀번호 발급 여부
	

}
