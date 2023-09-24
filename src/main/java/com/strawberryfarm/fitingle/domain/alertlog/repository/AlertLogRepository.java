package com.strawberryfarm.fitingle.domain.alertlog.repository;

import com.strawberryfarm.fitingle.domain.alertlog.entity.AlertLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertLogRepository extends JpaRepository<AlertLog, Long> {

}
