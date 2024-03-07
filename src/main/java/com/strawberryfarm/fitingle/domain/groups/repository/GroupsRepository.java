package com.strawberryfarm.fitingle.domain.groups.repository;

import com.strawberryfarm.fitingle.domain.groups.entity.Groups;
import com.strawberryfarm.fitingle.domain.groups.entity.GroupsStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface GroupsRepository extends JpaRepository<Groups, Long> {
    int countByBoardId(Long boardId);

    @Query("select count(g) from Groups g where g.board.id = :boardId and g.status != 'WAIT'")
    int getParticipantCount(Long boardId);

    List<Groups> findGroupsByStatus(GroupsStatus status);
}
