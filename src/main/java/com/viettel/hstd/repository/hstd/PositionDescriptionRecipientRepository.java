package com.viettel.hstd.repository.hstd;

import com.viettel.hstd.dto.hstd.PositionDescriptionRecipientDTO;
import com.viettel.hstd.entity.hstd.PositionDescriptionRecipientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.stream.Stream;

public interface PositionDescriptionRecipientRepository extends JpaRepository<PositionDescriptionRecipientEntity, Long> {

    void deleteAllByPositionDescriptionId(Long positionDescriptionId);

    @Query("select new com.viettel.hstd.dto.hstd.PositionDescriptionRecipientDTO$Response(p.id,p.employeeId, p.employeeCode, " +
            "p.employeeName, p.employeeEmail, p.employeeMobilePhone, p.delFlag) " +
            "from PositionDescriptionRecipientEntity p where " +
            "p.delFlag = false and p.positionDescriptionId = :positionDescriptionId")
    List<PositionDescriptionRecipientDTO.Response> getRecipientsByPositionDescriptionId(Long positionDescriptionId);


    @Query("select new com.viettel.hstd.dto.hstd.PositionDescriptionRecipientDTO$EmailResponse(pd.unitName, " +
            "pd.departmentName, pd.groupName, pd.positionName, pdr.employeeName, pdr.employeeEmail) " +
            "from PositionDescriptionRecipientEntity pdr inner join PositionDescriptionEntity pd on pdr.positionDescriptionId = pd.id " +
            "where pdr.delFlag = false and pd.delFlag = false and pd.hasDescription = false")
    Stream<PositionDescriptionRecipientDTO.EmailResponse> findAllStream();


}
