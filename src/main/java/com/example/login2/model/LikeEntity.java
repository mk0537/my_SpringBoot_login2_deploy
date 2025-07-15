package com.example.login2.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "likes", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "post_id"}), // 한 게시글, 한 계정 당 좋아요 1개만 달 수 있음
    @UniqueConstraint(columnNames = {"user_id", "comment_id"}) // 한 댓글, 한 계정 당 좋아요 1개만 가능
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LikeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private PostEntity post;
    
    @ManyToOne
    @JoinColumn(name = "comment_id")
    private CommentEntity comment;
}