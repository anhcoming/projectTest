package com.viettel.hstd.dto.hstd;


public class RecruiteeAccountDTO {
    public static class RecruiteeAccountRequest {
        public long recruiteeAccountId;
        public String loginName;
//        public CvEntity cvEntity;
    }

    public static class RecruiteeAccountResponse {
        public long recruiteeAccountId;
        public String loginName;
        //        public CvEntity cvEntity;
        public InterviewSessionCvDTO.InterviewSessionCvResponse interviewSessionCvEntity;
    }
}
