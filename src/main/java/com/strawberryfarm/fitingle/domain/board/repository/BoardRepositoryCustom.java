package com.strawberryfarm.fitingle.domain.board.repository;

import static com.strawberryfarm.fitingle.domain.board.entity.QBoard.board;
import static com.strawberryfarm.fitingle.domain.field.entity.QField.field;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.strawberryfarm.fitingle.domain.board.dto.BoardSearchKeywordDto;
import com.strawberryfarm.fitingle.domain.board.dto.BoardSearchNonUserDto;
import com.strawberryfarm.fitingle.domain.board.dto.QBoardSearchNonUserDto;
import com.strawberryfarm.fitingle.domain.board.entity.Days;
import com.strawberryfarm.fitingle.domain.board.entity.Times;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BoardRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public List<BoardSearchKeywordDto> boardSearchKeyword(Long userId, String keyword, int page,
        int size) {

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
//            .leftJoin(board.wishes, wish)
            .where(keywordCondition)
            .orderBy(board.id.desc())
            .offset(page - 1)
            .limit(size)
            .fetch();
    }

    public List<BoardSearchNonUserDto> boardSearchNonUser(String keyword, String BCode, Days days,
        Times times, int page, int size) {
        BooleanBuilder builder = new BooleanBuilder();
        if (keyword != null && !keyword.equals("")) {
            builder.and(board.title.likeIgnoreCase("%" + keyword + "%")
                .or(board.location.likeIgnoreCase("%" + keyword + "%")));
        }
        if (days != null && !days.equals("")) {
            builder.and(board.days.eq(days));
        }
        if (times != null && !times.equals("")) {
            builder.and(board.times.eq(times));
        }
        if (BCode != null && !BCode.equals("")) {
            builder.and(board.BCode.eq(BCode));
        }

        return queryFactory
            .select(new QBoardSearchNonUserDto(
                board.id,
                board.title,
                board.location,
                field.name.as("fieldName"),
                board.days,
                board.times,
                board.headCount,
                board.postStatus
            ))
            .from(board)
            .where(builder)
            .orderBy(board.id.desc())
            .offset(page - 1)
            .limit(size)
            .fetch();
    }

    public long boardSearchNonUserTotalCount(String keyword, String BCode, Days days, Times times) {
        BooleanBuilder builder = new BooleanBuilder();
        if (keyword != null && !keyword.equals("")) {
            builder.and(board.title.likeIgnoreCase("%" + keyword + "%")
                .or(board.location.likeIgnoreCase("%" + keyword + "%")));
        }
        if (days != null && !days.equals("")) {
            builder.and(board.days.eq(days));
        }
        if (times != null && !times.equals("")) {
            builder.and(board.times.eq(times));
        }
        if (BCode != null && !BCode.equals("")) {
            builder.and(board.BCode.eq(BCode));
        }

        return queryFactory
            .select(board)
            .from(board)
            .where(builder)
            .fetchCount();
    }

}
