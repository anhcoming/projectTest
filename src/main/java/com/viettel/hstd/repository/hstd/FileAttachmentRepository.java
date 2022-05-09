package com.viettel.hstd.repository.hstd;

import com.viettel.hstd.core.repository.SoftJpaRepository;
import com.viettel.hstd.entity.hstd.FileAttachmentEntity;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface FileAttachmentRepository extends SoftJpaRepository<FileAttachmentEntity, Long> {
    ArrayList<FileAttachmentEntity> findByFileItemIdAndFileType(Long fileItemId, Integer fileType);
}
