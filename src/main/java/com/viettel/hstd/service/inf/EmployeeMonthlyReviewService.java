package com.viettel.hstd.service.inf;

import com.viettel.hstd.core.service.CRUDService;
import com.viettel.hstd.dto.hstd.EmployeeMonthlyReviewDTO.*;

import java.time.LocalDate;
import java.util.List;

public interface EmployeeMonthlyReviewService extends CRUDService<EmployeeMonthlyReviewRequest, EmployeeMonthlyReviewResponse, Long> {
//    EmployeeYearlyReviewResponse getYearlyReview(Long employeeId, LocalDate monthReview);
//
//    List<EmployeeYearlyReviewResponse> getYearlyReviewList (Long resignSessionId);
}
