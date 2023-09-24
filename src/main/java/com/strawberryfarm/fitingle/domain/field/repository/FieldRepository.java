package com.strawberryfarm.fitingle.domain.field.repository;

import com.strawberryfarm.fitingle.domain.field.entity.Field;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FieldRepository extends JpaRepository<Field, Long> {

}
