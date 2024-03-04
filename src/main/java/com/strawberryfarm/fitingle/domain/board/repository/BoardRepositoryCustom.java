package com.strawberryfarm.fitingle.domain.board.repository;

import static com.strawberryfarm.fitingle.domain.board.entity.QBoard.board;
import static com.strawberryfarm.fitingle.domain.field.entity.QField.field;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.strawberryfarm.fitingle.domain.board.dto.BoardSearchKeywordDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BoardRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public List<BoardSearchKeywordDto> boardSearchKeyword(int page, int size) {

        return queryFactory
            .select(Projections.constructor(BoardSearchKeywordDto.class,
                board.title,
                board.location,
                field.name.as("fieldName"),
                board.days,
                board.times,
                board.headCount,
                board.postStatus
                ))
            .from(board)
            .orderBy(board.createdDate.desc())
            .offset(page - 1)
            .limit(size)
            .fetch();
    }
}
