package com.viettel.hstd.repository.hstd;

import com.viettel.hstd.dto.hstd.PositionDescriptionFileDTO;
import com.viettel.hstd.entity.hstd.PositionDescriptionFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PositionDescriptionFileRepository extends JpaRepository<PositionDescriptionFileEntity, Long> {

    void deleteAllByPositionDescriptionId(Long positionDescriptionId);

    @Query("select new com.viettel.hstd.dto.hstd.PositionDescriptionFileDTO$Response(p.id,p.fileTitle, p.fileUrl, p.positionDescriptionId, p.delFlag) " +
            "from PositionDescriptionFileEntity p where " +
            "p.delFlag = false and p.positionDescriptionId = :positionDescriptionId")
    List<PositionDescriptionFileDTO.Response> getListFileByPositionDescriptionId(Long positionDescriptionId);
}
