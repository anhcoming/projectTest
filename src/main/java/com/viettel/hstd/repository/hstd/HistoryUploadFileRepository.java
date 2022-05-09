package com.viettel.hstd.repository.hstd;

import com.viettel.hstd.core.repository.SoftJpaRepository;
import com.viettel.hstd.entity.hstd.HistoryUploadEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HistoryUploadFileRepository extends SoftJpaRepository<HistoryUploadEntity, Long> {
    Optional<HistoryUploadEntity> findByUrl(String url);
}
