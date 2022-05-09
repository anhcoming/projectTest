package com.viettel.hstd.entity.hstd;

import com.viettel.hstd.core.entity.EntityBase;
import lombok.*;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "RECRUITEE_ACCOUNT")
@SequenceGenerator(name = "RECRUITEE_ACCOUNT_GEN", initialValue = 1, allocationSize = 1, sequenceName = "RECRUITEE_ACCOUNT_SEQ")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Where(clause = "DEL_FLAG = 0")
public class RecruiteeAccountEntity extends EntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RECRUITEE_ACCOUNT_GEN")
    @Column(name = "RECRUITEE_ACCOUNT_ID")
    private long recruiteeAccountId;
    @Column(name = "LOGIN_NAME")
    private String loginName;
    @Column(name = "PASSWORD")
    private String password;
//    @Column(name = "CV_ID")
//    private long cvId;

    //    @ManyToOne(cascade = CascadeType.REMOVE)
    @ManyToOne()
    @JoinColumn(name = "INTERVIEW_SESSION_CV_ID")
//    private CvEntity cvEntity;
    private InterviewSessionCvEntity interviewSessionCvEntity;
}
