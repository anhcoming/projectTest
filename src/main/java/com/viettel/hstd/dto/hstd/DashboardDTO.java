package com.viettel.hstd.dto.hstd;

public class DashboardDTO {

    public static class DashboardTopResponse {
        public int numResignationHaventVOYet =  0; // hồ sơ nghỉ việc chưa được trình ký
        public int numEmployeeMissingDocument = 0; // Nhân viên thiếu hồ sơ
        public int numNeedApprovalDocument = 0; // 	Hồ sơ đang chờ phê duyệt
        public int numRejectedDocument = 0; // Hồ sơ bị từ chối tiếp nhận
        public int numNotSignedContract = 0; // Hợp đồng cá nhân chưa ký
        public int numNotSentToEmployeeContract = 0; // Hợp đồng chưa bàn giao đến cá nhân
    }

}
