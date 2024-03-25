package com.strawberryfarm.fitingle.domain.apply.repository;

import com.strawberryfarm.fitingle.domain.apply.entity.Apply;
import com.strawberryfarm.fitingle.domain.apply.entity.ApplyStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplyRepository extends JpaRepository<Apply, Long> {
	//List<Apply> findByUserIdAndBoardId(Long userId, Long boardId);

	List<Apply> findByUserId(Long userId);

	Apply getApplyByBoardIdAndStatus(Long boardId, ApplyStatus status);

	Optional<Apply> findByUserIdAndBoardId(Long userId, Long boardId);

	List<Apply> getAppliesByBoardId(Long boardId);

	List<Apply> findByBoardId(Long userId);
}
