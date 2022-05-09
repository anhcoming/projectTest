package com.viettel.hstd.repository.hstd;

import com.viettel.hstd.core.repository.SoftJpaRepository;
import com.viettel.hstd.entity.hstd.ProvinceAreaEntity;
import com.viettel.hstd.entity.hstd.view.ProvinceAreaShortEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ProvinceAreaRepository extends SoftJpaRepository<ProvinceAreaEntity, Long> {
    @Query(value = "select u.id, u.code, u.name, u.parentId from ProvinceAreaEntity u")
    List<Object[]> getAllShort();

    @Query(nativeQuery = true, value = "select id, code, name, (case when PARENT_ID = 1 then AREA_ID + 99317 else PARENT_ID end) PARENT_ID from PROVINCE_AREA ")
    List<Object[]> getAllShort2();

    @Query(nativeQuery = true, value = "select id, code, name, (case when PARENT_ID = 1 then AREA_ID + 99317 else PARENT_ID end) PARENT_ID from PROVINCE_AREA where SYS_GROUP_NAME in ?1  or GROUP_LEVEL = '1'")
    List<Object[]> getAllShort2(Set<String> sysGroupNameList);

    @Query(value = "SELECT T FROM ProvinceAreaShortEntity T WHERE T.provinceCode IN ?1 OR T.parentId IS NULL")
    List<ProvinceAreaShortEntity> getAllInListProvinceCode(Set<String> provinceCodeList);

    @Query(value = "SELECT T FROM ProvinceAreaShortEntity T")
    List<ProvinceAreaShortEntity> getAll3();

    Optional<ProvinceAreaEntity> findById(Long id);

    List<ProvinceAreaEntity> findByParentId(Long parentId);

    List<ProvinceAreaEntity> findByAreaCode(String areaCode);

    Optional<ProvinceAreaEntity> findFirstByCode(String code);

    @Query(nativeQuery = true, value = "select t1.* from PROVINCE_AREA t1, SYSTEM t2, SYSTEM_ELEMENT_VALUE t3\n" +
            "   where t2.ID = t3.SYSTEM_ID and\n" +
            "      t3.ELEMENT_CODE = ?1 and\n" +
            "      t1.ID = t3.APP_PARAM_ID and\n" +
            "      t2.ID = ?2 and\n" +
            "      t1.DEL_FLAG = 0 and t2.DEL_FLAG = 0 and t3.DEL_FLAG = 0")
    Optional<ProvinceAreaEntity> findBySystemId(String addressCode, Long systemId);

    Optional<ProvinceAreaEntity> findFirstByProvinceCode(String provinceCode);

}

