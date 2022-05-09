package com.viettel.hstd.dto.hstd;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.viettel.hstd.core.serializer.CustomLocalDateDeserializer;
import com.viettel.hstd.core.serializer.CustomLocalDateSerializer;

import javax.persistence.Column;
import java.time.LocalDate;

public class EmployerInfoDTO {
    public static class EmployerInfoRequest {
        public String address;
        public String unitName;
        public Long unitId;
        public String phoneNumber;
        public String taxCode;
        public String authorizedNo;
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        public LocalDate authorizedDate;
        public String representative;
        public String position;

        public String representativeNationality;
        public String representativePersonalIdNumber;
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        public LocalDate representativePersonalIdIssueDate;
        public String representativePersonalIdIssuePlace;
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        public LocalDate representativeDateOfBirth;
        public String representativePlaceOfBirth;
        public String representativeAddress;
    }

    public static class EmployerInfoResponse {
        public Long employerInfoId;
        public String address;
        public String unitName;
        public Long unitId;
        public String phoneNumber;
        public String taxCode;
        public String authorizedNo;
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        public LocalDate authorizedDate;
        public String representative;
        public String position;

        public String representativeNationality;
        public String representativePersonalIdNumber;
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        public LocalDate representativePersonalIdIssueDate;
        public String representativePersonalIdIssuePlace;
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        public LocalDate representativeDateOfBirth;
        public String representativePlaceOfBirth;
        public String representativeAddress;
    }

    public static class EmployerInfoResponseForExport {
        public Long employerInfoId;
        public String employerAddress;
        public String employerUnitName;
        public Long employerUnitId;
        public String employerPhoneNumber;
        public String employerTaxCode;
        public String employerAuthorizedNo;
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        public LocalDate employerAuthorizedDate;
        public String employerRepresentative;
        public String employerPosition;

        public String employerRepresentativeNationality;
        public String employerRepresentativePersonalIdNumber;
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        public LocalDate employerRepresentativePersonalIdIssueDate;
        public String employerRepresentativePersonalIdIssuePlace;
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        public LocalDate employerRepresentativeDateOfBirth;
        public String employerRepresentativePlaceOfBirth;
        public String employerRepresentativeAddress;
    }
}
