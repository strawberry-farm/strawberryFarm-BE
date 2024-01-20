package com.strawberryfarm.fitingle.domain.qna.repository;

import com.strawberryfarm.fitingle.domain.qna.entity.Qna;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QnaRepository extends JpaRepository<Qna,Long> {

}
