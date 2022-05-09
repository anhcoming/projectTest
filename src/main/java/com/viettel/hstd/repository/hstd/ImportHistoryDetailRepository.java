package com.viettel.hstd.repository.hstd;

import com.viettel.hstd.entity.hstd.ImportHistoryDetailEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ImportHistoryDetailRepository extends JpaRepository<ImportHistoryDetailEntity, Long> {

    @Query("select d.rowContent from ImportHistoryDetailEntity d where d.importHistoryId = :importHistoryId")
    Page<String> search(Long importHistoryId, Pageable pageable);
}
