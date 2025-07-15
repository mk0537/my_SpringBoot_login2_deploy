package com.example.login2.DTO;

import com.example.login2.model.UserEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// 사용자 응답용
// 비밀번호는 응답 DTO에 포함하지 말기!
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserDTO {
	private Long id;
    private String email; // 아이디
    private String name; // 사용자 이름
    private String nickName;
    private String token;
    
    private Boolean tempPassword; // 임시 비밀번호 발급 여부
    
    private Boolean isDeleted; // 탈퇴 여부
    
    
   
    // UserEntity -> UserDTO
    public UserDTO(final UserEntity entity) {
		this.id = entity.getId();
		this.email = entity.getEmail();
		this.name = entity.getName();
		this.nickName = entity.getNickName();
		this.tempPassword = entity.getTempPassword();
		this.isDeleted = entity.getIsDeleted();
	}
	
    
    // UserDTO -> UserEntity
	public static UserEntity toEntity(UserDTO dto) {
		return UserEntity.builder()
				.id(dto.getId())
				.email(dto.getEmail())
				.name(dto.getName())
				.nickName(dto.getNickName())
				.tempPassword(dto.getTempPassword())
				.isDeleted(dto.getIsDeleted())
				.build();
	}
	

}
