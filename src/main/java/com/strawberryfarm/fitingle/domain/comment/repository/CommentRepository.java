package com.strawberryfarm.fitingle.domain.comment.repository;

import com.strawberryfarm.fitingle.domain.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

}
