package com.strawberryfarm.fitingle.domain.board.repository;

import com.strawberryfarm.fitingle.domain.board.dto.BoardSearchDTO;
import com.strawberryfarm.fitingle.domain.board.entity.Board;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BoardRepository extends JpaRepository<Board, Long> {
    Board save(Board board);

   // Optional<Board> findById(Long id);

    @Query(value = "SELECT b.title, b.location, f.name fieldName, b.days, b.times, b.head_count, b.post_status, (case when w.board_id = b.id then 'Y' else 'N' end) wishYn from board b join field f on f.id = b.field_id left join wish w on w.board_id = b.id and w.user_id = :userId WHERE upper(b.title) LIKE upper(concat('%', :keyword, '%')) OR upper(b.location) LIKE upper(concat('%', :keyword, '%'))",
        countQuery = "SELECT count(*) from board b join field f on f.id = b.field_id left join wish w on w.board_id = b.id and w.user_id = :userId WHERE upper(b.title) LIKE upper(concat('%', :keyword, '%')) OR upper(b.location) LIKE upper(concat('%', :keyword, '%'))",
        nativeQuery = true)
    List<BoardSearchDTO> boardSearch(@Param("userId") Long userId, @Param("keyword") String keyword,
        @Param("pageable") Pageable pageable);

    @Query(value = "SELECT count(*) from board b join field f on f.id = b.field_id left join wish w on w.board_id = b.id and w.user_id = :userId WHERE upper(b.title) LIKE upper(concat('%', :keyword, '%')) OR upper(b.location) LIKE upper(concat('%', :keyword, '%'))", nativeQuery = true)
    long boardSearchTotalCount(@Param("userId") Long userId, @Param("keyword") String keyword);


}
