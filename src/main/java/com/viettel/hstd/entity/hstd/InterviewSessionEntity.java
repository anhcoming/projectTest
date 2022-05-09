package com.viettel.hstd.entity.hstd;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.viettel.hstd.core.custom.CustomLocalDateTimeDeserializer;
import com.viettel.hstd.core.custom.CustomLocalDateTimeSerializer;
import com.viettel.hstd.core.entity.EntityBase;
import com.viettel.hstd.core.serializer.CustomLocalDateDeserializer;
import com.viettel.hstd.core.serializer.CustomLocalDateSerializer;
import com.viettel.hstd.entity.listener.InterviewSessionListener;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.*;
import org.springframework.context.event.EventListener;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "INTERVIEW_SESSION")
@SequenceGenerator(name = "INTERVIEW_SESSION_GEN", initialValue = 1, allocationSize = 1, sequenceName = "INTERVIEW_SESSION_SEQ")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "update INTERVIEW_SESSION set DEL_FLAG = 1 where INTERVIEW_SESSION_ID = ?")
@Where(clause = "DEL_FLAG = 0")
//@EntityListeners(InterviewSessionListener.class)
public class InterviewSessionEntity extends EntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "INTERVIEW_SESSION_GEN")
    @Column(name = "INTERVIEW_SESSION_ID")
    private Long interviewSessionId;
    @Column(name = "START_DATE")
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime startDate;
    @Column(name = "END_DATE")
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime endDate;
    @Column(name = "INTERVIEW_LOCATION")
    private String interviewLocation;
    @Column(name = "NAME")
    private String name;
    /**
     * Chu tri phong van
     */
    @Column(name = "LEADER")
    private String leader;

    @Column(name = "POSITION_ID")
    private Long positionId;
    @Column(name = "POSITION_CODE")
    private String positionCode;
    @Column(name = "POSITION_NAME")
    private String positionName;
    @Column(name = "LEADER_ID")
    private Long leaderId;
    @Column(name = "LEADER_NAME")
    private String leaderName;
    @Column(name = "LEADER_EMAIL")
    private String leaderEmail;
    @Column(name = "DESCRIPTION")
    private String description;
    @Column(name = "UNIT_ID")
    private Long unitId = 9004488l; // KCQ
    @Column(name = "DEPARTMENT_ID")
    private Long departmentId; // Tổ chức lao động
    @Column(name = "IS_LOCK")
    private Boolean isLock = false; // Đã trúng tuyển và tạo tài khoản nhân viên sẽ khóa lại

    @OneToMany(mappedBy = "interviewSessionEntity",
//            cascade = CascadeType.ALL,
            orphanRemoval = true)
    @LazyCollection(LazyCollectionOption.TRUE)
    @JsonBackReference(value = "interviewSessionCvEntities")
    private List<InterviewSessionCvEntity> interviewSessionCvEntities = new ArrayList<>();

    @OneToMany(mappedBy = "interviewSessionEntity", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    @LazyCollection(LazyCollectionOption.TRUE)
    @JsonBackReference(value = "employeeInterviewSessionEntities")
    private List<EmployeeInterviewSessionEntity> employeeInterviewSessionEntities = new ArrayList<>();
}
