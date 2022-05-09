package com.viettel.hstd.repository.hstd;

import com.viettel.hstd.constant.ImportConstant;
import com.viettel.hstd.dto.hstd.ImportHistoryDTO;
import com.viettel.hstd.entity.hstd.ImportHistoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

public interface ImportHistoryRepository extends JpaRepository<ImportHistoryEntity, Long> {

    @Query("select h.importStatus from ImportHistoryEntity h where h.id = :id")
    ImportConstant.ImportStatus findStatusById(Long id);

    @Query("select new com.viettel.hstd.dto.hstd.ImportHistoryDTO$Response(p.id, p.employeeCode, p.employeeName, " +
            "p.importAt,p.importStatus, p.statusTitle, p.importCode, p.importCodeTitle, p.fileTitle, p.fileUrl) " +
            "from ImportHistoryEntity p where " +
            "p.delFlag = false and " +
            "(:employeeId is null or p.employeeId = :employeeId) and " +
            "(:importStatus is null or p.importStatus = :importStatus) and " +
            "(:importCode is null or p.importCode = :importCode) and " +
            "(:fromImportDate is null or p.importAt >= :fromImportDate) and " +
            "(:toImportDate is null or p.importAt <= :toImportDate)")
    Page<ImportHistoryDTO.Response> search(Long employeeId, ImportConstant.ImportStatus importStatus, ImportConstant.ImportCode importCode,
                                           LocalDateTime fromImportDate, LocalDateTime toImportDate, Pageable pageable);
}
