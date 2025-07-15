package com.example.login2.DTO;

import com.example.login2.model.UserEntity;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

//비밀번호 변경 요청용
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ChangePasswordDTO {
	private String currentPassword;
    private String newPassword;
    
	
    
    

}
