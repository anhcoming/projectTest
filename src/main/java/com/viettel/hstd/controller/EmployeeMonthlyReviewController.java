package com.viettel.hstd.controller;

import com.viettel.hstd.core.dto.BaseResponse;
import com.viettel.hstd.core.dto.SearchDTO;
import com.viettel.hstd.core.utils.SearchUtils;
import com.viettel.hstd.dto.hstd.EmployeeMonthlyReviewDTO.*;
import com.viettel.hstd.service.inf.EmployeeMonthlyReviewService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Description;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController()
@RequestMapping("employee-monthly-review")
@Tag(name = "Employee Monthly Review")
public class EmployeeMonthlyReviewController {
    @Autowired
    EmployeeMonthlyReviewService employeeMonthlyReviewService;

    @GetMapping
    public BaseResponse<List<EmployeeMonthlyReviewResponse>> findAll() {
        return SearchUtils.getResponseFromPage(employeeMonthlyReviewService.findPage(new SearchDTO()));
    }

    @PostMapping("search")
    public BaseResponse<List<EmployeeMonthlyReviewResponse>> search(@RequestBody SearchDTO searchDTO) {
        return SearchUtils.getResponseFromPage(employeeMonthlyReviewService.findPage(searchDTO));
    }

    @PostMapping
    public BaseResponse<EmployeeMonthlyReviewResponse> create(@Valid @RequestBody EmployeeMonthlyReviewRequest request) {
        return new BaseResponse
                .ResponseBuilder<EmployeeMonthlyReviewResponse>()
                .success(employeeMonthlyReviewService.create(request));
    }

    @GetMapping("/{id}")
    public BaseResponse<EmployeeMonthlyReviewResponse> findOneById(@PathVariable Long id) {
        return new BaseResponse
                .ResponseBuilder<EmployeeMonthlyReviewResponse>()
                .success(employeeMonthlyReviewService.findOneById(id));
    }

    @PutMapping("/{id}")
    public BaseResponse<EmployeeMonthlyReviewResponse> update(@PathVariable Long id, @Valid @RequestBody EmployeeMonthlyReviewRequest request) {
        return new BaseResponse
                .ResponseBuilder<EmployeeMonthlyReviewResponse>()
                .success(employeeMonthlyReviewService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public BaseResponse<Boolean> delete(@PathVariable Long id) {
        return new BaseResponse
                .ResponseBuilder<Boolean>()
                .success(employeeMonthlyReviewService.delete(id));
    }

//    @GetMapping("/yearly/{employeeId}")
//    public BaseResponse<EmployeeYearlyReviewResponse> getYearlyReview(@PathVariable Long employeeId) {
//        return new BaseResponse
//                .ResponseBuilder<EmployeeYearlyReviewResponse>()
//                .success(employeeMonthlyReviewService.getYearlyReview(employeeId, LocalDate.now()));
//    }

}
