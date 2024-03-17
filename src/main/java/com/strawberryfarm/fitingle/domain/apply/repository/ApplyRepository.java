package com.strawberryfarm.fitingle.domain.apply.repository;

import com.strawberryfarm.fitingle.domain.apply.entity.Apply;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplyRepository extends JpaRepository<Apply, Long> {
	Apply getApplyByBoardIdAndUserId(Long userId, Long boardId);

	List<Apply> getAppliesByBoardId(Long boardId);
}
