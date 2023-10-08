package com.strawberryfarm.fitingle.domain.adminarea.repository;

import com.strawberryfarm.fitingle.domain.adminarea.entity.AdminArea;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminAreaRepository extends JpaRepository<AdminArea, Long> {
    AdminArea save(AdminArea adminArea);
}
