package com.viettel.hstd.entity.hstd;

import com.viettel.hstd.core.entity.EntityBase;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "SIGNATURE")
@SequenceGenerator(name = "SIGNATURE_GEN", initialValue = 1, allocationSize = 1, sequenceName = "SIGNATURE_SEQ")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "update SIGNATURE set DEL_FLAG = 1 where SIGNATURE_ID = ?")
@Where(clause = "DEL_FLAG = 0")
public class SignatureEntity extends EntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SIGNATURE_GEN")
    @Column(name = "SIGNATURE_ID")
    private Long signatureId;
    @Column(name = "ACCOUNT_ID")
    private Long accountId;
    @Column(name = "EMPLOYEE_ID")
    private Long employeeId;
    @Column(name = "EMPLOYEE_CODE")
    private String employeeCode;
    @Column(name = "FILE_PATH")
    private String filePath;
    @Column(name = "FILE_NAME")
    private String fileName;
}
