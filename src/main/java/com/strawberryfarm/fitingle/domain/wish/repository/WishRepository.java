package com.strawberryfarm.fitingle.domain.wish.repository;
import com.strawberryfarm.fitingle.domain.board.entity.Board;
import com.strawberryfarm.fitingle.domain.users.entity.Users;
import com.strawberryfarm.fitingle.domain.wish.entity.Wish;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WishRepository extends JpaRepository<Wish, Long> {
    Optional<Wish> findByUserIdAndBoardId(Long userId, Long boardId);

    List<Wish> findByUserId(Long userId);
}
