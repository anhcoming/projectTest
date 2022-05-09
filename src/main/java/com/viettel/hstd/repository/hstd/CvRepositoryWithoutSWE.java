package com.viettel.hstd.repository.hstd;

import com.viettel.hstd.core.repository.SoftJpaRepository;
import com.viettel.hstd.entity.hstd.CvEntity;
import com.viettel.hstd.entity.hstd.CvEntityWithoutSWE;
import org.springframework.data.jpa.repository.Query;

import java.util.ArrayList;

public interface CvRepositoryWithoutSWE extends SoftJpaRepository<CvEntityWithoutSWE, Long> {
    @Query("select u from  CvEntity  u where u.cvId IN ?1")
    ArrayList<CvEntity> findByIdIn(ArrayList<Long> ids);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);
}
