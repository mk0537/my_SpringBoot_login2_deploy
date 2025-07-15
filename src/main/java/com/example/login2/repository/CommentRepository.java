package com.example.login2.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.login2.model.CommentEntity;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    
	List<CommentEntity> findByPostIdOrderByCreatedAtAsc(Long postId);
}
