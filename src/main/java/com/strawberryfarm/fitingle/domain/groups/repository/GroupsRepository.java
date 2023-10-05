package com.strawberryfarm.fitingle.domain.groups.repository;

import com.strawberryfarm.fitingle.domain.groups.entity.Groups;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupsRepository extends JpaRepository<Groups, Long> {

}
