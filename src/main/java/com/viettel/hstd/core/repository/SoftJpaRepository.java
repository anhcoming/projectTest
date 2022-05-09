package com.viettel.hstd.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@NoRepositoryBean
public interface SoftJpaRepository<T, ID> extends JpaRepository<T, ID>,
        JpaSpecificationExecutor<T> {

    //Soft delete.
    @Query("update #{#entityName} e set e.delFlag=true where e.id=?1")
    @Modifying
    @Transactional
    Integer softDelete(ID id);


    @Query("update #{#entityName} e set e.delFlag=true where e.id IN (?1)")
    @Modifying
    @Transactional
    Integer softDeleteAll(List<ID> ids);
    
}
