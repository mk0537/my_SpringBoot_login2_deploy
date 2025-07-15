package com.example.login2.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "users")
public class UserEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
    @Column(nullable=false)
    private String name; // 사용자 이름

    @Column(nullable = false, unique = true)
    private String email; // 사용자 아이디
    
    @Column(nullable = false)
    private String password;
    
    @Column(nullable = false)
    private String nickName; // 사용자 이름
    
    @Column(name = "tempPassword", nullable = false)
    private Boolean tempPassword = false;// 임시 비밀번호 발급 여부

    @Column(nullable = false, name = "isDeleted")
    private Boolean isDeleted = false; // 탈퇴 여부
    
}