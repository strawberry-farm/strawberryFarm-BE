package com.strawberryfarm.fitingle.domain.keyword.repository;

import com.strawberryfarm.fitingle.domain.keyword.entity.Keyword;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface KeywordRepository extends JpaRepository<Keyword,Long> {

}
