package com.viettel.hstd.repository.hstd;

import com.viettel.hstd.core.repository.SoftJpaRepository;
import com.viettel.hstd.dto.hstd.RecruitmentProgressDetailDTO;
import com.viettel.hstd.entity.hstd.RecruitmentProgressDetailEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Temporal;

import javax.persistence.TemporalType;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface RecruitmentProgressDetailRepository extends SoftJpaRepository<RecruitmentProgressDetailEntity, Long> {

    @Query(value = "select d.RECRUITED as recruited, d.RECRUITMENT_DATE as recruitmentDate, f.files as files from RECRUITMENT_PROGRESS_DETAIL d " +
            "inner join (select listagg(concat(f.FILE_TITLE, concat('/', f.FILE_URL)), ':') within group ( order by f.DETAIL_FILE_ID) as files, f.PROGRESS_DETAIL_ID " +
            "from RECRUITMENT_PROGRESS_DETAIL_FILE f where DEL_FLAG = 0 group by f.PROGRESS_DETAIL_ID) f on f.PROGRESS_DETAIL_ID = d.PROGRESS_DETAIL_ID " +
            "where d.DEL_FLAG = 0 " +
            "and d.RECRUITMENT_PROGRESS_ID = :progressId " +
            "and (:recruitmentDate is null or d.RECRUITMENT_DATE = :recruitmentDate) " +
            "ORDER BY d.RECRUITMENT_DATE DESC", nativeQuery = true)
    Page<RecruitmentProgressDetailDTO.ResponseProjection> searchByProgressId(Long progressId, @Temporal(TemporalType.DATE) Date recruitmentDate, Pageable pageable);


    @Query(value = "select d.PROGRESS_DETAIL_ID as id, r.PROGRESS_ID as recruitmentProgressId, " +
            ":currentDate as recruitmentDate, DECODE(c.countContract, null, 0, c.countContract) as recruited " +
            "from RECRUITMENT_PROGRESS r " +
            "left join" +
            " (select count(c.CONTRACT_ID) as countContract, c.POSITION_ID, c.UNIT_ID from CONTRACT c " +
            "where c.DEL_FLAG = 0 and c.IS_ACTIVE = 1 and c.EFFECTIVE_DATE = :currentDate and c.RESIGN_STATUS = -1 " +
            "group by c.POSITION_ID, c.UNIT_ID) c " +
            "on (r.POSITION_ID = c.POSITION_ID and r.ORGANIZATION_ID = c.UNIT_ID) " +
            "left join RECRUITMENT_PROGRESS_DETAIL d on (r.PROGRESS_ID = d.RECRUITMENT_PROGRESS_ID and d.DEL_FLAG = 0 and d.RECRUITMENT_DATE = :currentDate) " +
            "where r.DEL_FLAG = 0 and r.DEADLINE >= :currentDate", nativeQuery = true)
    List<RecruitmentProgressDetailDTO.DailyUpdateProjection> getResultDailyUpdateNewDate(LocalDate currentDate);

    @Query(value = "select d.PROGRESS_DETAIL_ID as id," +
            "d.RECRUITMENT_PROGRESS_ID as recruitmentProgressId," +
            "d.RECRUITMENT_DATE as recruitmentDate," +
            "DECODE(c.countContract, null, 0, c.countContract) as recruited " +
            "from RECRUITMENT_PROGRESS_DETAIL d " +
            "inner join RECRUITMENT_PROGRESS r on d.RECRUITMENT_PROGRESS_ID = r.PROGRESS_ID " +
            "left join (select count(c.CONTRACT_ID) as countContract, c.POSITION_ID, c.UNIT_ID, c.EFFECTIVE_DATE " +
            "from CONTRACT c " +
            "where c.DEL_FLAG = 0 " +
            "and c.IS_ACTIVE = 1 " +
            "and c.RESIGN_STATUS = -1 " +
            "and c.EFFECTIVE_DATE is not null " +
            "group by c.POSITION_ID, c.UNIT_ID, c.EFFECTIVE_DATE\n" +
            "order by c.EFFECTIVE_DATE desc) c " +
            "on (c.POSITION_ID = r.POSITION_ID and c.UNIT_ID = r.ORGANIZATION_ID and c.EFFECTIVE_DATE = d.RECRUITMENT_DATE) " +
            "where d.DEL_FLAG = 0 " +
            "and r.DEL_FLAG = 0", nativeQuery = true)
    List<RecruitmentProgressDetailDTO.DailyUpdateProjection> getResultDailyUpdate();

    boolean existsByRecruitmentProgressId(Long progressId);
}
