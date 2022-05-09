package com.viettel.hstd.repository.hstd;

import com.viettel.hstd.core.repository.SoftJpaRepository;
import com.viettel.hstd.entity.hstd.CvEntity;
import com.viettel.hstd.entity.hstd.CvEntityWithoutSWE;
import org.springframework.data.jpa.repository.Query;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;

public interface CvRepository extends SoftJpaRepository<CvEntity, Long> {
    @Query("select u from  CvEntity  u where u.cvId IN ?1")
    ArrayList<CvEntity> findByIdIn(ArrayList<Long> ids);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByPersonalIdNumber(String personalIdNumber);

    @Query("select u.summaryWorkingExperience from CvEntity u where u.cvId = ?1")
    String getSummaryWorkingExperience(Long cvId);
}
