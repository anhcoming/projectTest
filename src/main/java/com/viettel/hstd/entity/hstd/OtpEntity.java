package com.viettel.hstd.entity.hstd;

import com.viettel.hstd.constant.OtpType;
import com.viettel.hstd.core.entity.EntityBase;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "OTP")
@SequenceGenerator(name = "OTP_GEN", initialValue = 1, allocationSize = 1, sequenceName = "OTP_SEQ")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Where(clause = "DEL_FLAG = 0")
public class OtpEntity extends EntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "OTP_GEN")
    @Column(name = "OTP_ID")
    private Long otpId;
    @Column(name = "CODE")
    private String otpCode;
    @Column(name = "NAME")
    private String name;
    @Column(name = "CONTENT")
    private String content;
    @Column(name = "DURATION")
    private Long durtaion = 0l;
    @Column(name = "OTP_TYPE", nullable = false)
    private OtpType otpType = OtpType.UNKNOWN;
}