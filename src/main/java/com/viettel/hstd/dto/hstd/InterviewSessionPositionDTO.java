package com.viettel.hstd.dto.hstd;

public class InterviewSessionPositionDTO {
    public static class InterviewSessionPositionRequest {
        public Long interviewSessionPositionId;
        public Long positionId;
        public Long interviewSessionId;
        public String positionCode;
        public String positionName;
    }

    public static class InterviewSessionPositionResponse {
        public Long interviewSessionPositionId;
        public Long positionId;
        public Long interviewSessionId;
        public String positionCode;
        public String positionName;
    }
}
