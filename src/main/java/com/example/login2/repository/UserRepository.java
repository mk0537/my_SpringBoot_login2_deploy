package com.example.login2.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.login2.model.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
	
	//Optional<UserEntity> : 0개 또는 1개 결과 (유일한 값 검색)
	Optional<UserEntity> findByEmail(String email); // 사용자 이메일로 정보 조회
	
	// List<UserEntity> : 0개 이상 결과 (중복될 수 있는 값 검색)
    List<UserEntity> findByName(String name); // 이름만으로는 여러 이름이 있기 때문에 List에 담기
    
    Optional<UserEntity> findByEmailAndIsDeletedFalse(String email);
	
    boolean existsById(Long id);
	Boolean existsByNickName(String nickName); // 닉네임 중복 검사 (T/F)
	Boolean existsByEmail(String email); // 이메일 중복 검사 (T/F)
}
