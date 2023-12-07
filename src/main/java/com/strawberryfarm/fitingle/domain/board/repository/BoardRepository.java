package com.strawberryfarm.fitingle.domain.board.repository;

import com.strawberryfarm.fitingle.domain.adminarea.entity.AdminArea;
import com.strawberryfarm.fitingle.domain.board.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long> {
    Board save(Board board);

}
