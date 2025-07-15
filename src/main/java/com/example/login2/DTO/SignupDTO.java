package com.example.login2.DTO;

import com.example.login2.model.UserEntity;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// 회원가입 요청용
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class SignupDTO {
	private String name; // 사용자 이름
	private String email; // 아이디
	private String password;
	private String nickName; // 닉네임
	
	// SignupDTO -> UserEntity
    public UserEntity toEntity() {
        return UserEntity.builder()
        		.name(this.name)
                .email(this.email)
                .password(this.password)
                .nickName(this.nickName)
                .build();
    }
}
