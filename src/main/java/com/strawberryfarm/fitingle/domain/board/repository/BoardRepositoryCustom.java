package com.strawberryfarm.fitingle.domain.board.repository;

import static com.strawberryfarm.fitingle.domain.board.entity.QBoard.board;
import static com.strawberryfarm.fitingle.domain.field.entity.QField.field;
import static com.strawberryfarm.fitingle.domain.wish.entity.QWish.wish;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.strawberryfarm.fitingle.domain.board.dto.BoardSearchKeywordDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BoardRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public List<BoardSearchKeywordDto> boardSearchKeyword(Long userId, String keyword, int page, int size) {

        BooleanExpression keywordCondition = board.title.likeIgnoreCase("%" + keyword + "%")
            .or(board.location.likeIgnoreCase("%" + keyword + "%"));

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
            .where(keywordCondition)
            .orderBy(board.id.desc())
            .offset(page - 1)
            .limit(size)
            .fetch();
    }
}
