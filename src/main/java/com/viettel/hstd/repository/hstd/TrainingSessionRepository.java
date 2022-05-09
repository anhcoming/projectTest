package com.viettel.hstd.repository.hstd;

import com.viettel.hstd.dto.hstd.TrainingSessionDTO;
import com.viettel.hstd.entity.hstd.TrainingSessionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;

public interface TrainingSessionRepository extends JpaRepository<TrainingSessionEntity, Long> {

    @Query("select new com.viettel.hstd.dto.hstd.TrainingSessionDTO$Response(t.id, t.name, t.fromDate, t.toDate, t.trainingLocation) " +
            "from TrainingSessionEntity t where t.delFlag = false and " +
            "(:name is null or t.name like concat('%',concat(:name, '%'))) and " +
            "(:fromStartDate is null or t.fromDate >= : fromStartDate) and " +
            "(:toStartDate is null or t.fromDate <= :toStartDate) and " +
            "(:fromFinishDate is null or t.toDate >= :fromFinishDate) and " +
            "(:toFinishDate is null or t.toDate <= :toFinishDate)")
    Page<TrainingSessionDTO.Response> search(String name, LocalDate fromStartDate, LocalDate toStartDate,
                                             LocalDate fromFinishDate, LocalDate toFinishDate, Pageable pageable);
}
