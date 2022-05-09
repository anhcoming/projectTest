package com.viettel.hstd.entity.hstd;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.viettel.hstd.core.entity.EntityBase;
import com.viettel.hstd.entity.vps.EmployeeVhrEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLDeleteAll;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Entity
@Table(name = "EMPLOYEE_INTERVIEW_SESSION")
@SequenceGenerator(name = "EMPLOYEE_INTERVIEW_SESSION_GEN", initialValue = 1, allocationSize = 1, sequenceName = "EMPLOYEE_INTERVIEW_SESSION_SEQ")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "update EMPLOYEE_INTERVIEW_SESSION set DEL_FLAG = 1 where EMPLOYEE_INTERVIEW_SESSION_ID = ?")
@Where(clause = "DEL_FLAG = 0")
public class EmployeeInterviewSessionEntity extends EntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EMPLOYEE_INTERVIEW_SESSION_GEN")
    @Column(name = "EMPLOYEE_INTERVIEW_SESSION_ID")
    private Long employeeInterviewSessionId;
    @Column(name = "EMPLOYEE_ID")
    private Long employeeId;
    @Column(name = "EMPLOYEE_NAME")
    private String employeeName;

    @ManyToOne
    @JoinColumn(name = "INTERVIEW_SESSION_ID")
//    @JsonManagedReference
    private InterviewSessionEntity interviewSessionEntity;

    public EmployeeVhrEntity convertToEmployeeVhr() {
        EmployeeVhrEntity employeeVhrEntity = new EmployeeVhrEntity();
        employeeVhrEntity.setEmployeeId(employeeId);
        employeeVhrEntity.setFullname(employeeName);
        return employeeVhrEntity;
    }
}
