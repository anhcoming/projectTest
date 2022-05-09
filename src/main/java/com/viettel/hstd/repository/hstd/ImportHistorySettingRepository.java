package com.viettel.hstd.repository.hstd;

import com.viettel.hstd.constant.ImportConstant;
import com.viettel.hstd.dto.hstd.ImportHistorySettingDTO;
import com.viettel.hstd.entity.hstd.ImportHistorySettingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ImportHistorySettingRepository extends JpaRepository<ImportHistorySettingEntity, Long> {
    boolean existsByImportCode(ImportConstant.ImportCode importCode);

    @Query("select new com.viettel.hstd.dto.hstd.ImportHistorySettingDTO$Response(s.importField, s.importTitle) " +
            "from ImportHistorySettingEntity s where s.delFlag = false and s.importCode = :importCode")
    List<ImportHistorySettingDTO.Response> search(ImportConstant.ImportCode importCode);

}
