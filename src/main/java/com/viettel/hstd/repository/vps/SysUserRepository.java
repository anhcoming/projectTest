package com.viettel.hstd.repository.vps;

import com.viettel.hstd.entity.vps.SysUserEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface SysUserRepository extends JpaRepository<SysUserEntity, Long>,
        JpaSpecificationExecutor<SysUserEntity> {
    List<SysUserEntity> findBySysGroupId(Long groupId);


    List<SysUserEntity> findBySysUserIdIn(Set<Long> sysUserIds);

   Optional<SysUserEntity> findFirstByLoginName(String employeeCode);

    @Query(value = "select t.sysUserId from SysUserEntity t where t.loginName = ?1")
    Optional<Long> getSysUserIdByLoginName(String loginName);


    //Soft delete.
    @Query("update SysUserEntity e set e.isActive = true where e.sysUserId = ?1")
    @Modifying
    @Transactional
    Integer softDelete(Long id);


    @Query("update SysUserEntity e set e.isActive = true where e.sysUserId IN (?1)")
    @Modifying
    @Transactional
    Integer softDeleteAll(List<Long> ids);

    @Query(nativeQuery = true, value = "select * from SYS_USER\n" +
            "   where UPPER(EMPLOYEE_CODE)  like concat(concat('%',UPPER(:key)),'%') or\n" +
            "      UPPER(FULL_NAME) like concat(concat('%',UPPER(:key)),'%') or\n" +
            "      UPPER(EMAIL) like concat(concat('%',UPPER(:key)),'%')")
    List<SysUserEntity> searchMulti(@Param("key") String searchKey, Pageable pageable);

    @Query(nativeQuery = true, value = "select SYS_GROUP_ID from USER_GROUP where SYS_USER_ID = ?1")
    Set<Long> getListGroupFromUserID(Long sysUserId);

    @Query(nativeQuery = true, value = "select T4.DATA_CODE \n" +
            "from APPLICATION T1, APP_DOMAIN_TYPE T2, DOMAIN_TYPE T3, DOMAIN_DATA T4, USER_ROLE_DATA T5,\n" +
            "     USER_ROLE T6\n" +
            "where T1.CODE = ?1  AND T2.APPLICATION_ID = T1.APPLICATION_ID AND\n" +
            "      T3.DOMAIN_TYPE_ID = T2.DOMAIN_TYPE_ID AND T3.DOMAIN_TYPE_ID = T4.DOMAIN_TYPE_ID AND\n" +
            "      T4.DOMAIN_DATA_ID = T5.DOMAIN_DATA_ID AND T5.USER_ROLE_ID = T6.USER_ROLE_ID AND\n" +
            "      T6.SYS_USER_ID = ?2 AND\n" +
            "      T6.SYS_ROLE_ID IN\n" +
            "        (\n" +
            "            WITH CTE AS (\n" +
            "                SELECT ROW_NUMBER() over (PARTITION BY SYS_ROLE_ID, T30.APPLICATION_ID ORDER BY T30.MENU_ID) RN,\n" +
            "                       SYS_ROLE_ID,\n" +
            "                       T30.APPLICATION_ID\n" +
            "                FROM ROLE_MENU T20, MENU T30, APPLICATION T10\n" +
            "                WHERE T20.MENU_ID = T30.MENU_ID AND T30.APPLICATION_ID = T10.APPLICATION_ID AND T10.CODE = ?1 \n" +
            "            )\n" +
            "            SELECT SYS_ROLE_ID FROM CTE WHERE RN = 1\n" +
            "        )")
    Set<String> getProvinceCodeFromSysUserId(String applicationCode, Long sysUserId);

    @Query(nativeQuery = true, value = "select case\n" +
            "           when exists (select 1 from SYS_ROLE T1, USER_ROLE T2\n" +
            "                        WHERE T1.SYS_ROLE_ID = T2.SYS_ROLE_ID AND\n" +
            "                                SYS_USER_ID = ?1 AND T1.CODE = ?2)\n" +
            "               then 1\n" +
            "           else 0\n" +
            "           end as rec_exists\n" +
            " from dual")
    Long isSysUserAdmin(Long sysUserId, String adminCode);

    @Query(nativeQuery = true, value = "SELECT SR.CODE FROM SYS_ROLE SR, USER_ROLE UR\n" +
        "  WHERE SR.SYS_ROLE_ID = UR.SYS_ROLE_ID AND\n" +
        "          UR.SYS_USER_ID = ?1 AND\n" +
        "          SR.CODE IN ('HSTD_ADMIN', 'HSTD_ADMIN_PROVINCE', 'HSTD_USER')")
    Set<String> getRoleOfUser(Long sysUserId, String appCode);

    @Query(nativeQuery = true, value =
            "SELECT P.CODE FROM V_PERMISSION P, ROLE_PERMISSION RP\n" +
            "WHERE P.PERMISSION_ID = RP.PERMISSION_ID AND\n" +
            "      RP.SYS_ROLE_ID = (SELECT SR.SYS_ROLE_ID FROM SYS_ROLE SR WHERE SR.CODE = ?1)")
    Set<String> getPermissionOfRole(String roleCode, String appCode);

    @Query(nativeQuery = true, value = "select distinct DOMAIN_DATA_ID from domain_data WHERE DOMAIN_DATA.DOMAIN_TYPE_ID = ?2 start with DATA_ID in (\n" +
            "        select dd.DATA_ID from domain_data dd\n" +
            "                                   inner join user_role_data urd on dd.DOMAIN_DATA_ID = urd.DOMAIN_DATA_ID\n" +
            "                                   inner join user_role ur on urd.USER_ROLE_ID = ur.USER_ROLE_ID\n" +
            "                                   inner join SYS_ROLE sr on ur.SYS_ROLE_ID = sr.SYS_ROLE_ID\n" +
            "                                   inner join sys_user su on ur.SYS_USER_ID = su.SYS_USER_ID\n" +
            "        where su.SYS_USER_ID = ?1 and sr.code in ('HSTD_USER', 'HSTD_ADMIN', 'HSTD_ADMIN_PROVICE')\n" +
            "          and dd.DOMAIN_TYPE_ID = ?2\n" +
            "    )\n" +
            "      connect by prior DATA_ID = PARENT_ID")
    Set<Long> getAllDomainData(Long sysUserId, Long domainTypeId);

    @Query(nativeQuery = true, value = "select distinct DATA_CODE from domain_data WHERE DOMAIN_DATA.DOMAIN_TYPE_ID = ?2 start with DATA_ID in (\n" +
        "        select dd.DATA_ID from domain_data dd\n" +
        "                                   inner join user_role_data urd on dd.DOMAIN_DATA_ID = urd.DOMAIN_DATA_ID\n" +
        "                                   inner join user_role ur on urd.USER_ROLE_ID = ur.USER_ROLE_ID\n" +
        "                                   inner join SYS_ROLE sr on ur.SYS_ROLE_ID = sr.SYS_ROLE_ID\n" +
        "                                   inner join sys_user su on ur.SYS_USER_ID = su.SYS_USER_ID\n" +
        "        where su.SYS_USER_ID = ?1 and sr.code in ('HSTD_USER', 'HSTD_ADMIN', 'HSTD_ADMIN_PROVICE')\n" +
        "          and dd.DOMAIN_TYPE_ID = ?2\n" +
        "    )\n" +
        "      connect by prior DATA_ID = PARENT_ID")
    Set<Long> getAllDomainDataCode(Long sysUserId, Long domainTypeId);

    @Query(nativeQuery = true, value = "select M.URL\n" +
            "        from ROLE_MENU RM\n" +
            "                 inner join SYS_ROLE SR on SR.SYS_ROLE_ID = RM.SYS_ROLE_ID\n" +
            "                 inner join MENU M on RM.MENU_ID = M.MENU_ID\n" +
            "        where M.APPLICATION_ID = (SELECT A.APPLICATION_ID FROM APPLICATION A WHERE A.CODE = ?1) and SR.CODE = ?2")
    Set<String> getAllMenuUrlsBySysRole(String vpsCode, String code);

    @Query(nativeQuery = true, value = "select M.URL\n" +
            "        from ROLE_MENU RM\n" +
            "                 inner join SYS_ROLE SR on SR.SYS_ROLE_ID = RM.SYS_ROLE_ID\n" +
            "                 inner join MENU M on RM.MENU_ID = M.MENU_ID\n" +
            "        where M.APPLICATION_ID = (SELECT A.APPLICATION_ID FROM APPLICATION A WHERE A.CODE = ?1) and SR.CODE in ?2")
    Set<String> getAllMenuUrlsBySysRoleIn(String vpsCode, Set<String> codeSet);
}
